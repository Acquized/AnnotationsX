/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.config;

import cc.acquized.annotationsx.AnnotationsX;
import cc.acquized.annotationsx.config.extension.ConfigService;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * An abstract representation of File and YamlConfiguration.
 * Obclass for every Class that wishes to be a ConfigService saved in the Plugin Directory.
 *
 * @author Acquized
 * @since Annotations 1.0
 */
public class ConfigFramework extends ConfigService {

    /**
     * Represents the File and the Path of the YamlConfiguration
     * @see java.io.File
     */
    @Getter private final File file;

    /**
     * Represents the Configuration Object of the YamlConfiguration
     * Used for loading, setting and saving all Entry Fields
     * @see org.bukkit.configuration.file.FileConfiguration
     */
    @Getter private FileConfiguration config;

    public ConfigFramework(File file) throws IOException {
        this.file = file;
        load();
    }

    /**
     * Loads (or create if it doesn't exists) the YamlConfiguration.
     * This creates first the Parent Directory and aafter that it loads the YamlConfiguration
     * @throws IOException If loading failed
     */
    @Override
    public final void load() throws IOException {
        if(!file.getParentFile().isDirectory()) {
            if(!file.getParentFile().mkdirs()) {
                throw new IOException("Could not create " + file.getName() + "'s Parent Directory.");
            }
        }
        if(!file.exists()) {
            Files.copy(AnnotationsX.getParent().getResource(file.getName()), file.toPath());
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the YamlConfiguration from Memory and writes it to File
     * @throws IOException If saving failed
     */
    @Override
    public final void save() throws IOException {
        if(config != null) {
            config.save(file);
        }
    }

    /**
     * Writes the YamlConfiguration from Memory to file and loads it again (if changed from User while not loaded)
     * Invoke <code>#parse(this)</code> after this Method to re-set the Fields
     * @throws IOException If reloading failed
     */
    @Override
    public final void reload() throws IOException {
        save();
        load();
    }

    /**
     * Deletes the File and creates it again from in-jar.
     * @throws IOException If regeneration failed
     */
    @Override
    public final void regenerate() throws IOException {
        if(!file.delete()) {
            throw new IOException("Could not regenerate " + file.getName() + ". Deleting failed.");
        }
        load();
    }

    /**
     * Parses trough all Fields annotated with <code>@Entry</code> and sets the Field Values to the values from the YamlConfiguration
     * @param loader Class Object
     * @throws IllegalAccessException If parsing failed
     * @throws IllegalArgumentException If the <code>loader</code> is null or not a Class Object
     */
    @SuppressWarnings("deprecation")
    @Override
    public void parse(Object loader) throws IllegalArgumentException, IllegalAccessException {
        Validate.notNull(loader, "The Class is null.");
        if(!loader.getClass().isAssignableFrom(ConfigFramework.class)) {
            throw new IllegalArgumentException("The Class Object doesn't extends ConfigFramework.");
        }
        ConfigParser.parse(loader, config);
    }

}
