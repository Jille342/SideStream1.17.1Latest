/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RotationUtils implements MCUtil
{

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    public static Vec3d getEyesPos()
    {
        ClientPlayerEntity player = MC.player;
        float eyeHeight = player.getEyeHeight(player.getPose());
        return player.getPos().add(0, eyeHeight, 0);
    }
    public static boolean fov(Entity entity, double fov) {
        fov = (fov * 0.5);
        double v = ((double) (mc.player.getYaw() - fovToEntity(entity)) % 360.0D + 540.0D) % 360.0D - 180.0D;
        return v > 0.0D && v < fov || -fov < v && v < 0.0D;
    }
    public static float[] getRotationsEntity(LivingEntity entity) {
        return PlayerHelper.isMoving() ? getRotations(entity.getX() + ThreadLocalRandom.current().nextDouble(-0.03D,0.03D), entity.getY() + (double) entity.getEyeHeight(entity.getPose()) - 0.4D + ThreadLocalRandom.current().nextDouble(-0.07D, 0.07D), entity.getZ() + ThreadLocalRandom.current().nextDouble(-0.03D, 0.03D)) : getRotations(entity.getX(), entity.getY() + (double) entity.getEyeHeight(entity.getPose()) - 0.4D, entity.getZ());
    }
    public static float fovToEntity(Entity ent) {
        double x = ent.getX() -mc.player.getX();
        double z = ent.getZ() - mc.player.getZ();
        double yaw = Math.atan2(x, z) * 57.2957795D;
        return (float) (yaw * -1.0D);
    }
    public static float[] fixedSensitivity(float[] rotations, float sens) {
        float f = sens * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        return new float[]{(rotations[0] - rotations[0] % gcd),
                (rotations[1] - rotations[1] % gcd)
        };
    }
    public static float[] getRotations(double posX, double posY, double posZ) {
        PlayerEntity player = mc.player;
        double x = posX - player.getX();
        double y = posY - (player.getY() + (double) player.getEyeHeight(mc.player.getPose()));
        double z = posZ - player.getZ();
        double dist = (double) MathHelper.sqrt((float) (x * x + z * z));
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0D / Math.PI));
        final float finishedYaw = player.getYaw() + MathHelper.wrapDegrees(yaw - player.getYaw());
        final float finishedPitch = player.getPitch() + MathHelper.wrapDegrees(pitch - player.getPitch());
        return new float[]{finishedYaw, finishedPitch};
    }

    public static Vec3d getClientLookVec(float partialTicks)
    {
        float yaw = MC.player.getYaw(partialTicks);
        float pitch = MC.player.getPitch(partialTicks);
        return new Rotation(yaw, pitch).toLookVec();
    }
    public static float calculateYawChangeToDst(Entity entity) {
        double diffX = entity.getX() - MC.player.getX();
        double diffZ = entity.getZ() - MC.player.getZ();
        double deg = Math.toDegrees(Math.atan(diffZ / diffX));
        if (diffZ < 0.0 && diffX < 0.0) {
            return (float) MathHelper.wrapDegrees(-(MC.player.getYaw() - (90 + deg)));
        } else if (diffZ < 0.0 && diffX > 0.0) {
            return (float) MathHelper.wrapDegrees(-(MC.player.getYaw() - (-90 + deg)));
        } else {
            return (float) MathHelper.wrapDegrees(-(MC.player.getYaw() - Math.toDegrees(-Math.atan(diffX / diffZ))));
        }
    }


    public static Rotation getNeededRotations(Vec3d vec)
    {
        Vec3d eyes = getEyesPos();

        double diffX = vec.x - eyes.x;
        double diffZ = vec.z - eyes.z;
        double yaw = Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;

        double diffY = vec.y - eyes.y;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        double pitch = -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return Rotation.wrapped((float)yaw, (float)pitch);
    }








    /**
     * Limits the change in angle between the current and intended rotation to
     * the specified maximum change. Useful for smoothing out rotations and
     * making combat hacks harder to detect.
     *
     * <p>
     * For best results, do not wrap the current angle before calling this
     * method!
     */
    public static float limitAngleChange(float current, float intended,
                                         float maxChange)
    {
        float currentWrapped = MathHelper.wrapDegrees(current);
        float intendedWrapped = MathHelper.wrapDegrees(intended);

        float change = MathHelper.wrapDegrees(intendedWrapped - currentWrapped);
        change = MathHelper.clamp(change, -maxChange, maxChange);

        return current + change;
    }

    /**
     * Removes unnecessary changes in angle caused by wrapping. Useful for
     * making combat hacks harder to detect.
     *
     * <p>
     * For example, if the current angle is 179 degrees and the intended angle
     * is -179 degrees, you only need to turn 2 degrees to face the intended
     * angle, not 358 degrees.
     *
     * <p>
     * DO NOT wrap the current angle before calling this method! You will get
     * incorrect results if you do.
     */
    public static float limitAngleChange(float current, float intended)
    {
        float currentWrapped = MathHelper.wrapDegrees(current);
        float intendedWrapped = MathHelper.wrapDegrees(intended);

        float change = MathHelper.wrapDegrees(intendedWrapped - currentWrapped);

        return current + change;
    }
}