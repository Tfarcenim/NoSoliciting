package com.tfar.nosoliciting;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NoSoliciting.MODID)
public class NoSoliciting {
  public static final String MODID = "nosoliciting";

  private static final Pattern pattern = Pattern.compile("(?i)no soliciting");

  public NoSoliciting() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_SPEC);
    MinecraftForge.EVENT_BUS.addListener(this::wanderingTraderSpawn);
  }

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void wanderingTraderSpawn(LivingSpawnEvent.SpecialSpawn event) {
    LivingEntity entity = event.getEntityLiving();
    if (!(entity instanceof WanderingTraderEntity) || event.getSpawnReason() != SpawnReason.EVENT) return;
    World world = entity.world;
    BlockPos eventPos = entity.getPosition();

    IChunk eventChunk = world.getChunk(eventPos);

    int radius = Config.range.get();

    ChunkPos chunkPos = eventChunk.getPos();

    int startX = chunkPos.x - radius;
    int startZ = chunkPos.z - radius;
    int endX = chunkPos.x + radius;
    int endZ = chunkPos.z + radius;
    ArrayList<IChunk> chunks = new ArrayList<>();

    IntStream.range(startZ, endZ)
            .forEach(z ->
                    IntStream.range(startX, endX)
                            .forEach(x -> chunks.add(world.getChunk(x, z))));


    event.setCanceled(
            chunks
                    .stream()
                    .filter(Chunk.class::isInstance)
                    .flatMap(c -> ((Chunk) c).getTileEntityMap().values().stream())
                    .filter(SignTileEntity.class::isInstance)
                    .flatMap(tileEntity -> Arrays.stream(((SignTileEntity) tileEntity).signText))
                    .map(ITextComponent::getString)
                    .map(pattern::matcher)
                    .anyMatch(Matcher::matches)
    );
  }
}
