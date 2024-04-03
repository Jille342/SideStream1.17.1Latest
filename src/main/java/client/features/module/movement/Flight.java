

package client.features.module.movement;

import client.event.Event;
import client.event.listeners.EventTick;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Flight extends Module {

ModeSetting mode;

    final List<Packet<?>> queue = new ArrayList<>();
    int bypassTimer = 0;
    boolean flewBefore = false;
    NumberSetting speed;
    BooleanSetting bypassvanilla;


    public Flight() {
        super("Flight", 0, Category.MOVEMENT);
    }
    public void init() {
        super.init();
        speed = new NumberSetting("Speed", 2,0, 10,1);
        mode = new ModeSetting("Mode", "Vanilla", new String[] { "Vanilla","Static"});
        bypassvanilla = new BooleanSetting("Bypass Vanilla", true);
addSetting(speed,mode,bypassvanilla);
    }


    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            setTag(mode.getMode());
            if (mc.player == null || mc.world == null || mc.getNetworkHandler() == null) {
                return;
            }
            double speed = this.speed.getValue();
            if (bypassvanilla.getValue()) {
                bypassTimer++;
                if (bypassTimer > 10) {
                    bypassTimer = 0;
                    Vec3d p = mc.player.getPos();
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y - 0.2, p.z, false));
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.x, p.y + 0.2, p.z, false));
                }
            }
            switch (mode.getValue()) {
                case "Vanilla" -> {
                    mc.player.getAbilities().setFlySpeed((float) (this.speed.getValue() + 0f) / 20f);
                    mc.player.getAbilities().flying = true;
                }
                case "Static" -> {
                    GameOptions go = mc.options;
                    float y = mc.player.getYaw();
                    int mx = 0, my = 0, mz = 0;

                    if (go.keyJump.isPressed()) {
                        my++;
                    }
                    if (go.keyBack.isPressed()) {
                        mz++;
                    }
                    if (go.keyLeft.isPressed()) {
                        mx--;
                    }
                    if (go.keyRight.isPressed()) {
                        mx++;
                    }
                    if (go.keySneak.isPressed()) {
                        my--;
                    }
                    if (go.keyForward.isPressed()) {
                        mz--;
                    }
                    double ts = speed / 2;
                    double s = Math.sin(Math.toRadians(y));
                    double c = Math.cos(Math.toRadians(y));
                    double nx = ts * mz * s;
                    double nz = ts * mz * -c;
                    double ny = ts * my;
                    nx += ts * mx * -c;
                    nz += ts * mx * -s;
                    Vec3d nv3 = new Vec3d(nx, ny, nz);
                    mc.player.setVelocity(nv3);
                }
             

            }
        }
    }



    @Override
    public void onEnable() {
        super.onEnable();
        bypassTimer = 0;
        flewBefore = Objects.requireNonNull(mc.player).getAbilities().flying;
        mc.player.setOnGround(false);
        Objects.requireNonNull(mc.getNetworkHandler())
                .sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
    }



    @Override
    public void onDisable() {
        super.onDisable();
        Objects.requireNonNull(mc.player).getAbilities().flying = flewBefore;
        mc.player.getAbilities().setFlySpeed(0.05f);
        if (mc.player == null || mc.getNetworkHandler() == null) {
            queue.clear();
            return;
        }
        for (Packet<?> packet : queue.toArray(new Packet<?>[0])) {
            mc.getNetworkHandler().sendPacket(packet);
        }
        queue.clear();
    }
    

}
