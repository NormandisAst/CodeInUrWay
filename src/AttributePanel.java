import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AttributePanel extends JPanel {
    private JTextField attributeNameField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> visibilityComboBox;
    private JButton addButton;
    private JButton addMoreAttributesButton; // New button for adding more attributes

    private List<Attribute> attributes; // List to store attributes

    public AttributePanel() {
        setLayout(new BorderLayout());
        attributes = new ArrayList<>();

        // Create the panel for attribute details
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new GridLayout(3, 2));

        // Create the attribute name label and text field
        attributeNameField = new JTextField();
        attributePanel.add(new JLabel("Attribute Name:"));
        attributePanel.add(attributeNameField);

        // Create the attribute type combo box
        typeComboBox = new JComboBox<>(getCommonTypes());
        attributePanel.add(new JLabel("Attribute Type:"));
        attributePanel.add(typeComboBox);

        // Create the attribute visibility combo box
        visibilityComboBox = new JComboBox<>(new String[]{"public", "protected", "private"});
        attributePanel.add(new JLabel("Visibility:"));
        attributePanel.add(visibilityComboBox);

        add(attributePanel, BorderLayout.CENTER);

        // Add Attribute button
        addButton = new JButton("Add Attribute");
        addButton.addActionListener(e -> handleAddAttribute());
        add(addButton, BorderLayout.SOUTH);

        // Add More Attributes button
        addMoreAttributesButton = new JButton("Add More Attributes");
        addMoreAttributesButton.addActionListener(e -> handleAddMoreAttributes());
        add(addMoreAttributesButton, BorderLayout.SOUTH);
    }

    private String[] getCommonTypes() {
        return new String[]{
                "byte",
                "short",
                "int",
                "long",
                "float",
                "double",
                "boolean",
                "char",
                "String"
                // Add more types as needed
        };
    }

    // Additional methods for accessing attribute details
    public String getAttributeName() {
        return attributeNameField.getText();
    }

    public String getAttributeType() {
        return (String) typeComboBox.getSelectedItem();
    }

    public String getAttributeVisibility() {
        return (String) visibilityComboBox.getSelectedItem();
    }

    public void resetFields() {
        attributeNameField.setText("");
        typeComboBox.setSelectedIndex(0);
        visibilityComboBox.setSelectedIndex(0);
    }

    // Get the attributes as a list
    public List<Attribute> getAttributes() {
        return attributes;
    }

    private void handleAddAttribute() {
        String attributeName = getAttributeName();
        String attributeType = getAttributeType();
        String attributeVisibility = getAttributeVisibility();

        if (attributeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Attribute name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Attribute attribute = new Attribute(attributeName, attributeType, attributeVisibility);
        addAttribute(attribute);
        resetFields();
    }

    private void handleAddMoreAttributes() {
        String attributeName = getAttributeName();
        String attributeType = getAttributeType();
        String attributeVisibility = getAttributeVisibility();

        if (attributeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Attribute name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Attribute attribute = new Attribute(attributeName, attributeType, attributeVisibility);
        addAttribute(attribute);
        resetFields();
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
        System.out.println("Attribute added: " + attribute);
    }

    public void addAttribute(String className) {
        // Prompt the user for attribute details using input dialogs
        String attributeName = JOptionPane.showInputDialog(this, "Enter attribute name:");
        if (attributeName == null || attributeName.isEmpty()) {
            // User canceled or entered an empty attribute name
            return;
        }

        String attributeType = (String) JOptionPane.showInputDialog(this, "Select attribute type:", "Type",
                JOptionPane.PLAIN_MESSAGE, null, getCommonTypes(), "byte");
        if (attributeType == null) {
            // User canceled
            return;
        }

        String visibility = (String) JOptionPane.showInputDialog(this, "Select attribute visibility:", "Visibility",
                JOptionPane.PLAIN_MESSAGE, null, new String[]{"public", "protected", "private"}, "public");
        if (visibility == null) {
            // User canceled
            return;
        }

        // Create a new Attribute object with the entered details
        Attribute attribute = new Attribute(attributeName, attributeType, visibility);

        // Add the attribute to the list of attributes or update the UI accordingly
        attributes.add(attribute);
        System.out.println("Attribute added: " + attribute);
    }
}
