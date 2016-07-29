/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

public class AnnotationsX extends JavaPlugin {

    /**
     * Owning JavaPlugin which is currently the Parent of this Plugin
     * @param parent A Plugin's Main Class which will be the new Boss of this Library
     * @return The owning JavaPlugin of this Library
     */
    @Getter @Setter private static JavaPlugin parent;

    @Override
    public void onEnable() {
        parent = this;
        getLogger().warning("Using Annotations as Plugin is not officially supported.");
        getLogger().warning("Consider shading AnnotationsX directly into your Project for the best experience.");
    }

    @Override
    public void onDisable() {
        parent = null;
    }

}
