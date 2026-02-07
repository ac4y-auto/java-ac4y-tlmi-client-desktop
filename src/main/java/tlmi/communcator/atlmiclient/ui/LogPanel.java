package tlmi.communcator.atlmiclient.ui;

import javax.swing.*;
import java.awt.*;

public class LogPanel extends JPanel {

    private final DefaultListModel<String> listModel;
    private final JList<String> list;

    public LogPanel() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            listModel.insertElementAt(message, 0);
            if (listModel.size() > 500) {
                listModel.removeElementAt(listModel.size() - 1);
            }
        });
    }

    public void clear() {
        SwingUtilities.invokeLater(listModel::clear);
    }

}
