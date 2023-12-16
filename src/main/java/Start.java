import DBConnectors.PostgresHandler;
import UI.TreeExplorer;

import javax.swing.*;
import java.io.IOException;

/**
 * <h2>Выполнение домашнего задания 3 (HW3).</h1>
 *
 * @author Сорокина Надежда, группа ЗБ-ПИ21-2.
 * <p><a href="https://github.com/not-fate/HW3">Удаленный репозиторий проекта на Github.</a>
 * @version 08.12.2023
 */
public class Start {
    public static void main(String[] args) throws IOException {
        new TreeExplorer(new PostgresHandler());
    }
}
