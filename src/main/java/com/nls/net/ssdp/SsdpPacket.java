package com.nls.net.ssdp;

import java.net.SocketAddress;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        return Objects.hash(message, channel, socketAddress);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SsdpPacket)) {
            return false;
        }

        SsdpPacket that = (SsdpPacket) o;
        return Objects.equals(message, that.message)
                && Objects.equals(channel, that.channel)
                && Objects.equals(socketAddress, that.socketAddress);

    }

    @Override
    public String toString() {
        return String.format(
                "SsdpPacket[messageType=%s, address=%s, channel=%s]",
                message.getType(), socketAddress, channel);
    }
}
