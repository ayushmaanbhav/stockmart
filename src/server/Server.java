package server;  
import java.io.*;
import javax.swing.*;
import java.net.*;
import java.util.*;
import user.*;
public class Server {

    private ServerSocket serverSocket;
    private boolean started;
    private Thread serverThread;
    int clientCount=0;
    ChatServer chat;
    static String chatHist="";
    List<Company> companies;
    List<Thread1> clientList;
    
    abstract class Thread1 extends Thread
    {
        abstract public void sendMessage(String mess);
    }
    
    public Server(List<Company> comp) throws IOException {
        companies=comp;
        serverSocket = new ServerSocket(4446,20000);
        serverSocket.setReuseAddress(true);
        chat=new ChatServer(this);
        clientList=new ArrayList<Thread1>();
    }

    // This server starts on a seperate thread so you can still do other things on the server program
    public void startServer() {
        if (!started) {
            started = true;
            serverThread = new Thread() {
                public void run() {
                    while (Server.this.started) {
                        Socket clientSocket = null;
                        try {
                            clientSocket = serverSocket.accept();
                            openClient(clientSocket);
                            try{
                                Thread.sleep(10);
                            }catch(Exception n){}
                        } catch (SocketException e) {
                            System.err.println("Server closed.");
                        } catch (IOException e) {
                            System.err.println("Accept failed.");
                        }
                    }
                }
            };
            serverThread.start();
            chat.start();
        }
    }

    public void stopServer() {
        this.started = false;
        serverThread.interrupt();
        try {
            serverSocket.close();
        } catch (IOException ex) {
            System.err.println("Server stop failed.");
        }
    }
    
    public void sendMulti(String mess) {
        for(int i=0;i<clientList.size();i++)
        {
            if(clientList.get(i).isAlive())
                clientList.get(i).sendMessage(mess);
            else
                clientList.remove(i--);
        }
    }

    int id=0;
    public void openClient(final Socket socket) 
    {
        clientCount++;
        Thread1 g=new Thread1() 
        {
            BufferedReader in;
            //PrintWriter out;
            ObjectOutputStream objout;
            ObjectInputStream objin;
            volatile protected User user=null;
            volatile protected boolean loggedIn=false;
            public void run() 
            {
                try 
                {
                    //out=new PrintWriter(socket.getOutputStream(),true);
                    in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    objout=new ObjectOutputStream(socket.getOutputStream());
                    //objout.enableReplaceObject(true);
                    //objin=new ObjectInputStream(socket.getInputStream());
                    
                    String inputLine, outputLine;

                    while((inputLine=(String)in.readLine())!=null)
                    {
                        //System.out.println(inputLine);
                        processCommand(inputLine);
                    }

                    objout.close();
                    in.close();
                    socket.close();
                } catch (Exception ex) {
                ex.printStackTrace();}
            }
            void processCommand(String command1)
            {
                String command=command1.toLowerCase().split(";")[1];
                String cmmd[]=command.split(":");
                for(int i=0;i<cmmd.length;i++)
                    cmmd[i]=cmmd[i].trim();
                if(cmmd[0].equals("reg"))
                { 
                    int res=RegList.registerUser(cmmd[1],cmmd[2],cmmd[3]);
                    if(res==1)
                    {
                        //out.println("1:Registration Successful !!");
                        try
                        {
                            //System.out.println(res);
                            objout.writeObject(new String("1:rs"));
                            objout.flush();
                            //System.out.println(res);
                        }catch(Exception e){e.printStackTrace();}
                    }
                    else
                    {
                        //out.println(res+":Wrong Registration Number !!");
                        //out.flush();    
                        try
                        {
                            //System.out.println(res);
                            objout.writeObject(new String(res+":wrn"));
                            objout.flush();
                            //System.out.println(res);
                        }catch(Exception e){e.printStackTrace();}
                    }
                    try
                    {
                        //Thread.sleep(5000);
                        in.close();
                        objout.close();
                        socket.close();
                        return;
                    }catch(Exception e)
                    {
                        in=null;
                        objout=null;
                        return;
                    }
                }
                else if(cmmd[0].equals("login"))
                {
                    int res=RegList.validate(cmmd[1],cmmd[2]);
                    if(res==0 && !RegList.getUser(cmmd[1],cmmd[2]).isBanned())
                    {
                        user=RegList.getUser(cmmd[1],cmmd[2]);
                        loggedIn=true;
                        /*new Thread(){
                            public void run(){
                                try{
                                    while(loggedIn)
                                    {
                                        try{
                                            Thread.sleep(60000);
                                        }catch(Exception f){}
                                        objout.reset();
                                        objout.writeObject(new String("there?"));
                                        objout.flush();
                                    }
                                }catch(Exception e){}
                            }
                        }.start();*/
                        try{  
                            objout.writeObject(new String("0:s"));
                            objout.flush();
                        }catch(Exception e){e.printStackTrace();}
                    }
                    else
                    {
                        if(user!=null)
                        {
                            loggedIn=false;
                        }
                        //out.println(res+":failed");
                        //out.flush();
                        try{
                            objout.writeObject(new String(res+":f"));
                            objout.flush();
                        }catch(Exception e){e.printStackTrace();}
                        try
                        {
                            //Thread.sleep(5000);
                            in.close();
                            objout.close();
                            socket.close();
                            return;
                        }catch(Exception e)
                        {
                            in=null;
                            objout=null;
                            return;
                        }
                    }
                }
                else if(cmmd[0].equals("logout"))
                {
                    if(user!=null)
                    {
                        loggedIn=false;
                    }
                    clientCount--;
                    clientList.remove(this);
                    //out.println("1");
                    //out.flush();
                    try{
                        objout.writeObject(new String("1"));
                        objout.flush();
                    }catch(Exception e){}
                    try
                    {
                        //Thread.sleep(5000);
                        in.close();
                        objout.close();
                        socket.close();
                        return;
                    }catch(Exception e)
                    {
                        in=null;
                        objout=null;
                        return;
                    }
                }
                else if(user!=null && loggedIn && !user.isBanned())
                {
                    if(cmmd[0].equals("gud"))
                    {
                        try
                        {
                            try{
                                objout.reset();
                                objout.writeObject(user);
                                objout.flush();
                                clientList.add(this);
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(cmmd[0].equals("chat"))
                    {
                        String str="";
                        if(user.chatEnabled && user.getChat()>=0)
                        {
                            if(user.getChat()-command1.substring(command1.indexOf(":")+1).length()>=0)
                            {
                                //synchronized(chatHist)
                                //{
                                    user.setChat(user.getChat()-command1.substring(command1.indexOf(":")+1).length());
                                    str=user.getName()+":"+command1.substring(command1.indexOf(":")+1);
                                    chatHist += "<"+user.getName()+"> : "+command1.substring(command1.indexOf(":")+1)+"\n";
                                    chat.chaat+="<"+user.getName()+"> : "+command1.substring(command1.indexOf(":")+1)+"\n";
                                //}
                                final String ptr=str;
                                new Thread(){
                                    public void run()
                                    {
                                        chat.interrupt();
                                    }
                                }.start();
                                SwingUtilities.invokeLater(new Runnable(){
                                    public void run(){
                                        StockMart.chat.setText(chatHist);
                                    }
                                });
                            }
                        }
                        try{
                            objout.writeObject(Integer.toString(user.getChat()));
                            objout.flush();
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(cmmd[0].equals("chath"))
                    {
                        try{
                            objout.writeObject(chatHist);
                            objout.flush();
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if(StockMart.started)
                    {
                        if(cmmd[0].equals("buy"))
                        {            
                            final Shares share=new Shares();
                            share.company=command1.split(":")[1].trim();
                            share.qty=Integer.parseInt(cmmd[2]);
                            share.id=id++;
                            share.cost=-1.0;
                            share.sellid=-1;
                            share.buyed=new Date();
                            share.ordered=new Date();
                            share.status="Shares Pending";
                            share.notCanceled=true;
                        
                            Company comp=null;
                            for(int k=0;k<companies.size();k++)
                            {
                                if(companies.get(k).name.equalsIgnoreCase(share.company))
                                {
                                    comp=companies.get(k);
                                    break;
                                }
                            }
                        
                            share.cost=comp.sharevalue.get(comp.sharevalue.size()-1);
                                        
                            new Thread(){
                                volatile Company comp;
                                volatile Shares sh;
                                public void run(){
                                    final int id=share.id;
                                    try{
                                        Thread.sleep(32000);
                                    }catch(Exception e){}
                                    synchronized(user)
                                    {
                                        synchronized(companies)
                                        {
                                            for(int i=0;i<user.getPendingShares().size();i++)
                                            {
                                                if(user.getPendingShares().get(i).id==id)
                                                {
                                                    sh=user.getPendingShares().get(i);
                                                    comp=null;
                                                    for(int k=0;k<companies.size();k++)
                                                    {
                                                        if(companies.get(k).name.equalsIgnoreCase(sh.company))
                                                        {
                                                            comp=companies.get(k);
                                                            break;
                                                        }
                                                    }
                                                    if(sh.notCanceled && !user.isBanned())
                                                    {      
                                                        if(sh.qty<=comp.totalshares && sh.cost!=-1.0 && Double.parseDouble(StockMart.twoDForm.format(sh.qty*sh.cost*1.02))<=user.getCurrentMoney())
                                                        {
                                                            sh.buyed=new Date();
                                                            sh.status="Delivered";
                                                            user.setCurrentMoney(user.getCurrentMoney()-Double.parseDouble(StockMart.twoDForm.format(sh.cost*sh.qty*1.02)));
                                                            comp.sharessold+=share.qty;
                                                            int y=0;
                                                            for(int z=0;z<user.getCurrentShares().size();z++)
                                                            {
                                                                if(user.getCurrentShares().get(z).company.equalsIgnoreCase(sh.company))
                                                                {
                                                                    user.getCurrentShares().get(z).cost=Double.parseDouble(StockMart.twoDForm.format((user.getCurrentShares().get(z).cost*user.getCurrentShares().get(z).qty+sh.cost*sh.qty)/(sh.qty+user.getCurrentShares().get(z).qty)));
                                                                    user.getCurrentShares().get(z).qty+=sh.qty;
                                                                    y++;
                                                                    break;
                                                                }
                                                            }
                                                            if(y==0)
                                                            {
                                                                user.getCurrentShares().add(new Shares(sh));
                                                            }
                                                        }
                                                        else
                                                        {
                                                            sh.status="Failed (Not enough money in your account)";
                                                        }
                                                    }
                                                    else
                                                    {
                                                        //comp.sharessold-=share.qty;
                                                        //user.setCurrentMoney(user.getCurrentMoney()+sh.qty*sh.cost);
                                                        sh.status="Canceled";
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    try{
                                        objout.reset();
                                        objout.writeObject(user);
                                        objout.flush();
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        
                            try{                          
                                user.getPendingShares().add(share);
                                objout.writeObject(share);
                                objout.flush();
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if(cmmd[0].equals("sell"))
                        {            
                            final Shares share=new Shares();
                            share.company=command1.split(":")[1].trim();
                            share.qty=Integer.parseInt(cmmd[2]);
                            share.id=Integer.parseInt(cmmd[3]);
                            share.sellid=id++;
                            share.cost=-1.0;
                            share.ordered=new Date();
                            share.status="Payment Pending";
                            share.notCanceled=true;
                        
                            Company comp=null;
                            for(int i=0;i<companies.size();i++)
                            {
                                if(companies.get(i).name.equalsIgnoreCase(share.company))
                                {
                                    comp=companies.get(i);
                                    break;
                                }
                            }
                        
                            share.cost=comp.sharevalue.get(comp.sharevalue.size()-1);
                        
                            new Thread(){
                                volatile Shares sh=null,shar=null;
                                volatile Company comp=null;
                                public void run(){
                                    final int sellid=share.sellid;
                                    final int id=share.id;
                                    try{
                                        Thread.sleep(32000);
                                    }catch(Exception e){} 
                                    synchronized(companies)
                                    {
                                        synchronized(user)
                                        {
                                            for(int i=0;i<user.getPendingShares().size();i++)
                                            {
                                                if(user.getPendingShares().get(i).sellid==sellid)
                                                {
                                                    sh=user.getPendingShares().get(i);
                                                    break;
                                                }
                                            }
                                            for(int k=0;k<user.getCurrentShares().size();k++)
                                            {
                                                if(user.getCurrentShares().get(k).id==id)
                                                {
                                                    shar=user.getCurrentShares().get(k);
                                                    break;
                                                }
                                            }                                 
                                            for(int j=0;j<companies.size();j++)
                                            {
                                                if(companies.get(j).name.equalsIgnoreCase(sh.company))
                                                {
                                                    comp=companies.get(j);
                                                    break;
                                                }
                                            }                                
                                            if(sh.notCanceled && !user.isBanned())
                                            {
                                                try{
                                                    if(sh.qty>0 && shar.qty>0 && sh.qty<=shar.qty && sh.cost!=-1.0)
                                                    {
                                                        shar.qty-=sh.qty;
                                                        comp.sharessold-=sh.qty;
                                                        sh.buyed=(Date)shar.buyed.clone();
                                                        user.setCurrentMoney(user.getCurrentMoney()+Double.parseDouble(StockMart.twoDForm.format(sh.qty*sh.cost*0.98)));
                                                        sh.status="Payment Received";
                                                        if(shar.qty<=0)
                                                            user.getCurrentShares().remove(shar);
                                                    }
                                                    else
                                                    {
                                                        sh.status="Failed (Nothing to sell)";
                                                    }
                                                }catch(Exception nn)
                                                {
                                                    sh.status="Failed (Nothing to sell)";
                                                }
                                            }
                                            else
                                            {
                                                //comp.sharessold+=sh.qty;
                                                sh.status="Canceled";
                                            }
                                        }
                                    }
                                    try{
                                        objout.reset();
                                        objout.writeObject(user);
                                        objout.flush();
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();
                        
                            try{                          
                                user.getPendingShares().add(share);
                                objout.writeObject(share);
                                objout.flush();
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else if(cmmd[0].equals("cancel"))
                        {
                            try{
                                int id=Integer.parseInt(cmmd[1]);
                                int sellid=Integer.parseInt(cmmd[2]);
                                Shares sh=null;
                                for(int i=0;i<user.getPendingShares().size();i++)
                                {
                                    if(user.getPendingShares().get(i).id==id && user.getPendingShares().get(i).sellid==sellid)
                                    {
                                        sh=user.getPendingShares().get(i);
                                        break;
                                    }
                                }
               
                                if((long)(new Date().getTime()-sh.ordered.getTime())<=(long)10000)
                                {
                                    sh.notCanceled=false;
                                    sh.status="Cancelling";
                                    try{
                                        objout.writeObject(new String("1"));
                                        objout.flush();
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    try{
                                        objout.writeObject(new String("0"));
                                        objout.flush();
                                    }catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }catch(Exception ef)
                            {
                                ef.printStackTrace();
                            }
                        }
                        else
                        {
                            try{
                                objout.writeObject(new String("102"));
                                objout.flush();
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        clientCount--;
                        clientList.remove(this);
                        try
                        {
                            loggedIn=false;
                            objout.writeObject(new String("101"));
                            objout.flush();
                            in.close();
                            objout.close();
                            socket.close();
                            user=null;
                            return;
                        }catch(Exception e)
                        {
                            in=null;
                            objout=null;
                            return;
                        }
                    }
                }
                else
                {
                    clientCount--;
                    clientList.remove(this);
                    try
                    {
                        loggedIn=false;
                        objout.writeObject(new String("101"));
                        objout.flush();
                        in.close();
                        objout.close();
                        socket.close();
                        user=null;
                        return;
                    }catch(Exception e)
                    {
                        in=null;
                        objout=null;
                        return;
                    }
                }
            }
            public void sendMessage(final String mess)
            {
                new Thread(){
                    public void run()
                    {
                        try{            
                            objout.reset();
                            objout.writeObject(mess);
                            objout.flush();
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        };
        g.start();
    }
}