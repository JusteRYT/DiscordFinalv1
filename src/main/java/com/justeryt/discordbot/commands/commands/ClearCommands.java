package com.justeryt.discordbot.commands.commands;

import com.justeryt.discordbot.commands.types.ServerCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

import java.util.List;

public class ClearCommands implements ServerCommand {

    @Override
    public void performCommand(String[] arguments, Guild guild, Member member, TextChannel textChannel, Message message){
        if(arguments.length < 2) {
            textChannel.sendMessage("🤡Извините мистер, но вы бы не могли, сука ,после команды ввести число, блять").queue(); }
         else {
            try {
                try{
                    message.delete().queue();
                }catch (InsufficientPermissionException exception){
                    textChannel.sendMessage("😅Прости, но ты не дал мне права на это").queue();
                }
                List<Message> messageList = textChannel.getHistory().retrievePast(Integer.parseInt(arguments[1])).complete();
                textChannel.deleteMessages(messageList).queue();
                textChannel.sendMessage("☺Кароча я удалил твою жизнь, мать, собаку и ту шлюху, о которой не знает твоя девушка!").queue();
            } catch (IllegalArgumentException exception) {
                if (exception.toString().startsWith("java.lang.IllegalArgumentException: Message retrieval")) {
                    textChannel.sendMessage("😡Да блять, я не могу нахуй удалить больше 100 сообщений").queue();
                } else {
                    textChannel.sendMessage("🤔Один слишком мало, тут уже не к тебе претензии, а к разрабу").queue();
                }
        }
        }
    }
}



