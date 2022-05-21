package org.feup.cpd.store;

import org.feup.cpd.store.rmi.MembershipService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Store {

    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println("Usage: java Store <IP_mcast_addr> <IP_mcast_port> <node_id> <Store_port>");
            System.exit(1);
        }

        // Command line arguments processing
        NodeAccessPoint cluster = NodeAccessPoint.getInstance(args[0], args[1]);
        NodeAccessPoint node = NodeAccessPoint.getInstance(args[2], args[3]);

        // Identifier for the node
        StringBuilder nodeKey = new StringBuilder(64);
        StringBuilder id = new StringBuilder().append(cluster).append(node);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(id.toString().getBytes(StandardCharsets.UTF_8));

            for (byte b : hash)
                nodeKey.append(String.format("%02x", b));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Environment setup for the node
        try {
            Files.createDirectory(Path.of("logs"));
            Files.createDirectories(Path.of("db/" + nodeKey));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        MembershipService membership = new MembershipService(cluster, node);
        membership.listen();
    }
}
