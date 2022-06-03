package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public final class JoinMessage extends Message {

    private final AccessPoint newNode;
    private final long counter;
    private final int port;

    public JoinMessage(AccessPoint newNode, long counter, int port) {
        super("JOIN");
        this.newNode = newNode;
        this.counter = counter;
        this.port = port;

        body.append(newNode).append(' ').append(counter).append(' ').append(port);
    }

    @Override
    public String toString() {
        return type + CRLF + body;
    }

    @Override
    public String getContent() {
        return newNode + " " + counter;
    }
}
