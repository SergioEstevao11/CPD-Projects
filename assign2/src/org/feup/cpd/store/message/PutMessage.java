package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public class PutMessage extends Message{

    private final AccessPoint accessPoint;
    private final String key;
    private final String value;
    private final int port;


    protected PutMessage(AccessPoint accessPoint, String key, String value, int port) {
        super("PUT");
        this.accessPoint = accessPoint;
        this.key = key;
        this.value = value;

        body.append(accessPoint).append(' ')
                .append(key).append(' ')
                .append(value).append()
    }

    @Override
    public String getContent() {
        return null;
    }
}
