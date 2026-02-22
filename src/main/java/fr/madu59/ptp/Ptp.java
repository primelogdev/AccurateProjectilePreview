package fr.madu59.ptp;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Client-side trajectory preview mod bootstrap.
 * Networking handshake removed.
 */
@Mod(Ptp.MOD_ID)
public class Ptp {

    public static final String MOD_ID = "ptp";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Ptp(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("PTP trajectory preview client mod loaded.");
    }
}
