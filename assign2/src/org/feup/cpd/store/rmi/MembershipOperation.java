package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.AccessPoint;
import org.feup.cpd.store.Node;
import org.feup.cpd.store.message.JoinMessage;
import org.feup.cpd.store.message.LeaveMessage;
import org.feup.cpd.store.network.LeaderMulticastSender;
import org.feup.cpd.store.network.MembershipInitializer;
import org.feup.cpd.store.network.MulticastListener;
import org.feup.cpd.store.network.MulticastSender;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;

public class MembershipOperation implements Membership {

    private final ExecutorService pool;
    private final AccessPoint cluster;
    private final Node node;

    private final MulticastSender sender;
    private final MulticastListener listener;

    public MembershipOperation(ExecutorService pool, AccessPoint cluster, Node node) throws IOException {
        this.pool = pool;
        this.cluster = cluster;
        this.node = node;

        this.sender = new MulticastSender(cluster);
        this.listener = new MulticastListener(pool, cluster, node);
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

            if (node.getView().isEmpty()) {
                node.addNodeToView(node.getAccessPoint().toString());
                node.setLeader(true);
            }

        } catch (InterruptedException | IOException e) {
            node.decrementCounter();
            e.printStackTrace();
            throw new RemoteException("Unable to initialize " + node.getAccessPoint() + " within " + cluster);
        }

        listener.start();

        System.out.println(node.getAccessPoint() + " is now a part of " + cluster);
        System.out.println("node view = " + node.getView());

        if (node.isLeader()) {
            Thread leader = new Thread(new LeaderMulticastSender(cluster, node));
            leader.start();
        }
    }

    @Override
    public void leave() throws RemoteException {
        if (node.getCounter() % 2 != 0)
            throw new RemoteException("Unable to call leave() on a cluster already left");

        node.incrementCounter();

        try {
            listener.stopRunning();

            LeaveMessage leave = new LeaveMessage(node.getAccessPoint(), node.getCounter());
            sender.send(leave);
            node.addMembershipEvent(leave.getContent());

        } catch (IOException e) {
            node.decrementCounter();
            e.printStackTrace();
            throw new RemoteException("Unable to send LEAVE via multicast to " + cluster.getAddress());
        }

        node.clearMembershipView();
        System.out.println(node.getAccessPoint() + " is no longer a part of " + cluster);
    }
}
