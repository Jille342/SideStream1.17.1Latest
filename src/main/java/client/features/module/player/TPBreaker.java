package client.features.module.player;

import client.event.Event;
import client.event.listeners.EventMotion;
import client.event.listeners.EventUpdate;
import client.features.module.Module;
import client.setting.BooleanSetting;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.dispenser.BlockPlacementDispenserBehavior;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

/**
 * Created by cool1 on 1/19/2017.
 */
public class TPBreaker extends Module {

    public static BlockPos blockBreaking;
    private double xPos, yPos, zPos;

    ModeSetting mode;
    NumberSetting radius1;
    float[] rotations;
    BooleanSetting throughWalls;
    public TPBreaker() {
        super("TPBreaker", 0, Category.PLAYER);
    }

    @Override
    public void onDisable(){
        blockBreaking = null;
        super.onDisable();
    }

    public void init() {
        mode = new ModeSetting("Mode", "RightClick", new String[]{"RightClick"});
        this.radius1 = new NumberSetting("Radius", 5, 1, 10, 1f);
        addSetting(mode, radius1);
        super.init();

    }
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            setTag(mode.getMode());
            EventUpdate em = (EventUpdate) event;
            if(mode.getMode().equals("RightClick")  && em.isPre()){
                int radius = (int) radius1.getValue();
                for(int x = -radius; x < radius; x++){
                    for(int y = radius; y > -radius; y--){
                        for(int z = -radius; z < radius; z++){
                            this.xPos = mc.player.getX() + x;
                            this.yPos = mc.player.getY() + y;
                            this.zPos = mc.player.getZ() + z;
                            rotations = getBlockRotations(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);
                            BlockPos blockPos = new BlockPos(this.xPos, this.yPos, this.zPos);
                            Block block = mc.world.getBlockState(blockPos).getBlock();
                            if(block == Blocks.NETHER_QUARTZ_ORE){
                                mc.player.swingHand(Hand.MAIN_HAND);
                            placeBlock(blockPos);
                                blockBreaking = blockPos;

                            }
                        }
                    }
                }
                blockBreaking = null;
            }

        }
        if (event instanceof EventMotion) {
            EventMotion emm = (EventMotion) event;

            emm.setYaw(rotations[0]);
            emm.setPitch(rotations[1]);
        }
    }



    public float[] getBlockRotations(double x, double y, double z) {
        double var4 = x - mc.player.getX() + 0.5;
        double var5 = z - mc.player.getZ() + 0.5;
        double var6 = y - (mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()) - 1.0);
        double var7 = MathHelper.sqrt((float) (var4 * var4 + var5 * var5));
        float var8 = (float) (Math.atan2(var5, var4) * 180.0 / Math.PI) - 90.0f;
        return new float[]{var8, (float) (-(Math.atan2(var6, var7) * 180.0 / Math.PI))};
    }
    private void placeBlock(BlockPos pos)
    {

        for(Direction side : Direction.values())
        {
            BlockPos neighbor = pos.offset(side);
            Direction side2 = side.getOpposite();

            Vec3d hitVec = Vec3d.ofCenter(neighbor)
                    .add(Vec3d.of(side2.getVector()).multiply(0.5));



            BlockHitResult hitResult = new BlockHitResult(hitVec, side2, neighbor, false);
           mc.interactionManager.interactBlock(mc.player, mc.world,Hand.MAIN_HAND, hitResult);
            mc.interactionManager.interactItem(mc.player,mc.world,Hand.MAIN_HAND);

        }

    }
}