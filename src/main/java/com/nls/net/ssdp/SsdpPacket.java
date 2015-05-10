package com.nls.net.ssdp;

import java.net.SocketAddress;

public class SsdpPacket {

	private final SsdpMessage message;
	private final SsdpChannel channel;
	private final SocketAddress socketAddress;
	
	public SsdpPacket(SsdpMessage message, SsdpChannel channel, SocketAddress socketAddress) {
		this.message = message;
		this.channel = channel;
		this.socketAddress = socketAddress;
	}

	public SsdpMessage getMessage() {
		return message;
	}
	
	public SsdpChannel getChannel() {
		return channel;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
}
