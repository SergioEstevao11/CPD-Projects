package org.feup.cpd.store.network;

import java.util.List;

public class TCPMembershipDecoder implements Runnable {

    List<String> content;

    public TCPMembershipDecoder(List<String> content) {
        this.content = content;
    }

    @Override
    public void run() {

    }
}
