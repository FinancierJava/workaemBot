package org.example;

import org.example.commands.AppBotCommand;
import org.example.commands.BotCommonCommands;
import org.example.functions.FilterOperations;
import org.example.functions.ImagesOperation;
import org.example.utils.ImageUtils;
import org.example.utils.PhotoMessageUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Bot extends TelegramLongPollingBot {

    HashMap<String, Message> messages = new HashMap<>();

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        try {
            SendMessage responseTextMessage = runCommonCommand(message);
            if (responseTextMessage != null) {
                execute(responseTextMessage);
                return;
            }
            responseTextMessage = runPhotoMessage(message);
            if (responseTextMessage != null) {
                execute(responseTextMessage);
                return;
            }
            Object responseMediaMessage = runPhotoFilter(message);
            if (responseMediaMessage != null) {
                if (responseMediaMessage instanceof SendMediaGroup) {
                    execute((SendMediaGroup) responseMediaMessage);
                } else if (responseMediaMessage instanceof SendMessage) {
                    execute((SendMessage) responseMediaMessage);
                }
                return;
            }
            if (message != null && "/start".equals(message.getText())) {
                SendMessage startMessage = handleStartCommand(message);
                execute(startMessage);
                return;
            }
        } catch (InvocationTargetException | IllegalAccessException |
                 TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private KeyboardRow createKeyboardRow(String command) {
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(command));
        return row;
    }
    private SendMediaGroup preparePhotoMessage(List<String> localPaths,
                                               ImagesOperation operation, String chatId) throws Exception {
        SendMediaGroup mediaGroup = new SendMediaGroup();
        ArrayList<InputMedia> medias = new ArrayList<>();
        for (String path : localPaths) {
            InputMedia inputMedia = new InputMediaPhoto();
            PhotoMessageUtils.processingImage(path, operation);
            inputMedia.setMedia(new java.io.File(path), "path");
            medias.add(0, inputMedia);
        }
        mediaGroup.setMedias(medias);
        mediaGroup.setChatId(chatId);
        return mediaGroup;
    }


    private Object runPhotoFilter(Message newMessage) {
        final String text = newMessage.getText();
        ImagesOperation operation = ImageUtils.getOperation(text);
        if (operation == null) return null;
        String chatId = newMessage.getChatId().toString();
        Message photoMessage = messages.get(chatId);
        if (photoMessage != null) {
            List<org.telegram.telegrambots.meta.api.objects.File> files = getFilesByMessage(photoMessage);
            try {
                List<String> paths = PhotoMessageUtils.savePhotos(files,
                        getBotToken());
                return preparePhotoMessage(paths, operation, chatId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Мы конечно можем долго болтать, но у меня нет на это времени. Отправьте фото и я наложу тот фильтр, который понадобится");
            return sendMessage;
        }
        return null;
    }

    private SendMessage runPhotoMessage(Message message) {
        List<org.telegram.telegrambots.meta.api.objects.File> files = getFilesByMessage(message);
        if (files.isEmpty()) {
            return null;
        }
        String chatId = message.getChatId().toString();
        messages.put(chatId, message);
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> allKeyboardRows = new
                ArrayList<>(getKeyboardRows(FilterOperations.class));
        replyKeyboardMarkup.setKeyboard(allKeyboardRows);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Выберите фильтр");
        return sendMessage;
    }


    private List<org.telegram.telegrambots.meta.api.objects.File> getFilesByMessage(Message message) {
        List<PhotoSize> photoSizes = message.getPhoto();
        if (photoSizes == null) return new ArrayList<>();
        ArrayList<org.telegram.telegrambots.meta.api.objects.File> files = new ArrayList<>();
        for (PhotoSize photoSize : photoSizes) {
            final String fileId = photoSize.getFileId();
            try {
                files.add(sendApiMethod(new GetFile(fileId)));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return files;
    }


    private ReplyKeyboardMarkup getKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> allKeyboardRows = new ArrayList<>();
        allKeyboardRows.add(createKeyboardRow("/hello"));
        allKeyboardRows.add(createKeyboardRow("/bye"));
        allKeyboardRows.add(createKeyboardRow("/help"));
        allKeyboardRows.addAll(getKeyboardRows(BotCommonCommands.class));
        allKeyboardRows.addAll(getKeyboardRows(FilterOperations.class));
        return replyKeyboardMarkup;
    }
    private static ArrayList<KeyboardRow> getKeyboardRows(Class someClass) {
        Method[] classMethods = someClass.getDeclaredMethods();
        ArrayList<AppBotCommand> commands = new ArrayList<>();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(AppBotCommand.class)) {
                commands.add(method.getAnnotation(AppBotCommand.class));
            }
        }
        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>();
        int columCount = 3;
        int rowCount = commands.size() / columCount + ((commands.size() % columCount == 0) ? 0 : 1);
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            KeyboardRow row = new KeyboardRow();
            for (int columIndex = 0; columIndex < columCount; columIndex++) {
                int index = rowIndex * columCount + columIndex;
                if (index >= commands.size()) continue;
                AppBotCommand command = commands.get(rowIndex * columCount + columIndex);
                KeyboardButton keyboardButton = new KeyboardButton(command.name());
                row.add(keyboardButton);
            }
            keyboardRows.add(row);
        }
        return keyboardRows;
    }

    private SendMessage runCommonCommand(Message message) throws InvocationTargetException, IllegalAccessException {
        String text = message.getText();
        BotCommonCommands commands = new BotCommonCommands();
        Method[] classMethods = commands.getClass().getDeclaredMethods();
        for (Method method : classMethods) {
            if (method.isAnnotationPresent(AppBotCommand.class)) {
                AppBotCommand command = method.getAnnotation(AppBotCommand.class);
                if (command.name().equals(text)) {
                    method.setAccessible(true);
                    String responseText = (String) method.invoke(commands);
                    if (responseText != null) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId().toString());
                        sendMessage.setText(responseText);
                        return sendMessage;
                    }
                }
            }
        }
        return null;
    }

    private SendMessage handleStartCommand(Message message) {
        SendMessage responseMessage = new SendMessage();
        responseMessage.setChatId(message.getChatId().toString());
        responseMessage.setText("Привет! Я бот для обработки фотографий. Отправьте мне фото, и я применю фильтры.");
        return responseMessage;
    }
    @Override
    public String getBotUsername() {
        return "PhotoFilters";
    }

    @Override
    public String getBotToken() {
        return "6829186949:AAFCiwo2Ee37wYK2vhnVkuEPgR6mibdGHDw";
    }
}