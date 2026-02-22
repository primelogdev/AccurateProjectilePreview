package fr.madu59.ptp;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
// import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
// import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;

public class ProjectileInfo {

    public final double gravity;
    public final double drag;
    public final Vec3 initialVelocity;
    public final Vec3 offset;
    public final Vec3 position;
    public final boolean hasWaterCollision;
    public final double waterDrag;
    public final double underwaterGravity;
    public final List<Integer> order;
    public final boolean bypassAntiCheat;
    public final double inaccuracy;

    private static final List<Integer> ORDER_MDG = List.of(0, 1, 2);
    private static final List<Integer> ORDER_GMD = List.of(2, 0, 1);
    private static final List<Integer> ORDER_GDM = List.of(2, 1, 0);

    public ProjectileInfo(
            double gravity,
            double drag,
            Vec3 initialVelocity,
            Vec3 offset,
            Vec3 position,
            boolean hasWaterCollision,
            double waterDrag,
            double underwaterGravity,
            List<Integer> order,
            boolean bypassAntiCheat,
            double inaccuracy
    ) {
        this.gravity = gravity;
        this.drag = drag;
        this.initialVelocity = initialVelocity;
        this.offset = offset;
        this.position = position;
        this.hasWaterCollision = hasWaterCollision;
        this.waterDrag = waterDrag;
        this.underwaterGravity = underwaterGravity;
        this.order = order;
        this.bypassAntiCheat = bypassAntiCheat;
        this.inaccuracy = inaccuracy;
    }

    /* ----------------------------------------------------
     * Projectile detection
     * ---------------------------------------------------- */

    public static List<ProjectileInfo> getItemsInfo(
            ItemStack stack,
            Player player,
            boolean isMainHand
    ) {

        List<ProjectileInfo> result = new ArrayList<>();

        if (stack == null || stack.isEmpty()) {
            return result;
        }

        float tickProgress = Minecraft.getInstance()
                .getDeltaTracker()
                .getGameTimeDeltaPartialTick(false);

        Item item = stack.getItem();

        Vec3 eyePos = player.getEyePosition(tickProgress);

        double gravity = 0.05;
        double drag = 0.99;
        double waterDrag = 0.6;

        boolean bypassAntiCheat = false;

        Vec3 baseVelocity = player.getViewVector(tickProgress);

        /* ---------------- Bow ---------------- */

        if (item instanceof BowItem) {

            int useTicks = player.getTicksUsingItem();
            float pull = BowItem.getPowerForTime(useTicks);

            if (pull >= 0.1f) {

                Vec3 vel = baseVelocity.scale(3.0);

                Vec3 offset = new Vec3(0.2, -0.06, 0.2);

                result.add(new ProjectileInfo(
                        gravity,
                        drag,
                        vel,
                        offset,
                        eyePos,
                        false,
                        waterDrag,
                        gravity,
                        ORDER_MDG,
                        false,
                        1.0
                ));
            }
        }

        /* ---------------- Crossbow ---------------- */

        else if (item instanceof CrossbowItem) {

            Vec3 vel = baseVelocity.scale(3.15);
            Vec3 offset = new Vec3(0, -0.06, 0.03);

            if (CrossbowItem.isCharged(stack)) {

                result.add(new ProjectileInfo(
                        gravity,
                        drag,
                        vel,
                        offset,
                        eyePos,
                        false,
                        waterDrag,
                        gravity,
                        ORDER_MDG,
                        false,
                        1.0
                ));

                if (hasEnchantment(stack, Enchantments.MULTISHOT)) {

                    float angle = 10f;

                    result.add(new ProjectileInfo(
                            gravity,
                            drag,
                            vel.yRot((float) Math.toRadians(angle)),
                            offset,
                            eyePos,
                            false,
                            waterDrag,
                            gravity,
                            ORDER_MDG,
                            false,
                            1.0
                    ));

                    result.add(new ProjectileInfo(
                            gravity,
                            drag,
                            vel.yRot((float) Math.toRadians(-angle)),
                            offset,
                            eyePos,
                            false,
                            waterDrag,
                            gravity,
                            ORDER_MDG,
                            false,
                            1.0
                    ));
                }
            }
        }

        /* ---------------- Throwable ---------------- */

        else if (
                item instanceof SnowballItem ||
                item instanceof EggItem ||
                item instanceof EnderpearlItem
        ) {

            bypassAntiCheat = item instanceof EnderpearlItem;

            gravity = 0.03;
            waterDrag = 0.8;

            Vec3 vel = baseVelocity.scale(SnowballItem.PROJECTILE_SHOOT_POWER);

            Vec3 offset = new Vec3(0.2, -0.06, 0.2);

            result.add(new ProjectileInfo(
                    gravity,
                    drag,
                    vel,
                    offset,
                    eyePos,
                    false,
                    waterDrag,
                    gravity,
                    ORDER_GDM,
                    bypassAntiCheat,
                    1.0
            ));
        }

        return result;
    }

    /* ----------------------------------------------------
     * Drop trajectory
     * ---------------------------------------------------- */

    public static ProjectileInfo getDropTrajectory(Player player) {

        double gravity = 0.04;
        double drag = 0.98;
        double waterDrag = 0.98 * 0.99;

        Vec3 pos = new Vec3(
                player.getX(),
                player.getEyeY() - 0.3,
                player.getZ()
        );

        Vec3 vel = player.getViewVector(1.0F).scale(0.3);

        Vec3 offset = new Vec3(0.2, -0.06, 0.2);

        return new ProjectileInfo(
                gravity,
                drag,
                vel,
                offset,
                pos,
                true,
                waterDrag,
                gravity,
                ORDER_GMD,
                true,
                0.0
        );
    }

    /* ----------------------------------------------------
     * Utils
     * ---------------------------------------------------- */

    public static boolean hasEnchantment(
            ItemStack stack,
            ResourceKey<Enchantment> enchantment
    ) {

        var registry = Minecraft.getInstance()
                .player.level()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> entry = registry.getOrThrow(enchantment);

        return EnchantmentHelper.getItemEnchantmentLevel(entry, stack) > 0;
    }
}