/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.reflections;

public enum Resolve {

    NMS("net.minecraft.server"), OCB("org.bukkit.craftbukkit");

    private String pack;

    Resolve(String pack) {
        this.pack = pack;
    }

    public String getPackage() {
        return pack;
    }

}
