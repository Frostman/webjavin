package ru.frostman.mvc.classloading;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.frostman.mvc.Frosty;
import ru.frostman.mvc.classloading.enhance.Enhancer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author slukjanov aka Frostman
 */
public class FrostyClasses {
    private static final Logger log = LoggerFactory.getLogger(FrostyClass.class);

    /**
     * All application classes stored by name
     */
    private Map<String, FrostyClass> classes = Maps.newLinkedHashMap();

    /**
     * Current class loader instance
     */
    private FrostyClassLoader classLoader;

    public FrostyClasses() {
        update();
    }

    /**
     * Scan class path for classes in base packages and iff class added,
     * changed or removed than new ClassLoader will be created.
     *
     * @return true iff class loader changed
     */
    public boolean update() {
        //todo check that update runs not each 10 ms, synchronize it staticly

        boolean needReload = false;

        List<ClassFile> foundClasses = ClassPathUtil.findClassFiles(Frosty.getBasePackages());

        Set<String> existedClassNames = Sets.newHashSet(classes.keySet());
        Set<String> foundClassNames = Sets.newHashSet();
        for (ClassFile classFile : foundClasses) {
            final String className = classFile.getClassName();
            foundClassNames.add(className);

            if (!classes.containsKey(className)) {
                // new class added

                needReload = true;

                long lastModified = classFile.getLastModified();
                FrostyClass newClass = new FrostyClass();
                newClass.setName(className);
                newClass.setLastLoaded(lastModified);
                newClass.setHashCode(classFile.getHashCode());
                newClass.setBytecode(classFile.getBytes());
                classes.put(className, newClass);

                Enhancer.enhance(newClass);

                log.debug("Application class added: {}", className);
            } else {
                // existed class
                final FrostyClass existedClass = classes.get(className);

                final String hashCode = classFile.getHashCode();
                if (existedClass.getLastLoaded() < classFile.getLastModified()
                        && (!existedClass.getHashCode().equals(hashCode))) {
                    //class changed

                    needReload = true;

                    long lastModified = classFile.getLastModified();
                    FrostyClass changedClass = new FrostyClass();
                    changedClass.setName(className);
                    changedClass.setLastLoaded(lastModified);
                    changedClass.setHashCode(hashCode);
                    changedClass.setBytecode(classFile.getBytes());
                    classes.put(className, changedClass);

                    Enhancer.enhance(changedClass);

                    log.debug("Application class changed: {}", className);
                }
            }
        }

        existedClassNames.removeAll(foundClassNames);

        for (String removedClassName : existedClassNames) {
            classes.remove(removedClassName);

            needReload = true;

            log.debug("Application class removed: {}", removedClassName);
        }

        if (needReload) {
            FrostyClassLoader newClassLoader = new FrostyClassLoader(ImmutableMap.copyOf(classes));
            newClassLoader.loadAllClasses();
            this.classLoader = newClassLoader;

            log.info("Application classes successfully reloaded");
        }

        return needReload;
    }

    public FrostyClassLoader getClassLoader() {
        return classLoader;
    }
}
