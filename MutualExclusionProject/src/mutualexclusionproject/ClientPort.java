package mutualexclusionproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import static mutualexclusionproject.Client.getNumber;

/**
 *
 * @author efi
 */
public class ClientPort implements Runnable{

    private final PrintWriter       out;
    private final Socket            clientSocket;
    
    public ClientPort(Socket c) throws IOException
    {
        this.clientSocket = c;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }
    
    @Override
    public void run() {
        out.println(getNumber());
        out.flush();
        out.close();
    }
    
}
