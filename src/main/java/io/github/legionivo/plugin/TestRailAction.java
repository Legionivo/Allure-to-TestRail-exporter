package io.github.legionivo.plugin;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.github.legionivo.plugin.model.TestCase;
import io.github.legionivo.plugin.util.PsiUtils;

import java.util.Objects;

public class TestRailAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final PsiElement element = e.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            TestRailApiWrapper testRail = new TestRailApiWrapper(Objects.requireNonNull(Settings.getInstance(method.getProject())));
            PsiClass testClass = (PsiClass) method.getParent();
            PsiAnnotation epicAnnotation = testClass.getAnnotation(Annotations.ALLURE2_FEATURE_ANNOTATION);
            String epicName = AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(epicAnnotation), "value");

            if (method.hasAnnotation(Annotations.ALLURE2_TMS_LINK_ANNOTATION)) {
                testRail.updateTestCase(method);
            } else {
                int sectionId = testRail.createSections(epicName);
                TestCase testCase = testRail.createTestCase(sectionId, method);
                createCaseIdAnnotation(testCase, method);
            }
            Notifications.Bus.notify(new Notification("TestRail.Action",
                    "Export to TestRail",
                    "Finished exporting [" + method.getName() + "]",
                    NotificationType.INFORMATION));
        }
    }

    private void createCaseIdAnnotation(TestCase testCase, PsiMethod method) {
        final PsiAnnotation annotation = PsiUtils.createAnnotation(getCaseAnnotationText(testCase.getId()), method);
        final Project project = method.getProject();

        PsiAnnotation displayNameAnnotation = method.getAnnotation(Annotations.JUNIT_DISPLAY_NAME_ANNOTATION);

        CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
            PsiUtils.addImport(method.getContainingFile(), Annotations.ALLURE2_TMS_LINK_ANNOTATION);

            method.getModifierList().addBefore(annotation, displayNameAnnotation);
        }), "Insert TestRail id", null);
    }

    private String getCaseAnnotationText(int id) {
        return String.format("@%s(\"%s\")", Annotations.ALLURE2_TMS_LINK_ANNOTATION, id);
    }
}
