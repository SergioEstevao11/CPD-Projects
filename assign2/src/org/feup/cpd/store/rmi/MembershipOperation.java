package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.AccessPoint;
import org.feup.cpd.store.Node;

import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;

public class MembershipOperation implements Membership {

    private final AccessPoint cluster;
    private final Node node;

    public MembershipOperation(ExecutorService pool, AccessPoint cluster, Node node) {
        this.cluster = cluster;
        this.node = node;
    }

    @Override
    public void join() throws RemoteException {
        if (node.getCounter() % 2 == 0)
            throw new RemoteException("Unable to call join() on a cluster already joined");

        node.incrementCounter();
        System.out.println("join message accepted - new counter: " + node.getCounter());
    }

    @Override
    public void leave() throws RemoteException {
        if (node.getCounter() % 2 != 0)
            throw new RemoteException("Unable to call leave() on a cluster already left");

        node.incrementCounter();
        System.out.println("leave message accepted - new counter: " + node.getCounter());
    }
}
