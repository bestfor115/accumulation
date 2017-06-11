/**
 * probject:cim-server-sdk
 * @version 2.0
 *
 * @author 3979434@qq.com
 */
package com.zyl.push.sdk;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zyl.push.sdk.constant.Constant;
import com.zyl.push.sdk.exception.NetWorkDisableException;
import com.zyl.push.sdk.exception.SessionDisableException;
import com.zyl.push.sdk.exception.WriteToClosedSessionException;
import com.zyl.push.sdk.filter.ClientMessageDecoder;
import com.zyl.push.sdk.filter.ClientMessageEncoder;
import com.zyl.push.sdk.model.GCMessage;

/**
 * 连接服务端管理，cim核心处理类，管理连接，以及消息处理
 */
@io.netty.channel.ChannelHandler.Sharable
class ConnectorManager extends SimpleChannelInboundHandler<Object> {

    private final static String TAG = ConnectorManager.class.getSimpleName();

    public static final String ACTION_MESSAGE_RECEIVED = "com.xyz.gc.MESSAGE_RECEIVED";
    public static final String ACTION_SENT_FAILED = "com.xyz.gc.SENT_FAILED";
    public static final String ACTION_SENT_SUCCESSED = "com.xyz.gc.SENT_SUCCESSED";
    public static final String ACTION_CONNECTION_CLOSED = "com.xyz.gc.CONNECTION_CLOSED";
    public static final String ACTION_CONNECTION_FAILED = "com.xyz.gc.CONNECTION_FAILED";
    public static final String ACTION_CONNECTION_SUCCESSED = "com.xyz.gc.CONNECTION_SUCCESSED";
    public static final String ACTION_UNCAUGHT_EXCEPTION = "com.xyz.gc.UNCAUGHT_EXCEPTION";
    public final static String ACTION_CONNECTION_RECOVERY = "com.xyz.gc.CONNECTION_RECOVERY";
    public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final String HEARTBEAT_PINGED = "HEARTBEAT_PINGED";
    public static final int READ_IDLE_TIME = 180;// 秒
    public static final int HEART_TIME_OUT = 30 * 1000;// 秒
    private ExecutorService mExecutor;
    private Channel mChannel;;
    Context mContext;
    Bootstrap mBootstrap;
    EventLoopGroup mLoopGroup;
    private static volatile ConnectorManager mInstance;

    private ConnectorManager(Context ctx) {
        mContext = ctx;
        mExecutor = Executors.newCachedThreadPool();
        mBootstrap = new Bootstrap();
        mLoopGroup = new NioEventLoopGroup();
        mBootstrap.group(mLoopGroup);
        mBootstrap.channel(NioSocketChannel.class);
        mBootstrap.option(ChannelOption.TCP_NODELAY, true);
        mBootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ClientMessageDecoder(ClassResolvers
                        .cacheDisabled(ConnectorManager.class.getClassLoader())));
                pipeline.addLast(new ClientMessageEncoder());
                pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME, 0, 0));
                pipeline.addLast(ConnectorManager.this);
            }
        });
    }

    public static ConnectorManager getManager(Context context) {
        if (mInstance == null) {
            synchronized (ConnectorManager.class) {
                if (mInstance == null) {
                    mInstance = new ConnectorManager(context.getApplicationContext());
                }
            }
        }
        return mInstance;
    }
    private synchronized void syncConnection(final String cimServerHost, final int cimServerPort) {
        try {
            if (isConnected()) {
                return;
            }
            if(Constant.DEBUG){
                Log.e(Constant.TAG, String.format("start connect server %s at prot : %s", cimServerHost,cimServerPort));
            }
            ChannelFuture channelFuture = mBootstrap.connect(
                    new InetSocketAddress(cimServerHost, cimServerPort)).sync(); // 这里的IP和端口，根据自己情况修改
            mChannel = channelFuture.channel();
        } catch (Exception e) {
            Intent intent = new Intent();
            intent.setAction(ACTION_CONNECTION_FAILED);
            intent.putExtra("exception", e);
            mContext.sendBroadcast(intent);
            if(Constant.DEBUG){
                Log.e(Constant.TAG, "******************CIM连接服务器失败  " + cimServerHost + ":" + cimServerPort);
            }
        }

    }

    public void connect(final String cimServerHost, final int cimServerPort) {

        if (!netWorkAvailable(mContext)) {

            Intent intent = new Intent();
            intent.setAction(ACTION_CONNECTION_FAILED);
            intent.putExtra("exception", new NetWorkDisableException());
            mContext.sendBroadcast(intent);
            return;
        }
        Future<?> future = mExecutor.submit(new Runnable() {
            @Override
            public void run() {
                syncConnection(cimServerHost, cimServerPort);
            }
        });
        try {
            if (future.get() != null) {
                connect(cimServerHost, cimServerPort);
            }
        } catch (Exception e) {
            connect(cimServerHost, cimServerPort);
            e.printStackTrace();
        }
    }

    public void send(final GCMessage message) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (mChannel != null && mChannel.isActive()) {
                    boolean isDone = mChannel.writeAndFlush(message).awaitUninterruptibly(10000);
                    if (!isDone) {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_SENT_FAILED);
                        intent.putExtra("exception", new WriteToClosedSessionException());
                        intent.putExtra("message", message);
                        mContext.sendBroadcast(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(ACTION_SENT_SUCCESSED);
                        intent.putExtra("message", message);
                        mContext.sendBroadcast(intent);
                    }
                } else {
                    Intent intent = new Intent();
                    intent.setAction(ACTION_SENT_FAILED);
                    intent.putExtra("exception", new SessionDisableException());
                    intent.putExtra("message", message);
                    mContext.sendBroadcast(intent);
                }
            }
        });
    }

    public void destroy() {
        if (mInstance.mChannel != null) {
            mInstance.mChannel.close();
        }
        mLoopGroup.shutdownGracefully();
        mInstance = null;
    }

    public boolean isConnected() {
        if (mChannel == null) {
            return false;
        }
        return mChannel.isActive();
    }
    public void closeSession() {
        if (mChannel != null) {
            mChannel.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        Log.e(TAG, "userEventTriggered:" + evt.toString());
        if (evt instanceof IdleStateEvent
                && ((IdleStateEvent) evt).state().equals(IdleState.READER_IDLE)) {
            onReaderIdeled(ctx.channel());
        }
        super.userEventTriggered(ctx, evt);
    }

    private void onReaderIdeled(Channel channel) {
        Long lastTime = (Long) channel.attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).get();
        if (lastTime != null && System.currentTimeMillis() - lastTime > HEART_TIME_OUT) {
            channel.close();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        Log.i(TAG, "******************CIM连接服务器成功:" + ctx.channel().localAddress());

        ctx.channel().attr(AttributeKey.valueOf(HEARTBEAT_PINGED)).set(System.currentTimeMillis());

        Intent intent = new Intent();
        intent.setAction(ACTION_CONNECTION_SUCCESSED);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        Log.e(TAG, "******************closeCIM与服务器断开连接:" + ctx.channel().localAddress());
        if (mChannel.id().asLongText().equals(ctx.channel().id().asLongText())) {
            Intent intent = new Intent();
            intent.setAction(ACTION_CONNECTION_CLOSED);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Intent intent = new Intent();
        intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
        intent.putExtra("exception", cause.getCause());
        mContext.sendBroadcast(intent);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Log.d(TAG, "receive a msg :"+msg);

        if (msg.equals(Constant.CMD_HEARTBEAT_REQUEST)) {
            ctx.writeAndFlush(Constant.CMD_HEARTBEAT_RESPONSE);
            ctx.channel().attr(AttributeKey.valueOf(HEARTBEAT_PINGED))
                    .set(System.currentTimeMillis());
        }
        if(msg instanceof GCMessage){
            Intent intent = new Intent();
            intent.setAction(ACTION_MESSAGE_RECEIVED);
            intent.putExtra("message",(GCMessage)msg);
            mContext.sendBroadcast(intent);
        }
    }

    public static boolean netWorkAvailable(Context context) {
        try {
            ConnectivityManager nw = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = nw.getActiveNetworkInfo();
            return networkInfo != null;
        } catch (Exception e) {
        }
        return false;
    }
    public void sendFile(String filePath, final String addr) {


    }
}
