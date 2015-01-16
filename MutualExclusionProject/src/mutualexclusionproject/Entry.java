/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutualexclusionproject;

/**
 *
 * @author efi
 */
public class Entry {
    
    private final String name;
    private final String host;
    private final int port;

    public Entry(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
    
    @Override
    public String toString()
    {
        return this.name + "," + this.host + "," + this.port;
    }
    
}
