/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.command.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Method as Command Method.
 * The Method will then be called (if registered) by the CommandManager if somebody on the Server executed the Command
 *
 * @author Acquized, minnymin3
 * @since Annotations 2.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * The Name of the Command. The Command will be registered as /[name]
     * @return Name of the Command
     */
    String name();

    /**
     * The Description of the Command. This will be registered in /help
     * @return Description of the Command - can be empty
     */
    String description() default "";

    /**
     * A List of Aliases of the Command. If a alias is executed, the method will still be called (even if the method isn't the command's name)
     * @return A List of Aliases of the Command - can be empty
     */
    String[] aliases() default "";

    /**
     * The Permission required to execute this Command. Leave empty for No Permission required.
     * @return The Permission required to execute this Command - can be empty
     */
    String permission() default "";

    /**
     * The Message sent to the CommandSender if he tries to execute the Command but doesn't has Permissions for it.
     * @return The Message sent to the CommandSender if tries to execute, but has no permission.
     */
    String noPermissions() default "You don't have Permission to execute this Command.";

    /**
     * The Usage of the Command. Will be displayed as syntax if command syntax failed. This will be registered in /help
     * @return The Syntax Pattern of the Command - can be empty
     */
    String usage() default "";

    /**
     * A Boolean dedicated to if the Command can only be executed from Players
     * The Name is missleading - it may be replaced with playersOnly();
     * @return a Boolean set to true if only executeable from In-Game Players
     */
    boolean inGameOnly() default false;

    /**
     * The Message sent to the CommandSender if he tries to execute the Command but isn't a Player.
     * @return The Message sent to the CommandSender if tries to execute, but is not a Player.
     */
    String notInGame() default "This command is only executable from In-Game.";

}
