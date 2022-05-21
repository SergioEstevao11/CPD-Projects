package org.feup.cpd.store;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.messages.MembershipMessage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.rmi.RemoteException;
import java.util.List;

public class StoreMembershipOperation implements Membership {

    private int membershipCounter = 0;
    private final NodeAccessPoint nodeId;
    private final Path log;

    public StoreMembershipOperation(NodeAccessPoint multicast, NodeAccessPoint nodeId) {
        this.nodeId = nodeId;
        log = Path.of(multicast.toString() + '-' + nodeId.toString() + ".log");

        if (Files.exists(log))
            restoreMembershipCounter();
    }

    @Override
    public void join() throws RemoteException {
        try {
            BufferedWriter logWriter = Files.newBufferedWriter(log, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            MembershipMessage message = new MembershipMessage(nodeId);

            logWriter.write(message.join(membershipCounter));
            logWriter.close();
            membershipCounter++;

        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void leave() throws RemoteException {
        try {
            if (!Files.exists(log))
                throw new FileNotFoundException("File not found: " + log);

            BufferedWriter logWriter = Files.newBufferedWriter(log, StandardOpenOption.APPEND);
            MembershipMessage message = new MembershipMessage(nodeId);

            logWriter.write(message.leave(membershipCounter));
            logWriter.close();
            membershipCounter++;

        } catch (IllegalArgumentException | FileNotFoundException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void restoreMembershipCounter() {
        try {
            List<String> lines = Files.readAllLines(log);
            String lastLine = lines.get(lines.size() - 1);
            String[] tokens = lastLine.split("\s+");

            // The read membership counter is from the previous state
            membershipCounter = Integer.parseInt(tokens[0]) + 1;

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
