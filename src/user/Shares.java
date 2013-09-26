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
    }
    public Shares(Shares s)
    {
        company=new String(s.company);
        status=new String(s.status);
        qty=s.qty;
        id=s.id;
        sellid=s.sellid;
        cost=s.cost;
        ordered=(Date)s.ordered.clone();
        buyed=(Date)s.buyed.clone();
        notCanceled=s.notCanceled;
    }
}
