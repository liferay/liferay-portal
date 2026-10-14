/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal.util;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.MethodParameter;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Igor Spasic
 */
public class MethodParametersResolverUtil {

	public static MethodParameter[] resolveMethodParameters(Method method) {
		try {
			return _resolveMethodParameters(method.getDeclaringClass(), method);
		}
		catch (PortalException portalException) {
			_log.error("Unable to resolve method parameters", portalException);
		}

		return null;
	}

	private static MethodParameter[] _resolveMethodParameters(
			Class<?> clazz, Method method)
		throws PortalException {

		ClassLoader classLoader = clazz.getClassLoader();

		String resourcePath = StringUtil.replace(
			clazz.getName(), CharPool.PERIOD, CharPool.SLASH);

		resourcePath = resourcePath.concat(".class");

		InputStream inputStream = classLoader.getResourceAsStream(resourcePath);

		if (inputStream == null) {
			throw new IllegalArgumentException(
				"Class not found: " + clazz.getName());
		}

		ClassReader classReader = null;

		try {
			classReader = new ClassReader(inputStream);
		}
		catch (IOException ioException) {
			throw new PortalException(
				"Unable to read class from path " + resourcePath, ioException);
		}

		MethodParametersResolverClassVisitor
			methodParametersResolverClassVisitor =
				new MethodParametersResolverClassVisitor(classLoader, method);

		classReader.accept(
			methodParametersResolverClassVisitor, ClassReader.SKIP_FRAMES);

		List<MethodParameter> methodParameters =
			methodParametersResolverClassVisitor.getMethodParameters();

		return methodParameters.toArray(new MethodParameter[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		MethodParametersResolverUtil.class);

	private static class MethodParametersResolverClassVisitor
		extends ClassVisitor {

		public List<MethodParameter> getMethodParameters() {
			if (_methodParametersResolverMethodVisitor != null) {
				return _methodParametersResolverMethodVisitor.
					getMethodParameters();
			}

			return Collections.emptyList();
		}

		@Override
		public MethodVisitor visitMethod(
			int access, String name, String descriptor, String signature,
			String[] exceptions) {

			if (!name.equals(_method.getName()) ||
				!Objects.equals(
					Type.getMethodDescriptor(_method), descriptor)) {

				return null;
			}

			int parameterCount = _method.getParameterCount();

			if (!Modifier.isStatic(_method.getModifiers())) {
				parameterCount++;
			}

			Class<?>[] parameterTypes = _method.getParameterTypes();

			for (Class<?> parameterType : parameterTypes) {
				if (StringUtil.equalsIgnoreCase(
						parameterType.getName(), double.class.getName()) ||
					StringUtil.equalsIgnoreCase(
						parameterType.getName(), long.class.getName())) {

					parameterCount++;
				}
			}

			_methodParametersResolverMethodVisitor =
				new MethodParametersResolverMethodVisitor(
					_classLoader, _method, parameterCount);

			return _methodParametersResolverMethodVisitor;
		}

		protected MethodParametersResolverClassVisitor(
			ClassLoader classLoader, Method method) {

			super(Opcodes.ASM9);

			_classLoader = classLoader;
			_method = method;
		}

		private final ClassLoader _classLoader;
		private final Method _method;
		private MethodParametersResolverMethodVisitor
			_methodParametersResolverMethodVisitor;

	}

	private static class MethodParametersResolverMethodVisitor
		extends MethodVisitor {

		public List<MethodParameter> getMethodParameters() {
			return _methodParameters;
		}

		@Override
		public void visitLocalVariable(
			String name, String descriptor, String signature, Label startLabel,
			Label endLabel, int index) {

			if ((!Modifier.isStatic(_method.getModifiers()) && (index == 0)) ||
				(index >= _parameterCount)) {

				return;
			}

			if (signature != null) {
				descriptor = StringUtil.removeSubstring(signature, descriptor);
			}

			Class<?>[] parameterTypes = _method.getParameterTypes();

			_methodParameters.add(
				new MethodParameter(
					_classLoader, name, descriptor,
					parameterTypes[_methodParameters.size()]));
		}

		protected MethodParametersResolverMethodVisitor(
			ClassLoader classLoader, Method method, int parameterCount) {

			super(Opcodes.ASM9);

			_classLoader = classLoader;
			_method = method;
			_parameterCount = parameterCount;
		}

		private final ClassLoader _classLoader;
		private final Method _method;
		private final List<MethodParameter> _methodParameters =
			new ArrayList<>();
		private final int _parameterCount;

	}

}