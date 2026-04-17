package com.rafacasari.mod.cobbledex.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rafacasari.mod.cobbledex.items.CobbledexItem;
import com.rafacasari.mod.cobbledex.utils.MiscUtils;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow
    public abstract ItemModelShaper getItemModelShaper();

    @Shadow
    public abstract void render(ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model);

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void cobbledex$determineCobbledexModel(ItemStack stack, ItemDisplayContext renderMode, boolean leftHanded, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        boolean shouldBe2d = renderMode == ItemDisplayContext.GUI || renderMode == ItemDisplayContext.FIXED;
        if (shouldBe2d && stack.getItem() instanceof CobbledexItem) {
            BakedModel replacementModel = this.getItemModelShaper().getModelManager().getModel(
                ModelResourceLocation.inventory(MiscUtils.INSTANCE.cobbledexResource("cobbledex_item_2d"))
            );
            BakedModel missingModel = this.getItemModelShaper().getModelManager().getMissingModel();
            if (!model.equals(replacementModel) && !replacementModel.equals(missingModel)) {
                ci.cancel();
                render(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, replacementModel);
            }
        }
    }
}
