package emissary.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.HashMap;
import java.util.Map;

import com.codahale.metrics.Timer;
import emissary.config.Configurator;
import emissary.config.ServiceConfigGuide;
import emissary.test.core.junit5.UnitTest;
import org.junit.jupiter.api.Test;

class MetricsManagerTest extends UnitTest {
    @Test
    void testMetricsReporting() {
        Configurator conf = new ServiceConfigGuide();
        conf.addEntry("JVM_METRICS_ENABLED", "true");
        conf.addEntry("GRAPHITE_METRICS_ENABLED", "true");
        conf.addEntry("SLF4J_METRICS_ENABLED", "true");
        MetricsManager mm = new MetricsManager(conf);
        Map<String, Timer> stats = new HashMap<>();
        assertDoesNotThrow(() -> mm.logMetrics(stats));
        // need to assert some stuff here, but what?
    }
}
