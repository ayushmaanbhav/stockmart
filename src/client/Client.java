package client;  
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import user.*;

public class Client {
    private boolean connected = false;
    Socket socket = null;
    PrintWriter out = null;
    //BufferedReader in = null;
    ObjectInputStream in;
    String domain;
    volatile int commID=0;
    Client ccl;
    int usrD;
    Main m;
    testMain maain;
    BroadcastClient bc=null;
    RankingClient rc=null;
    ChatClient cc=null;
    Receiver rec;
    
    public Client(Main mm,testMain maaii)
    {
        ccl=this;
        m=mm;
        maain = maaii;
        commID=0;
    }
    
    class Receiver extends Thread
    {
        ObjectInputStream in;
        Hashtable rev,rev2;
        public Receiver(ObjectInputStream i)
        {
            in=i;
            rev=new Hashtable();
            rev2=new Hashtable();
        }
        public void run()
        {
            while(connected)
            {
                try{
                    Object obj = in.readObject();
                    if(obj.toString().equals("-101"))
                    {
                        connected=false;
                        in.close();
                        out.close();
                        socket.close();
                        SwingUtilities.invokeLater(new Runnable(){
                            public void run(){
                                Cashier.closee=true;
                                JOptionPane.showMessageDialog(null,"Disconnected from server. Please Restart","Error:",JOptionPane.PLAIN_MESSAGE);
                                try{
                                    m.stop();
                                }catch(Exception w){}
                            }
                        });
                    }
                    else if(obj.toString().split("::")[0].equals("broadcast"))
                    {
                        //System.out.println("braodcast received");
                        bc.run(obj.toString().substring(obj.toString().indexOf("::")+2));
                    }
                    else if(obj.toString().split("::")[0].equals("chat"))
                    {
                        //System.out.println("chat received: "+obj.toString().substring(obj.toString().indexOf("::")+2));
                        cc.run(obj.toString().substring(obj.toString().indexOf("::")+2));
                    }
                    else if(obj.toString().split("::")[0].equals("rankings"))
                    {
                        String hhh = obj.toString().split("::")[1];
                        final JDialog jd=new JDialog();
                        jd.setUndecorated(false);
                        JPanel pan = new JPanel(new BorderLayout());
                        JLabel ppp = new JLabel();
                        ppp.setFont(new Font("Arial",Font.BOLD,20));
                        ppp.setText("<html><pre>Thanks for playing !!!<br/>1st: "+hhh.split(":")[0]+"<br/>2nd: "+hhh.split(":")[1]+"<br/>3rd: "+hhh.split(":")[2]+"</pre></html>");
                        pan.add(ppp, BorderLayout.CENTER);
                        JButton ok = new JButton("Ok");
                        ok.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent e){
                                jd.setVisible(false);
                                try{
                                    m.stop();
                                }catch(Exception w){}
                            }
                        });
                        pan.add(ok, BorderLayout.SOUTH);
                        jd.setContentPane(pan);
                        jd.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
                        jd.pack();
                        jd.setLocationRelativeTo(null);
                        jd.setVisible(true);
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
                            /*if(usrD==1)
                            {
                                reply=obj;
                                ccl.interrupt();
                            }
                            else*/
                            {
                                try{
                                    m.ur.changeData((User)obj);
                                }catch(Exception w)
                                {
                                    try{
                                        maain.ur.changeData((User)obj);
                                    }catch(Exception ppp)
                                    {ppp.printStackTrace();}
                                }
                            }
                        }catch(Exception p)
                        {
                            int iid = -1;
                            try{
                                iid = Integer.parseInt(obj.toString());
                                obj = in.readObject();
                                if(obj.toString().equals("-102"))
                                {
                                    //ccl.interrupt();
                                    SwingUtilities.invokeLater(new Runnable(){
                                        public void run(){
                                            Cashier.closee=true;
                                            JOptionPane.showMessageDialog(null,"Server Not Running.","Error:",JOptionPane.PLAIN_MESSAGE);
                                        }
                                    });
                                }
                                //Thread th = ((Thread)rev.remove(iid));
                                rev2.put(iid,obj);
                                //System.out.println("Put: "+iid+"   :   "+obj.toString()+"   :   "+Thread.currentThread());
                                //th.interrupt();
                                //ccl.interrupt();
                            }catch(Exception ppp)
                            {/*ppp.printStackTrace();*/System.out.println("Shit: "+iid+"   :   "+obj.toString()+"   :   "+Thread.currentThread());}
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
            String rep=(String)receiveReply(cmdID);
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
            String rep=(String)receiveReply(cmdID);
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
            User vv=(User)receiveReply(cmdID);
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
            return (String)receiveReply(cmdID);
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
            return (String)receiveReply(cmdID);
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
            Shares pen=(Shares)receiveReply(cmdID);
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
            return (String)receiveReply(cmdID);
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    java.util.List<Double> getHistory(String comp,int count)
    {
        int cmdID=commID++;
        //connect(user.getName(),user.getPassword());
        try{
            out.println(cmdID+";"+"getch:"+comp+":"+Integer.toString(count));
            out.flush();
            return (java.util.List<Double>)receiveReply(cmdID);
        }catch(Exception r){
        r.printStackTrace();}
        return null;
    }
    
    boolean loop;
    synchronized Object receiveReply(int cmdID)
    {
        Object reply = null;
        rec.rev.put(cmdID, Thread.currentThread());
        //loop=true;
        int i=0;
        while(/*loop &&*/ i<=150)
        {            
            try
            {
                Thread.sleep(200);
                //System.out.println("Shit: "+cmdID+"   :   "+Thread.currentThread());
                if (rec.rev2.containsKey(cmdID))
                {
                    reply = rec.rev2.get(cmdID);
                    break;
                }
                i++;
            }catch(Exception m){
                //m.printStackTrace();
            }
        }
        if(i==300)
            System.out.println("Shit: "+cmdID+"   :   "+Thread.currentThread());
        return reply;
    }
    
    /*synchronized void interrupt()
    {
        loop=false;
    }*/
}