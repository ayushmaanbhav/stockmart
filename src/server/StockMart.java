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
    static JScrollPane jkps;
    static JTable usertable,comptable;
    static DefaultTableModel dtmu,dtmc;
    static JTextField cht,ucht,addmoney,tt;
    static JCheckBox chatEnabled;
    static JComboBox users;
    static JFrame jf;
    static String newsfeed="";
    static ShareMarket shr;
    static JButton server,post,changep,banu,checkRN;
    static String colusr[]=new String[]{"SrNo.","Company","Quantity","C.M.P.","Mkt Value","Gain/Loss","Status"};
    static String[] columnNames2 = {"Srno.","Company","C.M.P.","%Change","Low","High","SS","TSS","TS"};
    static DecimalFormat twoDForm = new DecimalFormat("#.##");
    static boolean started=false,checkRegNo=true;
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
                if(hrs<=0 && mins <=0)
                {
                    started=false;
                    server.setText("Start");
                    //shr.stop();
                    timer2.stop();
                    finalize1();
                }
            }catch(Exception bb){}
        }
    });
    static Timer timer=new Timer(1000,new ActionListener(){
        public void actionPerformed(ActionEvent e)
        {
            try{
                uuu=RegList.getUserWithName((String)users.getSelectedItem());
                userd.setText("<html><pre>User->RegNo: "+uuu.getRegNo()+";Name: "+uuu.getName()+";Password: "+uuu.getPassword()+"<br/>Money: "+twoDForm.format(uuu.getCurrentMoney())+";Chat: "+uuu.getChat()+"</pre></html>");
                //ucht.setText(uuu.getChat()+"");
                chatEnabled.setSelected(uuu.chatEnabled);
                if(!uuu.isBanned())
                    banu.setText("Ban User");
                else
                    banu.setText("Unban User");
                refreshUserTable(uuu);
            }catch(Exception n)
            { 
                users.removeItem((String)users.getSelectedItem());
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
            Object data[][]=new Object[shr.companies.size()][9];
            for(int i=0;i<shr.companies.size();i++)
            {
                data[i]=new Object[]{Integer.toString(i+1) , shr.companies.get(i).name , twoDForm.format(shr.companies.get(i).sharevalue.get(shr.companies.get(i).sharevalue.size()-1)) , twoDForm.format(shr.companies.get(i).perchange) , twoDForm.format(shr.companies.get(i).getLowest()) , twoDForm.format(shr.companies.get(i).getHighest()) , shr.companies.get(i).sharessoldpast , shr.companies.get(i).tsharessold , shr.companies.get(i).totalshares};
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
                            GraphPanel.main();
                        }
                        else
                        {
                            started=false;
                            server.setText("Start");
                            //shr.stop();
                            timer2.stop();
                            finalize1();
                            //GraphPanel.frame.setVisible(false);
                        }
                    }
                });
            }
        });
    }
    
    public static void finalize1()
    {
        final JDialog jd=new JDialog();
        jd.setUndecorated(true);
        final JComboBox j1 = new JComboBox(),j2 = new JComboBox(),j3 = new JComboBox();
        for(int i = RegList.userList.size()-1;i>=0;i--)
        {
            j1.addItem(RegList.userList.get(i).getName());
            j2.addItem(RegList.userList.get(i).getName());
            j3.addItem(RegList.userList.get(i).getName());
        }
        JPanel pan2 = new JPanel(new BorderLayout());
        pan2.add(j1, BorderLayout.NORTH);
        pan2.add(j2, BorderLayout.CENTER);
        pan2.add(j3, BorderLayout.SOUTH);
        JPanel pan = new JPanel(new BorderLayout());
        pan.add(pan2, BorderLayout.CENTER);
        JButton ok = new JButton("Ok"),can = new JButton("Cancel");
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                shr.server.sendMulti("rankings::"+j1.getSelectedItem()+":"+j2.getSelectedItem()+":"+j3.getSelectedItem());
                jd.setVisible(false);
            }
        });
        can.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                jd.setVisible(false);
            }
        });
        JPanel pan3 = new JPanel(new BorderLayout());
        pan3.add(can, BorderLayout.EAST);
        pan3.add(ok, BorderLayout.WEST);
        pan.add(pan3, BorderLayout.NORTH);
        jd.setContentPane(pan);
        jd.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        jd.pack();
        jd.setLocationRelativeTo(null);
        jd.setVisible(true);
    }
    
    public static void initialise()
    {
        LookAndFeel.set();
        jf=new JFrame("StockMart");
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
        ucht=new JTextField(10);
        addmoney = new JTextField(10);
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
                    {
                        new Thread(){
                            public void run()
                            {
                                try{
                                    if(JOptionPane.showConfirmDialog(jf,"Are you sure ?","Confirm Value Change",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                        shr.companies.get(row).futurevalues.add(0,Double.parseDouble(twoDForm.format(Double.valueOf(dtmc.getValueAt(row,2).toString()))));
                                }catch(Exception m)
                                {
                                    m.printStackTrace();
                                }
                            }
                        }.start();
                    }
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
        JScrollPane jspu=new JScrollPane(usertable,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane jspc=new JScrollPane(comptable);
        
        JPanel chatp=new JPanel(new BorderLayout());
        chatp.setPreferredSize(new Dimension(300,300));
        chatp.add(new JScrollPane(chat,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),BorderLayout.CENTER);
        chatp.add(cht,BorderLayout.SOUTH);
        JPanel rr=new JPanel(new BorderLayout());
        rr.setPreferredSize(new Dimension(300,250));
        jkps = new JScrollPane(ranks,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        rr.add(jkps,BorderLayout.CENTER);
        JPanel rank=new JPanel(new BorderLayout());
        rank.setPreferredSize(new Dimension(600,250));
        rank.add(chatp,BorderLayout.WEST);
        rank.add(rr,BorderLayout.EAST);
        
        changep=new JButton("Change Password");
        banu=new JButton("Ban User");
        JButton deleteu = new JButton("Delete User");
        
        JPanel jpaa=new JPanel();
        jpaa.setLayout(new FlowLayout(FlowLayout.LEFT));
        jpaa.add(changep);
        jpaa.add(banu);
        jpaa.add(deleteu);
        jpaa.add(ucht);
        jpaa.add(addmoney);
        
        JPanel jp=new JPanel();
        jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));  //
        //jp.setAlignmentX( Component.LEFT_ALIGNMENT );
        userd.setAlignmentX( Component.LEFT_ALIGNMENT );
        users.setAlignmentX( Component.LEFT_ALIGNMENT );
        jpaa.setAlignmentX( Component.LEFT_ALIGNMENT );
        chatEnabled.setAlignmentX( Component.LEFT_ALIGNMENT );
        jspu.setAlignmentX( Component.LEFT_ALIGNMENT );
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
        
        checkRN = new JButton("UNCHKRegNo");
        checkRN.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    checkRegNo = !checkRegNo;
                    if(checkRegNo)
                        checkRN.setText("UNCHKRegNo");
                    else
                        checkRN.setText("CHKRegNo");
                }catch(Exception jj){}
            }
        });
        
        JPanel uu3=new JPanel(new BorderLayout());
        uu3.add(checkRN,BorderLayout.WEST);
        uu3.add(server,BorderLayout.EAST);
        
        JPanel uu=new JPanel(new BorderLayout());
        uu.add(tt,BorderLayout.WEST);
        uu.add(sensex,BorderLayout.CENTER);
        uu.add(uu3,BorderLayout.EAST);
        
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
        addmoney.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new Thread(){
                    public void run()
                    {
                        try{
                            int response = JOptionPane.showConfirmDialog(jf,"For only this user: "+uuu.getName()+"(YES) or to all users(NO) or cancel(CANCEL) ?","Confirm Value Change",JOptionPane.YES_NO_CANCEL_OPTION);
                            if(response == JOptionPane.YES_OPTION)
                                uuu.setCurrentMoney(uuu.getCurrentMoney() + Integer.parseInt(addmoney.getText().trim()));
                            else if(response == JOptionPane.NO_OPTION)
                            {
                                int mm = Integer.parseInt(addmoney.getText().trim());
                                for(int l=0;l<RegList.userList.size();l++)
                                    RegList.userList.get(l).setCurrentMoney(RegList.userList.get(l).getCurrentMoney() + mm);
                            }
                        }catch(Exception m)
                        {
                            m.printStackTrace();
                        }
                    }
                }.start();
                try{
                    uuu.setCurrentMoney(uuu.getCurrentMoney() + Integer.parseInt(ucht.getText().trim()));
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
        deleteu.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                try{
                    RegList.deleteUser(uuu);
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