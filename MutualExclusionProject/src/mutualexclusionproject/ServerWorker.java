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
    private final ArrayList<Entry>  HostTable;
    
    public ServerWorker (Socket c, ArrayList<Entry> h) throws IOException
    {
        this.clientSocket = c;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.HostTable = h;
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
                        if ((inputLine = in.readLine()) != null)
                        {
                            String parts[] = inputLine.split(",");
                            if (parts.length == 3)
                            {
                                this.register(parts[0], parts[1], Integer.parseInt(parts[2]));
                                this.HostTable.add(new Entry(parts[0],parts[1],Integer.parseInt(parts[2])));
                                out.println("ok");
                            }
                            else
                            {
                                out.println("nok");
                            }
                            break;
                        }
                        else
                        {
                            out.println("nok");
                        }   break;
                }
            }
            
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private synchronized void register (String name, String hostname, int port)
    {
        this.HostTable.add(new Entry(name, hostname, port));
    }
}