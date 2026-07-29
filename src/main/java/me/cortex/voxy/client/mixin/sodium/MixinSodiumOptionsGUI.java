package me.cortex.voxy.client.mixin.sodium;

import me.cortex.voxy.client.config.VoxySodiumOptions;
import net.caffeinemc.mods.sodium.client.gui.options.OptionPage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI", remap = false)
public class MixinSodiumOptionsGUI {
    @Shadow @Final private List<OptionPage> pages;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void voxy$addVoxyOptionsPage(CallbackInfo ci) {
        if (this.pages != null) {
            boolean alreadyAdded = this.pages.stream().anyMatch(page -> page.getName() != null && page.getName().getString().contains("Voxy"));
            if (!alreadyAdded) {
                VoxySodiumOptions.addVoxyPage(this.pages);
            }
        }
    }
}
