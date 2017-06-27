package org.iru.translation.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
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
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.iru.translation.DictionnaryManager;
import org.iru.translation.PreferencesException;
import org.iru.translation.PreferencesManager;
import org.iru.translation.TranslationException;
import org.iru.translation.io.ConfigurationUpdater;
import org.iru.translation.model.PropertiesManager;
import org.iru.translation.model.Property;
import org.iru.translation.model.PropertyTableModel;

public class Application extends JFrame implements ActionListener, Colors {

    private final JPanel mainPanel = new JPanel(new BorderLayout());
    private final JButton fromOpenButton = new JButton("Choose FROM file");
    private final JButton toOpenButton = new JButton("Choose TO file");
    private final JButton reloadButton = new JButton("Reload");
    private final JButton exportButton = new JButton("Export");    
    private final JButton importButton = new JButton("Import");
    private final JButton saveButton = new JButton("Save");
    private final JPanel files = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JLabel filesLabel = new JLabel("Files to compare: ");
    private final JLabel filesBetweenLabel = new JLabel(" <==> ");
    private final JLabel fromLabel = new JLabel("?");
    private final JLabel toLabel = new JLabel("?");
    private final JFileChooser fc = new JFileChooser();
    private final PropertyTableModel tableModel = new PropertyTableModel();
    private final JTable table = new JTable(tableModel);
    private final JScrollPane jScrollPane = new JScrollPane(table);
    private final JPanel legendPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JLabel legendLabel = new JLabel("Legend: ");
    private final JLabel deletedLegend = new JLabel("  Missing  ");
    private final JLabel addedLegend = new JLabel("  Unused  ");
    private final JLabel untranslatedLegend = new JLabel("  Not translated  ");
    private final JPanel northPanel = new JPanel();
    private final JButton filterDeletedButton = new JButton("Show missing");
    private final JButton filterAddedButton = new JButton("Show unused");
    private final JButton filterUntranslatedButton = new JButton("Show untranslated");
    private File fromFile, toFile;
    private FileBasedConfigurationBuilder<PropertiesConfiguration> fromBuilder, toBuilder;
    private final PreferencesManager preferencesManager;
    private final PropertiesManager propertiesManager;
    private ConfigurationUpdater configurationUpdater;

    private Application(PreferencesManager preferencesManager, PropertiesManager propertiesManager) {
        this.preferencesManager = preferencesManager;
        this.propertiesManager = propertiesManager;
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

    private void addToolbars() {
        JPanel actionToolbar = new JPanel();
        actionToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionToolbar.setBackground(MAIN_COLOR);
        actionToolbar.add(fromOpenButton);
        actionToolbar.add(toOpenButton);
        actionToolbar.add(reloadButton);
        actionToolbar.add(exportButton);
        actionToolbar.add(importButton);
        actionToolbar.add(saveButton);
        JPanel filterToolbar = new JPanel();
        filterToolbar.setBackground(MAIN_COLOR);
        filterToolbar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        filterToolbar.add(filterDeletedButton);
        filterToolbar.add(filterAddedButton);
        filterToolbar.add(filterUntranslatedButton);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(MAIN_COLOR);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(actionToolbar);
        buttonPanel.add(filterToolbar);
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(MAIN_COLOR);
        menuBar.add(buttonPanel);
        setJMenuBar(menuBar);
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
                Configuration fromProps;
                File file = fc.getSelectedFile();
                fromLabel.setText("" + file.getName());
                fromFile = file;
                try {
                    fromBuilder =  propertiesManager.getPropertiesBuilder(file);
                    fromProps = fromBuilder.getConfiguration();
                } catch (ConfigurationException | TranslationException ex) {
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
                PropertiesConfiguration fromProps, toProps;
                toFile = fc.getSelectedFile();
                toLabel.setText("" + toFile.getName());
                try {
                    fromProps = fromBuilder.getConfiguration();
                    toBuilder = propertiesManager.getPropertiesBuilder(toFile);
                    toProps = toBuilder.getConfiguration();
                } catch (ConfigurationException | TranslationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    return;
                }
                configurationUpdater = new ConfigurationUpdater(toProps);
                tableModel.setModel(propertiesManager.diff(fromProps, toProps, configurationUpdater), fromFile.getName(), toFile.getName());
                filterDeletedButton.setEnabled(true);
                filterAddedButton.setEnabled(true);
                filterUntranslatedButton.setEnabled(true);
                exportButton.setEnabled(true);
                importButton.setEnabled(true);
                reloadButton.setEnabled(true);
                saveButton.setEnabled(true);
            }
        } else if (event.getSource() == reloadButton) {
            PropertiesConfiguration fromProps, toProps;
            try {
                try {
                    fromBuilder =  propertiesManager.getPropertiesBuilder(fromFile);
                    toBuilder = propertiesManager.getPropertiesBuilder(toFile);
                } catch (TranslationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                    return;
                }
                fromProps = fromBuilder.getConfiguration();
                toProps = toBuilder.getConfiguration();
            } catch (ConfigurationException ex) {
                JOptionPane.showMessageDialog(this, ex);
                return;
            }
            configurationUpdater = new ConfigurationUpdater(toProps);
            tableModel.setModel(propertiesManager.diff(fromProps, toProps, configurationUpdater));
        } else if (event.getSource() == saveButton) {
            try {
                toBuilder.save();
            } catch (ConfigurationException ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        } else if (event.getSource() == filterDeletedButton) {
            tableModel.toggleFilterDeleted();
            filterDeletedButton.setBackground(tableModel.isFilterDeleted() ? DELETED_COLOR : Color.LIGHT_GRAY);
        } else if (event.getSource() == filterAddedButton) {
            tableModel.toggleFilterAdded();
            filterAddedButton.setBackground(tableModel.isFilterAdded() ? ADDED_COLOR : Color.LIGHT_GRAY);
        } else if (event.getSource() == filterUntranslatedButton) {
            tableModel.toggleFilterUntranslated();
            filterUntranslatedButton.setBackground(tableModel.isFilterUnstranslated()? UNTRANSLATED_COLOR : Color.LIGHT_GRAY);
        } else if (event.getSource() == exportButton) {
            try {
                propertiesManager.export(tableModel);
            } catch (TranslationException ex) {
                JOptionPane.showMessageDialog(this, ex);
            }
        } else if (event.getSource() == importButton) {
            int returnVal = fc.showOpenDialog(Application.this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    propertiesManager.importFromCsv(toBuilder.getConfiguration(), fc.getSelectedFile());
                    Configuration fromProps = fromBuilder.getConfiguration();
                    Configuration toProps = toBuilder.getConfiguration();
                    tableModel.setModel(propertiesManager.diff(fromProps, toProps, configurationUpdater));
                } catch (TranslationException | ConfigurationException ex) {
                    JOptionPane.showMessageDialog(this, ex);
                }
            }
        }
    }
    
    private void onExit() {
        preferencesManager.savePreferences();
    }
    
    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Translation tool");
        this.setBackground(MAIN_COLOR);
        table.setDefaultRenderer(Object.class, new PropertyCellRenderer());

        mainPanel.setOpaque(true);
        setContentPane(mainPanel);

        fromOpenButton.addActionListener(this);
        toOpenButton.addActionListener(this);
        reloadButton.addActionListener(this);
        exportButton.addActionListener(this);
        importButton.addActionListener(this);
        saveButton.addActionListener(this);
        filterDeletedButton.addActionListener(this);
        filterAddedButton.addActionListener(this);
        filterUntranslatedButton.addActionListener(this);
        
        reloadButton.setEnabled(false);
        saveButton.setEnabled(false);
        exportButton.setEnabled(false);
        importButton.setEnabled(false);
        
        resetFilters();
        filterDeletedButton.setEnabled(false);
        filterAddedButton.setEnabled(false);
        filterUntranslatedButton.setEnabled(false);

        add(jScrollPane, BorderLayout.CENTER);

        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        add(northPanel, BorderLayout.NORTH);

        addLegendPanel();
        addFilesPanel();
        addToolbars();
        addCopyCapability();
        
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent winEvt) {
                onExit();
            }
        });
        
    }
    
    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI(PreferencesManager preferencesManager, PropertiesManager propertiesManager) {
        Application frame = new Application(preferencesManager, propertiesManager);
        frame.init();
        frame.setSize(940, 600);
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
            DictionnaryManager dictionnaryManager = new DictionnaryManager();
            final PropertiesManager propertiesManager = new PropertiesManager(dictionnaryManager);
            PreferencesManager preferencesManager = new PreferencesManager(propertiesManager);
            preferencesManager.loadPreferences();
            dictionnaryManager.loadDictionnary();
            createAndShowGUI(preferencesManager, propertiesManager);
        });
    }

}
