package server;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer extends Thread
{    
    Server server;
    String chaat;
    
    public ChatServer(Server s) throws IOException 
    {
        server=s;
        chaat="";
    }

    public void run() {
        try 
        {
            while(true)
            {
                String toSend="chat::";
                if(StockMart.newsfeed.equals(""))
                    toSend+="-1";
                else
                    toSend+=StockMart.newsfeed;
                toSend+=";";
                if(chaat.equals(""))
                    toSend+="-1";
                else
                    toSend+=new String(chaat);
                chaat="";
                server.sendMulti(toSend);
                try
                {
                    sleep(15000);
                }catch(InterruptedException e){}
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
