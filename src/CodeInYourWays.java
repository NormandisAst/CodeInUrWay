import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.input.SAXBuilder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;

public class CodeInYourWays extends JFrame {

    private JTextField packageField;
    private List<ClassPanel> classPanels;

    public CodeInYourWays() {
        JFrame frame = new JFrame();
        frame.setSize(900, 500);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("      \t\t\t\t \t Code In My Ways         ");
        titleLabel.setFont(new Font("Times", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(15,62, 42));

        JPanel inputPanel = new JPanel(new GridLayout(3, 2,3, 11)); // Grid layout with 2 columns and spacing
        JPanel classButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Flow layout for Add Class button

        JLabel packageLabel = new JLabel("Package:");
        packageField = new JTextField();
        // packageField.setColumns(5);
        packageField.setPreferredSize(new Dimension(9, 9)); // Adjust the preferred size here
        // inputPanel.add(titleLabel);
        inputPanel.add(packageLabel);
        inputPanel.add(packageField);

        classPanels = new ArrayList<>();
        ClassPanel initialClassPanel = new ClassPanel();
        classPanels.add(initialClassPanel);
        inputPanel.add(new JLabel("Class Name:"));
        inputPanel.add(initialClassPanel);

        JButton addClassButton = new JButton("Add Class");
        inputPanel.setBackground(new Color(15,62, 42));
        addClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ClassPanel classPanel = new ClassPanel();
                classPanels.add(classPanel);
                inputPanel.add(new JLabel("Class Name:"));
                inputPanel.add(classPanel);

                frame.revalidate();
                frame.repaint();
            }
        });
        addClassButton.setForeground(new Color(15,62, 42));  // Set the background color of the button to brown
        classButtonPanel.add(addClassButton);
        classButtonPanel.add(titleLabel);
        classButtonPanel.setBackground(Color.BLACK);

        JButton generateButton = new JButton("Generate XML");
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateXML();
            }
        });
        JButton generateCodeButton = new JButton("Generate Java Code");
        generateCodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateJavaCode();
            }
        });
        classButtonPanel.add(generateCodeButton);

        JScrollPane scrollPane = new JScrollPane(inputPanel); // Add the inputPanel to the scroll pane

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(classButtonPanel, BorderLayout.NORTH);
        frame.add(generateButton, BorderLayout.SOUTH);

        frame.setVisible(true);
    }
    private static void generateJavaCode() {
        // Load the XML file
        File xmlFile = new File("output.xml");
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document document = saxBuilder.build(xmlFile);
            Element rootElement = document.getRootElement();

            // Get the package name from the XML file
            String packageName = rootElement.getAttributeValue("name");

            // Create the package folder
            File packageFolder = new File(packageName);
            packageFolder.mkdirs();

            // Generate Java code for each class in the XML file
            List<Element> classElements = rootElement.getChildren("class");
            for (Element classElement : classElements) {
                // Get the class name and visibility from the XML file
                String className = classElement.getAttributeValue("name");
                String classVisibility = classElement.getAttributeValue("visibility");

                // Create the class folder
                File classFolder = new File(packageFolder, className);
                classFolder.mkdirs();

                // Generate Java code for the class declaration
                StringBuilder codeBuilder = new StringBuilder();
                if (classVisibility != null && !classVisibility.isEmpty()) {
                    codeBuilder.append(classVisibility).append(" ");
                }
                codeBuilder.append("class ").append(className);

                // Generate Java code for inheritance
                List<Element> relationElements = classElement.getChildren("relation");
                for (Element relationElement : relationElements) {
                    String relationType = relationElement.getAttributeValue("type");
                    String relatedClass = relationElement.getAttributeValue("relatedClass");
                    String multiplicity = relationElement.getAttributeValue("multiplicity");

                    if (relationType.equals("inheritance")) {
                        // Generate Java code for inheritance
                        if (!relatedClass.isEmpty()) {
                            codeBuilder.append(" extends ").append(relatedClass);
                        }
                    }
                }
                codeBuilder.append(" {\n");

                // Generate Java code for attributes
                List<Element> attributeElements = classElement.getChildren("attribute");
                for (Element attributeElement : attributeElements) {
                    String attributeVisibility = attributeElement.getAttributeValue("visibility");
                    String attributeType = attributeElement.getAttributeValue("type");
                    String attributeName = attributeElement.getText();
                    codeBuilder.append("\t").append(attributeVisibility).append(" ").append(attributeType).append(" ").append(attributeName).append(";\n");
                }

                // Generate Java code for methods
                List<Element> methodElements = classElement.getChildren("method");
                for (Element methodElement : methodElements) {
                    String methodVisibility = methodElement.getAttributeValue("visibility");
                    String returnType = methodElement.getAttributeValue("returnType");
                    String methodName = methodElement.getText();
                    codeBuilder.append("\t").append(methodVisibility).append(" ").append(returnType).append(" ").append(methodName).append("() {\n");
                    codeBuilder.append("\t\t// Method implementation\n");
                    codeBuilder.append("\t}\n");
                }

                // Generate Java code for relations
                for (Element relationElement : relationElements) {
                    String relationType = relationElement.getAttributeValue("type");
                    String relatedClass = relationElement.getAttributeValue("relatedClass");
                    String multiplicity = relationElement.getAttributeValue("multiplicity");

                    if (relationType.equals("composition") || relationType.equals("association")) {
                        codeBuilder.append("\n\tprivate ").append(relatedClass);
                        if (multiplicity != null && multiplicity.equals("1..*")) {
                            codeBuilder.append("[]");
                        }
                        codeBuilder.append(" ").append(relatedClass.toLowerCase()).append(";");
                        codeBuilder.append("\n");
                    }
                }

                codeBuilder.append("\n\tpublic ").append(className).append("() {\n");
                for (Element relationElement : relationElements) {
                    String relationType = relationElement.getAttributeValue("type");
                    String relatedClass = relationElement.getAttributeValue("relatedClass");
                    String multiplicity = relationElement.getAttributeValue("multiplicity");

                    if (relationType.equals("composition") || relationType.equals("association")) {
                        codeBuilder.append("\t\t").append(relatedClass.toLowerCase());
                        if (multiplicity != null && multiplicity.equals("1..*")) {
                            codeBuilder.append(" = new ").append(relatedClass).append("[]");
                        } else {
                            codeBuilder.append(" = new ").append(relatedClass).append("()");
                        }
                        codeBuilder.append(";\n");
                    }
                }
                codeBuilder.append("\t}\n");

                codeBuilder.append("}");

                // Write the code to the class file
                File classFile = new File(classFolder, className + ".java");
                try (FileWriter writer = new FileWriter(classFile)) {
                    writer.write(codeBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Display a success message
            System.out.println("Java code generation completed successfully!");
        } catch (IOException | JDOMException e) {
            throw new RuntimeException(e);
        }
    }




    private void generateXML() {
        Element rootElement = new Element("package");
        Document document = new Document(rootElement);

        String packageName = packageField.getText();
        if (!packageName.isEmpty()) {
            rootElement.setAttribute("name", packageName);

            for (ClassPanel classPanel : classPanels) {
                String className = classPanel.getClassName();
                String classVisibility = classPanel.getClassVisibility();

                if (!className.isEmpty()) {
                    Element classElement = new Element("class");
                    classElement.setAttribute("name", className);

                    if (!classVisibility.isEmpty()) {
                        classElement.setAttribute("visibility", classVisibility);
                    }

                    List<AttributePanel> attributePanels = classPanel.getAttributePanels();
                    for (AttributePanel attributePanel : attributePanels) {
                        String attribute = attributePanel.getAttribute();
                        String attributeVisibility = attributePanel.getAttributeVisibility();
                        String attributeType = attributePanel.getAttributeType();

                        if (!attribute.isEmpty()) {
                            Element attributeElement = new Element("attribute");
                            attributeElement.setAttribute("visibility", attributeVisibility);
                            attributeElement.setAttribute("type", attributeType);
                            attributeElement.setText(attribute);
                            classElement.addContent(attributeElement);
                        }
                    }

                    List<MethodPanel> methodPanels = classPanel.getMethodPanels();
                    for (MethodPanel methodPanel : methodPanels) {
                        String method = methodPanel.getMethod();
                        String methodVisibility = methodPanel.getMethodVisibility();
                        String returnType = methodPanel.getReturnType();

                        if (!method.isEmpty()) {
                            Element methodElement = new Element("method");
                            methodElement.setAttribute("visibility", methodVisibility);
                            methodElement.setAttribute("returnType", returnType);
                            methodElement.setText(method);
                            classElement.addContent(methodElement);
                        }
                    }

                    List<RelationPanel> relationPanels = classPanel.getRelationPanels();
                    for (RelationPanel relationPanel : relationPanels) {
                        String relationType = relationPanel.getRelationType();
                        String relatedClass = relationPanel.getRelatedClass();
                        String multiplicity = relationPanel.getMultiplicity();

                        if (!relationType.isEmpty() && !relatedClass.isEmpty()) {
                            Element relationElement = new Element("relation");
                            relationElement.setAttribute("type", relationType);
                            relationElement.setAttribute("relatedClass", relatedClass);
                            relationElement.setAttribute("multiplicity", multiplicity);

                            // Add code for handling inheritance
                            if (relationType.equals("inheritance")) {
                                Element relatedClassElement = new Element("class");
                                relatedClassElement.setAttribute("name", relatedClass);
                                relationElement.addContent(relatedClassElement);
                            }
                            classElement.addContent(relationElement);
                        }
                    }

                    rootElement.addContent(classElement);
                }
            }

            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try {
                xmlOutputter.output(document, new FileOutputStream("output.xml"));
                JOptionPane.showMessageDialog(null, "XML generated successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error generating XML: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }





    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CodeInYourWays();
            }
        });


    }

    private class ClassPanel extends JPanel {

        private JTextField classNameField;
        private JComboBox<String> classVisibilityCombo;
        private List<AttributePanel> attributePanels;
        private List<MethodPanel> methodPanels;
        private List<RelationPanel> relationPanels;

        public ClassPanel() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JLabel classLabel = new JLabel("Class Name:");
            classNameField = new JTextField();
            add(classLabel);
            add(classNameField);

            JLabel visibilityLabel = new JLabel("Class Visibility:");
            classVisibilityCombo = new JComboBox<>(new String[]{"", "public", "private", "protected"});
            add(visibilityLabel);
            add(classVisibilityCombo);

            attributePanels = new ArrayList<>();
            AttributePanel initialAttributePanel = new AttributePanel();
            attributePanels.add(initialAttributePanel);
            add(initialAttributePanel);

            JButton addAttributeButton = new JButton("Add Attribute");
            addAttributeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AttributePanel attributePanel = new AttributePanel();
                    attributePanels.add(attributePanel);
                    add(attributePanel);
                    revalidate();
                    repaint();
                }
            });
            add(addAttributeButton);

            methodPanels = new ArrayList<>();
            MethodPanel initialMethodPanel = new MethodPanel();
            methodPanels.add(initialMethodPanel);
            add(initialMethodPanel);

            JButton addMethodButton = new JButton("Add Method");
            addMethodButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MethodPanel methodPanel = new MethodPanel();
                    methodPanels.add(methodPanel);
                    add(methodPanel);
                    revalidate();
                    repaint();
                }
            });
            add(addMethodButton);

            relationPanels = new ArrayList<>();
            RelationPanel initialRelationPanel = new RelationPanel();
            relationPanels.add(initialRelationPanel);
            add(initialRelationPanel);

            JButton addRelationButton = new JButton("Add Relation");
            addRelationButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    RelationPanel relationPanel = new RelationPanel();
                    relationPanels.add(relationPanel);
                    add(relationPanel);
                    revalidate();
                    repaint();
                }
            });
            add(addRelationButton);
        }

        public String getClassName() {
            return classNameField.getText();
        }

        public String getClassVisibility() {
            return (String) classVisibilityCombo.getSelectedItem();
        }

        public List<AttributePanel> getAttributePanels() {
            return attributePanels;
        }

        public List<MethodPanel> getMethodPanels() {
            return methodPanels;
        }

        public List<RelationPanel> getRelationPanels() {
            return relationPanels;
        }
    }

    private class AttributePanel extends JPanel {

        private JTextField attributeField;
        private JComboBox<String> attributeVisibilityCombo;
        private ButtonGroup attributeTypeGroup;
        private JRadioButton intAttributeType;
        private JRadioButton doubleAttributeType;
        private JRadioButton stringAttributeType;
        // Add more radio buttons for other types if needed

        public AttributePanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel attributeLabel = new JLabel("Attribute:");
            attributeField = new JTextField();
            add(attributeLabel);
            add(attributeField);

            JLabel visibilityLabel = new JLabel("Visibility:");
            attributeVisibilityCombo = new JComboBox<>(new String[]{"", "public", "private", "protected"});
            add(visibilityLabel);
            add(attributeVisibilityCombo);

            JLabel typeLabel = new JLabel("Type:");
            intAttributeType = new JRadioButton("int");
            doubleAttributeType = new JRadioButton("double");
            stringAttributeType = new JRadioButton("String");
            // Add more radio buttons for other types if needed

            attributeTypeGroup = new ButtonGroup();
            attributeTypeGroup.add(intAttributeType);
            attributeTypeGroup.add(doubleAttributeType);
            attributeTypeGroup.add(stringAttributeType);
            // Add more radio buttons to the group

            JPanel typePanel = new JPanel();
            typePanel.setLayout(new GridLayout(1, 0));
            typePanel.add(intAttributeType);
            typePanel.add(doubleAttributeType);
            typePanel.add(stringAttributeType);
            // Add more radio buttons to the panel

            add(typeLabel);
            add(typePanel);
        }

        public String getAttribute() {
            return attributeField.getText();
        }

        public String getAttributeVisibility() {
            return (String) attributeVisibilityCombo.getSelectedItem();
        }

        public String getAttributeType() {
            if (intAttributeType.isSelected()) {
                return "int";
            } else if (doubleAttributeType.isSelected()) {
                return "double";
            } else if (stringAttributeType.isSelected()) {
                return "String";
            } else {
                return "";
            }
        }

    }

    private class MethodPanel extends JPanel {

        private JTextField methodField;
        private JComboBox<String> methodVisibilityCombo;
        private ButtonGroup returnTypeGroup;
        private JRadioButton voidReturnType;
        private JRadioButton intReturnType;
        private JRadioButton doubleReturnType;
        // Add more radio buttons for other types if needed

        public MethodPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel methodLabel = new JLabel("Method:");
            methodField = new JTextField();
            add(methodLabel);
            add(methodField);

            JLabel visibilityLabel = new JLabel("Visibility:");
            methodVisibilityCombo = new JComboBox<>(new String[]{ "public", "private", "protected"});
            add(visibilityLabel);
            add(methodVisibilityCombo);

            JLabel returnTypeLabel = new JLabel("Return Type:");
            voidReturnType = new JRadioButton("void");
            intReturnType = new JRadioButton("int");
            doubleReturnType = new JRadioButton("double");
            // Add more radio buttons for other types if needed

            returnTypeGroup = new ButtonGroup();
            returnTypeGroup.add(voidReturnType);
            returnTypeGroup.add(intReturnType);
            returnTypeGroup.add(doubleReturnType);
            // Add more radio buttons to the group

            JPanel returnTypePanel = new JPanel();
            returnTypePanel.setLayout(new GridLayout(1, 0));
            returnTypePanel.add(voidReturnType);
            returnTypePanel.add(intReturnType);
            returnTypePanel.add(doubleReturnType);
            // Add more radio buttons to the panel

            add(returnTypeLabel);
            add(returnTypePanel);
        }

        public String getMethod() {
            return methodField.getText();
        }

        public String getMethodVisibility() {
            return (String) methodVisibilityCombo.getSelectedItem();
        }

        public String getReturnType() {
            if (voidReturnType.isSelected()) {
                return "void";
            } else if (intReturnType.isSelected()) {
                return "int";
            } else if (doubleReturnType.isSelected()) {
                return "double";
            } else {
                return "void";
            }
        }
    }

    private class RelationPanel extends JPanel {

        private JComboBox<String> relationTypeCombo;
        private JTextField relatedClassField;
        private JRadioButton multiplicityRadio;
        private JComboBox<String> multiplicityCombo;

        public RelationPanel() {
            setLayout(new FlowLayout(FlowLayout.LEFT));

            JLabel relationTypeLabel = new JLabel("Relation Type:");
            relationTypeCombo = new JComboBox<>(new String[]{"", "association", "inheritance", "composition", "aggregation"});
            add(relationTypeLabel);
            add(relationTypeCombo);

            JLabel relatedClassLabel = new JLabel("Related Class:");
            relatedClassField = new JTextField();
            add(relatedClassLabel);
            add(relatedClassField);

            JLabel multiplicityLabel = new JLabel("Multiplicity:");
            multiplicityCombo = new JComboBox<>(new String[]{"", "1..*", "0..1", "1"});
            add(multiplicityLabel);
            add(multiplicityCombo);

            JPanel multiplicityPanel = new JPanel();
            multiplicityPanel.setLayout(new FlowLayout(FlowLayout.LEFT));


        }

        public String getRelationType() {
            return (String) relationTypeCombo.getSelectedItem();
        }
        public String getMultiplicity() {
            return (String) multiplicityCombo.getSelectedItem();
        }
        public String getRelatedClass() {
            return relatedClassField.getText();
        }





    }
}

