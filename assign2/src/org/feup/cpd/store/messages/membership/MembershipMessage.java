package org.feup.cpd.store.messages.membership;


import org.feup.cpd.store.NodeAccessPoint;
import org.feup.cpd.store.messages.Message;
import org.feup.cpd.store.messages.NetworkMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MembershipMessage extends Message implements NetworkMessage<MembershipMessage> {

    private final File log;
    private final List<NodeAccessPoint> view;

    public MembershipMessage(File log, List<NodeAccessPoint> view) {
        this.log = log;
        this.view = view;
    }

    @Override
    public String serialize() {
        addHeaderLine("MEMBERSHIP");

        StringBuilder viewBuilder = new StringBuilder("VIEW");
        for (NodeAccessPoint node : view)
            viewBuilder.append(' ').append(node);
        addHeaderLine(viewBuilder.toString());

        try {
            BufferedReader reader = new BufferedReader(new FileReader(log));
            List<String> events = reader.lines().toList();
            reader.close();

            StringBuilder logBuilder = new StringBuilder("LOG");
            events.subList(Math.max(0, events.size() - 32), events.size());
            for (String event : events)
                logBuilder.append(' ').append(event);
            addHeaderLine(logBuilder.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.toString();
    }

    @Override
    public MembershipMessage deserialize(String message) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }
}
