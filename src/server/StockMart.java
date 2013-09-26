package server;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.Date;
import user.*;
import java.io.*;
class StockMart
{
    static JPanel puser,mpanel;
    static JLabel userd,sensex;
    static JTextArea chat,ranks,news;
    static JTable usertable,comptable;
    static DefaultTableModel dtmu,dtmc;
    static JTextField cht,ucht,tt;
    static JCheckBox chatEnabled;
    static JComboBox users;
    static String newsfeed="";
    static ShareMarket shr;
    static JButton server,post,changep,banu;
    static String colusr[]=new String[]{"SrNo.","Company","Quantity","C.M.P.","Mkt Value","Gain/Loss","Status"};
    static String[] columnNames2 = {"SrNo.","Company","C.M.P.","% Change","Low","High"};
    static DecimalFormat twoDForm = new DecimalFormat("#.##");
    static boolean started=false;
    static int totTime;
    static User uuu;
    static Date startDate;
    static DefaultTableCellRenderer dtr=new DefaultTableCellRenderer(){
        public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {
            if(value.toString().indexOf("-")!=-1)
            {
                value=value.toString()+" \u25bc";
            }
            else
            {
                value=value.toString()+" \u25b2";
            }
            Component c = super.getTableCellRendererComponent(table, value,isSelected, hasFocus,row, column);
            if(value.toString().indexOf("-")!=-1)
            {
                c.setForeground(Color.red);
            }
            else
            {
               c.setForeground(Color.green);
            }
            return c;
        }
    };
    static Timer timer2=new Timer(1000,new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            try{
                int secs = (int)(totTime*60-(new Date().getTime()-startDate.getTime())/1000);
                int hrs = secs/3600;
                int mins = (secs/60)-hrs*60;
                sensex.setText("Sensex: "+ShareMarket.sensex+"                Time Left: "+hrs+":"+mins);
                if(secs<=0)
                {
                    started=false;
                    server.setText("Start");
                    //shr.stop();
                    timer2.stop();
                }
            }catch(Exception bb){}
        }
    });
    static Timer timer=new Timer(1000,new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            try{
                uuu=RegList.getUserWithName((String)users.getSelectedItem());
                userd.setText("<html><pre>User-> RegNo: "+uuu.getRegNo()+"      Name: "+uuu.getName()+"      Password: "+uuu.getPassword()+"<br/>Money: "+twoDForm.format(uuu.getCurrentMoney())+"      Chat: "+uuu.getChat()+"</pre></html>");
                //ucht.setText(uuu.getChat()+"");
                chatEnabled.setSelected(uuu.chatEnabled);
                if(!uuu.isBanned())
                    banu.setText("Ban this User");
                else
                    banu.setText("Unban this User");
                refreshUserTable(uuu);
            }catch(Exception n)
            { 
            }
        }
    });
    
    public static void refreshUserTable(User u)
    {
        Object obj[][]=new Object[u.getCurrentShares().size()+u.getPendingShares().size()+1][7];
        java.util.List<Shares> ss=u.getCurrentShares();
        for(int i=0;i<u.getCurrentShares().size();i++)
        {
            Company comp=null;
            for(int j=0;j<shr.companies.size();j++)
            {
                if(shr.companies.get(j).name.equals(ss.get(i).company))
                {
                    comp=shr.companies.get(j);
                    break;
                }
            }
            obj[i]=new Object[]{Integer.toString(i+1) , ss.get(i).company , ss.get(i).qty , twoDForm.format(comp.sharevalue.get(comp.sharevalue.size()-1)) , twoDForm.format(ss.get(i).qty*comp.sharevalue.get(comp.sharevalue.size()-1)) , twoDForm.format(ss.get(i).qty*comp.sharevalue.get(comp.sharevalue.size()-1)-ss.get(i).qty*ss.get(i).cost) , ss.get(i).status };
        }
        obj[u.getCurrentShares().size()]=new Object[]{null,null,null,null,null,null,null};
        ss=u.getPendingShares();
        for(int i=0;i<u.getPendingShares().size();i++)
        {
            Company comp=null;
            for(int j=0;j<shr.companies.size();j++)
            {
                if(shr.companies.get(j).name.equals(ss.get(i).company))
                {
                    comp=shr.companies.get(j);
                    break;
                }
            }
            obj[i+u.getCurrentShares().size()+1]=new Object[]{Integer.toString(i+1) , ss.get(i).company , ss.get(i).qty , twoDForm.format(comp.sharevalue.get(comp.sharevalue.size()-1)) , null , null , ss.get(i).status };
        }
        dtmu.setDataVector(obj,colusr);
    }
    
    public static void updateCompanyTable()
    {
        try{
            Object data[][]=new Object[shr.companies.size()][6];
            for(int i=0;i<shr.companies.size();i++)
            {
                data[i]=new Object[]{Integer.toString(i+1) , shr.companies.get(i).name , twoDForm.format(shr.companies.get(i).sharevalue.get(shr.companies.get(i).sharevalue.size()-1)) , twoDForm.format((shr.companies.get(i).sharevalue.get(shr.companies.get(i).sharevalue.size()-1)-shr.companies.get(i).sharevalue.get(0))*100/shr.companies.get(i).sharevalue.get(0)) , twoDForm.format(shr.companies.get(i).getLowest()) , twoDForm.format(shr.companies.get(i).getHighest()) };
            }
            dtmc.setDataVector(data,columnNames2);
            comptable.getColumnModel().getColumn(3).setCellRenderer(dtr);
            comptable.repaint();
        }catch(Exception mm){mm.printStackTrace();}
    }
    
    public static void main(String args[])
    {
        SwingUtilities.invokeLater(new Runnable(){
            public void run(){
                initialise();
                shr=new ShareMarket();
                shr.initialise();
                shr.startApp();
                timer.start();
                server.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e){
                        if(!started)
                        {
                            started=true;
                            server.setText("Stop");
                            shr.start();
                            timer2.start();
                            startDate=new Date();
                            totTime=Integer.parseInt(tt.getText());
                        }
                        else
                        {
                            started=false;
                            server.setText("Start");
                            //shr.stop();
                            timer2.stop();
                        }
                    }
                });
            }
        });
    }
    
    public static void initialise()
    {
        LookAndFeel.set();
        JFrame jf=new JFrame("StockMart");
        mpanel=(JPanel)jf.getContentPane();
        mpanel.setLayout(new BorderLayout());
        post=new JButton("Post");
        post.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    newsfeed = news.getText().trim().replace("\n",":");
                    shr.server.chat.interrupt();
                }catch(Exception ppp){}
            }
        });
        server=new JButton("Start");
        userd=new JLabel("User-> RegNo:              Name:             Password:            Money:          Chat:         ");
        userd.setPreferredSize(new Dimension(300,25));
        sensex=new JLabel("Sensex:            Time Left:            ");
        tt=new JTextField(5);
        tt.setText("120");
        news=new JTextArea(5,30);
        chat=new JTextArea(5,30);
        chat.setFont(new Font("Lucida Sans Unicode",Font.PLAIN,15));
        chat.setForeground(Color.green.darker());
        chat.setEditable(false);
        chat.setLineWrap(true);
        ranks=new JTextArea(5,50);
        ranks.setEditable(false);
        ranks.setForeground(Color.red.darker());
        ranks.setFont(new Font("Lucida Sans Unicode",Font.BOLD,15));
        ranks.setLineWrap(true);
        cht=new JTextField(30);
        ucht=new JTextField(5);
        users=new JComboBox();
        chatEnabled=new JCheckBox("ChatEnabled",true);
        dtmu=new DefaultTableModel();
        dtmc=new DefaultTableModel();
        usertable=new JTable(dtmu){
            public boolean isCellEditable(int r,int c)
            {
                return false;
            }
        };
        comptable=new JTable(dtmc){
            public boolean isCellEditable(int r,int c)
            {
                if(c==2)
                    return true;
                return false;
            }
        };
        dtmc.addTableModelListener(new TableModelListener(){
            public void tableChanged(TableModelEvent tme)
            {
                final int row=tme.getFirstRow();
                try{
                    if(tme.getColumn()==2)
                        shr.companies.get(row).sharevalue.add(Double.parseDouble(twoDForm.format(Double.valueOf(dtmc.getValueAt(row,2).toString()))));
                }catch(Exception m)
                {
                    m.printStackTrace();
                }
            }
        });
        usertable.setRowHeight(30);
        usertable.getTableHeader().setReorderingAllowed(false);
        usertable.setFillsViewportHeight(true);
        comptable.setRowHeight(30);
        comptable.getTableHeader().setReorderingAllowed(false);
        comptable.setFillsViewportHeight(true);
        JScrollPane jspu=new JScrollPane(usertable);
        JScrollPane jspc=new JScrollPane(comptable);
        
        JPanel chatp=new JPanel(new BorderLayout());
        chatp.setPreferredSize(new Dimension(300,300));
        chatp.add(new JScrollPane(chat,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
        chatp.add(cht,BorderLayout.SOUTH);
        JPanel rr=new JPanel(new BorderLayout());
        rr.setPreferredSize(new Dimension(300,250));
        rr.add(new JScrollPane(ranks,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
        JPanel rank=new JPanel(new BorderLayout());
        rank.setPreferredSize(new Dimension(600,250));
        rank.add(chatp,BorderLayout.WEST);
        rank.add(rr,BorderLayout.EAST);
        
        changep=new JButton("Change Password");
        banu=new JButton("Ban this User");
        
        JPanel jpaa=new JPanel();
        jpaa.setLayout(new BorderLayout());
        jpaa.add(changep,BorderLayout.EAST);
        jpaa.add(banu,BorderLayout.WEST);
        jpaa.add(ucht,BorderLayout.CENTER);
        
        JPanel jp=new JPanel();
        jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
        jp.add(users);
        jp.add(userd);
        jp.add(jpaa);
        //jp.add(changep);
        //jp.add(ucht);
        jp.add(chatEnabled);
        jp.add(jspu);
        
        JPanel newspanel=new JPanel(new BorderLayout());
        newspanel.add(new JScrollPane(news,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
        newspanel.add(post,BorderLayout.EAST);
        
        puser=new JPanel(new BorderLayout());
        puser.add(rank,BorderLayout.NORTH);
        puser.add(jp,BorderLayout.CENTER);
        
        JPanel uu=new JPanel(new BorderLayout());
        uu.add(tt,BorderLayout.WEST);
        uu.add(sensex,BorderLayout.CENTER);
        uu.add(server,BorderLayout.EAST);
        
        JPanel qq=new JPanel(new BorderLayout());
        qq.add(jspc,BorderLayout.CENTER);
        qq.add(newspanel,BorderLayout.SOUTH);
        qq.add(uu,BorderLayout.NORTH);
        
        mpanel.add(qq,BorderLayout.CENTER);
        mpanel.add(puser,BorderLayout.EAST);
        
        ucht.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    uuu.setChat(Integer.parseInt(ucht.getText().trim()));
                }catch(Exception jj){}
            }
        });
        banu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    if(!uuu.isBanned())
                        uuu.setBanned(true);
                    else
                        uuu.setBanned(false);
                }catch(Exception jj){}
            }
        });
        chatEnabled.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    uuu.chatEnabled=chatEnabled.isSelected();
                }catch(Exception jj){}
            }
        });
        changep.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    String str=JOptionPane.showInputDialog("Enter new Password.");
                    if(str!=null && !str.equals(""))
                        uuu.setPassword(str);
                }catch(Exception jj){}
            }
        });
        cht.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    final String msg="ADMIN:"+cht.getText();                    
                    shr.server.chatHist+="<ADMIN> : "+cht.getText()+"\n";
                    shr.server.chat.chaat+="<ADMIN> : "+cht.getText()+"\n";
                    chat.setText(shr.server.chatHist);
                    cht.setText("");
                    new Thread(){
                        public void run()
                        {
                            shr.server.chat.interrupt();
                        }
                    }.start();
                }catch(Exception jj){}
            }
        });
        jf.pack();
        jf.setVisible(true);
    }
}