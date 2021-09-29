package io.github.legionivo.plugin.util;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.psi.PsiAnnotation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AnnotationUtils {
    @Nullable
    public static String getFeatureAnnotation(PsiAnnotation featureClassAnnotation, PsiAnnotation featureMethodAnnotation) {
        String featureName;
        if (featureClassAnnotation != null && featureMethodAnnotation != null) {
            // if @Feature is present on a class and methods levels, method annotation is taken
            featureName = AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(featureMethodAnnotation), "value");
        } else if (featureClassAnnotation == null) {
            // if @Feature is not present on a class level, method annotation is taken
            featureName = AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(featureMethodAnnotation), "value");
        } else {
            // else take class level annotation
            featureName = AnnotationUtil.getDeclaredStringAttributeValue(featureClassAnnotation, "value");
        }
        return featureName;
    }
}
