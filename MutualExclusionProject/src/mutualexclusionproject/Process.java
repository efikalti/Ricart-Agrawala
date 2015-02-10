package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Class modeling the process in the algorithm that tries to enter the critical
 * section
 *
 * @author efi
 */
public class Process {

    private BufferedReader in;
    private final String name;
    private final String hostname;
    private final int port;
    private final String mainServer;
    private final int mainPort;
    private static int number;
    private ArrayList<Entry> processes = null;
    private Thread server;

    public Process(String name, String hostname, int port, String mainServer, int mainPort) throws IOException {
        this.name = name;
        this.mainServer = mainServer;
        this.mainPort = mainPort;
        this.hostname = hostname;
        this.port = port;
        this.startServer();
        this.getOtherProcesses();
    }

    /**
     * creates a ProcessServer thread to constantly listen to this process
     * assigned port and answer any request from other processes.
     *
     * @throws IOException
     */
    public final void startServer() throws IOException {
        server = new Thread(new ProcessServer(this.port));
        server.start();
    }

    /**
     * The critical and non-critical implementation
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void Run() throws IOException, InterruptedException {
        while (number < 50) {
            PRE_PROTOCOL:

            //Send request messages to every process
            for (Entry t : this.processes) {
                int tries = 0;
                //exclude this process from the loop
                if (!t.getName().equals(this.name)) {
                    //establish connection with the process
                    Socket process;
                    boolean check = false;
                    do {
                        try {
                            process = new Socket(t.getHost(), t.getPort());
                            in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                            //process sends its' number
                            String str = in.readLine();
                            in.close();
                            //check if the number is greater than yours
                            try {
                                check = Integer.parseInt(str) > number;
                            }
                            catch (NumberFormatException nfe) {
                                //refresh the process table and start again
                                this.getOtherProcesses();
                                break PRE_PROTOCOL;
                            }
                            
                        }
                        catch (IOException e) {
                            System.out.println("Process busy will try " + (3 - tries) + " times again.");
                            check = false;
                            tries++;
                            //ask from server to remove the non responding process
                            if (tries == 3) {
                                this.unregister(t.getName(), t.getHost(), t.getPort());
                                //refresh the process table and start again
                                this.getOtherProcesses();
                                break PRE_PROTOCOL;
                            }
                        }
                        //contact the same process for as long as it has a greater number than you
                    }
                    while (!check);
                }
            }
            /**
             * Got permission from every process to enter critical section
             */
            CRITICAL_SECTION:
            {
                System.out.println("Process " + this.name + ", with number: " + number + ", entered the critical section.");

                try {
                    TimeUnit.SECONDS.sleep(2);
                }
                catch (InterruptedException e) {
                }

                System.out.println("Process " + this.name + ", with number: " + number + ", is leaving the critical section.");
            }
            POST_PROTOCOL:
            //update process table and get a new number 
            this.getOtherProcesses();
        }
        //unregister from the host table
        this.unregister(this.name, this.hostname, this.port);
        server.interrupt();
    }

    /**
     * Connect to the main server and request the name table with all the other
     * processes. If it is the first time you connect to the main server,
     * register to the host table.
     *
     * @throws IOException
     */
    private void getOtherProcesses() throws IOException {
        //create connection and establish communication
        Socket connect = new Socket(mainServer, mainPort);
        String str;
        PrintWriter out = new PrintWriter(connect.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        if (processes == null) {//resister if the processes table is null,it means you have never connected 
            //to the main server before hence you are not registered.
            do {
                out.println("register");
                out.flush();
                out.println(this.name + "," + this.hostname + "," + this.port);
                out.flush();
                out.println("ok");
                out.flush();
                str = in.readLine();
                print(str);
            }
            while (str.equals("nok"));
        }
        //Get HostTable
        out.println("send HostTable");
        processes = new ArrayList<>();
        while (!(str = in.readLine()).equals("ok")) {
            String parts[] = str.split(",");
            processes.add(new Entry(parts[0], parts[1], Integer.parseInt(parts[2])));
        }
        //get assigned a number by the main server, according to which you will 
        //enter the critical section
        this.number = Integer.parseInt(in.readLine());
        out.println("quit");
        out.close();
        in.close();
    }

    /**
     * Print message from the server,for debugging reasons
     *
     * @param str
     */
    public void print(String str) {
        System.out.println("Main Server says: " + str);
    }

    /**
     * Return the number of this process
     *
     * @return
     */
    public static synchronized int getNumber() {
        return number;
    }

    /**
     * Unregister the process with these data.
     *
     * @param name
     * @param hostname
     * @param port
     * @throws IOException
     */
    public void unregister(String name, String hostname, int port) throws IOException {
        try (
                //create connection and establish communication
                Socket connect = new Socket(mainServer, mainPort)) {
            String str;
            PrintWriter out = new PrintWriter(connect.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            do {
                out.println("unregister");
                out.flush();
                out.println(name + "," + hostname + "," + port);
                out.flush();
            }
            while (!(in.readLine().equals("ok")));
            out.println("quit");
            out.flush();
            out.close();
            in.close();
        }
    }
}
