/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.dalekcraft.structureedit.assets;

import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.requireNonNull;

public class Registry<V> implements Iterable<V> {
    private final Map<ResourceLocation, V> map = new HashMap<>();
    private final String name;
    private V defaultValue;

    public Registry(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public V get(final ResourceLocation key) {
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    public V register(final ResourceLocation key, final V value) {
        requireNonNull(key, "key");
        requireNonNull(value, "value");
        checkState(!map.containsKey(key), "key '%s' already has an associated %s", key, name);
        map.put(key, value);
        return value;
    }

    public void clear() {
        map.clear();
    }

    public boolean containsKey(ResourceLocation key) {
        return map.containsKey(key);
    }

    public Set<ResourceLocation> keySet() {
        return Collections.unmodifiableSet(map.keySet());
    }

    public Collection<V> values() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override
    public Iterator<V> iterator() {
        return map.values().iterator();
    }

    public V getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(V defaultValue) {
        this.defaultValue = defaultValue;
    }
}
