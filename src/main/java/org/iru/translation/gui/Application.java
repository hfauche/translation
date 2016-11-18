package org.iru.translation.gui;

import org.iru.translation.properties.PropertyModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.iru.translation.TranslationException;
import org.iru.translation.properties.PropertiesManager;

public class Application extends JPanel implements ActionListener {
    
    private final static JButton fromOpenButton = new JButton("Choose from file");
    private final static JButton toOpenButton = new JButton("Choose to file");
    private final static JButton diffButton = new JButton("Diff");
    private final JLabel fromLabel = new JLabel("From: -");
    private final JLabel toLabel = new JLabel("To: -");
    private final JFileChooser fc = new JFileChooser();
    private final PropertiesManager propertiesManager = new PropertiesManager();
    private final PropertyModel listModel = new PropertyModel();
    private final JList<String> list = new JList(listModel);
    private final JScrollPane jScrollPane = new JScrollPane(list);
    private Properties toProps, fromProps;

    public Application() {
        super(new BorderLayout());
        list.setFont(new Font("Courier", Font.PLAIN, 14));
        list.setCellRenderer(new PropertyListCellRender());
        JPanel files = new JPanel(new GridLayout(1, 2));
        files.add(fromLabel);
        files.add(toLabel);
        add(files, BorderLayout.NORTH);
        add(jScrollPane, BorderLayout.CENTER);
        
        addCopyCapability();
        manageButtons();

    }

    private void addCopyCapability() {
        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 67 && e.isControlDown()) {
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clpbrd.setContents(new StringSelection(listModel.getElementAt(list.getSelectedIndex()).getKey()), null);
                    e.consume();
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == fromOpenButton) {
            int returnVal = fc.showOpenDialog(Application.this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                fromLabel.setText("From: " + fc.getName(file));
                try {
                    fromProps = propertiesManager.readProperties(file);
                } catch (TranslationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    return;
                }
                List<PropertyModel.Property> result = new LinkedList<>();
                fromProps.entrySet().stream()
                    .forEach(p -> {
                            result.add(new PropertyModel.Property((String)p.getKey(), (String)p.getValue(), Action.NONE));
                    });
                Collections.sort(result);
                listModel.setValues(result);
            }
        } else if (event.getSource() == toOpenButton) {
            int returnVal = fc.showSaveDialog(Application.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                toLabel.setText("To: " + fc.getName(file));
                try {
                    toProps = propertiesManager.readProperties(file);
                } catch (TranslationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    return;
                }
            }
        } else if (event.getSource() == diffButton) {
            listModel.clear();
            listModel.setValues(propertiesManager.diff(fromProps, toProps));
        }
        manageButtons();
    }

   /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the 
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Translations Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        Application newContentPane = new Application();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
        
        fromOpenButton.addActionListener(newContentPane);
        toOpenButton.addActionListener(newContentPane);
        diffButton.addActionListener(newContentPane);

        JMenuBar toolbar = new JMenuBar();
        toolbar.setOpaque(true);
        toolbar.setPreferredSize(new Dimension(200, 40));
        toolbar.add(fromOpenButton);
        toolbar.add(toOpenButton);
        toolbar.add(diffButton);
 
        frame.setJMenuBar(toolbar);

        frame.setSize(700, 500);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);//center
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private void manageButtons() {
        diffButton.setEnabled(!(fromProps == null) && !(toProps == null));
    }
   

}
