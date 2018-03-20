package com.ln.xproject.base.application;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.base.TestBase;
import io.restassured.RestAssured;

/**
 * Created by ning on 1/15/18.
 */
public class ApplicaitonServiceTest extends TestBase {
    @Autowired
    private ApplicationAuditService applicationAuditService;

    @Test
    public void testGetTicket() {
        String idNo = "659001198712239741";
        String amount = "151200";
        String tid = this.generateTid(idNo, amount);
        System.out.println(tid);
    }

    private String generateTid(String idNo, String amount) {
        return RestAssured.given().auth().basic("youxin", "123456").params("idNo", idNo, "amount", amount)
                .post("http://172.16.4.86:9191/api/rest/lend/test2/").then().extract().jsonPath()
                .getString("ticket.tid");
    }

}
