package com.accumulation.app;

import com.accumulation.lib.sociability.SociabilityConfig;
import com.accumulation.lib.tool.net.RequestSetting;
import com.accumulation.lib.tool.net.ResponseSetting.Builder;

public class ESTPConfig extends SociabilityConfig {
	public static final String DOMAIN = "http://www.pei98.com:8080";

	@Override
	public String getDomain() {
		// TODO Auto-generated method stub
		return DOMAIN;
	}

}
