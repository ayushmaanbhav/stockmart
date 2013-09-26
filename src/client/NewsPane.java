package client;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.*;
class NewsPane extends JPanel implements ActionListener
{
    static StringBuffer temp;
    StringBuffer sb;
    Timer t;
    int loop;
    JLabel l;
    public NewsPane()
    {
        sb=new StringBuffer("");
        temp=new StringBuffer("");
        t=new Timer(140,this);
        setOpaque(false);
        setLayout(new BorderLayout());
        l=new JLabel("<html><pre><font face=\"arial\" color=\"red\">News : </font></pre></html>");
        l.setFont(new Font("Arial",Font.BOLD,13));
        l.setOpaque(false);
        setSize(getWidth(),30);
        add(l,BorderLayout.CENTER);
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
    }
    void stopAnimation()
    {
        loop=0;
        t.stop();
    }
    public void actionPerformed(ActionEvent e)
    {
        if(sb.length()>200)
        {
            l.setText("<html><pre><font face=\"arial\" color=\"red\">News : </font><font face=\"arial\" color=\"black\">"+sb.substring(0,200)+"</font></pre></html>");
            sb.append(sb.charAt(0));
            sb.delete(0,1);
            loop++;
            if(loop>sb.length())
            {
                sb=new StringBuffer(temp);
                loop=0;
            }
        }
        else
        {
            sb=new StringBuffer(temp);
            loop=0;
        }
    }
}