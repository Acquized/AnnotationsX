/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.pluginyaml.annotations;

import org.bukkit.plugin.PluginLoadOrder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Needs to annotate the Main Class of the Project
 * Look at http://wiki.bukkit.org/Plugin_YAML to see what the Values do
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Plugin {

    String name();

    String version();

    String description() default "";

    PluginLoadOrder load() default PluginLoadOrder.POSTWORLD;

    String[] authors() default "";

    String website() default "";

    boolean database() default false;

    String prefix() default "";

}
