package client;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.*;

public class GraphPanel extends JPanel {

    private int width = 800;
    private int heigth = 600;
    private int padding = 25;
    private int labelPadding = 25;
    private Color lineColor = new Color(44, 102, 230, 180);
    private Color pointColor = new Color(100, 100, 100, 180);
    private Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f);
    private int pointWidth = 4;
    private int numberYDivisions = 10;
    private static List<Company> score;
    private static List<Double> scores;

    public GraphPanel(List<Company> scores) {
        this.score = scores;
    }

    @Override
    protected void paintComponent(Graphics g) {
        for(int i=0;i<score.size();i++)
        {
            if(score.get(i).name.equals((String)comp.getSelectedItem()))
            {
                scores = score.get(i).sharevalue;
                break;
            }
        }
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        double xScale = ((double) getWidth() - (2 * padding) - labelPadding) / (scores.size() - 1);
        double yScale = ((double) getHeight() - 2 * padding - labelPadding) / (getMaxScore() - getMinScore());

        List<Point> graphPoints = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            int x1,y1;
            x1 = (int) (i * xScale + padding + labelPadding);
            y1 = (int) ((getMaxScore() - scores.get(i)) * yScale + padding);
            graphPoints.add(new Point(x1, y1));
        }

        // draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, getWidth() - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);

        // create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) {
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = getHeight() - ((i * (getHeight() - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
            if (scores.size() > 0) {
                g2.setColor(gridColor);
                g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, getWidth() - padding, y1);
                g2.setColor(Color.BLACK);
                String yLabel = ((int) ((getMinScore() + (getMaxScore() - getMinScore()) * ((i * 1.0) / numberYDivisions)) * 100)) / 100.0 + "";
                FontMetrics metrics = g2.getFontMetrics();
                int labelWidth = metrics.stringWidth(yLabel);
                g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            }
            g2.drawLine(x0, y0, x1, y1);
        }

        // and for x axis
        for (int i = 0; i < scores.size(); i++) {            
            if (scores.size()> 1) {
                int x0,x1,y0,y1;
                x0 = i * (getWidth() - padding * 2 - labelPadding) / (scores.size() - 1) + padding + labelPadding;
                x1 = x0;
                y0 = getHeight() - padding - labelPadding;
                y1 = y0 - pointWidth;
                if ((i % ((int) (((scores.size()) / 20.0)) + 1)) == 0) {
                    g2.setColor(gridColor);
                    g2.drawLine(x0, getHeight() - padding - labelPadding - 1 - pointWidth, x1, padding);
                    g2.setColor(Color.BLACK); 
                    String xLabel = i/2 + "m";
                    FontMetrics metrics = g2.getFontMetrics();
                    int labelWidth = metrics.stringWidth(xLabel);
                    if(i % 2 == 0)
                        g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
                }
                g2.drawLine(x0, y0, x1, y1);
            }
        }
        
        // create x and y axes 
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, getHeight() - padding - labelPadding, getWidth() - padding, getHeight() - padding - labelPadding);

        Stroke oldStroke = g2.getStroke();
        g2.setColor(lineColor);
        g2.setStroke(GRAPH_STROKE);
        for (int i = 0; i < graphPoints.size() - 1; i++) {
            int x1 = graphPoints.get(i).x;
            int y1 = graphPoints.get(i).y;
            int x2 = graphPoints.get(i + 1).x;
            int y2 = graphPoints.get(i + 1).y;
            g2.drawLine(x1, y1, x2, y2);
        }

        g2.setStroke(oldStroke);
        g2.setColor(pointColor);
        for (int i = 0; i < graphPoints.size(); i++) {
            if(i >= scores.size())
                g2.setColor(Color.RED);
            int x = graphPoints.get(i).x - pointWidth / 2;
            int y = graphPoints.get(i).y - pointWidth / 2;
            int ovalW = pointWidth;
            int ovalH = pointWidth;
            g2.fillOval(x, y, ovalW, ovalH);
        }
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return new Dimension(width, heigth);
//    }
    private double getMinScore() {
        double minScore = Double.MAX_VALUE;
        for (Company score1 : score) {
            for (Double score2 : score1.sharevalue) {
                minScore = Math.min(minScore, score2);
            }
        }
        return 0;
    }

    private double getMaxScore() {
        double maxScore = Double.MIN_VALUE;
        for (Company score1 : score) {
            for (Double score2 : score1.sharevalue) {
                maxScore = Math.max(maxScore, score2);
            }
        }
        return maxScore;
    }

    public void setScores(List<Company> score) {
        this.score = score;
        comp.removeAllItems();
        for(int i=0;i<score.size();i++)
            comp.addItem(score.get(i).name);
        try{
            comp.setSelectedIndex(0);
            scores = score.get(0).sharevalue;
        }catch(Exception o){}
        invalidate();
        this.repaint();
    }

    public List<Double> getScores() {
        return scores;
    }
    
    static JComboBox comp;
    static JPanel frame;
    static GraphPanel mainPanel;
    static Client client;
    static Company comm = null;
    public static JPanel main(Client cl) {
            client = cl;
            comp=new JComboBox();
            comp.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e)
                {
                    comp.setEnabled(false);
                    for(int i=0;i<score.size();i++)
                    {
                        if(score.get(i).name.equals((String)comp.getSelectedItem()))
                        {
                            comm = score.get(i);
                            break;
                        }
                    }
                    if(comm.yy==0)
                    {
                    new Thread(){
                        public void run()
                        {
                            List<Double> ret = client.getHistory(comm.name,comm.sharevalue.size());
                            if(ret != null)
                            {   
                                if(ret.size() > 0)
                                    comm.sharevalue = ret;
                                SwingUtilities.invokeLater(new Runnable()
                                {
                                    public void run()
                                    {
                                        mainPanel.repaint();
                                        comp.setEnabled(true);
                                        comp.requestFocus();
                                    }
                                });
                            }
                        }
                    }.start();
                    comm.yy=1;
                    }
                    else
                    {
                        mainPanel.repaint();
                        comp.setEnabled(true);
                    }
                }
            });
            comp.setLightWeightPopupEnabled(false);
            mainPanel = new GraphPanel(null);
            mainPanel.setScores(Companies.comp);
            //mainPanel.setPreferredSize(new Dimension(800, 500));
            //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame = new JPanel();
            frame.setLayout(new BorderLayout());
            frame.add(comp, BorderLayout.NORTH);
            frame.add(mainPanel, BorderLayout.CENTER);
            return frame;
   }
}