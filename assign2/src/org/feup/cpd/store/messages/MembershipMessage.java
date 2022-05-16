package org.feup.cpd.store.messages;


import org.feup.cpd.store.NodeAccessPoint;

public class MembershipMessage extends Message {

    private final NodeAccessPoint nodeId;

    public MembershipMessage(NodeAccessPoint nodeId) {
        super();
        this.nodeId = nodeId;
    }

    public String join(int counter) throws IllegalArgumentException {
        if (counter % 2 != 0)
            throw new IllegalArgumentException("Counter out of sync while joining cluster");

        clean();
        addHeaderLine(counter + ':' + nodeId.address().toString());
        return assemble();
    }

    public String leave(int counter) throws IllegalArgumentException {
        if (counter % 2 == 0)
            throw new IllegalArgumentException("Counter out of sync while leaving cluster");

        clean();
        addHeaderLine(counter + ':' + nodeId.address().toString());
        return assemble();
    }

    public String membership() {
        throw new UnsupportedOperationException();
    }
}
