package com.nls.net.ssdp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SsdpChannel implements Closeable, AutoCloseable {
    private static final CharsetDecoder DECODER = Charset.forName( "us-ascii" ).newDecoder();
	private static final String SSDP_MCAST_ADDRESS = "239.255.255.250";
	private static final int SSDP_PORT = 1900;
	
    private final InetSocketAddress multicastAddress;
    private final DatagramChannel unicastChannel;
    private final DatagramChannel multicastChannel;
    private final Selector selector;

    public SsdpChannel(NetworkInterface networkIf) throws IOException {
        selector = Selector.open();
        unicastChannel = createChannel(networkIf, new InetSocketAddress(networkIf.getInetAddresses().nextElement(), 0), selector);
        multicastChannel = createChannel(networkIf, new InetSocketAddress(SSDP_PORT), selector);
        multicastAddress = new InetSocketAddress(SSDP_MCAST_ADDRESS, SSDP_PORT);
    }
    
    public void send(SsdpMessage message) throws IOException {
    	send(message, multicastAddress);
    }

    public void send(SsdpMessage message, SocketAddress address) throws IOException {
		ByteBuffer.wrap(message.toBytes());
		unicastChannel.send(ByteBuffer.wrap(message.toBytes()), address);
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
        SocketAddress address = channel.receive(buffer);
        SsdpMessage message = SsdpMessage.toMessage(DECODER.decode((ByteBuffer) buffer.flip()).toString());
        return new SsdpPacket(message, address);
        
    }
    public void close() {
    	if(selector.isOpen()) {
    		selector.wakeup();
    		try { selector.close(); } catch(IOException ignore) {}
    	}
    	if(unicastChannel.isOpen()) {
    		try { unicastChannel.close(); } catch(IOException ignore) {}
    	}
    	if(multicastChannel.isOpen()) {
    		try { multicastChannel.close(); } catch(IOException ignore) {}
    	}
    }

    private DatagramChannel createChannel(NetworkInterface networkIf, InetSocketAddress address, Selector selector) throws IOException {
    	DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(address)
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, networkIf);
        channel.join(multicastAddress.getAddress(), networkIf);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
        return channel;
    }
}
