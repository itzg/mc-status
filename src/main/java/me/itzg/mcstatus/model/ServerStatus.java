package me.itzg.mcstatus.model;

import lombok.Data;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@Data
public class ServerStatus {
    String host;
    int port;
    String version;
    int protocolVersion;
    PlayersInfo players = new PlayersInfo();
    String description;
    byte[] icon;
}
