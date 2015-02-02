package mutualexclusionproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author efi
 */
public class ClientServer implements Runnable{
    
    private Socket                  clientSocket;
    private PrintWriter             out;
    private final ServerSocket      server;
    private final ExecutorService   threadPool;
    
    public ClientServer (int port) throws IOException
    {
        this.server = new ServerSocket(port);
        this.threadPool = Executors.newFixedThreadPool(2);
    }

    @Override
    public void run() {
        try 
        {
            while(true)
            {
                clientSocket = server.accept();
                this.threadPool.execute(new ClientPort(clientSocket));
            }
        }  
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
