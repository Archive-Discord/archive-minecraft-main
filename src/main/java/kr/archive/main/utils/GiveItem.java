package kr.archive.main.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class GiveItem {
    public String give(@NotNull ItemStack item, @NotNull Inventory inventory, int quantity) {
        if (quantity == 0) {
            quantity = 1;
        }
        boolean tooHighQuantity = false;
        int maxStackSize = 64;
        if (item.getMaxStackSize() < quantity) {
            tooHighQuantity = true;
            maxStackSize = item.getMaxStackSize();
        }
        if(tooHighQuantity) {
            return "too-high-quantity";
        }
        inventory.addItem(item);
        return "success";
    }
}
