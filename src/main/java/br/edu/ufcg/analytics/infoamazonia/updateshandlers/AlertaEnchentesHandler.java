package br.edu.ufcg.analytics.infoamazonia.updateshandlers;

import static br.edu.ufcg.analytics.infoamazonia.Commands.*;
import static br.edu.ufcg.analytics.infoamazonia.CustomMessages.*;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardHide;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.logging.BotLogger;

import br.edu.ufcg.analytics.infoamazonia.BotConfig;
import br.edu.ufcg.analytics.infoamazonia.Commands;
import br.edu.ufcg.analytics.infoamazonia.CustomMessages;
import br.edu.ufcg.analytics.infoamazonia.River;
import br.edu.ufcg.analytics.infoamazonia.State;
import br.edu.ufcg.analytics.infoamazonia.database.DatabaseManager;
import br.edu.ufcg.analytics.infoamazonia.services.Emoji;
import br.edu.ufcg.analytics.infoamazonia.services.LocalisationService;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 * @author Ricardo Araujo Santos - ricoaraujosantos@gmail.com
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

	private void handleIncomingMessage(Message message) throws TelegramApiException {

		State menu = DatabaseManager.getInstance().getState(message.getFrom().getId(), message.getChatId());
		final String language = "pt-BR";// DatabaseManager.getInstance().getUserWeatherOptions(message.getFrom().getId())[0];
		if (!message.isUserMessage() && message.hasText()) {
			if (isCommandForOther(message.getText())) {
				return;
			} else if (message.getText().startsWith(Commands.STOPCOMMAND)) {
				ReplyKeyboardHide replyKeyboard = new ReplyKeyboardHide();
				replyKeyboard.setSelective(true);
				sendMessage(buildSendMessage(message, replyKeyboard, Emoji.WAVING_HAND_SIGN.toString()));
				return;
			}
		}

		SendMessage sendMessageRequest;
		switch (menu) {
		case MAIN_MENU:
			sendMessageRequest = processMainMenuOption(message, language);
			break;
		case STATUS:
			sendMessageRequest = processStatusMenuOption(message, language, menu);
			break;
		case ALERT:
			sendMessageRequest = processAlertMenuOption(message, language);
			break;
		case ALERT_NEW:
			sendMessageRequest = processNewAlertMenuOption(message, language);
			break;
		case ALERT_DELETE:
			sendMessageRequest = processDeleteAlertMenuOption(message, language);
			break;
		default:
			sendMessageRequest = processDefaultStartOption(message, language);
			break;
		}
		System.out.println("AlertaEnchentesHandler.handleIncomingMessage()");
		System.out.println(sendMessageRequest);
		sendMessage(sendMessageRequest);
	}

	private static boolean isCommandForOther(String text) {
		boolean isSimpleCommand = text.equals("/start") || text.equals("/help") || text.equals("/stop");
		boolean isCommandForMe = text.equals("/start@weatherbot") || text.equals("/help@weatherbot")
				|| text.equals("/stop@weatherbot");
		return text.startsWith("/") && !isSimpleCommand && !isCommandForMe;
	}

	private static SendMessage processDefaultStartOption(Message message, String language) {
		ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard(language);
		DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);
		return buildSendMessage(message, replyKeyboardMarkup, CustomMessages.getHelpMessage(language));
	}
	
	private static SendMessage buildSendMessage(Message message, ReplyKeyboard replyKeyboard, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setReplyToMessageId(message.getMessageId());
		if (replyKeyboard != null) {
			sendMessage.setReplyMarkup(replyKeyboard);
		}
		sendMessage.setText(text);
		return sendMessage;
	}


	private static SendMessage processMainMenuOption(Message message, String language) {
		if (message.hasText()) {
			String text = message.getText();
			
			if (getStatusCommand(language).equals(text)) {
				DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.STATUS);
				return buildSendMessage(message, getRiversKeyboard(language), LocalisationService.getInstance().getString("onCurrentCommandFromHistory", language));
			} 
			
			if (getAlertCommand(language).equals(text)) {
				DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.ALERT);
				return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
			}
		}

		return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
	}

	private SendMessage processStatusMenuOption(Message message, String language, State state) {
		DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);

		if (message.hasText()) {
			String text = message.getText();
			if (!text.startsWith(getCancelCommand(language))) {
				River river = River.fromName(text);
				if(river == null){
					return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("invalidRiverBackToMainMenu", language));
				}
				
				return buildSendMessage(message, getMainMenuKeyboard(language), DatabaseManager.getInstance().getLastStatus(river.getCode(),
						message.getFrom().getId(), language));
			}
		}
		
		return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("backToMainMenu", language));
	}

	private static SendMessage processAlertMenuOption(Message message, String language) {
		if (message.hasText()) {
			if (message.getText().equals(getNewCommand(language))) {
				String text = LocalisationService.getInstance().getString("chooseNewAlertCity", language);
				DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.ALERT_NEW);
				return buildSendMessage(message, getRiversKeyboard(language), text);
			} else if (message.getText().equals(getDeleteCommand(language))) {

				String text = null;
		        ReplyKeyboard replyKeyboard = getAlertsListKeyboard(message.getFrom().getId(), language);
		        if (replyKeyboard != null) {
		            text = LocalisationService.getInstance().getString("chooseDeleteAlertCity", language);
		            DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.ALERT_DELETE);
		        } else {
		            replyKeyboard = getAlertsKeyboard(language);
		            text = LocalisationService.getInstance().getString("noAlertList", language);
		        }

				return buildSendMessage(message, replyKeyboard, text);
			} else if (message.getText().equals(getListCommand(language))) {
				return buildSendMessage(message, getAlertsKeyboard(language), getAlertListMessage(message.getFrom().getId(), language));
			} else if (message.getText().equals(getBackCommand(language))) {
				DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);
				return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("backToMainMenu", language));
			} else {
				return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
			}
		}
		DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);
		return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("backToMainMenu", language));
	}

	
    private static SendMessage processNewAlertMenuOption(Message message, String language) {
        if (message.hasText()) {
            if (!message.getText().equals(getCancelCommand(language))) {
                int userId = message.getFrom().getId();
                River river = River.fromName(message.getText());
                
                if(river == null){
                	return buildSendMessage(message, getRiversKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
                }
                
                DatabaseManager.getInstance().setState(userId, message.getChatId(), State.ALERT);
                DatabaseManager.getInstance().createNewAlert(userId, river);
                return buildSendMessage(message, getAlertsKeyboard(language), getChooseNewAlertSetMessage(message.getText(), language));
            } 
        }
        DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.ALERT);
        return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
    }
    
    private static SendMessage processDeleteAlertMenuOption(Message message, String language) {
        if (message.hasText()) {
            if (message.getText().equals(getCancelCommand(language))) {
                DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.ALERT);
                return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
            }
            
            River river = River.fromName(message.getText());
            if(river != null){
            	if (DatabaseManager.getInstance().getAlertNamesByUser(message.getFrom().getId()).contains(message.getText())) {
            		DatabaseManager.getInstance().deleteAlert(message.getFrom().getId(), river);
            		DatabaseManager.getInstance().setState(message.getFrom().getId(), message.getChatId(), State.ALERT);
            		return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertDeleted", language));
            	}
            }
        }

        return buildSendMessage(message, getAlertsListKeyboard(message.getFrom().getId(), language), LocalisationService.getInstance().getString("chooseOption", language));
    }



    private static ReplyKeyboardMarkup buildKeyboard(List<KeyboardRow> keyboard) {
    	ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    	replyKeyboardMarkup.setSelective(true);
    	replyKeyboardMarkup.setResizeKeyboard(true);
    	replyKeyboardMarkup.setOneTimeKeyboad(true);
    	replyKeyboardMarkup.setKeyboard(keyboard);
    	return replyKeyboardMarkup;
    }
	
    private static KeyboardRow buildKeyboardRow(String... commands) {
    	KeyboardRow row = new KeyboardRow();
    	for (String command : commands) {
    		row.add(command);
    	}
    	return row;
    }
        
	private static ReplyKeyboardMarkup getMainMenuKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getStatusCommand(language), getAlertCommand(language)));
		return buildKeyboard(keyboard);
	}
	
	private static ReplyKeyboardMarkup getRiversKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getRioBrancoCommand(language)));
		keyboard.add(buildKeyboardRow(getRioMadeiraCommand(language)));
		keyboard.add(buildKeyboardRow(getCancelCommand(language)));

		return buildKeyboard(keyboard);
	}

	private static ReplyKeyboardMarkup getAlertsKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getNewCommand(language), getDeleteCommand(language)));
		keyboard.add(buildKeyboardRow(getListCommand(language), getBackCommand(language)));
		
		return buildKeyboard(keyboard);
	}

	private static ReplyKeyboardMarkup getAlertsListKeyboard(Integer userId, String language) {

		List<String> alertNames = DatabaseManager.getInstance().getAlertNamesByUser(userId);

		if(alertNames.isEmpty()){
			return null;
		}

		List<KeyboardRow> keyboard = new ArrayList<>();
		for (String alertName: alertNames) {
			keyboard.add(buildKeyboardRow(alertName));
		}
		keyboard.add(buildKeyboardRow(getCancelCommand(language)));

		return buildKeyboard(keyboard);
    }

	
}
