/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import com.liferay.gradle.plugins.jasper.jspc.CompileJSPTask;
import com.liferay.gradle.plugins.jasper.jspc.JspCPlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;

import java.net.URLClassLoader;

import java.util.HashMap;
import java.util.Map;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Drew Brokke
 */
public class LiferayJspCompatibilityPlugin implements Plugin<Project> {

	@Override
	public void apply(Project project) {
		PluginManager pluginManager = project.getPluginManager();

		pluginManager.withPlugin(
			"com.liferay.jasper.jspc",
			plugin -> {
				_configureCompileJspClasspathConfiguration(project);
				_configureJspCConfiguration(project);
			});
	}

	private void _configureCompileJspClasspathConfiguration(Project project) {
		Configuration compileJspClasspathConfiguration =
			GradleUtil.addConfiguration(project, "compileJspClasspath");

		String configurationName = compileJspClasspathConfiguration.getName();

		ClassLoader classLoader = Project.class.getClassLoader();

		if (classLoader instanceof URLClassLoader) {
			URLClassLoader urlClassLoader = (URLClassLoader)classLoader;

			GradleUtil.addDependency(
				project, configurationName,
				project.files(urlClassLoader.getURLs()));
		}

		GradleUtil.addDependency(
			project, configurationName, "org.apache.tomcat", "tomcat-jasper",
			"9.0.104");
		GradleUtil.addDependency(
			project, configurationName, "com.liferay",
			"com.liferay.gradle.plugins.jasper.jspc", "3.0.0", false);

		TaskContainer tasks = project.getTasks();

		tasks.withType(
			CompileJSPTask.class,
			compileJSP -> compileJSP.setCompileJspClasspath(
				GradleUtil.getConfiguration(project, configurationName)));
	}

	private void _configureJspCConfiguration(Project project) {
		Configuration configuration = GradleUtil.getConfiguration(
			project, JspCPlugin.CONFIGURATION_NAME);

		configuration.exclude(
			_getExcludeMap(
				"jakarta.servlet.jsp.jstl", "jakarta.servlet.jsp.jstl-api"));
		configuration.exclude(
			_getExcludeMap("org.glassfish.web", "jakarta.servlet.jsp.jstl"));

		ResolutionStrategy resolutionStrategy =
			configuration.getResolutionStrategy();

		resolutionStrategy.force("org.apache.tomcat:tomcat-jasper:9.0.104");

		GradleUtil.addDependency(
			project, JspCPlugin.CONFIGURATION_NAME, "javax.servlet.jsp.jstl",
			"javax.servlet.jsp.jstl-api", "1.2.1");
		GradleUtil.addDependency(
			project, JspCPlugin.CONFIGURATION_NAME, "org.glassfish.web",
			"javax.servlet.jsp.jstl", "1.2.3", false);
	}

	private Map<String, String> _getExcludeMap(String group, String module) {
		return new HashMap<String, String>() {
			{
				put("group", group);
				put("module", module);
			}
		};
	}

}