package client.features.module.misc;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.*;

import client.features.module.Module;
import client.setting.ModeSetting;
import client.setting.NumberSetting;
import client.utils.ChatUtils;
import client.utils.ServerHelper;
import client.utils.TimeHelper;
import client.utils.font.Fonts;
import client.event.Event;
import client.event.listeners.EventPacket;
import client.event.listeners.EventRender2D;
import client.event.listeners.EventUpdate;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.util.Window;
import net.minecraft.command.CommandSource;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import net.minecraft.util.math.BlockPos;

public class AdminChecker extends Module {
    private int lastAdmins;

    private final ArrayList<String> admins;

    private final TimeHelper timer;
    NumberSetting delay;
    private final TimeHelper timer2 = new TimeHelper();

    Window window = mc.getWindow();
    ModeSetting checkMode;


    public AdminChecker() {
        super("AdminChecker",  0, Category.MISC);
        this.admins = new ArrayList<>();
        this.timer = new TimeHelper();
    }
    public void init() {
        this.delay = new NumberSetting("Chat Delay", 1000, 1000, 5000, 1000F);
        checkMode = new ModeSetting("Check Mode ", "Rank", new String[]{"Rank", "Tell"});
        addSetting(delay, checkMode); super.init();

    }


    public void onEvent(Event<?> e) {
        if (e instanceof EventRender2D) {
            if (!this.admins.isEmpty()) {
         //       font.drawStringWithShadow("" + String.valueOf(this.admins.size()), ((new ScaledResolution(mc)).getScaledWidth() / 2 - mc.fontRenderer.getStringWidth("" + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            } else {
              //  Fonts.font.drawString("Admins: " + String.valueOf(this.admins.size()), ((double) (new window.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth("Admins: " + String.valueOf(this.admins.size())) + 20), ((new ScaledResolution(mc)).getScaledHeight() / 2 + 20), -1);
            }
        }
        if (e instanceof EventUpdate) {
            if (this.timer.hasReached(5000.0F)) {
                int completionid = 0;
                this.timer.reset();
                mc.player.networkHandler.getCommandSource().onCommandSuggestions( completionid+1,null);
                if(checkMode.getMode().equals("Rank"))
          mc.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(completionid, "/rank "));
                else if (checkMode.getMode().equals("Tell"))
                    mc.player.networkHandler.sendPacket(new RequestCommandCompletionsC2SPacket(completionid, "/tell "));
            }
            setTag(String.valueOf(admins.size()));
            if (!this.admins.isEmpty())
                displayAdmins();
        }
        if (e instanceof EventPacket) {
            if(e.isIncoming()) {
                EventPacket event = ((EventPacket) e);
                Packet<?> p = event.getPacket();
                if (p instanceof CommandSuggestionsS2CPacket packet) {
                    this.admins.clear();
                        for (Suggestion suggestion : packet.getSuggestions().getList()) {
                            for (String staff : getAdministrators()) {
                                if (suggestion.getText().equalsIgnoreCase(staff)) {
                                    this.admins.add(staff);
                                    displayAdmins();
                                }
                            }
                        }
                    }

                    this.lastAdmins = this.admins.size();
                }
            }
        }

    public void displayAdmins() {
        if (timer2.hasReached(delay.value)) {
            ChatUtils.printChat(String.valueOf("INC " + admins + " " + admins.size()));
            timer2.reset();
        }
    }

    public String[] getAdministrators() {
        return new String[] {
                "HighlifeTTU",
                "Mistri",
                "Axyy",
                "lazertester",
                "Robertthegoat",
                "97WaterPolo",
                "aet2505",
                "Galap",
                "McJeffr",
                "Mistri",
                "halowars91",
                "Hilevi",
                "Pyachi2002",
                "Rafiki2085",
                "Red_Epicness",
                "_Silver",
                "InstantLightning",
                "JACOBSMILE",
                "JonnyDvE",
                "kbsfe",
                "ru555e11",
                "Selictove",
                "wmn",
                "sellejz",
                "Agypagy",
                "BasicAly",
                "Carrots386",
                "DJ_Pedro",
                "FullAdmin",
                "ImbC",
                "JTGangsterLP6",
                "M4bi",
                "Mistri",
                "MrJack",
                "GunOverdose",
                "pigplayer",
                "Pyachi2002",
                "Outra",
                "Rinjani",
                "Sevy13",
                "SnowVi1liers",
                "naqare",
                "ACrispyTortilla",
                "Hughzaz",
                "Moshyn",
                "Navarr",
                "ShadowLAX",
                "Brxnton",
                "ImAbbyy",
                "lPirlo",
                "Jarool",
                "Bupin",
                "Xhat",
                "EnderMCx",
                "LangScott",
                "WTDpuddles",
                "Daggez",
                "TurtleCobra",
                "OrcaHedral"
        };
    }





}
