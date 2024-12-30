package org.luke.newYearCountDown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.luke.takoyakiLibrary.TakoUtility.toColor;

public final class NewYearCountDown extends JavaPlugin {
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    LocalDateTime time = LocalDateTime.of(2025, 1, 1, 0, 0);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        startPreciseCountdown();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private Duration getLeftMillis() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        // 残り時間をミリ秒で計算
        return Duration.between(now, time);
    }

    private void startPreciseCountdown() {
        Bukkit.getScheduler().runTaskLater(this, this::startCountdown, 0);
    }

    private void startCountdown() {
        new BukkitRunnable() {
            int previousSecond = -1;
            long previousMinute = -1;

            @Override
            public void run() {
                long millisUntilCountdownStart = getLeftMillis().toMillis();

                LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
                if(Duration.between(now, time).toMillis() >= 1000*30) {
                    long current_hour = now.getHour();
                    long current_minute = now.getMinute();
                    long current_second = now.getSecond();
                    for(Player player: Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(toColor("&6&l現在時刻: "+ current_hour +"時 "+ current_minute +"分 " + current_second + "秒"));
                    }
                }

                if (millisUntilCountdownStart >= 0) {
                    int second = (int) (millisUntilCountdownStart / 1000.0);
                    int minute = (int) (millisUntilCountdownStart / (1000.0 * 60));

                    if(millisUntilCountdownStart <= 1000 * 15) {
                        DecimalFormat decimalFormat = new DecimalFormat("0.0");
                        String formattedTime = decimalFormat.format(millisUntilCountdownStart / 1000.0);

                        for(Player player : Bukkit.getOnlinePlayers()) {
                            String title = formattedTime;

                            if(millisUntilCountdownStart <= 1000 * 4) {
                                title = "&c&l" + formattedTime;
                            } else if(millisUntilCountdownStart <= 1000 * 7) {
                                title = "&6&l" + formattedTime;
                            } else if(millisUntilCountdownStart <= 1000 * 11) {
                                title = "&b&l" + formattedTime;
                            }

                            player.sendTitle(toColor(title), toColor("&c===&6新年へのカウントダウン&c==="), 0, 20 * 2, 0);
                        }
                    } else {


                        if(previousSecond != second) {
                            previousSecond = second;
                            if(millisUntilCountdownStart <= 1000 * 21) {
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "新年まであと " + ChatColor.RED + second + ChatColor.YELLOW + " 秒！");
                            } else if(second % 60 == 0) { //1分に一度メッセージを送信
                                Bukkit.broadcastMessage(toColor("&e新年まであと &c"+ minute +"&e分 &c"+ getLeftMillis().toSecondsPart() +"&c秒"));
                            }
                        }
                    }
                } else {
                    Complete();
                    cancel();
                }
            }
        }.runTaskTimer(this, 0, 2); // 2 ticks = 0.1秒
    }

    private void Complete() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "あけましておめでとうございます！");
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(toColor("&6&lあけおめ"), (time.getYear() + 1)+"年度もよろしく～～～～", 20, 20 * 20, 20);
        }
        playBGMToAllPlayers();
    }

    public void playBGMToAllPlayers() {
        String customSound = "newyear_pack:music_disc.newyearbgm"; // データパックで登録したサウンド名

        // サーバーにいる全プレイヤーにBGMを再生
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(
                    player.getLocation(),                 // 再生する位置（プレイヤーの位置）
                    customSound,                          // サウンドの名前
                    SoundCategory.RECORDS,               // サウンドのカテゴリ
                    0.5f,                                 // 音量
                    1.0f                                  // ピッチ
            );
        }
    }
}
