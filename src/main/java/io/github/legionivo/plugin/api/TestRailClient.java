package io.github.legionivo.plugin.api;

import io.github.legionivo.plugin.model.*;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface TestRailClient {

    @GET("/index.php%3F/api/v2/get_cases/{projectId}&suite_id={suite_id}")
    CasesResponse getTestCases(@Path("projectId") int projectId, @Path("suite_id") int suiteId);

    @POST("/index.php%3F/api/v2/add_case/{sectionId}")
    TestCase addTestCase(@Path("sectionId") int sectionId, @Body TestCase testCase);

    @POST("/index.php%3F/api/v2/update_case/{caseId}")
    TestCase updateTestCase(@Path("caseId") int caseId, @Body TestCase testCase);

    @GET("/index.php%3F/api/v2/get_project/{projectId}")
    Project getProject(@Path("projectId") int projectId);

    @GET("/index.php%3F/api/v2/get_sections/{project_id}&suite_id={suite_id}")
    SectionsResponse getSections(@Path("project_id") int projectId, @Path("suite_id") int suiteId);

    @POST("/index.php%3F/api/v2/add_section/{projectId}")
    Section addSection(@Path("projectId") int projectId, @Body Section section);

    @GET("/index.php%3F/api/v2/get_user_by_email&email={email}")
    User getUserByEmail(@Path("email") String email);

}
