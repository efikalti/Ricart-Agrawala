package mutualexclusionproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author efi
 */
public class ClientServer implements Runnable{
    
    private Socket                  clientSocket;
    private PrintWriter             out;
    private int                     number;
    private final ServerSocket      server;
    
    public ClientServer (int port) throws IOException
    {
        this.server = new ServerSocket(port);
    }

    @Override
    public void run() {
        try 
        {
            while(true)
            {
                clientSocket = server.accept();
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println(this.number);
                out.flush();
            }
        }
            
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void print(String str)
    {
        System.out.println("Client says: " + str);
    }
}
