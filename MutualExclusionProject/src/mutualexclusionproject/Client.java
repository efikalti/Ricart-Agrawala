package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author efi
 */
public class Client {

    private                     BufferedReader in;
    private final               String name;
    private final               String hostname;
    private final int           port;
    private final String        mainServer;
    private final int           mainPort;
    private static int          number;
    private ArrayList<Entry>    processes = null;
    private Thread              server;

    public Client(String name, String hostname, int port, String mainServer, int mainPort) throws IOException 
    {
        this.name = name;
        this.mainServer = mainServer;
        this.mainPort = mainPort;
        this.hostname = hostname;
        this.port = port;
        this.startServer();
        this.getOtherProcesses();
    }
    
    public final void startServer() throws IOException
    {
        server = new Thread(new ClientServer(this.port));
        server.start();
    }

    public void Run() throws IOException, InterruptedException {
        OUTER:
        while (true) {
            //Send request messages to every process
            for(Entry t: this.processes)
            {
                if(!t.getName().equals(this.name))
                {
                    
                    Socket process;
                    boolean check = false;
                    do
                    {
                        try
                        {
                            process = new Socket(t.getHost(),t.getPort());
                            in = new BufferedReader( new InputStreamReader(process.getInputStream()));
                            String str = in.readLine();
                            in.close();
                            check = Integer.parseInt(str) > number;
                        }
                        catch (IOException e)
                        {
                             System.out.println("Client busy will try again.");
                             check = false;
                        }
                    }while(!check);
                }
            }
            /**
             * Got permission from every process to enter critical section
             */
            CRITICAL_SECTION:
            {
                System.out.println("Process " + this.name + ", with number: " + number + ", is in the critical section.");
            
                try 
                {
                    TimeUnit.SECONDS.sleep(2);
                }
                catch (InterruptedException e) {
                }
                
                System.out.println("Process " + this.name + ", with number: " + number + ", is leaving the critical section.");
            }
            //update processed table
            this.getOtherProcesses();
        }
    }

    /**
     * Connect to the main server and request the name table with all the other
     * processes. If it is the first time you connect to the main server,
     * register to the name table.
     *
     * @throws IOException
     */
    private void getOtherProcesses() throws IOException 
    {
        Socket connect = new Socket(mainServer, mainPort);
        String str;
        PrintWriter out = new PrintWriter(connect.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        if (processes == null) 
        {
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
        while (!(str = in.readLine()).equals("ok"))
        {
            String parts[] = str.split(",");
            processes.add(new Entry(parts[0], parts[1], Integer.parseInt(parts[2])));
        }
        //get number
        this.number = Integer.parseInt(in.readLine());
        out.println("quit");
        out.close();
        in.close();
    }

    public void print(String str) 
    {
        System.out.println("Main Server says: " + str);
    }
    
    public static synchronized int getNumber()
    {
        return number;
    }
}
