package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public final class LeaveMessage extends Message {

    private final AccessPoint accessPoint;
    private final long count;

    public LeaveMessage(AccessPoint accessPoint, long count) {
        super("LEAVE");
        this.accessPoint = accessPoint;
        this.count = count;

        body.append(accessPoint).append(' ').append(count);
    }

    @Override
    public String toString() {
        return type + CRLF + body;
    }
}
