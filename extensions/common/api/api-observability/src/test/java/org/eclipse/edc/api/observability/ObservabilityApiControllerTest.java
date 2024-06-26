/*
 *  Copyright (c) 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - Initial implementation
 *
 */

package org.eclipse.edc.api.observability;

import io.restassured.specification.RequestSpecification;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.spi.system.health.HealthCheckResult;
import org.eclipse.edc.spi.system.health.HealthCheckService;
import org.eclipse.edc.spi.system.health.HealthStatus;
import org.eclipse.edc.web.jersey.testfixtures.RestControllerTestBase;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ApiTest
class ObservabilityApiControllerTest extends RestControllerTestBase {

    private final HealthCheckService healthCheckService = mock(HealthCheckService.class);

    @Test
    void checkHealth() {
        when(healthCheckService.getStartupStatus()).thenReturn(new HealthStatus(successResult()));

        baseRequest()
                .get("/health")
                .then()
                .statusCode(200)
                .contentType(JSON);

        verify(healthCheckService, times(1)).getStartupStatus();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void checkHealth_mixedResults() {
        when(healthCheckService.getStartupStatus()).thenReturn(new HealthStatus(Stream.concat(successResult().stream(), failedResult().stream()).toList()));

        baseRequest()
                .get("/health")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).getStartupStatus();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void checkHealth_noProviders() {
        when(healthCheckService.getStartupStatus()).thenReturn(new HealthStatus());

        baseRequest()
                .get("/health")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).getStartupStatus();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getLiveness() {
        when(healthCheckService.isLive()).thenReturn(new HealthStatus(successResult()));

        baseRequest()
                .get("/liveness")
                .then()
                .statusCode(200)
                .contentType(JSON);

        verify(healthCheckService, times(1)).isLive();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getLiveness_mixedResults() {
        when(healthCheckService.isLive()).thenReturn(new HealthStatus(Stream.concat(successResult().stream(), failedResult().stream()).toList()));

        baseRequest()
                .get("/liveness")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).isLive();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getLiveness_noProviders() {
        when(healthCheckService.isLive()).thenReturn(new HealthStatus());

        baseRequest()
                .get("/liveness")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).isLive();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getReadiness() {
        when(healthCheckService.isReady()).thenReturn(new HealthStatus(successResult()));

        baseRequest()
                .get("/readiness")
                .then()
                .statusCode(200)
                .contentType(JSON);

        verify(healthCheckService, times(1)).isReady();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getReadiness_mixedResults() {
        when(healthCheckService.isReady()).thenReturn(new HealthStatus(Stream.concat(successResult().stream(), failedResult().stream()).toList()));

        baseRequest()
                .get("/readiness")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).isReady();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getReadiness_noProvider() {
        when(healthCheckService.isReady()).thenReturn(new HealthStatus());

        baseRequest()
                .get("/readiness")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).isReady();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getStartup() {
        when(healthCheckService.getStartupStatus()).thenReturn(new HealthStatus(successResult()));

        baseRequest()
                .get("/startup")
                .then()
                .statusCode(200)
                .contentType(JSON);

        verify(healthCheckService, times(1)).getStartupStatus();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getStartup_mixedResults() {

        when(healthCheckService.getStartupStatus()).thenReturn(new HealthStatus(Stream.concat(successResult().stream(), failedResult().stream()).toList()));

        baseRequest()
                .get("/startup")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).getStartupStatus();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Test
    void getStartup_noProviders() {
        when(healthCheckService.getStartupStatus()).thenReturn(new HealthStatus());

        baseRequest()
                .get("/startup")
                .then()
                .statusCode(503)
                .contentType(JSON);

        verify(healthCheckService, times(1)).getStartupStatus();
        verifyNoMoreInteractions(healthCheckService);
    }

    @Override
    protected Object controller() {
        return new ObservabilityApiController(healthCheckService);
    }

    private List<HealthCheckResult> failedResult() {
        return Collections.singletonList(HealthCheckResult.Builder.newInstance().component("test component").failure("test failure").build());
    }

    private Collection<HealthCheckResult> successResult() {
        return Collections.singletonList(HealthCheckResult.Builder.newInstance().component("test component").success().build());
    }

    private RequestSpecification baseRequest() {
        return given()
                .baseUri("http://localhost:" + port + "/check")
                .when();
    }
}
