package org.feup.cpd.store.message;

import org.feup.cpd.store.AccessPoint;

public class ReturnMessage extends Message{
    private final AccessPoint accessPoint;
    private final String answer;
    private final String task;

    public ReturnMessage(AccessPoint accessPoint, String task, String answer) {
        super("RETURN");
        this.accessPoint = accessPoint;
        this.answer = answer;
        this.task = task;

        body.append(accessPoint).append(' ')
                .append(task).append(' ')
                .append(answer);

    }

    @Override
    public String getContent() {
        return body.toString();
    }
}
