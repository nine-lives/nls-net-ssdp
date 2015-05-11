package com.nls.net.ssdp;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SsdpSelector implements Closeable, AutoCloseable {
    private static final CharsetDecoder DECODER = Charset.forName( "us-ascii" ).newDecoder();
    private final boolean internal;
	private final Selector selector;
	private final Map<DatagramChannel, SsdpChannel> channelMap = new HashMap<>();
	
	private SsdpSelector(boolean internal) throws IOException {
		this.internal = internal;
		this.selector = Selector.open();
	}
	
	public static SsdpSelector open() throws IOException {
		return open(false);
	}
	
	static SsdpSelector open(boolean internal) throws IOException {
		return new SsdpSelector(internal);
	}

	@Override 
	public void close() throws IOException {
		selector.close();
	}
	
	public boolean isOpen() {
	    return selector.isOpen();
	}
	
    public List<SsdpPacket> receive() throws IOException {
    	if(selector.select() == 0) {
    		return Collections.emptyList();
    	}
    	
    	List<SsdpPacket> packets = new ArrayList<>();
		Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
		while (keyIterator.hasNext()) {
			SelectionKey key = (SelectionKey) keyIterator.next();
			packets.add(receive((DatagramChannel) key.channel(), (ByteBuffer) key.attachment()));
			keyIterator.remove();
		}
		return packets;
    }

    private SsdpPacket receive(DatagramChannel channel, ByteBuffer buffer) throws IOException {
    	buffer.clear();
        SocketAddress address = channel.receive(buffer);
        SsdpMessage message = SsdpMessage.toMessage(DECODER.decode((ByteBuffer) buffer.flip()).toString());
        return new SsdpPacket(message, channelMap.get(channel), address);
    }

	void register(SsdpChannel ssdpChannel, DatagramChannel datagramChannel) throws ClosedChannelException {
		datagramChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
		channelMap.put(datagramChannel, ssdpChannel);
	}

	void unregister(DatagramChannel datagramChannel) {
		channelMap.remove(datagramChannel);
		datagramChannel.keyFor(selector).cancel();
	}

	boolean isInternalSelector() {
		return internal;
	}
}
