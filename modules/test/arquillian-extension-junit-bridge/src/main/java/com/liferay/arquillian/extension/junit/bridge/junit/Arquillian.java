/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.arquillian.extension.junit.bridge.junit;

import com.liferay.arquillian.extension.junit.bridge.client.ClientState;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * @author Shuyang Zhou
 */
public class Arquillian extends Runner implements Filterable {

	public Arquillian(Class<?> clazz) {
		_clazz = clazz;

		_testMethods = _scanTestMethods(clazz);
	}

	@Override
	public void filter(Filter filter) throws NoTestsRemainException {
		_filter(_clazz, _testMethods, filter);

		if (_testMethods.isEmpty()) {
			throw new NoTestsRemainException();
		}

		_clientState.filterTestClasses(
			_clazz,
			testClass -> {
				List<Method> testMethods = _scanTestMethods(testClass);

				List<String> filteredMethodNames = _filter(
					testClass, testMethods, filter);

				if (testMethods.isEmpty()) {
					return true;
				}

				if (!filteredMethodNames.isEmpty()) {
					if (_filteredMethodNamesMap == null) {
						_filteredMethodNamesMap = new HashMap<>();
					}

					_filteredMethodNamesMap.put(
						testClass.getName(), filteredMethodNames);
				}

				return false;
			});
	}

	@Override
	public Description getDescription() {
		return Description.createSuiteDescription(_clazz);
	}

	@Override
	public void run(RunNotifier runNotifier) {
		_testMethods.removeIf(
			method -> {
				if (method.getAnnotation(Ignore.class) != null) {
					runNotifier.fireTestIgnored(
						Description.createTestDescription(
							_clazz, method.getName(), method.getAnnotations()));

					return true;
				}

				return false;
			});

		if (_testMethods.isEmpty()) {
			_clientState.removeTestClass(_clazz);

			return;
		}

		// Enforce client side test class initialization

		try {
			Class.forName(_clazz.getName(), true, _clazz.getClassLoader());
		}
		catch (ClassNotFoundException classNotFoundException) {
			runNotifier.fireTestFailure(
				new Failure(getDescription(), classNotFoundException));

			return;
		}

		try (ClientState.Connection connection = _clientState.open(
				_clazz, _filteredMethodNamesMap)) {

			connection.execute(
				_clazz.getName(),
				runNotifierCommand -> runNotifierCommand.execute(runNotifier));
		}
		catch (Throwable throwable) {
			runNotifier.fireTestFailure(
				new Failure(getDescription(), throwable));
		}
	}

	private List<String> _filter(
		Class<?> clazz, List<Method> testMethods, Filter filter) {

		List<String> filteredMethodNames = new ArrayList<>();

		Iterator<Method> iterator = testMethods.iterator();

		while (iterator.hasNext()) {
			Method method = iterator.next();

			String methodName = method.getName();

			if (filter.shouldRun(
					Description.createTestDescription(clazz, methodName))) {

				continue;
			}

			filteredMethodNames.add(methodName);

			iterator.remove();
		}

		return filteredMethodNames;
	}

	private List<Method> _scanTestMethods(Class<?> clazz) {
		List<Method> testMethods = new ArrayList<>();

		while (clazz != Object.class) {
			for (Method method : clazz.getDeclaredMethods()) {
				if (method.getAnnotation(Test.class) == null) {
					continue;
				}

				testMethods.add(method);
			}

			clazz = clazz.getSuperclass();
		}

		testMethods.sort(Comparator.comparing(Method::getName));

		return testMethods;
	}

	private static final ClientState _clientState = new ClientState();

	private final Class<?> _clazz;
	private Map<String, List<String>> _filteredMethodNamesMap;
	private final List<Method> _testMethods;

}