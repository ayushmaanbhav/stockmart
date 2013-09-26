package client;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
public class RankingClient extends Thread
{
    JTextPane jta;
    public RankingClient(JTextPane ja)
    {
        jta=ja;
    }
    public void run(String hh)
    {
        try{
                StringBuffer fin=new StringBuffer("          ");
                String str[]=hh.trim().split(":");
                String f="";
                for(int i=0;i<str.length;i++)
                {
                    f += (i+1)+": <"+str[i]+">\n";
                }
                final String h=f;
                SwingUtilities.invokeLater(new Runnable(){
                    public void run()
                    {
                        jta.setText(h);
                    }
                });
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
