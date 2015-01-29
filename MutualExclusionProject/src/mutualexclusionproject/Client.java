package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author efi
 */
public class Client {

    private final               ServerSocket server;
    private                     Socket client;
    private                     PrintWriter out;
    private                     BufferedReader in;
    private final               BufferedReader stdIn;
    private final               String name;
    private final               String hostname;
    private final int           port;
    private final String        mainServer;
    private final int           mainPort;
    private int                 number;
    private ArrayList<Entry>    processes = null;

    public Client(String name, String hostname, int port, String mainServer, int mainPort) throws IOException {
        this.name = name;
        this.mainServer = mainServer;
        this.mainPort = mainPort;
        this.hostname = hostname;
        this.port = port;

        server = new ServerSocket(port);
        stdIn = new BufferedReader(new InputStreamReader(System.in));
        this.getOtherProcesses();
    }

    public void Run() throws IOException, InterruptedException {
        System.out.println("Client running...");
        String userInput;
        OUTER:
        while (true) {
            //Send request messages to every process
            for(Entry t: this.processes)
            {
                if(!t.getName().equals(this.name))
                {
                    Socket process = null;
                    do
                    {
                        server.setSoTimeout(3000);
                        try
                        {
                            this.client = server.accept();
                        }
                        catch (SocketTimeoutException e)
                        {
                            
                        }
                        if (this.client != null)
                        {
                            out = new PrintWriter(client.getOutputStream(), true);
                            in = new BufferedReader( new InputStreamReader(client.getInputStream()));
                            String str = in.readLine();
                            String parts[] = str.split(",");
                            if(this.number > Integer.parseInt(parts[2]))
                            {
                                out.println("ok");
                            }
                            else
                            {
                                out.println("nok");
                            }
                        }
                        
                        System.out.println("Finished listening...now will try to connect with the client again.");
                        
                        try
                        {
                            process = new Socket(t.getHost(),t.getPort());
                        }
                        catch (IOException e)
                        {
                             System.out.println("Client busy will try again.");
                        }
                    }while(process == null);
                    out = new PrintWriter(process.getOutputStream(), true);
                    in = new BufferedReader( new InputStreamReader(process.getInputStream()));
                    out.println("Request access," + this.name + "," + this.number);
                    String str;
                    if(!(str = in.readLine()).equals("ok"))
                    {
                        process.wait();
                    }
                }
            }
            /**
             * Got permission from every process to enter critical section
             */
            CRITICAL_SECTION:
            {
                System.out.println("Process " + this.name + ", with number: " + this.number + ", is in the critical section.");
            
                try 
                {
                    TimeUnit.SECONDS.sleep(2);
                }
                catch (InterruptedException e) {
                }
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
        PrintWriter output = new PrintWriter(connect.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        if (processes == null) 
        {
            do {
                output.println("register");
                output.flush();
                output.println(this.name + "," + this.hostname + "," + this.port);
                output.flush();
                output.println("ok");
                output.flush();
                str = input.readLine();
                print(str);
            }
            while (str.equals("nok"));
        }
        //Get HostTable
        output.println("send HostTable");
        processes = new ArrayList<>();
        while (!(str = input.readLine()).equals("ok"))
        {
            String parts[] = str.split(",");
            processes.add(new Entry(parts[0], parts[1], Integer.parseInt(parts[2])));
        }
        //get number
        this.number = Integer.parseInt(input.readLine());
        output.println("quit");

        for (Entry t : this.processes) 
        {
            System.out.println(t.toString());
        }
    }

    public void print(String str) 
    {
        System.out.println("Main Server says: " + str);
    }
}
