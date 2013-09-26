package client;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
class RibbonPane extends JPanel implements ActionListener
{
    StringBuffer sb,temp;
    BroadcastClient bc;
    Timer t;
    int loop;
    JLabel l;
    final String wel="                                                              StockMart                                                                                                                            Anwesha'13                                                             ";
    public RibbonPane(int tm,Client cc)
    {
        sb=new StringBuffer(wel);
        temp=new StringBuffer(wel);
        bc=new BroadcastClient(temp);
        cc.bc=bc;
        t=new Timer(tm,this);
        setOpaque(false);
        l=new JLabel(wel);
        l.setFont(new Font("Arial",Font.BOLD,13));
        l.setOpaque(false);
        add(l);
    }
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2d = (Graphics2D) grphcs;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0,getBackground().brighter(), 0, getHeight(),getBackground().darker());
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }
    void startAnimation()
    {
        loop=0;
        t.start();
        //bc.start();
    }
    void stopAnimation()
    {
        loop=0;
        t.stop();
        //bc.interrupt();
    }
    public void actionPerformed(ActionEvent e)
    {
        if(sb.length()>260)
        {
            l.setText("<html><pre><font face=\"arial\">"+sb.substring(0,260).replace("\u25b2","</font><font face=\"arial\" color=\"green\">\u25b2</font><font face=\"arial\">").replace("\u25bc","</font><font face=\"arial\" color=\"red\">\u25bc</font><font face=\"arial\">")+"</font><pre></html>");
            sb.append(sb.charAt(0));
            sb.delete(0,1);
            loop++;
            if(loop>sb.length())
            {
                sb=new StringBuffer(temp);
                loop=0;
            }
        }
    }
    void addShareValuesChangeListener(ShareValuesChangeListener svcl)
    {
        bc.svcl=svcl;
    }
}