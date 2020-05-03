package com.xingyutang.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xingyutang.app.model.vo.WxUser;
import com.xingyutang.app.model.vo.WxUserToken;
import com.xingyutang.app.service.WeixinService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinServiceImpl implements WeixinService {
    private Logger logger = LoggerFactory.getLogger(WeixinServiceImpl.class);
    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;
    private String accessToken;
    private String jsApiTicket;

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    private void refreshAccessToken() throws Exception {
        String url = String.format(
                "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%1$s&secret=%2$s", appId,
                appSecret);
        JSONObject jsonObject = get(url);
        accessToken = jsonObject.getString("access_token");
        logger.info("weixin access token: {}", accessToken);
    }

    private void refreshJSApiTicket() throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
        JSONObject json = get(url);
        if (json != null) {
            jsApiTicket = json.getString("ticket");
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshWxTokens() {
        for (int i = 0; i < 3; i++) {
            try {
                logger.info("request weixin access token");
                refreshAccessToken();
                refreshJSApiTicket();
                break;
            } catch (Exception e) {
                logger.error("Error when request weixin access token", e);
            }
        }
    }

    @Override
    public WxUserToken getUserToken(String code) throws Exception {
        String url = String.format(
                "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%1$s&secret=%2$s&code=%3$s&grant_type=authorization_code",
                appId, appSecret, code);
        JSONObject jsonObject = get(url);
        WxUserToken token = new WxUserToken();
        token.setAccessToken(jsonObject.getString("access_token"));
        token.setExpiresIn(jsonObject.getInteger("expires_in"));
        token.setOpenId(jsonObject.getString("openid"));
        return token;
    }

    @Override
    public WxUser getUserInfo(String accessToken, String openId) throws Exception {
        WxUser userInfo = null;

        String requestUrl = String.format(
                "https://api.weixin.qq.com/sns/userinfo?access_token=%1$s&openid=%2$s&lang=zh_CN", accessToken, openId);
        JSONObject jsonObject = get(requestUrl);
        if (jsonObject != null) {
            try {
                userInfo = new WxUser();
                userInfo.setOpenId(jsonObject.getString("openid"));
                userInfo.setNickname(jsonObject.getString("nickname"));
                userInfo.setSex(jsonObject.getInteger("sex"));
                userInfo.setCountry(jsonObject.getString("country"));
                userInfo.setProvince(jsonObject.getString("province"));
                userInfo.setCity(jsonObject.getString("city"));
                userInfo.setLanguage(jsonObject.getString("language"));
                userInfo.setHeadImgUrl(jsonObject.getString("headimgurl"));
            } catch (Exception e) {
                throw e;
            }
        }
        return userInfo;
    }

    @Override
    public Map<String, String> getWxConfig(String requestURL) {
        logger.info("get wx config for jsApiTicket={}, url={}", jsApiTicket, requestURL);

        Map<String, String> ret = new HashMap<>();

        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        String nonceStr = RandomStringUtils.randomAlphabetic(16);

        String sign = "jsapi_ticket=" + jsApiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + requestURL;
        String signature = DigestUtils.sha1Hex(sign);

        ret.put("appId", appId);
        ret.put("timestamp", timestamp);
        ret.put("nonceStr", nonceStr);
        ret.put("signature", signature);
        return ret;
    }

    @Override
    public String getAppId() {
        return appId;
    }

    @Override
    public InputStream getVoiceInputStream(String serverId) throws IOException {
        InputStream is = null;
        String srtUrl = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + accessToken + "&media_id=" + serverId;

        URL url = new URL(srtUrl);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        http.setDoOutput(true);
        http.setDoInput(true);
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
        System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
        http.connect();
        return http.getInputStream();
    }

    private JSONObject get(String url) throws ParseException, IOException {
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(get);
        String res = EntityUtils.toString(response.getEntity(), Charset.forName("UTF-8"));
        JSONObject jsonObject = JSON.parseObject(res);
        response.close();
        return jsonObject;
    }
}
