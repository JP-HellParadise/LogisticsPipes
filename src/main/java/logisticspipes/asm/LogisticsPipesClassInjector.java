package logisticspipes.asm;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.io.BaseEncoding;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import logisticspipes.LogisticsPipes;
import logisticspipes.proxy.computers.wrapper.CCObjectWrapper;
import logisticspipes.proxy.opencomputers.asm.ClassCreator;
import logisticspipes.utils.ModStatusHelper;

public class LogisticsPipesClassInjector implements IClassTransformer {

	private Field fResourceCache;
	private Boolean isObfEnv = null;

	public LogisticsPipesClassInjector() throws NoSuchFieldException, SecurityException {
		fResourceCache = LaunchClassLoader.class.getDeclaredField("resourceCache");
		fResourceCache.setAccessible(true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		try {
			if (name.startsWith("logisticspipes.proxy.opencomputers.asm.BaseWrapperClass$") && name.endsWith("$OpenComputersWrapper")) {
				String correctName = name.substring(56, name.length() - 21);
				Class<?> clazz = Launch.classLoader.findClass(correctName);
				bytes = ClassCreator.getWrappedClassAsBytes(CCObjectWrapper.getWrapperInformation(clazz), clazz.getName());
				Set<String> set = new TreeSet<>();
				set.add(name);
				Launch.classLoader.clearNegativeEntries(set);
				Map<String, byte[]> map = (Map<String, byte[]>) fResourceCache.get(Launch.classLoader);
				map.put(name, bytes);
				return bytes;
			}
		} catch (Exception e) {
			if (LogisticsPipes.isDEBUG()) { // For better Debugging
				e.printStackTrace();
				return bytes;
			}
			throw new RuntimeException(e);
		}
		return bytes;
	}
}
