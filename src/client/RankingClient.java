package client;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
public class RankingClient extends Thread
{
    JTextPane jta;
    JScrollPane jspp;
    static String rank="";
    public RankingClient(JTextPane ja, JScrollPane o)
    {
        jta=ja;
        jspp=o;
        rank="";
    }
    public void run(String hh)
    {
        try{
                StringBuffer fin=new StringBuffer("          ");
                String str[]=hh.trim().split(":");
                String f="";int uu=0;
                for(int i=0;i<str.length;i++)
                {
                    f += (i+1)+": <"+str[i]+">\n";
                    if(uu==0 && str[i].indexOf(Main.ur.getName())!=-1)
                    {
                        uu=1;
                        rank = (i+1)+"     Score: "+str[i].substring(str[i].indexOf('>')+1);
                    }
                }
                final String h=f;
                SwingUtilities.invokeLater(new Runnable(){
                    public void run()
                    {
                        try{
                            jta.setText(h);
                            SwingUtilities.invokeLater(new Runnable(){
                                public void run(){
                                    jspp.getVerticalScrollBar().setValue(0);
                                }
                            });
                            try{
                                for(int i=0;i<3;i++)
                                {
                                    Main.mon[i].setText("Avail. Cash : "+Main.twoDForm.format(Main.ur.getCurrentMoney())+"     Rank: "+rank);
                                    Main.mon[i].repaint();
                                }
                            }catch(Exception mm){mm.printStackTrace();}
                        }catch(Exception e){}
                    }
                });
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
