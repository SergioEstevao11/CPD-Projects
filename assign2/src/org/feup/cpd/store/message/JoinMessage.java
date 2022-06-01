package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public final class JoinMessage extends Message {

    private final AccessPoint accessPoint;
    private final long counter;
    private final int port;

    public JoinMessage(AccessPoint accessPoint, long counter, int port) {
        super("JOIN");
        this.accessPoint = accessPoint;
        this.counter = counter;
        this.port = port;

        body.append(accessPoint).append(' ').append(counter).append(' ').append(port);
    }

    @Override
    public String toString() {
        return type + CRLF + body;
    }

    @Override
    public String getContent() {
        return accessPoint + " " + counter;
    }
}
