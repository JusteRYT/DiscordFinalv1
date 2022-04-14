package com.justeryt.discordbot.commands.commands;

import com.justeryt.discordbot.Main;
import com.justeryt.discordbot.commands.Utils.Utils;
import com.justeryt.discordbot.commands.types.ServerCommand;
import com.justeryt.discordbot.commands.music.MusicController;
import com.justeryt.discordbot.commands.music.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand implements ServerCommand {

    @Override
    public void performCommand(String[] arguments, Guild guild, Member member, TextChannel textChannel, Message message) {
        if (arguments.length == 2) { //! play <url>
            GuildVoiceState voiceState;
            if ((voiceState = member.getVoiceState()) != null) {
                VoiceChannel voiceChannel;
                if ((voiceChannel = voiceState.getChannel()) != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    MusicController musicController = Main.getAudioManager().getMusicController(voiceChannel.getGuild().getIdLong());
                    AudioPlayer player = musicController.getAudioPlayer();
                    AudioPlayerManager audioPlayerManager = Main.getAudioPlayerManager();
                    TrackScheduler scheduler = musicController.getScheduler();
                    player.addListener(scheduler);
                    AudioManager audioManager = voiceState.getGuild().getAudioManager();
                    audioManager.openAudioConnection(voiceChannel);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 1; i < arguments.length; i++) builder.append(arguments[i] + " ");
                    String rawLink = builder.toString().trim();
                    if (!rawLink.startsWith("https")) {
                        rawLink = " ytsearch: " + rawLink;
                    }
                    final String url = rawLink;


                    assert audioPlayerManager != null;
                    audioPlayerManager.loadItem(url, new AudioLoadResultHandler() {
                        @Override
                        public void trackLoaded(AudioTrack audioTrack) {
                            if (audioTrack != null) {
                                embedBuilder.setTitle("Трек загружен: " + audioTrack.getInfo().title);
                                embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
                                embedBuilder.addField("Добавил", String.valueOf(member.getUser().getName()), true);
                                embedBuilder.addField("Длительность", Utils.formatLongDuration(audioTrack.getDuration()), true);
                                textChannel.sendMessageEmbeds(embedBuilder.build()).queue();
                                scheduler.addToQueue(audioTrack);
                            }
                        }

                        @Override
                        public void playlistLoaded(AudioPlaylist audioPlaylist) {
                            textChannel.sendMessage("Аудиоплейлист загружен: " +  audioPlaylist.getName()).queue();
                            for (AudioTrack audioTrack : audioPlaylist.getTracks()) {
                                    scheduler.addToQueue(audioTrack);
                                }
                            }



                        @Override
                        public void noMatches() {
                        textChannel.sendMessage("Не правильный url").queue();
                        }

                        @Override
                        public void loadFailed(FriendlyException e) {
                            scheduler.skip();
                            textChannel.sendMessage("❌Не удалось загрузить трек").queue();

                        }

                    });
                } else {
                    textChannel.sendMessage("Ты должен быть в голосовом чате дебил, а потом написать плей... еблан").queue();
                }
            } else {
                textChannel.sendMessage("Ты должен быть в голосовом чате дебил, а потом написать плей... еблан").queue();
            }
        }
    }

}
