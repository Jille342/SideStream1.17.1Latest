package client.features.module.misc;

import client.Client;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.features.module.Module;
import client.utils.ChatUtils;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

public class PacketChecker extends Module {

    public PacketChecker() {
        super("PacketChecker", 0, Category.MISC);
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventPacket) {
            EventPacket event = ((EventPacket)e);
            if(event.isIncoming()) {
                Packet<?> p = event.getPacket();

                if(p instanceof GameMessageS2CPacket) {
                    GameMessageS2CPacket packet = (GameMessageS2CPacket) event.getPacket();
                    if (packet.getMessage().getString().contains("")) {
                        ChatUtils.printChat("Debug" + packet.getMessage().toString());
                    }
                }

            }
        }
        super.onEvent(e);
    }

}
