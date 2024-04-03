package client.features.module.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.TimeHelper;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.ArrayList;
import java.util.Objects;

public class AdminCheckerTest extends Module {
    private final TimeHelper timer= new TimeHelper();
    private final TimeHelper timer2 = new TimeHelper();
    private String currentName;
    public NumberSetting checkswitchdelay;
    public static int index;
    private final ArrayList<String> adminsforcheck = new ArrayList<>();
    private final ArrayList<String> checkedAdmins = new ArrayList<>();
    public AdminCheckerTest() {
        super("AdminCheckerTest", 0, Category.MISC);
    }
public void init(){
        super.init();
        checkswitchdelay =new NumberSetting("Check Delay", 100, 10, 1000, 1);
        addSetting(checkswitchdelay);
}
    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate){

            for (String admin : getAdministrators()) {
                for (PlayerListEntry map : mc.getNetworkHandler().getPlayerList()) {
                        if (map.getProfile().getName().contains(admin)) {
                            checkedAdmins.add(admin);

                        } else {

                            adminsforcheck.add(admin);
                        }
                    }
                }


                if(timer2.hasReached(checkswitchdelay.getValue())){
                    index++;
                    timer2.reset();
                    if (index >= getAdministrators().length) {
                        index = 0;
                    }
                   currentName = getAdministrators()[index];
                    mc.player.sendChatMessage("/rank " + currentName);
                    ChatUtils.printChat("Send Chat Packet"+ currentName);
                }

        }
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                if(event.getPacket() instanceof GameMessageS2CPacket) {
                    GameMessageS2CPacket packet = (GameMessageS2CPacket) event.getPacket();
                    if (packet.getMessage().toString().contains("TextComponent") &&packet.getMessage().toString().contains("No player was found") &&!packet.getMessage().toString().contains("TranslatableComponent")) {
                        ChatUtils.printChat("Debug:" + currentName +" was found!");
                        checkedAdmins.add(currentName);
                    }
                }

            }
        }
    }
    public void onEnable(){
        timer.reset();
        adminsforcheck.clear();
    }


    public String[] getAdministrators() {
        return new String[] {
               "ImbC",
                "DJ_Pedro",
                "Outra",
                "Sevy13",
                "lPirlo",
                "Galap",
                "FullAdmin",
                "Carrots386",
                "Axyy",
                "ImAbbyy",
                "LangScott"
        };
    }
}
