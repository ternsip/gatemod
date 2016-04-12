package com.ternsip.gatemod;


import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
    public static final String MCVERSION = "1.9.*";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void playerInteract(PlayerInteractEvent event) {

        World world = event.getEntity().worldObj;
        System.out.println("OK");
        int x = event.getPos().getX();
        int y = event.getPos().getY();
        int z = event.getPos().getZ();
        BlockPos pos = event.getPos();

        if (event.getHand() == EnumHand.MAIN_HAND &&
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
                            event.getEntityPlayer().playSound(SoundEvents.entity_endermen_teleport, 1, 1);
                            poof(world, event.getEntityPlayer().posX, event.getEntityPlayer().posY - 0.5, event.getEntityPlayer().posZ);
                            event.getEntityPlayer().setPositionAndUpdate(nx + 0.5, ny + 0.5, nz + 0.5);
                            poof(world, nx + 0.5, ny - 0.5, nz + 0.5);
                            event.getEntityPlayer().playSound(SoundEvents.entity_endermen_teleport, 1, 1);
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

    private TileEntitySign findGate(World world, int x, int y, int z, int radiusX, int radiusY, int radiusZ) {
        for (int dx = -radiusX; dx <= radiusX; ++dx) {
            for (int dy = -radiusY; dy <= radiusY; ++dy) {
                for (int dz = -radiusZ; dz <= radiusZ; ++dz) {
                    TileEntity tile = world.getTileEntity(new BlockPos(x + dx, y + dy, z + dz));
                    if (tile instanceof TileEntitySign) {
                        TileEntitySign sign = ((TileEntitySign) tile);
                        if (sign.signText[0].getUnformattedTextForChat().equalsIgnoreCase("GATE")){
                            return sign;
                        }
                    }
                }
            }
        }
        return null;
    }

}
