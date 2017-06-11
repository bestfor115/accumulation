/**
 * probject:cim
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.app;

public interface GCConstant {

    // 服务端IP地址
    public static final String CIM_SERVER_HOST = "192.168.1.108";

    // 服务端web地址
    public static final String SERVER_URL = "http://" + CIM_SERVER_HOST + ":8080/cim-server";

    // 注意，这里的端口不是tomcat的端口，CIM端口在服务端spring-cim.xml中配置的，没改动就使用默认的23456
    public static final int CIM_SERVER_PORT = 23404;

}
