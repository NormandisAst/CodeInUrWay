import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MethodPanel extends JPanel {
    private JTextField methodNameField;
    private JComboBox<String> returnTypeComboBox;
    private JComboBox<String> visibilityComboBox;
    private JButton addButton;
    private JButton addMoreMethodsButton; // New button for adding more methods

    private List<Method> methods; // List to store methods

    public MethodPanel() {
        setLayout(new BorderLayout());
        methods = new ArrayList<>();

        // Create the panel for method details
        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new GridLayout(3, 2));

        // Create the method name label and text field
        methodNameField = new JTextField();
        methodPanel.add(new JLabel("Method Name:"));
        methodPanel.add(methodNameField);

        // Create the return type combo box
        returnTypeComboBox = new JComboBox<>(getCommonTypes());
        methodPanel.add(new JLabel("Return Type:"));
        methodPanel.add(returnTypeComboBox);

        // Create the method visibility combo box
        visibilityComboBox = new JComboBox<>(new String[]{"public", "protected", "private"});
        methodPanel.add(new JLabel("Visibility:"));
        methodPanel.add(visibilityComboBox);

        add(methodPanel, BorderLayout.CENTER);

        // Add Method button
        addButton = new JButton("Add Method");
        addButton.addActionListener(e -> handleAddMethod());
        add(addButton, BorderLayout.SOUTH);

        // Add More Methods button
        addMoreMethodsButton = new JButton("Add More Methods");
        addMoreMethodsButton.addActionListener(e -> handleAddMoreMethods());
        add(addMoreMethodsButton, BorderLayout.SOUTH);
    }

    private String[] getCommonTypes() {
        return new String[]{
                "void",
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

    // Additional methods for accessing method details
    public String getMethodName() {
        return methodNameField.getText();
    }

    public String getReturnType() {
        return (String) returnTypeComboBox.getSelectedItem();
    }

    public String getMethodVisibility() {
        return (String) visibilityComboBox.getSelectedItem();
    }

    public void resetFields() {
        methodNameField.setText("");
        returnTypeComboBox.setSelectedIndex(0);
        visibilityComboBox.setSelectedIndex(0);
    }

    // Get the methods as a list
    public List<Method> getMethods() {
        return methods;
    }

    private void handleAddMethod() {
        String methodName = getMethodName();
        String returnType = getReturnType();
        String methodVisibility = getMethodVisibility();

        if (methodName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Method name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Method method = new Method(methodName, returnType, methodVisibility);
        addMethod(method);
        resetFields();
    }

    private void handleAddMoreMethods() {
        String methodName = getMethodName();
        String returnType = getReturnType();
        String methodVisibility = getMethodVisibility();

        if (methodName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Method name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Method method = new Method(methodName, returnType, methodVisibility);
        addMethod(method);
        resetFields();
    }

    public void addMethod(Method method) {
        methods.add(method);
        System.out.println("Method added: " + method);
    }

    public void addMethod(String className) {
        // Prompt the user for method details using input dialogs
        String methodName = JOptionPane.showInputDialog(this, "Enter method name:");
        if (methodName == null || methodName.isEmpty()) {
            // User canceled or entered an empty method name
            return;
        }

        String returnType = JOptionPane.showInputDialog(this, "Enter return type:");
        if (returnType == null || returnType.isEmpty()) {
            // User canceled or entered an empty return type
            return;
        }

        String visibility = (String) JOptionPane.showInputDialog(this, "Select method visibility:", "Visibility",
                JOptionPane.PLAIN_MESSAGE, null, new String[]{"public", "protected", "private"}, "public");
        if (visibility == null) {
            // User canceled
            return;
        }

        // Create a new Method object with the entered details
        Method method = new Method(methodName, returnType, visibility);

        // Add the method to the list of methods or update the UI accordingly
        methods.add(method);
        System.out.println("Method added: " + method);
    }
}
