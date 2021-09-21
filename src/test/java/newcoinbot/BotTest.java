package newcoinbot;

import org.junit.jupiter.api.Test;

public class BotTest {

	@Test
	public void shouldConvertDateToLong() throws Exception {
		var timeInString = "2021-09-12 10:00";
		Bot.getStartTime(timeInString);
	}
	
}
