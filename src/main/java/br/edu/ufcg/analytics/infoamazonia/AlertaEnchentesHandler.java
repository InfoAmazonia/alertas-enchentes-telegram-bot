package br.edu.ufcg.analytics.infoamazonia;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
public class AlertaEnchentesHandler extends TelegramLongPollingBot {

	private static final String LOGTAG = "ALERTAHANDLERS";

	public String getBotUsername() {
		return BotConfig.USERNAMEMYPROJECT;
	}

	@Override
	public String getBotToken() {
		return BotConfig.TOKENMYPROJECT;
	}

	public void onUpdateReceived(Update update) {

		try {
			if (update.hasMessage()) {
				Message message = update.getMessage();
				if (message.hasText() || message.hasLocation()) {
					handleIncomingMessage(message);
				}
			}
		} catch (Exception e) {
			BotLogger.error(LOGTAG, e);
		}
	}

	private void handleIncomingMessage(Message message) {
		SendMessage sendMessageRequest = new SendMessage();
		String text = message.getText();
		String[] parts = text.split(" ", 2);

		// Commands
		if (parts[0].startsWith(Commands.STATUSRIOBRANCO)) {
			sendMessageRequest.setChatId(message.getChatId().toString());
			sendMessageRequest.setText("Status para Rio Branco");
		} else if (parts[0].startsWith(Commands.STATUSPORTOVELHO)) {
			sendMessageRequest.setChatId(message.getChatId().toString());
			sendMessageRequest.setText("Status para Porto Velho");
		} else {
			sendMessageRequest.setChatId(message.getChatId().toString());
			sendMessageRequest.setText(CustomMessages.help);
		}

		try {
			sendMessage(sendMessageRequest);
		} catch (TelegramApiException e) {

		}
	}
}
