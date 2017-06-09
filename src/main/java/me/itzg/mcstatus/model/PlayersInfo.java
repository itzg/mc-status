package me.itzg.mcstatus.model;

import lombok.Data;

import java.util.List;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@Data
public class PlayersInfo {
    int max;
    int online;
    List<Player> players;
}
