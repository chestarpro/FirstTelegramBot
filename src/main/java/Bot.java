import com.ibm.cloud.sdk.core.service.exception.BadRequestException;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Bot extends TelegramLongPollingBot {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new Bot());

        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            setButtons(sendMessage);
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        WeatherModel weatherModel = new WeatherModel();
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            if(message.getText().equals("help")) {
                sendMsg(message, "");
            }
            if (message.getText().substring(0, 1).toLowerCase(Locale.ROOT).equals("w")) {  // --> weather/bishkek
                try {
                    sendMsg(message, Weather.getWeather(message.getText().substring(2), weatherModel)); // --> bishkek
                } catch (IOException e) {
                    sendMsg(message, "Город не найден!");
                }
            }
            if (message.getText().substring(0, 1).toLowerCase(Locale.ROOT).equals("t")) { // --> translate/en-ru/text
                try {
                    sendMsg(message, TranslatorIBM.getTranslate(message.getText().substring(8), message.getText().substring(2, 7))); // en-ru/text
                }
                catch (BadRequestException e) {
                    sendMsg(message, "Поле пустое!");
                } catch (NotFoundException e) {
                    sendMsg(message, "Язык не найден!");
                }
            }
        }
    }

    public void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("help"));
        keyboardFirstRow.add(new KeyboardButton("fun"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    public String getBotUsername() {
        return "ChestarHelpBot";
    }

    public String getBotToken() {
        return "1811353563:AAErJjY6-XbiUEtH6GZIKYt_-CHRoxQqDh8";
    }
}
