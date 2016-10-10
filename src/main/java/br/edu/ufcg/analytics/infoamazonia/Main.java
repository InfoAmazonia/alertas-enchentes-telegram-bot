package br.edu.ufcg.analytics.infoamazonia;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
public class Main {
	private static final String LOGTAG = "MAIN";

	public static void main(String[] args) {
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new AlertaEnchentesHandler());
		} catch (TelegramApiException e) {
			BotLogger.severe(LOGTAG, e);
		}
	}
}
