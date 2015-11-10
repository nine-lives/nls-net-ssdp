package com.nls.net.ssdp;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SsdpSelectorTest {

    @Test
    public void test() throws IOException {
        try (SsdpSelector selector = SsdpSelector.open()) {
            assertFalse(selector.isInternalSelector());
        }
        try (SsdpSelector selector = SsdpSelector.open(true)) {
            assertTrue(selector.isInternalSelector());
        }
    }

    @Test
    public void testAutoclose() throws IOException {
        SsdpSelector selectorRef;
        try (SsdpSelector selector = SsdpSelector.open()) {
            selectorRef = selector;
            assertTrue(selectorRef.isOpen());
        }
        assertFalse(selectorRef.isOpen());
    }
}
