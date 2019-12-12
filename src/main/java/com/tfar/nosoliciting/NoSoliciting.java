package com.tfar.nosoliciting;

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

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NoSoliciting.MODID)
public class NoSoliciting {
  public static final String MODID = "nosoliciting";

  private static final Pattern pattern = Pattern.compile("(?i)no soliciting");

  public NoSoliciting() {
    MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(LivingSpawnEvent.SpecialSpawn event) {
    LivingEntity entity = event.getEntityLiving();
    if (!(entity instanceof WanderingTraderEntity) || event.getSpawnReason() != SpawnReason.NATURAL) return;
    World world = entity.world;
    BlockPos eventPos = entity.getPosition();
    BlockPos startPos = eventPos.add(-32, -32, -32);
    BlockPos endPos = eventPos.add(32, 32, 32);
    event.setCanceled(
            BlockPos.getAllInBox(startPos, endPos)
            .filter(pos -> world.getTileEntity(pos) instanceof SignTileEntity)
            .anyMatch(pos -> {
              SignTileEntity sign = (SignTileEntity) world.getTileEntity(pos);
              return Arrays
                      .stream(sign.signText)
                      .map(ITextComponent::getString)
                      .map(pattern::matcher)
                      .anyMatch(Matcher::matches);
            })
    );
  }
}
