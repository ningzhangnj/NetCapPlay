
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
 *
 * @author ningzhangnj
 */
@Sharable
public class TelnetClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = Logger.getLogger(TelnetClientHandler.class.getName());

    private AbstractProtocol<TelnetMessage> protocol;

    public TelnetClientHandler(AbstractProtocol<TelnetMessage> protocol) {
        this.protocol = protocol;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (MessageListener<TelnetMessage> listener:protocol.getListeners()) {
            if (listener instanceof TelnetRelay) {
                ((TelnetRelay)listener).setClientChannel(ctx);
            }
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, String msg) throws Exception {
        TimeManager.getInstance().startCount();

        TelnetMessage request = new TelnetMessage(TimeManager.getInstance().getCurrentTimeDiff(), msg);
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
