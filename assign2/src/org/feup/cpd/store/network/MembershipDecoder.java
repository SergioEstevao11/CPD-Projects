package org.feup.cpd.store.network;

import org.feup.cpd.store.Node;

import java.util.List;

public class MembershipDecoder implements Runnable {

    private final Node node;
    private final List<String> content;

    public MembershipDecoder(Node node, List<String> content) {
        this.node = node;
        this.content = content;
    }



    @Override
    public void run() {
        if (!content.get(0).equals("MEMBERSHIP"))
            return;

        int viewStart = content.indexOf("VIEW");
        int logsStart = content.indexOf("LOGS");

        List<String> view = content.subList(viewStart + 1, logsStart);
        for (String element : view)
            node.updateMembershipView(element);

        List<String> events = content.subList(logsStart + 1, content.size());
        for (String event : events)
            node.addMembershipEvent(event);
    }
}
