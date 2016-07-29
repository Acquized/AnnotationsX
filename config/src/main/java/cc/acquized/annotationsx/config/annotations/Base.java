/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.config.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Class with a BasePath. This is a Prefix for all Entries.
 *
 * @author Acquized
 * @since Annotations 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base {

    /**
     * Base Path which is the Prefix for all <code>Entry#value</code>'s Path.
     * @return the Base Path
     */
    String value();

}
