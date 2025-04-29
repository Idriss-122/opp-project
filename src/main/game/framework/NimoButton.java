package framework;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

class NimoButton extends JButton {
    private Color buttonColor;
    private int borderRadius;



    public NimoButton(String text, Color buttonColor, int borderRadius ,ActionListener actlisner ) {
        super(text);
        this.buttonColor = buttonColor;
        this.borderRadius = borderRadius;
        this.addActionListener(actlisner);
        // Make the button transparent to show our custom painting
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(true);
        setOpaque(false);

        // Default font and foreground
        setFont(new Font("Arial", Font.BOLD, 14));
        setForeground(Color.BLACK);

        // Add a default action listener

    }


    public Color getButtonColor() {
        return buttonColor;
    }

    public int getBorderRadius() {
        return borderRadius;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Paint the rounded background
        if (getModel().isPressed()) {
            g2.setColor(buttonColor.brighter());
        } else if (getModel().isRollover()) {
            g2.setColor(buttonColor.darker());
        } else {
            g2.setColor(buttonColor);
        }

        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                borderRadius, borderRadius));

        g2.dispose();

        // Paint the text and other components
        super.paintComponent(g);

    }

    public void setButtonColor(Color color) {
        this.buttonColor = color;
        repaint();
    }



    public void setBorderRadius(int radius) {
        this.borderRadius = radius;
        repaint();
    }


}
