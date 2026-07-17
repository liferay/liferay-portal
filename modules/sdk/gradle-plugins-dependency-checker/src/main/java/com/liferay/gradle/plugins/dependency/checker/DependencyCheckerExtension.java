/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.dependency.checker;

import com.liferay.gradle.plugins.dependency.checker.internal.DependencyChecker;
import com.liferay.gradle.plugins.dependency.checker.internal.DependencyKey;
import com.liferay.gradle.plugins.dependency.checker.internal.MaxAgeDependencyCheckerImpl;
import com.liferay.gradle.util.GradleUtil;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.gradle.StartParameter;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.util.ConfigureUtil;

/**
 * @author Andrea Di Giorgi
 */
public class DependencyCheckerExtension {

	public DependencyCheckerExtension(Project project) {
		_project = project;

		_ignoreFailures = GradleUtil.getProperty(
			project, "dependencyCheckerIgnoreFailures", true);
	}

	public void check(
		String configuration, String group, String name, String version) {

		Gradle gradle = _project.getGradle();
		Logger logger = _project.getLogger();

		StartParameter startParameter = gradle.getStartParameter();

		if (startParameter.isOffline()) {
			if (logger.isWarnEnabled()) {
				logger.warn("Build is offline, dependency check disabled");
			}

			return;
		}

		DependencyKey dependencyKey = new DependencyKey();

		dependencyKey.setConfiguration(configuration);
		dependencyKey.setGroup(group);
		dependencyKey.setName(name);

		DependencyChecker dependencyChecker = _dependencyCheckers.get(
			dependencyKey);

		if (dependencyChecker == null) {
			if (logger.isDebugEnabled()) {
				logger.debug(
					"No dependency checkers are defined for \"{}:{}:{}\" in " +
						"configuration \"{}\"",
					group, name, version, configuration);
			}

			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(
				"Checking dependency \"{}:{}:{}\" in configuration \"{}\"",
				group, name, version, configuration);
		}

		try {
			dependencyChecker.check(group, name, version);
		}
		catch (Exception exception) {
			if (!isIgnoreFailures()) {
				if (exception instanceof IOException) {
					throw new UncheckedIOException(exception);
				}

				if (exception instanceof RuntimeException) {
					throw (RuntimeException)exception;
				}

				throw new GradleException(exception.getMessage(), exception);
			}

			if (exception instanceof DependencyCheckerException) {
				System.out.println(exception.getMessage());
			}
			else {
				logger.error(
					"Unable to check dependency '" + group + ":" + name + ":" +
						version + "' in configuration '" + configuration + "'",
					exception);
			}
		}
	}

	public boolean isIgnoreFailures() {
		return _ignoreFailures;
	}

	public void maxAge(Map<?, ?> args) {
		_add(
			new MaxAgeDependencyCheckerImpl(_project.getLogger()), args,
			"maxAge", "throwError");
	}

	public void setIgnoreFailures(boolean ignoreFailures) {
		_ignoreFailures = ignoreFailures;
	}

	private DependencyChecker _add(
		DependencyChecker dependencyChecker, Map<?, ?> args,
		String... mandatoryKeys) {

		Map<Object, Object> dependencyKeyArgs = _extractArgs(
			args, _dependencyKeyMandatoryKeys);

		DependencyKey dependencyKey = ConfigureUtil.configureByMap(
			dependencyKeyArgs, new DependencyKey(),
			_dependencyKeyMandatoryKeys);

		dependencyChecker = ConfigureUtil.configureByMap(
			args, dependencyChecker, Arrays.asList(mandatoryKeys));

		_dependencyCheckers.put(dependencyKey, dependencyChecker);

		return dependencyChecker;
	}

	private Map<Object, Object> _extractArgs(
		Map<?, ?> args, Collection<?> keys) {

		Map<Object, Object> extractedArgs = new HashMap<>();

		for (Object key : keys) {
			Object value = args.remove(key);

			if (value != null) {
				extractedArgs.put(key, value);
			}
		}

		return extractedArgs;
	}

	private static final Collection<String> _dependencyKeyMandatoryKeys =
		Arrays.asList("configuration", "group", "name");

	private final Map<DependencyKey, DependencyChecker> _dependencyCheckers =
		new HashMap<>();
	private boolean _ignoreFailures;
	private final Project _project;

}