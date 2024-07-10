package com.rafacasari.mod.cobbledex.mixins;

import com.rafacasari.mod.cobbledex.items.CobbledexItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.rafacasari.mod.cobbledex.utils.MiscUtils;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Shadow @Final private ItemModels models;
    @Shadow public abstract void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void cobbledex$bakeCobbledexItem(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        if (stack.getItem() instanceof CobbledexItem) {
            Identifier identifier = MiscUtils.INSTANCE.cobbledexResource("cobbledex_model");
            BakedModel model = this.models.getModelManager().getModel(new ModelIdentifier(identifier, "inventory"));
            ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld) world : null;
            BakedModel overriddenModel = model.getOverrides().apply(model, stack, clientWorld, entity, seed);
            cir.setReturnValue(overriddenModel == null ? this.models.getModelManager().getMissingModel() : overriddenModel);
        }
    }


    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cobbledex$determineCobbledexModel(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        boolean shouldBe2d = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.FIXED;
        if (shouldBe2d && stack.getItem() instanceof CobbledexItem) {
            BakedModel replacementModel = this.models.getModelManager().getModel(new ModelIdentifier(MiscUtils.INSTANCE.cobbledexResource("cobbledex_item"), "inventory"));
            if (!model.equals(replacementModel)) {
                ci.cancel();
                renderItem(stack, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, replacementModel);
            }
        }
    }

}