package server;
import java.io.*;
import java.net.*;
import java.util.*;
import user.*;
import java.text.*;
import javax.swing.*;

public class RankingServer extends Thread
{    
    private long SECONDS = 30000;      
    List<Company> com;
    Server server;
    int close;
    
    public RankingServer(String name,List<Company> c,Server s) throws IOException 
    {
        super(name);
        com=c;
        server=s;
    }
    
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    public void run() {
        close=0;
        while (!interrupted())
        {
            if(close==1)
                break;
            try 
            {
                String dString="rank::";
                sortUsers();
                for(int i=RegList.userList.size()-1;i>=0;i--)
                {
                    User user=RegList.userList.get(i);
                    if(!user.isBanned())
                        dString+=user.getName()+"->"+twoDForm.format(getScore(user))+":";
                }
                //final String sss=dString;
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        String jj="";
                        for(int i=RegList.userList.size()-1;i>=0;i--)
                        {
                            User user=RegList.userList.get(i);
                            jj+=(RegList.userList.size()-i)+" )  "+user.getName()+"->"+twoDForm.format(getScore(user))+"\n";
                        }
                        StockMart.ranks.setText(jj);
                    }
                });
                server.sendMulti(dString);
                try 
                {
                    sleep(SECONDS);
                }catch(InterruptedException e){}
            }catch(Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
    }
    
    void sortUsers()
    {
        synchronized(RegList.userList)
        {
            synchronized(com)
            {
                for(int i=0;i<RegList.userList.size();i++)
                {
                    User key=RegList.userList.get(i);
                    int j;
                    for(j=i-1;j>=0;j--)
                    {
                        if((getScore(RegList.userList.get(j))>getScore(key) && !RegList.userList.get(j).isBanned()) || key.isBanned())
                            RegList.userList.set(j+1,RegList.userList.get(j));
                        else
                            break;
                    }
                    RegList.userList.set(j+1,key);
                }
            }
        }
    }
    
    double getScore(User u)
    {
        synchronized(u)
        {
            double mon=Double.valueOf(twoDForm.format(u.getCurrentMoney()));
            double score=mon;
            List<Shares> list=u.getCurrentShares();
            for(int i=0;i<list.size();i++)
            {
                String name=list.get(i).company;
                for(int j=0;j<com.size();j++)
                {
                    if(com.get(j).name.equalsIgnoreCase(name))
                    {
                        score=score + Double.valueOf(twoDForm.format(com.get(j).sharevalue.get(com.get(j).sharevalue.size()-1)*list.get(i).qty));
                        break;
                    }
                }
            }
            return Double.valueOf(twoDForm.format(score));
        }
    }
}