package emissary.transform;

import java.io.IOException;
import java.util.stream.Stream;

import emissary.place.IServiceProviderPlace;
import emissary.test.core.junit5.ExtractionTest;
import org.junit.jupiter.params.provider.Arguments;

public class JsonEscapePlaceTest extends ExtractionTest {

    public static Stream<? extends Arguments> data() {
        return getMyTestParameterFiles(JsonEscapePlaceTest.class);
    }

    @Override
    public IServiceProviderPlace createPlace() throws IOException {
        return new JsonEscapePlace();
    }
}
