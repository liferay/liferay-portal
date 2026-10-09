/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.deployment.helper;

import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Jar;

/**
 * @author Andrea Di Giorgi
 */
public class DeploymentHelperPlugin implements Plugin<Project> {

	public static final String BUILD_DEPLOYMENT_HELPER_TASK_NAME =
		"buildDeploymentHelper";

	public static final String CONFIGURATION_NAME = "deploymentHelper";

	@Override
	public void apply(Project project) {
		Configuration deploymentHelperConfiguration =
			addConfigurationDeploymentHelper(project);

		addTaskBuildDeploymentHelper(project);

		configureTasksBuildDeploymentHelper(
			project, deploymentHelperConfiguration);
	}

	protected Configuration addConfigurationDeploymentHelper(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					addDependenciesDeploymentHelper(project);
				}

			});

		configuration.setDescription(
			"Configures Liferay Deployment Helper for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	protected void addDependenciesDeploymentHelper(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "com.liferay",
			"com.liferay.deployment.helper", "latest.release");
	}

	protected BuildDeploymentHelperTask addTaskBuildDeploymentHelper(
		final Project project) {

		BuildDeploymentHelperTask buildDeploymentHelperTask =
			GradleUtil.addTask(
				project, BUILD_DEPLOYMENT_HELPER_TASK_NAME,
				BuildDeploymentHelperTask.class);

		buildDeploymentHelperTask.setDeploymentFiles(
			new Callable<List<Jar>>() {

				@Override
				public List<Jar> call() throws Exception {
					List<Jar> jarTasks = new ArrayList<>();

					for (Project curProject : project.getAllprojects()) {
						TaskContainer taskContainer = curProject.getTasks();

						Task task = taskContainer.findByName(
							JavaPlugin.JAR_TASK_NAME);

						if (!(task instanceof Jar)) {
							continue;
						}

						jarTasks.add((Jar)task);
					}

					return jarTasks;
				}

			});

		buildDeploymentHelperTask.setDescription(
			"Assembles a war file to deploy one or more apps to a cluster.");
		buildDeploymentHelperTask.setGroup(BasePlugin.BUILD_GROUP);

		buildDeploymentHelperTask.setOutputFile(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(
						project.getBuildDir(), project.getName() + ".war");
				}

			});

		return buildDeploymentHelperTask;
	}

	protected void configureTaskBuildDeploymentHelperClasspath(
		BuildDeploymentHelperTask buildDeploymentHelperTask,
		FileCollection classpath) {

		buildDeploymentHelperTask.setClasspath(classpath);
	}

	protected void configureTasksBuildDeploymentHelper(
		Project project, final Configuration deploymentHelperConfiguration) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			BuildDeploymentHelperTask.class,
			new Action<BuildDeploymentHelperTask>() {

				@Override
				public void execute(
					BuildDeploymentHelperTask buildDeploymentHelperTask) {

					configureTaskBuildDeploymentHelperClasspath(
						buildDeploymentHelperTask,
						deploymentHelperConfiguration);
				}

			});
	}

}