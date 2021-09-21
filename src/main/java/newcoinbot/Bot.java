package newcoinbot;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ztysdmy.binance.BinanceApi;

public class Bot implements Runnable {

	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	
	private final String pair;
	private final String startTime;
	private final BigDecimal amount;
	private final List<Goal> goals;
	
	private BinanceApi biananceApi;

	public Bot(String pair, String startTime, BigDecimal amount, List<Goal> goals) {
		this.pair = pair;
		this.startTime = startTime;
		this.amount = amount;
		this.goals = goals;
	}

	public void setBinanceApi(BinanceApi biananceApi) {
		this.biananceApi = biananceApi;
	}
	
	static class Goal {

		protected BigDecimal percent;
		protected String shift; //in seconds
		
	}

	@Override
	public void run() {
		LOGGER.info("HELLO FROM BOT");
		waitStartTime(this.startTime);
		sleep(10);//?
		//Create Buy MarkerOrderHere
		LOGGER.info("Creat Buy Market Order for "+this.pair);
		
		while (true) {
			//Create Market Order
			var goalsIrerator = goals.iterator();
			
			while(goalsIrerator.hasNext()) {
				
			   var goal = goalsIrerator.next();
			   long currentTime = System.currentTimeMillis();
			   long executionTime = getExecutionTime(this.startTime, goal.shift);
			   if (currentTime>executionTime) {
				   //Create Sell Market Order
				   LOGGER.info("Create Sell Market Order "+this.pair);
				   goalsIrerator.remove();
			   }
			}
			
			if (goals.isEmpty()) {
				LOGGER.info("NO GOALS LEFT");
				break;
			}
		}
	}
	
	@SuppressWarnings("static-access")
	private boolean sleep(long time) {
		try {
			Thread.currentThread().sleep(time);
			return false;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return true;
		}
	}
	
	protected static void waitStartTime(String timeInString) {
		
		long currentTime = System.currentTimeMillis();
		long startTime = getStartTime(timeInString);
		while(true) {
			if (currentTime>startTime) {
				break;
			}
			currentTime = System.currentTimeMillis();
		}
	}
	
	protected static long getStartTime(String timeInString) {
		var sdf = new SimpleDateFormat("yyyy-M-dd hh:mm");
		return invoke(() -> sdf.parse(timeInString).getTime());
	}
	
	protected static long getExecutionTime(String timeInString, String shift) {
		var sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
		var executionTimeInString = timeInString+":"+shift;
		return invoke(() -> sdf.parse(executionTimeInString).getTime());
	}
	
	@FunctionalInterface
	private static interface Action<T> {

		T doAction() throws Exception;
	}

	private static <T> T invoke(Action<T> a) {

		try {
			return a.doAction();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
