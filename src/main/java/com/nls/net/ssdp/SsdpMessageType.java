package com.nls.net.ssdp;


public enum SsdpMessageType {
	
	MSEARCH("M-SEARCH * HTTP/1.1"),
	NOTIFY("NOTIFY * HTTP/1.1"),
	RESPONSE("HTTP/1.1 200 OK");
	
	private final String representation;
	
	private SsdpMessageType(String representation) {
		this.representation = representation;
	}
	
	public static SsdpMessageType fromStartLine(String startLine) {
		for (SsdpMessageType type : values()) {
			if (type.representation.equals(startLine)) {
				return type;
			}
		}
		
		throw new IllegalArgumentException("startLine=" + startLine);
	}
	
	public String getRepresentation() {
		return representation;
	}
}
