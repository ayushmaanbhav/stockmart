package client;
import java.util.*;

class Company
{
    String name;
    int id,yy;
    Double inivalue,mktvalue,low,high;
    List<Double> sharevalue;
    public Company(String n,double m,double i,double hi,double lo,int ii)
    {
        name=new String(n);
        sharevalue = new ArrayList<Double>();
        mktvalue=m;
        sharevalue.add(m);
        inivalue=i;
        low=lo;
        high=hi;
        id=ii;
        yy=0;
    }
    public void updateData(double m,double hi,double lo)
    {
        mktvalue=m;
        try{
            if(m != sharevalue.get(sharevalue.size()-1))
                sharevalue.add(m);
        }catch(Exception ml){}
        low=lo;
        high=hi;
    }
}