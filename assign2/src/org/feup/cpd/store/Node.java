package org.feup.cpd.store;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Array;
import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class Node {

    private long counter;
    private final AccessPoint ap;
    private final Map<String, String> view;
    private Queue<String> events;
    private Map<String, String> bucket;
    private final File logger;
    private boolean isLeader;
    private String homeDir;

    public Node(AccessPoint ap) {
        this.ap = ap;
        this.isLeader = false;
        this.view = new HashMap<>();
        this.events = new LinkedList<>();
        this.bucket = new HashMap<>();
        initFolders();
        this.homeDir = "files/"+ap.toString()+"/";
        initBucket();
        this.logger = new File(homeDir + ap.toString() + ".log");

        try {
            this.counter = recoverCounter();
        } catch (FileNotFoundException e) {
            this.counter = -1;
        }


    }

    private void initFolders() {
        File bucketdir = new File("files/"+ap.toString()+"/bucket/");
        if (!bucketdir.exists()) {
            bucketdir.mkdirs();
        }
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
        view.put(element, getSHA(element));
    }

    public void removeNodeFromView(String element) {
        view.remove(element);

    }

    public Set<String> getView() {
        return view.keySet();
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

    Comparator<String> compareBySHA = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            return view.get(o1).compareTo(view.get(o2));
        }
    };

    public String findKeyValueLocation(String key){

        if (view.size() == 1){
            return ap.toString();
        }

        List<String> clock = new ArrayList<>();

        for(String node : view.keySet()){
            clock.add(node);
        }

        Collections.sort(clock, compareBySHA);

        for(String node : clock){
            if (key.compareTo(view.get(node)) < 0 ){
                return node;
            }
        }

        return clock.get(0);
    }



    public String locateKeyValue(String key){
        if (bucket.containsKey(key)){
            return ap.toString();
        }
        else{
            //locate node with the "clock structure" and binary search
            return findKeyValueLocation(key);
        }
    }

    public String getValue(String key){
        return bucket.get(key);
    }

    public void putValue(String key, String value){
        bucket.put(key, value);

        try {
            String path =  homeDir + "bucket/" + key;
            File file = new File(path);
            FileWriter writer = new FileWriter(path);
            writer.write(value);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Put Node IOException");
        }
    }

    public void deleteValue(String key){
        bucket.remove(key);

        String path = homeDir + "/bucket/" + key;
        File file = new File(path);
        file.delete();

    }

    private void initBucket() {
        File folder = new File(homeDir+"bucket");
        if (folder.exists()) {
            try {
                for (final File fileEntry : folder.listFiles()) {

                    String key = fileEntry.getName();
                    String value = "";
                    Scanner myReader = new Scanner(fileEntry);
                    while (myReader.hasNextLine()) {
                        value += myReader.nextLine();
                    }
                    myReader.close();
                    bucket.put(key, value);

                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("init bucket FileNotFoundException");
            }
        }
    }

    public void setLeader(boolean isLeader) {
        this.isLeader = isLeader;
    }

    public boolean isLeader() {
        return isLeader;
    }
}
