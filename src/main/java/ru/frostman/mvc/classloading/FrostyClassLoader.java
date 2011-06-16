package ru.frostman.mvc.classloading;

import ru.frostman.mvc.Frosty;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyClassLoader extends ClassLoader {
    /**
     * FrostyClassLoader instances counter
     */
    private static final AtomicLong LOADERS_COUNT = new AtomicLong();

    /**
     * Current instance id
     */
    private final long id = LOADERS_COUNT.getAndIncrement();

    /**
     * All application classes stored by name
     */
    private final Map<String, FrostyClass> classes;

    /**
     * Protection domain will be applied to all loaded classes
     */
    private ProtectionDomain protectionDomain;

    public FrostyClassLoader(Map<String, FrostyClass> classes) {
        super(Frosty.class.getClassLoader());

        this.classes = classes;

        try {
            CodeSource codeSource = new CodeSource(new URL("file:" + Frosty.getApplicationPath()), (Certificate[]) null);
            Permissions permissions = new Permissions();
            permissions.add(new AllPermission());
            protectionDomain = new ProtectionDomain(codeSource, permissions);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadAllClasses() {

    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        // Iff class is already loaded, return it
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
            return clazz;
        }

        // Try to load application class
        clazz = loadApplicationClass(name);
        if (clazz != null) {
            if (resolve) {
                resolveClass(clazz);
            }

            return clazz;
        }

        // Load class by parent classloader
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    public long getId() {
        return id;
    }

    public static long getLoadersCount() {
        return LOADERS_COUNT.get();
    }
}
