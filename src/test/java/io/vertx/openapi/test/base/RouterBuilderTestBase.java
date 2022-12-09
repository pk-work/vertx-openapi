package io.vertx.openapi.test.base;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.openapi.RouterBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@ExtendWith(VertxExtension.class)
public class RouterBuilderTestBase {
  private int port;
  private Vertx vertx;

  /**
   * Creates a HTTPServer based on the passed RouterBuilder.
   *
   * @param pathToContract      Path to the related OpenAPI contract
   * @param modifyRouterBuilder Function that allows to modify the RouterBuilder generated by the OpenAPI contract.
   * @return A Future which is succeeded when the server is started and failed if something went wrong.
   */
  public Future<Void> createServer(Path pathToContract,
    Function<RouterBuilder, Future<RouterBuilder>> modifyRouterBuilder) {
    return RouterBuilder.create(vertx, vertx.fileSystem().readFileBlocking(pathToContract.toString()).toJsonObject())
      .compose(modifyRouterBuilder)
      .compose(rb -> vertx.createHttpServer().requestHandler(rb.createRouter()).listen(0))
      .onSuccess(server -> port = server.actualPort()).mapEmpty();
  }

  @BeforeEach
  void setup(Vertx vertx) {
    this.vertx = vertx;
  }

  @AfterEach
  @Timeout(value = 2, timeUnit = TimeUnit.SECONDS)
  void tearDown(VertxTestContext testContext) {
    if (vertx != null) {
      vertx.close(testContext.succeedingThenComplete());
    } else {
      testContext.completeNow();
    }
  }

  /**
   * Returns a pre-configured HTTP request.
   *
   * @param method The HTTP method of the request
   * @param path   The path of the request
   * @return a pre-configured HTTP request.
   */
  public HttpRequest<Buffer> createRequest(HttpMethod method, String path) {
    WebClientOptions opts = new WebClientOptions().setDefaultHost("localhost").setDefaultPort(port);
    return WebClient.create(vertx, opts).request(method, path);
  }
}
