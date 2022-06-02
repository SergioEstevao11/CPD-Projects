package org.feup.cpd.store.network;

import com.sun.source.tree.Scope;
import org.feup.cpd.store.AccessPoint;
import org.feup.cpd.store.Node;

import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MulticastListener extends Thread {

    private final ExecutorService pool;
    private final MulticastSocket socket;
    private final SocketAddress address;
    private final NetworkInterface netInterface;

    private final Node node;
    private volatile boolean running;

    public MulticastListener(ExecutorService pool, AccessPoint cluster, Node node) throws IOException {
        this.pool = pool;
        this.socket = new MulticastSocket();
        this.socket.setReuseAddress(true);

        this.node = node;
        this.address = new InetSocketAddress(cluster.getAddress(), cluster.getPort());
        this.netInterface = NetworkInterface.getByInetAddress(cluster.getAddress());
        this.running = true;
    }

    public void stopRunning() {
        this.running = false;
    }

    @Override
    public void run() {
        try {
            socket.joinGroup(address, netInterface);
            System.out.println("Joined multicast group " + address);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        byte[] buffer = new byte[8192];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running) {
            try {
                socket.receive(packet);
                List<String> content = new String(packet.getData()).lines().toList();
                pool.submit(new MembershipDecoder(node, content));
                packet.setLength(buffer.length);

                System.out.println("content = " + content.get(0));

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            socket.leaveGroup(address, netInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
