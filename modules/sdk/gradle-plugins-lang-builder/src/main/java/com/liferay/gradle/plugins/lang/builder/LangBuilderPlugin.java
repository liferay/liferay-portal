/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.lang.builder;

import com.liferay.gradle.plugins.lang.builder.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import java.io.File;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskOutputs;

/**
 * @author Andrea Di Giorgi
 */
public class LangBuilderPlugin implements Plugin<Project> {

	public static final String APP_BUILD_LANG_TASK_NAME = "appBuildLang";

	public static final String BUILD_LANG_TASK_NAME = "buildLang";

	public static final String CONFIGURATION_NAME = "langBuilder";

	@Override
	public void apply(Project project) {
		Configuration langBuilderConfiguration = _addConfigurationLangBuilder(
			project);

		BuildLangTask buildLangTask = _addTaskBuildLang(project);

		_configureTaskBuildLang(buildLangTask);

		_configureTasksBuildLang(project, langBuilderConfiguration);
	}

	private Configuration _addConfigurationLangBuilder(final Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					_addDependenciesLangBuilder(project);
				}

			});

		configuration.setDescription(
			"Configures Liferay Lang Builder for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	private void _addDependenciesLangBuilder(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "com.liferay",
			"com.liferay.lang.builder", "latest.release");
	}

	private BuildLangTask _addTaskAppBuildLang(
		Project project, File appLangFile) {

		BuildLangTask buildLangTask = GradleUtil.addTask(
			project, APP_BUILD_LANG_TASK_NAME, BuildLangTask.class);

		buildLangTask.setDescription(
			"Runs Liferay Lang Builder to translate language property files " +
				"for the app.");
		buildLangTask.setLangDir(appLangFile.getParentFile());
		buildLangTask.setLangFileName("bundle");

		TaskOutputs taskOutputs = buildLangTask.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});

		return buildLangTask;
	}

	private BuildLangTask _addTaskBuildLang(Project project) {
		final BuildLangTask buildLangTask = GradleUtil.addTask(
			project, BUILD_LANG_TASK_NAME, BuildLangTask.class);

		buildLangTask.setDescription(
			"Runs Liferay Lang Builder to translate language property files.");
		buildLangTask.setGroup(BasePlugin.BUILD_GROUP);

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			JavaLibraryPlugin.class,
			new Action<JavaLibraryPlugin>() {

				@Override
				public void execute(JavaLibraryPlugin javaLibraryPlugin) {
					_configureTaskBuildLangForJavaLibraryPlugin(buildLangTask);
				}

			});

		TaskOutputs taskOutputs = buildLangTask.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});

		return buildLangTask;
	}

	private void _configureTaskBuildLang(BuildLangTask buildLangTask) {
		Project appProject = GradleUtil.getAppProject(
			buildLangTask.getProject());

		if (appProject != null) {
			File appLangFile = new File(
				appProject.getProjectDir(),
				"app.bnd-localization/bundle.properties");

			if (appLangFile.exists()) {
				BuildLangTask appBuildLangTask = null;

				if (GradleUtil.hasTask(appProject, APP_BUILD_LANG_TASK_NAME)) {
					appBuildLangTask = (BuildLangTask)GradleUtil.getTask(
						appProject, APP_BUILD_LANG_TASK_NAME);
				}
				else {
					appBuildLangTask = _addTaskAppBuildLang(
						appProject, appLangFile);
				}

				buildLangTask.dependsOn(appBuildLangTask);
			}
		}
	}

	private void _configureTaskBuildLangClasspath(
		BuildLangTask buildLangTask, FileCollection fileCollection) {

		buildLangTask.setClasspath(fileCollection);

		Project appProject = GradleUtil.getAppProject(
			buildLangTask.getProject());

		if ((appProject != null) &&
			GradleUtil.hasTask(appProject, APP_BUILD_LANG_TASK_NAME)) {

			BuildLangTask appBuildLangTask = (BuildLangTask)GradleUtil.getTask(
				appProject, APP_BUILD_LANG_TASK_NAME);

			appBuildLangTask.setClasspath(fileCollection);
		}
	}

	private void _configureTaskBuildLangForJavaLibraryPlugin(
		final BuildLangTask buildLangTask) {

		buildLangTask.setLangDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(
						_getResourcesDir(buildLangTask.getProject()),
						"content");
				}

			});

		_configureTaskProcessResources(buildLangTask.getProject());
	}

	@SuppressWarnings("serial")
	private void _configureTaskProcessResources(Project project) {
		File appDir = GradleUtil.getRootDir(project, "app.bnd");

		final File appBndLocalizationDir = new File(
			appDir, "app.bnd-localization");

		if (!appBndLocalizationDir.exists()) {
			return;
		}

		Copy copy = (Copy)GradleUtil.getTask(
			project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

		final Map<String, String> relengProperties = new HashMap<>();

		Map<String, ?> projectProperties = project.getProperties();

		for (Map.Entry<String, ?> entry : projectProperties.entrySet()) {
			Object value = entry.getValue();

			if (!(value instanceof String)) {
				continue;
			}

			String key = entry.getKey();

			if (!key.startsWith("liferay.releng")) {
				continue;
			}

			relengProperties.put("${" + key + "}", (String)value);
		}

		final Action<FileCopyDetails> action = new Action<FileCopyDetails>() {

			@Override
			public void execute(FileCopyDetails fileCopyDetails) {
				fileCopyDetails.filter(
					new Closure<Void>(copy) {

						@SuppressWarnings("unused")
						public String doCall(String line) {
							if (Validator.isNull(line)) {
								return line;
							}

							for (Map.Entry<String, String> entry :
									relengProperties.entrySet()) {

								line = line.replace(
									entry.getKey(), entry.getValue());
							}

							return line;
						}

					});
			}

		};

		copy.from(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return appBndLocalizationDir;
				}

			},
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(CopySpec copySpec) {
					copySpec.eachFile(action);
					copySpec.into("OSGI-INF/l10n");
				}

			});
	}

	private void _configureTasksBuildLang(
		Project project, final Configuration langBuilderConfiguration) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			BuildLangTask.class,
			new Action<BuildLangTask>() {

				@Override
				public void execute(BuildLangTask buildLangTask) {
					_configureTaskBuildLangClasspath(
						buildLangTask, langBuilderConfiguration);
				}

			});
	}

	private File _getResourcesDir(Project project) {
		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.MAIN_SOURCE_SET_NAME);

		return _getSrcDir(sourceSet.getResources());
	}

	private File _getSrcDir(SourceDirectorySet sourceDirectorySet) {
		Set<File> srcDirs = sourceDirectorySet.getSrcDirs();

		Iterator<File> iterator = srcDirs.iterator();

		return iterator.next();
	}

}