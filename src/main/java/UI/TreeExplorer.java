package UI;

import DBConnectors.DatabaseHandler;
import DBConnectors.Row;
import Trees.Tree;
import Trees.TreeBuilder;
import com.formdev.flatlaf.*;

import javax.swing.*;

import javax.swing.tree.DefaultMutableTreeNode;

import java.awt.*;

import java.util.Collections;
import java.util.List;


//TODO: Провести рефакторинг (сделать по классу на каждый фрейм, вынести обработчики, т.д.) для улучшения читаемости кода.

public class TreeExplorer {
    private final JFrame frame;
    private List<Tree> trees;
    private int maxNodeId;

    public TreeExplorer(DatabaseHandler connector) {

        // Включение библиотеки с темами:
        FlatIntelliJLaf.setup();
        UIManager.put("Button.arc", 30);

        frame = new JFrame("Tree Explorer");

        // Добавление кнопок:
        var showTreesBtn = new JButton("Show trees");
        showTreesBtn.setEnabled(false);
        var loadTreesBtn = new JButton("Load from DB");
        var uploadTreesBtn = new JButton("Upload to DB");
        uploadTreesBtn.setEnabled(false);
        var addTreeBtn = new JButton("+");

        // Добавим панель, в которой будут располагаться кнопки:
        JPanel btnPanel = new JPanel();
        btnPanel.add(showTreesBtn);
        btnPanel.add(loadTreesBtn);
        btnPanel.add(uploadTreesBtn);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));

        // Добавим панель, в которой будет список деревьев:
        var treesPanel = new JPanel();
        treesPanel.setLayout(new BoxLayout(treesPanel, BoxLayout.Y_AXIS));
        treesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Добавим прокрутку на случай, если деревьев будет много:
        var tressScrollPane = new JScrollPane(treesPanel);
        tressScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Добавим футер с информацией обо мне ^_^:
        var aboutAuthor = new JLabel("Сорокина Надежда, ЗБ-ПИ21-2. 2023 г.");
        aboutAuthor.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(aboutAuthor, BorderLayout.SOUTH);

        // Добавим обработчики на кнопки:
        showTreesBtn.addActionListener(action -> _showTrees(treesPanel, addTreeBtn));

        loadTreesBtn.addActionListener(action -> {
            try {
                treesPanel.removeAll();
                tressScrollPane.updateUI();
                trees = TreeBuilder.createListOfTrees(connector.load());
                JOptionPane.showMessageDialog(frame, "The data has been downloaded from the database and saved.");
                var ids = trees.stream().flatMap(tree -> tree.getNodes().stream().map(Tree.Node::getId)).toList();
                maxNodeId = (ids.isEmpty()) ? 0 : Collections.max(ids);
                showTreesBtn.setEnabled(true);
                uploadTreesBtn.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex);
            }
        });

        uploadTreesBtn.addActionListener(action -> {
            try {
                connector.upload(Row.buildRows(trees));
                JOptionPane.showMessageDialog(frame, "The trees have been successfully uploaded to the database.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex);
            }
        });

        addTreeBtn.addActionListener(action -> {
            var newTree = new Tree.Node(++maxNodeId);
            newTree.setParentNode(newTree);
            trees.add(new Tree(newTree));
            _showTrees(treesPanel, addTreeBtn);
        });

        // Добавление панелей с кнопками и деревьями на мейн-фрейм:
        frame.add(btnPanel, BorderLayout.NORTH);
        frame.add(tressScrollPane, BorderLayout.CENTER);
        show();
    }

    private void _showTrees(JPanel treesPanel, JButton btn) {
        treesPanel.removeAll();

        for (Tree tree : trees) {
            var node = new JPanel();
            node.add(new JLabel("Root ID: " + tree.getRoot().getId()));
            var treeBtn = new JButton("Show info");
            treeBtn.addActionListener(action -> {
                showTreeInfo(tree, frame);
                _showTrees(treesPanel, btn);
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
        var showTreeJT = new JTree(buildTree(tree.getRoot()));

        showTreeJT.setShowsRootHandles(true);
        showTreeJT.setCellRenderer(new TreeCustomRender());

        // Добавим панель с кнопками:
        var btnPanel = new JPanel();
        var deleteNodeBtn = new JButton("Delete node");
        deleteNodeBtn.setEnabled(false);
        var addNodeBtn = new JButton("Add child");
        addNodeBtn.setEnabled(false);

        // Добавим кнопки на панель:
        btnPanel.add(deleteNodeBtn);
        btnPanel.add(addNodeBtn);

        showTreeJT.addTreeSelectionListener(event -> {
            deleteNodeBtn.setEnabled(true);
            addNodeBtn.setEnabled(true);
        });

        // Добавим обработчики на кнопки:
        addNodeBtn.addActionListener(action -> {
            var selectedNode = (DefaultMutableTreeNode) showTreeJT.getLastSelectedPathComponent();
            var newNode = new Tree.Node(++maxNodeId);
            selectedNode.add(new DefaultMutableTreeNode(newNode));
            ((Tree.Node) selectedNode.getUserObject()).addChild(newNode);
            showTreeJT.updateUI();
        });

        deleteNodeBtn.addActionListener(action -> {
            var selectedNode = (DefaultMutableTreeNode) showTreeJT.getLastSelectedPathComponent();
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
            showTreeJT.updateUI();
        });

        // Добавим дерево и панель с кнопками на фрейм:
        treeInfo.add(new JScrollPane(showTreeJT), BorderLayout.CENTER);
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