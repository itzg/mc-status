package me.itzg.mcstatus.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

/**
 * @author Geoff Bourne
 * @since Jun 2017
 */
@Data @JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ServerStatus {
    String host;
    int port;
    String version;
    int protocolVersion;
    PlayersInfo players = new PlayersInfo();
    String description;
    byte[] icon;
}
