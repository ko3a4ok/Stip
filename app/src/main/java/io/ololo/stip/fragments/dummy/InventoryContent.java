package io.ololo.stip.fragments.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class InventoryContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<InventoryItem> ITEMS = new ArrayList<InventoryItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, InventoryItem> ITEM_MAP = new HashMap<String, InventoryItem>();

    static {
        // Add 3 sample items.
        addItem(new InventoryItem("1", "Item 1"));
        addItem(new InventoryItem("2", "Item 2"));
        addItem(new InventoryItem("3", "Item 3"));
    }

    private static void addItem(InventoryItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class InventoryItem {
        public String id;
        public String content;

        public InventoryItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
