/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.test.aspects;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayOutputStream;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.weaver.CrosscuttingMembersSet;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.tools.GeneratedClassHandler;
import org.aspectj.weaver.tools.WeavingAdaptor;

/**
 * @author Shuyang Zhou
 */
public class URLWeavingAdapter extends WeavingAdaptor {

	public URLWeavingAdapter(URL[] urls, Class<?>[] aspectClasses) {
		super(null, urls, new URL[0]);

		generatedClassHandler = new RecordGeneratedClassHandler();

		for (Class<?> aspectClass : aspectClasses) {
			_addAspectClass(aspectClass);
		}

		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

		ClassLoader platformClassLoader = systemClassLoader.getParent();

		bcelWorld.addTypeDelegateResolver(
			referenceType -> {
				try {
					return bcelWorld.buildBcelDelegate(
						referenceType,
						_classToJavaClass(
							platformClassLoader.loadClass(
								referenceType.getName())),
						false, false);
				}
				catch (Exception exception) {
					return null;
				}
			});

		weaver.prepareForWeave();
	}

	public byte[] removeGeneratedClassDate(String name) {
		return _generatedClasses.remove(name);
	}

	private void _addAspectClass(Class<?> aspectClass) {
		Class<?> currentClass = aspectClass;

		while (true) {
			Class<?>[] interfaceClasses = currentClass.getInterfaces();

			for (Class<?> interfaceClass : interfaceClasses) {
				JavaClass javaClass = _classToJavaClass(interfaceClass);

				if (javaClass == null) {
					continue;
				}

				bcelWorld.addSourceObjectType(javaClass, false);
			}

			currentClass = currentClass.getSuperclass();

			if (currentClass == null) {
				break;
			}

			JavaClass javaClass = _classToJavaClass(currentClass);

			if (javaClass == null) {
				continue;
			}

			bcelWorld.addSourceObjectType(javaClass, false);
		}

		JavaClass javaClass = _classToJavaClass(aspectClass);

		BcelObjectType bcelObjectType = bcelWorld.addSourceObjectType(
			javaClass, false);

		ResolvedType resolvedType = bcelObjectType.getResolvedTypeX();

		if (resolvedType.isAspect()) {
			CrosscuttingMembersSet crosscuttingMembersSet =
				bcelWorld.getCrosscuttingMembersSet();

			crosscuttingMembersSet.addOrReplaceAspect(resolvedType);
		}
		else {
			throw new IllegalArgumentException(
				"Class object " + aspectClass + " is not an aspect");
		}
	}

	private JavaClass _classToJavaClass(Class<?> aspectClass) {
		ClassLoader aspectClassLoader = aspectClass.getClassLoader();

		if (aspectClassLoader == null) {
			aspectClassLoader = ClassLoader.getSystemClassLoader();
		}

		String resourcePath = aspectClass.getName();

		resourcePath = StringUtil.replace(resourcePath, '.', '/') + ".class";

		ByteArrayInputStream byteArrayInputStream = null;

		InputStream inputStream = aspectClassLoader.getResourceAsStream(
			resourcePath);

		if (inputStream instanceof ByteArrayInputStream) {
			byteArrayInputStream = (ByteArrayInputStream)inputStream;
		}
		else {
			try {
				UnsyncByteArrayOutputStream unsyncByteArrayOutputStream =
					new UnsyncByteArrayOutputStream();

				StreamUtil.transfer(inputStream, unsyncByteArrayOutputStream);

				byte[] classData =
					unsyncByteArrayOutputStream.unsafeGetByteArray();

				byteArrayInputStream = new ByteArrayInputStream(
					classData, 0, unsyncByteArrayOutputStream.size());
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to reload class data", ioException);
			}
		}

		ClassParser classParser = new ClassParser(
			byteArrayInputStream, aspectClass.getSimpleName() + ".class");

		try {
			return classParser.parse();
		}
		catch (Exception exception) {
			throw new RuntimeException("Unable to parse class data", exception);
		}
	}

	private final Map<String, byte[]> _generatedClasses = new HashMap<>();

	private class RecordGeneratedClassHandler implements GeneratedClassHandler {

		@Override
		public void acceptClass(
			String name, byte[] originalBytes, byte[] weavedBytes) {

			_generatedClasses.put(name, weavedBytes);
		}

	}

}