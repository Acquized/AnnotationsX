/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.reflections;

import cc.acquized.annotationsx.api.Service;
import cc.acquized.annotationsx.reflections.annotations.Resolver;
import cc.acquized.annotationsx.reflections.extension.ReflectionService;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.regex.Pattern;

public final class Reflections extends ReflectionService {

    @Getter private static Reflections reflections;

    @Resolver(clazz = "PlayerConnection", target = Resolve.NMS)
    public Class<?> playerConnection;

    @Resolver(clazz = "Packet", target = Resolve.NMS)
    public Class<?> packet;

    @Resolver(clazz = "entity.CraftPlayer", target = Resolve.OCB)
    public Class<?> craftPlayer;

    public Reflections() {
        try {
            parse(this);
        } catch (IllegalAccessException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void parse(Object loader) throws IllegalAccessException, ClassNotFoundException {
        Validate.notNull(loader, "The Class Loader is null.");
        Class<?> clazz = loader.getClass();
        if(clazz.isAssignableFrom(Service.class)) {
            for(Field f : clazz.getDeclaredFields()) {
                Resolver resolver = f.getAnnotation(Resolver.class);
                if(resolver != null) {
                    f.setAccessible(true);
                    Class<?> target = null;
                    if((resolver.target() == Resolve.NMS) || (resolver.target() == Resolve.OCB)) {
                        String version = Bukkit.getServer().getClass().getPackage().getName().split(Pattern.quote("."))[3];
                        target = Class.forName(resolver.target().getPackage() + "." + version + "." + resolver.clazz());
                    }
                    if(target != null) {
                        if(!Modifier.isFinal(f.getModifiers())) {
                            f.set(loader, target);
                        }
                    } else {
                        throw new ClassNotFoundException("Could not find class " + resolver.target().getPackage() + ".XXXX." + resolver.clazz());
                    }
                }
            }
        } else {
            throw new IllegalAccessException("The Class does not extend Service<ReflectionService>.");
        }
    }

    public void sendPacket(Class<?> packet, Player p) throws NoSuchMethodException, IllegalAccessException, NoSuchFieldException, InvocationTargetException {
        playerConnection.getMethod("sendPacket", packet).invoke(getConnection(p), packet);
    }

    public Object getConnection(Player p) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, NoSuchFieldException {
        Method handle = p.getClass().getMethod("getHandle");
        Object nmsPlayer = handle.invoke(p);
        return nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
    }

    public Object getCraftPlayer(Player p) {
        return craftPlayer.cast(p);
    }

    public int getProtocol(Player p) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Object playerConnection = getConnection(p);
        Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
        return (int) networkManager.getClass().getMethod("getVersion").invoke(networkManager);
    }

    static {
        reflections = new Reflections();
    }

}
