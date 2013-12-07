package openperipheral.addons.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import openperipheral.TypeConversionRegistry;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

public final class CCUtils {
	public static Object[] wrap(Object... args) {
		return args;
	}

	public static Object[] convertArgs(Class<?> needed[], Object args[]) {
		if (needed.length != args.length) return null;

		List<Object> conventedArgs = Lists.newArrayList();
		for (int i = 0; i < needed.length; i++) {
			Object converted = TypeConversionRegistry.fromLua(args[i], needed[i]);
			if (converted == null) return null;
			conventedArgs.add(converted);
		}

		return conventedArgs.toArray();
	}

	public static Object callSelfMethod(Object target, String methodName, Object[] arguments) {
		for (Method method : target.getClass().getMethods()) {
			if (!methodName.equals(method.getName())) continue;
			Object[] converted = convertArgs(method.getParameterTypes(), arguments);
			if (converted == null) continue;

			try {
				Object v = method.invoke(target, converted);
				return TypeConversionRegistry.toLua(v);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}

		throw new RuntimeException("Method " + methodName + " not found");
	}

	@SuppressWarnings("unchecked")
	public static <T> T callConstructor(Class<? extends T> klazz, Object[] arguments) {
		for (Constructor<?> ctor : klazz.getConstructors()) {
			Object[] converted = convertArgs(ctor.getParameterTypes(), arguments);
			if (converted == null) continue;

			try {
				return (T)ctor.newInstance(converted);
			} catch (Exception e) {
				throw Throwables.propagate(e);
			}
		}

		throw new RuntimeException("No valid constructor found");
	}
}