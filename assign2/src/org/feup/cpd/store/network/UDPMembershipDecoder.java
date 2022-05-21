package org.feup.cpd.store.network;

import org.feup.cpd.store.NodeAccessPoint;

public class UDPMembershipDecoder implements Runnable {

    private final NodeAccessPoint cluster;
    private final NodeAccessPoint node;

    public UDPMembershipDecoder(NodeAccessPoint cluster, NodeAccessPoint node) {
        this.cluster = cluster;
        this.node = node;
    }

    @Override
    public void run() {

    }
}
