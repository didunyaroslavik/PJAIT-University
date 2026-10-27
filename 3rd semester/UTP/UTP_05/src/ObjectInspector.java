import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.function.Function;

public class ObjectInspector extends JFrame {

    JTable table;
    DefaultTableModel tableModel;
    JLabel statusLabel;
    JTabbedPane tabbedPane;

    public ObjectInspector() {
        setTitle("Object Inspector");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JButton loadButton = new JButton("Select file .class");
        JButton clearButton = new JButton("Clear");
        loadButton.addActionListener(e -> loadClassFile());
        clearButton.addActionListener(e -> clearTable());
        topPanel.add(loadButton);
        topPanel.add(clearButton);

        statusLabel = new JLabel("Select file .class for analysis");
        tabbedPane = new JTabbedPane();

        String[] columns = {"Type", "Modifiers", "Name", "Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] widths = {150, 150, 200, 350};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        tabbedPane.addTab("All elements", scrollPane);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(900, 600);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ObjectInspector().setVisible(true));
    }

    void loadClassFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Java Class files (.class)", "class");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            analyzeClassFile(fileChooser.getSelectedFile());
        }
    }

    void clearTable() {
        tableModel.setRowCount(0);
    }

    void analyzeClassFile(File selectedFile) {
        String className = selectedFile.getName().replace(".class", "");
        File parentDir = selectedFile.getAbsoluteFile().getParentFile();

        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{parentDir.toURI().toURL()});
            Class<?> loadedClass = classLoader.loadClass(className);
            analyzeClass(loadedClass);
            statusLabel.setText("Analyzed class: " + loadedClass.getName());
        } catch (Exception | NoClassDefFoundError e) {
            statusLabel.setText("Could not load class " + className + ": " + e);
        }
    }

    void analyzeClass(Class<?> clazz) {
        addReflectionElements(clazz.getDeclaredFields(), "FIELDS", ReflectionCLI::createFieldRow);
        addReflectionElements(clazz.getDeclaredConstructors(), "CONSTRUCTORS", ReflectionCLI::createConstructorRow);
        addReflectionElements(clazz.getDeclaredMethods(), "METHODS", ReflectionCLI::createMethodRow);
        addReflectionElements(clazz.getInterfaces(), "INTERFACES", ReflectionCLI::createInterfaceRow);
    }

    <T> void addReflectionElements(T[] elements, String sectionTitle, Function<T, Object[]> rowCreator) {
        if (elements != null && elements.length > 0) {
            tableModel.addRow(new Object[]{"--- " + sectionTitle + " ---", "", "", ""});
            for (T element : elements) {
                tableModel.addRow(rowCreator.apply(element));
            }
        }
    }
}
