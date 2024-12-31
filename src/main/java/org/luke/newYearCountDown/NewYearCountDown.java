package org.luke.newYearCountDown;

import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.luke.takoyakiLibrary.TakoUtility.toColor;

public final class NewYearCountDown extends JavaPlugin {
    public static final LocalDateTime time = LocalDateTime.of(2025, 1, 1, 0, 0);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        startPreciseCountdown();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Duration getLeftMillis() {
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

            @Override
            public void run() {
                long millisUntilCountdownStart = getLeftMillis().toMillis();
                Duration left = getLeftMillis();

                LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
                if(millisUntilCountdownStart >= 1000*30) {
                    long current_hour = now.getHour();
                    long current_minute = now.getMinute();
                    long current_second = now.getSecond();

                    String barMessage = "&a現在時刻: "+ current_hour +"時 "
                            + current_minute +"分 "
                            + current_second + "秒 &f|&c&l 新年まで ";
                    if(left.toHours() >= 1)  {
                        barMessage += left.toHours() + "時間 "
                                + left.toMinutesPart() +"分";
                    } else if(left.toMinutes() < 60){
                        barMessage += left.toMinutes() +"分 ";
                    } else  {
                        barMessage += left.toSeconds() +"秒";
                    }

                    for(Player player: Bukkit.getOnlinePlayers()) {
                        player.sendActionBar(toColor(barMessage));
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

                            if(previousSecond != second) {
                                previousSecond = second;
                                PlaySoundEffect();
                            }
                            player.sendTitle(toColor(title), toColor("&c===&6新年へのカウントダウン&c==="), 0, 20 * 2, 0);
                        }
                    } else {
                        if(previousSecond != second) {
                            previousSecond = second;
                            if(millisUntilCountdownStart <= 1000 * 61) {
                                PlaySoundEffect();
                                Bukkit.broadcastMessage(ChatColor.YELLOW + "新年まであと " + ChatColor.RED + second + ChatColor.YELLOW + " 秒！");
                            } else if(second % 60 == 0 && minute <= 10) { //1分に一度メッセージを送信(10分前から開始)
                                PlaySoundEffect();
                                Bukkit.broadcastMessage(toColor("&e新年まであと &c"+ minute +"&e分 &c"+ getLeftMillis().toSecondsPart() +"&c秒"));
                            } else if(second % (60 * 15) == 0 && minute < 60) { // 15分に一度メッセージを送信
                                PlaySoundEffect();
                                Bukkit.broadcastMessage(toColor("&e新年まであと &c"+ minute +"&e分 &c"+ getLeftMillis().toSecondsPart() +"&c秒"));
                            } else if(second % (60 * 60) == 0 && minute >= 60) {
                                PlaySoundEffect();
                                Bukkit.broadcastMessage(toColor("&e新年まであと &c"+ left.toHours() +"&e時間 &c"+ left.toMinutesPart() +"&c分"));
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

    private void PlaySoundEffect() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(
                    player.getLocation(),                 // 再生する位置（プレイヤーの位置）
                    Sound.UI_BUTTON_CLICK,                          // サウンドの名前
                    1.0f,                                 // 音量
                    1.0f                                  // ピッチ
            );
        }
    }

    private void launchFireworks() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.getWorld().spawn(player.getLocation(), Firework.class, firework -> {
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(FireworkEffect.builder()
                        .withColor(Color.RED)
                        .withColor(Color.YELLOW)
                        .withFade(Color.ORANGE)
                        .with(FireworkEffect.Type.BALL_LARGE)
                        .trail(true)
                        .flicker(true)
                        .build());
                meta.setPower(1); // パワーを設定
                firework.setFireworkMeta(meta);
            });

            player.spawnParticle(Particle.EXPLOSION_EMITTER, player.getLocation(), 1, 0, 0, 0, 0.1);
        }
    }

    private void Complete() {
        Bukkit.broadcastMessage(ChatColor.GOLD + "あけましておめでとうございます！ : " +  (time.getYear() + 1) + "年");
        for(Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(toColor("&6&lあけおめ"), (time.getYear() + 1)+"年度もよろしく～～～～", 20, 20 * 20, 20);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(
                    player.getLocation(),                 // 再生する位置（プレイヤーの位置）
                    Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, // サウンドの名前
                    1.1f,                                 // 音量
                    1.0f                                  // ピッチ
            );
        }
        playBGMToAllPlayers();
        launchFireworks();
    }

    public static void playBGMToAllPlayers() {
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
