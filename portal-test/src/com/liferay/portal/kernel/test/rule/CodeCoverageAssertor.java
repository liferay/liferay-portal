/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.rule;

import com.liferay.petra.process.ClassPathUtil;
import com.liferay.petra.string.StringPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * @author Shuyang Zhou
 */
public class CodeCoverageAssertor implements TestRule {

	public static final CodeCoverageAssertor INSTANCE =
		new CodeCoverageAssertor();

	public CodeCoverageAssertor() {
		this(null, null, true);
	}

	public CodeCoverageAssertor(
		String[] includes, String[] excludes, boolean includeInnerClasses) {

		_includes = includes;
		_excludes = excludes;
		_includeInnerClasses = includeInnerClasses;

		_skip = Boolean.getBoolean("junit.code.coverage");
	}

	public void appendAssertClasses(List<Class<?>> assertClasses) {
	}

	@Override
	public Statement apply(
		final Statement statement, final Description description) {

		if (_skip || (description.getMethodName() != null)) {
			return statement;
		}

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				String className = beforeClass(description);

				String whipStaticInstrument = System.getProperty(
					"whip.static.instrument");

				System.setProperty("whip.static.instrument", StringPool.TRUE);

				try {
					statement.evaluate();
				}
				finally {
					afterClass(description, className);

					if (whipStaticInstrument == null) {
						System.clearProperty("whip.static.instrument");
					}
					else {
						System.setProperty(
							"whip.static.instrument", whipStaticInstrument);
					}
				}
			}

		};
	}

	public List<Method> getAssertMethods() throws ReflectiveOperationException {
		return Collections.emptyList();
	}

	protected void afterClass(Description description, String className)
		throws Throwable {

		List<Class<?>> assertClasses = new ArrayList<>();

		if (className != null) {
			ClassLoader classLoader = getClassLoader();

			Class<?> clazz = classLoader.loadClass(className);

			assertClasses.add(clazz);
		}

		appendAssertClasses(assertClasses);

		try {
			_ASSERT_COVERAGE_METHOD.invoke(
				null, _includeInnerClasses, assertClasses, getAssertMethods());
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getCause();
		}
	}

	protected String beforeClass(Description description) throws Throwable {
		String className = description.getClassName();

		if (className.endsWith("Test")) {
			className = className.substring(0, className.length() - 4);
		}

		URL[] urls = ClassPathUtil.getClassPathURLs(
			ClassPathUtil.getJVMClassPath(false));

		ClassLoader classLoader = new URLClassLoader(urls, null);

		try {
			classLoader.loadClass(className);
		}
		catch (ClassNotFoundException classNotFoundException) {
			className = null;
		}

		String[] includes = _includes;

		if (includes == null) {
			includes = _generateIncludes(classLoader, className);
		}

		try {
			_DYNAMICALLY_INSTRUMENT_METHOD.invoke(null, includes, _excludes);
		}
		catch (InvocationTargetException invocationTargetException) {
			throw invocationTargetException.getCause();
		}

		return className;
	}

	protected ClassLoader getClassLoader() {
		Class<?> clazz = getClass();

		return clazz.getClassLoader();
	}

	private String[] _generateIncludes(
			ClassLoader classLoader, String mainClassName)
		throws Exception {

		List<Class<?>> assertClasses = new ArrayList<>();

		if (mainClassName != null) {
			Class<?> mainClass = classLoader.loadClass(mainClassName);

			assertClasses.add(mainClass);

			if (_includeInnerClasses) {
				Collections.addAll(
					assertClasses, mainClass.getDeclaredClasses());
			}
		}

		if (getClass() != CodeCoverageAssertor.class) {
			Class<?> clazz = getClass();

			Class<?> reloadedClass = classLoader.loadClass(clazz.getName());

			Method appendAssertClassesMethod = reloadedClass.getMethod(
				"appendAssertClasses", List.class);

			appendAssertClassesMethod.setAccessible(true);

			Constructor<?> constructor = reloadedClass.getDeclaredConstructor();

			constructor.setAccessible(true);

			Object reloadedObject = constructor.newInstance();

			appendAssertClassesMethod.invoke(reloadedObject, assertClasses);

			Method getAssertMethodsMethod = reloadedClass.getMethod(
				"getAssertMethods");

			getAssertMethodsMethod.setAccessible(true);

			List<Method> methods = (List<Method>)getAssertMethodsMethod.invoke(
				reloadedObject);

			for (Method method : methods) {
				Class<?> declaringClass = method.getDeclaringClass();

				if (assertClasses.contains(declaringClass)) {
					continue;
				}

				assertClasses.add(declaringClass);
			}
		}

		String[] includes = new String[assertClasses.size()];

		for (int i = 0; i < assertClasses.size(); i++) {
			Class<?> assertClass = assertClasses.get(i);

			String name = assertClass.getName();

			name = name.replace('.', '/');

			includes[i] = name.replace("$", "\\$");
		}

		return includes;
	}

	private static final Method _ASSERT_COVERAGE_METHOD;

	private static final Method _DYNAMICALLY_INSTRUMENT_METHOD;

	static {
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

		try {
			Class<?> instrumentationAgentClass = systemClassLoader.loadClass(
				"com.liferay.whip.agent.InstrumentationAgent");

			_ASSERT_COVERAGE_METHOD = instrumentationAgentClass.getMethod(
				"assertCoverage", boolean.class, List.class, List.class);
			_DYNAMICALLY_INSTRUMENT_METHOD =
				instrumentationAgentClass.getMethod(
					"dynamicallyInstrument", String[].class, String[].class);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private final String[] _excludes;
	private final boolean _includeInnerClasses;
	private final String[] _includes;
	private final boolean _skip;

}