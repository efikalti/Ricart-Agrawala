package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Class that handles the requests of every process calling the main server.
 * For every process requests a new thread of this class is created to handle it.
 * @author efi
 */
public final class MainServerWorker implements Runnable{
    
    private final Socket            ProcessSocket;
    private final PrintWriter       out;
    private final BufferedReader    in;
    private       ArrayList<Entry>  HostTable;
    
    /**
     * Save the process that did the request.
     * Establish communication connection with the process.
     * @param c
     * @throws IOException 
     */
    public MainServerWorker (Socket c) throws IOException
    {
        this.ProcessSocket = c;
        out = new PrintWriter(ProcessSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(ProcessSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String inputLine;
            
            //There are the programmed commands a process may ask from the server
            //These are:
            OUTER:
            while ((inputLine = in.readLine()) != null) {
                print(inputLine);
                switch (inputLine) {
                    //To end the conversation and terminate the connection
                    case "quit":
                        break OUTER;
                    //If the process is new and needs to register in the host table
                    case "register":
                        /*process sends its details(name,host,port) and the serverworker adds them to the 
                        main server.
                        This is achieved by calling the local method register that will contact the main server.
                        */
                        if (!(inputLine = in.readLine()).equals("ok"))
                        {
                            String parts[] = inputLine.split(",");
                            if (parts.length == 3)
                            {
                                this.register(parts[0], parts[1], Integer.parseInt(parts[2]));
                                out.println("ok");
                            }
                            else
                            {
                                out.println("nok");
                            }
                        }
                        else
                        {
                            out.println("nok");
                        }
                        out.flush();
                        break;
                    //Process asks for the host table in order to send requests to the other processes.
                    case "send HostTable":
                        //get the host table from the main server
                        this.HostTable = MainServer.getInstance().getProcesses();
                        //send each process line by line
                        for (Entry t : this.HostTable)
                        {
                            out.println(t.toString());
                            out.flush();
                        }
                        out.println("ok");
                        out.flush();
                        out.println(MainServer.getInstance().getNumber());
                        break;
                    case "unregister":
                        inputLine = in.readLine();
                        String parts[] = inputLine.split(",");
                        if (parts.length == 3)
                        {
                            this.unregister(parts[0], parts[1], Integer.parseInt(parts[2]));
                            out.println("ok");
                        }
                        else
                        {
                            out.println("nok");
                        }
                        break;
                }
            }
            //close communication connections
            out.close();
            in.close();
            //print out host table for debugging reasons
          //  System.out.println("HostTable: ");
          //  for (Entry t : this.HostTable)
         //   {
         //      System.out.println(t.toString());
          //  }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Call to the main server instance to register a new process
     * @param name
     * @param hostname
     * @param port 
     */
    private synchronized void register (String name, String hostname, int port)
    {
        MainServer.getInstance().register(new Entry(name,hostname,port));
    }
    
    /**
     * Call to the main server instance to unregister this process
     * @param name
     * @param hostname
     * @param port 
     */
    private void unregister (String name, String hostname, int port)
    {
        MainServer.getInstance().unregister(new Entry(name,hostname,port));
    }
    
    /**
     * Print the message of the client for debugging reasons
     * @param str 
     */
    public void print(String str)
    {
        System.out.println("Client says: " + str);
    }
}
