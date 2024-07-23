package com.cinemamod.fabric.mixins;

import com.cinemamod.fabric.CinemaModClient;
import com.cinemamod.fabric.cef.CefUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(GameRenderer.class)
public class CefRenderMixin {

    private static long RENDER_DELTA_NS = TimeUnit.MILLISECONDS.toNanos((long) Math.ceil(1000 / (float) 30));
    private static long CHECK_SETTINGS_DELTA_NS = TimeUnit.MILLISECONDS.toNanos(1000);

    private long lastRenderTime;
    private long lastCheckSettingsTime;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        var startTime = Util.getMeasuringTimeNano();
        if ((startTime - lastRenderTime) > RENDER_DELTA_NS) {
            if (!CefUtil.isInit()) return;
            CefUtil.getCefApp().N_DoMessageLoopWork();
            lastRenderTime = startTime;
        }
        if ((startTime - lastCheckSettingsTime) > CHECK_SETTINGS_DELTA_NS) {
            int refreshRate = CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate();
            RENDER_DELTA_NS = TimeUnit.MILLISECONDS.toNanos((long) Math.ceil(1000 / (float) refreshRate));
            lastCheckSettingsTime = startTime;
        }
    }

}