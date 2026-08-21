package me.cortex.voxy.client.pregen;

import me.cortex.voxy.client.config.VoxyConfig;
import me.cortex.voxy.common.Logger;
import me.cortex.voxy.common.world.WorldEngine;
import me.cortex.voxy.common.world.service.VoxelIngestService;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * World Pre-generator for Voxy LODs.
 * Generates and ingests chunks in a specified radius around the player
 * using background worker threads with smart-skip of existing chunks and real-time HUD progress.
 */
public class WorldPregenerator {
    private static final WorldPregenerator INSTANCE = new WorldPregenerator();

    public static WorldPregenerator getInstance() {
        return INSTANCE;
    }

    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final AtomicInteger processedChunks = new AtomicInteger(0);
    private final AtomicInteger skippedChunks = new AtomicInteger(0);
    private final AtomicInteger totalChunks = new AtomicInteger(0);
    private ExecutorService pregenExecutor;
    private long startTime;

    public synchronized boolean startPregen(int radiusChunks) {
        if (this.isRunning.get()) {
            return false;
        }

        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return false;
        }

        var server = mc.getSingleplayerServer();
        if (server == null) {
            mc.gui.getChat().addMessage(Component.literal("[Voxy Pregen] Pre-generation is currently available in Singleplayer / Integrated Server worlds.").withStyle(ChatFormatting.YELLOW));
            return false;
        }

        ServerLevel serverLevel = server.getLevel(mc.level.dimension());
        if (serverLevel == null) {
            return false;
        }

        int centerChunkX = mc.player.getBlockX() >> 4;
        int centerChunkZ = mc.player.getBlockZ() >> 4;

        int total = (radiusChunks * 2 + 1) * (radiusChunks * 2 + 1);
        this.totalChunks.set(total);
        this.processedChunks.set(0);
        this.skippedChunks.set(0);
        this.startTime = System.currentTimeMillis();
        this.isRunning.set(true);

        int threads = Math.max(1, Math.min(4, VoxyConfig.CONFIG.autoPregenThreads));
        this.pregenExecutor = Executors.newFixedThreadPool(threads, r -> {
            Thread t = new Thread(r, "Voxy-WorldPregenerator");
            t.setDaemon(true);
            return t;
        });

        mc.gui.getChat().addMessage(Component.literal(String.format("[Voxy Pregen] Starting LOD generation in %d chunks radius (%d total chunks with %d threads)...", radiusChunks, total, threads)).withStyle(ChatFormatting.GREEN));

        this.pregenExecutor.submit(() -> {
            try {
                long lastHudUpdate = System.currentTimeMillis();
                WorldEngine engine = WorldIdentifier.ofEngineNullable(mc.level);
                int minY = mc.level.getMinBuildHeight() >> 4;
                int maxY = mc.level.getMaxBuildHeight() >> 4;

                for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
                    for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
                        if (!this.isRunning.get()) {
                            break;
                        }

                        int cx = centerChunkX + dx;
                        int cz = centerChunkZ + dz;

                        // Smart-Skip check: Check if chunk already exists in Voxy's database
                        boolean alreadyIngested = false;
                        if (engine != null) {
                            for (int y = minY; y < maxY; y += 4) {
                                var sec = engine.acquireIfExists(0, cx, y, cz);
                                if (sec != null) {
                                    sec.release();
                                    alreadyIngested = true;
                                    break;
                                }
                            }
                        }

                        if (alreadyIngested) {
                            this.skippedChunks.incrementAndGet();
                            this.processedChunks.incrementAndGet();
                            continue;
                        }

                        try {
                            // Generate chunk from server chunk source on worker thread
                            var chunk = serverLevel.getChunkSource().getChunk(cx, cz, ChunkStatus.FULL, true);
                            if (chunk instanceof LevelChunk levelChunk) {
                                VoxelIngestService.tryAutoIngestChunk(levelChunk);
                            }
                        } catch (Exception e) {
                            Logger.error("Failed to generate chunk at " + cx + ", " + cz, e);
                        }

                        int current = this.processedChunks.incrementAndGet();
                        long now = System.currentTimeMillis();

                        if (now - lastHudUpdate >= 250) {
                            lastHudUpdate = now;
                            double elapsedSec = Math.max(0.1, (now - this.startTime) / 1000.0);
                            double rate = current / elapsedSec;
                            int percent = (int) ((current / (double) total) * 100.0);

                            mc.execute(() -> {
                                if (mc.player != null) {
                                    mc.gui.setOverlayMessage(Component.literal(
                                            String.format("[Voxy Pregen] Progress: %d%% (%d / %d chunks, %d skipped) - %.1f chunks/s", percent, current, total, this.skippedChunks.get(), rate)
                                    ).withStyle(ChatFormatting.AQUA), false);
                                }
                            });
                        }
                    }
                    if (!this.isRunning.get()) {
                        break;
                    }
                }

                long totalElapsed = System.currentTimeMillis() - this.startTime;
                double seconds = totalElapsed / 1000.0;
                int finished = this.processedChunks.get();
                int skipped = this.skippedChunks.get();

                mc.execute(() -> {
                    if (mc.gui != null && mc.gui.getChat() != null) {
                        if (this.isRunning.get()) {
                            mc.gui.getChat().addMessage(Component.literal(
                                    String.format("[Voxy Pregen] Finished! Processed %d chunks (%d skipped) in %.2f seconds.", finished, skipped, seconds)
                            ).withStyle(ChatFormatting.GREEN));
                        } else {
                            mc.gui.getChat().addMessage(Component.literal("[Voxy Pregen] Pre-generation cancelled.").withStyle(ChatFormatting.YELLOW));
                        }
                    }
                });

            } catch (Exception e) {
                Logger.error("Error during Voxy world pre-generation", e);
            } finally {
                this.isRunning.set(false);
            }
        });

        return true;
    }

    public synchronized boolean cancelPregen() {
        if (this.isRunning.compareAndSet(true, false)) {
            if (this.pregenExecutor != null) {
                this.pregenExecutor.shutdownNow();
            }
            var mc = Minecraft.getInstance();
            if (mc.gui != null && mc.gui.getChat() != null) {
                mc.gui.getChat().addMessage(Component.literal("[Voxy Pregen] Stopping pre-generation...").withStyle(ChatFormatting.YELLOW));
            }
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return this.isRunning.get();
    }

    public static void triggerAutoPregenIfEnabled() {
        if (VoxyConfig.CONFIG.enabled && VoxyConfig.CONFIG.autoPregenOnJoin) {
            CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
                var mc = Minecraft.getInstance();
                if (mc.player != null && mc.getSingleplayerServer() != null && !INSTANCE.isRunning()) {
                    INSTANCE.startPregen(VoxyConfig.CONFIG.autoPregenRadius);
                }
            });
        }
    }
}
