package src;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

public class FancyLayout implements LayoutManager2 {

    // --- SADELEŞTİRİLMİŞ UDim2 ---
    public static class Constraints {
        public double posX, posY;
        public double sizeX, sizeY;
        public double anchorX = 0.5, anchorY = 0.5; 
        public double textScale = 0.75; 
        public double aspectRatio = 0;

        public Constraints(double posX, double posY, double sizeX, double sizeY) {
            this.posX = posX;
            this.posY = posY;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }

        public Constraints(double posX, double posY, double sizeX, double sizeY, double aspectRatio) {
            this(posX,posY,sizeX,sizeY);
            this.aspectRatio = aspectRatio;
        }

        public Constraints setAnchor(double aX, double aY) {
            this.anchorX = aX;
            this.anchorY = aY;
            return this;
        }

        public Constraints setTextScale(double tScale) {
            this.textScale = tScale;
            return this;
        }

        public Constraints setAspectRatio(double ratio) {
            this.aspectRatio = ratio;
            return this;
        }
    }

    private final Map<Component, Constraints> constraints = new HashMap<>();

    @Override
    public void addLayoutComponent(Component comp, Object constraint) {
        if (constraint instanceof Constraints) {
            constraints.put(comp, (Constraints) constraint);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        int pWidth = parent.getWidth();
        int pHeight = parent.getHeight();

        for (Component comp : parent.getComponents()) {
            Constraints con = constraints.get(comp);
            if (con != null) {

                // size
                int width = (int)(pWidth * con.sizeX);
                int height = (int)(pHeight * con.sizeY);

                // aspect ratio
                if (con.aspectRatio > 0) {
                    height = (int)(width / con.aspectRatio);
                    int maxHeight = (int)(pHeight * con.sizeY);
                    if (height > maxHeight) {
                        height = maxHeight;
                        width = (int)(height * con.aspectRatio);
                    }
                }

                // position
                int x = (int)(pWidth*con.posX - width*con.anchorX);
                int y = (int)(pHeight*con.posY - height*con.anchorY);

                comp.setBounds(x, y, width, height);

                // scaling font
                if (con.textScale > 0 && height > 0) {
                    Font currentFont = comp.getFont();
                    if (currentFont != null) {
                        float newSize = Math.max(1f, (float)(height * con.textScale)); 
                        if (Math.abs(currentFont.getSize2D() - newSize) > 0.5f) {
                            comp.setFont(currentFont.deriveFont(newSize));
                        }
                    }
                }
            }
        }
    }

    // --- ZORUNLU BOŞ METODLAR ---
    @Override public void addLayoutComponent(String name, Component comp) {}
    @Override public void removeLayoutComponent(Component comp) { constraints.remove(comp); }
    @Override public Dimension preferredLayoutSize(Container parent) { return new Dimension(800, 600); }
    @Override public Dimension minimumLayoutSize(Container parent) { return new Dimension(100, 100); }
    @Override public Dimension maximumLayoutSize(Container target) { return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE); }
    @Override public float getLayoutAlignmentX(Container target) { return 0.5f; }
    @Override public float getLayoutAlignmentY(Container target) { return 0.5f; }
    @Override public void invalidateLayout(Container target) {}
}