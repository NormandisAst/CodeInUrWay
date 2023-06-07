import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class MainClass extends JFrame {
    private ClassPanel classPanel;
    private AttributePanel attributePanel;
    private MethodPanel methodPanel;
    private static RelationPanel relationPanel;
    private JComboBox<String> packageComboBox;
    private JComboBox<String> classComboBox;
    private JComboBox<String> relatedClassComboBox;
    private JComboBox<String> relationshipComboBox;
    private Document document;
    private JTextField packageTextField;
    private JTextField classTextField;

    public MainClass() {
        // Set up the main frame
        setTitle("Class Editor");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the panel for package and class selection
        relatedClassComboBox = new JComboBox<>();
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 3));

        // Create the package selection combo box
        packageComboBox = new JComboBox<>();
        controlPanel.add(new JLabel("Package:"));
        controlPanel.add(packageComboBox);

        // Create the package text field
        packageTextField = new JTextField();
        controlPanel.add(new JLabel("New Package:"));
        controlPanel.add(packageTextField);

        // Create the "Add Package" button
        JButton addPackageButton = new JButton("Add Package");
        addPackageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewPackage();
            }
        });
        controlPanel.add(addPackageButton);

        // Create the class selection combo box
        classComboBox = new JComboBox<>();
        controlPanel.add(new JLabel("Class:"));
        controlPanel.add(classComboBox);

        // Create the class text field
        classTextField = new JTextField();
        controlPanel.add(new JLabel("New Class:"));
        controlPanel.add(classTextField);

        // Create the "Add Class" button
        JButton addClassButton = new JButton("Add Class");
        addClassButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewClass();
            }
        });
        controlPanel.add(addClassButton);

        add(controlPanel, BorderLayout.NORTH);

        // Create the panel for attributes, methods, and relations
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        // Attribute Panel
        attributePanel = new AttributePanel();
        panel.add(attributePanel);

        // Create the "Add Attribute" button
        JButton addAttributeButton = new JButton("Add Attribute");
        addAttributeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddAttribute();
            }
        });
        panel.add(addAttributeButton);

        // Method Panel
        methodPanel = new MethodPanel();
        panel.add(methodPanel);

        // Create the "Add Method" button
        JButton addMethodButton = new JButton("Add Method");
        addMethodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddMethod();
            }
        });
        panel.add(addMethodButton);

        // Relation Panel
        relationPanel = new RelationPanel();
        panel.add(relationPanel);

        add(panel, BorderLayout.CENTER);

        // Create the "Generate XML" button
        JButton generateXmlButton = new JButton("Generate XML");
        generateXmlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateXml();
            }
        });
        JButton convertToJavaButton = new JButton("Convert XML to Java");
        convertToJavaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateJavaCode();
            }
        });
        controlPanel.add(convertToJavaButton);
        add(generateXmlButton, BorderLayout.SOUTH);


        // Display the main frame
        setVisible(true);
    }

    private void addNewPackage() {
        String newPackage = packageTextField.getText();
        if (!newPackage.isEmpty()) {
            packageComboBox.addItem(newPackage);
            packageTextField.setText("");
        }
    }

    private void addNewClass() {
        String packageName = (String) packageComboBox.getSelectedItem();
        String className = JOptionPane.showInputDialog("Enter class name:");
        if (className != null && !className.isEmpty()) {
            classComboBox.addItem(className);
            relationPanel.relatedClassComboBox.addItem("");
            relationPanel.relatedClassComboBox.addItem(className); // Add the class name to relatedClassComboBox
            attributePanel.resetFields();
            methodPanel.resetFields();
            relationPanel.resetFields();
        }
    }

    private void handleAddAttribute() {
        String className = (String) classComboBox.getSelectedItem();
        attributePanel.addAttribute(className);
    }

    private void handleAddMethod() {
        String className = (String) classComboBox.getSelectedItem();
        methodPanel.addMethod(className);
    }

    private void generateXml() {
        String packageName = (String) packageComboBox.getSelectedItem();
        String className = (String) classComboBox.getSelectedItem();

        // Create XML elements
        Element rootElement = new Element("diagclass");
        document = new Document(rootElement); // Assign the document variable

        // Create package element
        Element packageElement = new Element("package");
        packageElement.setAttribute("name", packageName);
        rootElement.addContent(packageElement);

        // Create class element
        Element classElement = new Element("class");
        classElement.setAttribute("name", className);
        packageElement.addContent(classElement);

        // Add attributes
        Element attributesElement = new Element("attributes");
        classElement.addContent(attributesElement);
        for (Attribute attribute : attributePanel.getAttributes()) {
            Element attributeElement = new Element("attribute");
            attributeElement.setAttribute("name", attribute.getName());
            attributeElement.setAttribute("type", attribute.getType());
            attributeElement.setAttribute("visibility", attribute.getVisibility());
            attributesElement.addContent(attributeElement);
        }

        // Add methods
        Element methodsElement = new Element("methods");
        classElement.addContent(methodsElement);
        for (Method method : methodPanel.getMethods()) {
            Element methodElement = new Element("method");
            methodElement.setAttribute("name", method.getName());
            methodElement.setAttribute("returnType", method.getReturnType());
            methodElement.setAttribute("visibility", method.getVisibility());
            methodsElement.addContent(methodElement);
        }

        // Add relations
        Element relationsElement = new Element("relations");
        classElement.addContent(relationsElement);
        for (int i = 0; i < relationPanel.relatedClassComboBox.getItemCount(); i++) {

            String relatedClass = (String) relationPanel.relatedClassComboBox.getItemAt(i);
            String relationship = (String) relationPanel.relationshipComboBox.getItemAt(i);
            if (relatedClass != null && relationship != null) {
                Element relationElement = new Element("relation");
                relationElement.setAttribute("relatedClass", relatedClass);
                relationElement.setAttribute("relationship", relationship);
                relationsElement.addContent(relationElement);
            }
        }

        // Write the XML document to a file
        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlOutputter.output(document, new FileWriter("output.xml"));
            System.out.println("XML file written successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void generateJavaCode() {
        // Load the XML file
        File xmlFile = new File("output.xml");
        SAXBuilder saxBuilder = new SAXBuilder();

        try {
            Document document = saxBuilder.build(xmlFile);
            Element rootElement = document.getRootElement();

            // Get the package name from the XML file
            Element packageElement = rootElement.getChild("package");
            String packageName = packageElement.getAttributeValue("name");

            // Create the package folder
            File packageFolder = new File(packageName);
            packageFolder.mkdirs();

            // Get the class element from the XML file
            Element classElement = packageElement.getChild("class");
            String className = classElement.getAttributeValue("name");

            // Create the class folder
            File classFolder = new File(packageFolder, className);
            classFolder.mkdirs();

            // Generate Java code for the class declaration
            StringBuilder codeBuilder = new StringBuilder();
            String classVisibility = classElement.getAttributeValue("visibility");
            if (classVisibility != null && !classVisibility.isEmpty()) {
                codeBuilder.append(classVisibility).append(" ");
            }
            codeBuilder.append("package ").append(packageName).append(";\n");
            codeBuilder.append("public class ").append(className);

            // Generate Java code for inheritance or composition
            String inheritanceClassName = null;
            String compositionClassName = null;
            Element relationsElement = classElement.getChild("relations");
            List<Element> relationElements = relationsElement.getChildren("relation");
            for (Element relationElement : relationElements) {
                String relationship = relationElement.getAttributeValue("relationship");
                String relatedClass = relationElement.getAttributeValue("relatedClass");

                if (relationship.equals("Inheritance")) {
                    inheritanceClassName = relatedClass;
                } else if (relationship.equals("Composition")) {
                    compositionClassName = relatedClass;
                }
            }

            if (inheritanceClassName != null && !inheritanceClassName.isEmpty()) {
                codeBuilder.append(" extends ").append(inheritanceClassName);
            }

            codeBuilder.append(" {");

            // Generate Java code for attributes
            Element attributesElement = classElement.getChild("attributes");
            List<Element> attributeElements = attributesElement.getChildren("attribute");
            for (Element attributeElement : attributeElements) {
                String attributeVisibility = attributeElement.getAttributeValue("visibility");
                String attributeType = attributeElement.getAttributeValue("type");
                String attributeName = attributeElement.getAttributeValue("name");

                // Generate attribute declaration
                codeBuilder.append("\n\t").append(attributeVisibility).append(" ").append(attributeType).append(" ").append(attributeName).append(";");

                // Generate getter method
                codeBuilder.append("\n\t").append(attributeVisibility).append(" ").append(attributeType).append(" get").append(attributeName).append("() {");
                codeBuilder.append("\n\t\treturn ").append(attributeName).append(";");
                codeBuilder.append("\n\t}");

                // Generate setter method
                codeBuilder.append("\n\t").append(attributeVisibility).append(" void set").append(attributeName).append("(").append(attributeType).append(" ").append(attributeName).append(") {");
                codeBuilder.append("\n\t\tthis.").append(attributeName).append(" = ").append(attributeName).append(";");
                codeBuilder.append("\n\t}");
            }

            // Generate Java code for associations
            for (Element relationElement : relationElements) {
                String relationship = relationElement.getAttributeValue("relationship");
                String relatedClass = relationElement.getAttributeValue("relatedClass");

                if (relationship.equals("Association")) {
                    // Generate code for association
                    codeBuilder.append("\n\tprivate ").append(relatedClass).append(" ").append(relatedClass).append(";");

                    // Generate getter method
                    codeBuilder.append("\n\tpublic ").append(relatedClass).append(" get").append(relatedClass).append("() {");
                    codeBuilder.append("\n\t\treturn ").append(relatedClass).append(";");
                    codeBuilder.append("\n\t}");

                    // Generate setter method
                    codeBuilder.append("\n\tpublic void set").append(relatedClass).append("(").append(relatedClass).append(" ").append(relatedClass).append(") {");
                    codeBuilder.append("\n\t\tthis.").append(relatedClass).append(" = ").append(relatedClass).append(";");
                    codeBuilder.append("\n\t}");
                } else if (relationship.equals("Composition")) {
                    // Generate code for composition
                    codeBuilder.append("\n\tprivate ").append(relatedClass).append(" ").append(relatedClass).append(";");

                    // Generate getter method
                    codeBuilder.append("\n\tpublic ").append(relatedClass).append(" get").append(relatedClass).append("() {");
                    codeBuilder.append("\n\t\treturn ").append(relatedClass).append(";");
                    codeBuilder.append("\n\t}");

                    // Generate constructor
                    codeBuilder.append("\n\tpublic ").append(className).append("(").append(relatedClass).append(" ").append(relatedClass).append(") {");
                    codeBuilder.append("\n\t\tthis.").append(relatedClass).append(" = ").append(relatedClass).append(";");
                    codeBuilder.append("\n\t}");
                }
            }

            codeBuilder.append("\n}");

            // Write the code to the class file
            File classFile = new File(classFolder, className + ".java");
            try (FileWriter writer = new FileWriter(classFile)) {
                writer.write(codeBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Display a success message
            System.out.println("Java code generation completed successfully!");
        } catch (IOException | JDOMException e) {
            throw new RuntimeException(e);
        }
    }







    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainClass();
            }
        });
    }
}
