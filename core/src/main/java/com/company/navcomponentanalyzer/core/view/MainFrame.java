package com.company.navcomponentanalyzer.core.view;

import com.company.navcomponentanalyzer.core.config.ConfigFile;
import com.company.navcomponentanalyzer.core.listener.HighlightedTextAreaListener;
import com.company.navcomponentanalyzer.core.listener.MethodListSelectionListener;
import com.company.navcomponentanalyzer.core.listener.ObjectTreeSelectionListener;
import com.company.navcomponentanalyzer.core.listener.menu.AboutListener;
import com.company.navcomponentanalyzer.core.listener.menu.FileListener;
import com.company.navcomponentanalyzer.core.listener.menu.SearchTextListener;
import com.company.navcomponentanalyzer.core.listener.menu.ThemeListener;
import com.company.navcomponentanalyzer.core.listener.menu.antipattern.AntipatternListener;
import com.company.navcomponentanalyzer.core.model.NavObjects;
import com.company.navcomponentanalyzer.core.model.object.NavType;
import com.company.navcomponentanalyzer.core.model.Theme;
import com.company.navcomponentanalyzer.core.model.search.SearchProcessor;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final NavObjects navObjects;
    private JTextPane textArea;
    private DefaultListModel<String> listModel;
    private JTree tree;
    private JList<String> list;
    private final List<SearchProcessor> searchProcessors;

    public MainFrame(NavObjects navObjects, List<SearchProcessor> searchProcessors) throws HeadlessException {
        this.navObjects = navObjects;
        this.searchProcessors = searchProcessors;
        prepareGUI();
    }

    public NavObjects getNavObjects() {
        return navObjects;
    }

    private void prepareGUI() {
        //--MENU
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        // -------------------------------------------
        setTitle("Navision Component Analyzer"); // заголовок окна
        setMinimumSize(new Dimension(640, 480));
        //setLayout(new GridLayout(1, 2));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new NavObjectCellElementRenderer());
        MethodListSelectionListener methodListSelectionListener = new MethodListSelectionListener(this);
        list.addListSelectionListener(methodListSelectionListener);
        list.setComponentPopupMenu(new MethodListPopupMenu(this));

        textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setComponentPopupMenu(new TextAreaPopupMenu());
        StyledDocument doc = textArea.getStyledDocument();


        // Create the list for line numbers
        DefaultListModel<Integer> lineNumberListModel = new DefaultListModel<>();
        JList<Integer> lineNumberList = new JList<>(lineNumberListModel);
        lineNumberList.setBackground(Color.LIGHT_GRAY);
        lineNumberList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, textArea.getFont().getSize()));
        //lineNumberList.setSelectionModel(textArea.getSelectionModel());
        lineNumberList.setFixedCellHeight(textArea.getFontMetrics(textArea.getFont()).getHeight());
        //lineNumberList.setFixedCellWidth(40);
        // Add a scroll pane containing the text area and line number list to the panel
        JScrollPane scrollTextPane = new JScrollPane(textArea);
        scrollTextPane.setRowHeaderView(lineNumberList);

        HighlightedTextAreaListener highlightedTextAreaListener = new HighlightedTextAreaListener(this, doc, lineNumberListModel);
        doc.addDocumentListener(highlightedTextAreaListener);

        tree = new JTree(new DefaultMutableTreeNode());
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ObjectTreeSelectionListener treeListener = new ObjectTreeSelectionListener(this);
        tree.addTreeSelectionListener(treeListener);

        // Create the JPanel and add the components
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tree), BorderLayout.WEST);
        panel.add(scrollTextPane, BorderLayout.CENTER);
        panel.add(new JScrollPane(list), BorderLayout.EAST);
        // Add the JPanel to the JFrame
        add(panel);
        pack(); // устанавливаем желательные размеры
        SwingUtilities.updateComponentTreeUI(this);
        setLocationRelativeTo(null);
        setVisible(true); // отображаем окно
    }

    public void updateTree(){
        DefaultMutableTreeNode root = addParentNode();
        Map<NavType, DefaultMutableTreeNode> parentNodes = new HashMap<>();
        for(NavType navType: NavType.values()){
            parentNodes.put(navType, addChildNode(root, navType.toString()));
        }
        if(navObjects != null) {
            navObjects.getNavObjectsList().forEach((n) -> {
                DefaultMutableTreeNode parent = parentNodes.get(n.getNavType());
                addChildNode(parent, n);
            });
        }
        showTree(parentNodes);
    }

    private DefaultMutableTreeNode addParentNode() {
        return new DefaultMutableTreeNode("");
    }

    private DefaultMutableTreeNode addChildNode(DefaultMutableTreeNode parent, Object nodeCaption) {
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(nodeCaption);
        parent.add(child);
        return child;
    }

    private void showTree(Map<NavType, DefaultMutableTreeNode> parentNodes) {
        //create the tree with department as root node
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DefaultMutableTreeNode baseRoot = (DefaultMutableTreeNode) model.getRoot();
        baseRoot.removeAllChildren();
        for(Map.Entry<NavType, DefaultMutableTreeNode> item : parentNodes.entrySet() ) {
            baseRoot.add(item.getValue());
        }
        model.nodeStructureChanged(baseRoot);

        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar= new JMenuBar();
        // создаем меню
        JMenu menu = createMenuFile();
        menuBar.add(menu);

        menu = createMenuThemes();
        menuBar.add(menu);

        menu = createMenuAction();
        menuBar.add(menu);

        menu = createMenuHelp();
        menuBar.add(menu);

        return menuBar;
    }

    private JMenu createMenuHelp() {
        JMenu menu = new JMenu("Help");
        JMenuItem itm = new JMenuItem("About");
        menu.add(itm);
        AboutListener aboutListener = new AboutListener(this);
        itm.addActionListener(aboutListener);
        return menu;
    }

    private JMenu createMenuAction() {
        JMenu menu = new JMenu("Search");

        JMenuItem itm = new JMenuItem("Search text...");
        menu.add(itm);
        SearchTextListener searchTextListener = new SearchTextListener(this);
        itm.addActionListener(searchTextListener);

        JMenu menuAnti = new JMenu("Antipatterns");
        menu.add(menuAnti);

        for (SearchProcessor searchProcessor : searchProcessors) {
            itm = new JMenuItem(searchProcessor.getCaption());
            menuAnti.add(itm);
            ActionListener listener = new AntipatternListener(this, searchProcessor);
            itm.addActionListener(listener);
        }

        return menu;
    }

    private JMenu createMenuFile() {
        JMenu menu = new JMenu("File");
        FileListener fileListener = new FileListener(this);

        JMenuItem itm = new JMenuItem("Open");
        itm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                InputEvent.CTRL_DOWN_MASK));
        menu.add(itm);
        itm.addActionListener(fileListener);

        itm = new JMenuItem("Append");
        itm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,
                InputEvent.CTRL_DOWN_MASK));
        itm.addActionListener(fileListener);
        menu.add(itm);

        JMenu submenu = new JMenu("Open Recent");
        ConfigFile configFile = ConfigFile.getInstance();
        ArrayList<String> recentFiles = configFile.getRecentFiles();
        for (String path : recentFiles) {
            File file = new File(path);
            if(file.exists()) {
                itm = new JMenuItem(file.getName());
                itm.addActionListener(fileListener);
                submenu.add(itm);
            }
        }
        menu.add(submenu);

        itm = new JMenuItem("Close");
        itm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_DOWN_MASK));
        itm.addActionListener(fileListener);
        menu.add(itm);
        return menu;
    }

    private JMenu createMenuThemes() {
        JMenu menu = new JMenu("Themes");
        ButtonGroup group = new ButtonGroup();
        ThemeListener themeListener = new ThemeListener(this);
        ArrayList<Theme> themeList = themeListener.getThemeList();
        String currentLookAndFeelName =
                UIManager.getLookAndFeel().getClass().getName();

        String configLookAndFeelName = ConfigFile.getInstance().getTheme();
        if(configLookAndFeelName != null) {
            if(!configLookAndFeelName.equals(currentLookAndFeelName)) {
                currentLookAndFeelName = configLookAndFeelName;
                try {
                    UIManager.setLookAndFeel(currentLookAndFeelName);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        for (Theme theme : themeList) {
            JRadioButtonMenuItem itm = new JRadioButtonMenuItem (theme.name);
            itm.addActionListener(themeListener);
            if (currentLookAndFeelName.equals(theme.className)) {
                itm.setSelected(true);
            }
            group.add(itm);
            menu.add(itm);
        }
        return menu;
    }

    public void close() {
        ConfigFile.getInstance().closeAndSaveConfig();
        dispose();
        System.exit(0);
    }

    public JTextPane getTextArea() {
        return textArea;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }

    public JTree getTree() {
        return tree;
    }

    public JList<String> getList() {
        return list;
    }

}
