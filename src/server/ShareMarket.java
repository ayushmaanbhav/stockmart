package server;
import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
class ShareMarket
{
    volatile static List<Company> companies;
    Server server;
    BroadcastServer bserver;
    RankingServer rserver;
    static double sensex;
    Orders orderbook;
    Thread calculations;
    int SLEEP=30000,loopp = 1;
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    BufferedWriter bw;
    double T,N;
     
    void initialise()
    {
        companies=new ArrayList<Company>();
        RegList.loadList();
        loadCompanies();
        try{
            bw = new BufferedWriter(new FileWriter("appdata/sharemarket.txt"));
        }catch(Exception mm){}
    }
    void startApp()
    {
        orderbook = new Orders();
        try{
            bw.write(new Date().toString()+":app started\n");
            bw.flush();
        }catch(Exception mm){mm.printStackTrace();}
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
        try{
            bw.write(new Date().toString()+":market started\n");
            bw.flush();
        }catch(Exception mm){mm.printStackTrace();}
        bserver.start();
        calculations=new Thread(){
            public void run()
            {
                T = StockMart.totTime;
                N = (int)(T*2);
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){                            
                        try{
                            StockMart.updateCompanyTable();
                            /*if(GraphPanel.frame.isVisible())
                                GraphPanel.mainPanel.repaint();*/
                        }catch(Exception r){r.printStackTrace();}
                    }
                });
                while(StockMart.started)
                {
                    try{
                        Thread.sleep(SLEEP);
                    }catch(Exception mm){}
                    synchronized(companies)
                    {
                        /*OLD METHOD
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
                            {
                                com.sharevalue.add(newvalue);
                                com.d=new Date();
                            }
                            else
                            {
                                //bankrupt
                            }
                            com.tsharessold+=com.sharessold;
                            com.sharessoldpast=com.sharessold;
                            com.sharessold=0;
                        }
                        sensex = Double.valueOf(twoDForm.format(e/f*100));*/
                        Random random = new Random(System.currentTimeMillis());
                        //double ti = loopp*T/N;
                        double avg = 0;
                        for(int i=0;i<companies.size();i++)
                        {
                            Company com=companies.get(i);
                            int oiu = (int)N - loopp;
                            oiu = Math.min(oiu, 10);
                            while(com.futurevalues.size()<=oiu)
                            {
                                com.W = com.W + Math.sqrt(T/N)*random.nextGaussian();
                                com.futurevalues.add(Double.valueOf(twoDForm.format(com.sharevalue.get(0)*Math.exp((com.rate-.5*Math.pow(com.vol,2))*((loopp+com.futurevalues.size())*T/N)+com.vol*com.W))));
                                //System.out.println(com.name+" "+N);
                            }
                            com.sharevalue.add(com.futurevalues.remove((int)0));
                            
                            com.d=new Date();
                            try{
                                com.perchange = Double.valueOf(twoDForm.format((com.sharevalue.get(com.sharevalue.size()-1) - com.sharevalue.get(com.sharevalue.size()-2))*100/com.sharevalue.get(com.sharevalue.size()-2)));
                            }catch(Exception mm)
                            {
                                com.perchange = 0;
                            }  
                            if(com.sharevalue.get(com.sharevalue.size()-1) <= 0.0)
                            {
                                com.bankrupt = true;
                            }
                            avg += com.sharevalue.get(com.sharevalue.size()-1);
                            com.tsharessold+=com.sharessold;
                            com.sharessoldpast=com.sharessold;
                            com.sharessold=0;
                        }
                        sensex = Double.valueOf(twoDForm.format(avg/companies.size()));
                        loopp++;
                    }
                    bserver.interrupt();
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){                            
                            try{
                                StockMart.updateCompanyTable();
                                if(GraphPanel.frame.isVisible())
                                    GraphPanel.mainPanel.repaint();
                            }catch(Exception r){r.printStackTrace();}
                        }
                    });
                    for(int i=0;i<companies.size();i++)
                    {
                        companies.get(i).updateFile();
                    }
                }
            }
        };
        calculations.setPriority(Thread.MAX_PRIORITY);
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
                //companies.add(new Company(s[0],l,0,Integer.parseInt(s[2]),Double.parseDouble(s[3]),Double.parseDouble(s[4])));
                companies.add(new Company(s[0],l,0,Integer.parseInt(s[2]),0.0005,0.03162277660168379331998893544433));
            }
        }catch(Exception m){
        m.printStackTrace();}
    }
    void stop()
    {
        try{
            bw.write(new Date().toString()+":market stopped\n");
            bw.flush();
        }catch(Exception mm){mm.printStackTrace();}
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
