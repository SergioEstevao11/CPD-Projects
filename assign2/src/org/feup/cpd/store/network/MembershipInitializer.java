package org.feup.cpd.store.network;

import org.feup.cpd.store.Node;
import org.feup.cpd.store.message.JoinMessage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;

public class MembershipInitializer extends Thread {

    private final ExecutorService pool;
    private final MulticastSender sender;
    private final Node node;

    private int retries, received;

    private final JoinMessage join;
    private final ServerSocket server;

    public MembershipInitializer(ExecutorService pool, Node node, MulticastSender sender) throws IOException {
        this.pool = pool;
        this.sender = sender;
        this.node = node;
        this.retries = this.received = 0;

        this.server = new ServerSocket(0, 3, node.getAccessPoint().getAddress());
        this.server.setSoTimeout(3 * 1000);
        this.join = new JoinMessage(node.getAccessPoint(), node.getCounter(), this.server.getLocalPort());
    }

    public JoinMessage getJoin() {
        return join;
    }

    @Override
    public void run() {
        while (retries < 3) {

            try {
                sender.send(join);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            try {
                received = 0;
                do {
                    Socket socket = server.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    pool.submit(new MembershipDecoder(node, in.lines().toList()));
                    received++;

                    socket.close();
                } while (received < 3);

            } catch (SocketTimeoutException e) {
                retries++;
                continue;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
