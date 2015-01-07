/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ra.mutualexclusion;

import java.io.IOException;

/**
 *
 * @author efi
 */
public class Main {
    
    public static void main(String []args) throws IOException
    {
        String message = "Correct usage of the program: java -jar MutualExclusion-1.0.jar <Server/Client> <hostname> <port>";
        
        if (args.length < 3)
        {
            System.out.println("Wrong input." + message);
        }
        else
        {
            String hostname = args[1];
            int port = Integer.parseInt(args[2]);
            if (args[0].equals("Server"))
            {
                Server s = new Server (hostname, port);
            }
            else if (args[0].equals("Client"))
            {
                Client c = new Client (hostname, port);
                c.Run();
            }
            else
            {
                System.out.println("Unknown first argument." + message);
            }
        }
        System.out.println("Hello World!");
    }
    
}
