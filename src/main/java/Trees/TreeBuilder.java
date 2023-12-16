package Trees;

import DBConnectors.Row;
import Trees.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class TreeBuilder {
    /**
     * Строит список деревьев ({@link Tree}) на основе данных из <code>List<{@link Row}></code>.
     */
    public static ArrayList<Tree> createListOfTrees(List<Row> data) throws Exception {
        var nodesMap = new HashMap<Integer, Tree.Node>();
        for (Row row : data) {

            int id = row.id;
            int parentId = row.parent_id;

            if (!nodesMap.containsKey(id))
                nodesMap.put(id, new Tree.Node(id));

            var node = nodesMap.get(id);
            if (node.getParentNode() != null)
                throw new TreeBuilderException("Дублирование id узла: " + id); // id узла должно быть уникальным в пределах программы.


            if (parentId != id) {
                if (!nodesMap.containsKey(parentId))
                    nodesMap.put(parentId, new Tree.Node(parentId));
                nodesMap.get(parentId).addChild(node);
            } else node.setParentNode(node);
        }
        var trees = new ArrayList<Tree>();

        for (var item : nodesMap.values()) {
            checkLoop(item);
            if (item.isRoot())
                trees.add(new Tree(item));
        }

        for (var node : nodesMap.values())
            if (node.getParentNode() == null)
                throw new TreeBuilderException("Узел " + node.getId() + " не создается явно " +
                        "и упоминается только в качестве родителя.");
        return trees;
    }

    static class TreeBuilderException extends Exception {
        public TreeBuilderException(String message) {
            super(message);
        }
    }

    /**
     * Т.к. дерево по определению является нецикличным графом, необходимо убедиться, что узлы не зацикливаются.
     * @throws TreeBuilderException, если обнаружена цикличность.
     */
    private static void checkLoop(Tree.Node node) throws TreeBuilderException {
        var set = new HashSet<Integer>();
        while (!node.isRoot()) {
            if (set.contains(node.getId())) throw new TreeBuilderException("Обнаружено зацикливание.");
            set.add(node.getId());
            node = node.getParentNode();
        }
    }
}