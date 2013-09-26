package client;

class Company
{
    String name;
    int id;
    Double inivalue,mktvalue,low,high;
    public Company(String n,double m,double i,double hi,double lo,int ii)
    {
        name=new String(n);
        mktvalue=m;
        inivalue=i;
        low=lo;
        high=hi;
        id=ii;
    }
    public void updateData(double m,double hi,double lo)
    {
        mktvalue=m;
        low=lo;
        high=hi;
    }
}