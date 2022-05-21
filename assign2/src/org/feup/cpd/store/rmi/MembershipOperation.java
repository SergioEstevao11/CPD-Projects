package org.feup.cpd.store.rmi;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.NodeAccessPoint;
import org.feup.cpd.store.messages.Message;
import org.feup.cpd.store.messages.NetworkMessage;
import org.feup.cpd.store.messages.membership.JoinMessage;
import org.feup.cpd.store.messages.membership.LeaveMessage;
import org.feup.cpd.store.network.TPCMembershipListener;
import org.feup.cpd.store.network.UDPMembershipListener;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MembershipOperation implements Membership {

    private final NodeAccessPoint cluster;
    private final NodeAccessPoint node;
    private List<NodeAccessPoint> membershipView;
    private long membershipCounter;

    private final TPCMembershipListener membershipInitializer;
    private final UDPMembershipListener membershipListener;

    public MembershipOperation(ExecutorService pool, NodeAccessPoint cluster, NodeAccessPoint node) {
        this.cluster = cluster;
        this.node = node;

        this.membershipCounter = -1;
        this.membershipView = new ArrayList<>();

        this.membershipInitializer = new TPCMembershipListener(pool, node);
        this.membershipListener = new UDPMembershipListener(pool, cluster);
    }

    private void sendMulticastMessage(NetworkMessage<? extends Message> message) {
        try {
            SocketAddress address = new InetSocketAddress(cluster.address(), cluster.port());
            byte[] messageBytes = message.serialize().getBytes(StandardCharsets.UTF_8);

            DatagramSocket socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length, address);

            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            membershipInitializer.interrupt();
        }
    }

    @Override
    public void join() throws RemoteException {
        if (membershipCounter % 2 == 0)
            throw new RemoteException("Unable to call join() on a cluster already joined");

        // 1. Increase the membership counter
        // FIXME: Need to sync - membership channel must be ready before sending JOIN
        // 2. Prepare the TCP listener for the incoming MEMBERSHIP messages
        // 3. Send the JOIN message
        // 4. Wait for the decoding of the MEMBERSHIP messages

        membershipCounter++;
        membershipInitializer.start();
        sendMulticastMessage(new JoinMessage(node, membershipCounter));

        try {
            membershipInitializer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RemoteException("Unable to fulfill the membership initialization");
        }

        // 5. Prepare the UDP listener
        membershipListener.start();

        // 6. Election
    }

    @Override
    public void leave() throws RemoteException {
        if (membershipCounter % 2 != 0)
            throw new RemoteException("Unable to call leave() on a cluster already left");

        // 1. Increment the membership counter
        // 2. Send the LEAVE message
        // 3. Interrupt the membership listener

        membershipCounter++;
        sendMulticastMessage(new LeaveMessage(node, membershipCounter));
        membershipListener.interrupt();

        // 4. ?? To be discovered
    }
}
