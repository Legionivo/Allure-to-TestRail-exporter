package io.github.legionivo.plugin.api;

import io.github.legionivo.plugin.model.Section;
import io.github.legionivo.plugin.model.TestCase;
import io.github.legionivo.plugin.model.Project;
import io.github.legionivo.plugin.model.User;
import retrofit2.http.*;

import java.util.List;

public interface TestRailClient {

    @GET("/index.php%3F/api/v2/get_cases/{projectId}&suite_id={suite_id}&section_id={section_id}")
    List<TestCase> getTestCases(@Path("projectId") int projectId, @Path("suite_id") int suiteId,
                                @Path("section_id") int sectionId);

    @POST("/index.php%3F/api/v2/add_case/{sectionId}")
    TestCase addTestCase(@Path("sectionId") int sectionId, @Body TestCase testCase);

    @POST("/index.php%3F/api/v2/update_case/{caseId}")
    TestCase updateTestCase(@Path("caseId") int caseId, @Body TestCase testCase);

    @GET("/index.php%3F/api/v2/get_project/{projectId}")
    Project getProject(@Path("projectId") int projectId);

    @GET("/index.php%3F/api/v2/get_sections/{project_id}&suite_id={suite_id}")
    List<Section> getSections(@Path("project_id") int projectId, @Path("suite_id") int suiteId);

    @POST("/index.php%3F/api/v2/add_section/{projectId}")
    Section addSection(@Path("projectId") int projectId, @Body Section section);


    @GET("/index.php%3F/api/v2/get_user_by_email&email={email}")
    User getUserByEmail(@Path("email") String email);

}
