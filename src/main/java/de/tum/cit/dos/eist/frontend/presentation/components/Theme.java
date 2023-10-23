package de.tum.cit.dos.eist.frontend.presentation.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import de.tum.cit.dos.eist.frontend.BeUnrealApp;

public class Theme {
    public static String fontName = "Infer";
    public static String appName = "BeUnreal.";
    public static Color backgroundColor = Color.BLACK;

    public static void setUIFont(FontUIResource f) {
        Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource orig = (FontUIResource) value;
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }

    public static void setAppIcon(JFrame frame) {
        ImageIcon imageIcon = new ImageIcon(BeUnrealApp.class.getResource("/images/be-unreal-logo.png"));
        Image image = imageIcon.getImage();

        boolean isMacOs = System.getProperty("os.name").toLowerCase().contains("mac");
        if (isMacOs) {
            // For macOS we need to set the icon in a different way.
            // See: https://stackoverflow.com/a/72970139/8358501
            modifyImageForMacOs(image);
        }

        frame.setIconImage(image);
    }

    static public void modifyImageForMacOs(Image image) {
        try {
            Class<?> taskbar = Class.forName("java.awt.Taskbar");
            Method getTaskbar = taskbar.getDeclaredMethod("getTaskbar");
            Object instance = getTaskbar.invoke(taskbar);
            Method setIconImage = instance.getClass().getDeclaredMethod("setIconImage", Image.class);
            setIconImage.invoke(instance, image);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
