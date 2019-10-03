package io.github.legionivo.plugin.gui;

import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import io.github.legionivo.plugin.Settings;
import io.github.legionivo.plugin.api.TestRailClient;
import io.github.legionivo.plugin.api.TestRailClientBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
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

    private static final Logger LOGGER = new DefaultLogger(TestRailExporterForm.class.getName());


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
        Optional<String> username = getTestRailUser();
        if (username.isPresent()) {
            statusField.setText(String.format("authorized as '%s'", username.get()));
        } else {
            statusField.setText("Bad credentials");
        }
    }

    private boolean isEmpty(final JTextField field) {
        return StringUtils.isBlank(field.getText());
    }

    private boolean isEmpty(final JPasswordField field) {
        return StringUtils.isBlank(String.copyValueOf(field.getPassword()));
    }

    private Optional<String> getTestRailUser() {
        if (isEmpty(url) && isEmpty(usernameTextField) && isEmpty(passwordPasswordField)) {
            return Optional.empty();
        }
        final TestRailClient client = getTestRailClient();
        try {
            return Optional.of(client.getUserByEmail(settings.getUserName()).getName());
        } catch (Exception e) {
            return Optional.empty();
        }
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
        modified = !usernameTextField.getText().equals(settings.getUserName());
        modified |= !passwordPasswordField.getText().equals(settings.getPassword());
        modified |= !url.getText().equals(settings.getApiUrl());
        return modified;
    }
}
