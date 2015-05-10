package com.nls.net.ssdp;

import java.io.Closeable;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SsdpService implements Closeable, AutoCloseable {
	private final SsdpSelector selector;
	private final List<SsdpChannel> channels;
	private final SsdpPacketListener listener;
	private final Thread thread;
	
	public SsdpService(List<NetworkInterface> interfaces, SsdpPacketListener listener) throws IOException {
		this.listener = listener;
		this.selector = SsdpSelector.open();
		this.channels = buildChannels(interfaces, selector);
		this.thread = new Thread(new Receiver(), "SSDP Service");
	}
	
	public void listen() throws IllegalStateException {
		if (thread.isAlive() || thread.isInterrupted()) {
			throw new IllegalStateException("the ssdp service is already running");
		}
		thread.start();
	}
	
	@Override
	public void close() {
		for (SsdpChannel channel : channels) {
			channel.close();
		}
		try { selector.close(); } catch (IOException ignore) { }
		thread.interrupt();
	}

	public static SsdpService forAllMulticastAvailableNetworkInterfaces(SsdpPacketListener listener) throws IOException {
		List<NetworkInterface> interfaces = new ArrayList<NetworkInterface>();
		
		for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
			NetworkInterface ni = en.nextElement();
			if (ni.supportsMulticast() && ni.isUp()) {
				interfaces.add(ni);
			}
		}
		
		return new SsdpService(interfaces, listener);
	}

	private List<SsdpChannel> buildChannels(List<NetworkInterface> interfaces, SsdpSelector selector) throws IOException {
		List<SsdpChannel> channels = new ArrayList<>(interfaces.size());
		for (NetworkInterface networkIf : interfaces) {
			channels.add(new SsdpChannel(networkIf, selector));
		}
		return channels;
	}
	
	private final class Receiver implements Runnable {
		@Override
		public void run() {
			while(!thread.isInterrupted()) {
				try {
					for(SsdpPacket packet : selector.receive()) {
						listener.received(packet);
					}
				} catch(IOException ignore) {
				}
			}
		}
	}
	
	private static class MyHandler implements SsdpPacketListener {
		@Override
		public void received(SsdpPacket packet) {
			System.out.println(packet.getSocketAddress() + ", " + packet.getChannel() + ", " + packet.getMessage().getType());
			
		}
	}
	
	public static void main(String args[]) throws IOException, InterruptedException {
		try (SsdpService service = SsdpService.forAllMulticastAvailableNetworkInterfaces(new MyHandler())) {
			System.out.println("start");
			service.listen();
			System.out.println("listening");
			Thread.sleep(30000);
			System.out.println("wakeup");
		}
	}
}
