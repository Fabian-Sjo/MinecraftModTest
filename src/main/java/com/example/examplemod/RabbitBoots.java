package com.example.examplemod;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent.LivingJumpEvent;

@EventBusSubscriber(modid = ExampleMod.MODID)
public class RabbitBoots extends Item {

	public RabbitBoots(Properties properties) {
		super(properties);
	}

	@SubscribeEvent
	public static void onJump(LivingJumpEvent event) {
		ItemStack boots = event.getEntity().getItemBySlot(EquipmentSlot.FEET);
		if (boots.getItem() instanceof RabbitBoots) {
			event.getEntity().addDeltaMovement(new Vec3(0, 10, 0));
		}
	}
}
