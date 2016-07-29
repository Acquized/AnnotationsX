/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.command.extension;

import cc.acquized.annotationsx.api.Service;
import cc.acquized.annotationsx.command.annotations.Command;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;

public abstract class CommandService extends Service {

    public CommandService() {
        super("CommandService");
    }

    public abstract boolean handle(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args);
    public abstract void registerCommands(Object loader);
    public abstract void registerCommand(Command command, String labe, Method m, Object obj);
    public abstract void registerHelp();

}
