/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal;

import com.liferay.gradle.plugins.BaseDefaultsPlugin;
import com.liferay.gradle.plugins.defaults.internal.util.FileUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradlePluginsDefaultsUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.jsdoc.BaseJSDocPlugin;
import com.liferay.gradle.plugins.jsdoc.JSDocPlugin;
import com.liferay.gradle.plugins.jsdoc.JSDocTask;
import com.liferay.gradle.plugins.node.task.DownloadNodeModuleTask;

import java.io.File;
import java.io.IOException;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.resources.ResourceHandler;
import org.gradle.api.resources.TextResourceFactory;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Andrea Di Giorgi
 */
public class JSDocDefaultsPlugin extends BaseDefaultsPlugin<JSDocPlugin> {

	public static final Plugin<Project> INSTANCE = new JSDocDefaultsPlugin();

	@Override
	protected void applyPluginDefaults(
		Project project, JSDocPlugin jsDocPlugin) {

		_configureTaskDownloadJSDoc(project);
		_configureTasksJSDoc(project);
	}

	@Override
	protected Class<JSDocPlugin> getPluginClass() {
		return JSDocPlugin.class;
	}

	private JSDocDefaultsPlugin() {
	}

	private void _configureTaskDownloadJSDoc(Project project) {
		DownloadNodeModuleTask downloadJSDocTask =
			(DownloadNodeModuleTask)GradleUtil.getTask(
				project, BaseJSDocPlugin.DOWNLOAD_JSDOC_TASK_NAME);

		GradlePluginsDefaultsUtil.configureTaskDownloadNodeModule(
			downloadJSDocTask);
	}

	private void _configureTaskJSDoc(JSDocTask jsDocTask) {
		Project project = jsDocTask.getProject();

		ResourceHandler resourceHandler = project.getResources();

		TextResourceFactory textResourceFactory = resourceHandler.getText();

		jsDocTask.setConfiguration(
			textResourceFactory.fromString(_CONFIG_JSON));

		File readmeFile = project.file("README.md");

		if (readmeFile.exists()) {
			jsDocTask.setReadmeFile(readmeFile);
		}
	}

	private void _configureTasksJSDoc(Project project) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			JSDocTask.class,
			new Action<JSDocTask>() {

				@Override
				public void execute(JSDocTask jsDocTask) {
					_configureTaskJSDoc(jsDocTask);
				}

			});
	}

	private static final String _CONFIG_JSON;

	static {
		try {
			_CONFIG_JSON = FileUtil.read(
				"com/liferay/gradle/plugins/defaults/internal/dependencies" +
					"/config-jsdoc.json");
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

}