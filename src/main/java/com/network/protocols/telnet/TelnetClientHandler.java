
package com.network.protocols.telnet;

import com.network.listener.MessageListener;
import com.network.protocols.AbstractProtocol;
import com.network.protocols.TimeManager;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles a client-side channel.
 */
@Sharable
public class TelnetClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = Logger.getLogger(TelnetClientHandler.class.getName());

    private AbstractProtocol<TelnetMessage> protocol;

    public TelnetClientHandler(AbstractProtocol<TelnetMessage> protocol) {
        this.protocol = protocol;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        TimeManager.getInstance().startCount();

        TelnetMessage request = new TelnetMessage(TimeManager.getInstance().getCurrentTimeDiff(), msg);
        //System.out.println("msg:" + msg);
        for (MessageListener<TelnetMessage> listener:protocol.getListeners()) {
            listener.onClientReceiveMessage(request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(
                Level.WARNING,
                "Unexpected exception from downstream.", cause);
        ctx.close();
    }
}
