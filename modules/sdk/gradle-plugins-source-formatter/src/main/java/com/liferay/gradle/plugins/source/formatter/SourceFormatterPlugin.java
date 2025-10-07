/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.source.formatter;

import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.file.FileCollection;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

/**
 * @author Raymond Augé
 * @author Andrea Di Giorgi
 */
public class SourceFormatterPlugin implements Plugin<Project> {

	public static final String CHECK_SOURCE_FORMATTING_TASK_NAME =
		"checkSourceFormatting";

	public static final String CONFIGURATION_NAME = "sourceFormatter";

	public static final String FORMAT_SOURCE_TASK_NAME = "formatSource";

	@Override
	public void apply(Project project) {
		Configuration sourceFormatterConfiguration =
			_addConfigurationSourceFormatter(project);

		_addTaskCheckSourceFormatting(project);
		_addTaskFormatSource(project);

		_configureTasksFormatSource(project, sourceFormatterConfiguration);
	}

	private Configuration _addConfigurationSourceFormatter(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					_addDependenciesSourceFormatter(project);
				}

			});

		configuration.setDescription(
			"Configures Liferay Source Formatter for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	private void _addDependenciesSourceFormatter(Project project) {
		GradleUtil.addDependency(
			project, CONFIGURATION_NAME, "com.liferay",
			"com.liferay.source.formatter", "latest.release");
	}

	private FormatSourceTask _addTaskCheckSourceFormatting(Project project) {
		FormatSourceTask formatSourceTask = GradleUtil.addTask(
			project, CHECK_SOURCE_FORMATTING_TASK_NAME, FormatSourceTask.class);

		formatSourceTask.onlyIf(_skipIfExecutingParentTaskSpec);
		formatSourceTask.setAutoFix(false);
		formatSourceTask.setDescription(
			"Checks the source formatting of this project.");
		formatSourceTask.setFailOnAutoFix(true);
		formatSourceTask.setFailOnHasWarning(true);
		formatSourceTask.setGroup(LifecycleBasePlugin.VERIFICATION_GROUP);
		formatSourceTask.setPrintErrors(true);

		return formatSourceTask;
	}

	private FormatSourceTask _addTaskFormatSource(Project project) {
		FormatSourceTask formatSourceTask = GradleUtil.addTask(
			project, FORMAT_SOURCE_TASK_NAME, FormatSourceTask.class);

		formatSourceTask.onlyIf(_skipIfExecutingParentTaskSpec);
		formatSourceTask.setDescription(
			"Runs Liferay Source Formatter to format the project files.");
		formatSourceTask.setGroup("formatting");

		return formatSourceTask;
	}

	private void _configureTaskFormatSource(
		FormatSourceTask formatSourceTask, FileCollection classpath) {

		formatSourceTask.setClasspath(classpath);

		String baseDirName = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "source.base.dir");

		if (Validator.isNotNull(baseDirName)) {
			formatSourceTask.setBaseDirName(baseDirName);
		}

		String checkCategoryNames = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "source.check.category.names");

		if (Validator.isNotNull(checkCategoryNames)) {
			formatSourceTask.setCheckCategoryNames(
				checkCategoryNames.split(","));
		}

		String checkNames = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "source.check.names");

		if (Validator.isNotNull(checkNames)) {
			formatSourceTask.setCheckNames(checkNames.split(","));
		}

		String fileExtensions = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "file.extensions");

		if (Validator.isNotNull(fileExtensions)) {
			formatSourceTask.setFileExtensions(fileExtensions.split(","));
		}

		String fileNames = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "file.names");

		if (Validator.isNotNull(fileNames)) {
			formatSourceTask.setFileNames(fileNames.split(","));
		}

		String formatCurrentBranch = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "format.current.branch");

		if (Validator.isNotNull(formatCurrentBranch)) {
			formatSourceTask.setFormatCurrentBranch(
				Boolean.parseBoolean(formatCurrentBranch));
		}

		String formatLatestAuthor = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "format.latest.author");

		if (Validator.isNotNull(formatLatestAuthor)) {
			formatSourceTask.setFormatLatestAuthor(
				Boolean.parseBoolean(formatLatestAuthor));
		}

		String formatLocalChanges = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "format.local.changes");

		if (Validator.isNotNull(formatLocalChanges)) {
			formatSourceTask.setFormatLocalChanges(
				Boolean.parseBoolean(formatLocalChanges));
		}

		String javaParserEnabled = GradleUtil.getTaskPrefixedProperty(
			formatSourceTask, "java.parser.enabled");

		if (Validator.isNotNull(javaParserEnabled)) {
			formatSourceTask.setJavaParserEnabled(
				Boolean.parseBoolean(javaParserEnabled));
		}
	}

	private void _configureTasksFormatSource(
		Project project, final FileCollection classpath) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			FormatSourceTask.class,
			new Action<FormatSourceTask>() {

				@Override
				public void execute(FormatSourceTask formatSourceTask) {
					_configureTaskFormatSource(formatSourceTask, classpath);
				}

			});
	}

	private static final Spec<Task> _skipIfExecutingParentTaskSpec =
		new Spec<Task>() {

			@Override
			public boolean isSatisfiedBy(Task task) {
				Project project = task.getProject();

				Gradle gradle = project.getGradle();

				TaskExecutionGraph taskExecutionGraph = gradle.getTaskGraph();

				Project parentProject = project;

				while ((parentProject = parentProject.getParent()) != null) {
					TaskContainer parentProjectTaskContainer =
						parentProject.getTasks();

					Task parentProjectTask =
						parentProjectTaskContainer.findByName(task.getName());

					if ((parentProjectTask != null) &&
						taskExecutionGraph.hasTask(parentProjectTask)) {

						return false;
					}
				}

				return true;
			}

		};

}