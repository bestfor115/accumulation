/**
 * probject:cim-server-sdk
 * @version 2.0
 * 
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk.filter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import android.util.Log;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.model.GCMessage;

/**
 * 客户端消息解码
 */
public class ClientMessageDecoder extends ObjectDecoder {

    public ClientMessageDecoder(ClassResolver classResolver) {
        super(classResolver);
    }

    @Override
    public Object decode(ChannelHandlerContext arg0, ByteBuf buffer) throws Exception {

        final ByteBuf tBuffer = PooledByteBufAllocator.DEFAULT.buffer(640);

        buffer.markReaderIndex();
        boolean complete = false;

        while (buffer.isReadable()) {
            byte b = buffer.readByte();
            if (b == Constant.MESSAGE_SEPARATE) {
                complete = true;
                break;
            } else {
                tBuffer.writeByte(b);
            }
        }

        if (complete) {
            String message = new String(new String(ByteBufUtil.getBytes(tBuffer), Constant.UTF8));
            Log.i("ClientMessageDecoder", message);
            Object msg = mappingMessageObject(message);
            return msg;
        } else {
            buffer.resetReaderIndex();
            return null;

        }
    }

    private Object mappingMessageObject(String message) throws Exception {

        if (message.equals(Constant.CMD_HEARTBEAT_REQUEST))// 如果是心跳请求命令则直接返回
        {
            return Constant.CMD_HEARTBEAT_REQUEST;
        }
        return GCMessage.fromXML(message);
    }

}
