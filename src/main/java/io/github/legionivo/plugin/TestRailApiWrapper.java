package io.github.legionivo.plugin;

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.lang.properties.PropertiesImplUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.PsiMethodCallExpressionImpl;
import com.intellij.psi.impl.source.tree.java.PsiReferenceExpressionImpl;
import io.github.legionivo.plugin.api.TestRailClient;
import io.github.legionivo.plugin.api.TestRailClientBuilder;
import io.github.legionivo.plugin.enums.State;
import io.github.legionivo.plugin.model.Section;
import io.github.legionivo.plugin.model.TestCase;
import io.github.legionivo.plugin.model.TestStep;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class TestRailApiWrapper {

    private final Settings settings;
    private TestRailClient testRailClient;

    TestRailApiWrapper(Settings settings) {
        this.settings = settings;
        this.testRailClient = new TestRailClientBuilder(settings.getApiUrl(), settings.getUserName(), settings.getPassword())
                .build();
    }

    int createSections(String... sections) {
        int parentSectionId = -1;
        for (int i = 0; i < sections.length; i++) {
            if (i == 0) {
                parentSectionId = saveSection(settings.getProjectId(), settings.getSuiteId(), sections[i]).getId();
            } else {
                parentSectionId = saveSection(settings.getProjectId(), settings.getSuiteId(), parentSectionId, sections[i]).getId();
            }
        }
        return parentSectionId;
    }

    private Section saveSection(int projectId, int suiteId, String name) {
        if (sectionExists(projectId, suiteId, name)) {
            return getSection(projectId, suiteId, name);
        }

        Section section = new Section();
        section.setSuiteId(suiteId);
        section.setName(name);

        return createSection(projectId, section);
    }

    private Section saveSection(int projectId, int suiteId, int parentId, String name) {
        if (sectionExists(projectId, suiteId, name)) {
            return getSection(projectId, suiteId, name);
        }

        Section section = new Section();
        section.setSuiteId(suiteId);
        section.setParentId(parentId);
        section.setName(name);

        return createSection(projectId, section);
    }

    private boolean sectionExists(int projectId, int suiteId, String name) {
        try {
            return getSection(projectId, suiteId, name) != null;
        } catch (Exception e) {
            return false;
        }
    }

    private Section getSection(int projectId, int suiteId, String name) {
        return testRailClient.getSections(projectId, suiteId)
                .stream()
                .filter(it -> it.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No such section"));
    }

    private Section createSection(int projectId, Section section) {
        return testRailClient.addSection(projectId, section);
    }

    TestCase createTestCase(int sectionId, PsiMethod testMethod) {
        TestCase testCase = setTestCaseDetails(testMethod);
        return saveTestCase(sectionId, testCase);
    }

    void updateTestCase(PsiMethod testMethod) {
        TestCase testCase = setTestCaseDetails(testMethod);
        int id = Integer.parseInt(Objects.requireNonNull(AnnotationUtil.getStringAttributeValue(
                Objects.requireNonNull(testMethod.getAnnotation(Annotations.ALLURE2_TMS_LINK_ANNOTATION)), "value")));
        updateTestCase(id, testCase);
    }

    private TestCase setTestCaseDetails(PsiMethod testMethod) {
        TestCase testCase = new TestCase();
        testCase.setCustomSteps(toScenario(getSteps(testMethod)));

        String title = AnnotationUtil.getStringAttributeValue(Objects.requireNonNull(
                testMethod.getAnnotation(Annotations.JUNIT_DISPLAY_NAME_ANNOTATION)), "value");
        testCase.setTitle(title);
        testCase.setCustomState(State.AUTOMATED.getValue());
        testCase.setRefs(getLinkRef(testMethod));
        testCase.setTypeId(1);
        return testCase;
    }


    private String toScenario(final List<TestStep> steps) {
        return steps.stream()
                .filter(Objects::nonNull)
                .map(TestStep::getName)
                .map(step -> step.replace("{", "\\{"))
                .map(step -> "# " + step)
                .collect(Collectors.joining("\r\n"));
    }

    private static List<TestStep> getSteps(final PsiMethod method) {
        List<TestStep> list = new ArrayList<>();
        PsiClass testClass = (PsiClass) method.getParent();
        PsiMethod[] classMethods = testClass.getMethods();

        // BeforeAll, BeforeEach annotations in class
        for (PsiMethod classMethod : classMethods) {
            if (isBeforeAllAnnotation(classMethod) || isBeforeEachAnnotation(classMethod)) {
                final PsiStatement[] statements = Optional.ofNullable(classMethod.getBody())
                        .map(PsiCodeBlock::getStatements)
                        .orElse(new PsiStatement[]{});

                final List<PsiMethodCallExpression> methodCallExpressions = new ArrayList<>();
                for (PsiStatement statement : statements) {
                    PsiElement[] children = statement.getChildren();
                    for (PsiElement t : children) {
                        if (t instanceof PsiMethodCallExpression) {
                            PsiMethodCallExpression expression = (PsiMethodCallExpression) t;
                            if (isStepMethod(Objects.requireNonNull(((PsiMethodCallExpression) t).resolveMethod()))) {
                                methodCallExpressions.add(expression);
                            }
                        }
                    }
                }
                //Get Steps from methods in Before* annotations
                for (PsiMethodCallExpression methodCallExpression : methodCallExpressions) {
                    list.add(getStepsFromMethod(methodCallExpression)); //Get steps from test body
                }
            }
        }

        PsiCodeBlock block = method.getBody();
        List<PsiMethodCallExpression> methodCallExpressions = SyntaxTraverser.psiTraverser().withRoot(block)
                .postOrderDfsTraversal().filter(PsiMethodCallExpression.class).toList();

        for (PsiMethodCallExpression methodCallExpression : methodCallExpressions) {
            list.add(getStepsFromMethod(methodCallExpression)); //Get steps from test body
        }
        return list;
    }

    private static String extractStringFromVarArgs(PsiExpression[] expressions) {
        StringJoiner joiner = new StringJoiner(", ");

        for (PsiExpression psiExpression : expressions) {
            joiner.add(getValueFromExpression(psiExpression));
        }
        return joiner.toString();
    }

    private static TestStep getStepsFromMethod(PsiMethodCallExpression methodCallExpression) {
        Map<String, String> expressionsMap = new HashMap<>();
        TestStep step = null;
        PsiExpression[] expressions = methodCallExpression.getArgumentList().getExpressions();
        PsiParameter[] methodParameters = Objects.requireNonNull(methodCallExpression.resolveMethod()).getParameterList().getParameters();

        if (methodCallExpression.getText().toLowerCase().startsWith("assert")) {
            step = new TestStep().setName(methodCallExpression.getText().replace("\"", "'"));
        } else {
            int counter = 0;
            while (counter < expressions.length) {
                if (!methodParameters[counter].isVarArgs()) {
                    expressionsMap.put(methodParameters[counter].getName(), getValueFromExpression(expressions[counter]));
                    counter++;
                } else {
                    expressionsMap.put(methodParameters[counter].getName(), extractStringFromVarArgs(expressions));
                    counter = expressions.length;
                }
            }

            PsiMethod m = methodCallExpression.resolveMethod();
            if (m != null) {
                if (isStepMethod(m)) {
                    PsiAnnotation annotation = m.getAnnotation(Annotations.ALLURE2_STEP_ANNOTATION);
                    String stringAttributeValue = AnnotationUtil.getStringAttributeValue(Objects.requireNonNull(annotation), "value");
                    String stepText = processNameTemplate(stringAttributeValue, expressionsMap).replaceAll("`", "\"");
                    String stepName = Objects.requireNonNull(stepText);
                    step = new TestStep().setName(stepName);
                }
            }
        }
        return step;
    }

    private static String processNameTemplate(final String template, final Map<String, String> params) {
        final Matcher matcher = Pattern.compile("\\{([^}]*)}").matcher(template);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String pattern = matcher.group(1);
            final String replacement = processPattern(pattern, params).orElseGet(matcher::group);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Optional<String> processPattern(final String pattern, final Map<String, String> params) {
        if (pattern.isEmpty()) {
            return Optional.empty();
        }
        final String[] parts = pattern.split("\\.");
        final String parameterName = parts[0];
        if (!params.containsKey(parameterName)) {
            return Optional.empty();
        }
        return Optional.of(params.get(parameterName));
    }

    private static String getValueFromExpression(PsiExpression expression) {
        String text = null;
        if (expression instanceof PsiMethodCallExpression) {
            PsiMethod method = ((PsiMethodCallExpressionImpl) expression).resolveMethod();
            if (Objects.requireNonNull(method).hasAnnotation(Annotations.OWNER_KEY_ANNOTATION) || method.getAnnotations().length > 0) {
                PsiModifierList modifierList = method.getModifierList();
                Project project = expression.getProject();
                PsiAnnotation[] list = modifierList.getAnnotations();
                PsiLiteralExpression expression1 = (PsiLiteralExpression) list[0].findAttributeValue("value");
                String parameterValue = Objects.requireNonNull(expression1).getText().replace("\"", "");
                text = PropertiesImplUtil.findPropertiesByKey(project, parameterValue).get(0).getValue();
            } else text = expression.getText();
        } else if (expression instanceof PsiReferenceExpression) {
            PsiVariable variable = (PsiVariable) ((PsiReferenceExpressionImpl) expression).resolve();
            if (Objects.requireNonNull(variable).hasInitializer()) {
                PsiExpression initializer = Objects.requireNonNull(variable).getInitializer();
                if (initializer == null) {
                    text = Objects.requireNonNull(variable.getNameIdentifier()).getText();
                } else text = Objects.requireNonNull(initializer).getText().replace("\"", "");
            } else text = expression.getText();
        } else if (expression instanceof PsiLiteralExpression) {
            text = expression.getText().replaceAll("^\"|\"$", "");
        } else if (expression instanceof PsiPrefixExpression) {
            text = expression.getText();
        } else if (expression instanceof PsiPolyadicExpression)
            text = getValueFromPsiPolyadicExpression((PsiPolyadicExpression) expression);
        return text;

    }

    private static String getValueFromPsiPolyadicExpression(PsiPolyadicExpression polyadicExpression) {
        PsiExpression[] operands = polyadicExpression.getOperands();
        StringBuilder fullExpression = new StringBuilder();

        Arrays.stream(operands).forEach(operand -> {
            if (operand instanceof PsiReferenceExpression) {
                PsiElement element = ((PsiReferenceExpression) operand).resolve();
                if (element instanceof PsiField) {
                    List<PsiReferenceExpression> list = SyntaxTraverser.psiTraverser().withRoot(element).filter(PsiReferenceExpression.class).toList();
                    if (list.size() > 0) {
                        PsiField psiField = ((PsiField) Objects.requireNonNull(list.get(0).resolve()));
                        fullExpression.append(Objects.requireNonNull(psiField.getInitializer()).getText());
                    } else
                        fullExpression.append(Objects.requireNonNull(((PsiField) element).getInitializer()).getText());
                }
            } else if (operand instanceof PsiLiteralExpression) {
                fullExpression.append(((PsiLiteralExpression) operand).getValue());
            }
        });
        return fullExpression.toString();
    }


    private static boolean isStepMethod(final PsiMethod method) {
        return method.hasAnnotation(Annotations.ALLURE2_STEP_ANNOTATION);
    }

    private static boolean isBeforeEachAnnotation(final PsiMethod method) {
        return method.hasAnnotation(Annotations.JUNIT_BEFORE_EACH_ANNOTATION);
    }

    private static boolean isBeforeAllAnnotation(final PsiMethod method) {
        return method.hasAnnotation(Annotations.JUNIT_BEFORE_ALL_ANNOTATION);
    }

    private TestCase saveTestCase(int sectionId, TestCase testCase) {
        TestCase tCase = getTestCase(sectionId, testCase.getTitle());
        if (tCase != null) {
            return tCase;
        }

        return testRailClient.addTestCase(sectionId, testCase);
    }

    private void updateTestCase(int caseId, TestCase testCase) {
        testRailClient.updateTestCase(caseId, testCase);
    }

    private String getLinkRef(PsiMethod testMethod) {
        if (testMethod.hasAnnotation(Annotations.ALLURE2_LINK_ANNOTATION) && testMethod.hasAnnotation(Annotations.ALLURE2_ISSUE_ANNOTATION)) {
            return AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(testMethod.getAnnotation(Annotations.ALLURE2_LINK_ANNOTATION)), "name")
                    + ", " + AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(testMethod.getAnnotation(Annotations.ALLURE2_ISSUE_ANNOTATION)), "value");
        } else if (testMethod.hasAnnotation(Annotations.ALLURE2_LINK_ANNOTATION)) {
            return AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(testMethod.getAnnotation(Annotations.ALLURE2_LINK_ANNOTATION)), "name");
        } else if (testMethod.hasAnnotation(Annotations.ALLURE2_ISSUE_ANNOTATION)) {
            return AnnotationUtil.getDeclaredStringAttributeValue(Objects.requireNonNull(testMethod.getAnnotation(Annotations.ALLURE2_ISSUE_ANNOTATION)), "value");
        }
        return "";
    }

    private List<TestCase> getTestCases(int sectionId) {
        return testRailClient.getTestCases(settings.getProjectId(), settings.getSuiteId(), sectionId);
    }

    private TestCase getTestCase(int sectionId, String testCaseTitle) {
        return getTestCases(sectionId).stream()
                .filter(it -> it.getTitle()
                        .equals(testCaseTitle))
                .findFirst()
                .orElse(null);
    }

}


