package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author efi
 */
public final class ServerWorker implements Runnable{
    
    private final Socket            clientSocket;
    private final PrintWriter       out;
    private final BufferedReader    in;
    
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
            while ((inputLine = in.readLine()) != null) 
            {
                if (inputLine.equals("quit"))
                {
                    break;
                }
                else
                {
                    
                }
            }
            
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
