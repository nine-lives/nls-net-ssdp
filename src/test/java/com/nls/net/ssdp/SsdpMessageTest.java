package com.nls.net.ssdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SsdpMessageTest {
	private final static String DISCOVER_MESSAGE = 
			"M-SEARCH * HTTP/1.1\r\n" +
			"HOST: 239.255.255.250:1900\r\n" + 
			"MAN: \"ssdp:discover\"\r\n" + 
			"ST: roku:ecp\r\n\r\n";

	@Test
	public void testConstructor() {
		SsdpMessage message = new SsdpMessage(SsdpMessageType.MSEARCH);
		message.setHeader("Host", "239.255.255.250:1900");
		message.setHeader("Man", "\"ssdp:discover\"");
		message.setHeader("ST", "roku:ecp");
		assertMessage(message);	
	}

	@Test
	public void testParser() {
		SsdpMessage message = SsdpMessage.toMessage(DISCOVER_MESSAGE);
		assertMessage(message);
	}
	
	@Test
	public void testEquality() {
		String commonHost = "239.255.255.250:1900";
		SsdpMessage message1 = new SsdpMessage(SsdpMessageType.MSEARCH);
		message1.setHeader("Host", commonHost);
		
		SsdpMessage message2 = new SsdpMessage(SsdpMessageType.MSEARCH);
		message2.setHeader("Host", commonHost);
		
		assertEquals(message1.hashCode(), message2.hashCode());
		assertTrue(message1.equals(message2));
		assertTrue(message2.equals(message1));

		SsdpMessage message3 = new SsdpMessage(SsdpMessageType.NOTIFY);
		message3.setHeader("Host", commonHost);

		assertNotEquals(message1.hashCode(), message3.hashCode());
		assertFalse(message1.equals(message3));
		assertFalse(message3.equals(message1));
		
		SsdpMessage message4 = new SsdpMessage(SsdpMessageType.MSEARCH);
		message4.setHeader("Host", "localhost:2000");
	
		assertNotEquals(message1.hashCode(), message4.hashCode());
		assertFalse(message1.equals(message4));
		assertFalse(message4.equals(message1));

		assertFalse(message1.equals("a string"));
	}
	
	private void assertMessage(SsdpMessage message) {
		assertEquals(SsdpMessageType.MSEARCH, message.getType());
		assertEquals(3, message.getHeaders().size());
		assertEquals("239.255.255.250:1900", message.getHeader("HOST"));
		assertEquals("239.255.255.250:1900", message.getHeaders().get("HOST"));
		assertEquals("\"ssdp:discover\"", message.getHeader("MAN"));
		assertEquals("\"ssdp:discover\"", message.getHeaders().get("MAN"));
		assertEquals("roku:ecp", message.getHeader("ST"));
		assertEquals("roku:ecp", message.getHeaders().get("ST"));
		
		assertEquals(DISCOVER_MESSAGE, message.toString());
	}
}
