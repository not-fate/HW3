package UI;

import Trees.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class TreeCustomRender implements TreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        var node = (Tree.Node) ((DefaultMutableTreeNode) value).getUserObject();
        var text = node.isRoot() ? "Root ID: " : node.isLeaf() ? "Leaf ID: " : "Node ID: ";
        return new JLabel(text + node.getId());
    }
}
