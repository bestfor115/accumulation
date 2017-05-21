package com.accumulation.lib.sociability;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.accumulation.lib.tool.net.RequestSetting.Builder;
import com.accumulation.lib.tool.net.http.RequestParams;

public class BaseRequestBuilder extends Builder{
	
	@Override
	public RequestParams decorateCommonParams(RequestParams params) {
		// TODO Auto-generated method stub
		params.put("datatype", "json");
		params.put("isApp", "true");
		return super.decorateCommonParams(params);
	}
	
	@Override
	public Header[] decorateCommonHeader(Header[] headers) {
		// TODO Auto-generated method stub
		if(headers==null){
			headers=new Header[1];
		}
		headers[0] = new BasicHeader("cookie", SociabilityClient.getClient().getLoginCookie());
		return headers;
	}
}
