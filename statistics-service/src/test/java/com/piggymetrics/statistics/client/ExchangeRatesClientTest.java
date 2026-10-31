package com.piggymetrics.statistics.client;

import com.piggymetrics.statistics.domain.Currency;
import com.piggymetrics.statistics.domain.ExchangeRatesContainer;
import org.junit.Test;
import org.junit.ClassRule;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.sun.net.httpserver.HttpServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExchangeRatesClientTest {

	@ClassRule
	public static final ExternalResource ratesServer = new ExternalResource() {
		private HttpServer server;
		private String previousUrl;

		@Override
		protected void before() throws Exception {
			server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
			server.createContext("/latest", exchange -> {
				String query = exchange.getRequestURI().getRawQuery();
				String base = "base=EUR".equals(query) ? "EUR" : "USD";
				byte[] body = ("{\"base\":\"" + base + "\",\"rates\":{\"USD\":1,\"EUR\":0.9,\"RUB\":90}}").getBytes(StandardCharsets.UTF_8);
				exchange.getResponseHeaders().set("Content-Type", "application/json");
				exchange.sendResponseHeaders(200, body.length);
				try (java.io.OutputStream output = exchange.getResponseBody()) { output.write(body); }
			});
			server.start();
			previousUrl = System.getProperty("rates.url");
			System.setProperty("rates.url", "http://127.0.0.1:" + server.getAddress().getPort());
		}

		@Override
		protected void after() {
			if (previousUrl == null) System.clearProperty("rates.url");
			else System.setProperty("rates.url", previousUrl);
			server.stop(0);
		}
	};

	@Autowired
	private ExchangeRatesClient client;

	@Test
	public void shouldRetrieveExchangeRates() {

		ExchangeRatesContainer container = client.getRates(Currency.getBase());

		assertEquals(container.getDate(), LocalDate.now());
		assertEquals(container.getBase(), Currency.getBase());

		assertNotNull(container.getRates());
		assertNotNull(container.getRates().get(Currency.USD.name()));
		assertNotNull(container.getRates().get(Currency.EUR.name()));
		assertNotNull(container.getRates().get(Currency.RUB.name()));
	}

	@Test
	public void shouldRetrieveExchangeRatesForSpecifiedCurrency() {

		Currency requestedCurrency = Currency.EUR;
		ExchangeRatesContainer container = client.getRates(requestedCurrency);

		assertEquals(container.getDate(), LocalDate.now());
		assertEquals(container.getBase(), requestedCurrency);

		assertNotNull(container.getRates());
		assertNotNull(container.getRates().get(requestedCurrency.name()));
	}
}
