package com.fireball1725.devworld;

import com.fireball1725.devworld.proxy.ClientProxy;
import com.fireball1725.devworld.proxy.IProxy;
import com.fireball1725.devworld.proxy.ServerProxy;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;

@Mod( ModInfo.MOD_ID )
public class DevWorld {
    public static DevWorld instance;
    public static IProxy proxy;

    public DevWorld() {
        instance = this;
        proxy = DistExecutor.runForDist( ()->ClientProxy::new, ()->ServerProxy::new );

        FMLModLoadingContext.get().getModEventBus().addListener( this::setup );
    }

    public void setup( FMLCommonSetupEvent event) {
        proxy.registerEvents();
    }
}
