package client.features.module.combat;

import client.event.Event;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.NumberSetting;
import client.utils.PlayerHelper;
import client.utils.TimeHelper;
import net.minecraft.block.AirBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.apache.commons.lang3.RandomUtils;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class AutoClicker extends Module {
//    private final BooleanSetting ignoreFriendsSetting = registerSetting(BooleanSetting.builder()
    //          .name("Ignore Friends")
    //          .value(true)
    //        .build()
    // );





    private final TimeHelper timer = new TimeHelper();
    private final TimeHelper rightStopWatch = new TimeHelper();

    private boolean attacked;
    private boolean clicked;
    private int breakTick;
    public static double cps;

    BooleanSetting leftClickSetting;

    BooleanSetting ignoreTeamsSetting;
    NumberSetting leftCpsSetting;
    NumberSetting rightCpsSetting;
    BooleanSetting rightClickSetting;
    NumberSetting minCPS;
    NumberSetting maxCPS;
    public AutoClicker() {
        super("Auto Clicker", 0, Category.COMBAT);



    }
    @Override
    public void init() {
        super.init();
        this.leftClickSetting = new BooleanSetting("LeftClick", true);
        this.ignoreTeamsSetting = new BooleanSetting("IgnoreTeams", true);
        this.rightCpsSetting = new NumberSetting("RightCPS", 7, 0, 20, 1f);
        this.maxCPS = new NumberSetting("MaxCPS", 7, 2, 20, 1f);
        minCPS = new NumberSetting("MinCPS", 6, 1, 19, 1f);
        this.rightClickSetting = new BooleanSetting("RightClick", true);

        addSetting(rightCpsSetting, ignoreTeamsSetting, rightClickSetting,  leftClickSetting, maxCPS,minCPS);
    }

    @Override
    public void onDisable() {
        attacked = false;
        clicked = false;
        breakTick = 0;
    }

    @Override
    public void onEvent(Event<?> e) {
        if(e instanceof EventUpdate) {
            if (mc.options.keyAttack.isPressed() && shouldClick(true)) {
                doLeftClick();
            }

        }
    }


    private void doLeftClick() {
        if (timer.hasReached(calculateTime(minCPS.getValue(), maxCPS.getValue()))) {
            timer.reset();
            legitAttack();
        }
    }
    public void legitAttack(){
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.player.swingHand(Hand.MAIN_HAND);
        if (mc.crosshairTarget == null || mc.player.isRiding() || mc.crosshairTarget.getType() == null) {
            return;
        }

      if(mc.crosshairTarget.getType() == HitResult.Type.ENTITY)
      {
          mc.interactionManager.attackEntity(mc.player,mc.targetedEntity);
      }
    }

    private void doRightClick() {
        int cps = (int) rightCpsSetting.getValue();
        if (clicked && mc.player.age % RandomUtils.nextInt(1, 3) == 0) {
            clicked = false;
            return;
        }


        clicked = true;
    }

    private double calculateTime(double mincps, double maxcps) {
        if (mincps > maxcps)
            mincps = maxcps;
        cps = (client.utils.RandomUtils.nextInt((int) mincps, (int) maxcps) + client.utils.RandomUtils.nextInt(-3,3));
        if (cps > maxcps)
            cps = (int)maxcps;

        return   ((Math.random() * (1000 / (cps - 2) - 1000 / cps + 1)) + 1000 / cps);
    }

    public boolean shouldClick(boolean left) {
        if (!mc.isWindowFocused()) {
            return false;
        }

        if (mc.player.isUsingItem()) {
            return false;
        }

        if (mc.crosshairTarget != null && left) {
            if (mc.crosshairTarget != null && mc.crosshairTarget.getType() == net.minecraft.util.hit.HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult)mc.crosshairTarget;
                BlockPos blockPos = blockHitResult.getBlockPos();
                Block block = mc.world.getBlockState(blockPos).getBlock();
                if (block instanceof AirBlock ) {
                    return true;
                }

                if (mc.options.keyAttack.isPressed()) {
                    if (breakTick > 1) {
                        return false;
                    }
                    breakTick++;
                } else {
                    breakTick = 0;
                }
            } else {
                breakTick = 0;

            }
        }
        return true;
    }
}
