/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.jsp.engine.internal.jakarta.transformer;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.reflect.ReflectionUtil;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.Map;
import java.util.function.BiFunction;

import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.JDTCompiler;
import org.apache.jasper.compiler.SmapStratum;

/**
 * @author Shuyang Zhou
 */
public class JakartaTransformerJDTCompiler extends JDTCompiler {

	@Override
	protected void generateClass(Map<String, SmapStratum> smaps)
		throws Exception, FileNotFoundException, JasperException {

		if (_classRemapperBiFunction == null) {
			super.generateClass(smaps);

			return;
		}

		ClassLoader classLoader = ctxt.getJspLoader();

		URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], null) {

			@Override
			public InputStream getResourceAsStream(String name) {
				InputStream inputStream = classLoader.getResourceAsStream(name);

				if (inputStream == null) {
					return null;
				}

				try {
					return new UnsyncByteArrayInputStream(
						_classRemapperBiFunction.apply(
							"PortalJspCClass#" + name,
							StreamUtil.toByteArray(inputStream)));
				}
				catch (IOException ioException) {
					return ReflectionUtil.throwException(ioException);
				}
			}

		};

		try {
			_jspLoaderMethodHandle.invokeExact(ctxt, urlClassLoader);
		}
		catch (Throwable throwable) {
			ReflectionUtil.throwException(throwable);
		}

		super.generateClass(smaps);
	}

	private static final BiFunction<String, byte[], byte[]>
		_classRemapperBiFunction;
	private static final MethodHandle _jspLoaderMethodHandle;

	static {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();

		Object instance = null;

		try {
			Class<?> clazz = classLoader.loadClass(
				"com.liferay.portal.tools.jakarta.ee.transformer.function." +
					"ClassRemapperBiFunction");

			instance = clazz.newInstance();
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			if (!(reflectiveOperationException instanceof
					ClassNotFoundException)) {

				throw new ExceptionInInitializerError(
					reflectiveOperationException);
			}
		}

		_classRemapperBiFunction = (BiFunction<String, byte[], byte[]>)instance;

		try {
			MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

			_jspLoaderMethodHandle = lookup.findSetter(
				JspCompilationContext.class, "jspLoader", URLClassLoader.class);
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

}