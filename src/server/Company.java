package server;
import java.util.*;
class Company
{
    String name;
    volatile List<Double> sharevalue;
    volatile int sharessold,totalshares,tsharessold;
    volatile double perchange;
    public Company(String n, List<Double> l, int sd, int tot)
    {
        name=n;
        sharevalue=l;
        sharessold=sd;
        totalshares=tot;
        tsharessold=0;
    }
    /*public Company(String n)
    {
        name=n;
        sharevalue=new ArrayList<Double>();
        sharessold=0;
    }*/
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