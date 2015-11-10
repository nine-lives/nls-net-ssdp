package com.nls.net.ssdp;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class SsdpMessage {
    private static final String EOL = "\r\n";

    private final SsdpMessageType type;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public SsdpMessage(SsdpMessageType type) {
        this.type = type;
    }

    public SsdpMessageType getType() {
        return type;
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public SsdpMessage setHeader(String name, String value) {
        headers.put(name.toUpperCase(), value);
        return this;
    }

    public SsdpNotificationType getNotificationType() {
        return SsdpNotificationType.fromRepresentation(headers.get(SsdpCommonHeaders.NTS.name()));
    }

    public static SsdpMessage toMessage(String raw) {
        if (!raw.endsWith(EOL + EOL)) {
            throw new IllegalArgumentException("message is not complete, it should end with a blank line");
        }

        String[] lines = raw.split(EOL);
        SsdpMessage message = new SsdpMessage(SsdpMessageType.fromStartLine(lines[0]));

        for (int i = 1; i < lines.length; ++i) {
            int index = lines[i].indexOf(":");
            if (index < 1) {
                throw new IllegalArgumentException(String.format("invalid header format, line=%s, header=%s", i, lines[i]));
            }

            message.setHeader(lines[i].substring(0, index).trim(), lines[i].substring(index + 1).trim());
        }

        return message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, headers);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SsdpMessage)) {
            return false;
        }

        SsdpMessage that = (SsdpMessage) o;
        return Objects.equals(type, that.type)
                && Objects.equals(headers, that.headers);
    }

    public byte[] toBytes() throws UnsupportedEncodingException {
        return toString().getBytes("UTF-8");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type.getRepresentation()).append(EOL);
        for (Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey().toUpperCase())
                    .append(": ")
                    .append(entry.getValue())
                    .append(EOL);
        }
        builder.append(EOL);
        return builder.toString();
    }
}
