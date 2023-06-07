import javax.swing.*;
import java.awt.*;

public class ClassPanel extends JPanel {
    private JTextField classNameField;
    private JTextField classVisibilityField;

    public ClassPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel classNameLabel = new JLabel("Class Name:");
        classNameField = new JTextField();
        classNameField.setEditable(false);

        JLabel classVisibilityLabel = new JLabel("Visibility:");
        classVisibilityField = new JTextField();
        classVisibilityField.setEditable(false);

        add(classNameLabel);
        add(classNameField);
        add(classVisibilityLabel);
        add(classVisibilityField);
    }

    public void setClassName(String className) {
        classNameField.setText(className);
    }

    public void setClassVisibility(String visibility) {
        classVisibilityField.setText(visibility);
    }
}
