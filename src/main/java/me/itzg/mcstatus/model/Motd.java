package me.itzg.mcstatus.model;

import lombok.Data;

/**
 * @author Geoff Bourne
 * @since Aug 2018
 */
@Data
public class Motd {
    String raw;
    String html;
    String stripped;
}
