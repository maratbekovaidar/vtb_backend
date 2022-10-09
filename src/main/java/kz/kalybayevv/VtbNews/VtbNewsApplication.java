package kz.kalybayevv.VtbNews;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
public class VtbNewsApplication extends SpringBootServletInitializer {
	private static final Logger log = LoggerFactory.getLogger(VtbNewsApplication.class);

	/**
	 * Used when run as JAR; API will be like as a common
	 * @param args the command line arguments
	 * @throws UnknownHostException if the local host name could not be resolved into an address
	 */
	public static void main(String[] args) throws UnknownHostException {
		SpringApplication app = new SpringApplication(VtbNewsApplication.class);
		Environment env = app.run(args).getEnvironment();
		log.info("\n----------------------------------------------------------\n\t" +
						"Application '{}' is running! Access URLs:\n\t" +
						"Local: \t\t{}://localhost:{}\n\t" +
						"External: \t{}://{}:{}\n\t" +
						"\n----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				"http",
				env.getProperty("server.port"),
				"http",
				InetAddress.getLocalHost().getHostAddress(),
				env.getProperty("server.port"));
	}

	/**
	 * Used when method of running application is WAR
	 * changes all API's of this module
	 * @param builder SpringApplicationBuilder
	 * @return takes the application context after the server port
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(VtbNewsApplication.class);
	}
}
