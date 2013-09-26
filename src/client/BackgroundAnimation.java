package client;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BackgroundAnimation extends JPanel {

  private CurvesPanel curves;

  public BackgroundAnimation() throws HeadlessException {
    //super("Stack Layout");

    buildContentPane();
    // buildDebugControls();

    startAnimation();

    //setSize(640, 400);
    //setLocationRelativeTo(null);

    //setDefaultCloseOperation(EXIT_ON_CLOSE);
  }

  private void startAnimation() {
    Timer timer = new Timer(50, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        curves.animate();
        curves.repaint();
      }
    });
    timer.start();
  }

  private void buildContentPane() {
    //JPanel pane = new JPanel();
    setOpaque(false);
    setLayout(new StackLayout());

    GradientPanel gradient = new GradientPanel();
    //chooser = new AvatarChooser();
    curves = new CurvesPanel();

    add(gradient, StackLayout.TOP);
    //add(chooser, StackLayout.TOP);
    add(curves, StackLayout.TOP);

  }

  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } catch (ClassNotFoundException ex) {
      ex.printStackTrace();
    } catch (IllegalAccessException ex) {
      ex.printStackTrace();
    } catch (InstantiationException ex) {
      ex.printStackTrace();
    } catch (UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
    }

    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        BackgroundAnimation tester = new BackgroundAnimation();
        tester.setVisible(true);
      }
    });
  }
}

class CurvesPanel extends JPanel {
  protected RenderingHints hints;

  protected int counter = 0;

  protected Color start = new Color(255, 255, 255, 200);

  protected Color end = new Color(255, 255, 255, 0);

  public CurvesPanel() {
    this(new BorderLayout());
  }

  public CurvesPanel(LayoutManager manager) {
    super(manager);
    hints = createRenderingHints();
  }

  protected RenderingHints createRenderingHints() {
    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    return hints;
  }

  public void animate() {
    counter++;
  }

  @Override
  public boolean isOpaque() {
    return false;
  }

  @Override
  protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    RenderingHints oldHints = g2.getRenderingHints();
    g2.setRenderingHints(hints);

    float width = getWidth();
    float height = getHeight();

    g2.translate(0, -30);

    drawCurve(g2, 20.0f, -10.0f, 20.0f, -10.0f, width / 2.0f - 40.0f, 10.0f, 0.0f, -5.0f,
        width / 2.0f + 40, 1.0f, 0.0f, 5.0f, 50.0f, 5.0f, false);

    g2.translate(0, 30);
    g2.translate(0, height - 60);

    drawCurve(g2, 30.0f, -15.0f, 50.0f, 15.0f, width / 2.0f - 40.0f, 1.0f, 15.0f, -25.0f,
        width / 2.0f, 1.0f / 2.0f, 0.0f, 25.0f, 15.0f, 9.0f, false);

    g2.translate(0, -height + 60);

    drawCurve(g2, height - 35.0f, -5.0f, height - 50.0f, 10.0f, width / 2.0f - 40.0f, 1.0f,
        height - 35.0f, -25.0f, width / 2.0f, 1.0f / 2.0f, height - 20.0f, 25.0f, 25.0f, 7.0f, true);

    g2.setRenderingHints(oldHints);
  }

  protected void drawCurve(Graphics2D g2, float y1, float y1_offset, float y2, float y2_offset,
      float cx1, float cx1_offset, float cy1, float cy1_offset, float cx2, float cx2_offset,
      float cy2, float cy2_offset, float thickness, float speed, boolean invert) {
    float width = getWidth();

    float offset = (float) Math.sin(counter / (speed * Math.PI));

    float start_x = 0.0f;
    float start_y = offset * y1_offset + y1;
    float end_x = width;
    float end_y = offset * y2_offset + y2;

    float ctrl1_x = offset * cx1_offset + cx1;
    float ctrl1_y = offset * cy1_offset + cy1;
    float ctrl2_x = offset * cx2_offset + cx2;
    float ctrl2_y = offset * cy2_offset + cy2;

    GeneralPath thickCurve = new GeneralPath();
    thickCurve.moveTo(start_x, start_y);
    thickCurve.curveTo(ctrl1_x, ctrl1_y, ctrl2_x, ctrl2_y, end_x, end_y);
    thickCurve.lineTo(end_x, end_y + thickness);
    thickCurve.curveTo(ctrl2_x, ctrl2_y + thickness, ctrl1_x, ctrl1_y + thickness, start_x, start_y
        + thickness);
    thickCurve.lineTo(start_x, start_y);

    Rectangle bounds = thickCurve.getBounds();
    if (!bounds.intersects(g2.getClipBounds())) {
      return;
    }

    GradientPaint painter = new GradientPaint(0, bounds.y, invert ? end : start, 0, bounds.y
        + bounds.height, invert ? start : end);

    Paint oldPainter = g2.getPaint();
    g2.setPaint(painter);
    g2.fill(thickCurve);

    g2.setPaint(oldPainter);
  }
}

class GradientPanel extends JPanel {
  protected BufferedImage gradientImage;

  protected Color gradientStart = Color.GRAY.brighter();//new Color(204, 249, 124);

  protected Color gradientEnd = Color.GRAY;//new Color(174, 222, 94);

  public GradientPanel() {
    this(new BorderLayout());
  }

  public GradientPanel(LayoutManager layout) {
    super(layout);
    addComponentListener(new GradientCacheManager());
  }

  @Override
  protected void paintComponent(Graphics g) {
    createImageCache();

    if (gradientImage != null) {
      g.drawImage(gradientImage, 0, 0, getWidth(), getHeight(), null);
    }
  }

  protected void createImageCache() {
    int width = 2;
    int height = getHeight();

    if (width == 0 || height == 0) {
      return;
    }

    if (gradientImage == null || width != gradientImage.getWidth()
        || height != gradientImage.getHeight()) {

      gradientImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

      Graphics2D g2 = gradientImage.createGraphics();
      GradientPaint painter = new GradientPaint(0, 0, gradientEnd, 0, height / 2, gradientStart);
      g2.setPaint(painter);

      Rectangle2D rect = new Rectangle2D.Double(0, 0, width, height / 2.0);
      g2.fill(rect);

      painter = new GradientPaint(0, height / 2, gradientStart, 0, height, gradientEnd);
      g2.setPaint(painter);

      rect = new Rectangle2D.Double(0, (height / 2.0) - 1.0, width, height);
      g2.fill(rect);

      g2.dispose();
    }
  }

  private void disposeImageCache() {
    synchronized (gradientImage) {
      gradientImage.flush();
      gradientImage = null;
    }
  }

  private class GradientCacheManager implements ComponentListener {
    public void componentResized(ComponentEvent e) {
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
      disposeImageCache();
    }
  }
}

class StackLayout implements LayoutManager2 {
  public static final String BOTTOM = "bottom";

  public static final String TOP = "top";

  private List<Component> components = new LinkedList<Component>();

  public void addLayoutComponent(Component comp, Object constraints) {
    synchronized (comp.getTreeLock()) {
      if (BOTTOM.equals(constraints)) {
        components.add(0, comp);
      } else if (TOP.equals(constraints)) {
        components.add(comp);
      } else {
        components.add(comp);
      }
    }
  }

  public void addLayoutComponent(String name, Component comp) {
    addLayoutComponent(comp, TOP);
  }

  public void removeLayoutComponent(Component comp) {
    synchronized (comp.getTreeLock()) {
      components.remove(comp);
    }
  }

  public float getLayoutAlignmentX(Container target) {
    return 0.5f;
  }

  public float getLayoutAlignmentY(Container target) {
    return 0.5f;
  }

  public void invalidateLayout(Container target) {
  }

  public Dimension preferredLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      int width = 0;
      int height = 0;

      for (Component comp : components) {
        Dimension size = comp.getPreferredSize();
        width = Math.max(size.width, width);
        height = Math.max(size.height, height);
      }

      Insets insets = parent.getInsets();
      width += insets.left + insets.right;
      height += insets.top + insets.bottom;

      return new Dimension(width, height);
    }
  }

  public Dimension minimumLayoutSize(Container parent) {
    synchronized (parent.getTreeLock()) {
      int width = 0;
      int height = 0;

      for (Component comp : components) {
        Dimension size = comp.getMinimumSize();
        width = Math.max(size.width, width);
        height = Math.max(size.height, height);
      }

      Insets insets = parent.getInsets();
      width += insets.left + insets.right;
      height += insets.top + insets.bottom;

      return new Dimension(width, height);
    }
  }

  public Dimension maximumLayoutSize(Container target) {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  public void layoutContainer(Container parent) {
    synchronized (parent.getTreeLock()) {
      int width = parent.getWidth();
      int height = parent.getHeight();

      Rectangle bounds = new Rectangle(0, 0, width, height);

      int componentsCount = components.size();

      for (int i = 0; i < componentsCount; i++) {
        Component comp = components.get(i);
        comp.setBounds(bounds);
        parent.setComponentZOrder(comp, componentsCount - i - 1);
      }
    }
  }
}