package newcoinbot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ztysdmy.binance.BinanceApi;
import ztysdmy.binance.BinanceException;
import ztysdmy.binance.RequestLimitException;
import ztysdmy.binance.http.HttpBinanceApi;
import ztysdmy.binance.model.Order;
import ztysdmy.binance.model.OrderSide;
import ztysdmy.binance.model.OrderType;

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
		protected String shift; // in seconds

	}

	private BigDecimal buy() {
		var params = new HashMap<String, String>();
		params.put("quoteOrderQty", this.amount.toString());
		Order order;
		try {
			order = biananceApi.newOrder(this.pair, OrderSide.BUY, OrderType.MARKET, params);
			var amount = order.getExecutedQty();
			LOGGER.info(this.pair + ": bought " + amount.toString());
			return amount;
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

	private void sell(BigDecimal totalSum, BigDecimal percent) {
		var params = new HashMap<String, String>();
		var toSell = calcPercent(totalSum, percent);
		params.put("quantity", toSell.toString());
		try {
			LOGGER.info(this.pair + ": selling " + toSell.toString());
			var order = biananceApi.newOrder(this.pair, OrderSide.SELL, OrderType.MARKET, params);
			LOGGER.info(this.pair + ": sold " + toSell.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected BigDecimal calcPercent(BigDecimal totalSum, BigDecimal numberOfPercent) {
		var onePercent = totalSum.divide(BigDecimal.valueOf(100.d), 7, RoundingMode.HALF_DOWN);
		var result = numberOfPercent.multiply(onePercent);
		result = result.setScale(4, RoundingMode.DOWN);
		return result;
	}

	@Override
	public void run() {
		LOGGER.info("BOT " + this.pair + " has been initialized ");
		waitStartTime(this.startTime);
		sleep(10);// ?
		// Create Buy MarkerOrderHere
		BigDecimal amount = buy();
		while (true) {

			var goalsIrerator = goals.iterator();

			while (goalsIrerator.hasNext()) {

				var goal = goalsIrerator.next();
				long currentTime = System.currentTimeMillis();
				long executionTime = getExecutionTime(this.startTime, goal.shift);
				if (currentTime > executionTime) {
					sell(amount, goal.percent);
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
		while (true) {
			if (currentTime > startTime) {
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
		var executionTimeInString = timeInString + ":" + shift;
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
