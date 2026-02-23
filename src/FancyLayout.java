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
    public static class UDim2 {
        public double posX, posY;
        public double sizeX, sizeY;
        
        public double anchorX = 0.5, anchorY = 0.5; 
        public double textScale = 0.75; 

        public UDim2(double posX, double posY, double sizeX, double sizeY) {
            this.posX = posX;
            this.posY = posY;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }

        public UDim2 setAnchor(double aX, double aY) {
            this.anchorX = aX;
            this.anchorY = aY;
            return this;
        }

        public UDim2 setTextScale(double tScale) {
            this.textScale = tScale;
            return this;
        }
    }

    private final Map<Component, UDim2> constraints = new HashMap<>();

    @Override
    public void addLayoutComponent(Component comp, Object constraint) {
        if (constraint instanceof UDim2) {
            constraints.put(comp, (UDim2) constraint);
        }
    }

    @Override
    public void layoutContainer(Container parent) {
        int pWidth = parent.getWidth();
        int pHeight = parent.getHeight();

        for (Component comp : parent.getComponents()) {
            UDim2 udim = constraints.get(comp);
            if (udim != null) {

                // size
                int width = (int)(pWidth * udim.sizeX);
                int height = (int)(pHeight * udim.sizeY);

                // position
                int x = (int)(pWidth*udim.posX - width*udim.anchorX);
                int y = (int)(pHeight*udim.posY - height*udim.anchorY);

                comp.setBounds(x, y, width, height);

                // scaling font
                if (udim.textScale > 0 && height > 0) {
                    Font currentFont = comp.getFont();
                    if (currentFont != null) {
                        float newSize = Math.max(1f, (float)(height * udim.textScale)); 
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