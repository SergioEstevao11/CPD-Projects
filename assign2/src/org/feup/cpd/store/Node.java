package org.feup.cpd.store;

public class Node {

    // FIXME: update from a log file later
    private long counter;
    private final AccessPoint ap;

    public Node(AccessPoint ap) {
        this.ap = ap;
        this.counter = -1;
    }

    public AccessPoint getAccessPoint() {
        return ap;
    }

    public long getCounter() {
        return counter;
    }

    public void incrementCounter() {
        counter++;
    }
}
