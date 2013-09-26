package server;
import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
class ShareMarket
{
    volatile List<Company> companies;
    Server server;
    BroadcastServer bserver;
    RankingServer rserver;
    static double sensex;
    Thread calculations;
    int SLEEP=30000;
    DecimalFormat twoDForm = new DecimalFormat("#.##");
     
    void initialise()
    {
        companies=new ArrayList<Company>();
        RegList.loadList();
        loadCompanies();
    }
    void startApp()
    {
        try{
            server=new Server(companies);
        }catch(Exception m)
        {
            m.printStackTrace();
        }
        server.startServer();
        try{
            bserver=new BroadcastServer("Share Market Broadcast",companies,server);
        }catch(Exception mm)
        {
            mm.printStackTrace();
        }
        try{
            rserver=new RankingServer("Share Market Ranks Broadcast",companies,server);
        }catch(Exception mm)
        {
            mm.printStackTrace();
        }
        rserver.start();
    }
    void start()
    {
        bserver.start();
        calculations=new Thread(){
            public void run()
            {
                while(!interrupted())
                {
                    synchronized(companies)
                    {
                        double d=0.0;
                        double e=0.0;
                        double f=0.0;
                        for(int j=0;j<companies.size();j++)
                        {
                            companies.get(j).totalshares -= companies.get(j).sharessold;
                        }
                        for(int j=0;j<companies.size();j++)
                        {
                            d += Double.valueOf(twoDForm.format(companies.get(j).sharevalue.get(companies.get(j).sharevalue.size()-1)))*companies.get(j).sharessold;
                            e += Double.valueOf(twoDForm.format(companies.get(j).sharevalue.get(companies.get(j).sharevalue.size()-1)))*companies.get(j).totalshares;
                            f += Double.valueOf(twoDForm.format(companies.get(j).sharevalue.get(0)))*companies.get(j).totalshares;
                        }
                        double d2=d/companies.size();
                        d2=Double.valueOf(twoDForm.format(d2));
                        for(int i=0;i<companies.size();i++)
                        {
                            Company com=companies.get(i);
                            com.perchange = Double.valueOf(twoDForm.format(companies.get(i).sharevalue.get(companies.get(i).sharevalue.size()-1)))*companies.get(i).sharessold;
                            com.perchange = com.perchange - d2;
                            if(d==0.0)
                                com.perchange=0.0;
                            else
                                com.perchange = (com.perchange)/d;
                            double newvalue = Double.valueOf(twoDForm.format((com.perchange/100+1)*Double.valueOf(twoDForm.format(companies.get(i).sharevalue.get(companies.get(i).sharevalue.size()-1)))));
                            if(newvalue>0.0)
                                com.sharevalue.add(newvalue);
                            else
                            {
                                //bankrupt
                            }
                            com.tsharessold+=com.sharessold;
                            com.sharessold=0;
                        }
                        sensex = Double.valueOf(twoDForm.format(e/f*100));
                    }
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            StockMart.updateCompanyTable();
                        }
                    });
                    try{
                        Thread.sleep(SLEEP);
                    }catch(Exception mm){}
                }
            }
        };
        calculations.start();
    }
    void loadCompanies()
    {
        try
        {
            BufferedReader br=new BufferedReader(new FileReader("companies.txt"));
            String str;
            while((str=br.readLine())!=null)
            {
                String s[]=str.trim().split(":");
                List<Double> l=new ArrayList<Double>();
                l.add(Double.parseDouble(s[1]));
                companies.add(new Company(s[0],l,0,Integer.parseInt(s[2])));
            }
        }catch(Exception m){
        m.printStackTrace();}
    }
    void stop()
    {
        try
        {
            server.stopServer();
        }catch(Exception m){}
        try
        {
            bserver.close=1;
        }catch(Exception m){}
        try
        {
            rserver.close=1;
        }catch(Exception m){}
        try
        {
            calculations.stop();
        }catch(Exception m){}
    }
}
