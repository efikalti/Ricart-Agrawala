package mutualexclusionproject;

import java.io.IOException;

/**
 *
 * @author efi
 */
public class Main {
    
    private static final String mainServer = "localhost";
    private static final int mainPort = 10000;
    
    public static void main(String []args) throws IOException, InterruptedException
    {
        String message = "Wrong input.Correct usage of the program: java -jar MutualExclusion-1.0.jar <Server/Client> <name for Client> <hostname for Client> <port for Client>";
        if ((args.length < 1 && args[0].equals("Server")) || (args.length < 3 && args[0].equals("Client")))
        {
            System.out.println(message);
        }
        else
        {
            switch (args[0]) {
                case "Server":
                    MainServer.getInstance().Initialize(mainPort);
                    break;
                case "Client":
                    String name = args[1];
                    String hostname = args[2];
                    int port = Integer.parseInt(args[3]);
                    Client c = new Client (name, hostname, port, mainServer, mainPort);
                    c.Run();
                    break;
                default:
                    System.out.println("Unknown first argument." + message);
                    break;
            }
        }
    }
}
