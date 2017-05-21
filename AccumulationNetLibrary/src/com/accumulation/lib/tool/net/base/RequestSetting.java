package com.accumulation.lib.tool.net.base;

import org.apache.http.Header;

import com.accumulation.lib.tool.net.http.RequestParams;

public class RequestSetting {
	
	private static final String defaultEncoding = "UTF-8";

	public static interface IRequestParams {
		public boolean isGetMethode();

		public RequestParams getRequestParams();

		public String getRequestURI();
		
		public Header[] getRequestHead();
		
		public String getEncoding ();
	}

	public static class Builder {
		RequestParams params = new RequestParams();
		String url = "";
		String encoding=defaultEncoding;
		Header[] headers;
		boolean get=true;

		public Builder setURL(String url) {
			this.url = url;
			return this;
		}

		public Builder setGETMethod(boolean get) {
			this.get = get;
			return this;
		}
		
		public Builder setEncoding(String encoding) {
			this.encoding = encoding;
			return this;
		}

		public Builder setParams(RequestParams params) {
			this.params = params;
			return this;
		}
		
		public Builder setHeader(Header[] headers) {
			this.headers = headers;
			return this;
		}
		public RequestParams decorateCommonParams(RequestParams params){
			return params;
		}
		
		public Header[] decorateCommonHeader(Header[] headers){
			return headers;
		}

		public IRequestParams create() {
			return new IRequestParams() {

				@Override
				public RequestParams getRequestParams() {
					// TODO Auto-generated method stub
					return decorateCommonParams(params);
				}

				@Override
				public String getRequestURI() {
					// TODO Auto-generated method stub
					return url;
				}

				@Override
				public boolean isGetMethode() {
					// TODO Auto-generated method stub
					return get;
				}

				@Override
				public Header[] getRequestHead() {
					// TODO Auto-generated method stub
					return decorateCommonHeader(headers);
				}

				@Override
				public String getEncoding() {
					// TODO Auto-generated method stub
					return encoding;
				}
			};
		}
	}
}
