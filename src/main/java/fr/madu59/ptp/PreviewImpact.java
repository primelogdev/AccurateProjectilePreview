package fr.madu59.ptp;

import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PreviewImpact {

    public final boolean guaranteedHit;
    public final Vec3 position;
    public final HitResult impact;
    public final Entity entityImpact;
    public final boolean hasHit;
    public final List<Vec3> trajectoryPoints;

    public PreviewImpact(Vec3 position, HitResult impact, Entity entityImpact, boolean hasHit, List<Vec3> trajectoryPoints, boolean guaranteedHit) {
        this.position = position;
        this.impact = impact;
        this.entityImpact = entityImpact;
        this.hasHit = hasHit;
        this.trajectoryPoints = trajectoryPoints;
        this.guaranteedHit = guaranteedHit;
    }
}
