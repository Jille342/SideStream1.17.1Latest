package client.features.module.combat;

import java.util.*;

import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.features.module.ModuleManager;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
public final class AntiBot extends Module {

    public AntiBot() {
        super("AntiBot", 0, Category.COMBAT);
    }

    static ModeSetting mode;
    Entity currentEntity;
    Entity[] playerList;
    int index;
    boolean next;
    public static NumberSetting matrixflyingmotiony;

    @Override
    public void init() {
        super.init();
        mode = new ModeSetting("Mode ", "Shotbow", new String[]{"Hypixel", "Mineplex", "Shotbow", "ShotbowTeams", "MatrixFlying"});
        addSetting(mode);
    }

    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setTag(mode.getMode());
        }

        }


    public static boolean isBot(PlayerEntity entityPlayer) {
        if (!(ModuleManager.getModulebyClass(AntiBot.class).isEnable()))
            return false;
        switch (mode.getMode()) {
            case "Shotbow":
                return entityPlayer.getHealth() - entityPlayer.getAbsorptionAmount() != 0.1f || mc.getNetworkHandler().getPlayerListEntry(entityPlayer.getUuid()).getProfile() == null;
            case "Hypixel":
                return entityPlayer.isInvisible();
            case"ShotbowTeams":
                return false;
            case"MatrixFlying":
          return false;
        }
        return false;
    }


}
