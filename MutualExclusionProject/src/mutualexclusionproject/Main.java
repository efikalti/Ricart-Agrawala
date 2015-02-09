package mutualexclusionproject;

import java.io.IOException;

/**
 *
 * @author efi
 */
public class Main {
    
    //connection data for the main server
    private static final String mainServer = "localhost";
    private static final int mainPort = 10000;
    
    public static void main(String []args) throws IOException, InterruptedException
    {
        //error message
        String message = "Wrong input.Correct usage of the program: java -jar MutualExclusion-1.0.jar <Server/Process> <name for Process> <hostname for Process> <port for Process>";
        //check arguments given at start
        if ((args.length < 1 && args[0].equals("Server")) || (args.length < 3 && args[0].equals("Process")))
        {
            System.out.println(message);
        }
        else
        {
            switch (args[0]) {
                //initiate main server with the main server data
                case "Server":
                    MainServer.getInstance().Initialize(mainPort);
                    break;
                //initiate a process with the data given in the arguments
                case "Process":
                    String name = args[1];
                    String hostname = args[2];
                    int port = Integer.parseInt(args[3]);
                    Process c = new Process (name, hostname, port, mainServer, mainPort);
                    c.Run();
                    break;
                default:
                    System.out.println("Unknown first argument." + message);
                    break;
            }
        }
    }
}
