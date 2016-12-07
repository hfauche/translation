package org.iru.translation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.iru.translation.PreferencesException;
import org.iru.translation.PreferencesManager;
import org.iru.translation.TranslationException;
import org.iru.translation.model.PropertiesManager;
import org.iru.translation.model.PropertyTableModel;
import org.iru.translation.model.PropertyTableModel.Property;

public class Application extends JFrame implements ActionListener, Colors {

    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private final JButton fromOpenButton = new JButton("Choose from file");
    private final JButton toOpenButton = new JButton("Choose to file");
    private final JButton reloadButton = new JButton("Reload");
    private final JPanel files = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JLabel filesLabel = new JLabel("Files to compare: ");
    private final JLabel filesBetweenLabel = new JLabel(" <==> ");
    private final JLabel fromLabel = new JLabel("?");
    private final JLabel toLabel = new JLabel("?");
    private final JFileChooser fc = new JFileChooser();
    private final PropertiesManager propertiesManager = new PropertiesManager();
    private final PropertyTableModel tableModel = new PropertyTableModel();
    private final JTable table = new JTable(tableModel);
    private final JScrollPane jScrollPane = new JScrollPane(table);
    private final JPanel legendPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JLabel legendLabel = new JLabel("Legend: ");
    private final JLabel deletedLegend = new JLabel("  Deleted  ");
    private final JLabel addedLegend = new JLabel("  Added  ");
    private final JLabel untranslatedLegend = new JLabel("  Not translated  ");
    private final JPanel northPanel = new JPanel();
    private final JButton filterDeletedButton = new JButton("Show deleted");
    private final JButton filterAddedButton = new JButton("Show added");
    private final JButton filterUntranslatedButton = new JButton("Show untranslated");
    private File fromFile, toFile;
    private final PreferencesManager preferencesManager;

    private Application(PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
        
        //Create and set up the window.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Translation tool");
        table.setDefaultRenderer(Object.class, new PropertyCellRenderer());

        //Create and set up the content pane.
        mainPanel.setOpaque(true);
        setContentPane(mainPanel);

        fromOpenButton.addActionListener(this);
        toOpenButton.addActionListener(this);
        reloadButton.addActionListener(this);
        filterDeletedButton.addActionListener(this);
        filterAddedButton.addActionListener(this);
        filterUntranslatedButton.addActionListener(this);
        
        reloadButton.setEnabled(false);
        
        resetFilters();
        filterDeletedButton.setEnabled(false);
        filterAddedButton.setEnabled(false);
        filterUntranslatedButton.setEnabled(false);

        add(jScrollPane, BorderLayout.CENTER);

        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        add(northPanel, BorderLayout.NORTH);

        addLegendPanel();
        addFilesPanel();
        addToolbar();
        addCopyCapability();
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
                onExit();
            }
        });
    }

    private void resetFilters() {
        filterDeletedButton.setBackground(Color.LIGHT_GRAY);
        filterAddedButton.setBackground(Color.LIGHT_GRAY);
        filterUntranslatedButton.setBackground(Color.LIGHT_GRAY);
    }

    private void addLegendPanel() {
        Font font = addedLegend.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize());

        legendLabel.setFont(boldFont);

        addedLegend.setOpaque(true);
        addedLegend.setFont(boldFont);
        addedLegend.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        addedLegend.setBackground(ADDED_COLOR);
        
        deletedLegend.setOpaque(true);
        deletedLegend.setFont(boldFont);
        deletedLegend.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        deletedLegend.setBackground(DELETED_COLOR);
        
        untranslatedLegend.setOpaque(true);
        untranslatedLegend.setFont(boldFont);
        untranslatedLegend.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        untranslatedLegend.setBackground(UNTRANSLATED_COLOR);        
        legendPane.add(legendLabel);
        legendPane.add(deletedLegend);
        legendPane.add(addedLegend);
        legendPane.add(untranslatedLegend);
        northPanel.add(legendPane);
    }

    private void addFilesPanel() {
        Font font = filesLabel.getFont();
        Font boldFont = new Font(font.getFontName(), Font.BOLD, font.getSize()+5);
        files.add(filesLabel);
        fromLabel.setFont(boldFont);
        files.add(fromLabel);
        files.add(filesBetweenLabel);
        toLabel.setFont(boldFont);
        files.add(toLabel);
        northPanel.add(files, BorderLayout.NORTH);
    }

    private void addToolbar() {
        JMenuBar toolbar = new JMenuBar();
        toolbar.setOpaque(true);
        toolbar.setPreferredSize(new Dimension(200, 40));
        toolbar.add(fromOpenButton);
        toolbar.add(toOpenButton);
        toolbar.add(reloadButton);
        toolbar.add(filterDeletedButton);
        toolbar.add(filterAddedButton);
        toolbar.add(filterUntranslatedButton);
        setJMenuBar(toolbar);
    }

    private void addCopyCapability() {
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 67 && e.isControlDown()) {
                    Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                    final Property selectedProp = (Property)tableModel.getModel(table.getSelectedRow());
                    clpbrd.setContents(new StringSelection(selectedProp.getKey()), null);
                    e.consume();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == fromOpenButton) {
            final String prefFileDirectory = preferencesManager.getPreference(PreferencesManager.FILES_DIRECTORY);
            if (prefFileDirectory != null) {
                fc.setCurrentDirectory(new File(prefFileDirectory));
            }
            int returnVal = fc.showOpenDialog(Application.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Properties fromProps;
                File file = fc.getSelectedFile();
                fromLabel.setText("" + file.getName());
                fromFile = file;
                try {
                    fromProps = propertiesManager.readProperties(file);
                } catch (TranslationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    return;
                }
                tableModel.setModel(propertiesManager.loadProperties(fromProps), file.getName());
                preferencesManager.setPreference(PreferencesManager.FILES_DIRECTORY, file.getParent());
            }
        } else if (event.getSource() == toOpenButton) {
            final String prefFileDirectory = preferencesManager.getPreference(PreferencesManager.FILES_DIRECTORY);
            if (prefFileDirectory != null) {
                fc.setCurrentDirectory(new File(prefFileDirectory));
            }
            int returnVal = fc.showSaveDialog(Application.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                Properties fromProps, toProps;
                toFile = fc.getSelectedFile();
                toLabel.setText("" + toFile.getName());
                try {
                    fromProps = propertiesManager.readProperties(fromFile);
                    toProps = propertiesManager.readProperties(toFile);
                } catch (TranslationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    return;
                }
                tableModel.setModel(propertiesManager.diff(fromProps, toProps), fromFile.getName(), toFile.getName());
                filterDeletedButton.setEnabled(true);
                filterAddedButton.setEnabled(true);
                filterUntranslatedButton.setEnabled(true);
                reloadButton.setEnabled(true);
            }
        } else if (event.getSource() == reloadButton) {
            Properties fromProps, toProps;
            try {
                fromProps = propertiesManager.readProperties(fromFile);
                toProps = propertiesManager.readProperties(toFile);
            } catch (TranslationException ex) {
                JOptionPane.showMessageDialog(this, ex);
                return;
            }
            tableModel.setModel(propertiesManager.diff(fromProps, toProps));
        } else if (event.getSource() == filterDeletedButton) {
            tableModel.toggleFilterDeleted();
            filterDeletedButton.setBackground(tableModel.isFilterDeleted() ? DELETED_COLOR : Color.LIGHT_GRAY);
        } else if (event.getSource() == filterAddedButton) {
            tableModel.toggleFilterAdded();
            filterAddedButton.setBackground(tableModel.isFilterAdded() ? ADDED_COLOR : Color.LIGHT_GRAY);
        } else if (event.getSource() == filterUntranslatedButton) {
            tableModel.toggleFilterUntranslated();
            filterUntranslatedButton.setBackground(tableModel.isFilterUnstranslated()? UNTRANSLATED_COLOR : Color.LIGHT_GRAY);
        }
    }
    
    private void onExit() {
        try {
            preferencesManager.savePreferences();
        } catch (PreferencesException ex) {}
    }
    
    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI(PreferencesManager pm) {
        Application frame = new Application(pm);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);//center
        try {
            frame.setIconImage(ImageIO.read(frame.getClass().getResourceAsStream("/magician-rabbit-128.png")));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, ex);
            System.exit(0);
        }
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> {
            PreferencesManager pm = new PreferencesManager();
            try {
                pm.loadPreferences();
            } catch (PreferencesException ex) {
                JOptionPane.showMessageDialog(null, "Unable to load preferences");
                System.exit(0);
            }
            createAndShowGUI(pm);
        });
    }

}
