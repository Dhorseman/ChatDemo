package com.hzm.chatdemo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WSServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //websocket基于HTTP协议，所以要有HTTP解码器
        pipeline.addLast(new HttpServerCodec());
        //对于写大数据流的支持
        pipeline.addLast(new ChunkedWriteHandler());
        //对HttpMessage进行聚合，聚合成FullHttpRequest或FullHttpResponse
        //几乎在netty编程中都会用到此handler
        pipeline.addLast(new HttpObjectAggregator(1024*64));
//        以上是用于支持http协议
//        以下是用于支持httpwebsocket

        /**
         *  websocket服务器处理的协议，用于指定给客户端连接访问的路由: /ws
         *  本handler会帮你处理一些繁重复杂的事情
         *  比如握手动作：handshaking(closing,ping,pong) ping+pong=心跳
         *  对于websocket来讲，都是以frames进行传输的，不同的数据类型对应的frames也不同
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        //自定义的handler
        pipeline.addLast(new ChatHandler());

    }
}
