package UI;

import DBConnectors.DatabaseHandler;
import DBConnectors.Row;
import Trees.Tree;
import Trees.TreeBuilder;
import com.formdev.flatlaf.*;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import javax.swing.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.*;

import java.util.Collections;
import java.util.List;


//TODO: Провести рефакторинг для улучшения читаемости кода.


public class TreeExplorer {
    private final JFrame frame;
    private List<Tree> trees;
    private int maxNodeId;


    public TreeExplorer(DatabaseHandler database) {
        FlatIntelliJLaf.setup();
        UIManager.put("Button.arc", 30);

        frame = new JFrame("Tree Explorer");

        var showTreesBtn = new JButton("Show trees");
        var loadTreesBtn = new JButton("Load from DB");
        var uploadTreesBtn = new JButton("Upload to DB");
        var addTreeBtn = new JButton("+");

        JPanel btnPanel = new JPanel();

        showTreesBtn.setEnabled(false);
        uploadTreesBtn.setEnabled(false);

        btnPanel.add(showTreesBtn);
        btnPanel.add(loadTreesBtn);
        btnPanel.add(uploadTreesBtn);

        var treesPanel = new JPanel();
        treesPanel.setLayout(new BoxLayout(treesPanel, BoxLayout.Y_AXIS));
        treesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        var tressScrollPane = new JScrollPane(treesPanel);


        var aboutAuthor = new JLabel("Сорокина Надежда, ЗБ-ПИ21-2. 2023 г.");
        aboutAuthor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(aboutAuthor, BorderLayout.SOUTH);


        // Добавление компонентов на мейн-фрейм.
        frame.add(btnPanel, BorderLayout.NORTH);
        frame.add(tressScrollPane, BorderLayout.CENTER);


        showTreesBtn.addActionListener(action -> {
            showTrees(treesPanel, addTreeBtn);
        });

        loadTreesBtn.addActionListener(action -> {
            try {
                treesPanel.removeAll();
                tressScrollPane.updateUI();
                trees = TreeBuilder.createListOfTrees(database.load());
                JOptionPane.showMessageDialog(frame, "The data has been downloaded from the database and saved.");
                var ids = trees.stream().flatMap(tree -> tree.getNodes().stream().map(Tree.Node::getId)).toList();
                maxNodeId = Collections.max(ids);
                showTreesBtn.setEnabled(true);
                uploadTreesBtn.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex);
            }
        });

        uploadTreesBtn.addActionListener(action -> {
            try {
                database.upload(Row.buildRows(trees));
                JOptionPane.showMessageDialog(frame, "The trees have been successfully uploaded to the database.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex);
            }
        });

        addTreeBtn.addActionListener(action -> {
            var newTree = new Tree.Node(++maxNodeId);
            newTree.setParentNode(newTree);
            trees.add(new Tree(newTree));
            showTrees(treesPanel, addTreeBtn);
        });

        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
        tressScrollPane.setBorder(BorderFactory.createEmptyBorder());
        show();
    }

    private void showTrees(JPanel treesPanel, JButton btn) {
        treesPanel.removeAll();

        for (Tree tree : trees) {
            var node = new JPanel();
            node.add(new JLabel("Root ID: " + tree.getRoot().getId()));
            var treeBtn = new JButton("Show info");
            treeBtn.addActionListener(action -> {
                showTreeInfo(tree, frame);
                showTrees(treesPanel, btn);
            });
            node.add(treeBtn);
            treesPanel.add(node);
        }
        treesPanel.add(btn);
        btn.requestFocusInWindow();
        treesPanel.updateUI();
    }


    private void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 250);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showTreeInfo(Tree tree, Frame parentFrame) {
        var treeInfo = new JDialog(parentFrame, String.valueOf(tree.getRoot().getId()), true);
        var showTreePanel = new JTree(buildTree(tree.getRoot()));
        showTreePanel.setShowsRootHandles(true);

        showTreePanel.setCellRenderer(new TreeCustomRender());
        treeInfo.add(new JScrollPane(showTreePanel), BorderLayout.CENTER);

        var deleteNodeBtn = new JButton("Delete node");
        var addNodeBtn = new JButton("Add child");

        addNodeBtn.addActionListener(action -> {
            var selectedNode = (DefaultMutableTreeNode) showTreePanel.getLastSelectedPathComponent();
            var newNode = new Tree.Node(++maxNodeId);
            selectedNode.add(new DefaultMutableTreeNode(newNode));
            ((Tree.Node) selectedNode.getUserObject()).addChild(newNode);
            showTreePanel.updateUI();
        });

        deleteNodeBtn.addActionListener(action -> {
            var selectedNode = (DefaultMutableTreeNode) showTreePanel.getLastSelectedPathComponent();
            var node = (Tree.Node) selectedNode.getUserObject();
            if (node.isRoot()) {
                trees.removeIf(item -> (item.getRoot().getId() == node.getId()));
                JOptionPane.showMessageDialog(frame, "The root was deleted.");
                treeInfo.dispose();
                return;
            }
            var parent = (DefaultMutableTreeNode) selectedNode.getParent();
            parent.remove(selectedNode);
            node.remove();
            deleteNodeBtn.setEnabled(false);
            addNodeBtn.setEnabled(false);
            showTreePanel.updateUI();
        });


        var btnPanel = new JPanel();

        deleteNodeBtn.setEnabled(false);
        addNodeBtn.setEnabled(false);

        showTreePanel.addTreeSelectionListener(event -> {
            deleteNodeBtn.setEnabled(true);
            addNodeBtn.setEnabled(true);
        });

        btnPanel.add(deleteNodeBtn);
        btnPanel.add(addNodeBtn);
        treeInfo.add(btnPanel, BorderLayout.NORTH);

        treeInfo.setSize(400, 300);
        treeInfo.setLocationRelativeTo(null);
        treeInfo.setVisible(true);
    }

    private DefaultMutableTreeNode buildTree(Tree.Node node) {
        var tree = new DefaultMutableTreeNode(node);
        for (var child : node.getChildren()) {
            tree.add(buildTree(child));
        }
        return tree;
    }
}