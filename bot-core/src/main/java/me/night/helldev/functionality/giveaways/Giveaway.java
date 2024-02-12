package me.night.helldev.functionality.giveaways;

import lombok.Data;
import lombok.Getter;

import java.time.Duration;
import java.util.List;

@Data
@Getter
public class Giveaway {

    private int id;
    private long messageId;
    private long textChannelId;
    private String name;
    private long creator;
    private Duration duration;
    private List<Long> users;
}
