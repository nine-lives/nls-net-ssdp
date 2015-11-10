package com.nls.net.ssdp;

import java.io.Closeable;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
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

    public List<SsdpChannel> getChannels() {
        return channels;
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
        try {
            selector.close();
        } catch (IOException ignore) {
        }
        thread.interrupt();
    }

    public static SsdpService forAllMulticastAvailableNetworkInterfaces(SsdpPacketListener listener) throws IOException {
        List<NetworkInterface> interfaces = new ArrayList<>();

        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface ni = en.nextElement();
            if (ni.supportsMulticast() && ni.isUp()) {
                interfaces.add(ni);
            }
        }

        return new SsdpService(interfaces, listener);
    }

    public static SsdpService forAllMulticastNetworkInterfaces(SsdpPacketListener listener) throws IOException {
        List<NetworkInterface> interfaces = new ArrayList<>();

        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface ni = en.nextElement();
            if (ni.supportsMulticast()) {
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
        return Collections.unmodifiableList(channels);
    }

    private final class Receiver implements Runnable {
        @Override
        public void run() {
            while (!thread.isInterrupted()) {
                try {
                    for (SsdpPacket packet : selector.receive()) {
                        listener.received(packet);
                    }
                } catch (IOException ignore) {
                }
            }
        }
    }
}
