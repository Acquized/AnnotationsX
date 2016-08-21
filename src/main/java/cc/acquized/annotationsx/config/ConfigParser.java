/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.config;

import cc.acquized.annotationsx.config.annotations.Base;
import cc.acquized.annotationsx.config.annotations.Entry;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Final Class containing the Parse Method which parses trough all Fields annotated with <code>@Entry</code>
 *
 * @author Acquized
 * @since Annotations 1.0
 */
public final class ConfigParser {

    /**
     * Non-sense Constructor which throws a Exception if called. This Class isn't allowed to be initalized
     * @throws IllegalAccessException If called
     */
    public ConfigParser() throws IllegalAccessException {
        throw new IllegalAccessException("The ConfigParser isn't allowed to be initalized.");
    }

    /**
     * Parses trough all Fields annotated with <code>@Entry</code> and sets the Field Values to the values from the YamlConfiguration
     * @param loader Class Object
     * @param config YamlConfiguration from which the Values should be getted
     * @throws IllegalAccessException If parsing failed
     * @deprecated Use ConfigFramework#parse
     * @see cc.acquized.annotationsx.config.ConfigFramework#parse(Object)
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static void parse(Object loader, FileConfiguration config) throws IllegalAccessException {
        Validate.notNull(loader, "The Class is null.");
        Validate.notNull(config, "The ConfigService is null.");
        Class<?> clazz = loader.getClass();
        if(clazz.isAssignableFrom(ConfigFramework.class)) {
            Base basePath = clazz.getAnnotation(Base.class);
            for (Field f : clazz.getDeclaredFields()) {
                Entry annotation = f.getAnnotation(Entry.class);
                if (annotation != null) {
                    f.setAccessible(true);
                    Object value = config.get(basePath != null ? basePath.value() + "." + annotation.value() : annotation.value(), f.get(loader));
                    if (value != null) {
                        if((annotation.color())) {
                            if(value instanceof String) {
                                value = ChatColor.translateAlternateColorCodes('&', (String) value);
                            } else if(value instanceof List) {
                                List list = (List) value;
                                int count = 0;
                                for(Object obj : list) {
                                    if(obj instanceof String) {
                                        list.set(count, ChatColor.translateAlternateColorCodes('&', (String) obj));
                                        count++;
                                    } else {
                                        throw new IllegalArgumentException("Please set #color only to true if the Object is a Type of String, Char or StringList");
                                    }
                                }
                            } else {
                                throw new IllegalArgumentException("Please set #color only to true if the Object is a Type of String, Char or List");
                            }
                        }
                        if (!Modifier.isFinal(f.getModifiers())) {
                            f.set(loader, value);
                        } else {
                            throw new IllegalAccessException("Final fields cannot be annotated with @Entry");
                        }
                    }
                }
            }
        } else {
            throw new IllegalAccessException("You need to parse trough a Class that is a part of DEUTSCHLAND");
        }
    }

}
