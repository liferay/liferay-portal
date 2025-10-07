/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.configurator;

import com.bmuschko.gradle.docker.DockerRegistryCredentials;
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin;
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer;
import com.bmuschko.gradle.docker.tasks.container.DockerLogsContainer;
import com.bmuschko.gradle.docker.tasks.container.DockerRemoveContainer;
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer;
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer;
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage;
import com.bmuschko.gradle.docker.tasks.image.DockerPullImage;
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage;
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage;
import com.bmuschko.gradle.docker.tasks.image.DockerTagImage;
import com.bmuschko.gradle.docker.tasks.image.Dockerfile;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.node.NodeExtension;
import com.liferay.gradle.plugins.node.task.NpmInstallTask;
import com.liferay.gradle.plugins.source.formatter.FormatSourceTask;
import com.liferay.gradle.plugins.source.formatter.SourceFormatterPlugin;
import com.liferay.gradle.plugins.workspace.LiferayWorkspaceYarnPlugin;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.WorkspacePlugin;
import com.liferay.gradle.plugins.workspace.docker.DockerPruneImage;
import com.liferay.gradle.plugins.workspace.internal.configurator.TargetPlatformRootProjectConfigurator;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.gradle.plugins.workspace.task.CreateTokenTask;
import com.liferay.gradle.plugins.workspace.task.InitBundleTask;
import com.liferay.gradle.plugins.workspace.task.VerifyBundleTask;
import com.liferay.gradle.plugins.workspace.task.VerifyProductTask;
import com.liferay.gradle.util.ArrayUtil;
import com.liferay.gradle.util.OSDetector;
import com.liferay.gradle.util.Validator;
import com.liferay.release.util.ReleaseEntry;
import com.liferay.release.util.ReleaseUtil;

import de.undercouch.gradle.tasks.download.Download;

import groovy.lang.Closure;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.io.FilenameUtils;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;
import org.gradle.api.tasks.bundling.Compression;
import org.gradle.api.tasks.bundling.Tar;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.util.Path;

/**
 * @author Andrea Di Giorgi
 * @author David Truong
 * @author Simon Jiang
 * @author Seiphon Wang
 * @author Kevin Lee
 */
@SuppressWarnings("deprecation")
public class RootProjectConfigurator implements Plugin<Project> {

	public static final String BUILD_DOCKER_IMAGE_TASK_NAME =
		"buildDockerImage";

	public static final String BUNDLE_CONFIGURATION_NAME = "bundle";

	public static final String BUNDLE_GROUP = "bundle";

	public static final String BUNDLE_SUPPORT_CONFIGURATION_NAME =
		"bundleSupport";

	public static final String CLEAN_DOCKER_IMAGE_TASK_NAME =
		"cleanDockerImage";

	public static final String CLEAN_TASK_NAME =
		LifecycleBasePlugin.CLEAN_TASK_NAME;

	public static final String CREATE_DOCKER_CONTAINER_TASK_NAME =
		"createDockerContainer";

	public static final String CREATE_DOCKERFILE_ALL_TASK_NAME =
		"createDockerfileAll";

	public static final String CREATE_DOCKERFILE_TASK_NAME = "createDockerfile";

	public static final String CREATE_TOKEN_TASK_NAME = "createToken";

	public static final String DIST_BUNDLE_TAR_ALL_TASK_NAME =
		"distBundleTarAll";

	public static final String DIST_BUNDLE_TAR_TASK_NAME = "distBundleTar";

	public static final String DIST_BUNDLE_TASK_NAME = "distBundle";

	public static final String DIST_BUNDLE_ZIP_ALL_TASK_NAME =
		"distBundleZipAll";

	public static final String DIST_BUNDLE_ZIP_TASK_NAME = "distBundleZip";

	public static final String DOCKER_DEPLOY_TASK_NAME = "dockerDeploy";

	public static final String DOCKER_GROUP = "docker";

	public static final String DOWNLOAD_BUNDLE_TASK_NAME = "downloadBundle";

	public static final String FORMAT_SOURCE_UPGRADE_TASK_NAME =
		"formatSourceUpgrade";

	public static final String INIT_BUNDLE_TASK_NAME = "initBundle";

	public static final String LIFERAY_CONFIGS_DIR_NAME = "configs";

	public static final String LOGS_DOCKER_CONTAINER_TASK_NAME =
		"logsDockerContainer";

	public static final String PROVIDED_MODULES_CONFIGURATION_NAME =
		"providedModules";

	public static final String PRUNE_DOCKER_IMAGE_TASK_NAME =
		"pruneDockerImage";

	public static final String PULL_DOCKER_IMAGE_TASK_NAME = "pullDockerImage";

	public static final String PUSH_DOCKER_IMAGE_TASK_NAME = "pushDockerImage";

	public static final String REMOVE_DOCKER_CONTAINER_TASK_NAME =
		"removeDockerContainer";

	public static final String START_DOCKER_CONTAINER_TASK_NAME =
		"startDockerContainer";

	public static final String START_REGISTRY_CONTAINER_TASK_NAME =
		"startRegistryContainer";

	public static final String STOP_DOCKER_CONTAINER_TASK_NAME =
		"stopDockerContainer";

	public static final String TAG_DOCKER_IMAGE_TASK_NAME = "tagDockerImage";

	public static final String UPGRADE_JAKARTA_TASK_NAME = "upgradeJakarta";

	public static final String VERIFY_BUNDLE_TASK_NAME = "verifyBundle";

	public static final String VERIFY_PRODUCT_TASK_NAME = "verifyProduct";

	/**
	 * @deprecated As of 1.4.0, replaced by
	 *             {@link #RootProjectConfigurator(Settings)}
	 */
	@Deprecated
	public RootProjectConfigurator() {
	}

	public RootProjectConfigurator(Settings settings) {
		_bundleCheckSumSHA512 = GradleUtil.getProperty(
			settings,
			WorkspacePlugin.PROPERTY_PREFIX + "bundle.checksum.sha512", null);
		_defaultRepositoryEnabled = GradleUtil.getProperty(
			settings,
			WorkspacePlugin.PROPERTY_PREFIX + "default.repository.enabled",
			_DEFAULT_REPOSITORY_ENABLED);
	}

	@Override
	public void apply(Project project) {
		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		_configureWorkspaceExtension(project, workspaceExtension);

		GradleUtil.applyPlugin(project, DockerRemoteApiPlugin.class);
		GradleUtil.applyPlugin(project, LifecycleBasePlugin.class);
		GradleUtil.applyPlugin(project, SourceFormatterPlugin.class);

		String nodePackageManager = workspaceExtension.getNodePackageManager();

		if (nodePackageManager.equals("yarn")) {
			GradleUtil.applyPlugin(project, LiferayWorkspaceYarnPlugin.class);
		}
		else {
			_configureNpmProject(project);
		}

		if (isDefaultRepositoryEnabled()) {
			GradleUtil.addDefaultRepositories(project);
		}

		Configuration bundleSupportConfiguration =
			_addConfigurationBundleSupport(project);

		Configuration providedModulesConfiguration =
			_addConfigurationProvidedModules(project);

		TargetPlatformRootProjectConfigurator.INSTANCE.apply(project);

		_addTaskCreateToken(project);

		VerifyProductTask verifyProductTask = _addTaskVerifyProduct(
			project, workspaceExtension);

		Download downloadBundleTask = _addTaskDownloadBundle(
			project, verifyProductTask, workspaceExtension);

		_addTaskVerifyBundle(project, downloadBundleTask, workspaceExtension);

		_addTaskInitBundle(
			project, downloadBundleTask, workspaceExtension,
			bundleSupportConfiguration, providedModulesConfiguration,
			INIT_BUNDLE_TASK_NAME);

		Copy distBundleTask = _addTaskDistBundle(
			project, downloadBundleTask, DIST_BUNDLE_TASK_NAME,
			workspaceExtension, bundleSupportConfiguration, null,
			providedModulesConfiguration);

		_addTasksDistBundleEnvironments(
			project, downloadBundleTask, workspaceExtension,
			bundleSupportConfiguration, providedModulesConfiguration);

		_addTasksDistBundleArchive(project, distBundleTask, workspaceExtension);

		_addDockerTasks(
			project, workspaceExtension, providedModulesConfiguration,
			verifyProductTask);

		_addTaskUpgradeJakarta(project);

		_addTaskFormatSourceUpgrade(project);
	}

	public boolean isDefaultRepositoryEnabled() {
		return _defaultRepositoryEnabled;
	}

	public void setDefaultRepositoryEnabled(boolean defaultRepositoryEnabled) {
		_defaultRepositoryEnabled = defaultRepositoryEnabled;
	}

	private Configuration _addConfigurationBundleSupport(
		final Project project) {

		Configuration configuration = GradleUtil.addConfiguration(
			project, BUNDLE_SUPPORT_CONFIGURATION_NAME);

		configuration.defaultDependencies(
			new Action<DependencySet>() {

				@Override
				public void execute(DependencySet dependencySet) {
					_addDependenciesBundleSupport(project);
				}

			});

		configuration.setDescription(
			"Configures Liferay Bundle Support for this project.");
		configuration.setVisible(false);

		return configuration;
	}

	private Configuration _addConfigurationProvidedModules(Project project) {
		Configuration configuration = GradleUtil.addConfiguration(
			project, PROVIDED_MODULES_CONFIGURATION_NAME);

		configuration.setDescription(
			"Configures additional 3rd-party OSGi modules to add to Liferay.");
		configuration.setTransitive(false);
		configuration.setVisible(true);

		return configuration;
	}

	private void _addDependenciesBundleSupport(Project project) {
		GradleUtil.addDependency(
			project, BUNDLE_SUPPORT_CONFIGURATION_NAME, "com.liferay",
			"com.liferay.portal.tools.bundle.support", "latest.release");
	}

	private void _addDockerTasks(
		Project project, WorkspaceExtension workspaceExtension,
		Configuration providedModulesConfiguration,
		VerifyProductTask verifyProductTask) {

		Copy dockerDeploy = _addTaskDockerDeploy(
			project, workspaceExtension, providedModulesConfiguration);

		Dockerfile dockerfile = _addTaskCreateDockerfile(
			project, workspaceExtension, dockerDeploy, verifyProductTask);

		DockerBuildImage dockerBuildImage = _addTaskBuildDockerImage(
			dockerfile, workspaceExtension, verifyProductTask);

		DockerStopContainer dockerStopContainer = _addTaskStopDockerContainer(
			project);

		DockerRemoveContainer dockerRemoveContainer =
			_addTaskRemoveDockerContainer(project, dockerStopContainer);

		DockerCreateContainer dockerCreateContainer =
			_addTaskCreateDockerContainer(
				project, workspaceExtension, dockerBuildImage,
				dockerRemoveContainer, verifyProductTask);

		_addTaskStartDockerContainer(project, dockerCreateContainer);

		_addTaskLogsDockerContainer(project);

		_addTaskPullDockerImage(project, workspaceExtension, verifyProductTask);

		DockerTagImage dockerTagImage = _addTaskDockerTagImage(
			project, workspaceExtension, dockerBuildImage);

		_addTaskPushDockerImage(project, workspaceExtension, dockerTagImage);
	}

	private DockerBuildImage _addTaskBuildDockerImage(
		Dockerfile dockerfile, WorkspaceExtension workspaceExtension,
		VerifyProductTask verifyProductTask) {

		Project project = dockerfile.getProject();

		DockerBuildImage dockerBuildImage = GradleUtil.addTask(
			project, BUILD_DOCKER_IMAGE_TASK_NAME, DockerBuildImage.class);

		dockerBuildImage.dependsOn(verifyProductTask, dockerfile);
		dockerBuildImage.mustRunAfter(verifyProductTask);

		DirectoryProperty inputDirectoryProperty =
			dockerBuildImage.getInputDir();

		inputDirectoryProperty.set(workspaceExtension.getDockerDir());

		Property<Boolean> pullProperty = dockerBuildImage.getPull();

		pullProperty.set(workspaceExtension.getDockerPullPolicy());

		if (Objects.nonNull(
				workspaceExtension.getDockerLocalRegistryAddress())) {

			DockerRegistryCredentials dockerRegistryCredentials =
				dockerBuildImage.getRegistryCredentials();

			String dockerUserName = workspaceExtension.getDockerUserName();

			if (Objects.nonNull(dockerUserName)) {
				Property<String> userNameProperty =
					dockerRegistryCredentials.getUsername();

				userNameProperty.set(dockerUserName);
			}

			String dockerUserAccessToken =
				workspaceExtension.getDockerUserAccessToken();

			if (Objects.nonNull(dockerUserAccessToken)) {
				Property<String> passwordProperty =
					dockerRegistryCredentials.getPassword();

				passwordProperty.set(dockerUserAccessToken);
			}
		}

		dockerBuildImage.setDescription(
			"Builds a child docker image from Liferay base image with all " +
				"configs deployed.");
		dockerBuildImage.setGroup(DOCKER_GROUP);

		DockerRemoveImage dockerRemoveImage = GradleUtil.addTask(
			project, CLEAN_DOCKER_IMAGE_TASK_NAME, DockerRemoveImage.class);

		dockerRemoveImage.dependsOn(REMOVE_DOCKER_CONTAINER_TASK_NAME);
		dockerRemoveImage.setDescription("Removes the Docker image.");
		dockerRemoveImage.setGroup(DOCKER_GROUP);

		Property<Boolean> forceProperty = dockerRemoveImage.getForce();

		forceProperty.set(true);

		dockerRemoveImage.onError(
			new Action<Throwable>() {

				@Override
				public void execute(Throwable throwable) {
					Logger logger = project.getLogger();

					if (logger.isWarnEnabled()) {
						logger.warn(
							"No image with ID '" + _getDockerImageId(project) +
								"' found.");
					}
				}

			});

		DockerPruneImage dockerPruneImage = GradleUtil.addTask(
			project, PRUNE_DOCKER_IMAGE_TASK_NAME, DockerPruneImage.class);

		dockerPruneImage.setDescription("Prunes the Docker image.");
		dockerPruneImage.setGroup(DOCKER_GROUP);

		Project rootProject = project.getRootProject();

		rootProject.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project p) {
					String dockerImageId = _getDockerImageId(project);

					SetProperty<String> setProperty =
						dockerBuildImage.getImages();

					setProperty.add(dockerImageId);

					Property<String> dockerPruneImageIdProperty =
						dockerPruneImage.getImageId();

					dockerPruneImageIdProperty.set(dockerImageId);

					Property<String> dockerRemoveImageIdProperty =
						dockerRemoveImage.getImageId();

					dockerRemoveImageIdProperty.set(dockerImageId);
				}

			});

		Task cleanTask = GradleUtil.getTask(
			project, LifecycleBasePlugin.CLEAN_TASK_NAME);

		cleanTask.dependsOn(dockerRemoveImage);

		return dockerBuildImage;
	}

	private DockerCreateContainer _addTaskCreateDockerContainer(
		Project project, WorkspaceExtension workspaceExtension,
		DockerBuildImage dockerBuildImage,
		DockerRemoveContainer dockerRemoveContainer,
		VerifyProductTask verifyProductTask) {

		DockerCreateContainer dockerCreateContainer = GradleUtil.addTask(
			project, CREATE_DOCKER_CONTAINER_TASK_NAME,
			DockerCreateContainer.class);

		dockerCreateContainer.dependsOn(verifyProductTask, dockerBuildImage);
		dockerCreateContainer.mustRunAfter(
			verifyProductTask, dockerRemoveContainer);

		File dockerDir = workspaceExtension.getDockerDir();

		File clientExtensionsDir = new File(dockerDir, "client-extensions");
		File deployDir = new File(dockerDir, "deploy");
		File workDir = new File(dockerDir, "work");

		String clientExtensionsPath = clientExtensionsDir.getAbsolutePath();
		String deployPath = deployDir.getAbsolutePath();
		String dockerPath = dockerDir.getAbsolutePath();
		String workPath = workDir.getAbsolutePath();

		if (OSDetector.isWindows()) {
			String prefix = FilenameUtils.getPrefix(dockerPath);

			if (prefix.contains(":")) {
				clientExtensionsPath =
					'/' + clientExtensionsPath.replace(":", "");
				deployPath = '/' + deployPath.replace(":", "");
				dockerPath = '/' + dockerPath.replace(":", "");
				workPath = '/' + workPath.replace(":", "");
			}

			clientExtensionsPath = clientExtensionsPath.replace('\\', '/');
			deployPath = deployPath.replace('\\', '/');
			dockerPath = dockerPath.replace('\\', '/');
			workPath = workPath.replace('\\', '/');
		}

		DockerCreateContainer.HostConfig hostConfig =
			dockerCreateContainer.getHostConfig();

		Property<Boolean> autoRemoveProperty = hostConfig.getAutoRemove();

		autoRemoveProperty.set(true);

		MapProperty<String, String> bindsMapProperty = hostConfig.getBinds();

		bindsMapProperty.put(
			clientExtensionsPath, "/opt/liferay/osgi/client-extensions");
		bindsMapProperty.put(deployPath, "/mnt/liferay/deploy");
		bindsMapProperty.put(workPath, "/opt/liferay/work");

		dockerCreateContainer.setDescription(
			"Creates a Docker container from your built image and mounts " +
				dockerPath + " to /mnt/liferay.");
		dockerCreateContainer.setGroup(DOCKER_GROUP);

		ListProperty<String> portBindingsListProperty =
			hostConfig.getPortBindings();

		portBindingsListProperty.add("8000:8000");
		portBindingsListProperty.add("8080:8080");
		portBindingsListProperty.add("11311:11311");

		dockerCreateContainer.targetImageId(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return _getDockerImageId(project);
				}

			});

		dockerCreateContainer.withEnvVar("JPDA_ADDRESS", "0.0.0.0:8000");
		dockerCreateContainer.withEnvVar("LIFERAY_JPDA_ENABLED", "true");
		dockerCreateContainer.withEnvVar(
			_getEnvVarOverride("module.framework.properties.osgi.console"),
			"0.0.0.0:11311");
		dockerCreateContainer.withEnvVar(
			"LIFERAY_WORKSPACE_ENVIRONMENT",
			workspaceExtension.getEnvironment());

		Property<String> containerNameProperty =
			dockerCreateContainer.getContainerName();

		containerNameProperty.set(_getDockerContainerId(project));

		Task cleanTask = GradleUtil.getTask(
			project, LifecycleBasePlugin.CLEAN_TASK_NAME);

		cleanTask.dependsOn(dockerRemoveContainer);

		return dockerCreateContainer;
	}

	private Dockerfile _addTaskCreateDockerfile(
		Project project, final WorkspaceExtension workspaceExtension,
		Copy dockerDeploy, VerifyProductTask verifyProductTask) {

		Dockerfile dockerfile = GradleUtil.addTask(
			project, CREATE_DOCKERFILE_TASK_NAME, Dockerfile.class);

		dockerfile.dependsOn(verifyProductTask, dockerDeploy);
		dockerfile.mustRunAfter(verifyProductTask);

		dockerfile.instruction(
			"ENV LIFERAY_WORKSPACE_ENVIRONMENT=" +
				workspaceExtension.getEnvironment());

		dockerfile.instruction(
			"COPY --chown=liferay:liferay client-extensions /home/liferay" +
				"/osgi/client-extensions");
		dockerfile.instruction(
			"COPY --chown=liferay:liferay deploy /mnt/liferay/deploy");
		dockerfile.instruction(
			"COPY --chown=liferay:liferay patching /mnt/liferay/patching");
		dockerfile.instruction(
			"COPY --chown=liferay:liferay scripts /mnt/liferay/scripts");
		dockerfile.instruction(
			"COPY --chown=liferay:liferay " + LIFERAY_CONFIGS_DIR_NAME +
				" /home/liferay/configs");
		dockerfile.instruction(
			"COPY --chown=liferay:liferay " + _LIFERAY_IMAGE_SETUP_SCRIPT +
				" /usr/local/liferay/scripts/pre-configure/" +
					_LIFERAY_IMAGE_SETUP_SCRIPT);

		File file = project.file("Dockerfile.ext");

		if (file.exists()) {
			dockerfile.instructionsFromTemplate(file);
		}

		dockerfile.setDescription(
			"Creates a Dockerfile to build the Liferay Workspace Docker " +
				"image.");
		dockerfile.setGroup(DOCKER_GROUP);

		dockerfile.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					try {
						File destinationDir = workspaceExtension.getDockerDir();

						_createTouchFile(
							new File(destinationDir, "client-extensions"));
						_createTouchFile(new File(destinationDir, "configs"));
						_createTouchFile(new File(destinationDir, "deploy"));
						_createTouchFile(new File(destinationDir, "patching"));
						_createTouchFile(new File(destinationDir, "scripts"));
						_createTouchFile(new File(destinationDir, "work"));

						File file = new File(
							destinationDir, _LIFERAY_IMAGE_SETUP_SCRIPT);

						try {
							String template = _loadTemplate(
								_LIFERAY_IMAGE_SETUP_SCRIPT + ".tpl");

							Files.write(file.toPath(), template.getBytes());
						}
						catch (IOException ioException) {
							throw new GradleException(
								"Unable to write script file: " +
									file.getAbsolutePath(),
								ioException);
						}
					}
					catch (IOException ioException) {
						Logger logger = dockerfile.getLogger();

						if (logger.isWarnEnabled()) {
							StringBuilder sb = new StringBuilder();

							sb.append("Could not create a placeholder file. ");
							sb.append("Please make sure you have at least ");
							sb.append("one config or the buildDockerImage ");
							sb.append("task will fail.");

							logger.warn(sb.toString());
						}
					}
				}

			});

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project p) {
					ListProperty<Dockerfile.Instruction> instructions =
						dockerfile.getInstructions();

					List<Dockerfile.Instruction> originalInstructions =
						new ArrayList<>(instructions.get());

					String dockerLocalRegistryAddress =
						workspaceExtension.getDockerLocalRegistryAddress();

					String dockerImageLiferay =
						workspaceExtension.getDockerImageLiferay();

					if (Objects.nonNull(dockerLocalRegistryAddress)) {
						dockerImageLiferay = dockerImageLiferay.replace(
							"liferay", dockerLocalRegistryAddress);
					}

					Dockerfile.FromInstruction baseImage =
						new Dockerfile.FromInstruction(
							new Dockerfile.From(dockerImageLiferay));

					originalInstructions.add(0, baseImage);

					instructions.set(originalInstructions);
				}

			});

		return dockerfile;
	}

	private CreateTokenTask _addTaskCreateToken(Project project) {
		CreateTokenTask createTokenTask = GradleUtil.addTask(
			project, CREATE_TOKEN_TASK_NAME, CreateTokenTask.class);

		createTokenTask.setDescription(
			"This task is deprecated and it will be removed in future.");

		return createTokenTask;
	}

	private Copy _addTaskDistBundle(
		Project project, Download downloadBundleTask, String taskName,
		WorkspaceExtension workspaceExtension,
		Configuration bundleSupportConfiguration, String environment,
		Configuration providedModulesConfiguration) {

		InitBundleTask initBundleTask = _addTaskInitBundle(
			project, downloadBundleTask, workspaceExtension,
			bundleSupportConfiguration, providedModulesConfiguration,
			taskName + "InitBundle");

		initBundleTask.setConfigEnvironment(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					if (environment == null) {
						return workspaceExtension.getEnvironment();
					}

					return environment;
				}

			});
		initBundleTask.setDestinationDir(
			new File(
				project.getBuildDir(),
				"dist" + StringUtil.capitalize(environment)));
		initBundleTask.setGroup("hidden");

		initBundleTask.doFirst(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					InitBundleTask initBundleTask = (InitBundleTask)task;

					Project project = initBundleTask.getProject();

					project.delete(initBundleTask.getDestinationDir());
				}

			});

		Copy copy = GradleUtil.addTask(project, taskName, Copy.class);

		copy.dependsOn(initBundleTask);
		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return initBundleTask.getDestinationDir();
				}

			});
		copy.setDescription("Assembles the Liferay bundle.");
		copy.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE);

		_configureTaskCopyBundlePreserveTimestamps(copy);
		_configureTaskDisableUpToDate(copy);

		return copy;
	}

	@SuppressWarnings("serial")
	private <T extends AbstractArchiveTask> T _addTaskDistBundle(
		Project project, String taskName, Class<T> clazz,
		final Copy distBundleTask,
		final WorkspaceExtension workspaceExtension) {

		T task = GradleUtil.addTask(project, taskName, clazz);

		_configureTaskDisableUpToDate(task);

		task.into(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					String bundleDistRootDirName =
						workspaceExtension.getBundleDistRootDirName();

					if (Validator.isNull(bundleDistRootDirName)) {
						bundleDistRootDirName = "";
					}

					return bundleDistRootDirName;
				}

			},
			new Closure<Void>(task) {

				@SuppressWarnings("unused")
				public void doCall(CopySpec copySpec) {
					copySpec.from(distBundleTask);
				}

			});

		DirectoryProperty destinationDirectoryProperty =
			task.getDestinationDirectory();

		destinationDirectoryProperty.set(project.getBuildDir());

		task.setGroup(BUNDLE_GROUP);

		return task;
	}

	@SuppressWarnings("serial")
	private Copy _addTaskDockerDeploy(
		Project project, WorkspaceExtension workspaceExtension,
		Configuration providedModulesConfiguration) {

		Copy copy = GradleUtil.addTask(
			project, DOCKER_DEPLOY_TASK_NAME, Copy.class);

		copy.setDescription(
			"Copy the Liferay configs and provided configurations to the " +
				"docker build directory.");
		copy.setGroup(DOCKER_GROUP);

		copy.setDestinationDir(workspaceExtension.getDockerDir());
		copy.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);

		copy.from(
			providedModulesConfiguration,
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(CopySpec copySpec) {
					copySpec.into("deploy");
				}

			});

		File configsDir = workspaceExtension.getConfigsDir();

		if (configsDir.exists()) {
			List<String> commonConfigDirNames = Arrays.asList(
				"common", "docker");

			File[] configDirs = configsDir.listFiles(
				(dir, name) -> {
					File file = new File(dir, name);

					if (!file.isDirectory() ||
						commonConfigDirNames.contains(name)) {

						return false;
					}

					return true;
				});

			if ((configDirs == null) || (configDirs.length == 0)) {
				throw new GradleException(
					"The 'configs' directory must contain one directory not " +
						"named: " + StringUtil.toString(commonConfigDirNames));
			}

			for (String commonConfigDirName : commonConfigDirNames) {
				for (File configDir : configDirs) {
					copy.from(
						new Callable<File>() {

							@Override
							public File call() throws Exception {
								return new File(
									configsDir, commonConfigDirName);
							}

						},
						new Closure<Void>(project) {

							@SuppressWarnings("unused")
							public void doCall(CopySpec copySpec) {
								copySpec.into(
									LIFERAY_CONFIGS_DIR_NAME + "/" +
										configDir.getName());
							}

						});
				}
			}

			copy.from(
				new Callable<File>() {

					@Override
					public File call() throws Exception {
						return configsDir;
					}

				},
				new Closure<Void>(project) {

					@SuppressWarnings("unused")
					public void doCall(CopySpec copySpec) {
						copySpec.exclude(commonConfigDirNames);
						copySpec.into(LIFERAY_CONFIGS_DIR_NAME);
					}

				});
		}

		Task deployTask = GradleUtil.addTask(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME, Copy.class);

		deployTask.finalizedBy(copy);

		return copy;
	}

	private DockerTagImage _addTaskDockerTagImage(
		Project project, WorkspaceExtension workspaceExtension,
		DockerBuildImage buildDockerImage) {

		DockerTagImage dockerTagImage = GradleUtil.addTask(
			project, TAG_DOCKER_IMAGE_TASK_NAME, DockerTagImage.class);

		dockerTagImage.setDescription("Tag the Docker image.");
		dockerTagImage.setGroup(DOCKER_GROUP);

		dockerTagImage.dependsOn(buildDockerImage);

		Property<String> imageIdProperty = dockerTagImage.getImageId();

		imageIdProperty.set(workspaceExtension.getDockerImageId());

		String dockerUserName = workspaceExtension.getDockerUserName();

		Property<String> repositoryProperty = dockerTagImage.getRepository();

		if (Objects.nonNull(
				workspaceExtension.getDockerLocalRegistryAddress())) {

			repositoryProperty.set(
				workspaceExtension.getDockerLocalRegistryAddress() + "/" +
					workspaceExtension.getDockerImageId());
		}
		else if (Objects.nonNull(dockerUserName)) {
			repositoryProperty.set(
				dockerUserName + "/" + workspaceExtension.getDockerImageId());
		}
		else {
			repositoryProperty.set(workspaceExtension.getDockerImageId());
		}

		Property<String> tagProperty = dockerTagImage.getTag();

		tagProperty.set(_getDockerImageTag(project, workspaceExtension));

		return dockerTagImage;
	}

	private Download _addTaskDownloadBundle(
		final Project project, VerifyProductTask verifyProductTask,
		final WorkspaceExtension workspaceExtension) {

		final Download download = GradleUtil.addTask(
			project, DOWNLOAD_BUNDLE_TASK_NAME, Download.class);

		download.dependsOn(verifyProductTask);

		download.onlyIf(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					if (Validator.isNull(workspaceExtension.getBundleUrl())) {
						return false;
					}

					File downloadFile = _getDownloadFile((Download)task);

					if (downloadFile.exists()) {
						Logger logger = task.getLogger();

						if (logger.isInfoEnabled()) {
							logger.info(
								"Bundle archive file is already downloaded " +
									"at {}",
								downloadFile);
						}

						return false;
					}

					return true;
				}

			});

		download.onlyIfNewer(true);
		download.setDescription("Downloads the Liferay bundle zip file.");

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureDownloadTask(
						project, download, workspaceExtension);
				}

			});

		return download;
	}

	private FormatSourceTask _addTaskFormatSourceUpgrade(Project project) {
		FormatSourceTask formatSourceTask = GradleUtil.addTask(
			project, FORMAT_SOURCE_UPGRADE_TASK_NAME, FormatSourceTask.class);

		formatSourceTask.onlyIf(_skipIfExecutingParentTaskSpec);
		formatSourceTask.setCheckCategoryNames("Upgrade");
		formatSourceTask.setDescription(
			"Runs Liferay Source Formatter to perform Upgrade SF checks.");
		formatSourceTask.setGroup("formatting");

		return formatSourceTask;
	}

	private InitBundleTask _addTaskInitBundle(
		Project project, Download downloadBundleTask,
		final WorkspaceExtension workspaceExtension,
		Configuration bundleSupportConfiguration,
		Configuration osgiModulesConfiguration, String taskName) {

		InitBundleTask initBundleTask = GradleUtil.addTask(
			project, taskName, InitBundleTask.class);

		initBundleTask.dependsOn(
			VERIFY_PRODUCT_TASK_NAME, downloadBundleTask,
			VERIFY_BUNDLE_TASK_NAME);

		_configureFixTargetTomcatConfigs(
			initBundleTask::getDestinationDir, initBundleTask);

		initBundleTask.mustRunAfter(VERIFY_PRODUCT_TASK_NAME);
		initBundleTask.setClasspath(bundleSupportConfiguration);
		initBundleTask.setConfigEnvironment(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return workspaceExtension.getEnvironment();
				}

			});
		initBundleTask.setConfigsDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return workspaceExtension.getConfigsDir();
				}

			});
		initBundleTask.setDescription("Downloads and unzips the bundle.");
		initBundleTask.setDestinationDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return workspaceExtension.getHomeDir();
				}

			});
		initBundleTask.setGroup(BUNDLE_GROUP);
		initBundleTask.setProvidedModules(osgiModulesConfiguration);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					if (Validator.isNotNull(
							workspaceExtension.getBundleUrl())) {

						initBundleTask.setFile(
							new Callable<File>() {

								@Override
								public File call() throws Exception {
									return _getDownloadFile(downloadBundleTask);
								}

							});
					}
				}

			});

		return initBundleTask;
	}

	private DockerLogsContainer _addTaskLogsDockerContainer(Project project) {
		DockerLogsContainer dockerLogsContainer = GradleUtil.addTask(
			project, LOGS_DOCKER_CONTAINER_TASK_NAME,
			DockerLogsContainer.class);

		dockerLogsContainer.setDescription("Logs the Docker container.");
		dockerLogsContainer.setGroup(DOCKER_GROUP);

		Property<Boolean> followProperty = dockerLogsContainer.getFollow();

		followProperty.set(true);

		Property<Boolean> tailAllProperty = dockerLogsContainer.getTailAll();

		tailAllProperty.set(true);

		dockerLogsContainer.targetContainerId(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return _getDockerContainerId(project);
				}

			});

		return dockerLogsContainer;
	}

	private DockerPullImage _addTaskPullDockerImage(
		Project project, WorkspaceExtension workspaceExtension,
		VerifyProductTask verifyProductTask) {

		DockerPullImage dockerPullImage = GradleUtil.addTask(
			project, PULL_DOCKER_IMAGE_TASK_NAME, DockerPullImage.class);

		dockerPullImage.dependsOn(verifyProductTask);

		Property<String> imageProperty = dockerPullImage.getImage();

		String dockerUserName = workspaceExtension.getDockerUserName();

		if (Objects.nonNull(
				workspaceExtension.getDockerLocalRegistryAddress())) {

			imageProperty.set(
				workspaceExtension.getDockerLocalRegistryAddress() + "/" +
					workspaceExtension.getDockerImageId());
		}
		else if (Objects.nonNull(dockerUserName)) {
			imageProperty.set(
				dockerUserName + "/" + workspaceExtension.getDockerImageId());
		}
		else {
			imageProperty.set(workspaceExtension.getDockerImageLiferay());
		}

		DockerRegistryCredentials dockerRegistryCredentials =
			dockerPullImage.getRegistryCredentials();

		if (Objects.nonNull(dockerUserName)) {
			Property<String> userNameProperty =
				dockerRegistryCredentials.getUsername();

			userNameProperty.set(dockerUserName);
		}

		String dockerUserAccessToken =
			workspaceExtension.getDockerUserAccessToken();

		if (Objects.nonNull(dockerUserAccessToken)) {
			Property<String> passwordProperty =
				dockerRegistryCredentials.getPassword();

			passwordProperty.set(dockerUserAccessToken);
		}

		dockerPullImage.setDescription("Pull the Docker image.");
		dockerPullImage.setGroup(DOCKER_GROUP);

		return dockerPullImage;
	}

	private DockerPushImage _addTaskPushDockerImage(
		Project project, WorkspaceExtension workspaceExtension,
		DockerTagImage dockerTagImage) {

		DockerPushImage dockerPushImage = GradleUtil.addTask(
			project, PUSH_DOCKER_IMAGE_TASK_NAME, DockerPushImage.class);

		dockerPushImage.dependsOn(dockerTagImage);

		dockerPushImage.setDescription("Push the Docker iamge.");
		dockerPushImage.setGroup(DOCKER_GROUP);

		SetProperty<String> imagesSetProperty = dockerPushImage.getImages();

		String dockerUserName = workspaceExtension.getDockerUserName();

		if (Objects.nonNull(
				workspaceExtension.getDockerLocalRegistryAddress())) {

			imagesSetProperty.add(
				workspaceExtension.getDockerLocalRegistryAddress() + "/" +
					workspaceExtension.getDockerImageId());
		}
		else if (Objects.nonNull(dockerUserName)) {
			imagesSetProperty.add(
				workspaceExtension.getDockerUserName() + "/" +
					workspaceExtension.getDockerImageId());
		}
		else {
			imagesSetProperty.add(workspaceExtension.getDockerImageId());
		}

		DockerRegistryCredentials dockerRegistryCredentials =
			dockerPushImage.getRegistryCredentials();

		if (Objects.nonNull(dockerUserName)) {
			Property<String> userNameProperty =
				dockerRegistryCredentials.getUsername();

			userNameProperty.set(dockerUserName);
		}

		String dockerUserAccessToken =
			workspaceExtension.getDockerUserAccessToken();

		if (Objects.nonNull(dockerUserAccessToken)) {
			Property<String> passwordProperty =
				dockerRegistryCredentials.getPassword();

			passwordProperty.set(dockerUserAccessToken);
		}

		return dockerPushImage;
	}

	private DockerRemoveContainer _addTaskRemoveDockerContainer(
		Project project, DockerStopContainer stopDockerContainer) {

		DockerRemoveContainer dockerRemoveContainer = GradleUtil.addTask(
			project, REMOVE_DOCKER_CONTAINER_TASK_NAME,
			DockerRemoveContainer.class);

		dockerRemoveContainer.setDescription("Removes the Docker container.");
		dockerRemoveContainer.setGroup(DOCKER_GROUP);

		Property<Boolean> forceProperty = dockerRemoveContainer.getForce();

		forceProperty.set(true);

		Property<Boolean> removeVolumesProperty =
			dockerRemoveContainer.getRemoveVolumes();

		removeVolumesProperty.set(true);

		dockerRemoveContainer.targetContainerId(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return _getDockerContainerId(project);
				}

			});

		dockerRemoveContainer.dependsOn(stopDockerContainer);

		dockerRemoveContainer.onError(
			new Action<Throwable>() {

				@Override
				public void execute(Throwable throwable) {
					Logger logger = project.getLogger();

					if (logger.isWarnEnabled()) {
						logger.warn(
							"No container with ID '" +
								_getDockerContainerId(project) + "' found.");
					}
				}

			});

		return dockerRemoveContainer;
	}

	private void _addTasksDistBundleArchive(
		Project project, Copy distBundleTask,
		WorkspaceExtension workspaceExtension) {

		long buildTime = System.currentTimeMillis();

		Tar distBundleTar = _addTaskDistBundle(
			project, DIST_BUNDLE_TAR_TASK_NAME, Tar.class, distBundleTask,
			workspaceExtension);

		distBundleTar.setDescription(
			"Assembles a Liferay bundle (tar.gz) for the current environment.");
		distBundleTar.setGroup(BUNDLE_GROUP);

		Zip distBundleZip = _addTaskDistBundle(
			project, DIST_BUNDLE_ZIP_TASK_NAME, Zip.class, distBundleTask,
			workspaceExtension);

		distBundleZip.setDescription(
			"Assembles a Liferay bundle (zip) for the current environment.");
		distBundleZip.setGroup(BUNDLE_GROUP);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_configureDistBundleEnvArchive(
						project, distBundleTar,
						workspaceExtension.getEnvironment(),
						workspaceExtension.isBundleDistIncludeMetadata(),
						buildTime);

					_configureDistBundleEnvArchive(
						project, distBundleZip,
						workspaceExtension.getEnvironment(),
						workspaceExtension.isBundleDistIncludeMetadata(),
						buildTime);
				}

			});
	}

	private void _addTasksDistBundleEnvironments(
		Project project, Download downloadBundleTask,
		WorkspaceExtension workspaceExtension,
		Configuration bundleSupportConfiguration,
		Configuration providedModulesConfiguration) {

		long buildTime = System.currentTimeMillis();

		File configsDir = workspaceExtension.getConfigsDir();

		String[] environments = configsDir.list(
			new FilenameFilter() {

				@Override
				public boolean accept(File file, String name) {
					if (!name.startsWith(".") && file.isDirectory() &&
						!name.equals("common") && !name.equals("docker")) {

						return true;
					}

					return false;
				}

			});

		if ((environments == null) || (environments.length == 0)) {
			return;
		}

		Task distBundleTarAll = GradleUtil.addTask(
			project, DIST_BUNDLE_TAR_ALL_TASK_NAME, DefaultTask.class);

		distBundleTarAll.setDescription(
			"Assembles a Liferay bundle (tar.gz) for each environment.");
		distBundleTarAll.setGroup(BUNDLE_GROUP);

		Task distBundleZipAll = GradleUtil.addTask(
			project, DIST_BUNDLE_ZIP_ALL_TASK_NAME, DefaultTask.class);

		distBundleZipAll.setDescription(
			"Assembles a Liferay bundle (zip) for each environment.");
		distBundleZipAll.setGroup(BUNDLE_GROUP);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					for (String environment : environments) {
						Copy distBundleEnvTask = _addTaskDistBundle(
							project, downloadBundleTask,
							DIST_BUNDLE_TASK_NAME +
								StringUtil.capitalize(environment),
							workspaceExtension, bundleSupportConfiguration,
							environment, providedModulesConfiguration);

						Tar distBundleTarTask = _addTaskDistBundle(
							project,
							DIST_BUNDLE_TAR_TASK_NAME +
								StringUtil.capitalize(environment),
							Tar.class, distBundleEnvTask, workspaceExtension);

						Property<String> archiveExtensionProperty =
							distBundleTarTask.getArchiveExtension();

						archiveExtensionProperty.set("tar.gz");

						distBundleTarTask.setCompression(Compression.GZIP);
						distBundleTarTask.setDescription(
							"Assembles a Liferay bundle (tar.gz) for " +
								environment + ".");

						_configureDistBundleEnvArchive(
							project, distBundleTarTask, environment, true,
							buildTime);

						distBundleTarAll.dependsOn(distBundleTarTask);

						Zip distBundleZipTask = _addTaskDistBundle(
							project,
							DIST_BUNDLE_ZIP_TASK_NAME +
								StringUtil.capitalize(environment),
							Zip.class, distBundleEnvTask, workspaceExtension);

						distBundleZipTask.setDescription(
							"Assembles a Liferay bundle (zip) for " +
								environment + ".");

						_configureDistBundleEnvArchive(
							project, distBundleZipTask, environment, true,
							buildTime);

						distBundleZipAll.dependsOn(distBundleZipTask);
					}
				}

			});
	}

	private DockerStartContainer _addTaskStartDockerContainer(
		Project project, DockerCreateContainer dockerCreateContainer) {

		DockerStartContainer dockerStartContainer = GradleUtil.addTask(
			project, START_DOCKER_CONTAINER_TASK_NAME,
			DockerStartContainer.class);

		dockerStartContainer.dependsOn(dockerCreateContainer);

		dockerStartContainer.setDescription("Starts the Docker container.");
		dockerStartContainer.setGroup(DOCKER_GROUP);

		dockerStartContainer.targetContainerId(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return _getDockerContainerId(project);
				}

			});

		return dockerStartContainer;
	}

	private DockerStopContainer _addTaskStopDockerContainer(Project project) {
		DockerStopContainer dockerStopContainer = GradleUtil.addTask(
			project, STOP_DOCKER_CONTAINER_TASK_NAME,
			DockerStopContainer.class);

		dockerStopContainer.setDescription("Stops the Docker container.");
		dockerStopContainer.setGroup(DOCKER_GROUP);

		dockerStopContainer.targetContainerId(
			new Callable<String>() {

				@Override
				public String call() throws Exception {
					return _getDockerContainerId(project);
				}

			});

		dockerStopContainer.onError(
			new Action<Throwable>() {

				@Override
				public void execute(Throwable throwable) {
					Logger logger = project.getLogger();

					if (logger.isWarnEnabled()) {
						logger.warn(
							"No container with ID '" +
								_getDockerContainerId(project) + "' running.");
					}
				}

			});

		return dockerStopContainer;
	}

	private FormatSourceTask _addTaskUpgradeJakarta(Project project) {
		FormatSourceTask formatSourceTask = GradleUtil.addTask(
			project, UPGRADE_JAKARTA_TASK_NAME, FormatSourceTask.class);

		formatSourceTask.onlyIf(_skipIfExecutingParentTaskSpec);
		formatSourceTask.setCheckCategoryNames("JakartaTransform");
		formatSourceTask.setDescription(
			"Runs the Jakarta source code upgrade.");
		formatSourceTask.setGroup("build");
		formatSourceTask.setJavaParserEnabled(false);

		return formatSourceTask;
	}

	private VerifyBundleTask _addTaskVerifyBundle(
		Project project, Download downloadBundleTask,
		WorkspaceExtension workspaceExtension) {

		VerifyBundleTask verifyBundleTask = GradleUtil.addTask(
			project, VERIFY_BUNDLE_TASK_NAME, VerifyBundleTask.class);

		verifyBundleTask.algorithm("SHA-512");
		verifyBundleTask.dependsOn(
			VERIFY_PRODUCT_TASK_NAME, DOWNLOAD_BUNDLE_TASK_NAME);
		verifyBundleTask.setDescription(
			"Verifies the Liferay bundle zip file.");

		verifyBundleTask.onlyIf(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					if (!Objects.equals(
							workspaceExtension.getBundleUrl(),
							ReleaseUtil.getFromReleaseEntry(
								workspaceExtension.getProduct(),
								ReleaseEntry::getBundleURL))) {

						return Objects.nonNull(_bundleCheckSumSHA512);
					}

					return Validator.isNotNull(verifyBundleTask.getChecksum());
				}

			});

		downloadBundleTask.finalizedBy(verifyBundleTask);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project p) {
					if (Validator.isNotNull(
							workspaceExtension.getBundleUrl())) {

						verifyBundleTask.checksum(
							workspaceExtension.getBundleChecksumSHA512());

						TaskOutputs taskOutputs =
							downloadBundleTask.getOutputs();

						FileCollection fileCollection = taskOutputs.getFiles();

						verifyBundleTask.src(fileCollection.getSingleFile());
					}
				}

			});

		return verifyBundleTask;
	}

	private VerifyProductTask _addTaskVerifyProduct(
		Project project, WorkspaceExtension workspaceExtension) {

		VerifyProductTask verifyProductTask = GradleUtil.addTask(
			project, VERIFY_PRODUCT_TASK_NAME, VerifyProductTask.class);

		verifyProductTask.onlyIf(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return Validator.isNotNull(workspaceExtension.getProduct());
				}

			});

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					verifyProductTask.setProduct(
						workspaceExtension.getProduct());
					verifyProductTask.setExtension(workspaceExtension);
				}

			});

		verifyProductTask.setDescription(
			"Verify Liferay Workspace product settings.");

		return verifyProductTask;
	}

	private <T extends AbstractArchiveTask> void _configureDistBundleEnvArchive(
		Project project, T distBundleArchiveTask, String environment,
		boolean bundleDistIncludeMetadata, long buildTime) {

		Property<String> archiveBaseNameProperty =
			distBundleArchiveTask.getArchiveBaseName();

		archiveBaseNameProperty.set(
			project.provider(
				new Callable<String>() {

					@Override
					public String call() throws Exception {
						StringBuilder sb = new StringBuilder();

						sb.append(project.getName());

						if (bundleDistIncludeMetadata) {
							sb.append("-");
							sb.append(environment);
							sb.append("-");
							sb.append(buildTime);
						}

						return sb.toString();
					}

				}));
	}

	private void _configureDownloadTask(
		Project project, Download download,
		WorkspaceExtension workspaceExtension) {

		File destinationDir = workspaceExtension.getBundleCacheDir();

		destinationDir.mkdirs();

		download.dest(destinationDir);

		String bundleURLString = workspaceExtension.getBundleUrl();

		if (Objects.isNull(bundleURLString)) {
			return;
		}

		try {
			if (bundleURLString.startsWith("file:")) {
				URL url = new URL(bundleURLString);

				Path bundleFilePath = Path.path(url.getPath());

				File file = null;

				if (bundleFilePath.isAbsolute()) {
					file = new File(url.getFile());

					file = file.getAbsoluteFile();
				}
				else {
					file = project.file(url.getFile());
				}

				if (Objects.isNull(file)) {
					return;
				}

				URI uri = file.toURI();

				bundleURLString = uri.toASCIIString();
			}
			else {
				bundleURLString = bundleURLString.replace(" ", "%20");
			}

			download.src(bundleURLString);
		}
		catch (MalformedURLException malformedURLException) {
			throw new GradleException(
				malformedURLException.getMessage(), malformedURLException);
		}
	}

	private void _configureFixTargetTomcatConfigs(
		Callable<File> bundleHomeDirObject, Task task) {

		task.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					File bundleHomeDir = GradleUtil.toFile(
						task.getProject(), bundleHomeDirObject);

					File unversionedTomcatDirectory = new File(
						bundleHomeDir, "tomcat");

					if (!unversionedTomcatDirectory.exists()) {
						return;
					}

					File[] files = bundleHomeDir.listFiles(
						(dir, name) -> name.startsWith("tomcat-"));

					if (ArrayUtil.isEmpty(files)) {
						return;
					}

					File versionedTomcatDirectory = files[0];

					Project project = task.getProject();

					WorkResult workResult = project.copy(
						copySpec -> {
							copySpec.setDuplicatesStrategy(
								DuplicatesStrategy.INCLUDE);

							copySpec.from(unversionedTomcatDirectory);
							copySpec.into(versionedTomcatDirectory);

							copySpec.setIncludeEmptyDirs(false);
						});

					if (workResult.getDidWork()) {
						project.delete(unversionedTomcatDirectory);
					}
				}

			});
	}

	private void _configureNpmProject(Project project) {
		project.subprojects(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					project.afterEvaluate(
						new Action<Project>() {

							@Override
							public void execute(Project project) {
								TaskContainer taskContainer =
									project.getTasks();

								taskContainer.withType(
									NpmInstallTask.class,
									new Action<NpmInstallTask>() {

										@Override
										public void execute(
											NpmInstallTask npmInstallTask) {

											NodeExtension nodeExtension =
												GradleUtil.getExtension(
													npmInstallTask.getProject(),
													NodeExtension.class);

											nodeExtension.setUseNpm(true);
										}

									});
							}

						});
				}

			});
	}

	@SuppressWarnings("serial")
	private void _configureTaskCopyBundleFromConfig(
		Project project, CopySpec copy, Callable<File> dir) {

		copy.from(
			dir,
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(CopySpec copySpec) {
					copySpec.exclude("**/.touch");
				}

			});
	}

	private void _configureTaskCopyBundlePreserveTimestamps(Copy copy) {
		final Set<FileCopyDetails> fileCopyDetailsSet = new HashSet<>();

		copy.doLast(
			new Action<Task>() {

				@Override
				public void execute(Task task) {
					Copy copy = (Copy)task;

					Logger logger = copy.getLogger();

					for (FileCopyDetails fileCopyDetails : fileCopyDetailsSet) {
						File file = new File(
							copy.getDestinationDir(),
							fileCopyDetails.getPath());

						if (!file.exists()) {
							logger.error(
								"Unable to set last modified time of {}, it " +
									"has not been copied",
								file);

							return;
						}

						boolean success = file.setLastModified(
							fileCopyDetails.getLastModified());

						if (!success) {
							logger.error(
								"Unable to set last modified time of {}", file);
						}
					}
				}

			});

		copy.eachFile(
			new Action<FileCopyDetails>() {

				@Override
				public void execute(FileCopyDetails fileCopyDetails) {
					fileCopyDetailsSet.add(fileCopyDetails);
				}

			});
	}

	private void _configureTaskDisableUpToDate(Task task) {
		TaskOutputs taskOutputs = task.getOutputs();

		taskOutputs.upToDateWhen(
			new Spec<Task>() {

				@Override
				public boolean isSatisfiedBy(Task task) {
					return false;
				}

			});
	}

	private void _configureWorkspaceExtension(
		Project project, WorkspaceExtension workspaceExtension) {

		String dockerContainerId = workspaceExtension.getDockerContainerId();

		if (dockerContainerId == null) {
			workspaceExtension.setDockerContainerId(
				StringUtil.getDockerSafeName(project.getName()) + "-liferay");
		}

		String dockerImageId = workspaceExtension.getDockerImageId();

		if (dockerImageId == null) {
			String version = _getDockerImageTag(project, workspaceExtension);

			workspaceExtension.setDockerImageId(
				String.format(
					"%s-liferay:%s",
					StringUtil.getDockerSafeName(project.getName()), version));
		}
	}

	private void _createTouchFile(File dir) throws IOException {
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File file = new File(dir, ".touch");

		file.createNewFile();
	}

	private String _getDockerContainerId(Project project) {
		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		return workspaceExtension.getDockerContainerId();
	}

	private String _getDockerImageId(Project project) {
		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		return workspaceExtension.getDockerImageId();
	}

	private String _getDockerImageTag(
		Project project, WorkspaceExtension workspaceExtension) {

		String dockerImageLiferay = workspaceExtension.getDockerImageLiferay();
		Object version = project.getVersion();

		if (Objects.nonNull(dockerImageLiferay) &&
			Objects.equals(version, "unspecified")) {

			int index = dockerImageLiferay.indexOf(":");

			version = dockerImageLiferay.substring(index + 1);
		}

		return version.toString();
	}

	private File _getDownloadFile(Download download) {
		String fileName = String.valueOf((URL)download.getSrc());

		return new File(
			download.getDest(),
			fileName.substring(fileName.lastIndexOf('/') + 1));
	}

	private String _getEnvVarOverride(String string) {
		String[] segments = string.split("\\.");

		StringBuilder sb = new StringBuilder();

		sb.append("LIFERAY_");

		for (int i = 0; i < segments.length; i++) {
			sb.append(segments[i].toUpperCase());

			if (i < (segments.length - 1)) {
				sb.append("_PERIOD_");
			}
		}

		return sb.toString();
	}

	private String _loadTemplate(String name) {
		try (InputStream inputStream =
				RootProjectConfigurator.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to read template " + name, exception);
		}
	}

	private static final boolean _DEFAULT_REPOSITORY_ENABLED = true;

	private static final String _LIFERAY_IMAGE_SETUP_SCRIPT =
		"100_liferay_image_setup.sh";

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

	private String _bundleCheckSumSHA512;
	private boolean _defaultRepositoryEnabled;

}