package com.omible.smartomega;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Mod.EventBusSubscriber(modid = SmartOmega.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // TABLIST
    private static final ForgeConfigSpec.Builder TablistHeader = BUILDER.push("TabList");

    public static final ForgeConfigSpec.ConfigValue<String> TABLIST_HEADER = BUILDER
            .comment("Message on the top of the TabList, you can use minecraft format")
            .define("tablist_header", "Default Header");

    public static final ForgeConfigSpec.ConfigValue<String> TABLIST_FOOTER = BUILDER
            .comment("Message on the bottom of the TabList")
            .define("tablist_footer", "Default Footer");

    private static final ForgeConfigSpec.Builder TablistEnd = BUILDER.pop();
    //  TABLIST END

    //  T0 RECON START
    private static final ForgeConfigSpec.Builder T0SecurityHeader = BUILDER.push("Security T0 - AlreadyOnline");

    public static final ForgeConfigSpec.ConfigValue<Boolean> ALREADY_LOGON_SEC = BUILDER
            .comment("If enabled: Cancels logins if the account is already playing")
            .define("already_login_security", true);

    private static final ForgeConfigSpec.Builder T0SecurityEnd = BUILDER.pop();
    //  T0 RECON END


    // T2 OP START
    private static final ForgeConfigSpec.Builder OPSecurityHeader = BUILDER.push("Security T2 - OP");

    public static final ForgeConfigSpec.ConfigValue<Boolean> DEOP_ONJOIN = BUILDER
            .comment("Whether to disable op on player join")
            .define("deop_onjoin", true);

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


    private static final ForgeConfigSpec.Builder OPSecurityEnd = BUILDER.pop();
    // T2 OP END

    // MISC HEADER
    private static final ForgeConfigSpec.Builder MiscHeader = BUILDER.push("Miscellaneous");

    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_REGIONS = BUILDER
            .comment("If enabled, allows protective regions")
            .define("regions_enabled", true);

    public static final ForgeConfigSpec.ConfigValue<Boolean> TAG_NAMES = BUILDER
            .comment("If enabled, shows admin/member tags on chat")
            .define("tag_names", true);

    public static final ForgeConfigSpec.ConfigValue<String> WELCOME_MSG = BUILDER
            .comment("Shows to a player when joined")
            .define("welcome_message", "");

    private static final ForgeConfigSpec.Builder MiscEnd = BUILDER.pop();
    // MISC END

    static final ForgeConfigSpec SPEC = BUILDER.build();


    public static String tablistHeader;
    public static String tablistFooter;
    public static Boolean alreadyLoginCancel;
    public static Boolean deopOnJoin;
    public static Boolean deopOnLeave;
    public static String opPassword;
    public static Set<String> allowedIps;
    public static Set<String> allowedNames;
    public static Boolean tagNames;
    public static String welcomeMessage;
    public static Boolean regionsEnabled;



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
        alreadyLoginCancel = ALREADY_LOGON_SEC.get();
        deopOnJoin = DEOP_ONJOIN.get();
        deopOnLeave = DEOP_ONLEAVE.get();
        opPassword = OP_PASSWORD.get();
        allowedIps = new HashSet<>(OP_IPS.get());
        allowedNames = new HashSet<>(OP_NAMES.get());
        tagNames = TAG_NAMES.get();
        welcomeMessage = WELCOME_MSG.get();
        regionsEnabled = ALLOW_REGIONS.get();
    }
}
