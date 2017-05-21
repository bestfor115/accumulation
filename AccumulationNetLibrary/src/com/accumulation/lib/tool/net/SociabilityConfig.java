package com.accumulation.lib.tool.net;


import com.accumulation.lib.tool.net.base.RequestSetting;
import com.accumulation.lib.tool.net.base.ResponseSetting;
import com.accumulation.lib.tool.net.config.BaseConfig;

public abstract class SociabilityConfig extends BaseConfig{
	
	public abstract Class<? extends RequestSetting.Builder> getRequestBuilderClass();

	public  abstract <T> Class< ? extends ResponseSetting.Builder<T>>  getResponseBuilderClass(Class<T> c);
}
