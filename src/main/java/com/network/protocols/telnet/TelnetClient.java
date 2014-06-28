
package com.network.protocols.telnet;

import com.network.protocols.AbstractProtocol;
import com.network.protocols.Startable;
import com.network.worker.RunnableDecorator;
import com.network.worker.ThreadDispatcher;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Simplistic telnet client.
 *
 * @author ningzhangnj
 */
public class TelnetClient extends AbstractProtocol<TelnetMessage> implements Startable {

    private final String host;
    private final int port;
    private final TelnetRelay relay;
    private Channel ch;

    public TelnetClient(String host, int port, TelnetRelay relay) {
        this.host = host;
        this.port = port;
        this.relay = relay;
    }

    @Override
    public void start() {
        ThreadDispatcher.dispatch(new RunnableDecorator("Telnet client thread", new Runnable() {
            @Override
            public void run() {
                addMessageListener(relay);
                EventLoopGroup group = new NioEventLoopGroup();
                try {
                    Bootstrap b = new Bootstrap();
                    b.group(group)
                            .channel(NioSocketChannel.class)
                            .handler(new TelnetClientInitializer(TelnetClient.this));

                    // Start the connection attempt.
                    ch = b.connect(host, port).sync().channel();
                    ch.closeFuture().sync();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }
            }
        }));
    }

    @Override
    public void stop() {
        if (ch != null) {
            ch.close();
        }
    }

    public static void main(String[] args) throws Exception {



    }
}
