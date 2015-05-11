package com.nls.net.ssdp;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;

import org.junit.Test;

public class SsdpNotifcationTypeTest {
	@Test
	public void testAvailableValues() {
		assertEquals(
				EnumSet.allOf(SsdpNotificationType.class),
				EnumSet.of(SsdpNotificationType.ALIVE, SsdpNotificationType.BYEBYE, SsdpNotificationType.UPDATE));
	}

	@Test
	public void testFromRepresentation() {
		assertEquals(SsdpNotificationType.ALIVE, SsdpNotificationType.fromRepresentation("ssdp:alive"));
		assertEquals(SsdpNotificationType.BYEBYE, SsdpNotificationType.fromRepresentation("ssdp:byebye"));
		assertEquals(SsdpNotificationType.UPDATE, SsdpNotificationType.fromRepresentation("ssdp:update"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFromStartLineWithInvalidValue() {
	    SsdpNotificationType.fromRepresentation("INVALID");
	}

	@Test
	public void testGetRepresentation() {
		assertEquals("ssdp:alive", SsdpNotificationType.ALIVE.getRepresentation());
		assertEquals("ssdp:byebye", SsdpNotificationType.BYEBYE.getRepresentation());
		assertEquals("ssdp:update", SsdpNotificationType.UPDATE.getRepresentation());
	}

}
