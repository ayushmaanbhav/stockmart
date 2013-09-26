package client;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UIManager;  
import javax.swing.UnsupportedLookAndFeelException;        
class LookAndFeel
{
    public static void set() 
    {
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName()); //com.sun.java.swing.plaf.windows.WindowsLookAndFeel
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        } 
    }
}