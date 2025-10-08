package net.vildulv.minecraft.justspace.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.vildulv.minecraft.justspace.SoundRegister;
import net.vildulv.minecraft.justspace.justspace;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TerminalScreen extends Screen implements MenuAccess<TerminalMenu> {

    private final TerminalMenu menu;

    private static final ResourceLocation GUI_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(justspace.MODID, "textures/gui/terminal_screen_2.png");
    private static final ResourceLocation FLICKER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("justspace", "block/terminal_screen_line");

    private static final ResourceLocation BLOCK_ATLAS =  ResourceLocation.fromNamespaceAndPath("minecraft", "textures/atlas/blocks.png");

    public TerminalScreen(TerminalMenu menu, Inventory playerInventory, Component title) {
        super(title);
        this.menu = menu;
    }

    @Override
    public void init() {
        super.init();
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(SoundRegister.STATIC_TERMINAL.get().getLocation(), SoundSource.MASTER, 0.5f, 1.25f, SoundInstance.createUnseededRandom(), true, 0, SoundInstance.Attenuation.NONE, (double)0.0F, (double)0.0F, (double)0.0F, true));
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().getSoundManager().stop();
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - 256) / 2;
        int y = (height - 256) / 2;

        RenderSystem.setShaderTexture(0, BLOCK_ATLAS);

        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(BLOCK_ATLAS).apply(FLICKER_TEXTURE);

        guiGraphics.blit(x, y, 0,  256, 256, sprite );
    }


    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        int leftEdge = (this.width - 256) / 2;
        FormattedText formattedText = Component.literal(menu.getBlockEntity().getTerminalText());
        List<FormattedCharSequence> formattedcharsequences =  this.font.split(formattedText,168);
        int rows = Math.min(15, formattedcharsequences.size());
        for(int row = 0; row < rows; ++row) {
            pGuiGraphics.drawString(this.font, formattedcharsequences.get(row), leftEdge + 48, 56 + row * 8, 2206513, false);
        }

    }

    @Override
    public TerminalMenu getMenu() {
        return menu;
    }
}
