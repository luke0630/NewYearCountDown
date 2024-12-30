package org.luke.newYearCountDown;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.luke.takoyakiLibrary.TakoUtility;

import java.time.Duration;

public class EventManager implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        int nextYear = UtilityClass.getNextNewYear().getYear();
        Duration leftTime = UtilityClass.getLeftTime();

        long left_hours = leftTime.toHours();
        long left_minutes = leftTime.toMinutes();

        String builder = nextYear + "まで あと" +
                left_hours + "時間" + ":" +
                left_minutes + "分";


        player.sendMessage(
                TakoUtility.toColor(builder)
        );
    }

}
