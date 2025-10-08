package net.vildulv.minecraft.justspace.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.vildulv.minecraft.justspace.BlockRegister;
import net.vildulv.minecraft.justspace.MenuRegister;
import net.vildulv.minecraft.justspace.block.entity.TerminalBlockEntity;

public class TerminalMenu extends AbstractContainerMenu {


    private final TerminalBlockEntity blockEntity;
    private final Level level;

    public TerminalMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public TerminalMenu(int containerId, Inventory inv, BlockEntity blockEntity) {
        super(MenuRegister.TERMINAL_MENU.get(), containerId);
        this.blockEntity = ((TerminalBlockEntity) blockEntity);
        this.level = inv.player.level();
    }


    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockRegister.COMMANDERS_TERMINAL_DEVICE.get());
    }

    public TerminalBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
