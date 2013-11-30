package openperipheral.addons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import openperipheral.TypeConversionRegistry;

import com.google.common.collect.Lists;

public final class CCUtils {
	public static Object[] callSelfMethod(Object target, String methodName, Object[] arguments) throws IllegalAccessException, InvocationTargetException {
		METHOD_LOOP: for (Method method : target.getClass().getMethods()) {
			if (!methodName.equals(method.getName())) continue;
			Class<?>[] requiredParameters = method.getParameterTypes();
			if (requiredParameters.length != arguments.length) continue;

			List<Object> args = Lists.newArrayList();
			for (int i = 0; i < requiredParameters.length; i++) {
				Object converted = TypeConversionRegistry.fromLua(arguments[i], requiredParameters[i]);
				if (converted == null) continue METHOD_LOOP;
				args.add(converted);
			}

			Object v = method.invoke(target, args.toArray());
			return new Object[] { TypeConversionRegistry.toLua(v) };
		}

		throw new RuntimeException("Method " + methodName + " not found");
	}
}