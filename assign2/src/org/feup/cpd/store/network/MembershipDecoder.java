package org.feup.cpd.store.network;

import org.feup.cpd.store.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.List;

public class MembershipDecoder implements Runnable {

    private final Node node;
    private final List<String> content;

    public MembershipDecoder(Node node, List<String> content) {
        this.node = node;
        this.content = content;
    }

    private void decodeMembership() {
        int viewStart = content.indexOf("VIEW");
        int logsStart = content.indexOf("LOGS");

        List<String> view = content.subList(viewStart + 1, logsStart);
        for (String element : view)
            node.addNodeToView(element);

        List<String> events = content.subList(logsStart + 1, content.size());
        for (String event : events)
            node.addMembershipEvent(event);
    }

    private void decodeJoin() {
        String[] fields = content.get(1).split("\\s+");
        node.addNodeToView(fields[0]);
        node.addMembershipEvent(fields[0] + " " + fields[1]);

        String hostname = fields[0].split(":")[0];
        int port = Integer.parseUnsignedInt(fields[2]);

        try  {
            Socket socket = new Socket(hostname, port);
            socket.shutdownOutput();

        } catch (IOException | UnknownHostException e) {

        }
    }

    private void decodeLeave() {
        String[] fields = content.get(1).split("\\s+");
        node.removeNodeFromView(fields[0]);
        node.addMembershipEvent(content.get(1));
    }

    @Override
    public void run() {
        switch (content.get(0)) {
            case "MEMBERSHIP" -> decodeMembership();
            case "JOIN" -> decodeJoin();
            case "LEAVE" -> decodeLeave();
            default -> System.err.println("Error while decoding message of type: " + content.get(0));
        }
    }
}
