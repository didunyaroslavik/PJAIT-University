import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Function;

public class ReflectionCLI {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Incorrect number of parameters. Usage: java ReflectionCLI <path to .class file>");
            return;
        }

        File classFile = new File(args[0]);
        String className = classFile.getName().replace(".class", "");
        File parentDir = classFile.getAbsoluteFile().getParentFile();

        try {
            URLClassLoader classLoader = new URLClassLoader(new URL[]{parentDir.toURI().toURL()});
            Class<?> loadedClass = classLoader.loadClass(className);
            analyzeClass(loadedClass);
        } catch (Exception | NoClassDefFoundError e) {
            System.out.println("Could not load class " + className + ": " + e);
        }
    }

    private static String getParameterString(Class<?>[] paramTypes) {
        if (paramTypes.length == 0) {
            return "";
        }
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < paramTypes.length; i++) {
            if (i > 0) {
                params.append(", ");
            }
            params.append(paramTypes[i].getSimpleName());
        }
        return params.toString();
    }

    static Object[] createFieldRow(Field field) {
        return new Object[]{
                "Field",
                Modifier.toString(field.getModifiers()),
                field.getName(),
                "Type:" + field.getType().getSimpleName()
        };
    }

    static Object[] createConstructorRow(Constructor<?> constructor) {
        return new Object[]{
                "Constructor",
                Modifier.toString(constructor.getModifiers()),
                constructor.getDeclaringClass().getSimpleName(),
                "(" + getParameterString(constructor.getParameterTypes()) + ")"
        };
    }

    static Object[] createMethodRow(Method method) {
        return new Object[]{
                "Method",
                Modifier.toString(method.getModifiers()),
                method.getName(),
                method.getReturnType().getSimpleName() + "(" + getParameterString(method.getParameterTypes()) + ")"
        };
    }

    static Object[] createInterfaceRow(Class<?> iface) {
        return new Object[]{
                "Interface",
                "",
                iface.getSimpleName(),
                iface.getName()
        };
    }

    static <T> void showReflectionElements(T[] elements, String sectionTitle, Function<T, Object[]> rowCreator) {
        if (elements != null && elements.length > 0) {
            System.out.println(sectionTitle);
            for (T element : elements) {
                System.out.println(Arrays.toString(rowCreator.apply(element)));
            }
            System.out.println();
        }
    }

    static void analyzeClass(Class<?> clazz) {
        showReflectionElements(clazz.getDeclaredFields(), "FIELDS", ReflectionCLI::createFieldRow);
        showReflectionElements(clazz.getDeclaredConstructors(), "CONSTRUCTORS", ReflectionCLI::createConstructorRow);
        showReflectionElements(clazz.getDeclaredMethods(), "METHODS", ReflectionCLI::createMethodRow);
        showReflectionElements(clazz.getInterfaces(), "INTERFACES", ReflectionCLI::createInterfaceRow);
    }
}
