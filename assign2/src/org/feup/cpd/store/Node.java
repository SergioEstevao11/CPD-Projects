package org.feup.cpd.store;

import java.io.*;
import java.util.*;

public class Node {

    private long counter;
    private final AccessPoint ap;
    private final Set<String> view;
    private Queue<String> events;
    private final File logger;

    public Node(AccessPoint ap) {
        this.ap = ap;
        this.view = new HashSet<>();
        this.events = new LinkedList<>();

        this.logger = new File("log/" + ap.toString() + ".log");
        if (!this.logger.getParentFile().exists()) {
            boolean ignored = this.logger.getParentFile().mkdirs();
        }

        try {
            this.counter = recoverCounter();
        } catch (FileNotFoundException e) {
            this.counter = -1;
        }
    }

    private long recoverCounter() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(logger));
        List<String> lines = reader.lines().toList();

        long cnt = -1;

        for (String event : lines) {
            String[] fields = event.split("\\s+");
            String id = fields[0];
            long eventCounter = Long.parseLong(fields[1]);

            if (id.equals(ap.toString()))
                cnt = Math.max(cnt, eventCounter);
        }

        return cnt;
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

    public void decrementCounter() {
        counter--;
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


    public void clearMembershipView() {
        view.clear();
    }

    public void addNodeToView(String element) {
        view.add(element);
    }

    public void removeNodeFromView(String element) {
        view.remove(element);
    }

    public Set<String> getView() {
        return view;
    }
}
