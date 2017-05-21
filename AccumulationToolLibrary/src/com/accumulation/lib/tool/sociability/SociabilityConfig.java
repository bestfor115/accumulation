package com.accumulation.lib.tool.sociability;

import com.accumulation.lib.tool.net.RequestSetting;
import com.accumulation.lib.tool.net.ResponseSetting;
import com.accumulation.lib.tool.net.ResponseSetting.Builder;
import com.accumulation.lib.tool.sociability.config.BaseConfig;

public abstract class SociabilityConfig extends BaseConfig{
	
	public Class<? extends RequestSetting.Builder> getRequestBuilderClass(){
		return BaseRequestBuilder.class;
	}

	public  <T> Class< ? extends ResponseSetting.Builder<T>>  getResponseBuilderClass(Class<T> c){
		return (Class<? extends Builder<T>>) BaseResponseBuilder.class;
	}
	
}
