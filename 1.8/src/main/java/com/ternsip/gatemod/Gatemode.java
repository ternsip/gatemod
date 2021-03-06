package com.ternsip.gatemod;


import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockSand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by TrnMain on 02.04.2016.
 */

@Mod(   modid = Gatemode.MODID,
        name = Gatemode.MODNAME,
        version = Gatemode.VERSION,
        acceptableRemoteVersions = "*")
public class Gatemode {

    public static final String MODID = "gatemod";
    public static final String MODNAME = "GateMod";
    public static final String VERSION = "2.3";
    public static final String AUTHOR = "Ternsip";
    public static final String MCVERSION = "1.8.*";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {

        World world = event.entity.worldObj;
        int x = event.pos.getX();
        int y = event.pos.getY();
        int z = event.pos.getZ();
        BlockPos pos = event.pos;

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK &&
                world.getBlockState(pos).getBlock().getUnlocalizedName().toLowerCase().contains("button")) {
            TileEntitySign gateSRC = findGate(world, x, y + 1, z, 0, 0, 0);
            if (gateSRC != null) {
                String[] src = gateSRC.signText[1].getUnformattedTextForChat().split("\\s");
                if (src.length == 3) {
                    try {
                        int srcX = Integer.valueOf(src[0]);
                        int srcY = Integer.valueOf(src[1]);
                        int srcZ = Integer.valueOf(src[2]);
                        TileEntitySign gateDST = findGate(world, srcX, srcY, srcZ, 3, 5, 3);
                        if (gateDST != null) {
                            int meta = gateDST.getBlockMetadata();
                            double nx = gateDST.getPos().getX();
                            double ny = gateDST.getPos().getY();
                            double nz = gateDST.getPos().getZ();
                            nx += meta == 5 ? 1 : 0;
                            nx -= meta == 4 ? 1 : 0;
                            nz += meta == 3 ? 1 : 0;
                            nz -= meta == 2 ? 1 : 0;
                            world.playSoundAtEntity(event.entityPlayer, "minecraft:mob.endermen.portal", 1, 1);
                            poof(world, event.entityPlayer.posX, event.entityPlayer.posY - 0.5, event.entityPlayer.posZ);
                            event.entityPlayer.setPositionAndUpdate(nx + 0.5, ny + 0.5, nz + 0.5);
                            poof(world, nx + 0.5, ny - 0.5, nz + 0.5);
                            world.playSoundAtEntity(event.entityPlayer, "minecraft:mob.endermen.portal", 1, 1);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }

    private void poof(World world, double x, double y, double z) {
        Random random = new Random();
        for (int i = 0; i < 128; ++i) {
            double dx = random.nextDouble() * (random.nextBoolean() ? 0.5 : -0.5);
            double dy = random.nextDouble() * (random.nextBoolean() ? 1 : -1);
            double dz = random.nextDouble() * (random.nextBoolean() ? 0.5 : -0.5);
            double vx = random.nextDouble() * (random.nextBoolean() ? 0.2 : -0.2);
            double vy = random.nextDouble() * (random.nextBoolean() ? 0.2 : -0.2);
            double vz = random.nextDouble() * (random.nextBoolean() ? 0.2 : -0.2);
            world.spawnParticle((i % 2 == 0) ? EnumParticleTypes.REDSTONE :  EnumParticleTypes.PORTAL, x + dx, y + dy, z + dz, vx, vy, vz);
        }
    }

    private boolean checkEnvironment(World world, int x, int y, int z) {
        int radius = 5;
        int lapis = 0;
        int iron = 0;
        int diamond = 0;
        int gold = 0;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    Block block = world.getBlockState(new BlockPos(x + dx, y + dy, z + dz)).getBlock();
                    diamond += Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.diamond_block) ? 1 : 0;
                    iron += Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.iron_block) ? 1 : 0;
                    lapis += Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.lapis_block) ? 1 : 0;
                    gold += Block.getIdFromBlock(block) == Block.getIdFromBlock(Blocks.gold_block) ? 1 : 0;
                }
            }
        }
        return (lapis >= 13 && iron >= 3 && diamond >= 1 && gold >= 1);
    }

    private TileEntitySign findGate(World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
        for (int dx = -radiusX; dx <= radiusX; ++dx) {
            for (int dy = -radiusY; dy <= radiusY; ++dy) {
                for (int dz = -radiusZ; dz <= radiusZ; ++dz) {
                    int nx = x + dx;
                    int ny = y + dy;
                    int nz = z + dz;
                    TileEntity tile = world.getTileEntity(new BlockPos(nx, ny, nz));
                    if (tile instanceof TileEntitySign) {
                        TileEntitySign sign = ((TileEntitySign) tile);
                        if (sign.signText[0].getUnformattedTextForChat().equalsIgnoreCase("GATE") && checkEnvironment(world, nx, ny, nz)){
                            return sign;
                        }
                    }
                }
            }
        }
        return null;
    }

}
