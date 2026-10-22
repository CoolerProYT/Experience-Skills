package com.coolerpromc.experienceskills.client.screen;

import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.client.SkillText;
import com.coolerpromc.experienceskills.client.hud.HudRenderer;
import com.coolerpromc.experienceskills.config.value.Milestone;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.config.value.WeightedStat;
import com.coolerpromc.experienceskills.data.attachment.ModDataAttachments;
import com.coolerpromc.experienceskills.key.ModKeyMappings;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.skill.SkillManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class SkillsScreen extends Screen {
    private static final int ROW_HEIGHT = 44;
    private static final int MAX_LIST_WIDTH = 380;
    private static final int LIST_TOP = 32;
    private static final int FOOTER_HEIGHT = 56;

    private List<ExperienceType> skills = List.of();
    private double scroll;
    private CycleButton<ExperienceType> fromButton;
    private CycleButton<ExperienceType> toButton;
    private EditBox amountBox;

    public SkillsScreen(){
        super(Component.translatable("screen.experienceskills.skills"));
    }

    @Override
    protected void init(){
        this.skills = ExperienceSkillsAPI.getEnabledTypes();
        int footerTop = this.height - FOOTER_HEIGHT + 6;
        int center = this.width / 2;
        int left = this.listLeft();

        if (this.skills.size() >= 2){
            int gap = 4;
            int amountWidth = 50;
            int buttonWidth = 64;
            int cycleWidth = (this.listWidth() - amountWidth - buttonWidth - gap * 3) / 2;
            int x = left;

            this.fromButton = this.addRenderableWidget(CycleButton.builder(SkillText::skillName, this.skills.get(0)).withValues(this.skills).displayOnlyValue()
                .create(x, footerTop, cycleWidth, 20, Component.translatable("screen.experienceskills.transfer.from")));
            x += cycleWidth + gap;
            this.toButton = this.addRenderableWidget(CycleButton.builder(SkillText::skillName, this.skills.get(1)).withValues(this.skills).displayOnlyValue()
                .create(x, footerTop, cycleWidth, 20, Component.translatable("screen.experienceskills.transfer.to")));
            x += cycleWidth + gap;
            this.amountBox = this.addRenderableWidget(new EditBox(this.font, x, footerTop, amountWidth, 20, Component.translatable("screen.experienceskills.transfer.amount")));
            this.amountBox.setMaxLength(9);
            this.amountBox.setHint(Component.translatable("screen.experienceskills.transfer.amount").withStyle(ChatFormatting.DARK_GRAY));
            x += amountWidth + gap;
            this.addRenderableWidget(Button.builder(Component.translatable("screen.experienceskills.transfer"), b -> this.transfer())
                .bounds(x, footerTop, buttonWidth, 20).build());
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).bounds(center - 100, this.height - 26, 200, 20).build());
    }

    private void transfer(){
        LocalPlayer player = this.minecraft.player;
        String digits = this.amountBox.getValue().replaceAll("[^0-9]", "");
        if (player == null || digits.isEmpty()) return;

        ExperienceType from = this.fromButton.getValue();
        ExperienceType to = this.toButton.getValue();
        int amount = (int) Math.min(Long.parseLong(digits), Integer.MAX_VALUE);
        if (amount <= 0) return;
        player.connection.sendCommand(String.format(Locale.ROOT, "experienceskills transfer %s %s %d", from.getSerializedName(), to.getSerializedName(), amount));
        this.amountBox.setValue("");
    }

    private int listLeft(){
        return (this.width - this.listWidth()) / 2;
    }

    private int listWidth(){
        return Math.min(MAX_LIST_WIDTH, this.width - 20);
    }

    private int listBottom(){
        return this.height - FOOTER_HEIGHT;
    }

    private double maxScroll(){
        return Math.max(0, this.skills.size() * ROW_HEIGHT - (this.listBottom() - LIST_TOP));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a){
        super.extractRenderState(graphics, mouseX, mouseY, a);
        graphics.centeredText(this.font, this.title, this.width / 2, 12, -1);

        LocalPlayer player = this.minecraft.player;
        if (player == null) return;

        if (this.skills.isEmpty()){
            graphics.centeredText(this.font, Component.translatable("screen.experienceskills.empty"), this.width / 2, LIST_TOP + 20, 0xFFAAAAAA);
            return;
        }

        this.scroll = Mth.clamp(this.scroll, 0, this.maxScroll());
        int left = this.listLeft();
        int width = this.listWidth();
        int bottom = this.listBottom();
        Map<String, Integer> progress = Services.ATTACHMENT.get(player, ModDataAttachments.SKILL_PROGRESS);
        ExperienceType hovered = null;

        graphics.enableScissor(left, LIST_TOP, left + width, bottom);
        for (int i = 0; i < this.skills.size(); i++){
            int y = LIST_TOP + i * ROW_HEIGHT - (int) this.scroll;
            if (y + ROW_HEIGHT < LIST_TOP || y > bottom) continue;

            ExperienceType type = this.skills.get(i);
            boolean isHovered = mouseX >= left && mouseX < left + width && mouseY >= Math.max(y, LIST_TOP) && mouseY < Math.min(y + ROW_HEIGHT, bottom);
            if (isHovered) hovered = type;
            this.extractRow(graphics, player, type, progress.get(type.getSerializedName()), left, y, width, isHovered);
        }
        graphics.disableScissor();

        if (this.maxScroll() > 0){
            int trackHeight = bottom - LIST_TOP;
            int thumbHeight = Math.max(16, (int) (trackHeight * (trackHeight / (double) (this.skills.size() * ROW_HEIGHT))));
            int thumbY = LIST_TOP + (int) ((trackHeight - thumbHeight) * (this.scroll / this.maxScroll()));
            graphics.fill(left + width + 2, LIST_TOP, left + width + 6, bottom, 0x80000000);
            graphics.fill(left + width + 2, thumbY, left + width + 6, thumbY + thumbHeight, 0xFFA0A0A0);
        }

        if (hovered != null){
            graphics.setTooltipForNextFrame(this.tooltip(player, hovered), mouseX, mouseY);
        }
    }

    private void extractRow(GuiGraphicsExtractor graphics, LocalPlayer player, ExperienceType type, Integer awardProgress, int x, int y, int width, boolean hovered){
        SkillsConfig config = type.getConfig();
        int color = ARGB.color(0xFF, type.color());
        int level = SkillManager.getLevel(player, type);
        int into = SkillManager.getPointsIntoLevel(player, type);
        int needed = type.pointsForNextLevel(level);
        boolean maxed = level >= type.getMaxLevel();

        graphics.fill(x, y + 1, x + width, y + ROW_HEIGHT - 1, hovered ? 0x60FFFFFF : 0x40000000);
        if (type == HudRenderer.currentType){
            graphics.outline(x, y + 1, width, ROW_HEIGHT - 2, color);
        }
        graphics.fakeItem(SkillText.icon(type), x + 4, y + 6);

        int textX = x + 26;
        int right = x + width - 6;
        graphics.text(this.font, SkillText.skillName(type), textX, y + 4, color);
        Component levelText = maxed
            ? Component.translatable("screen.experienceskills.level_max", level)
            : Component.translatable("screen.experienceskills.level", level, type.getMaxLevel());
        graphics.text(this.font, levelText, right - this.font.width(levelText), y + 4, -1);

        int barTop = y + 15;
        int barRight = right;
        float fraction = maxed ? 1 : Mth.clamp(into / (float) needed, 0, 1);
        graphics.fill(textX, barTop, barRight, barTop + 4, 0xFF1A1A1A);
        graphics.fill(textX, barTop, textX + (int) ((barRight - textX) * fraction), barTop + 4, color);

        graphics.text(this.font, SkillText.bonus(type, level), textX, y + 22, 0xFFC0C0C0);
        if (!maxed){
            Component points = Component.translatable("screen.experienceskills.points", into, needed);
            graphics.text(this.font, points, right - this.font.width(points), y + 22, 0xFFC0C0C0);
        }

        MutableComponent footer = Component.empty();
        if (awardProgress != null && !maxed){
            footer.append(Component.translatable("screen.experienceskills.next_award", awardProgress, config.xpAwardActionCount(), SkillText.statName(config.stat()), config.xpPointToAward()));
        }
        nextMilestone(config, level).ifPresent(m -> {
            if (!footer.getSiblings().isEmpty()) footer.append(Component.literal("  ·  "));
            footer.append(Component.translatable("screen.experienceskills.next_milestone", m.level(), SkillText.milestone(m)));
        });
        List<FormattedCharSequence> lines = this.font.split(footer, right - textX);
        if (!lines.isEmpty()){
            graphics.text(this.font, lines.getFirst(), textX, y + 32, 0xFF808080);
        }
    }

    private static Optional<Milestone> nextMilestone(SkillsConfig config, int level){
        return config.milestones().stream().filter(m -> m.level() > level).min((a, b) -> Integer.compare(a.level(), b.level()));
    }

    private List<FormattedCharSequence> tooltip(LocalPlayer player, ExperienceType type){
        SkillsConfig config = type.getConfig();
        int level = SkillManager.getLevel(player, type);
        List<Component> lines = new ArrayList<>();

        lines.add(SkillText.skillName(type).withStyle(s -> s.withColor(type.color())));
        lines.add(Component.translatable("screen.experienceskills.tooltip.max_bonus", SkillText.bonus(type, type.getMaxLevel())).withStyle(ChatFormatting.GRAY));

        lines.add(Component.translatable("screen.experienceskills.tooltip.sources").withStyle(ChatFormatting.YELLOW));
        for (WeightedStat source : config.allStats()){
            MutableComponent line = Component.literal(" • ").append(SkillText.statName(source.stat()));
            if (source.weight() != 1.0){
                line.append(String.format(Locale.ROOT, " ×%s", trim(source.weight())));
            }
            lines.add(line.withStyle(ChatFormatting.GRAY));
        }

        if (!config.milestones().isEmpty()){
            lines.add(Component.translatable("screen.experienceskills.tooltip.milestones").withStyle(ChatFormatting.YELLOW));
            config.milestones().stream().sorted((a, b) -> Integer.compare(a.level(), b.level())).forEach(m -> {
                boolean unlocked = level >= m.level();
                lines.add(Component.literal(unlocked ? " ✔ " : " ✖ ")
                    .append(Component.translatable("screen.experienceskills.tooltip.milestone", m.level(), SkillText.milestone(m)))
                    .withStyle(unlocked ? ChatFormatting.GREEN : ChatFormatting.DARK_GRAY));
            });
        }

        if (config.dropOnDeath() && config.deathPenalty() > 0){
            lines.add(Component.translatable("screen.experienceskills.tooltip.death_penalty", Math.round(config.deathPenalty() * 100)).withStyle(ChatFormatting.RED));
        }
        lines.add(Component.translatable("screen.experienceskills.tooltip.click").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));

        List<FormattedCharSequence> out = new ArrayList<>();
        for (Component line : lines){
            out.addAll(this.font.split(line, 260));
        }
        return out;
    }

    private static String trim(double value){
        return value == Math.rint(value) ? Long.toString((long) value) : Double.toString(value);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY){
        if (y >= LIST_TOP && y < this.listBottom()){
            this.scroll = Mth.clamp(this.scroll - scrollY * ROW_HEIGHT / 2.0, 0, this.maxScroll());
            return true;
        }
        return super.mouseScrolled(x, y, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick){
        if (super.mouseClicked(event, doubleClick)) return true;

        int left = this.listLeft();
        if (event.x() >= left && event.x() < left + this.listWidth() && event.y() >= LIST_TOP && event.y() < this.listBottom()){
            int index = (int) ((event.y() - LIST_TOP + this.scroll) / ROW_HEIGHT);
            if (index >= 0 && index < this.skills.size()){
                ExperienceType type = this.skills.get(index);
                HudRenderer.currentType = HudRenderer.currentType == type ? ExperienceType.VANILLA : type;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event){
        if (ModKeyMappings.OPEN_SKILLS_SCREEN.matches(event) && !(this.getFocused() instanceof EditBox)){
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen(){
        return false;
    }
}
