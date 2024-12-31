package org.luke.newYearCountDown;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.luke.takoyakiLibrary.TakoUtility;

public class EventManager implements Listener{
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.playSound(
                player.getLocation(),                 // 再生する位置（プレイヤーの位置）
                Sound.ENTITY_PLAYER_LEVELUP, // サウンドの名前
                1.0f,                                 // 音量
                1.0f                                  // ピッチ
        );
        player.sendMessage(
                TakoUtility.toColor("&c&l"+NewYearCountDown.time.getYear() + "年までのカウントダウン待機所です！")
        );
        player.sendMessage(
                TakoUtility.toColor("&a&l好きに建築してください！！！ただし、荒らしは禁止です")
        );
        if(NewYearCountDown.getLeftMillis().isNegative()) {
            player.sendMessage(
                    TakoUtility.toColor("&6あけおめ～～～～")
            );
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        if (type == Material.BARRIER || type == Material.BEDROCK) {
            Player player = event.getPlayer();
            if (!player.isOp()) {
                event.setCancelled(true);
            }
        }
    }
}
