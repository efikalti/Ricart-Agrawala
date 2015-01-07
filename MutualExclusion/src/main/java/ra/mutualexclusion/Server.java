package ra.mutualexclusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author efi
 */
public class Server {
    
    private final ServerSocket server;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    
    public Server (String hostname, int port) throws IOException
    {
        server = new ServerSocket(port);
    }
    
    public void Run() throws IOException 
    {
        String inputLine;
        clientSocket = server.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        while ((inputLine = in.readLine()) != null) 
        {
            if (inputLine.equals("Bye.")) 
            {
                break;
            } 
            else
            {
                out.println(inputLine);
            }
        }
    }
}
