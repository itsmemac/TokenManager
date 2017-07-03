/*
 *
 *   This file is part of TokenManager, licensed under the MIT License.
 *
 *   Copyright (c) Realized
 *   Copyright (c) contributors
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 *
 */

package me.realized.tokenmanager.shop;

import me.realized.tokenmanager.TokenManager;
import me.realized.tokenmanager.util.ItemBuilder;
import me.realized.tokenmanager.util.ItemUtil;
import me.realized.tokenmanager.util.NumberUtil;
import me.realized.tokenmanager.util.config.AbstractConfiguration;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class created at 6/18/17 by Realized
 **/

public class ShopConfig extends AbstractConfiguration<TokenManager> {

    private final Map<String, Shop> shops = new HashMap<>();

    public ShopConfig(final TokenManager plugin) {
        super(plugin, "shops");
    }

    @Override
    public void handleLoad() throws IOException, InvalidConfigurationException {
        super.handleLoad();

        final ConfigurationSection shopsSection = getConfiguration().getConfigurationSection("shops");

        if (shopsSection == null) {
            return;
        }

        for (final String name : shopsSection.getKeys(false)) {
            final ConfigurationSection shopSection = shopsSection.getConfigurationSection(name);
            final Shop shop;

            try {
                shop = new Shop(name,
                        shopSection.getString("title", "&cShop title was not found!"),
                        shopSection.getInt("rows", 1),
                        shopSection.getBoolean("auto-close", false),
                        shopSection.getBoolean("use-permission", false)
                );
            } catch (IllegalArgumentException ex) {
                getPlugin().getLogger().warning("Failed to initialize shop '" + name + "': " + ex.getMessage());
                continue;
            }

            final ConfigurationSection itemsSection = shopSection.getConfigurationSection("items");

            if (itemsSection != null) {
                for (final String num : itemsSection.getKeys(false)) {
                    final Optional<Integer> slot = NumberUtil.parseInt(num);

                    if (!slot.isPresent() || slot.get() < 0 || slot.get() >= shop.getGui().getSize()) {
                        getPlugin().getLogger().warning("Failed to load slot '" + num + "' for shop '" + name + "': '" + slot + "' is not a valid number or is over the shop size.");
                        continue;
                    }

                    final ConfigurationSection slotSection = itemsSection.getConfigurationSection(num);
                    final ItemStack displayed;

                    try {
                        displayed = ItemUtil.loadFromString(slotSection.getString("displayed"), getPlugin().getLogger());
                    } catch (Exception ex) {
                        shop.getGui().setItem(slot.get(), ItemBuilder
                                .of(Material.REDSTONE_BLOCK)
                                .name("&4&m------------------")
                                .lore(
                                        "&cThere was an error",
                                        "&cwhile loading this",
                                        "&citem, please contact",
                                        "&can administrator.",
                                        "&4&m------------------"
                                )
                                .build()
                        );
                        getPlugin().getLogger().warning("Failed to load displayed item for slot '" + num + "' of shop '" + name + "': " + ex.getMessage());
                        continue;
                    }

                    shop.setSlot(slot.get(), displayed, new SlotData(
                            slot.get(),
                            slotSection.getInt("cost", 1000000),
                            slotSection.getString("message"),
                            slotSection.getString("subshop"),
                            slotSection.getStringList("commands"),
                            slotSection.getBoolean("use-permission", false)
                    ));
                }
            }

            shops.put(name, shop);
        }
    }

    @Override
    public void handleUnload() {
        shops.clear();
    }

    public Shop getShop(final String name) {
        return shops.get(name);
    }

    public Collection<Shop> getShops() {
        return shops.values();
    }
}