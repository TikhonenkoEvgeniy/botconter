package com.example.botcounter.controller;

import com.example.botcounter.entity.User;
import com.example.botcounter.service.JournalService;
import com.example.botcounter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CommandController {
    private final UserService userService;
    private final JournalService journalService;

    public CommandController(@Autowired UserService userService,
                             @Autowired JournalService journalService) {
        this.userService = userService;
        this.journalService = journalService;
    }

    public SendMessage sendGreeting(Update update) {
        String s = journalService.getLastDateByUserId(update.getMessage().getFrom().getId());
        Double defaultLimit = 300D;
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        User user;
        if (userService.isExist(userId)) {
            user = userService.getUserById(userId);
            return new SendMessage(chatId, "Данный пользователь уже сохранен в БД!\n" +
                    "Установренный дневной лимит = " + user.getLimitPerDay());
        } else {
            user = new User(userId, update.getMessage().getFrom().getFirstName(),
                    update.getMessage().getFrom().getLastName(),
                    update.getMessage().getFrom().getUserName(), defaultLimit);
            userService.saveUser(user);
            return new SendMessage(chatId, "Добро пожаловать! Вы сохренены в БД!\n" +
                    "Установренный дневной лимит = " + user.getLimitPerDay());
        }
    }

    public SendMessage sendGoodbye(Update update) {
        //journalService.deleteByUserId(update.getMessage().getFrom().getId()); //todo пофиксит удаление из БД
        userService.deleteUser(update.getMessage().getFrom().getId());
        return new SendMessage(update.getMessage().getChatId().toString(),
                "Бот остановлен, все данные из БД удалены!");
    }

    public SendMessage setLimit(Update update) {
        Message message = update.getMessage();
        String command = message.getText().split(" ")[0];
        String param = message.getText().replaceAll(command, "").trim();

        double limitPerDay;
        if (param.equals("")) {
            limitPerDay = userService.getUserLimitPerDayById(update.getMessage().getFrom().getId());
            return new SendMessage(message.getChatId().toString(),
                    "Установренный дневной лимит = " + limitPerDay);
        }
        try {
            limitPerDay = Double.parseDouble(param);
            if (limitPerDay < 0) {
                return new SendMessage(message.getChatId().toString(),
                        "Ошибка: Лимит на день не может быть меньше 0");
            } else {
                userService.setUserLimitPerDayById(update.getMessage().getChatId(), limitPerDay);
                if (journalService.isExistByUserId(update.getMessage().getFrom().getId())) {
                    return new SendMessage(message.getChatId().toString(),
                            "Установренный дневной лимит = " + limitPerDay + "\n" +
                            "Поскольку сегодня уже были затраты, то новый лимит\n" +
                            "вступит в действие со следующего дня.");
                }
                return new SendMessage(message.getChatId().toString(),
                        "Установренный дневной лимит = " + limitPerDay);
            }
        } catch (NumberFormatException exception) {
            return new SendMessage(message.getChatId().toString(),
                    "Ошибка: Не верный формат параметра команды!\n" +
                    "Пример: /limit 300");
        }
    }

    public SendMessage getBalance(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        double balance;
        if (journalService.isExistByUserId(userId)) {
            balance = journalService.getBalanceOnToday(userId);
        } else {
            balance = userService.getUserLimitPerDayById(userId);
        }
        if (balance > 0) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    "Доступный остаток на сегодня: " + balance);
        } else if (balance < 0) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    "Перерасход на сегодня: " + balance * -1);
        } else {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    "У вас нет остатка на сегодня!");
        }
    }

    public SendMessage getAllMoney(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        if (!journalService.isExistByUserId(userId)) {
            journalService.saveBalanceOnToday(userId, userService.getUserLimitPerDayById(userId));
        }
        Long balance = journalService.getSumBalanceByUserId(userId);
        int days = journalService.countAllByUserId(userId);
        if (balance > 0) {
            return new SendMessage(update.getMessage().getChatId().toString(),
                    "Накоплено денег: " + balance + "\n" +
                            "Количество дней: " + days);
        } else {
            if (balance < 0) {
                return new SendMessage(update.getMessage().getChatId().toString(),
                        "Отрицательное накопление денег: " + balance + "\n" +
                                "Количество дней: " + days);
            } else {
                return new SendMessage(update.getMessage().getChatId().toString(),
                        "Поздравляю! Вы вышли в 0, что так же хорошо\n" +
                                "Количество дней: " + days);
            }
        }
    }
}
