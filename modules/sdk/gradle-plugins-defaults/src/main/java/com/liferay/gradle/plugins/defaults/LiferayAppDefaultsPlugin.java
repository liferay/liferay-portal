/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults;

import com.liferay.gradle.plugins.NodeDefaultsPlugin;
import com.liferay.gradle.plugins.app.javadoc.builder.AppJavadocBuilderExtension;
import com.liferay.gradle.plugins.app.javadoc.builder.AppJavadocBuilderPlugin;
import com.liferay.gradle.plugins.defaults.internal.LiferayRelengPlugin;
import com.liferay.gradle.plugins.defaults.internal.util.FileUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradlePluginsDefaultsUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.defaults.internal.util.LiferayRelengUtil;
import com.liferay.gradle.plugins.defaults.task.WritePropertiesTask;
import com.liferay.gradle.plugins.jsdoc.AppJSDocConfigurationExtension;
import com.liferay.gradle.plugins.jsdoc.AppJSDocPlugin;
import com.liferay.gradle.plugins.jsdoc.JSDocTask;
import com.liferay.gradle.plugins.tlddoc.builder.AppTLDDocBuilderExtension;
import com.liferay.gradle.plugins.tlddoc.builder.AppTLDDocBuilderPlugin;
import com.liferay.gradle.plugins.tlddoc.builder.task.TLDDocTask;
import com.liferay.gradle.util.Validator;

import groovy.json.JsonOutput;

import groovy.lang.Closure;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.resources.ResourceHandler;
import org.gradle.api.resources.TextResourceFactory;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.execution.ProjectConfigurer;
import org.gradle.internal.service.ServiceRegistry;
import org.gradle.util.GUtil;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayAppDefaultsPlugin implements Plugin<Project> {

	public static final String WRITE_APP_PACKAGE_JSON_FILE_TASK_NAME =
		"writeAppPackageJsonFile";

	@Override
	public void apply(Project project) {
		String appDescription = GradleUtil.getProperty(
			project, "app.description", (String)null);
		String appTitle = GradleUtil.getProperty(
			project, "app.title", (String)null);
		String appVersion = GradleUtil.getProperty(
			project, "app.version", (String)null);

		if (Validator.isNull(appDescription)) {
			File appBndFile = project.file("app.bnd");

			if (appBndFile.exists()) {
				Properties properties = GUtil.loadProperties(appBndFile);

				appDescription = properties.getProperty(
					"Liferay-Releng-App-Description");
			}
		}

		Properties appProperties = null;

		Project privateProject = project.findProject(
			":private" + project.getPath());

		if (privateProject != null) {
			appProperties = _getAppProperties(privateProject);
		}

		if (appProperties == null) {
			appProperties = _getAppProperties(project);
		}

		if (appProperties != null) {
			if (Validator.isNull(appTitle)) {
				appTitle = appProperties.getProperty("app.marketplace.title");
			}

			if (Validator.isNull(appVersion)) {
				appVersion = appProperties.getProperty(
					"app.marketplace.version");
			}
		}

		_applyPlugins(project);

		File portalRootDir = GradleUtil.getRootDir(
			project.getRootProject(), "portal-impl");

		GradlePluginsDefaultsUtil.configureRepositories(project, portalRootDir);

		Task writeAppPackageJsonFileTask = _addTaskWriteAppPackageJsonFile(
			project, appTitle, appDescription, appVersion);

		_configureAppJSDoc(project, privateProject);
		_configureAppJavadocBuilder(project, privateProject);
		_configureAppTLDDocBuilder(project, privateProject);
		_configureProject(project, appDescription, appVersion);
		_configureTaskAppJSDoc(writeAppPackageJsonFileTask);
		_configureTaskAppJavadoc(project, appTitle, appVersion);
		_configureTaskAppTlddoc(project, portalRootDir);

		if (privateProject != null) {
			Gradle gradle = project.getGradle();

			StartParameter startParameter = gradle.getStartParameter();

			List<String> taskNames = startParameter.getTaskNames();

			if (taskNames.contains(AppJSDocPlugin.APP_JSDOC_TASK_NAME) ||
				taskNames.contains(AppJSDocPlugin.JAR_APP_JSDOC_TASK_NAME) ||
				taskNames.contains(
					AppJavadocBuilderPlugin.APP_JAVADOC_TASK_NAME) ||
				taskNames.contains(
					AppJavadocBuilderPlugin.JAR_APP_JAVADOC_TASK_NAME) ||
				taskNames.contains(
					AppTLDDocBuilderPlugin.APP_TLDDOC_TASK_NAME) ||
				taskNames.contains(
					AppTLDDocBuilderPlugin.JAR_APP_TLDDOC_TASK_NAME)) {

				_forceProjectHierarchyEvaluation(privateProject);
			}
		}
	}

	private Task _addTaskWriteAppPackageJsonFile(
		final Project project, String appTitle, String appDescription,
		String appVersion) {

		Task task = project.task(WRITE_APP_PACKAGE_JSON_FILE_TASK_NAME);

		final Map<String, String> packageJsonMap = new HashMap<>();

		packageJsonMap.put("description", appDescription);
		packageJsonMap.put("name", appTitle);
		packageJsonMap.put("version", appVersion);

		task.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					String packageJSON = JsonOutput.toJson(packageJsonMap);

					File file = GradleUtil.getOutputFile(task);

					Path path = file.toPath();

					try {
						Files.createDirectories(path.getParent());

						Files.write(
							file.toPath(),
							packageJSON.getBytes(StandardCharsets.UTF_8));
					}
					catch (IOException ioException) {
						throw new UncheckedIOException(ioException);
					}
				}

			});

		task.setDescription(
			"Writes a temporary package.json file for the app.");

		TaskInputs taskInputs = task.getInputs();

		taskInputs.properties(packageJsonMap);

		TaskOutputs taskOutputs = task.getOutputs();

		taskOutputs.file(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(project.getBuildDir(), "app-package.json");
				}

			});

		return task;
	}

	private void _applyPlugins(Project project) {
		GradleUtil.applyPlugin(project, AppJSDocPlugin.class);
		GradleUtil.applyPlugin(project, AppJavadocBuilderPlugin.class);
		GradleUtil.applyPlugin(project, AppTLDDocBuilderPlugin.class);
		GradleUtil.applyPlugin(project, NodeDefaultsPlugin.class);
	}

	private void _configureAppJSDoc(Project project, Project privateProject) {
		if (privateProject == null) {
			return;
		}

		AppJSDocConfigurationExtension appJSDocConfigurationExtension =
			GradleUtil.getExtension(
				project, AppJSDocConfigurationExtension.class);

		appJSDocConfigurationExtension.subprojects(
			privateProject.getSubprojects());
	}

	@SuppressWarnings("serial")
	private void _configureAppJavadocBuilder(
		Project project, Project privateProject) {

		AppJavadocBuilderExtension appJavadocBuilderExtension =
			GradleUtil.getExtension(project, AppJavadocBuilderExtension.class);

		appJavadocBuilderExtension.onlyIf(
			new Spec<Project>() {

				@Override
				public boolean isSatisfiedBy(Project project) {
					TaskContainer taskContainer = project.getTasks();

					WritePropertiesTask recordArtifactTask =
						(WritePropertiesTask)taskContainer.findByName(
							LiferayRelengPlugin.RECORD_ARTIFACT_TASK_NAME);

					if (recordArtifactTask != null) {
						File artifactPropertiesFile =
							recordArtifactTask.getOutputFile();

						if (artifactPropertiesFile.exists()) {
							return true;
						}
					}

					return false;
				}

			});

		appJavadocBuilderExtension.setGroupNameClosure(
			new Closure<String>(project) {

				@SuppressWarnings("unused")
				public String doCall(Project subproject) {
					return _getAppJavadocGroupName(subproject);
				}

			});

		if (privateProject != null) {
			appJavadocBuilderExtension.subprojects(
				privateProject.getSubprojects());
		}
	}

	private void _configureAppTLDDocBuilder(
		Project project, Project privateProject) {

		if (privateProject == null) {
			return;
		}

		AppTLDDocBuilderExtension appTLDDocBuilderExtension =
			GradleUtil.getExtension(project, AppTLDDocBuilderExtension.class);

		appTLDDocBuilderExtension.subprojects(privateProject.getSubprojects());
	}

	private void _configureProject(
		Project project, String description, String version) {

		if (Validator.isNotNull(description)) {
			project.setDescription(description);
		}

		if (Validator.isNotNull(version)) {
			project.setVersion(version);
		}
	}

	private void _configureTaskAppJSDoc(
		final Task writeAppPackageJsonFileTask) {

		Project project = writeAppPackageJsonFileTask.getProject();

		JSDocTask jsDocTask = (JSDocTask)GradleUtil.getTask(
			project, AppJSDocPlugin.APP_JSDOC_TASK_NAME);

		ResourceHandler resourceHandler = project.getResources();

		TextResourceFactory textResourceFactory = resourceHandler.getText();

		jsDocTask.setConfiguration(
			textResourceFactory.fromString(_APP_JSDOC_CONFIG_JSON));

		if (jsDocTask.getPackageJsonFile() == null) {
			jsDocTask.dependsOn(writeAppPackageJsonFileTask);

			jsDocTask.setPackageJsonFile(
				new Callable<File>() {

					@Override
					public File call() throws Exception {
						return GradleUtil.getOutputFile(
							writeAppPackageJsonFileTask);
					}

				});
		}
	}

	private void _configureTaskAppJavadoc(
		Project project, String appTitle, String appVersion) {

		if (Validator.isNotNull(appTitle) && Validator.isNotNull(appVersion)) {
			Javadoc javadoc = (Javadoc)GradleUtil.getTask(
				project, AppJavadocBuilderPlugin.APP_JAVADOC_TASK_NAME);

			javadoc.setTitle(String.format("%s %s API", appTitle, appVersion));
		}
	}

	private void _configureTaskAppTlddoc(Project project, File portalRootDir) {
		if (portalRootDir == null) {
			return;
		}

		TLDDocTask tldDocTask = (TLDDocTask)GradleUtil.getTask(
			project, AppTLDDocBuilderPlugin.APP_TLDDOC_TASK_NAME);

		File xsltDir = new File(portalRootDir, "tools/styles/taglibs");

		tldDocTask.setXsltDir(xsltDir);
	}

	private void _forceProjectHierarchyEvaluation(Project project) {
		GradleInternal gradleInternal = (GradleInternal)project.getGradle();

		ServiceRegistry serviceRegistry = gradleInternal.getServices();

		ProjectConfigurer projectConfigurer = serviceRegistry.get(
			ProjectConfigurer.class);

		projectConfigurer.configureHierarchy((ProjectInternal)project);
	}

	private String _getAppJavadocGroupName(Project project) {
		String groupName = project.getDescription();

		if (Validator.isNull(groupName)) {
			groupName = project.getName();
		}

		TaskContainer taskContainer = project.getTasks();

		WritePropertiesTask recordArtifactTask =
			(WritePropertiesTask)taskContainer.findByName(
				LiferayRelengPlugin.RECORD_ARTIFACT_TASK_NAME);

		if (recordArtifactTask != null) {
			String artifactURL = null;

			File artifactPropertiesFile = recordArtifactTask.getOutputFile();

			if (artifactPropertiesFile.exists()) {
				Properties properties = GUtil.loadProperties(
					artifactPropertiesFile);

				artifactURL = properties.getProperty("artifact.url");
			}

			if (Validator.isNotNull(artifactURL)) {
				int start = artifactURL.lastIndexOf('/') + 1;
				int end = artifactURL.lastIndexOf('.');

				int pos = artifactURL.indexOf('-', start);

				String moduleName = artifactURL.substring(start, pos);
				String moduleVersion = artifactURL.substring(pos + 1, end);

				StringBuilder sb = new StringBuilder();

				sb.append(groupName);
				sb.append(" - com.liferay:");
				sb.append(moduleName);
				sb.append(':');
				sb.append(moduleVersion);

				groupName = sb.toString();
			}
		}

		return groupName;
	}

	private Properties _getAppProperties(Project project) {
		File relengDir = LiferayRelengUtil.getRelengDir(project);

		if (relengDir != null) {
			File appPropertiesFile = new File(relengDir, "app.properties");

			if (appPropertiesFile.exists()) {
				return GUtil.loadProperties(appPropertiesFile);
			}
		}

		return null;
	}

	private static final String _APP_JSDOC_CONFIG_JSON;

	static {
		try {
			_APP_JSDOC_CONFIG_JSON = FileUtil.read(
				"com/liferay/gradle/plugins/defaults/internal/dependencies" +
					"/config-jsdoc.json");
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

}