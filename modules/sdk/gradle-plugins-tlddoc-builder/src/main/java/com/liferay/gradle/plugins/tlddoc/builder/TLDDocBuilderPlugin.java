/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.tlddoc.builder;

import com.liferay.gradle.plugins.tlddoc.builder.task.TLDDocTask;
import com.liferay.gradle.plugins.tlddoc.builder.task.ValidateSchemaTask;
import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.Collections;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Andrea Di Giorgi
 */
public class TLDDocBuilderPlugin implements Plugin<Project> {

	public static final String CONFIGURATION_NAME = "tlddoc";

	public static final String COPY_TLDDOC_RESOURCES_TASK_NAME =
		"copyTLDDocResources";

	public static final String TLDDOC_TASK_NAME = "tlddoc";

	public static final String VALIDATE_TLD_TASK_NAME = "validateTLD";

	public static final String XML_PARSER_CONFIGURATION_NAME = "xmlParser";

	@Override
	public void apply(Project project) {
		Configuration tlddocConfiguration = addConfigurationTLDDoc(project);

		ValidateSchemaTask validateTLDTask = _addTaskValidateTLD(project);

		Copy copyTLDDocResourcesTask = _addTaskCopyTLDDocResources(project);

		_addTaskTLDDoc(copyTLDDocResourcesTask, validateTLDTask);

		_configureTasksTLDDoc(project, tlddocConfiguration);
	}

	protected static Configuration addConfigurationTLDDoc(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					_addDependenciesTLDDoc(project);
				}

			});

		configuration.setDescription(
			"Configures Tag Library Documentation Generator for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	private static void _addDependenciesTLDDoc(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "taglibrarydoc", "tlddoc", "1.3");
	}

	private Copy _addTaskCopyTLDDocResources(final Project project) {
		Copy copy = GradleUtil.addTask(
			project, COPY_TLDDOC_RESOURCES_TASK_NAME, Copy.class);

		copy.from("src/main/tlddoc");

		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					TLDDocTask tldDocTask = (TLDDocTask)GradleUtil.getTask(
						project, TLDDOC_TASK_NAME);

					return tldDocTask.getDestinationDir();
				}

			});

		copy.setDescription("Copies tag library documentation resources.");

		return copy;
	}

	private TLDDocTask _addTaskTLDDoc(
		Copy copyTLDDocResourcesTask, ValidateSchemaTask validateTLDTask) {

		Project project = copyTLDDocResourcesTask.getProject();

		final TLDDocTask tldDocTask = GradleUtil.addTask(
			project, TLDDOC_TASK_NAME, TLDDocTask.class);

		tldDocTask.dependsOn(copyTLDDocResourcesTask, validateTLDTask);
		tldDocTask.setDescription("Generates tag library documentation.");
		tldDocTask.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			JavaLibraryPlugin.class,
			new Action<JavaLibraryPlugin>() {

				@Override
				public void execute(JavaLibraryPlugin javaLibraryPlugin) {
					_configureTaskTLDDocForJavaLibraryPlugin(tldDocTask);
				}

			});

		return tldDocTask;
	}

	private ValidateSchemaTask _addTaskValidateTLD(Project project) {
		final ValidateSchemaTask validateSchemaTask = GradleUtil.addTask(
			project, VALIDATE_TLD_TASK_NAME, ValidateSchemaTask.class);

		validateSchemaTask.setDescription("Validates TLD files.");

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			JavaLibraryPlugin.class,
			new Action<JavaLibraryPlugin>() {

				@Override
				public void execute(JavaLibraryPlugin javaLibraryPlugin) {
					_configureTaskValidateSchemaForJavaLibraryPlugin(
						validateSchemaTask);
				}

			});

		return validateSchemaTask;
	}

	private void _configureTaskTLDDocClasspath(
		TLDDocTask tldDocTask, FileCollection fileCollection) {

		tldDocTask.setClasspath(fileCollection);
	}

	private void _configureTaskTLDDocForJavaLibraryPlugin(
		TLDDocTask tldDocTask) {

		final Project project = tldDocTask.getProject();

		tldDocTask.setDestinationDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					JavaPluginConvention javaPluginConvention =
						GradleUtil.getConvention(
							project, JavaPluginConvention.class);

					return new File(
						javaPluginConvention.getDocsDir(), "tlddoc");
				}

			});

		tldDocTask.setIncludes(Collections.singleton("**/*.tld"));

		tldDocTask.setSource(
			new Callable<Iterable<File>>() {

				@Override
				public Iterable<File> call() throws Exception {
					return _getResourceDirs(project);
				}

			});
	}

	private void _configureTaskValidateSchemaForJavaLibraryPlugin(
		ValidateSchemaTask validateSchemaTask) {

		final Project project = validateSchemaTask.getProject();

		validateSchemaTask.setIncludes(Collections.singleton("**/*.tld"));

		validateSchemaTask.setSource(
			new Callable<Iterable<File>>() {

				@Override
				public Iterable<File> call() throws Exception {
					return _getResourceDirs(project);
				}

			});
	}

	private void _configureTasksTLDDoc(
		Project project, final Configuration tlddocConfiguration) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			TLDDocTask.class,
			new Action<TLDDocTask>() {

				@Override
				public void execute(TLDDocTask tldDocTask) {
					_configureTaskTLDDocClasspath(
						tldDocTask, tlddocConfiguration);
				}

			});
	}

	private Iterable<File> _getResourceDirs(Project project) {
		SourceSet sourceSet = GradleUtil.getSourceSet(
			project, SourceSet.MAIN_SOURCE_SET_NAME);

		SourceDirectorySet sourceDirectorySet = sourceSet.getResources();

		return sourceDirectorySet.getSrcDirs();
	}

}