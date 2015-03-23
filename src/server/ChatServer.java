package server;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer extends Thread
{    
    Server server;
    String chaat;
    BufferedWriter bw;
    public ChatServer(Server s) throws IOException 
    {
        server=s;
        chaat="";
        try{
            bw = new BufferedWriter(new FileWriter("appdata/chat.txt"));
        }catch(Exception mm){}
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
                try{
                    bw.write(new Date().toString()+":"+toSend+"\n");
                    bw.flush();
                }catch(Exception mm){mm.printStackTrace();}                
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
