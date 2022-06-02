package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public class PutMessage extends Message{

    private final AccessPoint accessPoint;
    private final String key;
    private final String value;
    private final int port;


    public PutMessage(AccessPoint accessPoint, String key, String value, int port) {
        /**
         * accessPoint : origin node ip address
         * key : key
         * value : value
         * port : open tpc port to receive response, if -1 it means its the node that received the
         *        request from the user
         */
        super("PUT");
        this.accessPoint = accessPoint;
        this.key = key;
        this.value = value;
        this.port = port;

        body.append(accessPoint).append(' ')
                .append(key).append(' ')
                .append(value);

        if (port == -1){
            body.append(' ').append(port);
        }
    }

    @Override
    public String getContent() {
        return accessPoint + " " + key + " " + value;
    }
}
