package br.edu.ufcg.analytics.infoamazonia;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import br.edu.ufcg.analytics.infoamazonia.database.AlertDemoRepository;
import br.edu.ufcg.analytics.infoamazonia.database.AlertRepository;
import br.edu.ufcg.analytics.infoamazonia.database.ConversationRepository;
import br.edu.ufcg.analytics.infoamazonia.updateshandlers.AlertaEnchentesHandler;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
@SpringBootApplication
public class Main {
	private static final String LOGTAG = "MAIN";
	
	public static void main(String[] args) {
		SpringApplication.run(Main.class);
	}

	@Bean
	public CommandLineRunner demo(AlertRepository alertRepo, ConversationRepository conversationRepo, AlertDemoRepository alertDemoRepo) {
		return (args) -> {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
			try {
				telegramBotsApi.registerBot(new AlertaEnchentesHandler(alertRepo, conversationRepo, alertDemoRepo));
			} catch (TelegramApiException e) {
				BotLogger.severe(LOGTAG, e);
			}
		};
	}
}
