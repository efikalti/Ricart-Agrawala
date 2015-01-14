/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutualexclusionproject;

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
            switch (args[0]) {
                case "Server":
                    MainServer s = new MainServer (port);
                    s.run();
                    break;
                case "Client":
                    Client c = new Client (hostname, port);
                    c.Run();
                    break;
                default:
                    System.out.println("Unknown first argument." + message);
                    break;
            }
        }
    }
    
}
