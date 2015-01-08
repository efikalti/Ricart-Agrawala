/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ra.mutualexclusion;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author efi
 */
public class MainServer implements Runnable{
    
    private ServerSocket        server        = null;
    private Thread              currentThread = null;
    private boolean             isStopped     = false;
    
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
                ServerWorker s = new ServerWorker(clientSocket);
                new Thread( new ServerWorker(clientSocket)).start();
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
            this.server.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
}
