import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.codehaus.groovy.syntax.Token;
import org.json.simple.JSONObject;
import static org.hamcrest.Matchers.equalTo;


//import org.junit.Test;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.testng.TestRunner.PriorityWeight.dependsOnMethods;

public class Testtt {

     String token;
    String baseURL ="https://api.namaa.sumerge.com";


    @Test
    public void TestPost() {

        JSONObject user = new JSONObject();
        user.put("mockRequest", true);
        user.put("nationalId", "1000000008");

        Response response =
                given()

                        .header("Content-Type", "application/json")
                        .contentType(ContentType.JSON)
                        .body(user.toJSONString())
                        .when()
                        .post("https://api.namaa.sumerge.com/auth-svc/api/v1/nafath")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .response();
        String transactionId = response.path("transactionId");
        System.out.println(transactionId);


        Response response1 =
                given().accept(ContentType.JSON)
                        .header("Referer", "https://portal.namaa.sumerge.com")
                        .when()
                        .get("https://api.namaa.sumerge.com/auth-svc/api/v1/nafath/status/" + transactionId)
                        .then()
                        .statusCode(200)
                        .extract().response();
        token = response1.getCookie("accessToken");

    }

    @Test(dependsOnMethods = "TestPost")
    public void AssertField(){
        JSONObject field = new JSONObject();
        given()
                .header("Cookie", "accessToken=" + token)
                .header("Referer", "https://portal.namaa.sumerge.com")
                .header("Content-Type", "application/json")
                .body(field.toJSONString())
                .when()
                .post("https://api.namaa.sumerge.com/bpm-svc/request/current?page=0&size=10")
                .then()
                .statusCode(200)
                .body("content[0].ownerFullName", equalTo("نورة حسن محمد عبدالعزيز"));


    }


}


