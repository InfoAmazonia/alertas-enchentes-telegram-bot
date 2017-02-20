package br.edu.ufcg.analytics.infoamazonia;

import br.edu.ufcg.analytics.infoamazonia.services.Emoji;
import br.edu.ufcg.analytics.infoamazonia.services.LocalisationService;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
public class Commands {
    public static final String commandInitChar = "/";
    public static final String START = commandInitChar + "iniciar";
    public static final String HELP = commandInitChar + "ajuda";
    public static final String STATUSRIOACRE = commandInitChar + "rioacre";
    public static final String STATUSRIOMADEIRA = commandInitChar + "riomadeira";
    
    public static final String STOPCOMMAND = commandInitChar + "stop";

    public enum AlertaEnchentesCommands{
    	STATUS("status", Emoji.CHART_WITH_UPWARDS_TREND),
    	STATUS_RIO_ACRE("status_rio_acre", null)
    	;
    	
    	private final String command;
		private final Emoji emoji;

		private AlertaEnchentesCommands(String command, Emoji emoji) {
			this.command = command;
			this.emoji = emoji;
		}
		
		public String getCommand(String language) {
			return String.format(LocalisationService.getInstance().getString(command, language),
	                emoji);
		}
    }
    
    
    public static String getStatusCommand(String language) {
        return AlertaEnchentesCommands.STATUS.getCommand(language);
    }

    public static String getRioBrancoCommand(String language) {
        return AlertaEnchentesCommands.STATUS_RIO_ACRE.getCommand(language);
    }

    public static String getRioMadeiraCommand(String language) {
        return LocalisationService.getInstance().getString("status_rio_madeira", language);
    }
    
    public static String getCancelCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("cancel", language),
                Emoji.CROSS_MARK.toString());
    }


    public static String getAlertCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("alert", language),
                Emoji.ALARM_CLOCK.toString());
    }
    
    public static String getNewCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("new", language),
                Emoji.HEAVY_PLUS_SIGN.toString());
    }
    
    public static String getDeleteCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("delete", language),
                Emoji.HEAVY_MINUS_SIGN.toString());
    }

    public static String getListCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("showList", language),
                Emoji.CLIPBOARD.toString());
    }

    public static String getDemoCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("demo", language),
                Emoji.SPARKLE.toString());
    }

    public static String getBackCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("back", language),
                Emoji.BACK_WITH_LEFTWARDS_ARROW_ABOVE.toString());
    }

    public static String getStopCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("stop", language),
                Emoji.CROSS_MARK.toString());
    }

    public static String getNextCommand(String language) {
        return String.format(LocalisationService.getInstance().getString("next", language),
                Emoji.BLACK_RIGHTWARDS_ARROW.toString());
    }



}
