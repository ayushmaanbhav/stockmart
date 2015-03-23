package server;
import java.io.*;
import java.net.*;
import java.util.*;
import java.text.*;

public class BroadcastServer extends Thread
{    
    private long SECONDS = 30000;   
    List<Company> com;
    int close;
    Server server;
    
    public BroadcastServer(String name,List<Company> c,Server s) throws IOException 
    {
        super(name);
        com=c;
        server=s;
        setPriority(MAX_PRIORITY);
    }

    static DecimalFormat twoDForm = new DecimalFormat("#.##");
    public void run() {
        close=0;
        while (com.size()>0)
        {
            if(close==1)
                break;
            try 
            {
                int hrs=((int)(StockMart.totTime*60-(new Date().getTime()-StockMart.startDate.getTime())/1000))/3600;
                int mins=((((int)(StockMart.totTime*60-(new Date().getTime()-StockMart.startDate.getTime())/1000))/60)%60);
                String dString="broadcast::"+hrs+":"+mins+";"+twoDForm.format(ShareMarket.sensex)+"=";
                for(int i=0;i<com.size();i++)
                {
                    Company c=com.get(i);
                    if(!c.bankrupt)
                        dString+=Integer.toString(i)+":"+c.name+":"+String.valueOf(c.sharevalue.get(c.sharevalue.size()-1))+":"+String.valueOf(c.sharevalue.get(0))+":"+String.valueOf(c.getHighest())+":"+String.valueOf(c.getLowest())+";";
                }
                server.sendMulti(dString);
                try 
                {
                    sleep(SECONDS);
                }catch(InterruptedException e){}
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
