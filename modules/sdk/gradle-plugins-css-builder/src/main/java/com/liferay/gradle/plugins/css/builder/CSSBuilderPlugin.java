/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.css.builder;

import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.bundling.War;
import org.gradle.language.jvm.tasks.ProcessResources;

/**
 * @author Andrea Di Giorgi
 */
public class CSSBuilderPlugin implements Plugin<Project> {

	public static final String BUILD_CSS_TASK_NAME = "buildCSS";

	public static final String COPY_CSS_TASK_NAME = "copyCSS";

	public static final String CSS_BUILDER_CONFIGURATION_NAME = "cssBuilder";

	public static final String PORTAL_COMMON_CSS_CONFIGURATION_NAME =
		"portalCommonCSS";

	@Override
	public void apply(Project project) {
		Configuration cssBuilderConfiguration = _addConfigurationCSSBuilder(
			project);
		Configuration portalCommonCSSConfiguration =
			_addConfigurationPortalCommonCSS(project);

		Sync copyCSSTask = _addTaskCopyCSS(project);

		BuildCSSTask buildCSSTask = _addTaskBuildCSS(project, copyCSSTask);

		_configureTasksBuildCSS(
			project, cssBuilderConfiguration, portalCommonCSSConfiguration);

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			JavaLibraryPlugin.class,
			new Action<JavaLibraryPlugin>() {

				@Override
				public void execute(JavaLibraryPlugin javaLibraryPlugin) {
					_configureTaskProcessResourcesForJavaLibraryPlugin(
						buildCSSTask, copyCSSTask);
				}

			});

		pluginContainer.withType(
			WarPlugin.class,
			new Action<WarPlugin>() {

				@Override
				public void execute(WarPlugin warPlugin) {
					_configureTaskWarForWarPlugin(buildCSSTask, copyCSSTask);
				}

			});

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureTaskBuildCSSJvmArgs(buildCSSTask);
				}

			});
	}

	private Configuration _addConfigurationCSSBuilder(final Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, CSS_BUILDER_CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					_addDependenciesCSSBuilder(project);
				}

			});

		configuration.setDescription(
			"Configures Liferay CSS Builder for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	private Configuration _addConfigurationPortalCommonCSS(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, PORTAL_COMMON_CSS_CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures com.liferay.frontend.css.common for compiling CSS " +
				"files.");
		configuration.setTransitive(false);
		configuration.setVisible(false);

		GradleUtil.executeIfEmpty(
			configuration,
			new Action<Configuration>() {

				@Override
				public void execute(Configuration configuration) {
					_addDependenciesPortalCommonCSS(project);
				}

			});

		return configuration;
	}

	private void _addDependenciesCSSBuilder(Project project) {
		GradleUtil.addDependency(
			project, CSS_BUILDER_CONFIGURATION_NAME, "com.liferay",
			"com.liferay.css.builder", "latest.release");
	}

	private void _addDependenciesPortalCommonCSS(Project project) {
		GradleUtil.addDependency(
			project, PORTAL_COMMON_CSS_CONFIGURATION_NAME, "com.liferay",
			"com.liferay.frontend.css.common", "latest.release", false);
		GradleUtil.addDependency(
			project, PORTAL_COMMON_CSS_CONFIGURATION_NAME, "org.webjars",
			"font-awesome", "latest.release", false);
	}

	private BuildCSSTask _addTaskBuildCSS(
		Project project, final Sync copyCSSTask) {

		BuildCSSTask buildCSSTask = GradleUtil.addTask(
			project, BUILD_CSS_TASK_NAME, BuildCSSTask.class);

		buildCSSTask.dependsOn(copyCSSTask);

		buildCSSTask.setBaseDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return copyCSSTask.getDestinationDir();
				}

			});

		buildCSSTask.setDescription("Build CSS files.");
		buildCSSTask.setGroup(BasePlugin.BUILD_GROUP);

		TaskOutputs taskOutputs = buildCSSTask.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});

		return buildCSSTask;
	}

	private Sync _addTaskCopyCSS(Project project) {
		final Sync copyCSSTask = GradleUtil.addTask(
			project, COPY_CSS_TASK_NAME, Sync.class);

		copyCSSTask.setDescription("Copies CSS files to a temp directory.");

		copyCSSTask.include("**/*.css", "**/*.scss");

		copyCSSTask.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return copyCSSTask.getTemporaryDir();
				}

			});

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			JavaLibraryPlugin.class,
			new Action<JavaLibraryPlugin>() {

				@Override
				public void execute(JavaLibraryPlugin javaLibraryPlugin) {
					_configureTaskCopyCSSForJavaLibraryPlugin(copyCSSTask);
				}

			});

		pluginContainer.withType(
			WarPlugin.class,
			new Action<WarPlugin>() {

				@Override
				public void execute(WarPlugin warPlugin) {
					_configureTaskCopyCSSForWarPlugin(copyCSSTask);
				}

			});

		return copyCSSTask;
	}

	private void _configureTaskBuildCSSClasspath(
		BuildCSSTask buildCSSTask, FileCollection classpath) {

		buildCSSTask.setClasspath(classpath);
	}

	private void _configureTaskBuildCSSImportFile(
		BuildCSSTask buildCSSTask,
		final Configuration portalCommonCSSConfiguration) {

		buildCSSTask.setImports(
			new Callable<FileCollection>() {

				@Override
				public FileCollection call() throws Exception {
					return portalCommonCSSConfiguration;
				}

			});
	}

	private void _configureTaskBuildCSSJvmArgs(BuildCSSTask buildCSSTask) {
		if (Objects.equals(buildCSSTask.getSassCompilerClassName(), "ruby")) {
			buildCSSTask.jvmArgs("-Xss4096k");
		}
	}

	private void _configureTaskCopyCSSForJavaLibraryPlugin(
		final Sync copyCSSTask) {

		copyCSSTask.from(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File dir = _getResourcesDir(copyCSSTask.getProject());

					if (!dir.exists()) {
						return null;
					}

					return dir;
				}

			});
	}

	private void _configureTaskCopyCSSForWarPlugin(final Sync copyCSSTask) {
		copyCSSTask.from(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File dir = _getWebAppDir(copyCSSTask.getProject());

					if (!dir.exists()) {
						return null;
					}

					return dir;
				}

			});
	}

	private void _configureTaskProcessResourcesForJavaLibraryPlugin(
		BuildCSSTask buildCSSTask, final Sync copyCSSTask) {

		final Project project = buildCSSTask.getProject();

		ProcessResources processResourcesTask =
			(ProcessResources)GradleUtil.getTask(
				project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

		processResourcesTask.dependsOn(buildCSSTask);

		processResourcesTask.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);

		processResourcesTask.from(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File resourcesDir = _getResourcesDir(project);

					if (!resourcesDir.exists()) {
						return null;
					}

					return copyCSSTask.getDestinationDir();
				}

			});
	}

	private void _configureTaskWarForWarPlugin(
		final BuildCSSTask buildCSSTask, final Sync copyCSSTask) {

		War war = (War)GradleUtil.getTask(
			buildCSSTask.getProject(), WarPlugin.WAR_TASK_NAME);

		war.dependsOn(buildCSSTask);

		war.eachFile(
			new Action<FileCopyDetails>() {

				@Override
				public void execute(FileCopyDetails fileCopyDetails) {
					String outputDirName = _normalizeDirName(
						buildCSSTask.getOutputDirName());

					String path = fileCopyDetails.getPath();

					fileCopyDetails.setPath(
						path.replace('/' + outputDirName + '/', "/"));
				}

				private String _normalizeDirName(String dirName) {
					String name = dirName.replace('\\', '/');

					if (name.charAt(0) == '/') {
						name = name.substring(1);
					}

					if (name.charAt(name.length() - 1) == '/') {
						name = name.substring(0, name.length() - 1);
					}

					return name;
				}

			});

		war.exclude("**/*.scss");

		war.from(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return copyCSSTask.getDestinationDir();
				}

			});

		war.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
		war.setIncludeEmptyDirs(false);
	}

	private void _configureTasksBuildCSS(
		Project project, final Configuration cssBuilderConfiguration,
		final Configuration portalCommonCSSConfiguration) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			BuildCSSTask.class,
			new Action<BuildCSSTask>() {

				@Override
				public void execute(BuildCSSTask buildCSSTask) {
					_configureTaskBuildCSSClasspath(
						buildCSSTask, cssBuilderConfiguration);
					_configureTaskBuildCSSImportFile(
						buildCSSTask, portalCommonCSSConfiguration);
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

	private File _getWebAppDir(Project project) {
		WarPluginConvention warPluginConvention = GradleUtil.getConvention(
			project, WarPluginConvention.class);

		return warPluginConvention.getWebAppDir();
	}

}