package org.telegram.updateshandlers;

import org.telegram.BotConfig;
import org.telegram.Commands;
import org.telegram.CustomMessages;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class AlertaEnchentesHandler extends	TelegramLongPollingBot {

	@Override
	public String getBotUsername() {
		return BotConfig.USERNAMEMYPROJECT;
	}

	@Override
	public String getBotToken() {
		return BotConfig.TOKENMYPROJECT;
	}

	@Override
	public void onUpdateReceived(Update update) {
		
        if(update.hasMessage()){
	        Message message = update.getMessage();
	
	        if (message != null && message.hasText()) {
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
		
	}

}
