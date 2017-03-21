package br.edu.ufcg.analytics.infoamazonia;

import java.util.List;

import br.edu.ufcg.analytics.infoamazonia.services.Emoji;
import br.edu.ufcg.analytics.infoamazonia.services.LocalisationService;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
public class CustomMessages {
    public static final String help = "Alerta de enchentes\n\nVeja as previsões de volume para os rios da região amazônica. Para qual rio?\n\n" +
            Commands.STATUSRIOACRE + " para previsões do Rio Acre.\n" +
            Commands.STATUSRIOMADEIRA + " para previsões de Rio Madeira.\n" +
            Commands.HELP + " para obter ajuda.";
    
    
    public static String getHelpMessage(String language) {
        String baseString = LocalisationService.getInstance().getString("helpWeatherMessage", language);
        return String.format(baseString, Emoji.CHART_WITH_UPWARDS_TREND.toString(),
                Emoji.ALARM_CLOCK.toString());
    }
    
    public static String getAlertListMessage(List<String> alertNames, String language) {
        String alertListMessage;

        if (alertNames.size() > 0) {
            String baseAlertListString = LocalisationService.getInstance().getString("initialAlertList", language);
            String partialAlertListString = LocalisationService.getInstance().getString("partialAlertList", language);
            String fullListOfAlerts = "";
            for (String alertCity : alertNames) {
                fullListOfAlerts += String.format(partialAlertListString, Emoji.ALARM_CLOCK.toString(), alertCity);
            }
            alertListMessage = String.format(baseAlertListString, alertNames.size(), fullListOfAlerts);
        } else {
            alertListMessage = LocalisationService.getInstance().getString("noAlertList", language);
        }

        return alertListMessage;
    }

    public static String getChooseNewAlertSetMessage(String city, String language, boolean alertCreated) {
        String baseString = LocalisationService.getInstance().getString(alertCreated?"newAlertSaved":"newAlertNotSaved", language);
        return String.format(baseString, Emoji.THUMBS_UP_SIGN.toString(), city);
    }

}
