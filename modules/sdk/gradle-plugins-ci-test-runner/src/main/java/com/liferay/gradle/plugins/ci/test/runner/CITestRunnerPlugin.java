/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.ci.test.runner;

import com.liferay.ant.mirrors.get.MirrorsGetTask;
import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.extensions.TomcatAppServer;
import com.liferay.gradle.util.GradleUtil;

import org.apache.tools.ant.BuildException;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtensionContainer;

/**
 * @author Calum Ragan
 */
public class CITestRunnerPlugin implements Plugin<Project> {
    
	public static final String DOWNLOAD_TOMCAT_ZIP_TASK_NAME =
		"downloadTomcatZip";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, BasePlugin.class);

		GradleUtil.applyPlugin(project, LiferayBasePlugin.class);

		ExtensionContainer extensionContainer = project.getExtensions();

		LiferayExtension liferayExtension = extensionContainer.getByType(
			LiferayExtension.class);

		TomcatAppServer tomcatAppServer =
			(TomcatAppServer)liferayExtension.getAppServer("tomcat");

		_addTaskDownloadTomcatZip(project, tomcatAppServer);
	}

	private Task _addTaskDownloadTomcatZip(
		Project project, TomcatAppServer tomcatAppServer) {

		Task task = GradleUtil.addTask(
			project, DOWNLOAD_TOMCAT_ZIP_TASK_NAME, Task.class);

		task.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					MirrorsGetTask mirrorsGetTask = new MirrorsGetTask();

					org.apache.tools.ant.Project antProject =
						new org.apache.tools.ant.Project();

					antProject.init();

					mirrorsGetTask.setProject(antProject);

					mirrorsGetTask.setVerbose(true);

					mirrorsGetTask.setSrc(tomcatAppServer.getZipUrl());

					mirrorsGetTask.setDest(tomcatAppServer.getDir());

					try {
						mirrorsGetTask.execute();
					}
					catch (BuildException buildException) {
						throw new RuntimeException(buildException);
					}
				}

			});

		return task;
	}

}