package io.github.legionivo.plugin.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMethod;
import io.github.legionivo.plugin.Annotations;
import io.github.legionivo.plugin.model.TestCase;

public class CaseAnnotationUtils {
    public static void createCaseIdAnnotation(TestCase testCase, PsiMethod method) {
        final PsiAnnotation annotation = PsiUtils.createAnnotation(getCaseAnnotationText(testCase.getId()), method);
        final Project project = method.getProject();

        PsiAnnotation displayNameAnnotation = method.getAnnotation(Annotations.JUNIT_DISPLAY_NAME_ANNOTATION);

        CommandProcessor.getInstance().executeCommand(project, () -> ApplicationManager.getApplication().runWriteAction(() -> {
            PsiUtils.addImport(method.getContainingFile(), Annotations.ALLURE2_TMS_LINK_ANNOTATION);

            method.getModifierList().addBefore(annotation, displayNameAnnotation);
        }), "Insert TestRail id", null);
    }

    private static String getCaseAnnotationText(int id) {
        return String.format("@%s(\"%s\")", Annotations.ALLURE2_TMS_LINK_ANNOTATION, id);
    }
}
