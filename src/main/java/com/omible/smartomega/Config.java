package com.omible.smartomega;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("UnusedParameters")
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


    //  T1 IMPERSONATE START
    private static final ForgeConfigSpec.Builder T1SecurityHeader = BUILDER.push("Security T0 - AlreadyOnline");

    public static final ForgeConfigSpec.ConfigValue<Boolean> IP_SECURITY_ENABLED = BUILDER
            .comment("If enabled: Cancles the login attempt if the ip doesnt match the latest connection")
            .define("account_ip_mismatch", true);

    public static final ForgeConfigSpec.ConfigValue<Integer> IP_SECURITY_TIMESAFE = BUILDER
            .comment("Defines how much (in seconds) has to pass in order to another ip to match")
            .define("account_ip_timesafe", 10800);

    public static final ForgeConfigSpec.ConfigValue<String> IP_SECURITY_KICK_MESSAGE = BUILDER
            .comment("Kick message to show if IP mismatchs")
            .define("account_ip_mismatch_message", "Account IP Mismatch, contact an Administrator.");

    private static final ForgeConfigSpec.Builder T1SecurityEnd = BUILDER.pop();
    //  T1 IMPERSONATE END


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

    public static final ForgeConfigSpec.ConfigValue<Boolean> WELCOME_SOUND = BUILDER
            .comment("Plays a audio when joines")
            .define("welcome_audio", false);

    private static final ForgeConfigSpec.Builder MiscEnd = BUILDER.pop();
    // MISC END


    // DISCORD WEBHOOK START
    private static final ForgeConfigSpec.Builder WebhookHeader = BUILDER.push("Discord Webhook");

    public static final ForgeConfigSpec.ConfigValue<Boolean> ALLOW_DISCOHOOK = BUILDER
            .comment("If enabled, send discord webhook notifications")
            .define("discord_webhook_enabled", false);

    public static final ForgeConfigSpec.ConfigValue<String> DISCOHOOK_URL = BUILDER
            .comment("URL To send webhooks")
            .define("discord_webhook_url", "");

    public static final ForgeConfigSpec.ConfigValue<Boolean> DISCOHOOK_STARTUP_ENABLED = BUILDER
            .comment("Send webhooks on server startup?")
            .define("discord_webhook_startup", true);

    public static final ForgeConfigSpec.ConfigValue<Boolean> DISCOHOOK_CLIENT_ENABLED = BUILDER
            .comment("Send webhooks on player join/left?")
            .define("discord_webhook_client", true);

    public static final ForgeConfigSpec.ConfigValue<Boolean> DISCOHOOK_DEATH_ENABLED = BUILDER
            .comment("Send webhooks on player death?")
            .define("discord_webhook_death", true);

    private static final ForgeConfigSpec.Builder WebhookEnd = BUILDER.pop();
    // DISDORD WEBHOOK END

    static final ForgeConfigSpec SPEC = BUILDER.build();


    public static String tablistHeader;
    public static String tablistFooter;

    public static Boolean alreadyLoginCancel;

    public static Boolean ipSecurityEnabled;
    public static Integer ipTimeSafe;
    public static String kickMessage;

    public static Boolean deopOnJoin;
    public static Boolean deopOnLeave;
    public static String opPassword;
    public static Set<String> allowedIps;
    public static Set<String> allowedNames;

    public static Boolean tagNames;
    public static String welcomeMessage;
    public static Boolean welcomeAudio;
    public static Boolean regionsEnabled;

    public static Boolean webhooksEnabled;
    public static String webhookUrl;
    public static Boolean webhooksStartupEnabled;
    public static Boolean webhooksClientEnabled;
    public static Boolean webhooksDeathEnabled;



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

        ipSecurityEnabled = IP_SECURITY_ENABLED.get();
        ipTimeSafe = IP_SECURITY_TIMESAFE.get();
        kickMessage = IP_SECURITY_KICK_MESSAGE.get();

        deopOnJoin = DEOP_ONJOIN.get();
        deopOnLeave = DEOP_ONLEAVE.get();
        opPassword = OP_PASSWORD.get();
        allowedIps = new HashSet<>(OP_IPS.get());
        allowedNames = new HashSet<>(OP_NAMES.get());

        tagNames = TAG_NAMES.get();
        welcomeMessage = WELCOME_MSG.get();
        welcomeAudio = WELCOME_SOUND.get();
        regionsEnabled = ALLOW_REGIONS.get();

        webhooksEnabled = ALLOW_DISCOHOOK.get();
        webhookUrl = DISCOHOOK_URL.get();
        webhooksStartupEnabled = DISCOHOOK_STARTUP_ENABLED.get();
        webhooksClientEnabled = DISCOHOOK_CLIENT_ENABLED.get();
        webhooksDeathEnabled = DISCOHOOK_DEATH_ENABLED.get();
    }
}
