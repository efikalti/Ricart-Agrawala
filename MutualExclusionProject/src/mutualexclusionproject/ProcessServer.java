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
public class ProcessServer implements Runnable{
    
    private Socket                  clientSocket;
    private PrintWriter             out;
    private final ServerSocket      server;
    private final ExecutorService   threadPool;
    
    public ProcessServer (int port) throws IOException
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
                this.threadPool.execute(new ProcessPort(clientSocket));
            }
        }  
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
