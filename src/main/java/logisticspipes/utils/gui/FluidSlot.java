package logisticspipes.utils.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class FluidSlot extends Slot {

	public FluidSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

	public FluidSlot(Slot slot) {
		super(slot.inventory, slot.getSlotIndex(), slot.xPos, slot.yPos);
	}

	@Override
	public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
		return false;
	}
}