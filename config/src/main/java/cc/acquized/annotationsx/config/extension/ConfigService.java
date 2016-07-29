/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.config.extension;

import cc.acquized.annotationsx.api.Service;

import java.io.IOException;

public abstract class ConfigService extends Service {

    public ConfigService() {
        super("ConfigService");
    }

    public abstract void load() throws IOException;
    public abstract void save() throws IOException;
    public abstract void reload() throws IOException;
    public abstract void regenerate() throws IOException;
    public abstract void parse(Object loader) throws IllegalAccessException;

}
