package repulica.cardstock.component;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.inventory.SimpleInventory;

public interface CardBinderComponent extends ComponentV3, AutoSyncedComponent {
	CardBinderInventory getInv();
}
