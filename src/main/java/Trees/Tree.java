package Trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * <h2>Класс, представляющий структуру данных "Дерево".</h2>
 */
public class Tree {
    /**
     * <h3>Вложенный класс, представляющий узел дерева.</h3>
     * <ul>
     *     <li> id - уникальный идентификатор узла;</li>
     *     <li> parentNode - ссылка на родительский узел;</li>
     *     <li> children - список дочерних узлов.</li>
     * </ul>
     */
    public static class Node {
        private final int id;
        private Node parentNode;
        private final ArrayList<Node> children;

        public Node(int id) {
            this.id = id;
            this.parentNode = null;
            this.children = new ArrayList<>();
        }

        /**
         * Получить id узла.
         */
        public int getId() {
            return this.id;
        }

        /**
         * Получить список всех дочерних узлов.
         */
        public ArrayList<Node> getChildren() {
            return this.children;
        }

        public void addChild(Node child) {
            this.children.add(child);
            child.setParentNode(this);
        }

        /**
         * Получить родительский узел.
         */
        public Node getParentNode() {
            return parentNode;
        }

        /**
         * Установить родительский узел.
         * @param parent ссылка на родителя.
         */
        public void setParentNode(Node parent) {
            this.parentNode = parent;
        }

        /**
         * Является ли узел листом (т.е. узлом, у которого нет дочерних узлов).
         */
        public boolean isLeaf() {
            return children.isEmpty();
        }

        /**
         * Является ли узел корнем (т.е. узлом, у которого нет родительских узлов).
         */
        public boolean isRoot() {
            return parentNode == this;
        }

        public void remove() {
           parentNode.getChildren().remove(this);
           setParentNode(this);
        }

    }

    /**
     * Корень дерева.
     */
    private Node root;

    public Node getRoot(){
        return root;
    }

    /**
     * Создание дерева на основе ссылки на корневой узел дерева.
     * @param root ссылка на корень.
     */
    public Tree(Node root) {
        this.root = root;
    }

    /**
     * Получить список всех узлов дерева.
     * <p> <b>Метод реализован при помощи стэка.</b>
     * @return список всех узлов дерева.
     */
    public List<Node> getNodes() {
        var nodes = new ArrayList<Node>();
        var stack = new Stack<Node>();
        stack.push(root);
        while (!stack.isEmpty()) {
            var node = stack.pop();
            nodes.add(node);
            stack.addAll(node.getChildren());
        }
        return nodes;
    }

    /**
     * Получить список всех листьев дерева.
     * <p> <b>Метод реализован на базе {@link #getNodes()} при помощи
     * фильтрации на основе {@link Node#isLeaf()}.</b>
     * @return список всех листьев дерева.
     */
    public List<Node> getLeaves() {
        return this.getNodes().stream().filter(Node::isLeaf).toList();
    }

}