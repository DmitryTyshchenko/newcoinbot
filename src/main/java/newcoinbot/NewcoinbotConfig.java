package newcoinbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ztysdmy.binance.http.HttpBinanceApi;

@Configuration
public class NewcoinbotConfig {

	@Bean
	public Trading trading() {
		return new Trading(new HttpBinanceApi("", 
				""));
	}
}
