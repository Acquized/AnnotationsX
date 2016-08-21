/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.command;

import cc.acquized.annotationsx.AnnotationsX;
import cc.acquized.annotationsx.api.Service;
import cc.acquized.annotationsx.command.annotations.Command;
import cc.acquized.annotationsx.command.extension.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicComparator;
import org.bukkit.help.IndexHelpTopic;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public final class CommandFramework extends CommandService implements CommandExecutor {

    private final Map<String, Map.Entry<Method, Object>> commandMap = new HashMap<>();
    private CommandMap map;

    /**
     * Initalizes the CommandFramework and gets the CommandMap from the SimplePluginManager
     * Call this in your onEnable();
     */
    public CommandFramework() {
        if(AnnotationsX.getParent().getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) AnnotationsX.getParent().getServer().getPluginManager();
            try {
                Field f = SimplePluginManager.class.getDeclaredField("commandMap");
                f.setAccessible(true);
                map = (CommandMap) f.get(manager);
            } catch (NoSuchFieldException | IllegalAccessException ex) {
                throw new CommandException("Could not get CommandService Map from SimplePluginManager.", ex);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return handle(commandSender, command, s, strings);
    }

    @Override
    public boolean handle(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        for(int i = args.length; i >= 0; i--) {
            StringBuilder builder = new StringBuilder();
            builder.append(label.toLowerCase());
            for(int x = 0; x < i; x++) {
                builder.append(".").append(args[x].toLowerCase());
            }
            String cmdLabel = builder.toString();
            if(commandMap.containsKey(cmdLabel)) {
                Method m = commandMap.get(cmdLabel).getKey();
                Object obj = commandMap.get(cmdLabel).getValue();
                Command command = m.getAnnotation(Command.class);
                if((!command.permission().isEmpty()) && (!sender.hasPermission(command.permission()))) {
                    sender.sendMessage(command.noPermissions());
                    return true;
                }
                if((command.inGameOnly()) && !(sender instanceof Player)) {
                    sender.sendMessage(command.notInGame());
                    return true;
                }
                try {
                    m.invoke(obj, command.inGameOnly() ? ((Player)sender) : sender, args);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    throw new CommandException("Could not execute CommandService '" + label + "'.", ex);
                }
                return true;
            }
        }
        sender.sendMessage("§4§lERROR: §cCommand " + label + " is not handeled! Oh nooooooo...");
        return true;
    }

    /**
     * Registers all Commands in the Class passed with the <code>obj</code> Argument
     * @param obj Class Instance of the CommandService Class - use either <code>this</code> or <code>new CommandClassInstance()</code>
     */
    @Override
    public void registerCommands(Object obj) {
        Class<?> clazz = obj.getClass();
        if(clazz.isAssignableFrom(Service.class)) {
            for(Method m : clazz.getMethods()) {
                if(m.getAnnotation(Command.class) != null) {
                    Command c = m.getAnnotation(Command.class);
                    boolean valid = false;
                    if((m.getParameterTypes().length == 2) && (m.getParameterTypes()[1] == String[].class)) {
                        if((c.inGameOnly()) && (m.getParameterTypes()[0] == Player.class)) {
                            valid = true;
                        }
                        if((!c.inGameOnly()) && (m.getParameterTypes()[0] == CommandSender.class)) {
                            valid = true;
                        }
                    }
                    if(valid) {
                        registerCommand(c, c.name(), m, obj);
                        for(String alias : c.aliases()) {
                            registerCommand(c, alias, m, obj);
                        }
                    } else {
                        throw new CommandException("The method '" + m.getName() + "' doesn't has the right Arguments! " +
                                "Required Args: 2 - " + (c.inGameOnly() ? "Player" : "CommandSender") + ", String[]");
                    }
                }
            }
        } else {
            throw new CommandException("The Class '" + clazz.getName() + "' does not extend 'Service<CommandsService>'.");
        }
    }

    @Override
    public void registerCommand(Command c, String label, Method m, Object o) {
        commandMap.put(label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, o));
        commandMap.put(AnnotationsX.getParent().getName() + ":" + label.toLowerCase(), new AbstractMap.SimpleEntry<>(m, o));
        String cmdLabel = label.split(Pattern.quote("."))[0].toLowerCase();
        if(map.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command cmd = new CommandExecutioner(cmdLabel, this);
            map.register(AnnotationsX.getParent().getName(), cmd);
        }
        if((!c.description().isEmpty()) && (cmdLabel.equals(label))) {
            map.getCommand(cmdLabel).setDescription(c.description());
        }
        if((!c.usage().isEmpty()) && (cmdLabel.equals(label))) {
            map.getCommand(cmdLabel).setUsage(c.usage());
        }
    }

    /**
     * Registers all Commands from the Plugin to /help
     * Call this after all Commands has been registered.
     */
    @Override
    public void registerHelp() {
        Set<HelpTopic> topics = new TreeSet<>(HelpTopicComparator.helpTopicComparatorInstance());
        for(String cmd : commandMap.keySet()) {
            if(!cmd.contains(".")) {
                org.bukkit.command.Command bCmd = map.getCommand(cmd);
                HelpTopic help = new GenericCommandHelpTopic(bCmd);
                topics.add(help);
            }
        }
        IndexHelpTopic topic = new IndexHelpTopic(AnnotationsX.getParent().getName(), "All commands for " + AnnotationsX.getParent().getName(),
                null, topics, "Below is a list of all " + AnnotationsX.getParent().getName() + " commands:");
        Bukkit.getServer().getHelpMap().addTopic(topic);
    }

}
