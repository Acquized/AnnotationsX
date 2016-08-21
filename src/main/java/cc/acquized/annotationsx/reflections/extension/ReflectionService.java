/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.reflections.extension;

import cc.acquized.annotationsx.api.Service;

public abstract class ReflectionService extends Service {

    public ReflectionService() {
        super("Reflections");
    }

    public abstract void parse(Object loader) throws IllegalAccessException, ClassNotFoundException;

}
