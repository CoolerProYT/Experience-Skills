package com.coolerpromc.experienceskills.client.toast;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.client.SkillText;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

public class SkillLevelUpToast implements Toast {
    private static final Identifier BACKGROUND_SPRITE = Identifier.withDefaultNamespace("toast/advancement");
    private static final long DISPLAY_TIME = 4000L;

    private final ExperienceType type;
    private final ItemStack icon;
    private int level;
    private int width = 160;
    private boolean changed;
    private long lastChanged;
    private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

    private SkillLevelUpToast(ExperienceType type, int level){
        this.type = type;
        this.level = level;
        this.icon = SkillText.icon(type);
    }

    public static void show(ToastManager manager, ExperienceType type, int level){
        SkillLevelUpToast toast = manager.getToast(SkillLevelUpToast.class, type);
        if (toast == null){
            toast = new SkillLevelUpToast(type, level);
            manager.addToast(toast);
        } else {
            toast.level = level;
            toast.changed = true;
        }
        toast.width = Math.max(160, 30 + manager.getMinecraft().font.width(toast.description()) + 10);
    }

    private Component description(){
        return Component.translatable("toast.experienceskills.level_up.description", SkillText.skillName(this.type), this.level);
    }

    @Override
    public int width(){
        return this.width;
    }

    @Override
    public Object getToken(){
        return this.type;
    }

    @Override
    public Toast.Visibility getWantedVisibility(){
        return this.wantedVisibility;
    }

    @Override
    public void update(ToastManager manager, long fullyVisibleForMs){
        if (this.changed){
            this.lastChanged = fullyVisibleForMs;
            this.changed = false;
        }
        this.wantedVisibility = fullyVisibleForMs - this.lastChanged >= DISPLAY_TIME * manager.getNotificationDisplayTimeMultiplier()
            ? Toast.Visibility.HIDE
            : Toast.Visibility.SHOW;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs){
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        graphics.text(font, Component.translatable("toast.experienceskills.level_up"), 30, 7, ARGB.color(0xFF, this.type.color()), false);
        graphics.text(font, this.description(), 30, 18, -1, false);
        graphics.fakeItem(this.icon, 8, 8);
    }
}
