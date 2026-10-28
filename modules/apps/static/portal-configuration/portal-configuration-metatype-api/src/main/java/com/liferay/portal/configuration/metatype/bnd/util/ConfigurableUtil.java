/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.metatype.bnd.util;

import aQute.bnd.annotation.metatype.Configurable;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.configuration.persistence.ConfigurationOverridePropertiesUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @author Shuyang Zhou
 */
public class ConfigurableUtil {

	public static <T> T createConfigurable(
		Class<T> clazz, Dictionary<?, ?> properties) {

		return _createConfigurableSnapshot(
			clazz,
			Configurable.createConfigurable(
				clazz, _overrideDictionary(clazz, properties)));
	}

	public static <T> T createConfigurable(
		Class<T> clazz, Map<?, ?> properties) {

		return _createConfigurableSnapshot(
			clazz,
			Configurable.createConfigurable(
				clazz, _overrideMap(clazz, properties)));
	}

	private static <T> T _createConfigurableSnapshot(
		Class<T> interfaceClass, T configurable) {

		String interfaceClassName = interfaceClass.getName();

		String snapshotClassName = interfaceClassName.concat("Snapshot");

		try {
			ClassLoader classLoader = interfaceClass.getClassLoader();

			Class<T> snapshotClass = (Class<T>)_findLoadedClassMethod.invoke(
				classLoader, snapshotClassName);

			if (snapshotClass == null) {
				byte[] snapshotClassData = _generateSnapshotClassData(
					interfaceClass, snapshotClassName);

				try {
					snapshotClass = (Class<T>)_defineClassMethod.invoke(
						classLoader, snapshotClassName, snapshotClassData, 0,
						snapshotClassData.length);
				}
				catch (InvocationTargetException invocationTargetException) {
					snapshotClass = (Class<T>)classLoader.loadClass(
						snapshotClassName);
				}
			}

			Constructor<T> snapshotClassConstructor =
				snapshotClass.getConstructor(interfaceClass);

			return snapshotClassConstructor.newInstance(configurable);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(
				"Unable to create snapshot class for " + interfaceClass,
				throwable);
		}
	}

	private static <T> byte[] _generateSnapshotClassData(
		Class<T> interfaceClass, String snapshotClassName) {

		String snapshotClassBinaryName = _getClassBinaryName(snapshotClassName);
		String objectClassBinaryName = _getClassBinaryName(
			Object.class.getName());

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classWriter.visit(
			Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
			snapshotClassBinaryName, null, objectClassBinaryName,
			new String[] {_getClassBinaryName(interfaceClass.getName())});

		List<Method> nonsyntheticDeclaredMethods = new ArrayList<>();

		for (Method method : interfaceClass.getDeclaredMethods()) {
			if (method.isSynthetic()) {
				continue;
			}

			nonsyntheticDeclaredMethods.add(method);
		}

		// Fields

		for (Method method : nonsyntheticDeclaredMethods) {
			FieldVisitor fieldVisitor = classWriter.visitField(
				Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, method.getName(),
				Type.getDescriptor(method.getReturnType()), null, null);

			fieldVisitor.visitEnd();
		}

		// Constructor

		MethodVisitor constructorMethodVisitor = classWriter.visitMethod(
			Opcodes.ACC_PUBLIC, "<init>",
			Type.getMethodDescriptor(
				Type.VOID_TYPE, Type.getType(interfaceClass)),
			null, null);

		constructorMethodVisitor.visitCode();

		constructorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
		constructorMethodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL, objectClassBinaryName, "<init>", "()V",
			false);

		for (Method method : nonsyntheticDeclaredMethods) {
			Class<?> returnType = method.getReturnType();

			constructorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
			constructorMethodVisitor.visitVarInsn(Opcodes.ALOAD, 1);

			String methodName = method.getName();

			constructorMethodVisitor.visitMethodInsn(
				Opcodes.INVOKEINTERFACE,
				_getClassBinaryName(interfaceClass.getName()), methodName,
				Type.getMethodDescriptor(method), true);

			constructorMethodVisitor.visitFieldInsn(
				Opcodes.PUTFIELD, snapshotClassBinaryName, methodName,
				Type.getDescriptor(returnType));
		}

		constructorMethodVisitor.visitInsn(Opcodes.RETURN);

		constructorMethodVisitor.visitMaxs(0, 0);

		constructorMethodVisitor.visitEnd();

		// Methods

		for (Method method : nonsyntheticDeclaredMethods) {
			String methodName = method.getName();
			Class<?> returnType = method.getReturnType();

			MethodVisitor methodVisitor = classWriter.visitMethod(
				Opcodes.ACC_PUBLIC, methodName,
				Type.getMethodDescriptor(method), null, null);

			methodVisitor.visitCode();

			method.setAccessible(true);

			methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

			methodVisitor.visitFieldInsn(
				Opcodes.GETFIELD, snapshotClassBinaryName, methodName,
				Type.getDescriptor(returnType));

			if (returnType.isPrimitive()) {
				Type returnValueType = Type.getType(returnType);

				methodVisitor.visitInsn(
					returnValueType.getOpcode(Opcodes.IRETURN));
			}
			else {
				methodVisitor.visitInsn(Opcodes.ARETURN);
			}

			methodVisitor.visitMaxs(0, 0);

			methodVisitor.visitEnd();
		}

		classWriter.visitEnd();

		return classWriter.toByteArray();
	}

	private static String _getClassBinaryName(String className) {
		return StringUtil.replace(
			className, CharPool.PERIOD, CharPool.FORWARD_SLASH);
	}

	private static Dictionary<?, ?> _overrideDictionary(
		Class<?> clazz, Dictionary<?, ?> properties) {

		Map<String, Object> overrideProperties =
			ConfigurationOverridePropertiesUtil.getOverrideProperties(
				clazz.getName());

		if (overrideProperties == null) {
			return properties;
		}

		Dictionary<Object, Object> overrideDictionary =
			new HashMapDictionary<>();

		Enumeration<?> enumeration = properties.keys();

		while (enumeration.hasMoreElements()) {
			Object key = enumeration.nextElement();

			overrideDictionary.put(key, properties.get(key));
		}

		overrideProperties.forEach(
			(key, value) -> overrideDictionary.put(key, value));

		return overrideDictionary;
	}

	private static Map<?, ?> _overrideMap(
		Class<?> clazz, Map<?, ?> properties) {

		Map<String, Object> overrideProperties =
			ConfigurationOverridePropertiesUtil.getOverrideProperties(
				clazz.getName());

		if (overrideProperties == null) {
			return properties;
		}

		return HashMapBuilder.create(
			(Map)properties
		).putAll(
			overrideProperties
		).build();
	}

	private static final Method _defineClassMethod =
		ReflectionUtil.fetchDeclaredMethod(
			ClassLoader.class, "defineClass", String.class, byte[].class,
			int.class, int.class);
	private static final Method _findLoadedClassMethod =
		ReflectionUtil.fetchDeclaredMethod(
			ClassLoader.class, "findLoadedClass", String.class);

}