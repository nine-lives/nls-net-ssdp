package com.nls.net.ssdp;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class SsdpMessageTypeTest {
    @Test
    public void testAvailableValues() {
        assertEquals(
                EnumSet.allOf(SsdpMessageType.class),
                EnumSet.of(SsdpMessageType.MSEARCH, SsdpMessageType.NOTIFY, SsdpMessageType.RESPONSE));
    }

    @Test
    public void testFromStartLine() {
        assertEquals(SsdpMessageType.MSEARCH, SsdpMessageType.fromStartLine("M-SEARCH * HTTP/1.1"));
        assertEquals(SsdpMessageType.NOTIFY, SsdpMessageType.fromStartLine("NOTIFY * HTTP/1.1"));
        assertEquals(SsdpMessageType.RESPONSE, SsdpMessageType.fromStartLine("HTTP/1.1 200 OK"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromStartLineWithInvalidValue() {
        SsdpMessageType.fromStartLine("INVALID");
    }

    @Test
    public void testGetRepresentation() {
        assertEquals("M-SEARCH * HTTP/1.1", SsdpMessageType.MSEARCH.getRepresentation());
        assertEquals("NOTIFY * HTTP/1.1", SsdpMessageType.NOTIFY.getRepresentation());
        assertEquals("HTTP/1.1 200 OK", SsdpMessageType.RESPONSE.getRepresentation());
    }

}
