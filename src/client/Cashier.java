package client;
import user.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.text.*;
import com.sun.awt.AWTUtilities;

class Cashier
{
    JDialog jd;
    float ii=0;
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    Timer alphaChanger = new Timer(50,new ActionListener() {
        private float incrementer = .10f;
        @Override
        public void actionPerformed(ActionEvent e)
        {
            ii=ii+incrementer;
            if(ii>((float)0.9))
            {   
                alphaChanger.stop();
                jd.setVisible(false);
                ii=0;
            }
            try{
                AWTUtilities.setWindowOpacity(jd,Float.valueOf((float)(1-ii)));
            }catch(Exception m){}
        }
    });
    Client client;
    User user;
    JLabel cash,jlab,tot;
    double total=0;
    Company comp;
    JTextField jtf;
    Shares pp;
    String cmp;
    JRadioButton b2,b1;
    static boolean closee=false;
    Timer timer = new Timer(250,new ActionListener() {
        public void actionPerformed(ActionEvent e)
        {
            if(closee)
            {
                timer.stop();
                jd.setVisible(false);
                closee=false;
            }
            comp=Companies.getCompanyWithName(cmp);
            cash.setText("Cash Avail : "+twoDForm.format(user.getCurrentMoney()));
            jlab.setText("Company : "+cmp+" ,        C.M.P. : "+twoDForm.format(comp.mktvalue));
            try
            {
                int ent=Integer.parseInt(jtf.getText());
                if(b1.isSelected())
                {
                    total=Double.valueOf(twoDForm.format(ent*comp.mktvalue*1.02));
                    if(total>user.getCurrentMoney())
                        tot.setForeground(Color.red);
                    else
                        tot.setForeground(Color.black);
                }
                else
                if(b2.isSelected())
                {
                    total=Double.valueOf(twoDForm.format(ent*comp.mktvalue*0.98));
                    if(ent>pp.qty)
                        tot.setForeground(Color.red);
                    else
                        tot.setForeground(Color.black);
                }
            }catch(Exception v)
            {
                total=0;
                tot.setForeground(Color.red);
            }
            tot.setText("Total Cash Req./Rec.*: "+twoDForm.format(total));
        }
    });
    public Cashier(Client c,User u)
    {
        client=c;
        user=u;
    }
    void showDialog(String c,final int bs,final Shares shr)
    {
        cmp=c;
        pp=shr;
        jd=new JDialog();
        jd.setUndecorated(true);
        comp=Companies.getCompanyWithName(cmp);
        JPanel pan=new JPanel(){
            @Override
            protected void paintComponent(Graphics grphcs) {
                Graphics2D g2d = (Graphics2D) grphcs;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,getBackground().brighter(), 0, getHeight(),getBackground().darker());
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(grphcs);
            }
        };
        pan.setLayout(new GridBagLayout());
        jlab=new JLabel("Company : "+cmp+" ,        C.M.P. : "+twoDForm.format(comp.mktvalue));
        JLabel op=new JLabel("Operation : ");
        tot=new JLabel("Total Cash Req./Rec.:"+twoDForm.format(total));
        b1=new JRadioButton("Buy");
        b2=new JRadioButton("Sell");
        if(bs==0)
            b1.setSelected(true);
        else
            b2.setSelected(true);
        ButtonGroup grp=new ButtonGroup();
        grp.add(b1);grp.add(b2);
        final JLabel qty=new JLabel("Quantity : ");
        jtf=new JTextField(10);
        jtf.setText("0");
        cash=new JLabel("Cash Avail : "+twoDForm.format(user.getCurrentMoney()));
        final JButton jb1=new JButton("Place Order");
        final JButton jb2=new JButton("Cancel");
        jb2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                timer.stop();
                jd.setVisible(false);
            }
        });
        jb1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e)
            {
                int noofs=0;
                try{
                    noofs=Integer.parseInt(jtf.getText());
                }catch(Exception ee){
                    jtf.setForeground(Color.red);
                    qty.setForeground(Color.red);
                    return;
                }
                if(noofs<=0)
                {
                    jtf.setForeground(Color.red);
                    qty.setForeground(Color.red);
                    return;
                }
                if(b2.isSelected() && noofs>shr.qty)
                {
                    jtf.setForeground(Color.red);
                    qty.setForeground(Color.red);
                    return;
                }
                if(b1.isSelected() && noofs*comp.mktvalue>user.getCurrentMoney())
                {
                    jtf.setForeground(Color.red);
                    qty.setForeground(Color.red);
                    return;
                }
                jtf.setForeground(Color.black);
                qty.setForeground(Color.black);
                tot.setForeground(Color.black);
                jtf.setEnabled(false);
                jb1.setEnabled(false);
                jb2.setEnabled(false);
                b1.setEnabled(false);
                b2.setEnabled(false);
                final int kk=noofs; 
                new Thread(){
                    public void run(){
                        String cmd;
                        if(b1.isSelected())
                            cmd="buy";
                        else
                            cmd="sell";
                        User usr;
                        try{
                            client.placeOrder(user,cmd,comp,kk,shr.id);
                        }catch(Exception ff)
                        {
                            client.placeOrder(user,cmd,comp,kk,-1);
                        }
                        //user.changeData(usr);
                        timer.stop();
                        alphaChanger.start();
                    }
                }.start();
            }
        });
        JLabel cc1=new JLabel("-> *Transaction charges : 2% of the total cash transactioned.");
        JLabel cc2=new JLabel("-> Order will be executed 30-40 secs after it is placed.");
        JLabel cc3=new JLabel("-> Order can be canceled within 10 secs after it is placed.");
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(jlab,gbc);
        gbc.gridx=0;
        gbc.gridy=1;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(op,gbc);
        gbc.gridx=1;
        gbc.gridy=1;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(b1,gbc);
        gbc.gridx=2;
        gbc.gridy=1;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(b2,gbc);
        gbc.gridx=0;
        gbc.gridy=2;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(qty,gbc);
        gbc.gridx=1;
        gbc.gridy=2;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=2;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(jtf,gbc);
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(tot,gbc);
        gbc.gridx=0;
        gbc.gridy=4;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(cash,gbc);
        gbc.gridx=0;
        gbc.gridy=5;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(jb1,gbc);
        gbc.gridx=1;
        gbc.gridy=5;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=1;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(jb2,gbc);
        gbc.gridx=0;
        gbc.gridy=6;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(cc1,gbc);
        gbc.gridx=0;
        gbc.gridy=7;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(cc2,gbc);
        gbc.gridx=0;
        gbc.gridy=8;
        gbc.fill=GridBagConstraints.NONE;
        gbc.gridwidth=3;
        gbc.gridheight=1;
        gbc.ipadx=0;
        gbc.ipady=0;
        gbc.anchor=GridBagConstraints.LINE_START;
        //gbc.insets=new Insets(0,0,100,0);
        pan.add(cc3,gbc);
        pan.setOpaque(false);
        pan.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.black, 1),BorderFactory.createEmptyBorder(5,5,5,5)));
        jd.setContentPane(pan);
        timer.start();
        jd.setModalityType(JDialog.ModalityType.APPLICATION_MODAL);
        jd.pack();
        jd.setLocationRelativeTo(null);
        jd.setVisible(true);
    }
}