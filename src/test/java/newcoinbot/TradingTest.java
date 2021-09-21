package newcoinbot;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TradingTest {

	@Test
	public void loadBoatsTest() throws Exception {
		var botsJson = "[{'pair': 'BTCBUSD','startTime': '2021-09-12 10:00', 'amount': 13.2, 'goals': [{'percent': 10, 'shift':'50'}]}]";
		var bots = Trading.loadBots(botsJson);
		var bot = bots.get(0);
		assertNotNull(bot);
	}

}
