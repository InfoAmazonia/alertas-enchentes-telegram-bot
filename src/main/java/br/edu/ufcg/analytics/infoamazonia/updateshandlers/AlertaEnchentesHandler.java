package br.edu.ufcg.analytics.infoamazonia.updateshandlers;

import static br.edu.ufcg.analytics.infoamazonia.Commands.getAlertCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getBackCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getCancelCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getDeleteCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getDemoCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getListCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getNewCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getNextCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getRioBrancoCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getRioMadeiraCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getStatusCommand;
import static br.edu.ufcg.analytics.infoamazonia.Commands.getStopCommand;
import static br.edu.ufcg.analytics.infoamazonia.CustomMessages.getAlertListMessage;
import static br.edu.ufcg.analytics.infoamazonia.CustomMessages.getChooseNewAlertSetMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
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
import br.edu.ufcg.analytics.infoamazonia.database.AlertDemoRepository;
import br.edu.ufcg.analytics.infoamazonia.database.AlertRepository;
import br.edu.ufcg.analytics.infoamazonia.database.ConversationRepository;
import br.edu.ufcg.analytics.infoamazonia.model.Alert;
import br.edu.ufcg.analytics.infoamazonia.model.AlertDemo;
import br.edu.ufcg.analytics.infoamazonia.model.AlertMessage;
import br.edu.ufcg.analytics.infoamazonia.model.Conversation;
import br.edu.ufcg.analytics.infoamazonia.services.Emoji;
import br.edu.ufcg.analytics.infoamazonia.services.LocalisationService;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 * @author Ricardo Araujo Santos - ricoaraujosantos@gmail.com
 */
public class AlertaEnchentesHandler extends TelegramLongPollingBot {

	private static final String LOGTAG = "ALERTAHANDLERS";

	private AlertRepository alertRepo;
	private AlertDemoRepository alertDemoRepo;
	private ConversationRepository conversationRepo;
	private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1); ///< Thread to execute operations

	private DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());
	private DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
	
	Map<Long,AlertMessage> alerts;

	private String defaultLanguage;
	

	public AlertaEnchentesHandler(AlertRepository alertRepo, ConversationRepository conversationRepo, AlertDemoRepository alertDemoRepo) {
		super();
		this.alertRepo = alertRepo;
		this.conversationRepo = conversationRepo;
		this.alertDemoRepo = alertDemoRepo;
		this.alerts = new HashMap<>();
        startAlertTimers();
	}

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

		State menu = getConversationState(message.getFrom().getId(), message.getChatId());
		defaultLanguage = "pt-BR";
		// DatabaseManager.getInstance().getUserWeatherOptions(message.getFrom().getId())[0];
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
			sendMessageRequest = processMainMenuOption(message, defaultLanguage);
			break;
		case STATUS:
			sendMessageRequest = processStatusMenuOption(message, defaultLanguage, menu);
			break;
		case ALERT:
			sendMessageRequest = processAlertMenuOption(message, defaultLanguage);
			break;
		case ALERT_NEW:
			sendMessageRequest = processNewAlertMenuOption(message, defaultLanguage);
			break;
		case ALERT_DEMO:
			sendMessageRequest = processNewAlertDemoMenuOption(message, defaultLanguage);
			break;
		case ALERT_DEMO_STARTED:
			sendMessageRequest = processAlertDemoMenuOption(message, defaultLanguage);
			break;
		case ALERT_DELETE:
			sendMessageRequest = processDeleteAlertMenuOption(message, defaultLanguage);
			break;
		default:
			sendMessageRequest = processDefaultStartOption(message, defaultLanguage);
			break;
		}
		System.out.println("AlertaEnchentesHandler.handleIncomingMessage()");
		System.out.println(sendMessageRequest);
		sendMessage(sendMessageRequest);
	}

	private boolean isCommandForOther(String text) {
		boolean isSimpleCommand = text.equals("/start") || text.equals("/help") || text.equals("/stop");
		boolean isCommandForMe = text.equals("/start@weatherbot") || text.equals("/help@weatherbot")
				|| text.equals("/stop@weatherbot");
		return text.startsWith("/") && !isSimpleCommand && !isCommandForMe;
	}

	private SendMessage processDefaultStartOption(Message message, String language) {
		ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard(language);
		setConversationState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);
		return buildSendMessage(message, replyKeyboardMarkup, CustomMessages.getHelpMessage(language));
	}
	
	private SendMessage buildSendMessage(Message message, ReplyKeyboard replyKeyboard, String text) {
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

	private SendMessage buildSendMessage(String chatID, ReplyKeyboard replyKeyboard, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(chatID);
		if (replyKeyboard != null) {
			sendMessage.setReplyMarkup(replyKeyboard);
		}
		sendMessage.setText(text);
		return sendMessage;
	}


	private SendMessage processMainMenuOption(Message message, String language) {
		if (message.hasText()) {
			String text = message.getText();
			
			if (getStatusCommand(language).equals(text)) {
				setConversationState(message.getFrom().getId(), message.getChatId(), State.STATUS);
				return buildSendMessage(message, getRiversKeyboard(language), LocalisationService.getInstance().getString("onCurrentCommandFromHistory", language));
			} 
			
			if (getAlertCommand(language).equals(text)) {
				setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT);
				return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
			}
		}

		return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
	}

	private SendMessage processStatusMenuOption(Message message, String language, State state) {
		setConversationState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);

		if (message.hasText()) {
			String text = message.getText();
			if (!text.startsWith(getCancelCommand(language))) {
				River river = River.fromName(text);
				if(river == null){
					return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("invalidRiverBackToMainMenu", language));
				}
				
				return buildSendMessage(message, getMainMenuKeyboard(language), getRiverStatusMessage(river.getCode(),
						language));
			}
		}
		
		return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("backToMainMenu", language));
	}

	private SendMessage processAlertMenuOption(Message message, String language) {
		if (message.hasText()) {
			if (message.getText().equals(getNewCommand(language))) {
				String text = LocalisationService.getInstance().getString("chooseNewAlertCity", language);
				setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT_NEW);
				return buildSendMessage(message, getRiversKeyboard(language), text);
			} else if (message.getText().equals(getDeleteCommand(language))) {

				String text = null;
		        ReplyKeyboard replyKeyboard = getAlertsListKeyboard(message.getChatId(), language);
		        if (replyKeyboard != null) {
		            text = LocalisationService.getInstance().getString("chooseDeleteAlertCity", language);
		            setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT_DELETE);
		        } else {
		            replyKeyboard = getAlertsKeyboard(language);
		            text = LocalisationService.getInstance().getString("noAlertList", language);
		        }

				return buildSendMessage(message, replyKeyboard, text);
			} else if (message.getText().equals(getListCommand(language))) {
				List<String> alertNames = getAlertNamesByUser(message.getChatId());
				return buildSendMessage(message, getAlertsKeyboard(language), getAlertListMessage(alertNames, language));
			} else if (message.getText().equals(getDemoCommand(language))) {
				String text = LocalisationService.getInstance().getString("chooseNewAlertCityForDemo", language);
				setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT_DEMO);
				return buildSendMessage(message, getRiversKeyboard(language), text);
			} else if (message.getText().equals(getBackCommand(language))) {
				setConversationState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);
				return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("backToMainMenu", language));
			} else {
				return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
			}
		}
		setConversationState(message.getFrom().getId(), message.getChatId(), State.MAIN_MENU);
		return buildSendMessage(message, getMainMenuKeyboard(language), LocalisationService.getInstance().getString("backToMainMenu", language));
	}

	
    private SendMessage processNewAlertMenuOption(Message message, String language) {
        if (message.hasText()) {
            if (!message.getText().equals(getCancelCommand(language))) {
                int userId = message.getFrom().getId();
                River river = River.fromName(message.getText());
                
                if(river == null){
                	return buildSendMessage(message, getRiversKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
                }
                
                setConversationState(userId, message.getChatId(), State.ALERT);
                boolean alertCreated = createNewAlert(message.getChatId(), river);
               	return buildSendMessage(message, getAlertsKeyboard(language), getChooseNewAlertSetMessage(message.getText(), language, alertCreated));
            } 
        }
        setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT);
        return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
    }
    
    private SendMessage processNewAlertDemoMenuOption(Message message, String language) {
        if (message.hasText()) {
            if (!message.getText().equals(getCancelCommand(language))) {
                int userId = message.getFrom().getId();
                River river = River.fromName(message.getText());
                
                if(river == null){
                	return buildSendMessage(message, getRiversKeyboard(language), LocalisationService.getInstance().getString("chooseOption", language));
                }
                
                setConversationState(userId, message.getChatId(), State.ALERT_DEMO_STARTED);
            	updateAlertDemo(userId, river, 1451606400L);
                return buildSendMessage(message, getDemoKeyboard(language), getStartDemoMessage(river.getCode(), language));
            } 
        }
        setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT);
        return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
    }
    
    private SendMessage processAlertDemoMenuOption(Message message, String language) {
        if (message.hasText()) {
            if (message.getText().equals(getNextCommand(language))) {
            	System.out.println("AlertaEnchentesHandler.processAlertDemoMenuOption() " + "next");
            	int userId = message.getFrom().getId();
            	String alert = getNextAlertDemo(userId);
            	return buildSendMessage(message, getDemoKeyboard(language), alert);
            }
        }
    	return processDefaultStartOption(message, language);
    }
    
	private SendMessage processDeleteAlertMenuOption(Message message, String language) {
        if (message.hasText()) {
            if (message.getText().equals(getCancelCommand(language))) {
                setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT);
                return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertsMenuMessage", language));
            }
            
            River river = River.fromName(message.getText());
            if(river != null && deleteAlert(message.getChatId(), river)){
            	setConversationState(message.getFrom().getId(), message.getChatId(), State.ALERT);
            	return buildSendMessage(message, getAlertsKeyboard(language), LocalisationService.getInstance().getString("alertDeleted", language));
            }
        }

        return buildSendMessage(message, getAlertsListKeyboard(message.getChatId(), language), LocalisationService.getInstance().getString("chooseOption", language));
    }



    private ReplyKeyboardMarkup buildKeyboard(List<KeyboardRow> keyboard) {
    	ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    	replyKeyboardMarkup.setSelective(true);
    	replyKeyboardMarkup.setResizeKeyboard(true);
    	replyKeyboardMarkup.setOneTimeKeyboad(true);
    	replyKeyboardMarkup.setKeyboard(keyboard);
    	return replyKeyboardMarkup;
    }
	
    private KeyboardRow buildKeyboardRow(String... commands) {
    	KeyboardRow row = new KeyboardRow();
    	for (String command : commands) {
    		row.add(command);
    	}
    	return row;
    }
        
	private ReplyKeyboardMarkup getMainMenuKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getStatusCommand(language), getAlertCommand(language)));
		return buildKeyboard(keyboard);
	}
	
	private ReplyKeyboardMarkup getRiversKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getRioBrancoCommand(language)));
		keyboard.add(buildKeyboardRow(getRioMadeiraCommand(language)));
		keyboard.add(buildKeyboardRow(getCancelCommand(language)));

		return buildKeyboard(keyboard);
	}

	private ReplyKeyboardMarkup getAlertsKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getNewCommand(language), getDeleteCommand(language)));
		keyboard.add(buildKeyboardRow(getListCommand(language), getBackCommand(language)));
		keyboard.add(buildKeyboardRow(getDemoCommand(language)));
		
		return buildKeyboard(keyboard);
	}

	private ReplyKeyboardMarkup getDemoKeyboard(String language) {

		List<KeyboardRow> keyboard = new ArrayList<>();
		keyboard.add(buildKeyboardRow(getStopCommand(language), getNextCommand(language)));
		
		return buildKeyboard(keyboard);
	}

	private ReplyKeyboardMarkup getAlertsListKeyboard(Long chatId, String language) {

		List<String> alertNames = getAlertNamesByUser(chatId);

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

	
	private void setConversationState(Integer userId, Long chatId, State state) {
		Conversation conversation = conversationRepo.findFirstByUserIdAndChatId(userId, chatId);
		if(conversation == null){
			conversation = new Conversation(userId, chatId, null);
		}
		conversation.state = state;
		conversationRepo.save(conversation);
	}

	private State getConversationState(Integer userId, Long chatId) {
		Conversation conversation = conversationRepo.findFirstByUserIdAndChatId(userId, chatId);
		conversationRepo.save(new Conversation(userId, chatId, State.START));
		return conversation == null? State.START: conversation.state;
	}

    private boolean createNewAlert(Long chatID, River river) {
    	if(alertRepo.findFirstByChatIdAndRiver(chatID, river) != null){
    		return false;
    	}
		alertRepo.save(new Alert(chatID, river));
		return true;
	}

    private void updateAlertDemo(int userId, River river, Long timestamp) {
    	System.out.println("AlertaEnchentesHandler.updateAlertDemo()");
    	AlertDemo alert = alertDemoRepo.findOne(userId);
    	System.out.println(alert);
    	if(alert == null){
    		alert = new AlertDemo(userId, river, timestamp);
    	}
    	System.out.println(alert);
    	alert.river = river;
    	alert.timestamp = timestamp;
		alertDemoRepo.save(alert);
	}

    private String getNextAlertDemo(int userId) {
    	AlertDemo alert = alertDemoRepo.findOne(userId);
    	
		String uriString = "https://enchentes.infoamazonia.org:8080/station/" + alert.river.getCode() + "/alert?timestamp=" + alert.timestamp;
		
		try {
			URI uri = new URI(uriString);
			JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
			JSONObject root = new JSONObject(tokener);
			
			alert.timestamp = root.getLong("timestamp");
			alertDemoRepo.save(alert);
			
			LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(alert.timestamp), ZoneId.systemDefault());
			
			return "Em " + dateTime.format(dayFormatter) + " às " + dateTime.format(hourFormatter) + ", os cidadão receberiam o seguinte alerta:\n\n\n" + root.getString("message");
		} catch (URISyntaxException | JSONException | IOException e) {
			BotLogger.error(LOGTAG, e);
		}
		return "Opção indisponível no momento. Tente novamente em alguns minutos!";
	}

	public boolean deleteAlert(Long chatId, River river) {
		Alert alert = alertRepo.findFirstByChatIdAndRiver(chatId, river);
		if(alert == null){
			return false;
		}
		alertRepo.delete(alert);
		return true;
	}
	
	public List<String> getAlertNamesByUser(Long chatId) {
		return alertRepo.findAllByChatId(chatId).stream().map(alert -> alert.river.getName()).collect(Collectors.toList());
	}

	private String getRiverStatusMessage(Long riverId, String language) {
		String uriString = "https://enchentes.infoamazonia.org:8080/station/" + riverId + "/now";
		
		try {
			URI uri = new URI(uriString);
			JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
			JSONObject root = new JSONObject(tokener);
			return root.getString("message");
		} catch (URISyntaxException | JSONException | IOException e) {
			BotLogger.error(LOGTAG, e);
		}
		return "Opção indisponível no momento. Tente novamente em alguns minutos!";
	}

	private String getStartDemoMessage(Long riverId, String language) {
		
		return "A seguir, mostraremos os alertas gerados para o " + River.fromCode(riverId) + " no ano de 2016. Escolha \"Próximo\" para ver mais um alerta ou \"Encerrar\" para terminar a demonstração.";
	}

	private void startAlertTimers() {
		for (River river : River.values()) {
			executorService.scheduleAtFixedRate(() -> updateStatus(river.getCode()), 10, 60, TimeUnit.SECONDS);
		}
	}

	private void updateStatus(Long riverId) {
		String message = collectRiverStatus(riverId);
		if (message != null) {
			River river = River.fromCode(riverId);
			List<Alert> subcriptions = alertRepo.findAllByRiver(river);
			for (Alert alert : subcriptions) {
				try {
					sendMessage(buildSendMessage(alert.chatId.toString(), getMainMenuKeyboard(defaultLanguage), message));
				} catch (TelegramApiException e) {
					BotLogger.error(LOGTAG, e);
				}
			}
		}else{
			BotLogger.debug(LOGTAG, "Sem alertas para rio: " + River.fromCode(riverId));
		}
	}

	private String collectRiverStatus(Long riverId) {

		String uriString = "https://enchentes.infoamazonia.org:8080/station/" + riverId + "/alert";

		try {
			URI uri = new URI(uriString);
			JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
			JSONObject root = new JSONObject(tokener);

			AlertMessage alertMessage = new AlertMessage(riverId, root.getLong("timestamp"), root.getString("message"));
			if (!alerts.containsKey(riverId) || alerts.get(riverId).timestamp < alertMessage.timestamp) {
				alerts.put(riverId, alertMessage);
				return alertMessage.message;
			}
		} catch (URISyntaxException | JSONException | IOException e) {
			BotLogger.error(LOGTAG, e);
		}
		return null;
	}

}
