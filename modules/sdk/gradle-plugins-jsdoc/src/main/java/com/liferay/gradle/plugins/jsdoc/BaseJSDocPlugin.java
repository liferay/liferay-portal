/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.jsdoc;

import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.task.DownloadNodeModuleTask;
import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Andrea Di Giorgi
 */
public abstract class BaseJSDocPlugin implements Plugin<Project> {

	public static final String DOWNLOAD_JSDOC_TASK_NAME = "downloadJSDoc";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, NodePlugin.class);

		DownloadNodeModuleTask downloadJSDocTask = _addTaskDownloadJSDoc(
			project);

		_configureTasksJSDoc(downloadJSDocTask);
	}

	private DownloadNodeModuleTask _addTaskDownloadJSDoc(Project project) {
		DownloadNodeModuleTask downloadNodeModuleTask = GradleUtil.addTask(
			project, DOWNLOAD_JSDOC_TASK_NAME, DownloadNodeModuleTask.class);

		downloadNodeModuleTask.args("--no-save");
		downloadNodeModuleTask.setDescription("Downloads JSDoc.");
		downloadNodeModuleTask.setModuleName("jsdoc");
		downloadNodeModuleTask.setModuleVersion(_VERSION);

		return downloadNodeModuleTask;
	}

	private void _configureTaskJSDoc(
		JSDocTask jsDocTask, final DownloadNodeModuleTask downloadJSDocTask) {

		jsDocTask.dependsOn(downloadJSDocTask);

		jsDocTask.setScriptFile(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(
						downloadJSDocTask.getModuleDir(), "jsdoc.js");
				}

			});
	}

	private void _configureTasksJSDoc(
		final DownloadNodeModuleTask downloadJSDocTask) {

		Project project = downloadJSDocTask.getProject();

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			JSDocTask.class,
			new Action<JSDocTask>() {

				@Override
				public void execute(JSDocTask jsDocTask) {
					_configureTaskJSDoc(jsDocTask, downloadJSDocTask);
				}

			});
	}

	private static final String _VERSION = "4.0.5";

}