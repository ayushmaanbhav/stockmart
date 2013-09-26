package client;  
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import user.*;

public class Client {
    private boolean connected = false;
    Socket socket = null;
    PrintWriter out = null;
    //BufferedReader in = null;
    ObjectInputStream in;
    String domain;
    Object reply;
    volatile int commID=0;
    Client ccl;
    int usrD;
    Main m;
    BroadcastClient bc=null;
    RankingClient rc=null;
    ChatClient cc=null;
    Receiver rec;
    
    public Client(Main mm)
    {
        ccl=this;
        reply=null;
        m=mm;
    }
    
    class Receiver extends Thread
    {
        ObjectInputStream in;
        List<Object> rev;
        public Receiver(ObjectInputStream i)
        {
            in=i;
            rev=new ArrayList<Object>();
        }
        public void run()
        {
            while(connected)
            {
                try{
                    Object obj = in.readObject();
                    if(obj.toString().equals("101"))
                    {
                        connected=false;
                        in.close();
                        out.close();
                        socket.close();
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                Cashier.closee=true;
                                JOptionPane.showMessageDialog(null,"Disconnected from server.","Error:",JOptionPane.PLAIN_MESSAGE);
                                m.getAppletContext().showDocument(m.getDocumentBase(), "_self");
                            }
                        });
                    }
                    else if(obj.toString().equals("102"))
                    {
                        reply=obj;
                        ccl.interrupt();
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                Cashier.closee=true;
                                JOptionPane.showMessageDialog(null,"Server Not Running.","Error:",JOptionPane.PLAIN_MESSAGE);
                            }
                        });
                    }
                    else if(obj.toString().split("::")[0].equals("broadcast"))
                    {
                        bc.run(obj.toString().substring(obj.toString().indexOf("::")+2));
                    }
                    else if(obj.toString().split("::")[0].equals("chat"))
                    {
                        cc.run(obj.toString().substring(obj.toString().indexOf("::")+2));
                    }
                    else if(obj.toString().split("::")[0].equals("rank"))
                    {
                        rc.run(obj.toString().substring(obj.toString().indexOf("::")+2));
                    }
                    else
                    {
                        User hhh=null;
                        try
                        {
                            hhh=(User)obj;
                            if(usrD==1)
                            {
                                reply=obj;
                                ccl.interrupt();
                            }
                            else
                            {
                                m.ur.changeData((User)obj);
                            }
                        }catch(Exception p)
                        {
                            reply=obj;
                            ccl.interrupt();
                        }
                    }
                    try{
                        Thread.sleep(500);
                    }catch(Exception n){}
                }catch(Exception m){}
            }
        }
    }
    
    public void connect(String user,String pass) {
        if (!connected) 
        {
            try {
                socket = new Socket(domain,4446);
                out = new PrintWriter(socket.getOutputStream(), true);
                //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                in=new ObjectInputStream(socket.getInputStream());
            } catch (java.net.UnknownHostException e) {
                System.err.println("Don't know about host");
                return;
            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to");
                return;
            }
            this.connected = true;
            rec=new Receiver(in);
            rec.start();
            commID=0;
        }
    }

    String login(String domain,String user,String pass)
    {
        int cmdID=commID++;
        this.domain=domain;
        connect(user,pass);
        try{
            out.println(cmdID+";login:"+user+":"+pass);
            out.flush();
            String rep=(String)receiveReply(0);
            return rep.split(":")[0];
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    String register(String domain,String regno,String user,String pass)
    {
        int cmdID=commID++;
        this.domain=domain;
        connect(user,pass);
        try{
            out.println(cmdID+";reg:"+regno+":"+user+":"+pass);
            out.flush();
            String rep=(String)receiveReply(0);
            disconnect();
            return rep.split(":")[0];
        }catch(Exception r){r.printStackTrace();}
        return null;
    }
    
    public void disconnect() 
    {
        int cmdID=commID++;
        this.connected = false;
        try {
            out.println(cmdID+";logout");
            out.flush();
            in.close();
            out.close();
            socket.close();
        } catch (IOException ex) {
            System.err.println("Server stop failed.");
        }
    }
    
    User getUserDetails(String user,String pass)
    {
        int cmdID=commID++;
        connect(user,pass);
        try{
            usrD=1;
            out.println(cmdID+";gud");
            out.flush();
            User vv=(User)receiveReply(0);
            usrD=0;
            return vv;
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    String sendChat(String s,String user,String pass)
    {
        int cmdID=commID++;
        connect(user,pass);
        try{
            out.println(cmdID+";chat:"+s.trim());
            out.flush();
            return (String)receiveReply(0);
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    String getChatHistory(String user,String pass)
    {
        int cmdID=commID++;
        connect(user,pass);
        try{
            out.println(cmdID+";chath");
            out.flush();
            return (String)receiveReply(0);
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    void placeOrder(final User user,String cmd,Company comp,int qty,int id)
    {
        int cmdID=commID++;
        connect(user.getName(),user.getPassword());
        try{
            out.println(cmdID+";"+cmd+":"+comp.name+":"+Integer.toString(qty)+":"+id);
            out.flush();
            Shares pen=(Shares)receiveReply(0);
            user.getPendingShares().add(pen);
            user.dataChanged();
        }catch(Exception r){
        r.printStackTrace();}
    }
    
    String cancelShares(String user,String pass,int id,int sellid)
    {
        int cmdID=commID++;
        connect(user,pass);
        try{
            out.println(cmdID+";cancel:"+id+":"+sellid);
            out.flush();
            return (String)receiveReply(0);
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    boolean loop;
    Object receiveReply(int cmdID)
    {
        loop=true;
        int i=0;
        while(loop && i<=300)
        {
            try
            {
                Thread.sleep(100);
                i++;
            }catch(Exception m){}
        }
        return reply;
    }
    
    void interrupt()
    {
        loop=false;
    }
}