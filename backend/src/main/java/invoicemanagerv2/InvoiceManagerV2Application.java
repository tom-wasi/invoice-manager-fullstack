package invoicemanagerv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InvoiceManagerV2Application {

	public static void main(String[] args) {
		SpringApplication.run(InvoiceManagerV2Application.class, args);
	}
}
