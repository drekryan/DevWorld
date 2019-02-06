package com.fireball1725.devworld.common.events;

import java.io.File;
import java.nio.file.Paths;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.fireball1725.devworld.ModInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.gui.GuiButtonClickConsumer;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber( modid = ModInfo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GuiEvents {
    private Minecraft mc = Minecraft.getInstance();
    private String worldName = "Development World";
    private String worldSaveName = "DevWorld";
    private static final String[] disallowedFilenames = new String[] {"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    @SubscribeEvent
    public void onScreenInitPost( GuiScreenEvent.InitGuiEvent.Post event )
    {
        if ( event.getGui() instanceof GuiMainMenu )
        {
            int buttonY = event.getGui().height / 4 + 48;
            GuiButton singlePlayerButton = event.getButtonList().get( 0 );
            event.removeButton( singlePlayerButton ); // Remove the single player world button...

            event.addButton( new GuiButtonClickConsumer( 1725, event.getGui().width / 2 + 2, buttonY, 98, 20, I18n.format( "New Dev World", new Object[0] ), this::createDevWorld ) );
            event.addButton( new GuiButtonClickConsumer(1, event.getGui().width / 2 - 100, buttonY, 98, 20, I18n.format( "menu.singleplayer", new Object[0] ), singlePlayerButton::onClick ) );
        }
    }

    private void createDevWorld( double v, double v1 )
    {
        this.mc.displayGuiScreen( null );

        long i = ( new Random() ).nextLong();
        String s = "DevWorld"; // World Seed

        if ( !StringUtils.isEmpty( s ) )
        {
            try
            {
                long j = Long.parseLong( s );

                if ( j != 0L )
                {
                    i = j;
                }
            }
            catch ( NumberFormatException var7 )
            {
                i = ( long ) s.hashCode();
            }
        }

        GameType gameType = GameType.CREATIVE;
        WorldSettings worldsettings = new WorldSettings( i, gameType, true, false, WorldType.FLAT );

        func_146314_g();

        this.launchIntegratedServer( this.worldSaveName, this.worldName.trim(), worldsettings );
    }

    private void func_146314_g()
    {
        this.worldSaveName = this.worldName.trim();

        for (char c0 : SharedConstants.ILLEGAL_FILE_CHARACTERS)
        {
            this.worldSaveName = this.worldSaveName.replace(c0, '_');
        }

        if (StringUtils.isEmpty(this.worldSaveName))
        {
            this.worldSaveName = "World";
        }

        this.worldSaveName = func_146317_a(this.mc.getSaveLoader(), this.worldSaveName);
    }

    public static String func_146317_a(ISaveFormat p_146317_0_, String p_146317_1_)
    {
        p_146317_1_ = p_146317_1_.replaceAll("[\\./\"]", "_");

        for (String s : disallowedFilenames)
        {
            if (p_146317_1_.equalsIgnoreCase(s))
            {
                p_146317_1_ = "_" + p_146317_1_ + "_";
            }
        }

        while (p_146317_0_.getWorldInfo(p_146317_1_) != null)
        {
            p_146317_1_ = p_146317_1_ + "-";
        }

        return p_146317_1_;
    }

    private ISaveFormat saveLoader;

    public void launchIntegratedServer(String folderName, String worldSaveName, WorldSettings settings) {
        this.saveLoader = new AnvilSaveConverter( Paths.get( mc.gameDir + File.separator + "saves" ), Paths.get( mc.gameDir + File.separator + "backups" ), mc.getDataFixer() );

        mc.loadWorld( null );
        System.gc();
        ISaveHandler isavehandler = saveLoader.getSaveLoader( folderName, mc.getIntegratedServer());

        NBTTagCompound worldData = new NBTTagCompound();
        worldData.setString("generatorName", "flat");

        NBTTagCompound generatorOptions = presetStringToGeneratorCompound( "minecraft:bedrock,3*minecraft:stone,52*minecraft:sandstone;minecraft:desert;" );
        worldData.setTag("generatorOptions", generatorOptions);
        worldData.setInt("generatorVersion", 0);

        worldData.setInt("GameType", 1);

        worldData.setBoolean("MapFeatures", true);

        worldData.setInt("SpawnX", 0);
        worldData.setInt("SpawnY", 57);
        worldData.setInt("SpawnZ", 0);

        worldData.setLong("Time", 6000);
        worldData.setLong("DayTime", 6000);

        worldData.setBoolean("initialized", true);

        worldData.setBoolean("allowCommands", true);

        NBTTagCompound worldRules = new NBTTagCompound();
        worldRules.setString("doMobSpawning", "false");
        worldRules.setString("doDaylightCycle", "false");
        worldRules.setString("doFireTick", "false");

        worldData.setTag("GameRules", worldRules);

        WorldInfo worldInfo = new WorldInfo(worldData, mc.getDataFixer(), 1519, null);

        isavehandler.saveWorldInfo(worldInfo);

        if (settings == null)
            settings = new WorldSettings(worldInfo);

        Minecraft.getInstance().launchIntegratedServer( folderName, worldSaveName, settings );
    }

    private NBTTagCompound presetStringToGeneratorCompound(String presetString) {
        String layerString = presetString.split( ";" )[0];
        String biomeID = presetString.split( ";" )[1];

        NBTTagList layersList = new NBTTagList();
        NBTTagCompound genOptions = new NBTTagCompound();

        genOptions.setTag( "structures", new NBTTagCompound() ); //TODO: Support structures
        genOptions.setString( "biome", biomeID );

        String[] layers = layerString.split( "," );
        for (String layer : layers)
        {
            String block;
            int height;

            if (layer.contains( "*" )) {
                block = layer.split( "\\*" )[1];
                height = Integer.parseInt(layer.split( "\\*" )[0]);
            } else {
                block = layer;
                height = 1;
            }

            NBTTagCompound layerNBT = new NBTTagCompound();
            layerNBT.setString( "block", block );
            layerNBT.setByte( "height", ( ( byte ) height ) );
            layersList.add( layerNBT );
        }

        genOptions.setTag( "layers", layersList );
        return genOptions;
    }
}
