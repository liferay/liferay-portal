/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal;

import com.liferay.gradle.plugins.cache.CachePlugin;
import com.liferay.gradle.plugins.defaults.internal.util.CIUtil;
import com.liferay.gradle.plugins.defaults.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.task.DownloadNodeTask;
import com.liferay.gradle.plugins.node.task.ExecuteNodeTask;
import com.liferay.gradle.plugins.node.task.ExecutePackageManagerTask;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.YarnInstallTask;
import com.liferay.gradle.plugins.test.integration.TestIntegrationBasePlugin;
import com.liferay.gradle.plugins.test.integration.TestIntegrationPlugin;
import com.liferay.gradle.util.Validator;

import java.io.File;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.file.FileTree;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.testing.Test;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayCIPlugin implements Plugin<Project> {

	public static final Plugin<Project> INSTANCE = new LiferayCIPlugin();

	@Override
	public void apply(final Project project) {

		// Containers

		final TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			DownloadNodeTask.class,
			new Action<DownloadNodeTask>() {

				@Override
				public void execute(DownloadNodeTask downloadNodeTask) {
					_configureTaskDownloadNode(downloadNodeTask);
				}

			});

		taskContainer.withType(
			ExecuteNodeTask.class,
			new Action<ExecuteNodeTask>() {

				@Override
				public void execute(ExecuteNodeTask executeNodeTask) {
					_configureTaskExecuteNode(executeNodeTask);
				}

			});

		taskContainer.withType(
			ExecutePackageManagerTask.class,
			new Action<ExecutePackageManagerTask>() {

				@Override
				public void execute(
					ExecutePackageManagerTask executePackageManagerTask) {

					_configureTaskExecutePackageManager(
						executePackageManagerTask);
				}

			});

		taskContainer.withType(
			NpmInstallTask.class,
			new Action<NpmInstallTask>() {

				@Override
				public void execute(NpmInstallTask npmInstallTask) {
					_configureTaskNpmInstall(npmInstallTask);
				}

			});

		taskContainer.withType(
			YarnInstallTask.class,
			new Action<YarnInstallTask>() {

				@Override
				public void execute(YarnInstallTask yarnInstallTask) {
					_configureTaskYarnInstall(yarnInstallTask);
				}

			});

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			TestIntegrationPlugin.class,
			new Action<TestIntegrationPlugin>() {

				@Override
				public void execute(
					TestIntegrationPlugin testIntegrationPlugin) {

					taskContainer.withType(
						Test.class,
						new Action<Test>() {

							@Override
							public void execute(Test test) {
								_configureTaskTestForTestIntegrationPlugin(
									project, test);
							}

						});
				}

			});

		// Other

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					taskContainer.withType(
						NpmInstallTask.class,
						new Action<NpmInstallTask>() {

							@Override
							public void execute(NpmInstallTask npmInstallTask) {
								_configureTaskNpmInstallAfterEvaluate(
									project, npmInstallTask);
							}

						});
				}

			});
	}

	private LiferayCIPlugin() {
	}

	private void _configureTaskDownloadNode(DownloadNodeTask downloadNodeTask) {
		downloadNodeTask.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					if (GradleUtil.hasPlugin(
							task.getProject(), CachePlugin.class)) {

						throw new GradleException(
							"Unable to use Node.js on CI, please configure " +
								"com.liferay.cache or update the cache");
					}
				}

			});
	}

	private void _configureTaskExecuteNode(ExecuteNodeTask executeNodeTask) {
		executeNodeTask.setNpmInstallRetries(_NPM_INSTALL_RETRIES);
	}

	private void _configureTaskExecutePackageManager(
		ExecutePackageManagerTask executePackageManagerTask) {

		String ciNodeEnv = GradleUtil.getProperty(
			executePackageManagerTask.getProject(), "nodejs.ci.node.env",
			(String)null);

		if (Validator.isNotNull(ciNodeEnv)) {
			executePackageManagerTask.environment("NODE_ENV", ciNodeEnv);
		}

		String ciRegistry = GradleUtil.getProperty(
			executePackageManagerTask.getProject(), "nodejs.npm.ci.registry",
			(String)null);

		if (Validator.isNotNull(ciRegistry)) {
			executePackageManagerTask.setRegistry(ciRegistry);
		}
	}

	private void _configureTaskNpmInstall(NpmInstallTask npmInstallTask) {
		npmInstallTask.setNodeModulesCacheDir(_NODE_MODULES_CACHE_DIR);
		npmInstallTask.setRemoveShrinkwrappedUrls(Boolean.TRUE);
		npmInstallTask.setUseNpmCI(Boolean.FALSE);
	}

	private void _configureTaskNpmInstallAfterEvaluate(
		Project project, NpmInstallTask npmInstallTask) {

		String ciSassBinarySite = GradleUtil.getProperty(
			project, "nodejs.npm.ci.sass.binary.site", (String)null);

		if (Validator.isNull(ciSassBinarySite)) {
			return;
		}

		Map<String, String> newArgs = Collections.singletonMap(
			_SASS_BINARY_SITE_ARG, ciSassBinarySite);

		List<Object> args = npmInstallTask.getArgs();

		for (Map.Entry<String, String> entry : newArgs.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			boolean changed = false;

			for (int i = 0; i < args.size(); i++) {
				String arg = GradleUtil.toString(args.get(i));

				if (arg.startsWith(key)) {
					changed = true;

					args.set(i, key + value);

					break;
				}
			}

			if (changed) {
				continue;
			}

			args.add(key + value);
		}

		npmInstallTask.setArgs(args);
	}

	private void _configureTaskTestForTestIntegrationPlugin(
		final Project project, Test test) {

		String taskName = TestIntegrationBasePlugin.TEST_INTEGRATION_TASK_NAME;

		if (!Objects.equals(test.getName(), taskName)) {
			return;
		}

		test.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {

					// Conventions

					Convention convention = project.getConvention();

					JavaPluginConvention javaPluginConvention =
						convention.getPlugin(JavaPluginConvention.class);

					SourceSetContainer sourceSetContainer =
						javaPluginConvention.getSourceSets();

					SourceSet testIntegrationSourceSet =
						sourceSetContainer.getByName(
							TestIntegrationBasePlugin.
								TEST_INTEGRATION_SOURCE_SET_NAME);

					// Configurations

					ConfigurationContainer configurationContainer =
						project.getConfigurations();

					Configuration configuration =
						configurationContainer.getByName(
							testIntegrationSourceSet.
								getImplementationConfigurationName());

					// Dependencies

					DependencySet dependencySet =
						configuration.getDependencies();

					for (ProjectDependency projectDependency :
							dependencySet.withType(ProjectDependency.class)) {

						Project dependencyProject =
							projectDependency.getDependencyProject();

						if (CIUtil.isExcludedDependencyProject(
								project, dependencyProject)) {

							Logger logger = project.getLogger();

							if (logger.isLifecycleEnabled()) {
								logger.lifecycle(
									"Excluded project dependency {} for {}",
									dependencyProject.getPath(),
									project.getPath());
							}

							continue;
						}

						File lfrBuildCIFile = dependencyProject.file(
							".lfrbuild-ci");
						File lfrBuildCISkipTestIntegrationCheckFile =
							dependencyProject.file(
								".lfrbuild-ci-skip-test-integration-check");
						File lfrBuildPortalDeprecatedFile =
							dependencyProject.file(
								".lfrbuild-portal-deprecated");
						File lfrBuildPortalFile = dependencyProject.file(
							".lfrbuild-portal");

						if (lfrBuildCISkipTestIntegrationCheckFile.exists()) {
							if (lfrBuildCIFile.exists() ||
								lfrBuildPortalFile.exists()) {

								throw new GradleException(
									"Please delete marker file " +
										lfrBuildCISkipTestIntegrationCheckFile);
							}
						}
						else if (!lfrBuildCIFile.exists() &&
								 !lfrBuildPortalDeprecatedFile.exists() &&
								 !lfrBuildPortalFile.exists()) {

							throw new GradleException(
								"Please create marker file " +
									lfrBuildPortalFile);
						}
					}
				}

			});
	}

	private void _configureTaskYarnInstall(YarnInstallTask yarnInstallTask) {
		Project project = yarnInstallTask.getProject();

		final String ciRegistry = GradleUtil.getProperty(
			project, "nodejs.npm.ci.registry", (String)null);

		if (Validator.isNull(ciRegistry)) {
			return;
		}

		yarnInstallTask.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Project project = task.getProject();

					Logger logger = project.getLogger();

					if (logger.isLifecycleEnabled()) {
						logger.lifecycle("Using registry {}", ciRegistry);
					}

					Map<String, Object> args = new HashMap<>();

					args.put("dir", project.getProjectDir());
					args.put("excludes", _excludes);
					args.put("includes", _includes);

					FileTree fileTree = project.fileTree(args);

					fileTree.forEach(
						yarnLockFile -> _updateYarnLockFile(
							ciRegistry, yarnLockFile));
				}

				private void _updateYarnLockFile(
					String ciRegistry, File yarnLockFile) {

					try {
						String text = new String(
							Files.readAllBytes(yarnLockFile.toPath()),
							StandardCharsets.UTF_8);

						text = text.replaceAll(
							"https://registry.yarnpkg.com", ciRegistry);

						Files.write(
							yarnLockFile.toPath(),
							text.getBytes(StandardCharsets.UTF_8));
					}
					catch (IOException ioException) {
						throw new UncheckedIOException(ioException);
					}
				}

			});
	}

	private static final File _NODE_MODULES_CACHE_DIR = new File(
		System.getProperty("user.home"), ".liferay/node-modules-cache");

	private static final int _NPM_INSTALL_RETRIES = 3;

	private static final String _SASS_BINARY_SITE_ARG = "--sass-binary-site=";

	private static final List<String> _excludes = Arrays.asList(
		"**/bin/", "**/build/", "**/classes/", "**/node_modules/",
		"**/node_modules_cache/", "**/test-classes/", "**/tmp/");
	private static final List<String> _includes = Arrays.asList(
		"yarn.lock", "private/yarn.lock", "apps/*/yarn.lock",
		"private/apps/*/yarn.lock");

}