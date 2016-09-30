package org.telegram;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.updateshandlers.*;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
public class Main {
    public static void main(String[] args) {
    	TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new AlertaEnchentesHandler());
        } catch (TelegramApiException e) {
            BotLogger.error("ERROU", e);
        }
    }
}
