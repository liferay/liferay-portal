/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins;

import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.NodeExtension;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.task.ExecutePackageManagerTask;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.PublishNodeModuleTask;
import com.liferay.gradle.util.Validator;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Andrea Di Giorgi
 */
public class NodeDefaultsPlugin extends BaseDefaultsPlugin<NodePlugin> {

	@Override
	protected void applyPluginDefaults(Project project, NodePlugin nodePlugin) {

		// Extensions

		ExtensionContainer extensionContainer = project.getExtensions();

		NodeExtension nodeExtension = extensionContainer.getByType(
			NodeExtension.class);

		_configureExtensionNode(project, nodeExtension);

		// Containers

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			ExecutePackageManagerTask.class,
			new Action<ExecutePackageManagerTask>() {

				@Override
				public void execute(
					ExecutePackageManagerTask executePackageManagerTask) {

					_configureTaskExecutePackageManager(
						executePackageManagerTask);
				}

			});

		taskContainer.withType(
			NpmInstallTask.class,
			new Action<NpmInstallTask>() {

				@Override
				public void execute(NpmInstallTask npmInstallTask) {
					_configureTaskNpmInstall(npmInstallTask);
				}

			});

		taskContainer.withType(
			PublishNodeModuleTask.class,
			new Action<PublishNodeModuleTask>() {

				@Override
				public void execute(
					PublishNodeModuleTask publishNodeModuleTask) {

					_configureTaskPublishNodeModule(publishNodeModuleTask);
				}

			});
	}

	@Override
	protected Class<NodePlugin> getPluginClass() {
		return NodePlugin.class;
	}

	private void _configureExtensionNode(
		Project project, NodeExtension nodeExtension) {

		nodeExtension.setGlobal(true);
		nodeExtension.setNodeVersion(_NODE_VERSION);
		nodeExtension.setNpmVersion(_NPM_VERSION);

		String npmArgs = GradleUtil.getProperty(
			project, "nodejs.npm.args", (String)null);

		if (Validator.isNotNull(npmArgs)) {
			nodeExtension.npmArgs((Object[])npmArgs.split("\\s+"));
		}
	}

	private void _configureTaskExecutePackageManager(
		ExecutePackageManagerTask executePackageManagerTask) {

		String nodeEnv = GradleUtil.getProperty(
			executePackageManagerTask.getProject(), "nodejs.node.env",
			(String)null);

		if (Validator.isNotNull(nodeEnv)) {
			executePackageManagerTask.environment("NODE_ENV", nodeEnv);
		}

		String registry = GradleUtil.getProperty(
			executePackageManagerTask.getProject(), "nodejs.npm.registry",
			(String)null);

		if (Validator.isNotNull(registry)) {
			executePackageManagerTask.setRegistry(registry);
		}
	}

	private void _configureTaskNpmInstall(NpmInstallTask npmInstallTask) {
		String sassBinarySite = GradleUtil.getProperty(
			npmInstallTask.getProject(), "nodejs.npm.sass.binary.site",
			(String)null);

		if (Validator.isNotNull(sassBinarySite)) {
			boolean sassBinarySiteArg = false;

			for (Object object : npmInstallTask.getArgs()) {
				String arg = GradleUtil.toString(object);

				if (arg.startsWith(_SASS_BINARY_SITE_ARG)) {
					sassBinarySiteArg = true;
				}
			}

			if (!sassBinarySiteArg) {
				npmInstallTask.args(_SASS_BINARY_SITE_ARG + sassBinarySite);
			}
		}
	}

	private void _configureTaskPublishNodeModule(
		PublishNodeModuleTask publishNodeModuleTask) {

		Project project = publishNodeModuleTask.getProject();

		String moduleAuthor = GradleUtil.getProperty(
			project, "nodejs.npm.module.author", (String)null);

		if (Validator.isNotNull(moduleAuthor)) {
			publishNodeModuleTask.setModuleAuthor(moduleAuthor);
		}

		String moduleBugsUrl = GradleUtil.getProperty(
			project, "nodejs.npm.module.bugs.url", (String)null);

		if (Validator.isNotNull(moduleBugsUrl)) {
			publishNodeModuleTask.setModuleBugsUrl(moduleBugsUrl);
		}

		String moduleLicense = GradleUtil.getProperty(
			project, "nodejs.npm.module.license", (String)null);

		if (Validator.isNotNull(moduleLicense)) {
			publishNodeModuleTask.setModuleLicense(moduleLicense);
		}

		String moduleRepository = GradleUtil.getProperty(
			project, "nodejs.npm.module.repository", (String)null);

		if (Validator.isNotNull(moduleRepository)) {
			publishNodeModuleTask.setModuleRepository(moduleRepository);
		}

		String npmAccessToken = GradleUtil.getProperty(
			project, "nodejs.npm.access.token", (String)null);

		if (Validator.isNotNull(npmAccessToken)) {
			publishNodeModuleTask.setNpmAccessToken(npmAccessToken);
		}
	}

	private static final String _NODE_VERSION = "20.20.2";

	private static final String _NPM_VERSION = "10.8.2";

	private static final String _SASS_BINARY_SITE_ARG = "--sass-binary-site=";

}