package com.justeryt.discordbot.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;


public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingDeque<AudioTrack> queue;



    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingDeque<>();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        super.onPlayerPause(player);

    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        super.onPlayerResume(player);

    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        super.onTrackStart(player, track);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        super.onTrackEnd(player, track, endReason);
        if (endReason.mayStartNext) {
            startNextTrack(true);
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        super.onTrackException(player, track, exception);
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        super.onTrackStuck(player, track, thresholdMs);
        startNextTrack(false);
    }


    public void addToQueue(AudioTrack audioTrack) {
        queue.addLast(audioTrack);
        startNextTrack(true);
    }

    public void startNextTrack(boolean noInterrupt) {
        AudioTrack next = queue.pollFirst();
        if (next != null) {
            if (!player.startTrack(next, noInterrupt)) {
                queue.addFirst(next);
            }
        } else {
            player.stopTrack();
        }
    }

    public List<AudioTrack> drainQueue() {
        List<AudioTrack> drainQueue = new ArrayList<>();
        queue.drainTo(drainQueue);
        return drainQueue;
    }

    public void playNow(AudioTrack audioTrack, boolean clearQueue) {
        if (clearQueue) {
            queue.clear();
        }
        queue.addFirst(audioTrack);
        startNextTrack(false);
    }

    public void skip() {
        startNextTrack(false);
    }
    public void shuffle(){
        Iterator<AudioTrack> x = queue.iterator();
        while (x.hasNext()){
            AudioTrack track = x.next();
            queue.add(track);
        }
        Collections.shuffle((List<AudioTrack>) queue);
        queue.clear();
        for (AudioTrack track:queue){
            queue.offer(track);
        }
    }
}
