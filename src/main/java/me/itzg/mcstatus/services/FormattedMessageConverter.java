package me.itzg.mcstatus.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Geoff Bourne
 * @since Aug 2018
 */
@Component
public class FormattedMessageConverter {
    enum Type {
        COLOR,
        FORMAT,
        RESET
    }

    @RequiredArgsConstructor
    static class Entry {
        final Type type;
        final String style;
    }

    private static Map<String, Entry> codes = new HashMap<>();
    static {
        codes.put("§0", new Entry(Type.COLOR, "color:#000000"));
        codes.put("§1", new Entry(Type.COLOR, "color:#0000AA"));
        codes.put("§2", new Entry(Type.COLOR, "color:#00AA00"));
        codes.put("§3", new Entry(Type.COLOR, "color:#00AAAA"));
        codes.put("§4", new Entry(Type.COLOR, "color:#AA0000"));
        codes.put("§5", new Entry(Type.COLOR, "color:#AA00AA"));
        codes.put("§6", new Entry(Type.COLOR, "color:#FFAA00"));
        codes.put("§7", new Entry(Type.COLOR, "color:#AAAAAA"));
        codes.put("§8", new Entry(Type.COLOR, "color:#555555"));
        codes.put("§9", new Entry(Type.COLOR, "color:#5555FF"));
        codes.put("§a", new Entry(Type.COLOR, "color:#55FF55"));
        codes.put("§b", new Entry(Type.COLOR, "color:#55FFFF"));
        codes.put("§c", new Entry(Type.COLOR, "color:#FF5555"));
        codes.put("§d", new Entry(Type.COLOR, "color:#FF55FF"));
        codes.put("§e", new Entry(Type.COLOR, "color:#FFFF55"));
        codes.put("§f", new Entry(Type.COLOR, "color:#FFFFFF"));

        codes.put("§l", new Entry(Type.FORMAT, "font-weight:bold"));
        codes.put("§m", new Entry(Type.FORMAT, "text-decoration:line-through"));
        codes.put("§n", new Entry(Type.FORMAT, "text-decoration:underline"));
        codes.put("§o", new Entry(Type.FORMAT, "font-style:italic"));

        codes.put("§r", new Entry(Type.RESET, ""));
    }

    private static final Pattern CODE_PATTERN = Pattern.compile("(§.)");

    public String convertToHtml(String raw) {
        final Matcher m = CODE_PATTERN.matcher(raw);
        final StringBuffer sb = new StringBuffer();
        boolean inColor = false;
        boolean inFormat = false;

        while (m.find()) {
            final String code = m.group();
            final Entry entry = codes.get(code);
            final StringBuilder replacement = new StringBuilder();
            if (entry != null) {
                switch (entry.type) {
                    case COLOR:
                        if (inFormat) {
                            replacement.append("</span>");
                            inFormat = false;
                        }
                        if (inColor) {
                            replacement.append("</span>");
                        }
                        inColor = true;
                        break;
                    case FORMAT:
                        if (inFormat) {
                            replacement.append("</span>");
                        }
                        inFormat = true;
                        break;
                    case RESET:
                        if (inFormat) {
                            replacement.append("</span>");
                        }
                        if (inColor) {
                            replacement.append("</span>");
                        }
                        inColor = inFormat = false;
                        break;
                }

                replacement.append(entry.type == Type.RESET ? "" : "<span style=\"" +
                    entry.style + ";\">");
            }

            m.appendReplacement(sb, replacement.toString());
        }
        m.appendTail(sb);
        if (inColor) {
            sb.append("</span>");
        }
        if (inFormat) {
            sb.append("</span>");
        }

        return sb.toString().replace("\n", "<br>");
    }

    public String stripCodes(String raw) {
        return CODE_PATTERN.matcher(raw)
            .replaceAll("")
            .replaceAll("\\s+", " ")
            .trim();
    }
}
