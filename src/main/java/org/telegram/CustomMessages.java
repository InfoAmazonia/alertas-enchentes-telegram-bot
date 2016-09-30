package org.telegram;

/**
 * @author Ruben Bermudez
 * @version 1.0
 * @brief Custom messages to be sent to the user
 * @date 21 of June of 2015
 */
public class CustomMessages {
    public static final String help = "Alerta de enchentes\n\nVeja as previsões de volume para os rios da região amazônica. Para qual rio?\n\n" +
            Commands.STATUSRIOBRANCO + " para previsões do Rio Branco.\n" +
            Commands.STATUSPORTOVELHO + " para previsões de Porto Velho.\n" +
            Commands.HELP + " para obter ajuda.";
}
