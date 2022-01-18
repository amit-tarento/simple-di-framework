package org.practice.dif;

import org.practice.Config;
import org.practice.dif.annotation.Component;
import org.practice.dif.annotation.Configuration;
import org.practice.dif.annotation.Inject;
import org.practice.dif.annotation.Repository;
import org.practice.dif.annotation.Service;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationContext {

    private Map<Class<?>, Object> container = new HashMap<>();

    public ApplicationContext(Class<Config> configClass) throws Exception {
        init(configClass);
    }

    public <T> T getBean(Class<T> clss) {
        return (T) container.get(clss);
    }

    private void init(Class<?> configClass) throws Exception {
        if (!configClass.isAnnotationPresent(Configuration.class)) {
            throw new RuntimeException("This class is not config class. Please annotate this class with @Configuration.");
        } else {
            String packageName = configClass.getPackageName();
            Class[] classArray = getClasses(packageName);
            for (Class clss : classArray) {
                if (clss.isAnnotationPresent(Component.class) || clss.isAnnotationPresent(Repository.class) || clss.isAnnotationPresent(Service.class)) {
                    Constructor constructor = clss.getConstructor();
                    constructor.setAccessible(true);
                    Object object = constructor.newInstance();
                    container.put(clss, object);
                }
            }
            for (Class clss : container.keySet()) {
                injectBean(clss);
            }
        }
    }

    private void injectBean(Class clss) throws Exception {
        Field []declaredFields = clss.getDeclaredFields();
        for (Field f : declaredFields) {
            if (f.isAnnotationPresent(Inject.class)) {
                f.setAccessible(true);
                Class clazzType = f.getType();
                Object fieldValue = container.get(clazzType);
                f.set(container.get(clss), fieldValue);
                Field[] fArray = clazzType.getDeclaredFields();
                for (Field field : fArray) {
                    injectBean(field.getClass());
                }
            }
        }
    }


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws Exception
     */
    private static Class[] getClasses(String packageName)
            throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().replace(".class", "")));
            }
        }
        return classes;
    }

}
