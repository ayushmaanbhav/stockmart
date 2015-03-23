package user;
import java.io.*;
import java.util.*;

public class Shares implements Serializable
{
    volatile public String company,status;
    volatile public int qty,id,sellid;
    volatile public double cost;
    public Date ordered,buyed;
    volatile public boolean notCanceled;
    public Shares()
    {
        company=null;
        status=null;
        qty=0;
        id=-1;
        sellid=-1;
        cost=0.0;
        ordered=null;
        buyed=null;
        notCanceled=true;
    }
    public Shares(Shares s)
    {
        try{
            company=new String(s.company);
        }catch(Exception k){
            company=null;
        }
        try{
            status=new String(s.status);
        }catch(Exception k){
            status=null;
        }
        qty=s.qty;
        id=s.id;
        sellid=s.sellid;
        cost=s.cost;
        try{
            ordered=(Date)s.ordered.clone();
        }catch(Exception k){
            ordered=null;
        }
        try{
            buyed=(Date)s.buyed.clone();
        }catch(Exception k){
            buyed=null;
        }
        notCanceled=s.notCanceled;
    }
}
