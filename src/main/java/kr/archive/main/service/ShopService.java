package kr.archive.main.service;

import kr.archive.main.utils.MaterialUtil;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopService {
    private static Boolean legacyContents = null;

    public static int getAmount(ItemStack item, Inventory inventory) {
        if (!inventory.contains(item.getType())) {
            return 0;
        }

        if (inventory.getType() == null) {
            return Integer.MAX_VALUE;
        }

        HashMap<Integer, ? extends ItemStack> items = inventory.all(item.getType());
        int itemAmount = 0;

        for (ItemStack iStack : items.values()) {
            if (!MaterialUtil.equals(iStack, item)) {
                continue;
            }

            itemAmount += iStack.getAmount();
        }

        return itemAmount;
    }

    public static boolean hasItems(ItemStack[] items, Inventory inventory) {
        ItemStack[] mergedItems = mergeSimilarStacks(items);
        for (ItemStack item : mergedItems) {
            if (getAmount(item, inventory) < item.getAmount()) {
                return false;
            }
        }

        return true;
    }

    public static ItemStack[] mergeSimilarStacks(ItemStack... items) {
        if (items.length <= 1) {
            return items;
        }

        List<ItemStack> itemList = new LinkedList<ItemStack>();

        Iterating:
        for (ItemStack item : items) {
            for (ItemStack iStack : itemList) {
                if (MaterialUtil.equals(item, iStack)) {
                    iStack.setAmount(iStack.getAmount() + item.getAmount());
                    continue Iterating;
                }
            }

            itemList.add(item.clone());
        }

        return itemList.toArray(new ItemStack[itemList.size()]);
    }

    public static int remove(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.removeItem(item);

        if (!leftovers.isEmpty()) {
            leftovers.values().removeIf(left -> removeManually(left, inventory) == 0);
        }

        return countItems(leftovers);
    }

    public static int add(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.addItem(item.clone()); // item needs to be cloned as cb changes the amount of the stack size

        if (!leftovers.isEmpty()) {
            for (Iterator<ItemStack> iterator = leftovers.values().iterator(); iterator.hasNext(); ) {
                ItemStack left = iterator.next();
                int amountLeft = addManually(left, inventory, left.getMaxStackSize());
                if (amountLeft == 0) {
                    iterator.remove();
                } else {
                    left.setAmount(amountLeft);
                }
            }
        }
        return countItems(leftovers);
    }

    private static int addManually(ItemStack item, Inventory inventory, int maxStackSize) {
        int amountLeft = item.getAmount();

        for (int currentSlot = 0; currentSlot < effectiveSize(inventory) && amountLeft > 0; currentSlot++) {
            ItemStack currentItem = inventory.getItem(currentSlot);

            if (MaterialUtil.isEmpty(currentItem)) {
                currentItem = new ItemStack(item);
                currentItem.setAmount(Math.min(amountLeft, maxStackSize));
                inventory.setItem(currentSlot, currentItem);

                amountLeft -= currentItem.getAmount();
            } else if (currentItem.getAmount() < maxStackSize && MaterialUtil.equals(currentItem, item)) {
                int neededToAdd = Math.min(maxStackSize - currentItem.getAmount(), amountLeft);

                currentItem.setAmount(currentItem.getAmount() + neededToAdd);

                amountLeft -= neededToAdd;
            }
        }
        return amountLeft;
    }

    private static int removeManually(ItemStack item, Inventory inventory) {
        int amountLeft = item.getAmount();

        for (int currentSlot = 0; currentSlot < effectiveSize(inventory) && amountLeft > 0; currentSlot++) {
            ItemStack currentItem = inventory.getItem(currentSlot);

            if (currentItem != null && MaterialUtil.equals(currentItem, item)) {
                int neededToRemove = Math.min(currentItem.getAmount(), amountLeft);

                currentItem.setAmount(currentItem.getAmount() - neededToRemove);
                inventory.setItem(currentSlot, currentItem);

                amountLeft -= neededToRemove;
            }
        }
        return amountLeft;
    }

    public static int countItems(Map<Integer, ItemStack> items) {
        int count = 0;
        Iterator<Integer> keys = items.keySet().iterator();
        while (keys.hasNext()){
            count += items.get(keys.next()).getAmount();
        }

        return count;
    }

    private static int effectiveSize(Inventory inventory) {
        return getStorageContents(inventory).length;
    }

    private static ItemStack[] getStorageContents(Inventory inventory) {
        if (legacyContents == null) {
            try {
                inventory.getStorageContents();
                legacyContents = false;
            } catch (NoSuchMethodError e) {
                legacyContents = true;
            }
        }

        return legacyContents ? inventory.getContents() : inventory.getStorageContents();
    }

}
