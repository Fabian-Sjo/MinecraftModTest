package com.example.examplemod;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EntitySpawnPointBinderItem extends Item {

	public EntitySpawnPointBinderItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {

		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResult interactLivingEntity(
			ItemStack stack,
			Player player,
			LivingEntity target,
			InteractionHand hand) {
		if (!player.level().isClientSide()) {
			// YOUR METHOD CALL HERE
			onEntityClicked(player, target);
		}

		return InteractionResult.SUCCESS;
	}

	private void onEntityClicked(Player player, LivingEntity entity) {
		String data = player.getData(ExampleMod.SPAWN_ENTITY.get());
		player.setData(ExampleMod.SPAWN_ENTITY.get(), entity.getUUID().toString());
		data = player.getData(ExampleMod.SPAWN_ENTITY.get());

	}
}
