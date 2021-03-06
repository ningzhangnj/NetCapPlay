package com.network.protocols.telnet;

import com.network.listener.MessageListener;
import com.network.protocols.AbstractProtocol;
import com.network.protocols.TimeManager;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles a server-side channel.
 *
 * @author ningzhangnj
 */
@Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = Logger.getLogger(TelnetServerHandler.class.getName());

    private AbstractProtocol<TelnetMessage> protocol;

    public TelnetServerHandler(AbstractProtocol<TelnetMessage> protocol) {
        this.protocol = protocol;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send greeting for a new connection.
        ctx.write(
                "Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
        ctx.write("It is " + new Date() + " now.\r\n");
        ctx.flush();

        for (MessageListener<TelnetMessage> listener:protocol.getListeners()) {
            if (listener instanceof TelnetRelay) {
                ((TelnetRelay)listener).setServerChannel(ctx);
            }
        }
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, String request) throws Exception {
        //Telnet server need to skip the first message, need to investigate why.

        TimeManager.getInstance().startCount();

        TelnetMessage msg = new TelnetMessage(TimeManager.getInstance().getCurrentTimeDiff(), request);

        for (MessageListener<TelnetMessage> listener:protocol.getListeners()) {
            listener.onServerReceiveMessage(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
