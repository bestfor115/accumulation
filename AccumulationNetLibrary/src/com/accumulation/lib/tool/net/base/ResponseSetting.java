package com.accumulation.lib.tool.net.base;

import com.accumulation.lib.tool.net.base.ServiceHelper.ResponseHandlerT;
import com.accumulation.lib.tool.net.base.ServiceHelper.SerializerType;

public class ResponseSetting {
	public final static int NET_EXCEPTION_TYPE_NONE=0;
	public final static int NET_EXCEPTION_TYPE_UN_AUTH=-1;
	public final static int NET_EXCEPTION_TYPE_FAIL=-2;
	public final static int NET_EXCEPTION_TYPE_INVALIDL=-3;
	public final static int NET_EXCEPTION_TYPE_BUSINESS=-4;

	public final static String NETWORK_EXCEPTION_MESSAGE="网络异常";
	
	public final static String DATA_EXCEPTION_MESSAGE="数据解析异常";

	private static final SerializerType defaultSerializerType = SerializerType.JSON;

	public static interface IResponseHandler<T>{
		public  Class<T> getClassType(); 
		public ResponseExceptionHandler getResponseExceptionHandler();
		public ResponseHandlerT<T> getResponseResultHandlerT();
		public SerializerType getSerializerType();
	}
	
	public static interface ResponseExceptionHandler{
		public boolean onResponseException(int type,int code);
	}
	
	public static class DefaultExceptionHandler implements ResponseExceptionHandler{
		@Override
		public boolean onResponseException(int type, int code) {
			// TODO Auto-generated method stub
			return false;
		}
	}

	public static class Builder<T> {
		private  Class<T> type;
		private ResponseExceptionHandler exceptionHandler=createDefaultExceptionHandler();
		private ResponseHandlerT<T> resultHandler;
		private SerializerType serializerType=defaultSerializerType;
		
		public Builder setResponseExceptionHandler(ResponseExceptionHandler handler){
			this.exceptionHandler=handler;
			return this;
		}
		
		public Builder setResponseResultHandlerT(ResponseHandlerT<T> handler,Class<T> type){
			this.resultHandler=handler;
			this.type=type;
			return this;
		}
		
		public Builder setSerializerType(SerializerType serializerType){
			this.serializerType=serializerType;
			return this;
		}
		
		public static ResponseExceptionHandler createDefaultExceptionHandler(){
			return new DefaultExceptionHandler();
		}
		
		
		public IResponseHandler<T> create(){
			return new IResponseHandler<T>(){

				@Override
				public Class<T> getClassType() {
					// TODO Auto-generated method stub
					return type;
				}

				@Override
				public ResponseExceptionHandler getResponseExceptionHandler() {
					// TODO Auto-generated method stub
					return exceptionHandler;
				}

				@Override
				public ResponseHandlerT<T> getResponseResultHandlerT() {
					// TODO Auto-generated method stub
					return resultHandler;
				}

				@Override
				public SerializerType getSerializerType() {
					// TODO Auto-generated method stub
					return serializerType;
				}
			};
		}
	}
}
