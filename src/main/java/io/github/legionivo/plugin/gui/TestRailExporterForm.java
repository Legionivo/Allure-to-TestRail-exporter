package io.github.legionivo.plugin.gui;

import com.intellij.openapi.project.Project;
import io.github.legionivo.plugin.Settings;
import io.github.legionivo.plugin.api.TestRailClient;
import io.github.legionivo.plugin.api.TestRailClientBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Optional;

public class TestRailExporterForm {
    private JPanel rootPanel;
    private JTextField usernameTextField;
    private JTextField passwordPasswordField;
    private JTextField url;
    private JTextField projectIdTextFiled;
    private JTextField suiteIdTextField;
    private JTextField statusField;
    private JButton testButton;
    private Settings settings;

    public TestRailExporterForm() {
        testButton.addActionListener(e -> handleTestButton());
        statusField.setText("press 'Test' button");
    }

    public void createUI(Project project) {
        settings = Settings.getInstance(project);
        usernameTextField.setText(Objects.requireNonNull(settings).getUserName());
        passwordPasswordField.setText(settings.getPassword());
        url.setText(settings.getApiUrl());
    }

    private void handleTestButton() {
        if (isEmpty(url) && isEmpty(usernameTextField) && isEmpty(passwordPasswordField)) {
            statusField.setText("Please fill all required fields");
        }

        final TestRailClient client = getTestRailClient();
        String connectionTest;
        try {
            connectionTest = client.getUserByEmail(settings.getUserName()).getName();
        } catch (Exception e) {
           connectionTest = e.getMessage();
        }
        statusField.setText(connectionTest);
    }

    private boolean isEmpty(final JTextField field) {
        return StringUtils.isBlank(field.getText());
    }

    private TestRailClient getTestRailClient() {
        return new TestRailClientBuilder(settings.getApiUrl(), settings.getUserName(), settings.getPassword()).build();
    }


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void apply() {
        settings.setUserName(usernameTextField.getText());
        settings.setPassword(passwordPasswordField.getText());
        settings.setApiUrl(url.getText());
        settings.setProjectId(Integer.parseInt(projectIdTextFiled.getText()));
        settings.setSuiteId(Integer.parseInt(suiteIdTextField.getText()));
    }

    public void reset() {
        usernameTextField.setText(settings.getUserName());
        passwordPasswordField.setText(settings.getPassword());
        url.setText(settings.getApiUrl());
        projectIdTextFiled.setText(String.valueOf(settings.getProjectId()));
        suiteIdTextField.setText(String.valueOf(settings.getSuiteId()));
    }

    public boolean isModified() {
        boolean modified = false;
        Integer projectId = Integer.parseInt(projectIdTextFiled.getText());
        Integer suiteId = Integer.parseInt(suiteIdTextField.getText());
        modified = !usernameTextField.getText().equals(settings.getUserName());
        modified |= !passwordPasswordField.getText().equals(settings.getPassword());
        modified |= !url.getText().equals(settings.getApiUrl());
        modified |= !projectId.equals(settings.getProjectId());
        modified |= !suiteId.equals(settings.getSuiteId());
        return modified;
    }
}