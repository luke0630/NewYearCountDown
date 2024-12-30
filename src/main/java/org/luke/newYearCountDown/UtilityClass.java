package org.luke.newYearCountDown;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@lombok.experimental.UtilityClass
public class UtilityClass {

    public LocalDateTime getNextNewYear() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0);
    }
    public Duration getLeftTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());

        // 次の新年の時刻を計算
        LocalDateTime nextNewYear = getNextNewYear();

        // 現在の時刻と新年の時刻の差を計算
        return Duration.between(now, nextNewYear);
    }
}
