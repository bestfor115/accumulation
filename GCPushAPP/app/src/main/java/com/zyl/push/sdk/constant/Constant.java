/**
 * probject:cim-android-sdk
 * @version 2.1.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk.constant;

/**
 * 常量
 */
public interface Constant {

    public static String UTF8 = "UTF-8";

    public static byte MESSAGE_SEPARATE = '\b';

    public static long RECONN_INTERVAL_TIME = 30 * 1000;

    public static int CIM_DEFAULT_MESSAGE_ORDER = 1;
    
    public static final boolean DEBUG=true;

    public static final String TAG="GCSystem";
    public static class ReturnCode {

		public final static int CODE_404 =404;
		
		public final static int CODE_403 =403;
		
		public final static int CODE_405 =405;
		
		public final static int CODE_200 =200;
		
		public final static int CODE_206 =206;
		
		public final static int CODE_500 =500;
		
		public final static int CODE_FILE_TASK_MASK =-501;
		public final static int CODE_FILE_TASK_EXCEPTION =-502;
		public final static int CODE_DEVICE_OFFLINE =-503;

    }

    /**
     * 服务端心跳请求命令 cmd_server_hb_request
     */
    public static final String CMD_HEARTBEAT_REQUEST = "S_H_RQ";
    /**
     * 客户端心跳响应命令 cmd_client_hb_response
     */
    public static final String CMD_HEARTBEAT_RESPONSE = "C_H_RS";

    public static class MessageType {
        // 用户会 踢出下线消息类型
        public final static String TYPE_999 = "999";
        public final static String TYPE_NULL = "0";
        public final static String TYPE_CLIENT_BIND = "100";
        public final static String TYPE_PUSH_SCRIPT = "101";
        public final static String TYPE_PUSH_LUA_SCRIPT = "102";
        public final static String TYPE_FILE_REQUEST = "103";
        public final static String TYPE_FILE_TRANSPORT= "104";
    }

}