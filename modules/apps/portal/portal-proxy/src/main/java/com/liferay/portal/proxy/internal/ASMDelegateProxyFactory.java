/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.proxy.internal;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.DelegateProxyFactory;
import com.liferay.portal.kernel.util.StringUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import org.osgi.service.component.annotations.Component;

/**
 * @author Shuyang Zhou
 */
@Component(service = DelegateProxyFactory.class)
public class ASMDelegateProxyFactory implements DelegateProxyFactory {

	@Override
	public <T> T newDelegateProxyInstance(
		ClassLoader classLoader, Class<T> interfaceClass, Object delegateObject,
		T defaultObject) {

		if (!interfaceClass.isInterface()) {
			throw new IllegalArgumentException(
				interfaceClass + " is not an interface");
		}

		Class<?> clazz = delegateObject.getClass();

		Package pkg = clazz.getPackage();

		String asmWrapperClassName = StringBundler.concat(
			pkg.getName(), StringPool.PERIOD, interfaceClass.getSimpleName(),
			"ASMWrapper");

		Map<String, Class<?>> classReferences = _classReferencesMap.get(
			classLoader);

		if (classReferences == null) {
			classReferences = new ConcurrentHashMap<>();

			Map<String, Class<?>> previousClassReferences =
				_classReferencesMap.putIfAbsent(classLoader, classReferences);

			if (previousClassReferences != null) {
				classReferences = previousClassReferences;
			}
		}

		Class<?> asmWrapperClass = classReferences.get(asmWrapperClassName);

		if (asmWrapperClass == null) {
			asmWrapperClass = _loadClass(
				classLoader, asmWrapperClassName, interfaceClass,
				delegateObject);

			classReferences.put(asmWrapperClassName, asmWrapperClass);
		}

		try {
			Constructor<?> constructor = asmWrapperClass.getDeclaredConstructor(
				delegateObject.getClass(), interfaceClass);

			constructor.setAccessible(true);

			return (T)constructor.newInstance(delegateObject, defaultObject);
		}
		catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	private <T> byte[] _generateASMWrapperClassData(
		String asmWrapperClassBinaryName, Class<T> interfaceClass,
		Object delegateObject) {

		String interfaceClassBinaryName = _getClassBinaryName(interfaceClass);
		String interfaceClassDescriptor = Type.getDescriptor(interfaceClass);

		Class<?> delegateObjectClass = delegateObject.getClass();

		String delegateObjectClassDescriptor = Type.getDescriptor(
			delegateObjectClass);

		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

		classWriter.visit(
			Opcodes.V1_7, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
			asmWrapperClassBinaryName, null, _getClassBinaryName(Object.class),
			new String[] {interfaceClassBinaryName});

		// Fields

		FieldVisitor fieldVisitor = classWriter.visitField(
			Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "_delegate",
			delegateObjectClassDescriptor, null, null);

		fieldVisitor.visitEnd();

		fieldVisitor = classWriter.visitField(
			Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL, "_default",
			interfaceClassDescriptor, null, null);

		fieldVisitor.visitEnd();

		// Constructor

		MethodVisitor methodVisitor = classWriter.visitMethod(
			Opcodes.ACC_PRIVATE, "<init>",
			Type.getMethodDescriptor(
				Type.VOID_TYPE, Type.getType(delegateObjectClass),
				Type.getType(interfaceClassDescriptor)),
			null, null);

		methodVisitor.visitCode();

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitMethodInsn(
			Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD, asmWrapperClassBinaryName, "_delegate",
			delegateObjectClassDescriptor);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);

		methodVisitor.visitFieldInsn(
			Opcodes.PUTFIELD, asmWrapperClassBinaryName, "_default",
			interfaceClassDescriptor);

		methodVisitor.visitInsn(Opcodes.RETURN);

		methodVisitor.visitMaxs(0, 0);

		methodVisitor.visitEnd();

		// Delegate and fallback methods

		for (Method method : interfaceClass.getMethods()) {
			try {
				Method delegateMethod = delegateObjectClass.getMethod(
					method.getName(), method.getParameterTypes());

				_generateMethod(
					classWriter, delegateMethod, asmWrapperClassBinaryName,
					"_delegate", delegateObjectClassDescriptor,
					_getClassBinaryName(delegateObjectClass),
					Opcodes.INVOKEVIRTUAL);
			}
			catch (NoSuchMethodException noSuchMethodException) {
				_generateMethod(
					classWriter, method, asmWrapperClassBinaryName, "_default",
					interfaceClassDescriptor, interfaceClassBinaryName,
					Opcodes.INVOKEINTERFACE);
			}
		}

		_generateMethod(
			classWriter, _equalsMethod, asmWrapperClassBinaryName, "_delegate",
			delegateObjectClassDescriptor,
			_getClassBinaryName(delegateObjectClass), Opcodes.INVOKEVIRTUAL);
		_generateMethod(
			classWriter, _hashCodeMethod, asmWrapperClassBinaryName,
			"_delegate", delegateObjectClassDescriptor,
			_getClassBinaryName(delegateObjectClass), Opcodes.INVOKEVIRTUAL);
		_generateMethod(
			classWriter, _toStringMethod, asmWrapperClassBinaryName,
			"_delegate", delegateObjectClassDescriptor,
			_getClassBinaryName(delegateObjectClass), Opcodes.INVOKEVIRTUAL);

		classWriter.visitEnd();

		return classWriter.toByteArray();
	}

	private void _generateMethod(
		ClassWriter classWriter, Method method,
		String asmWrapperClassBinaryName, String fieldName,
		String targetClassDescriptor, String targetClassBinaryName,
		int opcode) {

		Class<?>[] exceptions = method.getExceptionTypes();

		String[] exceptionsBinaryClassNames = new String[exceptions.length];

		for (int i = 0; i < exceptions.length; i++) {
			exceptionsBinaryClassNames[i] = _getClassBinaryName(exceptions[i]);
		}

		MethodVisitor methodVisitor = classWriter.visitMethod(
			Opcodes.ACC_PUBLIC, method.getName(),
			Type.getMethodDescriptor(method), null, exceptionsBinaryClassNames);

		methodVisitor.visitCode();

		methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);

		methodVisitor.visitFieldInsn(
			Opcodes.GETFIELD, asmWrapperClassBinaryName, fieldName,
			targetClassDescriptor);

		int i = 1;

		for (Class<?> parameterClass : method.getParameterTypes()) {
			Type type = Type.getType(parameterClass);

			methodVisitor.visitVarInsn(type.getOpcode(Opcodes.ILOAD), i);

			i += type.getSize();
		}

		methodVisitor.visitMethodInsn(
			opcode, targetClassBinaryName, method.getName(),
			Type.getMethodDescriptor(method),
			opcode == Opcodes.INVOKEINTERFACE);

		Type type = Type.getType(method.getReturnType());

		methodVisitor.visitInsn(type.getOpcode(Opcodes.IRETURN));

		methodVisitor.visitMaxs(0, 0);

		methodVisitor.visitEnd();
	}

	private String _getClassBinaryName(Class<?> clazz) {
		String className = clazz.getName();

		return StringUtil.replace(className, '.', '/');
	}

	private Class<?> _loadClass(
		ClassLoader classLoader, String asmWrapperClassName,
		Class<?> interfaceClass, Object delegateObject) {

		synchronized (classLoader) {
			try {
				return classLoader.loadClass(asmWrapperClassName);
			}
			catch (ClassNotFoundException classNotFoundException) {
				try {
					byte[] classData = _generateASMWrapperClassData(
						StringUtil.replace(asmWrapperClassName, '.', '/'),
						interfaceClass, delegateObject);

					return (Class<?>)_defineClassMethod.invoke(
						classLoader, asmWrapperClassName, classData, 0,
						classData.length);
				}
				catch (Throwable throwable) {
					throw new RuntimeException(throwable);
				}
			}
		}
	}

	private static final Map<ClassLoader, Map<String, Class<?>>>
		_classReferencesMap = new ConcurrentReferenceKeyHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);
	private static final Method _defineClassMethod =
		ReflectionUtil.fetchDeclaredMethod(
			ClassLoader.class, "defineClass", String.class, byte[].class,
			int.class, int.class);
	private static final Method _equalsMethod =
		ReflectionUtil.fetchDeclaredMethod(
			Object.class, "equals", Object.class);
	private static final Method _hashCodeMethod =
		ReflectionUtil.fetchDeclaredMethod(Object.class, "hashCode");
	private static final Method _toStringMethod =
		ReflectionUtil.fetchDeclaredMethod(Object.class, "toString");

}