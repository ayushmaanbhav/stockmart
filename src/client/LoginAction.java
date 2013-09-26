package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import javax.swing.border.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import user.*;

class LoginAction implements ActionListener
{
    HintTextField user;
    HintPasswordField pass;
    String domain;
    Client client;
    Main main;
    public LoginAction(Main mm,HintTextField u,HintPasswordField p,String d,Client c)
    {
        user=u;
        pass=p;
        domain=d;
        client =c;
        main=mm;
    }
    public void actionPerformed(ActionEvent e)
    {
        Pattern p1=Pattern.compile("[a-z0-9_]{3,16}");
        Pattern p2=Pattern.compile("[a-z0-9_]{6,18}");
        if(!p1.matcher(user.getText()).matches())
        {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null,"Incorrect username.\nA username should be of length {3-16} and can contain\nletters(a-z), numbers and underscores.","Error:",JOptionPane.PLAIN_MESSAGE);
                    user.setText("");
                }
            });
            return;
        }
        if(!p2.matcher(pass.getText()).matches())
        {
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    JOptionPane.showMessageDialog(null,"Incorrect password.\nA password should be of length {6-18} and can contain\nletters(a-z), numbers and underscores","Error:",JOptionPane.PLAIN_MESSAGE);
                    pass.setText("");
                }
            });
            return;
        }
        if(domain.equals("domain"))
        {
             domain=JOptionPane.showInputDialog("Enter domain.");
        }
        if(domain==null || domain.equals(""))
            return;
        new Thread(){
            public void run()
            {
                String us=user.getText();
                String pa=pass.getText();
                String rep=client.login(domain,us,pa);
                if(rep==null)
                    return;
                if(rep.equals("0"))
                {
                    final User user=(User)client.getUserDetails(us,pa);
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            main.proceedLogin(user);
                        }
                    }); 
                }
                else if(rep.equals("1"))
                {
                    client.disconnect();
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            JOptionPane.showMessageDialog(null,"Server Replies: Incorrect username.\nA username should be of length {3-16} and can contain\nletters(a-z), numbers and underscores.","Error:",JOptionPane.PLAIN_MESSAGE);
                            user.setText("");
                        }
                    });
                    return;
                }
                else if(rep.equals("2"))
                {
                    client.disconnect();
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            JOptionPane.showMessageDialog(null,"Server Replies: Incorrect password.\nA password should be of length {6-18} and can contain\nletters(a-z), numbers and underscores","Error:",JOptionPane.PLAIN_MESSAGE);
                            pass.setText("");
                        }
                    });
                    return;
                }
                else if(rep.equals("11"))
                {
                    client.disconnect();
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            JOptionPane.showMessageDialog(null,"Server Replies: Sorry you have been banned from playing this game ! Please contact the admmin.","Error:",JOptionPane.PLAIN_MESSAGE);
                            user.setText("");
                            pass.setText("");
                        }
                    });
                    return;
                }
            }
        }.start();
    }
}