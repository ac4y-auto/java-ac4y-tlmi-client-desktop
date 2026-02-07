package tlmi.communcator.atlmiclient.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class PartnerListPanel extends JPanel {

    /**
     * Simple partner data holder - decoupled from TlmiTranslateUser.
     * When ac4y libs are available, map TlmiTranslateUser to this.
     */
    public static class PartnerInfo {
        private final String name;
        private final String humanName;
        private final String avatar; // Base64 encoded

        public PartnerInfo(String name, String humanName, String avatar) {
            this.name = name;
            this.humanName = humanName;
            this.avatar = avatar;
        }

        public String getName() { return name; }
        public String getHumanName() { return humanName; }
        public String getAvatar() { return avatar; }

        @Override
        public String toString() {
            return humanName != null ? humanName : name;
        }
    }

    private final DefaultListModel<PartnerInfo> listModel;
    private final JList<PartnerInfo> list;

    public PartnerListPanel() {
        setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setCellRenderer(new PartnerCellRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(50);

        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addPartner(PartnerInfo partner) {
        SwingUtilities.invokeLater(() -> listModel.addElement(partner));
    }

    public JList<PartnerInfo> getList() {
        return list;
    }

    public List<PartnerInfo> getPartners() {
        List<PartnerInfo> partners = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            partners.add(listModel.getElementAt(i));
        }
        return partners;
    }

    private static class PartnerCellRenderer extends JPanel implements ListCellRenderer<PartnerInfo> {

        private final JLabel avatarLabel = new JLabel();
        private final JLabel nameLabel = new JLabel();

        public PartnerCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            avatarLabel.setPreferredSize(new Dimension(40, 40));
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            add(avatarLabel, BorderLayout.WEST);
            add(nameLabel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends PartnerInfo> list,
                                                       PartnerInfo partner,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus) {
            String displayName = partner.getHumanName() != null ? partner.getHumanName() : partner.getName();
            nameLabel.setText(displayName);

            if (partner.getAvatar() != null && !partner.getAvatar().isEmpty()) {
                BufferedImage img = ImageUtil.fromBase64(partner.getAvatar());
                if (img != null) {
                    Image scaled = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                    avatarLabel.setIcon(new ImageIcon(scaled));
                } else {
                    avatarLabel.setIcon(null);
                    avatarLabel.setText("?");
                }
            } else {
                avatarLabel.setIcon(null);
                avatarLabel.setText("?");
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                nameLabel.setForeground(list.getForeground());
            }

            return this;
        }
    }

}
