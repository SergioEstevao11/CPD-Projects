package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.AccessPoint;
import org.feup.cpd.store.Node;
import org.feup.cpd.store.message.JoinMessage;
import org.feup.cpd.store.message.LeaveMessage;

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

        JoinMessage join = new JoinMessage(node.getAccessPoint(), node.getCounter());
        node.addMembershipEvent(join.getContent());
    }

    @Override
    public void leave() throws RemoteException {
        if (node.getCounter() % 2 != 0)
            throw new RemoteException("Unable to call leave() on a cluster already left");

        node.incrementCounter();

        LeaveMessage leave = new LeaveMessage(node.getAccessPoint(), node.getCounter());
        node.addMembershipEvent(leave.getContent());
    }
}
