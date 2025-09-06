package net.vildulv.minecraft.justspace.client.render.entity;

import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.vildulv.minecraft.justspace.mob.entity.SpaceZombieEntity;

public class SpaceZombieRenderer extends AbstractZombieRenderer<SpaceZombieEntity, ZombieModel<SpaceZombieEntity> > {
    public SpaceZombieRenderer(net.minecraft.client.renderer.entity.EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public SpaceZombieRenderer(EntityRendererProvider.Context context, ModelLayerLocation zombieLayer, ModelLayerLocation innerArmor, ModelLayerLocation outerArmor) {
        super(context, new ZombieModel(context.bakeLayer(zombieLayer)), new ZombieModel(context.bakeLayer(innerArmor)), new ZombieModel(context.bakeLayer(outerArmor)));
    }

    @Override
    public ResourceLocation getTextureLocation(SpaceZombieEntity entity) {
        return ResourceLocation.fromNamespaceAndPath("justspace", "textures/entity/space_zombie.png");
    }
}
