/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutualexclusionproject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author efi
 */
public class MainServer implements Runnable{
    
    private ServerSocket        server        = null;
    private Thread              currentThread = null;
    private boolean             isStopped     = false;
    private ExecutorService     threadPool    = Executors.newFixedThreadPool(3);
    
    public MainServer(int port)
    {
        try {
            this.server = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port "+ port, e);
        }
    }

    @Override
    public void run() {
        this.currentThread = Thread.currentThread();
        while(!isStopped)
        {
            Socket clientSocket = null;
            try {
                clientSocket = server.accept();
                this.threadPool.execute(new ServerWorker(clientSocket));
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
    
}
