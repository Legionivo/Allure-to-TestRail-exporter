package io.github.legionivo.plugin;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.github.legionivo.plugin.model.TestCase;
import io.github.legionivo.plugin.util.AnnotationUtils;
import io.github.legionivo.plugin.util.CaseAnnotationUtils;
import io.github.legionivo.plugin.util.MethodNameWithExceptionHolder;
import io.github.legionivo.plugin.util.NotificationUtils;

import java.util.Objects;

public class TestRailActionSimplified extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
        final PsiElement element = e.getData(PlatformDataKeys.PSI_ELEMENT);
        if (element instanceof PsiMethod) {
            PsiMethod method = (PsiMethod) element;
            TestRailApiWrapper testRail = new TestRailApiWrapper(Objects.requireNonNull(Settings.getInstance(method.getProject())));
            PsiClass testClass = (PsiClass) method.getParent();
            PsiAnnotation featureClassAnnotation = testClass.getAnnotation(Annotations.ALLURE2_FEATURE_ANNOTATION);
            PsiAnnotation featureMethodAnnotation = method.getAnnotation(Annotations.ALLURE2_FEATURE_ANNOTATION);
            String featureName;
            String caseId = null;

            try {
                featureName = AnnotationUtils.getFeatureAnnotation(featureClassAnnotation, featureMethodAnnotation);
            } catch (NullPointerException ex) {
                NotificationUtils.showFeatureAnnotationNotFoundNotification();
                return;
            }

            if (method.hasAnnotation(Annotations.ALLURE2_TMS_LINK_ANNOTATION)) {
                testRail.updateTestCaseSimplified(method);
                caseId =
                        AnnotationUtil.getDeclaredStringAttributeValue(
                                Objects.requireNonNull(method.getAnnotation(Annotations.ALLURE2_TMS_LINK_ANNOTATION)), "value");
                NotificationUtils.showSuccessfulNotification(method, caseId);
            } else {
                int sectionId = testRail.createSections(featureName);
                TestCase testCase = null;
                try {
                    testCase = testRail.createTestCase(sectionId, method, true);
                    caseId = String.valueOf(testCase.getId());
                    CaseAnnotationUtils.createCaseIdAnnotation(testCase, method);
                    NotificationUtils.showSuccessfulNotification(method, caseId);
                } catch (Exception ex) {
                    NotificationUtils.showMethodErrorNotification(MethodNameWithExceptionHolder.methodName);
                    ex.printStackTrace();
                }
            }
        }
    }
}


