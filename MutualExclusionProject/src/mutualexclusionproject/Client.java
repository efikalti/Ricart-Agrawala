/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mutualexclusionproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author efi
 */
public class Client {
    
    private final Socket            server;
    private final PrintWriter       out;
    private final BufferedReader    in;
    private final BufferedReader    stdIn;
    private final String            mainServer;
    private final int               mainPort;
    private ArrayList<Entry>        processes = null;
    private final String            hostname;
    private final int               port;
    
    public Client (String hostname, int port, String mainServer, int mainPort) throws IOException
    {
        this.mainServer = mainServer;
        this.mainPort = mainPort;
        this.hostname = hostname;
        this.port = port;
        
        server = new Socket(hostname, port);
        out = new PrintWriter(server.getOutputStream(), true);
        in = new BufferedReader( new InputStreamReader(server.getInputStream()));
        stdIn = new BufferedReader( new InputStreamReader(System.in));
    }
    
    public void Run() throws IOException
    {
        String userInput;
        while ((userInput = stdIn.readLine()) != null)
        {
            out.println(userInput);
            if(userInput.equals("."))
            {
                break;
            }
            else
            {
                System.out.println("echo: " + in.readLine());
            }
        }
        if (server != null)
        {
            server.close();
        }
    }
    
    private void getOtherProcesses() throws IOException
    {
        Socket connect = new Socket(mainServer, mainPort);
        {
            ArrayList<String> rawInput = new ArrayList<>();
            String str;
            BufferedReader input = new BufferedReader( new InputStreamReader(connect.getInputStream()));
            if (processes == null)
            {
                PrintWriter output = new PrintWriter(connect.getOutputStream(), true);
                output.println("register");
                output.println(this.hostname + "," + this.port);
            }
            while ((str = input.readLine()) != null)
            {
                if(str.equals("quit"))
                {
                    break;
                }
                else
                {
                    rawInput.add(str);
                }
            }
            processes = new ArrayList<>();
            for (String s : rawInput)
            {
                String parts[] = s.split(",");
                processes.add(new Entry(parts[0], parts[1], Integer.parseInt(parts[2])));
            }
        }
    }
}
