package org.feup.cpd.store;

import org.feup.cpd.interfaces.Membership;
import org.feup.cpd.store.messages.MembershipMessage;
import org.w3c.dom.Node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;

public class StoreMembershipOperation implements Membership {

    private int membershipCounter = 0;
    private final NodeAccessPoint multicast;
    private final NodeAccessPoint nodeId;

    public StoreMembershipOperation(NodeAccessPoint multicast, NodeAccessPoint nodeId) {
        this.multicast = multicast;
        this.nodeId = nodeId;
    }

    @Override
    public void join() throws RemoteException {
        String logFileName = multicast.toString() + '-' + nodeId.toString() + ".log";
        File logFile = new File(logFileName);

        try {
            if (!logFile.exists()) {
                boolean logFileCreated = logFile.createNewFile();
                if (!logFileCreated)
                    throw new IOException("Cannot create logFile for node: " + nodeId);
            }

            FileWriter logFileWriter = new FileWriter(logFile, true);
            MembershipMessage message = new MembershipMessage(nodeId);

            logFileWriter.write(message.join(membershipCounter));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(1);
        }

        membershipCounter++;
    }

    @Override
    public void leave() throws RemoteException {
        File logFile = new File(nodeId.toString() + ".log");

        try {
            if (!logFile.exists())
                throw new FileNotFoundException("Cannot find log file for " + nodeId);

            FileWriter logFileWriter = new FileWriter(logFile, true);
            MembershipMessage message = new MembershipMessage(nodeId);

            logFileWriter.append(message.leave(membershipCounter));
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            System.exit(1);
        }

        membershipCounter++;
    }
}
