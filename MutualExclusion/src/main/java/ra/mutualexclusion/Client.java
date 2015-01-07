/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ra.mutualexclusion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author efi
 */
public class Client {
    
    private final Socket client;
    private final PrintWriter out;
    private final BufferedReader in;
    BufferedReader stdIn;
    
    public Client (String hostname, int port) throws IOException
    {
        client = new Socket(hostname, port);
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader( new InputStreamReader(client.getInputStream()));
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
    }
}
