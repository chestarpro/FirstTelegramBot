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

public class Bot extends TelegramLongPollingBot {
    private final ArrayList<Message> messageArrayList = new ArrayList<>();
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
        messageArrayList.add(message);
        if(messageArrayList.size() == 1) {
            if ("/help".equals(messageArrayList.get(0).getText())) {
                sendMsg(messageArrayList.get(0), "1");
                messageArrayList.removeAll(messageArrayList);
            }
        }


        if (messageArrayList.get(0).getText().equals("/translate") && messageArrayList.size() == 1) {
            sendMsg(messageArrayList.get(0), "Введите язык для перевода.");
        }
        if(messageArrayList.size() == 2 && messageArrayList.get(0).getText().equals("/translate")) {
            try {
                sendMsg(messageArrayList.get(1), Weather.getWeather(messageArrayList.get(1).getText(), weatherModel));
            } catch (IOException e) {
                sendMsg(messageArrayList.get(1), "Город не найден");
            }
            finally {
                messageArrayList.removeAll(messageArrayList);
            }
        }

        if (messageArrayList.get(0).getText().equals("/weather") && messageArrayList.size() == 1) {
            sendMsg(messageArrayList.get(0), "Выберите город");
        }
        if(messageArrayList.size() == 2 && messageArrayList.get(0).getText().equals("/weather")) {
            try {
                sendMsg(messageArrayList.get(1), Weather.getWeather(messageArrayList.get(1).getText(), weatherModel));
            } catch (IOException e) {
                sendMsg(messageArrayList.get(1), "Город не найден");
            }
            finally {
                messageArrayList.removeAll(messageArrayList);
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

        keyboardFirstRow.add(new KeyboardButton("/help"));
        keyboardFirstRow.add(new KeyboardButton("/weather"));
        keyboardFirstRow.add(new KeyboardButton("/translate"));

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
