package emissary.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import emissary.config.Configurator;
import emissary.config.ServiceConfigGuide;
import emissary.test.core.junit5.UnitTest;
import emissary.util.WindowedSeekableByteChannel;
import org.junit.jupiter.api.Test;

class ParserFactoryTest extends UnitTest {

    @Test
    void createFactoryWithSuppliedConfiguration() throws IOException {
        Configurator cfg = new ServiceConfigGuide();
        cfg.addEntry("PARSER_NIO_IMPL_FOO", "emissary.parser.TestParserFactory$SampleParser");
        cfg.addEntry("DEFAULT_NIO_PARSER", "emissary.parser.TestParserFactory$DefaultNioParser");
        ParserFactory pf = new ParserFactory(cfg);
        ReadableByteChannel c = Channels.newChannel(new ByteArrayInputStream("aaa".getBytes()));
        assertEquals(DataIdentifier.UNKNOWN_TYPE, pf.identify(new WindowedSeekableByteChannel(c, 24)), "Default parser used with null ID Engine");
    }

}
