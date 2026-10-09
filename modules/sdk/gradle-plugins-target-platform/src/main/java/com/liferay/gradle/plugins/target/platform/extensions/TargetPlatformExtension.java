/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.target.platform.extensions;

import com.liferay.gradle.plugins.target.platform.TargetPlatformPlugin;
import com.liferay.gradle.plugins.target.platform.internal.util.GradleUtil;
import com.liferay.gradle.plugins.target.platform.internal.util.TargetPlatformPluginUtil;
import com.liferay.gradle.util.GUtil;

import groovy.lang.Closure;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.specs.AndSpec;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;

/**
 * @author Gregory Amerson
 */
public class TargetPlatformExtension {

	public TargetPlatformExtension(final Project project) {
		_project = project;

		_subprojects.addAll(project.getSubprojects());

		Logger logger = project.getLogger();

		onlyIf(
			new Spec<Project>() {

				@Override
				public boolean isSatisfiedBy(Project project) {
					TaskContainer taskContainer = project.getTasks();

					Task jarTask = taskContainer.findByName(
						JavaPlugin.JAR_TASK_NAME);

					if (!(jarTask instanceof Jar)) {
						if (logger.isInfoEnabled()) {
							logger.info(
								"Excluding {} because it is not a valid Java " +
									"project",
								project);
						}

						return false;
					}

					return true;
				}

			});

		resolveOnlyIf(
			new Spec<Project>() {

				@Override
				public boolean isSatisfiedBy(Project project) {
					Project rootProject = project.getRootProject();

					File bndrunFile = rootProject.file(
						TargetPlatformPlugin.PLATFORM_BNDRUN_FILE_NAME);

					if (!bndrunFile.exists()) {
						StringBuilder sb = new StringBuilder();

						sb.append("Explicitly excluding ");
						sb.append(project);
						sb.append(" from resolution because there is no ");
						sb.append(
							TargetPlatformPlugin.PLATFORM_BNDRUN_FILE_NAME);
						sb.append(" file at the root of the gradle workspace");

						if (logger.isInfoEnabled()) {
							logger.info(sb.toString());
						}

						return false;
					}

					return true;
				}

			});

		resolveOnlyIf(
			new Spec<Project>() {

				@Override
				public boolean isSatisfiedBy(Project project) {
					PluginManager pluginManager = project.getPluginManager();

					if (!pluginManager.hasPlugin("com.liferay.osgi.plugin")) {
						if (logger.isInfoEnabled()) {
							logger.info(
								"Explicitly excluding {} from resolution " +
									"because it does not appear to be an " +
										"OSGi bundle",
								project);
						}

						return false;
					}

					return true;
				}

			});
	}

	public TargetPlatformExtension applyToConfiguration(
		Iterable<?> configurationNames) {

		Configuration targetPlatformBomsConfiguration =
			GradleUtil.getConfiguration(
				_project,
				TargetPlatformPlugin.TARGET_PLATFORM_BOMS_CONFIGURATION_NAME);

		List<String> configurationNamesList = new ArrayList<>();

		Iterator<?> iterator = configurationNames.iterator();

		while (iterator.hasNext()) {
			Object object = iterator.next();

			if (!(object instanceof String)) {
				continue;
			}

			configurationNamesList.add((String)object);
		}

		TargetPlatformPluginUtil.configureTargetPlatform(
			_project, configurationNamesList, targetPlatformBomsConfiguration);

		return this;
	}

	public TargetPlatformExtension applyToConfiguration(
		Object... configurationNames) {

		return applyToConfiguration(Arrays.asList(configurationNames));
	}

	public Spec<Project> getOnlyIf() {
		return _onlyIfSpec;
	}

	public Spec<Project> getResolveOnlyIf() {
		return _resolveOnlyIfSpec;
	}

	public Set<Project> getSubprojects() {
		return _subprojects;
	}

	public TargetPlatformExtension onlyIf(Closure<Boolean> onlyIfClosure) {
		_onlyIfSpec = _onlyIfSpec.and(onlyIfClosure);

		return this;
	}

	public TargetPlatformExtension onlyIf(Spec<Project> onlyIfSpec) {
		_onlyIfSpec = _onlyIfSpec.and(onlyIfSpec);

		return this;
	}

	public TargetPlatformExtension resolveOnlyIf(
		Closure<Boolean> resolveOnlyIfClosure) {

		_resolveOnlyIfSpec = _resolveOnlyIfSpec.and(resolveOnlyIfClosure);

		return this;
	}

	public TargetPlatformExtension resolveOnlyIf(
		Spec<Project> resolveOnlyIfSpec) {

		_resolveOnlyIfSpec = _resolveOnlyIfSpec.and(resolveOnlyIfSpec);

		return this;
	}

	public void setOnlyIf(Closure<Boolean> onlyIfClosure) {
		_onlyIfSpec = new AndSpec<>();

		_onlyIfSpec.and(onlyIfClosure);
	}

	public void setOnlyIf(Spec<Project> onlyIfSpec) {
		_onlyIfSpec = new AndSpec<>(onlyIfSpec);
	}

	public void setResolveOnlyIf(Closure<Boolean> resolveOnlyIfClosure) {
		_resolveOnlyIfSpec = new AndSpec<>();

		_resolveOnlyIfSpec.and(resolveOnlyIfClosure);
	}

	public void setResolveOnlyIf(Spec<Project> resolveOnlyIfSpec) {
		_resolveOnlyIfSpec = new AndSpec<>(resolveOnlyIfSpec);
	}

	public void setSubprojects(Iterable<Project> subprojects) {
		_subprojects.clear();

		subprojects(subprojects);
	}

	public void setSubprojects(Project... subprojects) {
		setSubprojects(Arrays.asList(subprojects));
	}

	public TargetPlatformExtension subprojects(Iterable<Project> subprojects) {
		GUtil.addToCollection(_subprojects, subprojects);

		return this;
	}

	public TargetPlatformExtension subprojects(Project... subprojects) {
		return subprojects(Arrays.asList(subprojects));
	}

	private AndSpec<Project> _onlyIfSpec = new AndSpec<>();
	private final Project _project;
	private AndSpec<Project> _resolveOnlyIfSpec = new AndSpec<>();
	private final Set<Project> _subprojects = new LinkedHashSet<>();

}