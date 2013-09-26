package user;
import java.io.*;
import java.util.*;
import javax.swing.*;
public class User implements Serializable
{
    volatile protected String name;
    volatile protected String regno;
    volatile protected String password;
    volatile protected boolean loggedin=false;
    volatile protected double money;
    volatile protected List<Shares> shares;
    volatile protected List<Shares> pendingshares;
    //protected List<Shares> history;
    volatile protected UserDataChangedListener udcl;
    protected User u;
    volatile protected int chat;
    volatile public boolean chatEnabled,banned;
    public User(String r,String n,String p,int c)
    {
        chat=c;
        name=n;
        regno=r;
        chatEnabled=true;
        banned=false;
        password=p;
        udcl=null;
        shares=new ArrayList<Shares>();
        pendingshares=new ArrayList<Shares>();
        //history=new ArrayList<Shares>();
        try{
            BufferedReader br=new BufferedReader(new FileReader("money.txt"));
            money=Integer.parseInt(br.readLine());
        }catch(Exception e){}
        u=this;
    }
    public void setChat(int c)
    {
        chat=c;
    }
    public void setBanned(boolean c)
    {
        banned=c;
    }
    public boolean isBanned()
    {
        return banned;
    }
    public int getChat()
    {
        return chat;
    }
    public void addUserDataChangeListener(UserDataChangedListener u)
    {
        udcl=u;
    }
    public UserDataChangedListener removeUserDataChangeListener()
    {
        UserDataChangedListener u=udcl;
        udcl=null;
        return u;
    }
    public void invokeListener()
    {
        dataChanged();
    }
    public void changeData(User user)
    {
        name=user.getName();
        regno=user.getRegNo();
        password=user.getPassword();
        loggedin=user.isLoggedIn();
        money=user.getCurrentMoney();
        shares=user.getCurrentShares();
        pendingshares=user.getPendingShares();
        chat=user.getChat();
        chatEnabled=user.chatEnabled;
        u=this;
        dataChanged();
    }
    public boolean checkName(String n)
    {
        if(name.equals(n))
            return true;
        return false;
    }
    public boolean checkPassword(String p)
    {
        if(password.equals(p))
            return true;
        return false;
    }
    public boolean checkRegNo(String r)
    {
        if(regno.equals(r))
            return true;
        return false;
    }
    public void setLoggedIn(boolean set)
    {
        loggedin=set;
    }
    public boolean isLoggedIn()
    {
        return loggedin;
    }
    public String getRegNo()
    {
        return regno;
    }
    public void setRegNo(String r)
    {
        regno=r;
        dataChanged();
    }
    public String getName()
    {
        return name;
    }
    public void setName(String n)
    {
        name=n;
        dataChanged();
    }
    public String getPassword()
    {
        return password;
    }
    public void setPassword(String p)
    {
        password=p;
        dataChanged();
    }
    public double getCurrentMoney()
    {
        return money;
    }
    public void setCurrentMoney(double m)
    {
        money=m;
        dataChanged();
    }
    public List<Shares> getCurrentShares()
    {
        return shares;
    }
    /*public List<Shares> getHistory()
    {
        return history;
    }*/
    public List<Shares> getPendingShares()
    {
        return pendingshares;
    }
    public void setCurrentShares(List<Shares> c)
    {
        shares=c;
    }
    public void setPendingShares(List<Shares> c)
    {
        pendingshares=c;
    }
    public void dataChanged()
    {
        if(udcl!=null)
            SwingUtilities.invokeLater(new Runnable(){
                public void run()
                {
                    udcl.userDataChanged(u);
                }
            });
    }
}