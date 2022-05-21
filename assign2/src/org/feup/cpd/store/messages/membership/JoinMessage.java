package org.feup.cpd.store.messages.membership;

import org.feup.cpd.store.NodeAccessPoint;
import org.feup.cpd.store.messages.Message;
import org.feup.cpd.store.messages.NetworkMessage;

public class JoinMessage extends Message implements NetworkMessage<JoinMessage> {

    private final NodeAccessPoint node;
    private final long count;

    public JoinMessage(NodeAccessPoint node, long count) {
        this.node = node;
        this.count = count;
    }

    @Override
    public String toString() {
        return count + " " + node;
    }

    @Override
    public String serialize() {
        addHeaderLine("JOIN" + ' ' + count + ' ' + node);
        return super.toString();
    }

    @Override
    public JoinMessage deserialize(String message) throws IllegalArgumentException {
        String[] tokens = message.split("\\s+");

        if (!tokens[0].equals("JOIN"))
            throw new IllegalArgumentException("Unable to deserialize the provided message");

        long count = Long.parseLong(tokens[1]);

        String[] address = tokens[1].split(":");
        NodeAccessPoint node = NodeAccessPoint.getInstance(address[0], address[1]);

        return new JoinMessage(node, count);
    }
}
