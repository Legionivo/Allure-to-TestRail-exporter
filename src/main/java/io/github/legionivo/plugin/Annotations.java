package io.github.legionivo.plugin;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Annotations {

    public static final String ALLURE2_STEP_ANNOTATION = "io.qameta.allure.Step";
    public static final String ALLURE2_FEATURE_ANNOTATION = "io.qameta.allure.Feature";
    public static final String ALLURE2_LINK_ANNOTATION = "io.qameta.allure.Link";
    public static final String ALLURE2_TMS_LINK_ANNOTATION = "io.qameta.allure.TmsLink";
    public static final String ALLURE2_ISSUE_ANNOTATION = "io.qameta.allure.Issue";
    public static final String OWNER_KEY_ANNOTATION = "org.aeonbits.owner.Config.Key";

    public static final String JUNIT_TEST_ANNOTATION = "org.junit.jupiter.api.Test";
    public static final String JUNIT_BEFORE_ALL_ANNOTATION = "org.junit.jupiter.api.BeforeAll";
    public static final String JUNIT_BEFORE_EACH_ANNOTATION = "org.junit.jupiter.api.BeforeEach";
    public static final String JUNIT_DISPLAY_NAME_ANNOTATION = "org.junit.jupiter.api.DisplayName";

}
