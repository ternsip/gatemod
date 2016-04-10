package com.ternsip.gatemod;


import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockSand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

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
    public static final String VERSION = "2.2";
    public static final String AUTHOR = "Ternsip";
    public static final String MCVERSION = "1.7.*";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {

        World world = event.entity.worldObj;
        int x = event.x;
        int y = event.y;
        int z = event.z;

        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK &&
                world.getBlock(x, y, z).getUnlocalizedName().toLowerCase().contains("button")) {
            TileEntitySign gateSRC = findGate(world, x, y + 1, z, 0, 0, 0);
            if (gateSRC != null) {
                String[] src = gateSRC.signText[1].split("\\s");
                if (src.length == 3) {
                    try {
                        int srcX = Integer.valueOf(src[0]);
                        int srcY = Integer.valueOf(src[1]);
                        int srcZ = Integer.valueOf(src[2]);
                        TileEntitySign gateDST = findGate(world, srcX, srcY, srcZ, 3, 5, 3);
                        if (gateDST != null) {
                            int meta = gateDST.blockMetadata;
                            double nx = gateDST.xCoord;
                            double ny = gateDST.yCoord;
                            double nz = gateDST.zCoord;
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
            world.spawnParticle((i % 2 == 0) ? "reddust" : "portal", x + dx, y + dy, z + dz, vx, vy, vz);
        }
    }

    private TileEntitySign findGate(World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
        for (int dx = -radiusX; dx <= radiusX; ++dx) {
            for (int dy = -radiusY; dy <= radiusY; ++dy) {
                for (int dz = -radiusZ; dz <= radiusZ; ++dz) {
                    TileEntity tile = world.getTileEntity(x + dx, y + dy, z + dz);
                    if (tile instanceof TileEntitySign) {
                        TileEntitySign sign = ((TileEntitySign) tile);
                        if (sign.signText[0].equalsIgnoreCase("GATE")){
                            return sign;
                        }
                    }
                }
            }
        }
        return null;
    }

}
