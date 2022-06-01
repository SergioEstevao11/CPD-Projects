package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public final class JoinMessage extends Message {

    private final AccessPoint accessPoint;
    private final long counter;

    public JoinMessage(AccessPoint accessPoint, long counter) {
        super("JOIN");
        this.accessPoint = accessPoint;
        this.counter = counter;

        body.append(accessPoint).append(' ').append(counter);
    }

    @Override
    public String toString() {
        return type + CRLF + body;
    }
}
