package com.techchallenge.bdd;



import com.techchallenge.infrastructure.api.request.PayRequest;
import com.techchallenge.infrastructure.api.request.PaymentRequest;
import com.techchallenge.infrastructure.api.request.PaymentWebhookRequest;
import com.techchallenge.util.PaymentHelper;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.restassured.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsMapContaining.hasKey;


public class StepDefinition {

    private Response response;
    PaymentRequest paymentRequest;

    PayRequest payRequest;
    String externalReference;



    private String ENDPOINT_PAYMENT = "http://localhost:8085/payment/api/v1";

    @Dado("que quero criar um pagamento")
    public void que_quero_criar_um_pagamento() {
        PaymentHelper helper = new PaymentHelper();
        paymentRequest = helper.paymentRequest();

    }
    @Dado("quando informar todos os campos obrigatórios")
    public void quando_informar_todos_os_campos_obrigatórios() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(paymentRequest).when().post(ENDPOINT_PAYMENT+"/create");
    }
    @Entao("quero cadastrar um pagamento")
    public void quero_cadastrar_um_pagamento() {
        response.then().statusCode(HttpStatus.CREATED.value());
    }

    @Dado("que tenho um pagamento cadastrado")
    public void que_tenho_um_pagamento_cadastrado() {
        externalReference = "854758";
    }
    @Quando("fizer uma consulta por externalReference")
    public void fizer_uma_consulta_por_external_reference() {
        response = given().contentType(MediaType.APPLICATION_JSON_VALUE).
                 when().get(ENDPOINT_PAYMENT+"/find/{externalReference}", externalReference);
    }
    @Entao("devo retornar o pagamento")
    public void devo_retornar_o_pagamento() {
        response.then().statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("./data/payment-schema.json"))
                .body(matchesJsonSchemaInClasspath("./data/payment-response.json"));;
    }

}
