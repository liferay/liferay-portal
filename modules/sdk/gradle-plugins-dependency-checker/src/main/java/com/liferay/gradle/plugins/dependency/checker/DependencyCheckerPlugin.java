/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.dependency.checker;

import com.liferay.gradle.util.GradleUtil;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.artifacts.component.ComponentIdentifier;
import org.gradle.api.artifacts.component.ModuleComponentIdentifier;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;

/**
 * @author Andrea Di Giorgi
 */
public class DependencyCheckerPlugin implements Plugin<Project> {

	public static final String PLUGIN_NAME = "dependencyChecker";

	@Override
	public void apply(Project project) {
		final DependencyCheckerExtension dependencyCheckerExtension =
			GradleUtil.addExtension(
				project, PLUGIN_NAME, DependencyCheckerExtension.class);

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		configurationContainer.all(
			new Action<Configuration>() {

				@Override
				public void execute(Configuration configuration) {
					_configureConfiguration(
						configuration, dependencyCheckerExtension);
				}

			});
	}

	private void _checkConfiguration(
		Configuration configuration, ResolutionResult resolutionResult,
		DependencyCheckerExtension dependencyCheckerExtension) {

		for (ResolvedComponentResult resolvedComponentResult :
				resolutionResult.getAllComponents()) {

			ComponentIdentifier componentIdentifier =
				resolvedComponentResult.getId();

			if (!(componentIdentifier instanceof ModuleComponentIdentifier)) {
				continue;
			}

			ModuleComponentIdentifier moduleComponentIdentifier =
				(ModuleComponentIdentifier)componentIdentifier;

			dependencyCheckerExtension.check(
				configuration.getName(), moduleComponentIdentifier.getGroup(),
				moduleComponentIdentifier.getModule(),
				moduleComponentIdentifier.getVersion());
		}
	}

	private void _configureConfiguration(
		final Configuration configuration,
		final DependencyCheckerExtension dependencyCheckerExtension) {

		ResolvableDependencies resolvableDependencies =
			configuration.getIncoming();

		resolvableDependencies.afterResolve(
			new Action<ResolvableDependencies>() {

				@Override
				public void execute(
					ResolvableDependencies resolvableDependencies) {

					_checkConfiguration(
						configuration,
						resolvableDependencies.getResolutionResult(),
						dependencyCheckerExtension);
				}

			});
	}

}