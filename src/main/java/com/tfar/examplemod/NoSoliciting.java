package com.tfar.examplemod;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NoSoliciting.MODID)
public class NoSoliciting
{
  // Directly reference a log4j logger.

  public static final String MODID = "nosoliciting";

  private static final Logger LOGGER = LogManager.getLogger();

  private static final Pattern pattern = Pattern.compile("(?i)no soliciting",Pattern.CASE_INSENSITIVE);

  public NoSoliciting() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the doClientStuff method for modloading
    // Register ourselves for server and other game events we are interested in
    MinecraftForge.EVENT_BUS.register(this);
  }

  private void setup(final FMLCommonSetupEvent event)
  {
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(LivingSpawnEvent.SpecialSpawn event) {
    LivingEntity entity = event.getEntityLiving();
    if (!(entity instanceof WanderingTraderEntity) || event.getSpawnReason() != SpawnReason.NATURAL)return;
    WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)entity;
    World world = entity.world;
    BlockPos eventPos = wanderingTraderEntity.getPosition();
    BlockPos startPos = eventPos.add(-32,-32,-32);
    BlockPos endPos = eventPos.add(32,32,32);
    event.setCanceled(BlockPos.getAllInBox(startPos,endPos)
            .filter(pos -> world.getTileEntity(pos) instanceof SignTileEntity)
            .anyMatch(pos -> {
              SignTileEntity tileEntity = (SignTileEntity)world.getTileEntity(pos);
              return Arrays
                      .stream(tileEntity.signText)
                      .map(ITextComponent::getString)
                      .map(pattern::matcher)
                      .anyMatch(Matcher::matches);
            }));
  }
}
