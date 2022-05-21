package org.feup.cpd.store.network;

import org.feup.cpd.store.NodeAccessPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.ExecutorService;

public class TPCMembershipListener extends Thread {

    private final ExecutorService pool;
    private final NodeAccessPoint node;

    private static final int MAX_RETRIES = 3;
    private static final int MAX_CONNECTIONS = 3;

    public TPCMembershipListener(ExecutorService pool, NodeAccessPoint node) {
        this.pool = pool;
        this.node = node;
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(node.port(), 3, node.address());
            socket.setSoTimeout(3000);

            int retries = 0;
            int receivedConnections = 0;

            while (receivedConnections < MAX_CONNECTIONS || retries < MAX_RETRIES) {
                try {
                    Socket message = socket.accept();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(message.getInputStream()));
                    pool.submit(new TCPMembershipDecoder(reader.lines().toList()));
                    receivedConnections++;
                } catch (SocketTimeoutException e) {
                    retries++;
                }
            }

        } catch (IOException e) {
            System.err.println("Unable to create ServerSocket");
            e.printStackTrace();
        }

    }
}
