package mutualexclusionproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main server keeping track of every running process and answering to requests
 * from processes.
 * Works for 3 processes.
 * Singleton class.
 * @author efi
 */
public class MainServer {
    
    private ServerSocket        server        = null;
    private boolean             isStopped     = false;
    private ExecutorService     threadPool    = null;
    private ArrayList<Entry>    HostTable     = null;
    private int                 number        = 0;
    private final int           numProcesses  = 3;
    
    private MainServer() {
        this.threadPool = Executors.newFixedThreadPool(numProcesses);
    }
    
    public static MainServer getInstance() {
        return MainServerHolder.INSTANCE;
    }
    
    private static class MainServerHolder {

        private static final MainServer INSTANCE = new MainServer();
    }
    
    /**
     * Initialize the server and bind it to the specified port.
     * @param port 
     */
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
    
    /**
     * Accept incoming requests to the port, create a thread of MainServerWorker 
     * to handle any incoming request and continue listening.
     */
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
    
    /**
     * Call to retrieve the host table safely.
     * @return the host table containing the entry of every process running at the time.
     */
    public synchronized ArrayList<Entry> getProcesses ()
    {
        return this.HostTable;
    }
    
    /**
     * Call to assign a number to every process.
     * @return an increment of the number starting from 1.Different for each process.
     */
    public synchronized int getNumber()
    {
        return ++this.number;
    }
    
    /**
     * Call to add a new process in the host table.This is the method every new process
     * calls before the pre-protocol.
     * @param e 
     */
    public synchronized void register (Entry e)
    {
        this.HostTable.add(e);
    }
    
    /**
     * Remove the specified entry from the host table
     * @param e 
     */
    public synchronized void unregister (Entry e)
    {
        this.HostTable.remove(e);
    }
}
