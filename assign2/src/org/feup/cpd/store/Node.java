package org.feup.cpd.store;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

    private long counter;
    private final AccessPoint ap;

    private Queue<String> events;
    private File logger;

    public Node(AccessPoint ap) {
        this.ap = ap;
        this.events = new LinkedList<>();

        this.logger = new File("log/" + ap.toString() + ".log");
        File logDir = this.logger.getParentFile();
        if (!logDir.exists()) {
            boolean ignored = logDir.mkdirs();
        }

        try {
            recoverCounter();
        } catch (FileNotFoundException e) {
            this.counter = -1;
        }
    }

    private void recoverCounter() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(logger));
        List<String> lines = reader.lines().toList();

        counter = -1;

        for (String event : lines) {
            String[] fields = event.split("\\s+");
            String id = fields[0];
            long eventCounter = Long.parseLong(fields[1]);

            if (id.equals(ap.toString()))
                counter = Math.max(counter, eventCounter);
        }
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

    public void addMembershipEvent(String event) {
        if (events.isEmpty()) {
            events.add(event);
            dumpMembershipEvent(event);
            return;
        }

        String[] fields = event.split("\\s+");
        String id = fields[0];
        long counter = Long.parseLong(fields[1]);

        Queue<String> tmpEvents = new LinkedList<>();

        while (!events.isEmpty()) {
            String localEvent = events.remove();

            String[] localFields = localEvent.split("\\s+");
            String localId = localFields[0];
            int localCounter = Integer.parseUnsignedInt(localFields[1]);

            if (id.equals(localId)) {
                if (counter <= localCounter) {
                    tmpEvents.add(localEvent);
                } else {
                    tmpEvents.add(event);
                    dumpMembershipEvent(event);
                }
            } else {
                tmpEvents.add(event);
                dumpMembershipEvent(event);
            }
        }

        events = tmpEvents;
    }

    private void dumpMembershipEvent(String membershipEvent) {
        try {
            FileWriter writer = new FileWriter(logger, true);
            writer.append(membershipEvent).append('\n');
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Exception while dumping " + membershipEvent + " to " + logger.getAbsolutePath());
        }
    }
}
