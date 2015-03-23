package client;
import user.*;
import java.io.*;
class test
{
    int i;
    BufferedWriter bw;
    public void main(final String d,final int n)throws IOException
    {
        //bw = new BufferedWriter(new FileWriter("test_client_log.txt"));
        for(i=0;i<n;i++)
        {
            new testMain(){
                String regn,un,pass;
                public void run()
                {
                    try{
                    //Main m = new Main();
                    //m.domain = d;
                    Client client = new Client(null,this);
                    regn = "robotregno"+(int)(Math.random()*10000);
                    un = "robotuser"+(int)(Math.random()*10000);
                    pass = "robotpass"+(int)(Math.random()*10000);
                    if(!client.register(d,regn,un,pass).equals("1"))
                        return;
                    if(!client.login(d,un,pass).equals("0"))
                        return;
                    ur = (User)client.getUserDetails(un,pass);
                    /*m.udcl=new UserDataChangedListener(){
                        public void userDataChanged(User user)
                        {
                            //updateTables();
                        }
                    };
                    m.svcl=new ShareValuesChangeListener(){
                        public void valuesChanged()
                        {
                            //updateTables();
                        }
                    };*/
                    //m.cash=new Cashier(client,m.ur);
                    client.bc = new BroadcastClient(new StringBuffer());
                    client.rc = null;
                    client.cc = null;
                    while(true)
                    {
                        try{
                            double r = Math.random();
                            //bw.write(un+" "+r);
                            if(r >= 0.0 && r < 0.4375 && Companies.comp.size() > 0)
                            {
                                //bw.write(un+" buying");
                                int id = (int)(Math.random()*Companies.comp.size());
                                Company c = Companies.getCompanyWithId(id);
                                double money = Math.random()*ur.getCurrentMoney();
                                int noofshares = (int)(money/c.mktvalue);
                                if(noofshares > 0)
                                {
                                    client.placeOrder(ur,"buy",c,noofshares,c.id);
                                    /*bw.write(un+" placing buy order: "+c.name+noofshares+"\n");
                                    bw.flush();*/
                                }
                            }
                            else if(r >= 0.4375 && r < 0.875 && ur.getCurrentShares().size() > 0)
                            {
                                //bw.write(un+" selling");
                                int id = (int)(Math.random()*ur.getCurrentShares().size());
                                Company c = Companies.getCompanyWithName(ur.getCurrentShares().get(id).company);
                                int noofshares = (int)(Math.random()*ur.getCurrentShares().get(id).qty);
                                if(noofshares > 0)
                                {
                                    client.placeOrder(ur,"sell",c,noofshares,ur.getCurrentShares().get(id).id);
                                    /*bw.write(un+" placing sell order: "+c.name+noofshares+"\n");
                                    bw.flush();*/
                                }
                            }
                            else if(r >= 0.875 && r < 0.9 && ur.getPendingShares().size() > 0)
                            {
                                //bw.write(un+" canceling");
                                int id = (int)(Math.random()*ur.getPendingShares().size());
                                client.cancelShares(un,pass,ur.getPendingShares().get(id).id,ur.getPendingShares().get(id).sellid);
                                /*bw.write(un+" corder placed"+"\n");
                                bw.flush();*/
                            }
                            else if(r >= 0.9 && r < 0.925)
                            {
                                //bw.write(un+" chatting");
                                client.sendChat("hi "+pp++,un,pass);
                                /*bw.write(un+" chatted"+"\n");
                                bw.flush();*/
                            }
                            else
                            {
                                /*bw.write(un+" idle"+"\n");
                                bw.flush();*/
                            }
                            try{
                                Thread.sleep((long)(Math.random()*3000));
                            }catch(Exception r2){}
                        }catch(Exception r1){r1.printStackTrace();}
                    }
                    }catch(Exception r4){
                        try{
                            /*bw.write(un+" : "+r4.getMessage()+"\n");
                            bw.flush();*/
                        }catch(Exception jjj){}
                    }
                }
                int pp=0;
            }.start();
            try{
                Thread.sleep(100);
            }catch(Exception r2){}
        }
    }
    public static void main(String args[])
    {
        try{
            test t = new test();
            t.main("127.0.0.1",100);
        }catch(IOException ff){}
    }
}