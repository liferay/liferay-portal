/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.node;

import com.liferay.gradle.plugins.node.internal.util.FileUtil;
import com.liferay.gradle.plugins.node.internal.util.GradleUtil;
import com.liferay.gradle.plugins.node.internal.util.StringUtil;
import com.liferay.gradle.plugins.node.task.DownloadNodeModuleTask;
import com.liferay.gradle.plugins.node.task.DownloadNodeTask;
import com.liferay.gradle.plugins.node.task.ExecuteNodeTask;
import com.liferay.gradle.plugins.node.task.ExecutePackageManagerTask;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.node.task.NpmShrinkwrapTask;
import com.liferay.gradle.plugins.node.task.PackageRunBuildTask;
import com.liferay.gradle.plugins.node.task.PackageRunTask;
import com.liferay.gradle.plugins.node.task.PackageRunTestTask;
import com.liferay.gradle.plugins.node.task.PublishNodeModuleTask;
import com.liferay.gradle.plugins.node.task.YarnInstallTask;
import com.liferay.gradle.util.OSDetector;
import com.liferay.gradle.util.OSGiUtil;
import com.liferay.gradle.util.Validator;

import groovy.json.JsonSlurper;

import groovy.lang.Closure;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.JavaLibraryPlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.jvm.tasks.Jar;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.util.VersionNumber;

/**
 * @author Andrea Di Giorgi
 */
public class NodePlugin implements Plugin<Project> {

	public static final String CLEAN_NPM_TASK_NAME = "cleanNPM";

	public static final String DOWNLOAD_NODE_TASK_NAME = "downloadNode";

	public static final String EXTENSION_NAME = "node";

	public static final String NPM_INSTALL_TASK_NAME = "npmInstall";

	public static final String NPM_PACKAGE_LOCK_TASK_NAME = "npmPackageLock";

	public static final String NPM_SHRINKWRAP_TASK_NAME = "npmShrinkwrap";

	public static final String PACKAGE_RUN_BUILD_TASK_NAME = "packageRunBuild";

	public static final String PACKAGE_RUN_TEST_TASK_NAME = "packageRunTest";

	@Override
	@SuppressWarnings("unchecked")
	public void apply(Project project) {
		final NodeExtension nodeExtension = GradleUtil.addExtension(
			project, EXTENSION_NAME, NodeExtension.class);

		File packageJsonFile = project.file("package.json");

		if (!packageJsonFile.exists()) {
			return;
		}

		_configureExtensionNode(project, nodeExtension);

		Delete cleanNpmTask = _addTaskCleanNpm(project, nodeExtension);

		final DownloadNodeTask downloadNodeTask = _addTaskDownloadNode(
			project, nodeExtension);

		NpmInstallTask npmInstallTask = _addTaskNpmInstall(
			project, cleanNpmTask);

		JsonSlurper jsonSlurper = new JsonSlurper();

		Map<String, Object> packageJsonMap =
			(Map<String, Object>)jsonSlurper.parse(packageJsonFile);

		_addTaskNpmPackageLock(project, cleanNpmTask, npmInstallTask);
		_addTaskNpmShrinkwrap(project, cleanNpmTask, npmInstallTask);
		_addTasksPackageRun(npmInstallTask, packageJsonMap, nodeExtension);

		_configureTasksDownloadNodeModule(
			project, npmInstallTask, packageJsonMap);

		_configureTasksExecuteNode(
			project, nodeExtension, GradleUtil.isRunningInsideDaemon());
		_configureTasksExecutePackageManager(project, nodeExtension);

		_configureTasksPublishNodeModule(project);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureTaskDownloadNodeGlobal(
						downloadNodeTask, nodeExtension);
					_configureTasksExecutePackageManagerArgs(
						project, nodeExtension);
					_configureTasksNpmInstall(project, nodeExtension);
					_configureTasksYarnInstall(project);
				}

			});
	}

	private Delete _addTaskCleanNpm(
		final Project project, final NodeExtension nodeExtension) {

		Delete delete = GradleUtil.addTask(
			project, CLEAN_NPM_TASK_NAME, Delete.class);

		delete.delete("node_modules", "npm-shrinkwrap.json");
		delete.setDescription("Deletes NPM files from this project.");

		delete.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					File file = project.file("package-lock.json");

					if (!file.exists()) {
						return;
					}

					try {
						Files.delete(file.toPath());
					}
					catch (IOException ioException) {
						throw new UncheckedIOException(ioException);
					}

					// LPS-110486

					nodeExtension.setUseNpm(true);
				}

			});

		return delete;
	}

	private DownloadNodeTask _addTaskDownloadNode(
		Project project, NodeExtension nodeExtension) {

		return _addTaskDownloadNode(
			project, DOWNLOAD_NODE_TASK_NAME, nodeExtension);
	}

	private DownloadNodeTask _addTaskDownloadNode(
		Project project, String taskName, final NodeExtension nodeExtension) {

		DownloadNodeTask downloadNodeTask = GradleUtil.addTask(
			project, taskName, DownloadNodeTask.class);

		downloadNodeTask.setNodeDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return nodeExtension.getNodeDir();
				}

			});

		downloadNodeTask.setNodeUrl(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return nodeExtension.getNodeUrl();
				}

			});

		downloadNodeTask.setNpmUrl(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return nodeExtension.getNpmUrl();
				}

			});

		downloadNodeTask.setYarnUrl(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return nodeExtension.getYarnUrl();
				}

			});

		downloadNodeTask.onlyIf(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return nodeExtension.isDownload();
				}

			});

		downloadNodeTask.setDescription(
			"Downloads Node.js in the project build directory.");

		if (!OSDetector.isWindows()) {

			// https://github.com/gradle/gradle/issues/3525

			TaskOutputs taskOutputs = downloadNodeTask.getOutputs();

			taskOutputs.upToDateWhen(
				new Spec<Task>() {

					@Override
					public boolean isSatisfiedBy(Task task) {
						return false;
					}

				});
		}

		return downloadNodeTask;
	}

	private NpmInstallTask _addTaskNpmInstall(
		Project project, Delete cleanNpmTask) {

		NpmInstallTask npmInstallTask = GradleUtil.addTask(
			project, NPM_INSTALL_TASK_NAME, NpmInstallTask.class);

		npmInstallTask.mustRunAfter(cleanNpmTask);
		npmInstallTask.setDescription(
			"Installs Node packages from package.json.");
		npmInstallTask.setNpmInstallRetries(2);

		return npmInstallTask;
	}

	private Task _addTaskNpmPackageLock(
		Project project, Delete cleanNpmTask, NpmInstallTask npmInstallTask) {

		Task task = project.task(NPM_PACKAGE_LOCK_TASK_NAME);

		task.dependsOn(cleanNpmTask, npmInstallTask);
		task.setDescription(
			"Deletes NPM files and installs Node packages from package.json.");

		return task;
	}

	private NpmShrinkwrapTask _addTaskNpmShrinkwrap(
		Project project, Delete cleanNpmTask, NpmInstallTask npmInstallTask) {

		NpmShrinkwrapTask npmShrinkwrapTask = GradleUtil.addTask(
			project, NPM_SHRINKWRAP_TASK_NAME, NpmShrinkwrapTask.class);

		npmShrinkwrapTask.dependsOn(cleanNpmTask, npmInstallTask);
		npmShrinkwrapTask.setDescription(
			"Locks down the versions of a package's dependencies in order to " +
				"control which versions of each dependency will be used.");

		return npmShrinkwrapTask;
	}

	private PackageRunTask _addTaskPackageRun(
		String scriptName, NpmInstallTask npmInstallTask) {

		Project project = npmInstallTask.getProject();

		String suffix = StringUtil.camelCase(scriptName, true);

		String taskName = _PACKAGE_RUN_TASK_NAME_PREFIX + suffix;

		PackageRunTask packageRunTask = GradleUtil.addTask(
			project, taskName, PackageRunTask.class);

		packageRunTask.dependsOn(npmInstallTask);
		packageRunTask.setDescription(
			"Runs the \"" + scriptName + "\" package.json script.");
		packageRunTask.setGroup(BasePlugin.BUILD_GROUP);
		packageRunTask.setScriptName(scriptName);

		return packageRunTask;
	}

	private PackageRunBuildTask _addTaskPackageRunBuild(
		NpmInstallTask npmInstallTask, NodeExtension nodeExtension) {

		Project project = npmInstallTask.getProject();

		final PackageRunBuildTask packageRunBuildTask = GradleUtil.addTask(
			project, PACKAGE_RUN_BUILD_TASK_NAME, PackageRunBuildTask.class);

		packageRunBuildTask.dependsOn(npmInstallTask);
		packageRunBuildTask.setDescription(
			"Runs the \"build\" package.json script.");
		packageRunBuildTask.setGroup(BasePlugin.BUILD_GROUP);
		packageRunBuildTask.setYarnWorkingDir(
			_getYarnWorkingDir(project, nodeExtension));

		packageRunBuildTask.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					PackageRunBuildTask packageRunBuildTask =
						(PackageRunBuildTask)task;

					String result = packageRunBuildTask.getResult();

					if (result.contains("errors during Soy compilation")) {
						project.delete(packageRunBuildTask.getDestinationDir());

						throw new GradleException("Soy compile error");
					}
				}

			});

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			JavaLibraryPlugin.class,
			new Action<JavaLibraryPlugin>() {

				@Override
				public void execute(JavaLibraryPlugin javaLibraryPlugin) {
					_configureTaskPackageRunBuildForJavaLibraryPlugin(
						packageRunBuildTask);
				}

			});

		return packageRunBuildTask;
	}

	private PackageRunTestTask _addTaskPackageRunTest(
		NpmInstallTask npmInstallTask) {

		Project project = npmInstallTask.getProject();

		final PackageRunTestTask packageRunTestTask = GradleUtil.addTask(
			project, PACKAGE_RUN_TEST_TASK_NAME, PackageRunTestTask.class);

		packageRunTestTask.dependsOn(npmInstallTask);
		packageRunTestTask.setDescription(
			"Runs the \"build\" package.json script.");
		packageRunTestTask.setGroup(BasePlugin.BUILD_GROUP);

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(
			LifecycleBasePlugin.class,
			new Action<LifecycleBasePlugin>() {

				@Override
				public void execute(LifecycleBasePlugin lifecycleBasePlugin) {
					_configureTaskPackageRunTestForLifecycleBasePlugin(
						packageRunTestTask);
				}

			});

		return packageRunTestTask;
	}

	@SuppressWarnings("unchecked")
	private void _addTasksPackageRun(
		NpmInstallTask npmInstallTask, Map<String, Object> packageJsonMap,
		NodeExtension nodeExtension) {

		Map<String, String> scriptsJsonMap =
			(Map<String, String>)packageJsonMap.get("scripts");

		if (scriptsJsonMap != null) {
			for (String scriptName : scriptsJsonMap.keySet()) {
				if (Objects.equals(scriptName, "build")) {
					_addTaskPackageRunBuild(npmInstallTask, nodeExtension);
				}
				else if (Objects.equals(scriptName, "test")) {
					_addTaskPackageRunTest(npmInstallTask);
				}
				else {
					_addTaskPackageRun(scriptName, npmInstallTask);
				}
			}
		}
	}

	private void _configureExtensionNode(
		Project project, NodeExtension nodeExtension) {

		if (FileUtil.exists(project, "package-lock.json")) {
			return;
		}

		Project rootProject = project.getRootProject();

		PluginContainer rootPluginContainer = rootProject.getPlugins();

		rootPluginContainer.withId(
			"com.liferay.yarn",
			new Action<Plugin>() {

				@Override
				public void execute(Plugin plugin) {
					nodeExtension.setUseNpm(false);
				}

			});
	}

	private void _configureTaskDownloadNodeGlobal(
		DownloadNodeTask downloadNodeTask, NodeExtension nodeExtension) {

		Project project = downloadNodeTask.getProject();

		if (!nodeExtension.isDownload() || !nodeExtension.isGlobal() ||
			(project.getParent() == null)) {

			return;
		}

		Project rootProject = project.getRootProject();

		DownloadNodeTask rootDownloadNodeTask = null;

		TaskContainer taskContainer = rootProject.getTasks();

		Set<DownloadNodeTask> rootDownloadNodeTasks = taskContainer.withType(
			DownloadNodeTask.class);

		File nodeDir = downloadNodeTask.getNodeDir();
		String nodeUrl = downloadNodeTask.getNodeUrl();

		for (DownloadNodeTask curRootDownloadNodeTask : rootDownloadNodeTasks) {
			if (nodeDir.equals(curRootDownloadNodeTask.getNodeDir()) &&
				nodeUrl.equals(curRootDownloadNodeTask.getNodeUrl())) {

				rootDownloadNodeTask = curRootDownloadNodeTask;

				break;
			}
		}

		if (rootDownloadNodeTask == null) {
			String taskName = DOWNLOAD_NODE_TASK_NAME;

			if (!rootDownloadNodeTasks.isEmpty()) {
				taskName += rootDownloadNodeTasks.size();
			}

			rootDownloadNodeTask = _addTaskDownloadNode(
				rootProject, taskName, nodeExtension);
		}

		downloadNodeTask.setActions(Collections.emptyList());

		downloadNodeTask.dependsOn(rootDownloadNodeTask);
	}

	private void _configureTaskDownloadNodeModule(
		DownloadNodeModuleTask downloadNodeModuleTask,
		final NpmInstallTask npmInstallTask,
		final Map<String, Object> packageJsonMap) {

		downloadNodeModuleTask.onlyIf(
			new Spec<Task>() {

				@Override
				@SuppressWarnings("unchecked")
				public boolean isSatisfiedBy(Task task) {
					DownloadNodeModuleTask downloadNodeModuleTask =
						(DownloadNodeModuleTask)task;

					File moduleDir = downloadNodeModuleTask.getModuleDir();

					File moduleParentDir = moduleDir.getParentFile();

					if (!moduleParentDir.equals(
							npmInstallTask.getNodeModulesDir())) {

						return true;
					}

					String moduleName = downloadNodeModuleTask.getModuleName();

					Map<String, Object> dependenciesJsonMap =
						(Map<String, Object>)packageJsonMap.get("dependencies");

					if ((dependenciesJsonMap != null) &&
						dependenciesJsonMap.containsKey(moduleName)) {

						return false;
					}

					dependenciesJsonMap =
						(Map<String, Object>)packageJsonMap.get(
							"devDependencies");

					if ((dependenciesJsonMap != null) &&
						dependenciesJsonMap.containsKey(moduleName)) {

						return false;
					}

					return true;
				}

			});
	}

	private void _configureTaskExecuteNode(
		ExecuteNodeTask executeNodeTask, final NodeExtension nodeExtension,
		boolean useGradleExec) {

		executeNodeTask.setNodeDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					if (nodeExtension.isDownload()) {
						return nodeExtension.getNodeDir();
					}

					return null;
				}

			});

		executeNodeTask.setUseGradleExec(useGradleExec);
	}

	private void _configureTaskExecutePackageManager(
		final ExecutePackageManagerTask executePackageManagerTask,
		final NodeExtension nodeExtension) {

		final Callable<Boolean> useGlobalConcurrentCacheCallable =
			new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					int value1 = _node8VersionNumber.compareTo(
						VersionNumber.parse(nodeExtension.getNodeVersion()));
					int value2 = _npm5VersionNumber.compareTo(
						VersionNumber.parse(nodeExtension.getNpmVersion()));

					if ((value1 <= 0) || (value2 <= 0)) {
						return true;
					}

					return false;
				}

			};

		executePackageManagerTask.setCacheConcurrent(
			useGlobalConcurrentCacheCallable);

		executePackageManagerTask.setCacheDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					if (useGlobalConcurrentCacheCallable.call()) {
						return null;
					}

					File nodeDir = executePackageManagerTask.getNodeDir();

					if (nodeDir == null) {
						return null;
					}

					return new File(nodeDir, ".cache");
				}

			});

		final Project project = executePackageManagerTask.getProject();

		executePackageManagerTask.setNodeModulesDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					if (nodeExtension.isUseNpm()) {
						return project.file("node_modules");
					}

					Project rootProject = project.getRootProject();

					return rootProject.file("node_modules");
				}

			});

		executePackageManagerTask.setScriptFile(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return nodeExtension.getScriptFile();
				}

			});

		executePackageManagerTask.setUseNpm(
			new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return nodeExtension.isUseNpm();
				}

			});
	}

	private void _configureTaskExecutePackageManagerArgs(
		ExecutePackageManagerTask executePackageManagerTask,
		NodeExtension nodeExtension) {

		if (nodeExtension.isUseNpm()) {
			executePackageManagerTask.args(nodeExtension.getNpmArgs());
		}
	}

	private void _configureTaskNpmInstall(
		NpmInstallTask npmInstallTask, NodeExtension nodeExtension) {

		npmInstallTask.setNodeVersion(nodeExtension.getNodeVersion());
		npmInstallTask.setNpmVersion(nodeExtension.getNpmVersion());

		if (!npmInstallTask.isUseNpm()) {
			Project curProject = npmInstallTask.getProject();

			do {
				TaskProvider<YarnInstallTask> yarnInstallTaskProvider =
					GradleUtil.fetchTaskProvider(
						curProject, YarnPlugin.YARN_INSTALL_TASK_NAME,
						YarnInstallTask.class);

				if (yarnInstallTaskProvider != null) {
					npmInstallTask.finalizedBy(yarnInstallTaskProvider);
				}
			}
			while ((curProject = curProject.getParent()) != null);
		}
	}

	@SuppressWarnings("serial")
	private void _configureTaskPackageRunBuildForJavaLibraryPlugin(
		final PackageRunBuildTask packageRunBuildTask) {

		final Project project = packageRunBuildTask.getProject();

		packageRunBuildTask.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Jar jar = (Jar)GradleUtil.getTask(
						project, JavaPlugin.JAR_TASK_NAME);

					project.delete(jar.getArchivePath());
				}

			});

		Copy processResourcesCopy = (Copy)GradleUtil.getTask(
			project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

		File yarnWorkingDir = packageRunBuildTask.getYarnWorkingDir();

		boolean nodeBuildRequired = _isNodeBuildRequired(project);

		if ((yarnWorkingDir != null) &&
			_hasLiferayNpmScripts12Dependency(project, yarnWorkingDir)) {

			final File destinationDir = new File(
				project.getBuildDir(), "node/packageRunBuild/resources");

			packageRunBuildTask.setDestinationDir(
				new Callable<File>() {

					@Override
					public File call() throws Exception {
						return destinationDir;
					}

				});

			final File sourceDir = project.file(
				"src/main/resources/META-INF/resources");

			packageRunBuildTask.setSourceDir(
				new Callable<File>() {

					@Override
					public File call() throws Exception {
						if (!sourceDir.exists()) {
							return null;
						}

						return sourceDir;
					}

				});

			packageRunBuildTask.doFirst(
				new Action<Task>() {

					@Override
					public void execute(Task task) {
						Action<CopySpec> action = new Action<CopySpec>() {

							@Override
							public void execute(CopySpec copySpec) {
								copySpec.from(sourceDir);
								copySpec.into(destinationDir);
							}

						};

						project.copy(action);
					}

				});

			String incrementalCacheEnabled = GradleUtil.getTaskPrefixedProperty(
				packageRunBuildTask, "incremental.cache.enabled");

			if (Validator.isNull(incrementalCacheEnabled) ||
				Boolean.parseBoolean(incrementalCacheEnabled)) {

				TaskOutputs taskOutputs = packageRunBuildTask.getOutputs();

				taskOutputs.dir(packageRunBuildTask.getDestinationDir());
			}

			if (nodeBuildRequired) {
				processResourcesCopy.dependsOn(packageRunBuildTask);

				processResourcesCopy.setDuplicatesStrategy(
					DuplicatesStrategy.INCLUDE);

				processResourcesCopy.from(
					new Callable<File>() {

						@Override
						public File call() throws Exception {
							return packageRunBuildTask.getDestinationDir();
						}

					},
					new Closure<Void>(project) {

						@SuppressWarnings("unused")
						public void doCall(CopySpec copySpec) {
							copySpec.exclude("**/*.d.js");
							copySpec.exclude("**/*.d.js.map");
							copySpec.exclude("**/*.ts");
							copySpec.into("META-INF/resources");
						}

					});
			}
		}
		else if (nodeBuildRequired) {
			packageRunBuildTask.mustRunAfter(processResourcesCopy);

			Task classesTask = GradleUtil.getTask(
				project, JavaPlugin.CLASSES_TASK_NAME);

			classesTask.dependsOn(packageRunBuildTask);
		}
	}

	private void _configureTaskPackageRunTestForLifecycleBasePlugin(
		PackageRunTestTask packageRunTestTask) {

		Task checkTask = GradleUtil.getTask(
			packageRunTestTask.getProject(),
			LifecycleBasePlugin.CHECK_TASK_NAME);

		checkTask.dependsOn(packageRunTestTask);
	}

	private void _configureTaskPublishNodeModule(
		PublishNodeModuleTask publishNodeModuleTask) {

		final Project project = publishNodeModuleTask.getProject();

		publishNodeModuleTask.setModuleDescription(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return project.getDescription();
				}

			});

		publishNodeModuleTask.setModuleName(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					String moduleName = OSGiUtil.getBundleSymbolicName(project);

					int pos = moduleName.indexOf('.');

					if (pos != -1) {
						moduleName = moduleName.substring(pos + 1);

						moduleName = moduleName.replace('.', '-');
					}

					return moduleName;
				}

			});

		publishNodeModuleTask.setModuleVersion(
			new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					return project.getVersion();
				}

			});
	}

	private void _configureTaskYarnInstall(YarnInstallTask yarnInstallTask) {
		TaskOutputs taskOutputs = yarnInstallTask.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});
	}

	private void _configureTasksDownloadNodeModule(
		Project project, final NpmInstallTask npmInstallTask,
		final Map<String, Object> packageJsonMap) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			DownloadNodeModuleTask.class,
			new Action<DownloadNodeModuleTask>() {

				@Override
				public void execute(
					DownloadNodeModuleTask downloadNodeModuleTask) {

					_configureTaskDownloadNodeModule(
						downloadNodeModuleTask, npmInstallTask, packageJsonMap);
				}

			});
	}

	private void _configureTasksExecuteNode(
		Project project, final NodeExtension nodeExtension,
		final boolean useGradleExec) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			ExecuteNodeTask.class,
			new Action<ExecuteNodeTask>() {

				@Override
				public void execute(ExecuteNodeTask executeNodeTask) {
					_configureTaskExecuteNode(
						executeNodeTask, nodeExtension, useGradleExec);
				}

			});
	}

	private void _configureTasksExecutePackageManager(
		Project project, final NodeExtension nodeExtension) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			ExecutePackageManagerTask.class,
			new Action<ExecutePackageManagerTask>() {

				@Override
				public void execute(
					ExecutePackageManagerTask executePackageManagerTask) {

					_configureTaskExecutePackageManager(
						executePackageManagerTask, nodeExtension);
				}

			});
	}

	private void _configureTasksExecutePackageManagerArgs(
		Project project, final NodeExtension nodeExtension) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			ExecutePackageManagerTask.class,
			new Action<ExecutePackageManagerTask>() {

				@Override
				public void execute(
					ExecutePackageManagerTask executePackageManagerTask) {

					_configureTaskExecutePackageManagerArgs(
						executePackageManagerTask, nodeExtension);
				}

			});
	}

	private void _configureTasksNpmInstall(
		Project project, final NodeExtension nodeExtension) {

		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			NpmInstallTask.class,
			new Action<NpmInstallTask>() {

				@Override
				public void execute(NpmInstallTask npmInstallTask) {
					_configureTaskNpmInstall(npmInstallTask, nodeExtension);
				}

			});
	}

	private void _configureTasksPublishNodeModule(Project project) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			PublishNodeModuleTask.class,
			new Action<PublishNodeModuleTask>() {

				@Override
				public void execute(
					PublishNodeModuleTask publishNodeModuleTask) {

					_configureTaskPublishNodeModule(publishNodeModuleTask);
				}

			});
	}

	private void _configureTasksYarnInstall(Project project) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.withType(
			YarnInstallTask.class,
			new Action<YarnInstallTask>() {

				@Override
				public void execute(YarnInstallTask yarnInstallTask) {
					_configureTaskYarnInstall(yarnInstallTask);
				}

			});
	}

	private File _getYarnWorkingDir(
		Project project, NodeExtension nodeExtension) {

		if (nodeExtension.isUseNpm()) {
			return null;
		}

		File projectDir = project.getProjectDir();

		File dir = projectDir.getParentFile();

		while (true) {
			File packageJsonFile = new File(dir, "package.json");

			if (_hasYarnPackage(projectDir, packageJsonFile)) {
				return dir;
			}

			dir = dir.getParentFile();

			if (dir == null) {
				return null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private boolean _hasLiferayNpmScripts12Dependency(File packageJSONFile) {
		if ((packageJSONFile == null) || !packageJSONFile.exists()) {
			return false;
		}

		JsonSlurper jsonSlurper = new JsonSlurper();

		Map<String, Object> packageJSONMap =
			(Map<String, Object>)jsonSlurper.parse(packageJSONFile);

		Map<String, String> scriptsJSONMap =
			(Map<String, String>)packageJSONMap.get("scripts");

		if (scriptsJSONMap != null) {
			for (Map.Entry<String, String> entry : scriptsJSONMap.entrySet()) {
				String command = entry.getValue();
				String name = entry.getKey();

				if (Objects.equals(name, "build") &&
					(Objects.equals(command, "node-scripts") ||
					 command.startsWith("node-scripts "))) {

					return true;
				}
			}
		}

		Map<String, Object> devDependencies =
			(Map<String, Object>)packageJSONMap.get("devDependencies");

		if (devDependencies == null) {
			return false;
		}

		String dependencyName = null;

		if (devDependencies.containsKey("@liferay/npm-scripts")) {
			dependencyName = "@liferay/npm-scripts";
		}
		else if (devDependencies.containsKey("liferay-npm-scripts")) {
			dependencyName = "liferay-npm-scripts";
		}
		else {
			return false;
		}

		VersionNumber versionNumber = VersionNumber.parse(
			(String)devDependencies.get(dependencyName));

		int majorVersion = versionNumber.getMajor();

		if (majorVersion < _liferayNpmScripts12VersionNumber.getMajor()) {
			return false;
		}

		return true;
	}

	private boolean _hasLiferayNpmScripts12Dependency(
		Project project, File yarnWorkingDir) {

		File packageJSONFile = project.file("package.json");

		if (_hasLiferayNpmScripts12Dependency(packageJSONFile)) {
			return true;
		}

		packageJSONFile = new File(yarnWorkingDir, "package.json");

		return _hasLiferayNpmScripts12Dependency(packageJSONFile);
	}

	@SuppressWarnings("unchecked")
	private boolean _hasYarnPackage(File projectDir, File packageJsonFile) {
		if (!packageJsonFile.exists()) {
			return false;
		}

		File dir = packageJsonFile.getParentFile();

		JsonSlurper jsonSlurper = new JsonSlurper();

		Map<String, Object> map = (Map<String, Object>)jsonSlurper.parse(
			packageJsonFile);

		map = (Map<String, Object>)map.get("workspaces");

		if (map == null) {
			return false;
		}

		List<String> packages = (List<String>)map.get("packages");

		if (packages == null) {
			return false;
		}

		String absolutePath = dir.getAbsolutePath();

		if (File.separatorChar == '\\') {
			absolutePath = absolutePath.replace('\\', '/');
		}

		Path dirPath = dir.toPath();

		FileSystem fileSystem = dirPath.getFileSystem();

		for (String pattern : packages) {
			String s = "glob:" + absolutePath + '/' + pattern;

			PathMatcher pathMatcher = fileSystem.getPathMatcher(s);

			if (pathMatcher.matches(projectDir.toPath())) {
				return true;
			}
		}

		return false;
	}

	private boolean _isNodeBuildRequired(Project project) {
		Gradle gradle = project.getGradle();

		StartParameter startParameter = gradle.getStartParameter();

		List<String> taskNames = startParameter.getTaskNames();

		if (taskNames.isEmpty()) {
			return true;
		}

		for (String taskName : taskNames) {
			int index = taskName.lastIndexOf(':');

			String simpleTaskName = taskName;

			if (index >= 0) {
				simpleTaskName = taskName.substring(index + 1);
			}

			if (!simpleTaskName.equals(_BASELINE_TASK_NAME)) {
				return true;
			}
		}

		return false;
	}

	private static final String _BASELINE_TASK_NAME = "baseline";

	private static final String _PACKAGE_RUN_TASK_NAME_PREFIX = "packageRun";

	private static final VersionNumber _liferayNpmScripts12VersionNumber =
		VersionNumber.version(12);
	private static final VersionNumber _node8VersionNumber =
		VersionNumber.version(8);
	private static final VersionNumber _npm5VersionNumber =
		VersionNumber.version(5);

}