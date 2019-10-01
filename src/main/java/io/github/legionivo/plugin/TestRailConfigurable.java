package io.github.legionivo.plugin;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import io.github.legionivo.plugin.gui.TestRailExporterForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TestRailConfigurable implements Configurable {

    private TestRailExporterForm exporterForm;

    @SuppressWarnings("FieldCanBeLocal")
    private final Project mProject;

    public TestRailConfigurable(Project mProject) {
        this.mProject = mProject;
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Allure to TestRail Exporter Plugin";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        exporterForm = new TestRailExporterForm();
        exporterForm.createUI(mProject);
        return exporterForm.getRootPanel();
    }

    @Override
    public boolean isModified() {
        return exporterForm.isModified();
    }

    @Override
    public void apply() {
        exporterForm.apply();
    }

    @Override
    public void reset() {
        exporterForm.reset();
    }

    @Override
    public void disposeUIResources() {
        exporterForm = null;
    }
}
