package com.xingyutang.service;

import com.xingyutang.model.vo.WxUser;
import com.xingyutang.model.vo.WxUserToken;

import java.util.Map;

public interface WeixinService {
	public void refreshWxTokens() throws Exception;
	
	public WxUserToken getUserToken(String code) throws Exception;

	public WxUser getUserInfo(String accessToken, String openId) throws Exception;

	public Map<String, String> getWxConfig(String requestURL);

    String getAppId();
}
