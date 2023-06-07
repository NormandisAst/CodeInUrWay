import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RelationPanel extends JPanel {
    public JComboBox<String> relatedClassComboBox;
    public JComboBox<String> relationshipComboBox;
    private JButton addButton;
    private JComboBox<String> associationComboBox;
    public RelationPanel() {
        setLayout(new GridLayout(3, 2));

        JLabel relatedClassLabel = new JLabel("Related Class:");
        relatedClassComboBox = new JComboBox<>();


        JLabel relationshipLabel = new JLabel("Relationship Type:");
        relationshipComboBox = new JComboBox<>();
        relationshipComboBox.addItem("");
        relationshipComboBox.addItem("Composition");
        relationshipComboBox.addItem("Aggregation");
        relationshipComboBox.addItem("Association");
        relationshipComboBox.addItem("Inheritance");

        addButton = new JButton("Add Relationship");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String relatedClass = (String) relatedClassComboBox.getSelectedItem();
                String relationship = (String) relationshipComboBox.getSelectedItem();

                if (relatedClass != null && relationship != null) {
                    // Add the relationship to the class
                    // ...

                    // Clear the selection
                    relatedClassComboBox.setSelectedIndex(0);
                    relationshipComboBox.setSelectedIndex(0);
                }
            }
        });

        add(relatedClassLabel);
        add(relatedClassComboBox);
        add(relationshipLabel);
        add(relationshipComboBox);
        add(new JLabel());
        add(addButton);
        // Create and configure the association combobox
        associationComboBox = new JComboBox<>(new String[]{"","1..*", "0..1", "1..1"});
        add(new JLabel("Association Type:"));
        add(associationComboBox);
    }

    public String getAssociationType() {
        return (String) associationComboBox.getSelectedItem();
    }


    public void resetFields() {
        relatedClassComboBox.setSelectedIndex(0);
        relationshipComboBox.setSelectedIndex(0);
    }
}
