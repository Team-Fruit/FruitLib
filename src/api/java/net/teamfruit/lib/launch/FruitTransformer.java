package net.teamfruit.lib.launch;

import static net.teamfruit.lib.reflect.ReflectionUtil.*;

import java.io.File;
import java.io.InputStream;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.teamfruit.lib.Log;
import net.teamfruit.lib.loader.PathFile;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.teamfruit.lib.reflect.ReflectionUtil._Field;
import net.teamfruit.lib.reflect.ReflectionUtil._Method;

public class FruitTransformer {
    public static final FruitTransformer instance = new FruitTransformer();

    public final List<IClassTransformer> delegatedTransformers = Lists.newArrayList();
    private final _Method m$defineClass = __pmethod(ClassLoader.class, $("defineClass"), String.class, byte[].class, Integer.TYPE, Integer.TYPE);
    private final _Field f$cachedClasses = __pfield(LaunchClassLoader.class, $("cachedClasses"));

    private FruitTransformer() {
    }

    public void addTransformerName(final String transformer, final File file) {
        Log.log.debug("Adding FruitTransformer: " + transformer);
        PathFile pathFile = null;
        try {
            pathFile = PathFile.create(file);
            byte[] bytes;
            bytes = Launch.classLoader.getClassBytes(transformer);

            if (bytes == null) {
                final String resourceName = transformer.replace('.', '/') + ".class";
                final PathFile.PathEntry entry = pathFile.getEntry(resourceName);
                if (entry == null)
                    throw new Exception("Failed to add transformer: " + transformer + ". Entry not found in jar file " + file.getName());

                InputStream input = null;
                try {
                    bytes = IOUtils.toByteArray(input = entry.getInputStream());
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }

            defineDependancies(bytes, pathFile, Queues.<String>newArrayDeque());
            final Class<?> clazz = defineClass(transformer, bytes);

            if (!IClassTransformer.class.isAssignableFrom(clazz))
                throw new Exception("Failed to add transformer: " + transformer + " is not an instance of IClassTransformer");

            IClassTransformer classTransformer;
            try {
                classTransformer = _pconstructor(clazz, File.class).$new(file);
            } catch (final NoSuchMethodException nsme) {
                classTransformer = $new(clazz);
            }
            this.delegatedTransformers.add(classTransformer);
        } catch (final Exception e) {
            Log.log.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(pathFile);
        }
    }

    public void addTransformerClass(final Class<?> transformer, final File file) {
        Log.log.debug("Adding FruitTransformer: " + transformer.getName());
        try {
            IClassTransformer classTransformer;
            try {
                classTransformer = _pconstructor(transformer, File.class).$new(file);
            } catch (final NoSuchMethodException nsme) {
                classTransformer = $new(transformer);
            }
            this.delegatedTransformers.add(classTransformer);
        } catch (final Exception e) {
            Log.log.error(e.getMessage(), e);
        }
    }

    public void addTransformerObject(final IClassTransformer classTransformer) {
        Log.log.debug("Adding FruitTransformer: " + classTransformer.getClass().getName());
        this.delegatedTransformers.add(classTransformer);
    }

    private void defineDependancies(final byte[] bytes, final PathFile path, final Deque<String> depStack) throws Exception {
        final ClassReader reader = new ClassReader(bytes);
        final DependencyDiscoverer lister = new DependencyDiscoverer(Opcodes.ASM5);
        reader.accept(lister, 0);

        depStack.push(reader.getClassName());

        for (final String dependancy : lister.getDependancies()) {
            if (depStack.contains(dependancy))
                continue;

            try {
                Launch.classLoader.loadClass(dependancy.replace('/', '.'));
            } catch (final ClassNotFoundException cnfe) {
                final PathFile.PathEntry entry = path.getEntry(dependancy + ".class");
                if (entry == null)
                    throw new Exception("Dependency " + dependancy + " not found in jar file " + path.getFile().getName());

                final byte[] depbytes;
                InputStream input = null;
                try {
                    depbytes = IOUtils.toByteArray(input = entry.getInputStream());
                } finally {
                    IOUtils.closeQuietly(input);
                }
                defineDependancies(depbytes, path, depStack);

                Log.log.debug("Defining dependancy: " + dependancy);

                defineClass(dependancy.replace('/', '.'), depbytes);
            }
        }

        depStack.pop();
    }

    private Class<?> defineClass(final String classname, final byte[] bytes) throws Exception {
        final Class<?> clazz = this.m$defineClass.$invoke(Launch.classLoader, classname, bytes, 0, bytes.length);
        this.f$cachedClasses.<Map<String, Class<?>>>$get(Launch.classLoader).put(classname, clazz);
        return clazz;
    }

    public static class DependencyDiscoverer extends ClassVisitor {
        private static Pattern classdesc = Pattern.compile("L(.+?);");

        private Set<String> dependancies = Sets.newHashSet();

        private void depend(final String classname) {
            this.dependancies.add(classname);
        }

        private void dependDesc(final String desc) {
            final Matcher match = classdesc.matcher(desc);
            while (match.find()) {
                final String s = match.group();
                depend(s.substring(1, s.length() - 1));
            }
        }

        private class DependancyMethodLister extends MethodVisitor {
            public DependancyMethodLister(final int api) {
                super(api);
            }

            @Override
            public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
                dependDesc(desc);
            }

            @Override
            public void visitLocalVariable(final String name, final String desc, final String signature, final Label start, final Label end, final int index) {
                dependDesc(desc);
            }

            @Override
            public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, boolean itf) {
                depend(owner);
                dependDesc(desc);
            }
        }

        public DependencyDiscoverer(final int api) {
            super(api);
        }

        @Override
        public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
            dependDesc(desc);
            return null;
        }

        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            dependDesc(desc);
            return new DependancyMethodLister(Opcodes.ASM5);
        }

        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            depend(superName);
            if (interfaces != null)
                for (final String interfacename : interfaces)
                    depend(interfacename);
        }

        public List<String> getDependancies() {
            return Lists.newArrayList(this.dependancies);
        }
    }
}
