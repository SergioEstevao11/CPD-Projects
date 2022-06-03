package org.feup.cpd.store;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Node {

    private NodeState state;
    private long counter;
    private final AccessPoint ap;
    private final Set<String> view;
    private final Map<String, String> viewSHA;
    private Queue<String> events;
    private Map<String, String> bucket;
    private final File logger;
    private boolean isLeader;

    public Node(AccessPoint ap) {
        this.ap = ap;
        this.isLeader = false;
        this.view = new HashSet<>();
        this.viewSHA = new HashMap<>();
        this.events = new LinkedList<>();
        this.bucket = new HashMap<>();
        this.logger = new File("log/" + ap.toString() + ".log");
        if (!this.logger.getParentFile().exists()) {
            boolean ignored = this.logger.getParentFile().mkdirs();
        }

        try {
            this.counter = recoverCounter();
        } catch (FileNotFoundException e) {
            this.counter = -1;
        }

        this.state = NodeState.NORMAL;
    }

    private long recoverCounter() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(logger));
        List<String> lines = reader.lines().collect(Collectors.toList());

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

    public NodeState getState() {
        return state;
    }

    public void setState(NodeState state) {
        this.state = state;
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

    public Queue<String> getEvents() {
        return events;
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


    //key-value operations

    public String getSHA(String key){
        StringBuilder keyToBigEndian = new StringBuilder(64);

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));

            for (byte b : hash)
                keyToBigEndian.append(String.format("%02x", b));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.exit(1);

        }

        return keyToBigEndian.toString();
    }

    private String locateKeyValue(String key){
        if (bucket.containsKey(key)){
            return ap.toString();
        }
        else{
            //locate node with the "clock structure" and binary search
            return "";
        }
    }

    public String getValue(String key){
        String key_location = locateKeyValue(key);
        if (key_location == ap.toString()){
            return bucket.get(key);
        }
        else{
            //mandar pedido "get" para o node "key_location"
            return "";
        }
    }

    public void putValue(String key, String value){
        String key_location = locateKeyValue(key);
        if (key_location == ap.toString()){
            bucket.put(key, value);
        }
        else{
            //mandar pedido "put" para o node "key_location"
        }
    }

    public void deleteValue(String key){
        String key_location = locateKeyValue(key);
        if (key_location == ap.toString()){
            bucket.remove(key);
        }
        else{
            //mandar pedido "remove" para o node "key_location"
        }
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return isLeader;
    }
}
