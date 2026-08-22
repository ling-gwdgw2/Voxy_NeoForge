package me.cortex.voxy.client.config;

import me.cortex.voxy.client.RenderStatistics;
import me.cortex.voxy.client.config.SodiumConfigBuilder.*;
import me.cortex.voxy.client.VoxyClient;
import me.cortex.voxy.client.VoxyClientInstance;
import me.cortex.voxy.client.core.IGetVoxyRenderSystem;
import me.cortex.voxy.client.core.util.IrisUtil;
import me.cortex.voxy.common.util.cpu.CpuLayout;
import me.cortex.voxy.commonImpl.VoxyCommon;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPoint;
import net.caffeinemc.mods.sodium.api.config.ConfigEntryPointForge;
import net.caffeinemc.mods.sodium.api.config.option.OptionFlag;
import net.caffeinemc.mods.sodium.api.config.option.OptionImpact;
import net.caffeinemc.mods.sodium.api.config.option.Range;
import net.caffeinemc.mods.sodium.api.config.structure.ConfigBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@ConfigEntryPointForge("voxy")
public class VoxyConfigMenu implements ConfigEntryPoint {
    @Override
    public void registerConfigLate(ConfigBuilder B) {
        if (!VoxyCommon.isAvailable())
            return;// Dont even register the config if its not avalible

        var CFG = VoxyConfig.CONFIG;

        var cc = B.registerModOptions("voxy", "Voxy", VoxyCommon.MOD_VERSION)
                .setIcon(ResourceLocation.parse("voxy:icon.png"));

        final var RENDER_RELOAD = OptionFlag.REQUIRES_RENDERER_RELOAD.getId().toString();

        SodiumConfigBuilder.buildToSodium(B, cc, CFG::save, postOp -> {
            postOp.register("voxy:update_threads", () -> {
                var instance = VoxyCommon.getInstance();
                if (instance != null) {
                    instance.updateDedicatedThreads();
                }
            }, "voxy:enabled").register("voxy:iris_reload", () -> IrisUtil.reload());
        },
                new Page(Component.translatable("voxy.config.general"),
                        new Group(
                                new BoolOption(
                                        "voxy:enabled",
                                        Component.translatable("voxy.config.general.enabled"),
                                        () -> CFG.enabled, v -> {
                                            CFG.enabled = v;
                                            // we need to special case enabled, since the render reload flag runs befor
                                            // us and its quite important we get it right
                                            if (v && VoxyClientInstance.isInGame) {
                                                VoxyCommon.createInstance();
                                            }
                                        })
                                        .setPostChangeRunner(c -> {
                                            if (!c) {
                                                var vrsh = (IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer;
                                                if (vrsh != null) {
                                                    vrsh.shutdownRenderer();
                                                }
                                                VoxyCommon.shutdownInstance();
                                            }
                                        }).setPostChangeFlags(RENDER_RELOAD, "voxy:iris_reload").setEnabler(null)),
                        new Group(
                                new IntOption(
                                        "voxy:thread_count",
                                        Component.translatable("voxy.config.general.serviceThreads"),
                                        () -> CFG.serviceThreads, v -> CFG.serviceThreads = v,
                                        new Range(1, CpuLayout.getCoreCount(), 1))
                                        .setPostChangeFlags("voxy:update_threads"),
                                new BoolOption(
                                        "voxy:use_sodium_threads",
                                        Component.translatable("voxy.config.general.useSodiumBuilder"),
                                        () -> !CFG.dontUseSodiumBuilderThreads,
                                        v -> CFG.dontUseSodiumBuilderThreads = !v)
                                        .setPostChangeFlags("voxy:update_threads")),
                        new Group(
                                new BoolOption(
                                        "voxy:ingest_enabled",
                                        Component.translatable("voxy.config.general.ingest"),
                                        () -> CFG.ingestEnabled, v -> CFG.ingestEnabled = v)),
                        new Group(
                                new IntOption(
                                        "voxy:vram_budget",
                                        Component.translatable("voxy.sodium.option.geometry_buffer_size"),
                                        () -> CFG.geometryBufferSizeMB, v -> CFG.geometryBufferSizeMB = v,
                                        new Range(0, 2048, 256))
                                        .setFormatter(v -> Component.literal(v == 0 ? "Auto" : (v + " MB")))
                                        .setPostChangeFlags(RENDER_RELOAD)),
                        new Group(
                                new BoolOption(
                                        "voxy:auto_pregen_enabled",
                                        Component.translatable("voxy.sodium.option.auto_pregen"),
                                        () -> CFG.autoPregenOnJoin, v -> CFG.autoPregenOnJoin = v)
                                        .setPostChangeRunner(c -> {
                                            var mc = Minecraft.getInstance();
                                            if (c) {
                                                if (mc.player != null && mc.getSingleplayerServer() != null) {
                                                    me.cortex.voxy.client.pregen.WorldPregenerator.getInstance()
                                                            .startPregen(CFG.autoPregenRadius);
                                                }
                                            } else {
                                                me.cortex.voxy.client.pregen.WorldPregenerator.getInstance()
                                                        .cancelPregen();
                                            }
                                        }, "voxy:enabled"),
                                new IntOption(
                                        "voxy:auto_pregen_radius",
                                        Component.translatable("voxy.sodium.option.auto_pregen_radius"),
                                        () -> CFG.autoPregenRadius, v -> CFG.autoPregenRadius = v,
                                        new Range(16, 128, 16))
                                        .setFormatter(v -> Component.literal(v + " chunks"))
                                        .setPostChangeRunner(r -> {
                                            var pregen = me.cortex.voxy.client.pregen.WorldPregenerator.getInstance();
                                            if (pregen.isRunning()) {
                                                pregen.startPregen(r);
                                            }
                                        }, "voxy:auto_pregen_enabled"),
                                new IntOption(
                                        "voxy:auto_pregen_threads",
                                        Component.translatable("voxy.sodium.option.auto_pregen_threads"),
                                        () -> CFG.autoPregenThreads, v -> CFG.autoPregenThreads = v,
                                        new Range(1, 4, 1))
                                        .setFormatter(v -> Component.literal(v + " Threads"))
                                        .setPostChangeRunner(t -> {
                                            var pregen = me.cortex.voxy.client.pregen.WorldPregenerator.getInstance();
                                            if (pregen.isRunning()) {
                                                pregen.startPregen(CFG.autoPregenRadius);
                                            }
                                        }, "voxy:auto_pregen_enabled")))
                        .setEnabler("voxy:enabled"),
                new Page(Component.translatable("voxy.config.rendering"),
                        new Group(
                                new BoolOption(
                                        "voxy:rendering",
                                        Component.translatable("voxy.config.general.rendering"),
                                        () -> CFG.enableRendering, v -> CFG.enableRendering = v)
                                        .setPostChangeRunner(c -> {
                                            var vrsh = (IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer;
                                            if (vrsh != null) {
                                                if (c) {
                                                    vrsh.createRenderer();
                                                } else {
                                                    vrsh.shutdownRenderer();
                                                }
                                            }
                                        }, "voxy:enabled", RENDER_RELOAD)
                                        .setPostChangeFlags("voxy:iris_reload")
                                        .setEnabler("voxy:enabled")),
                        new Group(
                                new IntOption(
                                        "voxy:subdivsize",
                                        Component.translatable("voxy.config.general.subDivisionSize"),
                                        () -> subDiv2ln(CFG.subDivisionSize), v -> CFG.subDivisionSize = ln2subDiv(v),
                                        new Range(0, SUBDIV_IN_MAX, 1))
                                        .setFormatter(
                                                v -> Component.literal(Integer.toString(Math.round(ln2subDiv(v)))))
                                        .setPostChangeFlags(RENDER_RELOAD),
                                new IntOption(
                                        "voxy:render_distance",
                                        Component.translatable("voxy.config.general.renderDistance"),
                                        () -> CFG.sectionRenderDistance, v -> CFG.sectionRenderDistance = v,
                                        new Range(2, 64, 1))
                                        .setFormatter(v -> Component.literal(Integer.toString(v * 32)))// Top level rd
                                                                                                       // == 32 chunks
                                        .setPostChangeRunner(c -> {
                                            var vrsh = (IGetVoxyRenderSystem) Minecraft.getInstance().levelRenderer;
                                            if (vrsh != null) {
                                                var vrs = vrsh.getVoxyRenderSystem();
                                                if (vrs != null) {
                                                    vrs.setRenderDistance(c);
                                                }
                                            }
                                        }, "voxy:rendering", RENDER_RELOAD)),
                        new Group(
                                new IntOption(
                                        "voxy:lod_boundary_buffer",
                                        Component.translatable("voxy.sodium.option.lod_boundary_buffer"),
                                        () -> CFG.lodBoundaryBuffer, v -> CFG.lodBoundaryBuffer = v,
                                        new Range(0, 4, 1))
                                        .setPostChangeFlags(RENDER_RELOAD),
                                new IntOption(
                                        "voxy:earth_curve_ratio",
                                        Component.translatable("voxy.sodium.option.earth_curve_ratio"),
                                        () -> CFG.earthCurveRatio, v -> CFG.earthCurveRatio = v,
                                        new Range(0, 500, 10))
                                        .setFormatter(v -> Component
                                                .literal(v == 0 ? "Off" : (v < 50 ? "50" : Integer.toString(v))))
                                        .setPostChangeFlags(RENDER_RELOAD)),
                        new Group(
                                new BoolOption(
                                        "voxy:eviromental_fog",
                                        Component.translatable("voxy.config.general.environmental_fog"),
                                        () -> CFG.useEnvironmentalFog, v -> CFG.useEnvironmentalFog = v)
                                        .setPostChangeFlags(RENDER_RELOAD),
                                new BoolOption(
                                        "voxy:water_ssr_reflection",
                                        Component.translatable("voxy.sodium.option.water_ssr_reflection"),
                                        () -> CFG.enableWaterSSR, v -> CFG.enableWaterSSR = v)
                                        .setPostChangeFlags(RENDER_RELOAD, "voxy:iris_reload"),
                                new BoolOption(
                                        "voxy:distant_shader_shadows",
                                        Component.translatable("voxy.sodium.option.distant_shader_shadows"),
                                        () -> CFG.enableDistantShaderShadows, v -> CFG.enableDistantShaderShadows = v)
                                        .setPostChangeFlags(RENDER_RELOAD, "voxy:iris_reload")),
                        new Group(
                                new BoolOption(
                                        "voxy:render_debug",
                                        Component.translatable("voxy.config.general.render_statistics"),
                                        () -> RenderStatistics.enabled, v -> RenderStatistics.enabled = v)
                                        .setPostChangeFlags(RENDER_RELOAD)))
                        .setEnablerAND("voxy:enabled", "voxy:rendering"));

    }

    private static final int SUBDIV_IN_MAX = 100;
    private static final double SUBDIV_MIN = 28;
    private static final double SUBDIV_MAX = 256;
    private static final double SUBDIV_CONST = Math.log(SUBDIV_MAX / SUBDIV_MIN) / Math.log(2);

    // In range is 0->200
    // Out range is 28->256
    private static float ln2subDiv(int in) {
        return (float) (SUBDIV_MIN * Math.pow(2, SUBDIV_CONST * ((double) in / SUBDIV_IN_MAX)));
    }

    // In range is ... any?
    // Out range is 0->200
    private static int subDiv2ln(float in) {
        return (int) (((Math.log(((double) in) / SUBDIV_MIN) / Math.log(2)) / SUBDIV_CONST) * SUBDIV_IN_MAX);
    }
}
