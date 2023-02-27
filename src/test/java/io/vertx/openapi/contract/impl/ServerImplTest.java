/*
 * Copyright (c) 2023, SAP SE
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *
 */

package io.vertx.openapi.contract.impl;

import io.vertx.core.json.JsonObject;
import io.vertx.openapi.contract.ContractErrorType;
import io.vertx.openapi.contract.OpenAPIContractException;
import io.vertx.openapi.contract.Server;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServerImplTest {

  @Test
  void testGetters() {
    String url = "http://foo.bar/foobar";
    JsonObject model = new JsonObject().put("url", url);
    Server server = new ServerImpl(model);

    assertThat(server.getOpenAPIModel()).isEqualTo(model);
    assertThat(server.getURL()).isEqualTo(url);
    assertThat(server.getBasePath()).isEqualTo("/foobar");
  }

  @Test
  void testExceptions() {
    String msgUnsupported = "The passed OpenAPI contract contains a feature that is not supported: Server Variables";

    OpenAPIContractException exceptionUnsupported =
      assertThrows(OpenAPIContractException.class,
        () -> new ServerImpl(new JsonObject().put("url", "http://{foo}.bar")));
    assertThat(exceptionUnsupported.type()).isEqualTo(ContractErrorType.UNSUPPORTED_FEATURE);
    assertThat(exceptionUnsupported).hasMessageThat().isEqualTo(msgUnsupported);

    String msgInvalid = "The passed OpenAPI contract is invalid: The specified URL is malformed: http://foo.bar:-80";
    OpenAPIContractException exceptionINvalid =
      assertThrows(OpenAPIContractException.class,
        () -> new ServerImpl(new JsonObject().put("url", "http://foo.bar:-80")));
    assertThat(exceptionINvalid.type()).isEqualTo(ContractErrorType.INVALID_SPEC);
    assertThat(exceptionINvalid).hasMessageThat().isEqualTo(msgInvalid);
  }
}
