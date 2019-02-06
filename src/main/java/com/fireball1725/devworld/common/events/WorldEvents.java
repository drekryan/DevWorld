package com.fireball1725.devworld.common.events;

import com.fireball1725.devworld.ModInfo;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber( modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class WorldEvents {

    @SubscribeEvent
    public void worldTick(TickEvent.WorldTickEvent event) {
        if (event.world.getWorldInfo().isRaining())
            event.world.getWorldInfo().setRaining(false);
    }
}
