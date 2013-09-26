package client;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.text.*;
public class BroadcastClient extends Thread
{
    StringBuffer sb;
    DecimalFormat twoDForm;
    ShareValuesChangeListener svcl;
    public BroadcastClient(StringBuffer s)
    {
        sb=s;
        svcl=null;
        Companies.comp=new ArrayList<Company>();
        twoDForm = new DecimalFormat("#.##");
    }
    public void run(String poo)
    {
        try{
                StringBuffer fin=new StringBuffer();
                try{
                    final String rec[]=poo.split("=");
                    final String mtr[]=rec[0].split(";");
                    final String str[]=rec[1].split(";");
                    final List<Company> clist=new ArrayList<Company>();
                    for(int i=0;i<str.length;i++)
                    {
                        String str2[]=str[i].split(":");
                        fin.append(str2[1]);
                        fin.append(" : ");
                        double a=Double.parseDouble(str2[2]);
                        fin.append(twoDForm.format(a));
                        double b=Double.parseDouble(str2[3]);
                        double h=Double.parseDouble(str2[4]);
                        double l=Double.parseDouble(str2[5]);
                        clist.add(new Company(new String(str2[1]),a,b,h,l,Integer.parseInt(new String(str2[0]))));
                        if(a>b)
                            fin.append("\u25b2");  //<font size="3" color="red">This is some text!</font>
                        else if(a<b)
                            fin.append("\u25bc");
                        double c=a-b;
                        if(c>0.0)
                            fin.append(twoDForm.format(c));
                        else if(c<0.0)
                            fin.append(twoDForm.format(-c));
                        fin.append("        ");
                    }
                    sb.delete(0,sb.length());
                    sb.append(fin.toString());
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run()
                        {
                            try{
                                if(Main.imglabel2!=null)
                                    Main.imglabel2.setText("<html><pre><font color=\"white\">Sensex: "+mtr[1]+"<br/>Time Left: "+mtr[0]+"</font></pre></html>");
                            }catch(Exception bb){bb.printStackTrace();}
                            try{
                                svcl.valuesChanged();
                                for(int i=0;i<clist.size();i++)
                                {
                                    Companies.updateValues(clist.get(i).id,clist.get(i).name,clist.get(i).mktvalue,clist.get(i).inivalue,clist.get(i).high,clist.get(i).low);
                                }
                            }catch(Exception e){}
                        }
                    });
                }catch(Exception mm){}
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
