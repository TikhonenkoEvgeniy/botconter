package com.example.botcounter.controller;

import com.example.botcounter.service.JournalService;
import com.example.botcounter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageController {
    private final JournalService journalService;
    private final UserService userService;

    public MessageController(@Autowired JournalService journalService,
                             @Autowired UserService userService) {
        this.journalService = journalService;
        this.userService = userService;
    }

    public SendMessage save(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        double balance;
        try {
            double price = Double.parseDouble(update.getMessage().getText());
            if (journalService.isExistByUserId(userId)) {
                balance = journalService.getBalanceOnToday(userId) + price * -1;
            } else {
                balance = userService.getUserLimitPerDayById(userId) + price * -1;
            }
            if (price < 0) {
                journalService.saveBalanceOnToday(userId, balance);
                if (balance < 0) {
                    return new SendMessage(chatId, "Отменена затрата на сумму: " + price * -1 + "\n" +
                            "Перерасход дневного лимита на: " + balance * -1);
                } else if (balance > 0) {
                    return new SendMessage(chatId, "Отменена затрата на сумму: " + price * -1 + "\n" +
                            "Остаток на сегодня: " + balance);
                } else {
                    return new SendMessage(chatId, "Отменена затрата на сумму: " + price * -1 + "\n" +
                            "У вас нет остатка на сегодня!");
                }
            } else {
                if (price > 0) {
                    journalService.saveBalanceOnToday(userId, balance);
                    if (balance < 0) {
                        return new SendMessage(chatId, "Внесена новая затрата в размере: " + price + "\n" +
                                "Перерасход на сегодня: " + balance * -1);
                    } else if (balance > 0) {
                        return new SendMessage(chatId, "Внесена новая затрата в размере: " + price + "\n" +
                                "Остаток на сегодня: " + balance);
                    } else {
                        return new SendMessage(chatId, "Внесена новая затрата в размере: " + price + "\n" +
                                "У вас нет остатка на сегодня!");
                    }
                } else {
                    return new SendMessage(chatId, "Поздравляю! Вы ничего не потратили - мы ничего не списали");
                }
            }
        } catch (NumberFormatException exception) {
            return new SendMessage(chatId, "Ошибка: Вводите только сумму затраты!\nНапример: 50");
        }
    }
}
