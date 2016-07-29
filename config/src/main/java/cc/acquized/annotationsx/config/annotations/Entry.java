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
 * Markes a field as ConfigService Entry
 * The Fields Value will be replaced when <code>ConfigFramework#parse</code> is called with the Values from the YamlConfiguration
 *
 * @author Acquized
 * @since Annotations 1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entry {

    /**
     * The Path of the Object set in the YamlConfiguration
     * @return Path of the Object in YamlConfiguration
     */
    String value();

    /**
     * Replaces all Strings, Lists or Chars prefixed with <code>&</code> with its <code>ColorCode</code>,
     * @return Boolean whenever the <code>&</code> Char should be replaced.
     */
    boolean color() default false;

}
