/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk.filter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import android.util.Log;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.model.GCMessage;

/**
 * 客户端消息发送前进行编码,可在此加密消息
 * 
 */
public class ClientMessageEncoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object message, ByteBuf out) throws Exception {
    	String sendMessage=null;
    	if(message instanceof GCMessage){
    		sendMessage=GCMessage.toXML((GCMessage) message);
    	}else{
    		sendMessage=message+"";
    	}
        out.writeBytes(sendMessage.getBytes(Constant.UTF8));
        out.writeByte(Constant.MESSAGE_SEPARATE);
        Log.i(ClientMessageEncoder.class.getSimpleName(),sendMessage);
    }

}
