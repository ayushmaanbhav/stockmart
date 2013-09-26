package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import user.*;

class RankingWindow extends JPanel
{
    JTextPane jta;
    public RankingWindow(Client cc)
    {
        setLayout(new BorderLayout());
        JLabel jlab=new JLabel("<<RANKINGS>>");
        jta=new JTextPane();
        StyledDocument doc = jta.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        jta.setFont(new Font("Lucida Sans Unicode",Font.BOLD,14));
        jta.setEditable(false);
        jta.setForeground(Color.red.darker());
        JScrollPane jsp=new JScrollPane(jta,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(jlab,BorderLayout.NORTH);
        add(jsp,BorderLayout.CENTER);
        RankingClient cser=new RankingClient(jta);
        cc.rc=cser;
    }
}