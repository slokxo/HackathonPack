package me.slokxoxo.hackathon.util;

import com.projectkorra.projectkorra.Element;
import net.md_5.bungee.api.ChatColor;

public class TechnoSubElement {
    public static final Element.SubElement TECHNO;

    public TechnoSubElement() {
    }
    static {
        TECHNO = new Element.SubElement("Techno", Element.CHI) {
            @Override
            public ChatColor getColor() {
                return Element.CHI.getColor();
            }
        };
    }
}

