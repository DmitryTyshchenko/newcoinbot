package newcoinbot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;

import ztysdmy.binance.BinanceApi;

public class Trading {

	private List<Bot> bots;
	
	public static final String BOTS_CONFIG_FILE = "bots.json";
	
	private final BinanceApi biananceApi;
	
	public Trading(BinanceApi biananceApi) {
		
		this.biananceApi = biananceApi;
		Path fileName = Path.of("bots.json");
		try {
			var json = Files.readString(fileName);
			this.bots = loadBots(json);
			//start all bots
			ExecutorService executor = Executors.newCachedThreadPool();
			for (Bot bot:bots) {
				bot.setBinanceApi(biananceApi);
				executor.execute(bot);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}		
		
	}

	protected static List<Bot> loadBots(String json) {
		var gson = new Gson();
		Bot[] botsArray = gson.fromJson(json, Bot[].class);
		return Arrays.asList(botsArray);
	}

	public static void main(String...strings) throws Exception {
		Path fileName = Path.of("bots.json");
		var json = Files.readString(fileName);
		var boats = Trading.loadBots(json);
	
	}
}
