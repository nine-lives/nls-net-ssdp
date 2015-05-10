package com.nls.net.ssdp;


/**
 * Interface for listening to devices either from NOTIFY messages or responses to
 * an M-SEARCH.
 *
 * @author marc.smith@9ls.com
 */
public interface SsdpPacketListener {
	void received(SsdpPacket packet);
}
