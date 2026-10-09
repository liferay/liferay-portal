/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.jsdoc;

import com.liferay.gradle.util.GradleUtil;

import groovy.lang.Closure;

import java.io.File;

import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Project;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.Jar;

/**
 * @author Andrea Di Giorgi
 * @author Peter Shin
 */
public class AppJSDocPlugin extends BaseJSDocPlugin {

	public static final String APP_JSDOC_TASK_NAME = "appJSDoc";

	public static final String JAR_APP_JSDOC_TASK_NAME = "jarAppJSDoc";

	public static final String PLUGIN_NAME = "appJSDocConfiguration";

	@Override
	public void apply(Project project) {
		super.apply(project);

		final AppJSDocConfigurationExtension appJSDocConfigurationExtension =
			GradleUtil.addExtension(
				project, PLUGIN_NAME, AppJSDocConfigurationExtension.class);

		final JSDocTask appJSDocTask = _addTaskAppJSDoc(project);

		_addTaskJarAppJSDoc(appJSDocTask);

		Gradle gradle = project.getGradle();

		gradle.afterProject(
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(Project subproject) {
					Set<Project> subprojects =
						appJSDocConfigurationExtension.getSubprojects();

					PluginContainer pluginContainer = subproject.getPlugins();

					if (subprojects.contains(subproject) &&
						pluginContainer.hasPlugin(JSDocPlugin.class)) {

						_configureTaskAppJSDoc(appJSDocTask, subproject);
					}
				}

			});
	}

	private JSDocTask _addTaskAppJSDoc(Project project) {
		final JSDocTask appJSDocTask = GradleUtil.addTask(
			project, APP_JSDOC_TASK_NAME, JSDocTask.class);

		appJSDocTask.setDescription(
			"Generates the API documentation for the JavaScript code in this " +
				"app.");

		appJSDocTask.setDestinationDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					Project project = appJSDocTask.getProject();

					return new File(project.getBuildDir(), "docs/jsdoc");
				}

			});

		appJSDocTask.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);

		return appJSDocTask;
	}

	private Jar _addTaskJarAppJSDoc(JSDocTask jsDocTask) {
		Jar jar = GradleUtil.addTask(
			jsDocTask.getProject(), JAR_APP_JSDOC_TASK_NAME, Jar.class);

		jar.from(jsDocTask);
		jar.setDescription(
			"Assembles a jar archive containing the JavaScript documentation " +
				"files for this app.");
		jar.setGroup(BasePlugin.BUILD_GROUP);

		Property<String> property = jar.getArchiveClassifier();

		property.set("jsdoc");

		return jar;
	}

	private void _configureTaskAppJSDoc(
		JSDocTask appJSDocTask, Project subproject) {

		JSDocTask subprojectJSDocTask = (JSDocTask)GradleUtil.getTask(
			subproject, JSDocPlugin.JSDOC_TASK_NAME);

		for (File dir : subprojectJSDocTask.getSourceDirs()) {
			if (!dir.exists()) {
				continue;
			}

			appJSDocTask.sourceDirs(dir);
		}
	}

}