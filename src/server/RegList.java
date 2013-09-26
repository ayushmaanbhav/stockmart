package server;
import java.util.*;
import java.io.*;
import javax.swing.*;
import user.*;
class RegList
{
    volatile static protected List<User> userList;
    static protected List<String> regno;
    static void loadList()
    {
        userList=new ArrayList<User>();
        regno=new ArrayList<String>();
        try
        {
            BufferedReader br=new BufferedReader(new FileReader("reg.txt"));
            String str;
            while((str=br.readLine())!=null)
            {
                str=str.trim().toLowerCase();
                regno.add(str);
            }
        }catch(Exception m){
        m.printStackTrace();}
    }
    static int registerUser(final String regno,final String name,String pass)
    {
        synchronized(userList)
        {
            //if(containsRegNo(regno))
            {
                for(int i=0;i<userList.size();i++)
                {
                    if(userList.get(i).checkName(name))
                        if(userList.get(i).checkRegNo(regno))
                            return 4;
                        else
                            return 3;
                }
                userList.add(new User(regno,name,pass,100));
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        StockMart.users.addItem(name);
                    }
                });
                return 1;
            }
            //return 2;
        }
    }
    static boolean containsRegNo(String reg)
    {
        for(int i=0;i<regno.size();i++)
        {
            if(regno.get(i).equals(reg))
                return true;
        }
        return false;
    }
    static User getUserWithRegNo(String r)
    {
        synchronized(userList)
        {
            for(int i=0;i<userList.size();i++)
            {
                if(userList.get(i).checkRegNo(r))
                {
                    return userList.get(i);
                }
            }
        }
        return null;
    }
    static User getUserWithName(String n)
    {
        synchronized(userList)
        {
            for(int i=0;i<userList.size();i++)
            {
                if(userList.get(i).checkName(n))
                {
                    return userList.get(i);
                }
            }
        }
        return null;
    }
    static int validate(String n,String p)
    {
        synchronized(userList)
        {
            User user;
            int value=1;
            try{
                for(int i=0;i<userList.size();i++)
                {
                    user=userList.get(i);
                    if(user.isBanned())
                        return 11;
                    if(user.checkName(n))
                    {
                        value=2;
                        if(user.checkPassword(p))
                        {
                            return 0;
                        }
                    }
                }
            }catch(Exception e){}
            return value;
        }
    }
    static User getUser(String n,String p)
    {
        synchronized(userList)
        {
            User user;
            try{
                for(int i=0;i<userList.size();i++)
                {
                    user=userList.get(i);
                    if(user.checkName(n))
                    {
                        if(user.checkPassword(p))
                        {
                            return user;
                        }
                    }
                }
            }catch(Exception e){}
        }
        return null;
    }
    static User deleteUserWithName(String n)
    {
        synchronized(userList)
        {
            for(int i=0;i<userList.size();i++)
            {
                if(userList.get(i).checkName(n))
                {
                    User uu=userList.get(i);
                    userList.remove(i);
                    return uu;
                }
            }
        }
        return null;
    }
}