package newcoinbot;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Test;

public class BotTest {

	@Test
	public void shouldConvertDateToLong() throws Exception {
		var timeInString = "2021-09-12 10:00";
		Bot.getStartTime(timeInString);
	}
	
	@Test
	public void test32() throws Exception {
		//0.0008460
		
		var value = BigDecimal.valueOf(0.0008460d);
		System.out.println(value.setScale(4, RoundingMode.DOWN));
	}
	
}
