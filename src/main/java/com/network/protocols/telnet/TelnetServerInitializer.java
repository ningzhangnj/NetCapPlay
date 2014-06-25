package com.network.protocols.telnet;

import com.network.protocols.AbstractProtocol;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {
    private final StringDecoder DECODER = new StringDecoder();
    private final StringEncoder ENCODER = new StringEncoder();
    private TelnetServerHandler SERVERHANDLER;

    public TelnetServerInitializer(AbstractProtocol<TelnetMessage> protocol) {
        SERVERHANDLER = new TelnetServerHandler(protocol);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // Add the text line codec combination first,
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
                8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", DECODER);
        pipeline.addLast("encoder", ENCODER);

        // and then business logic.
        pipeline.addLast("handler", SERVERHANDLER);
    }
}
