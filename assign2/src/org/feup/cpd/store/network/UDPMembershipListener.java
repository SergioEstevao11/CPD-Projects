package org.feup.cpd.store.network;

import org.feup.cpd.store.NodeAccessPoint;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;

public class UDPMembershipListener extends Thread {

    private final ExecutorService pool;
    private final NodeAccessPoint cluster;

    public UDPMembershipListener(ExecutorService pool, NodeAccessPoint cluster) {
        this.pool = pool;
        this.cluster = cluster;
    }

    @Override
    public void run() {
        try {
            byte[] packetBytes = new byte[4096];
            DatagramPacket packet = new DatagramPacket(packetBytes, packetBytes.length);

            SocketAddress address = new InetSocketAddress(cluster.port());
            NetworkInterface netInterface = NetworkInterface.getByIndex(0);

            MulticastSocket socket = new MulticastSocket();
            socket.joinGroup(address, netInterface);

            while (isAlive()) {
                socket.receive(packet);
                // pool.submit();
            }

            socket.leaveGroup(address, netInterface);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            interrupt();
        }
    }
}
