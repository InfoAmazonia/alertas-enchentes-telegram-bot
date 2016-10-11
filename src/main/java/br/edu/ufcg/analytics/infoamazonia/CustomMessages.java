package br.edu.ufcg.analytics.infoamazonia;

import java.util.List;

import br.edu.ufcg.analytics.infoamazonia.database.DatabaseManager;
import br.edu.ufcg.analytics.infoamazonia.services.Emoji;
import br.edu.ufcg.analytics.infoamazonia.services.LocalisationService;

/**
 * @author Jefferson Neves <jefferson.rpn@gmail.com>
 */
public class CustomMessages {
    public static final String help = "Alerta de enchentes\n\nVeja as previsões de volume para os rios da região amazônica. Para qual rio?\n\n" +
            Commands.STATUSRIOBRANCO + " para previsões do Rio Branco.\n" +
            Commands.STATUSPORTOVELHO + " para previsões de Porto Velho.\n" +
            Commands.HELP + " para obter ajuda.";
    
    
    public static String getHelpMessage(String language) {
        String baseString = LocalisationService.getInstance().getString("helpWeatherMessage", language);
        return String.format(baseString, Emoji.CHART_WITH_UPWARDS_TREND.toString(),
                Emoji.ALARM_CLOCK.toString());
    }
    
    public static String getAlertListMessage(int userId, String language) {
        String alertListMessage;

        List<String> alertCities = DatabaseManager.getInstance().getAlertNamesByUser(userId);
        if (alertCities.size() > 0) {
            String baseAlertListString = LocalisationService.getInstance().getString("initialAlertList", language);
            String partialAlertListString = LocalisationService.getInstance().getString("partialAlertList", language);
            String fullListOfAlerts = "";
            for (String alertCity : alertCities) {
                fullListOfAlerts += String.format(partialAlertListString, Emoji.ALARM_CLOCK.toString(), alertCity);
            }
            alertListMessage = String.format(baseAlertListString, alertCities.size(), fullListOfAlerts);
        } else {
            alertListMessage = LocalisationService.getInstance().getString("noAlertList", language);
        }

        return alertListMessage;
    }

    public static String getChooseNewAlertSetMessage(String city, String language) {
        String baseString = LocalisationService.getInstance().getString("newAlertSaved", language);
        return String.format(baseString, Emoji.THUMBS_UP_SIGN.toString(), city);
    }

}
