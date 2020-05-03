package com.xingyutang.app.service;

import com.xingyutang.app.model.vo.WxUser;
import com.xingyutang.app.model.vo.WxUserToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface WeixinService {
	public void refreshWxTokens() throws Exception;
	
	public WxUserToken getUserToken(String code) throws Exception;

	public WxUser getUserInfo(String accessToken, String openId) throws Exception;

	public Map<String, String> getWxConfig(String requestURL);

    String getAppId();

	InputStream getVoiceInputStream(String serverId) throws IOException;
}
