package rafacasari.cobbledex.fabric;

import rafacasari.cobbledex.CobbledexMod;
import net.fabricmc.api.ModInitializer;

public class CobbledexFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CobbledexMod.INSTANCE.init();
    }
}