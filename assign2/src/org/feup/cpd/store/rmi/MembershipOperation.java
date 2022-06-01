package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.AccessPoint;
import org.feup.cpd.store.Node;
import org.feup.cpd.store.message.JoinMessage;
import org.feup.cpd.store.message.LeaveMessage;
import org.feup.cpd.store.network.MembershipInitializer;
import org.feup.cpd.store.network.MulticastSender;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;

public class MembershipOperation implements Membership {

    private final ExecutorService pool;
    private final AccessPoint cluster;
    private final Node node;

    private final MulticastSender sender;

    public MembershipOperation(ExecutorService pool, AccessPoint cluster, Node node) {
        this.pool = pool;
        this.cluster = cluster;
        this.node = node;

        this.sender = new MulticastSender(this.cluster);
    }

    @Override
    public void join() throws RemoteException {
        if (node.getCounter() % 2 == 0)
            throw new RemoteException("Unable to call join() on a cluster already joined");

        node.incrementCounter();

        try {
            MembershipInitializer initializer = new MembershipInitializer(pool, node, sender);
            initializer.start();
            initializer.join();

            node.addMembershipEvent(initializer.getJoin().getContent());
        } catch (InterruptedException | IOException e) {
            node.decrementCounter();
            e.printStackTrace();
            throw new RemoteException("Unable to initialize " + node.getAccessPoint() + " within " + cluster);
        }

        System.out.println(node.getAccessPoint() + " is now a part of " + cluster);
    }

    @Override
    public void leave() throws RemoteException {
        if (node.getCounter() % 2 != 0)
            throw new RemoteException("Unable to call leave() on a cluster already left");

        node.incrementCounter();

        LeaveMessage leave = new LeaveMessage(node.getAccessPoint(), node.getCounter());
        node.addMembershipEvent(leave.getContent());

        try {
            sender.send(leave);
        } catch (IOException e) {
            node.decrementCounter();
            e.printStackTrace();
            throw new RemoteException("Unable to send LEAVE via multicast to " + cluster.getAddress());
        }
    }
}
