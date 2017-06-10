package me.itzg.mcstatus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@Data @JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PlayersInfo {
    int max;
    int online;
    List<Player> players;
}
