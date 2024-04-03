package client.features.module.combat;


import client.event.Event;
import client.event.listeners.EventTick;
import client.features.module.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.RandomUtils;
import client.utils.TimeHelper;
import net.minecraft.client.Keyboard;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.security.Key;

public class WTap extends Module {
    public boolean clicked;

    public boolean isWtap;

    public TimeHelper timer = new TimeHelper();

    public TimeHelper clickTimer = new TimeHelper();
    NumberSetting delay;
    ModeSetting mode;


    public WTap() {
        super("WTap",0, Category.COMBAT);
    }

    public void init() {
        super.init();
        delay = new NumberSetting("Delay", 200,0, 500,1);
        mode = new ModeSetting("Mode", "RayTrace", new String[] { "RayTrace"});
    }
    public void onEvent(Event<?> e) {
        if(e instanceof EventTick){

         if (mode.getMode().equalsIgnoreCase("RayTrace") && mc.targetedEntity != null && mc.targetedEntity instanceof net.minecraft.entity.player.PlayerEntity) {
                wtap();
            }
        }
    }

    public void wtap() {
        if (mc.player.forwardSpeed > 0.0F && !this.isWtap && this.timer.hasReached(50.0D))
            (new Thread(() -> {
                InputUtil.Key keyCode = mc.options.keyForward.getDefaultKey();
                try {
                    this.isWtap = true;
                    KeyBinding.setKeyPressed(keyCode, false);
                    KeyBinding.onKeyPressed(keyCode);
                    Thread.sleep(RandomUtils.nextInt(51, 75));
                    KeyBinding.setKeyPressed(keyCode, true);
                    KeyBinding.onKeyPressed(keyCode);
                    Thread.sleep(RandomUtils.nextInt(51, 75));
                    KeyBinding.setKeyPressed(keyCode, false);
                    KeyBinding.onKeyPressed(keyCode);
                    Thread.sleep(RandomUtils.nextInt(51, 75));
                    KeyBinding.setKeyPressed(keyCode, mc.options.keyForward.isPressed());
                    KeyBinding.onKeyPressed(keyCode);
                    Thread.sleep((long)delay.getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    this.isWtap = false;
                    this.timer.reset();
                }
            })).start();
    }

}
