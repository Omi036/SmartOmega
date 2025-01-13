package com.omible.smartomega;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber(modid = SmartOmega.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.ConfigValue<String> TABLIST_HEADER = BUILDER
            .comment("Message on the top of the TabList, you can use minecraft format")
            .define("tablist_header", "Default Header");

    public static final ForgeConfigSpec.ConfigValue<String> TABLIST_FOOTER = BUILDER
            .comment("Message on the bottom of the TabList")
            .define("tablist_footer", "Default Footer");



    public static final ForgeConfigSpec.ConfigValue<Boolean> DEOP_ONLEAVE = BUILDER
            .comment("Whether to disable op on player leave")
            .define("deop_onleave", true);

    public static final ForgeConfigSpec.ConfigValue<String> OP_PASSWORD = BUILDER
            .comment("Op password for oop command")
            .define("op_password", "changeme");

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> OP_IPS = BUILDER
            .comment("List of allowed IPS to access oop command, leave empty to disable")
            .defineListAllowEmpty("op_allowed_ips", List.of("127.0.0.1"), Config::validateIp);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> OP_NAMES = BUILDER
            .comment("List of allowed names to access oop command, leave empty to disable")
            .defineListAllowEmpty("op_allowed_names", List.of(), Config::validateName);

    static final ForgeConfigSpec SPEC = BUILDER.build();


    public static String tablistHeader;
    public static String tablistFooter;
    public static Boolean deopOnLeave;
    public static String opPassword;
    public static Set<String> allowedIps;
    public static Set<String> allowedNames;


    private static boolean validateIp(final Object obj){
        return obj instanceof final String ip && ip.matches("^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$");
    }

    private static boolean validateName(final Object obj){
        return obj instanceof String;
    }


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        tablistHeader = TABLIST_HEADER.get();
        tablistFooter = TABLIST_FOOTER.get();
        deopOnLeave = DEOP_ONLEAVE.get();
        opPassword = OP_PASSWORD.get();
        allowedIps = new HashSet<>(OP_IPS.get());
        allowedNames = new HashSet<>(OP_NAMES.get());
    }
}
