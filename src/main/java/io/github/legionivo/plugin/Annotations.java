package io.github.legionivo.plugin;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Annotations {

    static final String ALLURE2_STEP_ANNOTATION = "io.qameta.allure.Step";
    static final String ALLURE2_FEATURE_ANNOTATION = "io.qameta.allure.Feature";
    static final String ALLURE2_LINK_ANNOTATION = "io.qameta.allure.Link";
    static final String ALLURE2_TMS_LINK_ANNOTATION = "io.qameta.allure.TmsLink";
    static final String OWNER_KEY_ANNOTATION = "org.aeonbits.owner.Config.Key";

    static final String JUNIT_TEST_ANNOTATION = "org.junit.jupiter.api.Test";
    static final String JUNIT_BEFORE_ALL_ANNOTATION = "org.junit.jupiter.api.BeforeAll";
    static final String JUNIT_BEFORE_EACH_ANNOTATION = "org.junit.jupiter.api.BeforeEach";
    static final String JUNIT_DISPLAY_NAME_ANNOTATION = "org.junit.jupiter.api.DisplayName";

}
