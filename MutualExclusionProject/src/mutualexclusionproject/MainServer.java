package mutualexclusionproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author efi
 */
public class MainServer {
    
    private ServerSocket        server        = null;
    private boolean             isStopped     = false;
    private ExecutorService     threadPool    = null;
    private ArrayList<Entry>    HostTable     = null;
    private int                 number        = 0;
    
    private MainServer() {
        this.threadPool = Executors.newFixedThreadPool(3);
    }
    
    public static MainServer getInstance() {
        return MainServerHolder.INSTANCE;
    }
    
    private static class MainServerHolder {

        private static final MainServer INSTANCE = new MainServer();
    }
    
    public void Initialize(int port)
    {
        try {
            this.server = new ServerSocket(port);
            this.HostTable = new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+ port, e);
        }
        this.run();
    }
    
    public void run() {
        System.out.println("Main Server running...");
        while(!isStopped)
        {
            Socket clientSocket;
            try {
                clientSocket = server.accept();
                this.threadPool.execute(new MainServerWorker(clientSocket));
            }
            catch (IOException ex) 
            {
                if(isStopped) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException("Error accepting client connection", ex);
            }
        }
    }
    
    public void stop(){
        this.isStopped = true;
        try {
            this.threadPool.shutdown();
            this.server.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
    public synchronized ArrayList<Entry> getProcesses ()
    {
        return this.HostTable;
    }
    
    public synchronized int getNumber()
    {
        return ++this.number;
    }
    
    public synchronized void register (Entry e)
    {
        this.HostTable.add(e);
    }
}
