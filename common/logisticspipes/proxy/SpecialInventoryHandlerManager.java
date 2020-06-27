package logisticspipes.proxy;

import net.minecraftforge.fml.common.Loader;

import static logisticspipes.LPConstants.appliedenergisticsModID;
import static logisticspipes.LPConstants.betterStorageModID;
import static logisticspipes.LPConstants.factorizationModID;

import logisticspipes.proxy.specialinventoryhandler.AEInterfaceInventoryHandler;
import logisticspipes.proxy.specialinventoryhandler.BarrelInventoryHandler;
import logisticspipes.proxy.specialinventoryhandler.CrateInventoryHandler;
import logisticspipes.proxy.specialinventoryhandler.DSUInventoryHandler;

public class SpecialInventoryHandlerManager {

	public static void load() {
		if (Loader.isModLoaded(factorizationModID)) {
			SimpleServiceLocator.inventoryUtilFactory.registerHandler(new BarrelInventoryHandler());
		}

		if (Loader.isModLoaded(betterStorageModID)) {
			SimpleServiceLocator.inventoryUtilFactory.registerHandler(new CrateInventoryHandler());
		}

		if (Loader.isModLoaded(appliedenergisticsModID)) {
			SimpleServiceLocator.inventoryUtilFactory.registerHandler(new AEInterfaceInventoryHandler());
		}

		SimpleServiceLocator.buildCraftProxy.registerInventoryHandler();

		try {
			Class.forName("powercrystals.minefactoryreloaded.api.IDeepStorageUnit");
			SimpleServiceLocator.inventoryUtilFactory.registerHandler(new DSUInventoryHandler());
		} catch (ClassNotFoundException ignored) {}
	}
}
