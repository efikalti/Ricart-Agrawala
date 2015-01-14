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
    
    private static final String mainServer = "localhost";
    private static final int mainPort = 10000;
    
    public static void main(String []args) throws IOException
    {
        String message = "Correct usage of the program: java -jar MutualExclusion-1.0.jar <Server/Client> <hostname for Client> <port>";
        if (args.length < 3)
        {
            System.out.println("Wrong input." + message);
        }
        else
        {
            String hostname = args[1];
            int port = Integer.parseInt(args[2]);
            switch (args[0]) {
                case "MainServer":
                    MainServer s = new MainServer (mainPort);
                    s.run();
                    break;
                case "Client":
                    Client c = new Client (hostname, port, mainServer, mainPort);
                    c.Run();
                    break;
                default:
                    System.out.println("Unknown first argument." + message);
                    break;
            }
        }
    }
}
