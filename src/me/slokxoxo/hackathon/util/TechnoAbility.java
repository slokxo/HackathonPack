package me.slokxoxo.hackathon.util;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.ChiAbility;
import com.projectkorra.projectkorra.ability.SubAbility;
import org.bukkit.entity.Player;

public abstract class TechnoAbility extends ChiAbility implements SubAbility {
    public TechnoAbility(Player player) {
        super(player);
    }

    public Class<? extends Ability> getParentAbility() {
        return ChiAbility.class;
    }

    public Element getElement() {
        return TechnoSubElement.TECHNO;
    }
}
