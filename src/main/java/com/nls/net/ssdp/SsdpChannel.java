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
import java.util.List;

public class SsdpChannel implements Closeable, AutoCloseable {
	private static final InetSocketAddress SSDP_MCAST_ADDRESS = new InetSocketAddress("239.255.255.250", 1900);
	
    private final DatagramChannel unicastChannel;
    private final DatagramChannel multicastChannel;
    private final SsdpSelector selector;

    public SsdpChannel(NetworkInterface networkIf) throws IOException {
        this(networkIf, SsdpSelector.open(true));
    }
    
    public SsdpChannel(NetworkInterface networkIf, SsdpSelector selector) throws IOException {
        this.selector = selector;
        this.unicastChannel = createChannel(networkIf, new InetSocketAddress(networkIf.getInetAddresses().nextElement(), 0), selector);
        this.multicastChannel = createChannel(networkIf, new InetSocketAddress(SSDP_MCAST_ADDRESS.getPort()), selector);
    }

    public NetworkInterface getNetworkInterface() throws IOException {
        return multicastChannel.getOption(StandardSocketOptions.IP_MULTICAST_IF);
    }
    
	public void send(SsdpMessage message) throws IOException {
    	send(message, SSDP_MCAST_ADDRESS);
    }

    public void send(SsdpMessage message, SocketAddress address) throws IOException {
		ByteBuffer.wrap(message.toBytes());
		unicastChannel.send(ByteBuffer.wrap(message.toBytes()), address);
    }
    
    public List<SsdpPacket> receive() throws IOException {
    	if (!selector.isInternalSelector()) {
    		throw new IllegalAccessError("use the ssdp selector receive method on the selector passed in");
    	}
    	return selector.receive();
    }

    public void close() {
    	if(unicastChannel.isOpen()) {
        	selector.unregister(unicastChannel);
    		try { unicastChannel.close(); } catch(IOException ignore) {}
    	}
    	if(multicastChannel.isOpen()) {
        	selector.unregister(multicastChannel);
    		try { multicastChannel.close(); } catch(IOException ignore) {}
    	}
    	if (selector.isInternalSelector()) {
    		try { selector.close(); } catch(IOException ignore) {}
    	}
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder("SsdpChannel");
    	try {
			sb.append("[")
				.append(unicastChannel.getOption(StandardSocketOptions.IP_MULTICAST_IF).toString())
				.append("]");
		} catch (IOException ignore) {
			sb.append("[UnknownInterface]");
		}
    	
    	return sb.toString();
    }
    
    private DatagramChannel createChannel(NetworkInterface networkIf, InetSocketAddress address, SsdpSelector selector) throws IOException {
    	DatagramChannel channel = DatagramChannel.open(StandardProtocolFamily.INET6)
                .setOption(StandardSocketOptions.SO_REUSEADDR, true)
                .bind(address)
                .setOption(StandardSocketOptions.IP_MULTICAST_IF, networkIf);
        channel.join(SSDP_MCAST_ADDRESS.getAddress(), networkIf);
        channel.configureBlocking(false);
        selector.register(this, channel);
        return channel;
    }    
}
