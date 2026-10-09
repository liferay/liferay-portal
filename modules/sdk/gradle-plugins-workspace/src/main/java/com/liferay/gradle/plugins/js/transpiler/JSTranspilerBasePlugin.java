/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.js.transpiler;

import com.liferay.gradle.plugins.js.transpiler.internal.util.JSTranspilerPluginUtil;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.YarnPlugin;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.PackageRunTask;
import com.liferay.gradle.plugins.node.task.YarnInstallTask;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceNodePlugin;
import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.copy.RenameDependencyClosure;

import java.io.File;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskDependency;

/**
 * @author     Andrea Di Giorgi
 * @deprecated As of Judson (7.1.x), with no direct replacement
 */
@Deprecated
public class JSTranspilerBasePlugin implements Plugin<Project> {

	public static final String EXPAND_JS_COMPILE_DEPENDENCIES_TASK_NAME =
		"expandJSCompileDependencies";

	public static final String JS_COMPILE_CONFIGURATION_NAME = "jsCompile";

	@Override
	public void apply(Project project) {
		LiferayWorkspaceNodePlugin.INSTANCE.apply(project);

		final NpmInstallTask npmInstallTask =
			(NpmInstallTask)GradleUtil.fetchTask(
				project, NodePlugin.NPM_INSTALL_TASK_NAME);

		if (npmInstallTask == null) {
			Logger logger = project.getLogger();

			if (logger.isWarnEnabled()) {
				logger.warn(
					"Unable to find NPM install task, skipping " +
						"JSTranspilerBasePlugin configuration");
			}

			return;
		}

		final Configuration jsCompileConfiguration = _addConfigurationJSCompile(
			project);

		final Task expandJSCompileDependenciesTask =
			_addTaskExpandJSCompileDependencies(project);

		_configureTasksPackageRun(expandJSCompileDependenciesTask);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_addTasksExpandJSCompileDependency(
						expandJSCompileDependenciesTask, npmInstallTask,
						jsCompileConfiguration);
				}

			});
	}

	private Configuration _addConfigurationJSCompile(Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, JS_COMPILE_CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures additional JavaScript dependencies.");
		configuration.setVisible(false);

		return configuration;
	}

	private Task _addTaskExpandJSCompileDependencies(Project project) {
		Task task = project.task(EXPAND_JS_COMPILE_DEPENDENCIES_TASK_NAME);

		task.setDescription(
			"Expands the configured additional Javascript dependencies.");

		return task;
	}

	private void _addTasksExpandJSCompileDependency(
		Task expandJSCompileDependenciesTask, NpmInstallTask npmInstallTask,
		Configuration configuration) {

		Project project = expandJSCompileDependenciesTask.getProject();

		RenameDependencyClosure renameDependencyClosure =
			new RenameDependencyClosure(project, configuration.getName());

		Iterable<TaskDependency> taskDependencies =
			JSTranspilerPluginUtil.getTaskDependencies(configuration);

		for (File file : configuration) {
			Copy copy = JSTranspilerPluginUtil.addTaskExpandCompileDependency(
				project, file, npmInstallTask.getNodeModulesDir(),
				"expandJSCompileDependency", renameDependencyClosure);

			copy.dependsOn(taskDependencies);
			copy.mustRunAfter(npmInstallTask);

			if (!npmInstallTask.isUseNpm()) {
				Project curProject = npmInstallTask.getProject();

				do {
					YarnInstallTask yarnInstallTask =
						(YarnInstallTask)GradleUtil.fetchTask(
							curProject, YarnPlugin.YARN_INSTALL_TASK_NAME);

					if (yarnInstallTask == null) {
						continue;
					}

					copy.mustRunAfter(yarnInstallTask);
				}
				while ((curProject = curProject.getParent()) != null);
			}

			expandJSCompileDependenciesTask.dependsOn(copy);
		}
	}

	private void _configureTasksPackageRun(
		final Task expandJSCompileDependenciesTask) {

		Project project = expandJSCompileDependenciesTask.getProject();

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PackageRunTask.class,
			new Action<PackageRunTask>() {

				@Override
				public void execute(PackageRunTask packageRunTask) {
					packageRunTask.dependsOn(expandJSCompileDependenciesTask);
				}

			});
	}

}