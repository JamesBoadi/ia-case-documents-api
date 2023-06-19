package uk.gov.hmcts.reform.iacasedocumentsapi.component.testutils;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.Slf4jNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.microsoft.applicationinsights.web.internal.WebRequestTrackingFilter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import uk.gov.hmcts.reform.iacasedocumentsapi.Application;

@ActiveProfiles("integration")
@TestPropertySource(properties = {
    "S2S_URL=http://127.0.0.1:8992/serviceAuth",
    "IDAM_URL=http://127.0.0.1:8992/userAuth",
    "OPEN_ID_IDAM_URL=http://127.0.0.1:8992/userAuth",
    "docmosis.endpoint=http://127.0.0.1:8992",
    "docmosis.render.uri=/docmosis",
    "ccdGatewayUrl=http://127.0.0.1:8992",
    "emBundler.url=http://127.0.0.1:8992"})
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = {TestConfiguration.class, Application.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class SpringBootIntegrationTest {

    protected GivensBuilder given;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected IaCaseDocumentsApiClient iaCaseDocumentsApiClient;

    @Autowired
    private WebApplicationContext wac;

    protected static WireMockServer server;

    @BeforeAll
    public void spinUp() {
        server = new WireMockServer(WireMockConfiguration.options()
            .notifier(new Slf4jNotifier(true))
            .port(8992));
        server.start();
    }

    @BeforeEach
    void setUp() {
        WebRequestTrackingFilter filter;
        filter = new WebRequestTrackingFilter();
        filter.init(new MockFilterConfig());
        mockMvc = webAppContextSetup(wac).addFilters(filter).build();
    }

    @BeforeEach
    public void setUpApiClient() {
        iaCaseDocumentsApiClient = new IaCaseDocumentsApiClient(objectMapper, mockMvc);
    }

    @AfterEach
    public void reset() {
        server.resetMappings();
        server.resetRequests();
        server.resetScenarios();
        server.resetAll();
    }

    @AfterAll
    @SneakyThrows
    @SuppressWarnings("java:S2925")
    public void shutDown() {
        server.stop();
        /*
            We are not using Wiremock the way it's intended to be used. It should be used by
            starting a webserver at the beginning of all tests and taking it down at the end, but
            what we do is spinning up and down the server all the time and change its mappings
            all the time.
            The result is that its behaviour is somewhat flaky.
            The following pause is meant to allow Wiremock time to conclude some operations that
            we invoke.
        */
        Thread.sleep(1000);
    }
}
