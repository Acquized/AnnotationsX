/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.command;

import cc.acquized.annotationsx.AnnotationsX;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

/**
 * Intern used by the Library to register Command to Bukkit's CommandMap
 * Do not use or initalize.
 */
public class CommandExecutioner extends Command {

    private CommandExecutor executor;

    protected CommandExecutioner(String name, CommandExecutor executor) {
        super(name);
        this.executor = executor;
        usageMessage = "";
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        boolean success;
        if((AnnotationsX.getParent() == null) || (!AnnotationsX.getParent().isEnabled())) {
            return false;
        }
        if(!testPermission(sender)) {
            return false;
        }
        try {
            success = executor.onCommand(sender, this, label, args);
        } catch (Exception ex) {
            throw new CommandException("Unhandeld exception in command '" + label + "' in Plugin " + AnnotationsX.getParent().getName(), ex);
        }
        if((!success) && (usageMessage.length() > 0)) {
            for(String l : usageMessage.split("\n")) {
                sender.sendMessage(l.replaceAll(Pattern.quote("<command>"), label));
            }
        }
        return success;
    }

}
