package com.nls.net.ssdp;

import java.net.SocketAddress;

public class SsdpPacket {

	private final SsdpMessage message;
	private final SocketAddress socketAddress;
	
	public SsdpPacket(SsdpMessage message, SocketAddress socketAddress) {
		this.message = message;
		this.socketAddress = socketAddress;
	}

	public SsdpMessage getMessage() {
		return message;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}
}
