package com.nls.net.ssdp;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import static org.junit.Assert.*;

public class SsdpPacketTest {

    @Test
    public void testConstructorAndGetters() throws IOException {
        SsdpChannel channel = Mockito.mock(SsdpChannel.class);
        SsdpMessage message = new SsdpMessage(SsdpMessageType.NOTIFY);
        SocketAddress address = new InetSocketAddress("127.0.0.1", 1234);
        SsdpPacket packet = new SsdpPacket(message, channel, address);
        assertEquals(message, packet.getMessage());
        assertEquals(channel, packet.getChannel());
        assertEquals(address, packet.getSocketAddress());
    }

    @Test
    public void testEqualityAndHashCode() throws IOException {
        SsdpChannel channel = Mockito.mock(SsdpChannel.class);
        SsdpMessage message1 = new SsdpMessage(SsdpMessageType.NOTIFY);
        SsdpMessage message2 = new SsdpMessage(SsdpMessageType.MSEARCH);
        SocketAddress address1 = new InetSocketAddress("127.0.0.1", 1234);
        SocketAddress address2 = new InetSocketAddress("127.0.0.1", 4321);

        SsdpPacket packet1 = new SsdpPacket(message1, channel, address1);
        SsdpPacket packet2 = new SsdpPacket(message1, channel, address1);
        SsdpPacket packet3 = new SsdpPacket(message2, channel, address1);
        SsdpPacket packet4 = new SsdpPacket(message1, channel, address2);
        SsdpPacket packet5 = new SsdpPacket(message1, null, address1);

        assertEquals(packet1.hashCode(), packet2.hashCode());
        assertEquals(packet1, packet2);
        assertEquals(packet2, packet1);

        assertNotEquals(packet1, packet3);
        assertNotEquals(packet1, packet4);
        assertNotEquals(packet1, packet5);
    }

    @Test
    public void testToString() throws IOException {
        SsdpChannel channel = Mockito.mock(SsdpChannel.class);
        SsdpMessage message = new SsdpMessage(SsdpMessageType.NOTIFY);
        SocketAddress address = new InetSocketAddress("127.0.0.1", 1234);
        SsdpPacket packet = new SsdpPacket(message, channel, address);
        assertTrue(packet.toString().matches(
                "SsdpPacket\\[" +
                        "messageType=NOTIFY, " +
                        "address=.*, " +
                        "channel=.*" +
                        "\\]"));
    }
}
