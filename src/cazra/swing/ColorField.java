package cazra.swing;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.LineBorder;

/** A textfield that functions as a color picker. It displays the selected color as its background and displays the color's hex value as its text. Clicking it displays a color chooser dialog. */
public class ColorField extends JTextField implements MouseListener {

    public Color color = null;
    
    public ColorField(Color initColor) {
        super(7);
        if(initColor != null)
            color = new Color(initColor.getRGB());
        this.setEditable(false);
        this.addMouseListener(this);
        updateColorFld();
    }
    
    public ColorField(int rgb) {
        this(new Color(rgb));
    }
    
    public ColorField() {
        this(null);
    }
    
    

    public void setColor(Color c) {
        this.color = c;
        updateColorFld();
    }
    
    
    
    /** Updates the color and text of colorFld to reflect color */
    public void updateColorFld() {
        if(color != null) {
            this.setText("0x" + Integer.toHexString(color.getRGB() & 0x00FFFFFF).toUpperCase());
            this.setBackground(color);
            
            int brightness = (color.getRed() + color.getBlue() + color.getGreen())/3;
            if(brightness < 100)
                this.setForeground(Color.WHITE);
            else
                this.setForeground(Color.BLACK);
        }
        else {
            this.setText("None");
            this.setBackground(new Color(0xEEEEEE));
            this.setForeground(Color.BLACK);
        }
    }

    
    
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        
        if(source == this) {
            Color result = JColorChooser.showDialog(this, "Choose Color", color);
            if(result != null) {
                color = result;
                updateColorFld();
            }
            postActionEvent();
        }
    }
   
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }
   
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }
   
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }
}

