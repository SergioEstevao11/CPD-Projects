package org.feup.cpd.store;

import java.net.InetAddress;
import java.net.UnknownHostException;

public record NodeAccessPoint(InetAddress address, int port) {

    public static NodeAccessPoint getInstance(String host, String port) {
        try {
            InetAddress address = InetAddress.getByName(host);
            int parsedPort = Integer.parseUnsignedInt(port);

            return new NodeAccessPoint(address, parsedPort);
        } catch (UnknownHostException | NumberFormatException e) {
            e.printStackTrace();
            System.exit(1);
        }

        throw new IllegalArgumentException("Unable to build instance for object of type NodeAccessPoint");
    }

    @Override
    public String toString() {
        return address.getHostAddress() + ':' + port;
    }
}
