package com.example.botcounter.controller;

import com.example.botcounter.config.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.example.botcounter.data.Commands.*;

@Component
public class MainController extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final CommandController commandController;
    private final MessageController messageController;

    public MainController(@Autowired BotConfig botConfig,
                          @Autowired CommandController commandController,
                          @Autowired MessageController messageController) {
        this.botConfig = botConfig;
        this.commandController = commandController;
        this.messageController = messageController;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage()) {
                if (update.getMessage().isCommand()) {
                    switch (update.getMessage().getText().split(" ")[0]) {
                        case (COMMAND_START) -> execute(commandController.sendGreeting(update));
                        case (COMMAND_BALANCE) -> execute(commandController.getBalance(update));
                        case (COMMAND_MONEY) -> execute(commandController.getAllMoney(update));
                        case (COMMAND_LIMIT) -> execute(commandController.setLimit(update));
                        case (COMMAND_STOP) -> execute(commandController.sendGoodbye(update));
                        default -> execute(new SendMessage(update.getMessage().getChatId().toString(),
                                "Неизвестная команда!"));
                    }
                } else {
                    execute(messageController.save(update));
                }
            }
        } catch (TelegramApiException exception) {
            exception.printStackTrace();
        }
    }
}
