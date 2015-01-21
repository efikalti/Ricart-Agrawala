package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author efi
 */
public final class ServerWorker implements Runnable{
    
    private final Socket            clientSocket;
    private final PrintWriter       out;
    private final BufferedReader    in;
    private       ArrayList<Entry>  HostTable;
    private       int               number;
    
    public ServerWorker (Socket c) throws IOException
    {
        this.clientSocket = c;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String inputLine;
            
            OUTER:
            while ((inputLine = in.readLine()) != null) {
                switch (inputLine) {
                    case "quit":
                        break OUTER;
                    case "register":
                        if (!(inputLine = in.readLine()).equals("ok"))
                        {
                            print(inputLine);
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
                    case "send HostTable":
                        this.HostTable = MainServer.getInstance().getProcesses();
                        for (Entry t : this.HostTable)
                        {
                            out.println(t.toString());
                            out.flush();
                        }
                        out.println("ok");
                        out.flush();
                        out.println(MainServer.getInstance().getNumber());
                        break;
                }
            }
            for (Entry t : this.HostTable)
            {
                System.out.println(t.toString());
            }
            
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private synchronized void register (String name, String hostname, int port)
    {
        MainServer.getInstance().register(new Entry(name,hostname,port));
    }
    
    public void print(String str)
    {
        System.out.println("Client says: " + str);
    }
}
