package server;
import java.util.*;
import java.io.*;
class Company
{
    String name;
    volatile List<Double> sharevalue,futurevalues;
    volatile int sharessold,totalshares,tsharessold,sharessoldpast;
    volatile Date d;
    volatile double perchange,W,rate,vol;
    boolean bankrupt;
    BufferedWriter bw;
    public Company(String n, List<Double> l, int sd, int tot,double rate1, double vol1)
    {
        rate=rate1;
        vol=vol1;
        name=n;
        sharevalue=l;
        futurevalues = new ArrayList<Double>();
        sharessold=sd;
        totalshares=tot;
        tsharessold=0;
        bankrupt = false;
        W=0;
        try{
            bw = new BufferedWriter(new FileWriter("appdata/companydata/"+name+".txt"));
        }catch(Exception mm){}
    }
    /*public Company(String n)
    {
        name=n;
        sharevalue=new ArrayList<Double>();
        sharessold=0;
    }*/
    public void updateFile()
    {
        try{
            bw.write(d.toString()+":"+name+":"+sharevalue.get(sharevalue.size()-1)+":"+perchange+":"+sharessoldpast+":"+tsharessold+":"+totalshares+"\n");
            bw.flush();
        }catch(Exception mm){mm.printStackTrace();}
    }
    public double getHighest()
    {
        double h=sharevalue.get(0);
        for(int i=1;i<sharevalue.size();i++)
        {
            if((double)sharevalue.get(i)>h)
                h=sharevalue.get(i);
        }
        return h;
    }
    public double getLowest()
    {
        double l=sharevalue.get(0);
        for(int i=1;i<sharevalue.size();i++)
        {
            if((double)sharevalue.get(i)<l)
                l=sharevalue.get(i);
        }
        return l;
    }
}