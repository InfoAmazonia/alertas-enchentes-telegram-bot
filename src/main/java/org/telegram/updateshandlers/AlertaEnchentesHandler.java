package org.telegram.updateshandlers;

import org.telegram.BotConfig;
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
	public void onUpdateReceived(Update update) {
		
		 //check if the update has a message
        if(update.hasMessage()){
	        Message message = update.getMessage();
	
	        //check if the message has text. it could also  contain for example a location ( message.hasLocation() )
	        if(message.hasText()){

                //create a object that contains the information to send back the message
                SendMessage sendMessageRequest = new SendMessage();
                sendMessageRequest.setChatId(message.getChatId().toString()); //who should get the message? the sender from which we got the message...
                sendMessageRequest.setText(message.getText()+ " Estamos funcionando!");
                try {
                        sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                } catch (TelegramApiException e) {
                        //do some error handling
                }
	        }
        }
		
	}

	@Override
	public String getBotToken() {
		return BotConfig.TOKENMYPROJECT;
	}

}
