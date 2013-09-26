package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import user.*;

class ChatWindow extends JPanel
{
    JTextField jtf;
    JTextArea jta;
    JLabel jlab;
    Client cc;
    User user;
    String remain;
    public ChatWindow(final User u,Client client)
    {
        user=u;
        cc=client;
        setLayout(new BorderLayout());
        jlab=new JLabel("<<DISCUSSION>> ("+u.getChat()+" characters remainimg)");
        jtf=new JTextField();
        jta=new JTextArea();
        jta.setFont(new Font("Lucida Sans Unicode",Font.PLAIN,12));
        jta.setLineWrap(true);
        jta.setEditable(false);
        jta.setForeground(Color.green.darker());
        //jtf.setEnabled(false);
        jtf.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                final String s = jtf.getText();
                if (!s.equals("")) 
                {
                    jtf.setText("");
                    new Thread(){
                        public void run(){
                            try
                            {
                                remain=sendString(s);
                                u.setChat(Integer.parseInt(remain));
                                SwingUtilities.invokeLater(new Runnable(){
                                    public void run(){
                                        jlab.setText("<<DISCUSSION>> ("+u.getChat()+" characters remainimg)");
                                    }
                                });
                            }catch(Exception mm){}
                            if(u.getChat()<=0)
                                JOptionPane.showMessageDialog(null,"Sorry, You have finished your chat limit.","Error:",JOptionPane.PLAIN_MESSAGE);
                        }
                    }.start();
                }
            }
        });
        JScrollPane jsp=new JScrollPane(jta,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(jlab,BorderLayout.NORTH);
        add(jsp,BorderLayout.CENTER);
        add(jtf,BorderLayout.SOUTH);
        try{
            jta.setText(cc.getChatHistory(user.getName(),user.getPassword()));
        }catch(Exception mmm){}
        ChatClient cser=new ChatClient(jta);
        //cser.start();
        client.cc=cser;
    }
    String sendString(String s)
    {
        return cc.sendChat(s,user.getName(),user.getPassword());
    }
}