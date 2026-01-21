package com.example.examplemod;

import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ExampleMod.MODID)
public class ExampleMod {
	// Define mod id in a common place for everything to reference
	public static final String MODID = "examplemod";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();
	// Create a Deferred Register to hold Blocks which will all be registered under
	// the "examplemod" namespace
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
	// Create a Deferred Register to hold Items which will all be registered under
	// the "examplemod" namespace
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
	// Create a Deferred Register to hold CreativeModeTabs which will all be
	// registered under the "examplemod" namespace
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister
			.create(Registries.CREATIVE_MODE_TAB, MODID);

	// Creates a new Block with the id "examplemod:example_block", combining the
	// namespace and path
	public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block",
			p -> p.mapColor(MapColor.STONE).jumpFactor(55));
	// Creates a new BlockItem with the id "examplemod:example_block", combining the
	// namespace and path
	public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block",
			EXAMPLE_BLOCK);
	public static final DeferredItem<Item> BOOTS_OF_RABBIT = RabbitArmor.createBoots(ITEMS);
	// Creates a new food item with the id "examplemod:example_id", nutrition 1 and
	// saturation 2
	// public static final DeferredItem<Item> EXAMPLE_ITEM =
	// ITEMS.registerSimpleItem("example_item",
	// p -> p.food(new FoodProperties.Builder()
	// .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
	public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerItem(
			"example_item",
			EntitySpawnPointBinderItem::new,
			props -> props.stacksTo(1));

	// Creates a creative tab with the id "examplemod:example_tab" for the example
	// item, that is placed after the combat tab
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS
			.register("example_tab", () -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.examplemod")) // The language key for the title of your
																			// CreativeModeTab
					.withTabsBefore(CreativeModeTabs.COMBAT)
					.icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
					.displayItems((parameters, output) -> {
						addItemsToTab(output);
						; // Add the example item to the tab. For your own tabs, this
							// method is preferred over the event
					}).build());

	private static void addItemsToTab(Output output) {
		output.accept(EXAMPLE_ITEM);
		output.accept(BOOTS_OF_RABBIT);
		output.accept(EXAMPLE_BLOCK_ITEM);
	}

	// Create the DeferredRegister for attachment types
	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
			.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

	// Serialization via map codec
	public static final Supplier<AttachmentType<String>> SPAWN_ENTITY = ATTACHMENT_TYPES.register(
			"entity", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING.fieldOf("entity")).build());

	// In your mod constructor, don't forget to register the DeferredRegister to
	// your mod bus:

	// The constructor for the mod class is the first code that is run when your mod
	// is loaded.
	// FML will recognize some parameter types like IEventBus or ModContainer and
	// pass them in automatically.
	public ExampleMod(IEventBus modEventBus, ModContainer modContainer) {
		// Register the commonSetup method for modloading
		modEventBus.addListener(this::commonSetup);
		ATTACHMENT_TYPES.register(modEventBus);
		// Register the Deferred Register to the mod event bus so blocks get registered
		BLOCKS.register(modEventBus);
		// Register the Deferred Register to the mod event bus so items get registered
		ITEMS.register(modEventBus);
		// Register the Deferred Register to the mod event bus so tabs get registered
		CREATIVE_MODE_TABS.register(modEventBus);

		// Register ourselves for server and other game events we are interested in.
		// Note that this is necessary if and only if we want *this* class (ExampleMod)
		// to respond directly to events.
		// Do not add this line if there are no @SubscribeEvent-annotated functions in
		// this class, like onServerStarting() below.
		NeoForge.EVENT_BUS.register(this);
		NeoForge.EVENT_BUS.addListener((PlayerRespawnPositionEvent e) -> LOGGER.info("RESPAWN EVENT FIRED"));

		// Register the item to a creative tab
		modEventBus.addListener(this::addCreative);

		// Register our mod's ModConfigSpec so that FML can create and load the config
		// file for us
		modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		// Some common setup code
		LOGGER.info("HELLO FROM COMMON SETUP");

		if (Config.LOG_DIRT_BLOCK.getAsBoolean()) {
			LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));
		}

		LOGGER.info("{}{}", Config.MAGIC_NUMBER_INTRODUCTION.get(), Config.MAGIC_NUMBER.getAsInt());

		Config.ITEM_STRINGS.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
	}

	// Add the example block item to the building blocks tab
	private void addCreative(BuildCreativeModeTabContentsEvent event) {
		// if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
		// event.accept(EXAMPLE_BLOCK_ITEM);
		// }
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		// Do something when the server starts
		LOGGER.info("HELLO from server starting");
	}

	@SubscribeEvent
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		LOGGER.info("Player joined: {}", event.getEntity().getName());
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnPositionEvent event) {
		MinecraftServer server = event.getEntity().level().getServer();
		String uuid = event.getEntity().getData(ExampleMod.SPAWN_ENTITY.get());
		Iterable<ServerLevel> targetLevel = server.getAllLevels();
		for (ServerLevel serverLevel : targetLevel) {
			Entity entity = serverLevel != null
					? serverLevel.getEntity(UUID.fromString(uuid))
					: null;
			if (entity != null) {
				event.setTeleportTransition(event.getTeleportTransition().withPosition(entity.position()));
			}
		}

		LOGGER.info("Player respawned");
	}

	@SubscribeEvent
	public void onDeath(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		LOGGER.info("ENTITY DIED {}", entity.getName());
	}
}
