package mutualexclusionproject;

/**
 * Class for keeping the details of each process in the host table
 * @author efi
 */
public class Entry {
    
    //name of the process
    private final String name;
    //host of the process
    private final String host;
    //port of the process
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
