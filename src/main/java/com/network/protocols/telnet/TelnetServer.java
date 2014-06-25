package com.network.protocols.telnet;

import com.network.protocols.AbstractProtocol;
import com.network.protocols.Startable;
import com.network.worker.RunnableDecorator;
import com.network.worker.ThreadDispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Simplistic telnet server.
 */
public class TelnetServer extends AbstractProtocol<TelnetMessage> implements Startable {

    private final int port;
    private final TelnetRelay relay;
    private Channel ch;

    public TelnetServer(int port, TelnetRelay relay) {
        this.port = port;
        this.relay = relay;
    }

    @Override
    public void start() {
        ThreadDispatcher.dispatch(new RunnableDecorator("Telnet server thread", new Runnable() {
            @Override
            public void run() {
                addMessageListener(relay);
                EventLoopGroup bossGroup = new NioEventLoopGroup(1);
                EventLoopGroup workerGroup = new NioEventLoopGroup();
                try {
                    ServerBootstrap b = new ServerBootstrap();
                    b.group(bossGroup, workerGroup)
                            .channel(NioServerSocketChannel.class)
                            .childHandler(new TelnetServerInitializer(TelnetServer.this));

                    ch = b.bind(port).sync().channel();
                    ch.closeFuture().sync();
                } catch (Exception e) {
                  e.printStackTrace();
                } finally {
                    bossGroup.shutdownGracefully();
                    workerGroup.shutdownGracefully();
                }
            }
        }));

    }

    @Override
    public void stop() {
        if (ch != null) {
            ch.closeFuture().addListener(ChannelFutureListener.CLOSE);
        }
    }

    public TelnetRelay getRelay() {
        return relay;
    }

    public static void main(String[] args) throws Exception {
        new TelnetServer(8081, new TelnetRelay()).start();
        while (true) {
            Thread.sleep(1000);
        }

    }
}
