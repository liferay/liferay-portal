/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults;

import com.liferay.gradle.plugins.LiferayThemePlugin;
import com.liferay.gradle.plugins.cache.WriteDigestTask;
import com.liferay.gradle.plugins.defaults.extensions.LiferayThemeDefaultsExtension;
import com.liferay.gradle.plugins.defaults.internal.LiferayRelengPlugin;
import com.liferay.gradle.plugins.defaults.internal.util.FileUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradlePluginsDefaultsUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.defaults.internal.util.IncrementVersionClosure;
import com.liferay.gradle.plugins.defaults.task.ReplaceRegexTask;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.node.NodePlugin;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.PackageRunBuildTask;
import com.liferay.gradle.plugins.util.PortalTools;
import com.liferay.gradle.util.copy.StripPathSegmentsAction;

import groovy.lang.Closure;

import java.io.File;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.publish.PublicationContainer;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.util.GUtil;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayThemeDefaultsPlugin implements Plugin<Project> {

	public static final String EXPAND_FRONTEND_CSS_COMMON_TASK_NAME =
		"expandFrontendCSSCommon";

	public static final String FRONTEND_CSS_COMMON_CONFIGURATION_NAME =
		"frontendCSSCommon";

	public static final String PLUGIN_NAME = "liferayThemeDefaults";

	public static final String WRITE_PARENT_THEMES_DIGEST_TASK_NAME =
		"writeParentThemesDigest";

	public static final String ZIP_RESOURCES_IMPORTER_ARCHIVES_TASK_NAME =
		"zipResourcesImporterArchives";

	@Override
	public void apply(final Project project) {
		GradleUtil.applyPlugin(project, LiferayThemePlugin.class);

		_applyPlugins(project);

		_applyConfigScripts(project);

		final LiferayThemeDefaultsExtension liferayThemeDefaultsExtension =
			GradleUtil.addExtension(
				project, PLUGIN_NAME, LiferayThemeDefaultsExtension.class);

		File portalRootDir = GradleUtil.getRootDir(
			project.getRootProject(), "portal-impl");

		GradlePluginsDefaultsUtil.configureRepositories(project, portalRootDir);

		Configuration frontendCSSCommonConfiguration =
			_addConfigurationFrontendCSSCommon(project);

		final WriteDigestTask writeDigestTask = _addTaskWriteParentThemesDigest(
			project);

		final Copy expandFrontendCSSCommonTask =
			_addTaskExpandFrontendCSSCommon(
				project, frontendCSSCommonConfiguration);
		final ReplaceRegexTask updateVersionTask = _addTaskUpdateVersion(
			project, writeDigestTask);

		File resourcesImporterArchivesDir = project.file(
			"src/WEB-INF/src/resources-importer");
		File resourcesImporterExpandedArchivesDir = project.file(
			"resources-importer");

		Task zipResourcesImporterArchivesTask = _addTaskZipDirectories(
			project, ZIP_RESOURCES_IMPORTER_ARCHIVES_TASK_NAME,
			resourcesImporterExpandedArchivesDir, resourcesImporterArchivesDir,
			"lar");

		_configureDeployDir(project, portalRootDir);
		_configureProject(project);
		_configureTasksPackageRunBuild(
			project, zipResourcesImporterArchivesTask);

		GradleUtil.excludeTasksWithProperty(
			project, LiferayOSGiDefaultsPlugin.SNAPSHOT_IF_STALE_PROPERTY_NAME,
			true, MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME,
			PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureExtensionPublishing(project);

					if (liferayThemeDefaultsExtension.
							isUseLocalDependencies()) {

						_configureTasksPackageRunBuildLocalDependencies(
							project, expandFrontendCSSCommonTask);
					}
					else {
						writeDigestTask.setEnabled(false);
					}

					GradlePluginsDefaultsUtil.setProjectSnapshotVersion(
						project);

					// setProjectSnapshotVersion must be called before
					// configureTaskPublish, because the latter one needs
					// to know if we are publishing a snapshot or not.

					_configureTaskPublish(project, updateVersionTask);
				}

			});
	}

	private Configuration _addConfigurationFrontendCSSCommon(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, FRONTEND_CSS_COMMON_CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					_addDependenciesFrontendCSSCommon(project);
				}

			});

		configuration.setDescription(
			"Configures com.liferay.frontend.css.common for compiling the " +
				"theme.");
		configuration.setTransitive(false);
		configuration.setVisible(false);

		return configuration;
	}

	private void _addDependenciesFrontendCSSCommon(Project project) {
		String version = PortalTools.getVersion(
			project, _FRONTEND_COMMON_CSS_NAME);

		GradleUtil.addDependency(
			project, FRONTEND_CSS_COMMON_CONFIGURATION_NAME, "com.liferay",
			_FRONTEND_COMMON_CSS_NAME, version, false);
	}

	@SuppressWarnings("serial")
	private Copy _addTaskExpandFrontendCSSCommon(
		final Project project,
		final Configuration frontendCSSCommonConfiguration) {

		Copy copy = GradleUtil.addTask(
			project, EXPAND_FRONTEND_CSS_COMMON_TASK_NAME, Copy.class);

		copy.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Copy copy = (Copy)task;

					project.delete(copy.getDestinationDir());
				}

			});

		copy.eachFile(new StripPathSegmentsAction(2));

		copy.from(
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public FileTree doCall() {
					return project.zipTree(
						frontendCSSCommonConfiguration.getSingleFile());
				}

			});

		copy.include("META-INF/resources/");
		copy.into(new File(project.getBuildDir(), "frontend-css-common"));
		copy.setDescription(
			"Expands com.liferay.frontend.css.common to a temporary " +
				"directory.");
		copy.setIncludeEmptyDirs(false);

		return copy;
	}

	private ReplaceRegexTask _addTaskUpdateVersion(
		Project project, WriteDigestTask writeParentThemesDigestTask) {

		ReplaceRegexTask replaceRegexTask = GradleUtil.addTask(
			project, LiferayRelengPlugin.UPDATE_VERSION_TASK_NAME,
			ReplaceRegexTask.class);

		replaceRegexTask.finalizedBy(writeParentThemesDigestTask);

		for (String fileName :
				GradlePluginsDefaultsUtil.JSON_VERSION_FILE_NAMES) {

			File file = project.file(fileName);

			if (!file.exists()) {
				continue;
			}

			replaceRegexTask.match(
				GradlePluginsDefaultsUtil.jsonVersionPattern.pattern(), file);
		}

		replaceRegexTask.setDescription(
			"Updates the project version in the NPM files.");
		replaceRegexTask.setReplacement(
			IncrementVersionClosure.MICRO_INCREMENT);

		return replaceRegexTask;
	}

	private WriteDigestTask _addTaskWriteParentThemesDigest(Project project) {
		WriteDigestTask writeDigestTask = GradleUtil.addTask(
			project, WRITE_PARENT_THEMES_DIGEST_TASK_NAME,
			WriteDigestTask.class);

		writeDigestTask.setDescription(
			"Writes a digest file to keep track of the parent themes used by " +
				"this project.");

		if (!GradlePluginsDefaultsUtil.isSubrepository(project)) {
			for (String themeProjectName :
					GradlePluginsDefaultsUtil.PARENT_THEME_PROJECT_NAMES) {

				Project themeProject = _getThemeProject(
					project, themeProjectName);

				if (themeProject == null) {
					continue;
				}

				String path = themeProject.getPath();

				writeDigestTask.dependsOn(
					path + ":" + JavaPlugin.CLASSES_TASK_NAME);

				writeDigestTask.source(
					themeProject.file("src/main/resources/META-INF/resources"));
			}
		}
		else if (GradlePluginsDefaultsUtil.hasNPMParentThemesDependencies(
					project)) {

			writeDigestTask.dependsOn(NodePlugin.NPM_INSTALL_TASK_NAME);

			writeDigestTask.setExcludeIgnoredFiles(false);

			NpmInstallTask npmInstallTask = (NpmInstallTask)GradleUtil.getTask(
				project, NodePlugin.NPM_INSTALL_TASK_NAME);

			File nodeModulesDir = npmInstallTask.getNodeModulesDir();

			for (String themeProjectName :
					GradlePluginsDefaultsUtil.PARENT_THEME_PROJECT_NAMES) {

				writeDigestTask.source(
					new File(nodeModulesDir, "liferay-" + themeProjectName));
			}
		}

		return writeDigestTask;
	}

	private Task _addTaskZipDirectories(
		Project project, String taskName, File rootDir, File destinationDir,
		String extension) {

		Task task = project.task(taskName);

		StringBuilder sb = new StringBuilder();

		sb.append("Assembles ");
		sb.append(extension.toUpperCase());
		sb.append(" files in ");
		sb.append(project.relativePath(destinationDir));
		sb.append(" from the subdirectories of ");
		sb.append(project.relativePath(rootDir));
		sb.append('.');

		task.setDescription(sb.toString());

		File[] dirs = FileUtil.getDirectories(rootDir);

		if (dirs != null) {
			for (File dir : dirs) {
				Zip zip = _addTaskZipDirectory(
					project, GradleUtil.getTaskName(taskName, dir), dir,
					destinationDir, extension);

				task.dependsOn(zip);
			}
		}

		return task;
	}

	private Zip _addTaskZipDirectory(
		Project project, String taskName, File dir, File destinationDir,
		String extension) {

		Zip zip = GradleUtil.addTask(project, taskName, Zip.class);

		zip.from(dir);

		zip.setDescription(
			"Assembles " + project.relativePath(zip.getArchivePath()) +
				" with the contents of the " + project.relativePath(dir) +
					" directory.");

		Property<String> property = zip.getArchiveFileName();

		property.set(dir.getName() + "." + extension);

		DirectoryProperty directoryProperty = zip.getDestinationDirectory();

		directoryProperty.set(destinationDir);

		return zip;
	}

	private void _applyConfigScripts(Project project) {
		GradleUtil.applyScript(
			project,
			"com/liferay/gradle/plugins/defaults/dependencies" +
				"/config-maven-publish.gradle",
			project);
	}

	private void _applyPlugins(Project project) {
		GradleUtil.applyPlugin(project, MavenPublishPlugin.class);
	}

	private void _configureDeployDir(
		final Project project, final File portalRootDir) {

		final LiferayExtension liferayExtension = GradleUtil.getExtension(
			project, LiferayExtension.class);

		liferayExtension.setDeployDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					if ((portalRootDir != null) &&
						project.hasProperty("portal.war.auto.deploy.enabled")) {

						return new File(
							liferayExtension.getLiferayHome(),
							"osgi/portal-war");
					}

					boolean requiredForStartup = _getPluginPackageProperty(
						project, "required-for-startup");

					if (requiredForStartup) {
						return new File(
							liferayExtension.getLiferayHome(), "osgi/war");
					}

					return new File(
						liferayExtension.getLiferayHome(), "deploy");
				}

			});
	}

	private void _configureExtensionPublishing(final Project project) {
		PublishingExtension publishingExtension = GradleUtil.getExtension(
			project, PublishingExtension.class);

		publishingExtension.publications(
			new Action<PublicationContainer>() {

				@Override
				public void execute(PublicationContainer publicationContainer) {
					MavenPublication mavenPublication =
						publicationContainer.maybeCreate(
							"maven", MavenPublication.class);

					mavenPublication.setArtifactId(
						GradleUtil.getArchivesBaseName(project));
					mavenPublication.setGroupId(
						String.valueOf(project.getGroup()));

					mavenPublication.artifact(_getWarFile(project));
				}

			});
	}

	private void _configureProject(Project project) {
		project.setGroup(GradleUtil.getProjectGroup(project, _GROUP));
	}

	private void _configureTaskPackageRunBuild(
		PackageRunBuildTask packageRunBuildTask,
		Task zipResourcesImporterArchivesTask) {

		packageRunBuildTask.dependsOn(zipResourcesImporterArchivesTask);
	}

	private void _configureTaskPackageRunBuildLocalDependencies(
		PackageRunBuildTask packageRunBuildTask,
		Copy expandFrontendCSSCommonTask) {

		packageRunBuildTask.dependsOn(expandFrontendCSSCommonTask);

		Project project = packageRunBuildTask.getProject();

		if (!GradlePluginsDefaultsUtil.isSubrepository(project)) {
			for (String themeProjectName :
					GradlePluginsDefaultsUtil.PARENT_THEME_PROJECT_NAMES) {

				int index = themeProjectName.lastIndexOf("-");

				_configureTaskPackageRunBuildLocalDependenciesTheme(
					packageRunBuildTask,
					_getThemeProject(project, themeProjectName),
					themeProjectName.substring(index + 1));
			}
		}
	}

	private void _configureTaskPackageRunBuildLocalDependenciesTheme(
		PackageRunBuildTask packageRunBuildTask, Project themeProject,
		String name) {

		if (themeProject == null) {
			Project project = packageRunBuildTask.getProject();

			Logger logger = project.getLogger();

			if (logger.isWarnEnabled()) {
				logger.warn("Unable to configure {} parent theme", name);
			}

			return;
		}

		packageRunBuildTask.dependsOn(
			themeProject.getPath() + ":" + JavaPlugin.CLASSES_TASK_NAME);
	}

	private void _configureTaskPublish(
		final Project project, Task updateVersionTask) {

		Task publishTask = GradleUtil.getTask(
			project, PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME);

		publishTask.dependsOn(BasePlugin.ASSEMBLE_TASK_NAME);

		if (FileUtil.exists(project, ".lfrbuild-missing-resources-importer")) {
			Action<Task> action = new Action<Task>() {

				@Override
				public void execute(Task task) {
					throw new GradleException(
						"Unable to publish " + project +
							", resources-importer directory is missing");
				}

			};

			publishTask.doFirst(action);
		}

		if (!GradlePluginsDefaultsUtil.isSnapshot(project)) {
			publishTask.finalizedBy(updateVersionTask);
		}
	}

	private void _configureTasksPackageRunBuild(
		Project project, final Task zipResourcesImporterArchivesTask) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PackageRunBuildTask.class,
			new Action<PackageRunBuildTask>() {

				@Override
				public void execute(PackageRunBuildTask packageRunBuildTask) {
					_configureTaskPackageRunBuild(
						packageRunBuildTask, zipResourcesImporterArchivesTask);
				}

			});
	}

	private void _configureTasksPackageRunBuildLocalDependencies(
		Project project, final Copy expandFrontendCSSCommonTask) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PackageRunBuildTask.class,
			new Action<PackageRunBuildTask>() {

				@Override
				public void execute(PackageRunBuildTask packageRunBuildTask) {
					_configureTaskPackageRunBuildLocalDependencies(
						packageRunBuildTask, expandFrontendCSSCommonTask);
				}

			});
	}

	private boolean _getPluginPackageProperty(Project project, String key) {
		File file = project.file(
			"src/WEB-INF/liferay-plugin-package.properties");

		if (!file.exists()) {
			return false;
		}

		Properties properties = GUtil.loadProperties(file);

		return Boolean.parseBoolean(properties.getProperty(key));
	}

	private Project _getThemeProject(Project project, String name) {
		Project parentProject = project.getParent();

		Project themeProject = parentProject.findProject(name);

		if (themeProject == null) {
			themeProject = GradleUtil.getProject(
				project.getRootProject(), name);
		}

		return themeProject;
	}

	private File _getWarFile(Project project) {
		return project.file(
			"dist/" + GradleUtil.getArchivesBaseName(project) + ".war");
	}

	private static final String _FRONTEND_COMMON_CSS_NAME =
		"com.liferay.frontend.css.common";

	private static final String _GROUP = "com.liferay.plugins";

}