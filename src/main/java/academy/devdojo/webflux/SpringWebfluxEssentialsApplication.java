package academy.devdojo.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.blockhound.BlockHound;

@SpringBootApplication
public class SpringWebfluxEssentialsApplication {

    static {
        BlockHound.install(
                builder -> builder
                        .allowBlockingCallsInside("java.util.UUID", "randomUUID")
                        .allowBlockingCallsInside("java.io.InputStream", "readNBytes")
                        .allowBlockingCallsInside("java.io.FilterInputStream", "read")
                        .allowBlockingCallsInside("sun.misc.Unsafe", "park")
                        .allowBlockingCallsInside("java.util.concurrent.locks.LockSupport", "park"));
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebfluxEssentialsApplication.class, args);
    }
}
