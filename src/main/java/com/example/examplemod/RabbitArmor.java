package com.example.examplemod;

import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class RabbitArmor {

	public static final Map<ArmorType, Integer> RABBIT_ARMOR_MATERIAL = Map.of(
			ArmorType.HELMET, 1,
			ArmorType.BOOTS, 1,
			ArmorType.CHESTPLATE, 1,
			ArmorType.LEGGINGS, 1);

	public static final int durability = 256;
	public static final int enchantmentValue = 1;
	public static final Holder<SoundEvent> equipSound = null;
	public static final float toughness = 1;
	public static final float knockbackResistance = -2;
	public static final TagKey<Item> REPAIR_INGREDIENT = TagKey.create(Registries.ITEM,
			BuiltInRegistries.ITEM.getKey(Items.RABBIT_FOOT));
	public static final ResourceKey<EquipmentAsset> assetId = EquipmentAssets.createId("rabbit");

	public static DeferredItem<Item> createBoots(DeferredRegister.Items ITEMS) {
		return ITEMS.registerItem("boots_of_rabbit",
				RabbitBoots::new,
				p -> p.humanoidArmor(new ArmorMaterial(
						RabbitArmor.durability,
						RabbitArmor.RABBIT_ARMOR_MATERIAL,
						RabbitArmor.enchantmentValue,
						RabbitArmor.equipSound,
						RabbitArmor.toughness,
						RabbitArmor.knockbackResistance,
						RabbitArmor.REPAIR_INGREDIENT,
						RabbitArmor.assetId), ArmorType.BOOTS)
						.food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
	}
	// ArmorMaterial(int durability, Map<ArmorType, Integer> defense, int
	// enchantmentValue, Holder<SoundEvent> equipSound, float toughness, float
	// knockbackResistance,
	// TagKey<Item> repairIngredient, ResourceKey<EquipmentAsset> assetId) {

}
