package com.cinemamod.fabric.gui;

import com.cinemamod.fabric.CinemaMod;
import com.cinemamod.fabric.CinemaModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ButtonWidget.Builder;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class VideoSettingsScreen extends Screen {

    protected static final Identifier TEXTURE = new Identifier(CinemaMod.MODID, "textures/gui/menuui.png");
    private boolean shouldReloadScreen;

    public VideoSettingsScreen() {
        super(Text.of("비디오 설정"));
    }


    private static CheckboxWidget checkboxWidget(int x, int y, int width, int height, Text text, boolean checked, CheckboxWidget.Callback callback) {
        CheckboxWidget widget = CheckboxWidget.builder(text, MinecraftClient.getInstance().textRenderer)
                .pos(x, y)
                .checked(checked)
                .callback(callback)
                .build();
        widget.setWidth(width);
        widget.setHeight(height);
        return widget;
    }

    @Override
    protected void init() {
        addDrawableChild(new SliderWidget(method_31362() + 23, 78, 196, 20, Text.of("음량"),
                CinemaModClient.getInstance().getVideoSettings().getVolume()) {
            @Override
            protected void updateMessage() {
            }

            @Override
            protected void applyValue() {
                for (com.cinemamod.fabric.screen.Screen screen : CinemaModClient.getInstance().getScreenManager().getScreens())
                    screen.setVideoVolume((float) value);
                CinemaModClient.getInstance().getVideoSettings().setVolume((float) value);
            }
        });

        addDrawableChild(checkboxWidget(method_31362() + 23, 110, 196, 20, Text.of("창이 포커싱 중이 아닐 때 음소거"),
                CinemaModClient.getInstance().getVideoSettings().isMuteWhenAltTabbed(),
                (checkbox, checked) -> CinemaModClient.getInstance().getVideoSettings().setMuteWhenAltTabbed(checked)
        ));
        addDrawableChild(checkboxWidget(method_31362() + 23, 142, 196, 20, Text.of("영상이 재생중일 때 십자선 숨기기"),
                CinemaModClient.getInstance().getVideoSettings().isHideCrosshair(),
                (checkbox, checked) -> CinemaModClient.getInstance().getVideoSettings().setHideCrosshair(checked)
        ));
        ButtonWidget.Builder screenResolutionBuilder = new Builder(
            Text.of("화면 해상도: " + CinemaModClient.getInstance().getVideoSettings().getBrowserResolution() + "p"),
             button ->
        {
            CinemaModClient.getInstance().getVideoSettings().setNextBrowserResolution();
            button.setMessage(Text.of("화면 해상도: " + CinemaModClient.getInstance().getVideoSettings().getBrowserResolution() + "p"));
            shouldReloadScreen = true;
        });
        screenResolutionBuilder.dimensions(method_31362() + 23, 142 + 32, 196, 20);
        addDrawableChild(screenResolutionBuilder.build());
        ButtonWidget.Builder browserRefreshRateBuilder = new Builder(
                Text.of("화면 갱신 주기: " + CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate() + " fps"),
                button ->
                {
                    CinemaModClient.getInstance().getVideoSettings().setNextBrowserRefreshRate();
                    button.setMessage(Text.of("화면 갱신 주기: " + CinemaModClient.getInstance().getVideoSettings().getBrowserRefreshRate() + " fps"));
                    shouldReloadScreen = true;
                });
        browserRefreshRateBuilder.dimensions(method_31362() + 23, 142 + 32 + 32, 196, 20);
        addDrawableChild(browserRefreshRateBuilder.build());
    }

    private int method_31359() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int method_31360() {
        return this.method_31359() / 16;
    }

    private int method_31362() {
        return (this.width - 238) / 2;
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = this.method_31362() + 3;
//        super.renderBackground(context, mouseX, mouseY, delta);
        renderDarkening(context);
        renderInGameBackground(context);
        context.drawTexture(TEXTURE, i, 64, 1, 1, 236, 8);
        int j = this.method_31360();
        for (int k = 0; k < j; ++k)
            context.drawTexture(TEXTURE, i, 72 + 16 * k, 1, 10, 236, 16);
        context.drawTexture(TEXTURE, i, 72 + 16 * j, 1, 27, 236, 8);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.client.textRenderer, Text.of("비디오 설정"), this.width / 2, 64 - 10, -1);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        CinemaModClient.getInstance().getVideoSettings().saveAsync();
        if (shouldReloadScreen) {
            for (com.cinemamod.fabric.screen.Screen screen : CinemaModClient.getInstance().getScreenManager().getScreens()) {
                if (screen.hasBrowser()) {
                    screen.reload();
                }
            }
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

}
