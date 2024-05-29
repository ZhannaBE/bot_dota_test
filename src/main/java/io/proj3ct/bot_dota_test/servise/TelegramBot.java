package io.proj3ct.bot_dota_test.servise;


import io.proj3ct.bot_dota_test.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;

    String questions[] =
            {
                    "Какой герой умеет восстанавливать здоровье башням в игре?",
                    "Какой герой может убить другого игрока используя его же способность?",
                    "первый созданный герой в Dota 2",
                    "Сколько персонажей доступно на данный момент?",
                    "Какого урона нет в Доте 2?",
                    "Самый сильный нейтральный персонаж?",
                    "Что не является официальной ролью?",
                    "Как переводится полное название игры?",
                    "Почему битва древних никогда не закончится?",
                    "Сколько персонажей в Доте доступно на данный момент?"
            };
    String answers[][] =
            {
                    {"Трент", "Энигма", "Даззл", "Котел"},
                    {"Абаддон", "Нюкс", "Рубик", "Кристал Мейден"},
                    {"Снайпер", "Lina", "Зевс", "Рики"},
                    {"113", "110", "116", "101"},
                    {"Смешанный", "Физический", "Магический", "Чистый"},
                    {"Нашор", "Голем", "Рошан", "Дракон"},
                    {"Керри", "Саппорт", "Мидер", "Ганкер"},
                    {"Моя оборона", "Оборона древних", "Искатели древнего", "Никак не переводится"},
                    {"Из-за мультивселенных", "День сурка", "У всех героев маразм", "У этого есть конец"},
                    {"113", "110", "116", "101"}
            };
    int rightAnswers[] = {1, 3, 2, 1, 1, 3, 4, 2, 1, 1};
    UserBot user;
    long userId;
    boolean hasShowedConclusion;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }


    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();

            if(userId == 0){
                userId = message.getFrom().getId();
            }
            String text = message.getText();
            if (text.equals("/start")) {
                user = new UserBot(userId);
                startCommandReceived(userId, message.getChat().getFirstName());
            } else {
                if(!user.isReady()) {
                    if (text.equals("да")) {
                        user.setReady(true);
                        sendQuestion(userId, user.getCurrentQuestion());
                    }
                }

            }
            if (user.getCurrentQuestion() == questions.length - 1) {
                sendMessage(userId, "Для повторного прохождения викторины напишите /start");
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("FIRST_BUTTON") && rightAnswers[user.getCurrentQuestion() - 1] == 1 ||
                    callbackData.equals("SECOND_BUTTON") && rightAnswers[user.getCurrentQuestion() - 1] == 2 ||
                    callbackData.equals("THIRD_BUTTON") && rightAnswers[user.getCurrentQuestion() - 1] == 3 ||
                    callbackData.equals("FORTH_BUTTON") && rightAnswers[user.getCurrentQuestion() - 1] == 4) {
                sendMessage(userId, "Верно!");
                user.increaseScore();
            } else {
                sendMessage(userId, "Вы ответили неверно!");
            }

            if (user.getCurrentQuestion() == questions.length - 1) {
                sendMessage(userId, "Для повторного прохождения викторины напишите /start");
            } else {
                sendQuestion(userId, user.getCurrentQuestion());
            }
        }
        if (user.isReady()) {
            if (user.getCurrentQuestion() == questions.length - 1 && !hasShowedConclusion) {
                String conclusionText ="Викторина закончилась\n\n Вы набрали " + user.getScore() + " очков!\n\n";

                switch (user.getScore()){
                    case 0:
                    case 1:
                        conclusionText += "Вы мимо проходили?";
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        conclusionText += "Судя по результатам вы только недавно начали играть в доту. Но ничего страшного! Вернитесь сюда тогда, когда будете уверены, что ваши знания стали лучше";
                        break;
                    case 6:
                    case 7:
                    case 8:
                        conclusionText += "Неплохой результат!";
                        break;
                    case 9:
                    case 10:
                        conclusionText += "Вы случаем не разработчик игры?";
                        break;
                }

                sendMessage(userId, conclusionText);
            } else {
                user.setCurrentQuestion(user.getCurrentQuestion() + 1);
            }
        }
    }

    private void sendQuestion(long chatId, int numberQuestion) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(questions[numberQuestion]);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();

        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(answers[numberQuestion][0]);
        button.setCallbackData("FIRST_BUTTON");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText(answers[numberQuestion][1]);
        button.setCallbackData("SECOND_BUTTON");
        rowInline.add(button);

        rowsInline.add(rowInline);

        rowInline = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(answers[numberQuestion][2]);
        button.setCallbackData("THIRD_BUTTON");
        rowInline.add(button);

        button = new InlineKeyboardButton();
        button.setText(answers[numberQuestion][3]);
        button.setCallbackData("FORTH_BUTTON");
        rowInline.add(button);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answer= "Привет " + name+ ","+ " начнем тест ? (напишите да)";
        sendMessage(chatId,answer);

    }
    private void sendMessage(long chatId, String textToSeng) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSeng);
        try {
            execute(message);
        } catch (TelegramApiException e) {
        }
    }

    @Override
    public String getBotUsername() {
        return "dota_testBot";
    }

    @Override
    public String getBotToken() {
        return "7145975013:AAGd6eKRKwZAns39E2zPTWqesdddOcc0F9Q";
    }
}
