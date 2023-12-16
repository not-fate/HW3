package DBConnectors;

import Trees.Tree;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * <h3> Сущность записи из таблицы TREES.</h3>
 * Используется в качестве Entity-сущности JPA.
 */
@Entity
@Table(name="Trees")
public class Row {
    /**
     * Уникальный идентификатор узла.
     */
    @Id
    public int id;
    /**
     * Уникальный идентификатор родительского узла.
     */
    public int parent_id;
    public Row(){
    }

    public Row(int id, int parent_id){
        this.id = id;
        this.parent_id = parent_id;
    }
    @Override
    public String toString() {
        return id + " " + parent_id;
    }

    public static ArrayList<Row> buildRows(List<Tree> trees) {
        var rows = new ArrayList<Row>();
        for (var tree:
                trees) {
            var treeChildren = tree.getNodes();
            for (var nodes:
                    treeChildren) {
                rows.add(new Row(nodes.getId(), nodes.getParentNode().getId()));
            }
        }
        return rows;
    }
}