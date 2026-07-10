/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.configurator;

import com.bmuschko.gradle.docker.DockerRegistryCredentials;
import com.bmuschko.gradle.docker.DockerRemoteApiPlugin;
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage;
import com.bmuschko.gradle.docker.tasks.image.DockerRemoveImage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.lang.builder.BuildLangTask;
import com.liferay.gradle.plugins.lang.builder.LangBuilderPlugin;
import com.liferay.gradle.plugins.node.task.ExecuteNodeTask;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.WorkspacePlugin;
import com.liferay.gradle.plugins.workspace.internal.client.extension.ClientExtension;
import com.liferay.gradle.plugins.workspace.internal.client.extension.NodeBuildConfigurer;
import com.liferay.gradle.plugins.workspace.internal.client.extension.ThemeCSSTypeConfigurer;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.JsonNodeUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.gradle.plugins.workspace.internal.util.copy.HashifyAction;
import com.liferay.gradle.plugins.workspace.task.CreateClientExtensionConfigTask;
import com.liferay.gradle.plugins.workspace.task.WriteLanguageBatchEngineDataTask;
import com.liferay.gradle.util.ArrayUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.dsl.ArtifactHandler;
import org.gradle.api.execution.TaskExecutionGraph;
import org.gradle.api.execution.TaskExecutionListener;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.PluginManager;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Delete;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.TaskState;
import org.gradle.api.tasks.bundling.Zip;
import org.gradle.language.base.plugins.LifecycleBasePlugin;
import org.gradle.process.ProcessForkOptions;

/**
 * @author Gregory Amerson
 */
public class ClientExtensionProjectConfigurator
	extends BaseProjectConfigurator {

	public static final String ASSEMBLE_CLIENT_EXTENSION_TASK_NAME =
		"assembleClientExtension";

	public static final String BUILD_CLIENT_EXTENSION_ZIP_TASK_NAME =
		"buildClientExtensionZip";

	public static final String BUILD_SITE_INITIALIZER_ZIP_TASK_NAME =
		"buildSiteInitializerZip";

	public static final String CREATE_CLIENT_EXTENSION_CONFIG_TASK_NAME =
		"createClientExtensionConfig";

	public static final String VALIDATE_CLIENT_EXTENSION_IDS_TASK_NAME =
		"validateClientExtensionIds";

	public static final String VALIDATE_CLIENT_EXTENSIONS_TASK_NAME =
		"validateClientExtensions";

	public static final String WRITE_LANGUAGE_BATCH_ENGINE_DATA_TASK_NAME =
		"writeLanguageBatchEngineData";

	public static String getClientExtensionBuildDir(Project project) {
		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		return StringUtil.suffixIfNotBlank(
			"liferay-client-extension-build",
			workspaceExtension.getVirtualInstanceId());
	}

	public ClientExtensionProjectConfigurator(Settings settings) {
		super(settings);

		_defaultRepositoryEnabled = GradleUtil.getProperty(
			settings,
			WorkspacePlugin.PROPERTY_PREFIX + NAME +
				".default.repository.enabled",
			_DEFAULT_REPOSITORY_ENABLED);
	}

	@Override
	public void apply(Project project) {
		WorkspaceExtension workspaceExtension = GradleUtil.getExtension(
			(ExtensionAware)project.getGradle(), WorkspaceExtension.class);

		TaskProvider<CreateClientExtensionConfigTask>
			createClientExtensionConfigTaskProvider =
				GradleUtil.addTaskProvider(
					project, CREATE_CLIENT_EXTENSION_CONFIG_TASK_NAME,
					CreateClientExtensionConfigTask.class);

		TaskProvider<Copy> assembleClientExtensionTaskProvider =
			GradleUtil.addTaskProvider(
				project, ASSEMBLE_CLIENT_EXTENSION_TASK_NAME, Copy.class);

		TaskProvider<Zip> buildClientExtensionZipTaskProvider =
			GradleUtil.addTaskProvider(
				project, BUILD_CLIENT_EXTENSION_ZIP_TASK_NAME, Zip.class);

		TaskProvider<Zip> buildSiteInitializerZipTaskProvider =
			GradleUtil.addTaskProvider(
				project, BUILD_SITE_INITIALIZER_ZIP_TASK_NAME, Zip.class);

		TaskProvider<Task> validateClientExtensionIdsTaskProvider =
			GradleUtil.addTaskProvider(
				project, VALIDATE_CLIENT_EXTENSION_IDS_TASK_NAME, Task.class);

		TaskProvider<Task> validateClientExtensionTaskProvider =
			GradleUtil.addTaskProvider(
				project, VALIDATE_CLIENT_EXTENSIONS_TASK_NAME, Task.class);

		_baseConfigureClientExtensionProject(
			project, assembleClientExtensionTaskProvider,
			buildClientExtensionZipTaskProvider,
			buildSiteInitializerZipTaskProvider,
			createClientExtensionConfigTaskProvider,
			validateClientExtensionIdsTaskProvider,
			validateClientExtensionTaskProvider, workspaceExtension);

		AtomicBoolean hasThemeCSSClientExtension = new AtomicBoolean(false);

		Map<String, JsonNode> profileJsonNodes =
			_configureClientExtensionJsonNodes(
				project, assembleClientExtensionTaskProvider,
				createClientExtensionConfigTaskProvider,
				validateClientExtensionIdsTaskProvider,
				validateClientExtensionTaskProvider);

		for (Map.Entry<String, JsonNode> profileJsonNodeEntry :
				profileJsonNodes.entrySet()) {

			String profileName = profileJsonNodeEntry.getKey();

			JsonNode jsonNode = profileJsonNodeEntry.getValue();

			Iterator<Map.Entry<String, JsonNode>> iterator = jsonNode.fields();

			iterator.forEachRemaining(
				entry -> {
					String fieldName = entry.getKey();

					if (Objects.equals(fieldName, "runtime")) {
						return;
					}

					JsonNode fieldJsonNode = entry.getValue();

					if (Objects.equals(fieldName, "assemble")) {
						_configureAssembleClientExtensionTask(
							project, assembleClientExtensionTaskProvider,
							fieldJsonNode, profileName);

						return;
					}

					try {
						ClientExtension clientExtension =
							_yamlObjectMapper.treeToValue(
								(ObjectNode)fieldJsonNode,
								ClientExtension.class);

						clientExtension.id = fieldName;

						if (Validator.isNull(clientExtension.type)) {
							clientExtension.type = fieldName;
						}

						clientExtension.projectId =
							StringUtil.toAlphaNumericLowerCase(
								project.getName());
						clientExtension.projectName = project.getName();

						validateClientExtensionTaskProvider.configure(
							task -> task.doLast(
								new Action<Task>() {

									@Override
									public void execute(Task task1) {
										_validateClientExtension(
											clientExtension, project);
									}

								}));

						_registerClientExtensionId(project, clientExtension.id);

						createClientExtensionConfigTaskProvider.configure(
							createClientExtensionConfigTask -> {
								if (!_isActiveProfile(project, profileName)) {
									return;
								}

								createClientExtensionConfigTask.
									setVirtualInstanceId(
										workspaceExtension.
											getVirtualInstanceId());

								createClientExtensionConfigTask.
									addClientExtension(clientExtension);
							});

						if (clientExtension.type.equals("configuration")) {
							assembleClientExtensionTaskProvider.configure(
								copy -> {
									if (!_isActiveProfile(
											project, profileName)) {

										return;
									}

									copy.from(
										"src",
										copySpec -> copySpec.include("**/*"));
								});
						}
						else if (clientExtension.type.equals("themeCSS")) {
							hasThemeCSSClientExtension.set(true);
						}

						if (clientExtension.type.equals("siteInitializer")) {
							buildSiteInitializerZipTaskProvider.configure(
								zip -> {
									zip.from(project.file("site-initializer"));
									zip.into("site-initializer");
								});
							createClientExtensionConfigTaskProvider.configure(
								task -> task.dependsOn(
									BUILD_SITE_INITIALIZER_ZIP_TASK_NAME));
						}
					}
					catch (JsonProcessingException jsonProcessingException) {
						throw new GradleException(
							"Unable to parse client extension " + fieldName,
							jsonProcessingException);
					}
				});
		}

		if (hasThemeCSSClientExtension.get()) {
			_themeCSSTypeConfigurer.apply(
				project, assembleClientExtensionTaskProvider);
		}

		_nodeBuildConfigurer.apply(
			project, assembleClientExtensionTaskProvider);

		_addDockerTasks(
			project, assembleClientExtensionTaskProvider,
			createClientExtensionConfigTaskProvider, workspaceExtension);

		_configureLiferayRoutes(project, workspaceExtension);

		if (_isLanguageProject(project)) {
			GradleUtil.applyPlugin(project, LangBuilderPlugin.class);

			_configureLanguageProject(project);
		}

		_configureSpringBootPlugin(project);
	}

	@Override
	public String getName() {
		return "client-extension";
	}

	public boolean isDefaultRepositoryEnabled() {
		return _defaultRepositoryEnabled;
	}

	@Override
	protected Iterable<File> doGetProjectDirs(File rootDir) throws Exception {
		final Set<File> projectDirs = new HashSet<>();

		Files.walkFileTree(
			rootDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dirPath.getFileName());

					if (isExcludedDirName(dirName)) {
						return FileVisitResult.SKIP_SUBTREE;
					}

					if (_isLanguageProject(rootDir, dirPath.toFile())) {
						projectDirs.add(dirPath.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					Path clientExtensionPath = dirPath.resolve(
						_CLIENT_EXTENSION_YAML);

					if (Files.exists(clientExtensionPath) &&
						!Objects.equals(dirPath, rootDir.toPath())) {

						projectDirs.add(dirPath.toFile());

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

		return projectDirs;
	}

	protected static final String NAME = "client.extension";

	private void _addDockerTasks(
		Project project, TaskProvider<Copy> assembleClientExtensionTaskProvider,
		TaskProvider<CreateClientExtensionConfigTask>
			createClientExtensionConfigTaskProvider,
		WorkspaceExtension workspaceExtension) {

		DockerBuildImage dockerBuildImage = GradleUtil.addTask(
			project, RootProjectConfigurator.BUILD_DOCKER_IMAGE_TASK_NAME,
			DockerBuildImage.class);

		dockerBuildImage.setDescription(
			"Builds a child Docker image from the Liferay base image with " +
				"all configs deployed.");
		dockerBuildImage.setGroup(RootProjectConfigurator.DOCKER_GROUP);

		dockerBuildImage.dependsOn(createClientExtensionConfigTaskProvider);

		DirectoryProperty inputDirectoryProperty =
			dockerBuildImage.getInputDir();

		assembleClientExtensionTaskProvider.configure(
			copy -> inputDirectoryProperty.set(copy.getDestinationDir()));

		Property<Boolean> pullProperty = dockerBuildImage.getPull();

		pullProperty.set(workspaceExtension.getDockerPullPolicy());

		if (Objects.nonNull(
				workspaceExtension.getDockerLocalRegistryAddress())) {

			DockerRegistryCredentials dockerRegistryCredentials =
				dockerBuildImage.getRegistryCredentials();

			String dockerUserAccessToken =
				workspaceExtension.getDockerUserAccessToken();

			if (Objects.nonNull(dockerUserAccessToken)) {
				Property<String> passwordProperty =
					dockerRegistryCredentials.getPassword();

				passwordProperty.set(dockerUserAccessToken);
			}

			String dockerUserName = workspaceExtension.getDockerUserName();

			if (Objects.nonNull(dockerUserName)) {
				Property<String> userNameProperty =
					dockerRegistryCredentials.getUsername();

				userNameProperty.set(dockerUserName);
			}
		}

		DockerRemoveImage dockerRemoveImage = GradleUtil.addTask(
			project, RootProjectConfigurator.CLEAN_DOCKER_IMAGE_TASK_NAME,
			DockerRemoveImage.class);

		dockerRemoveImage.setDescription("Removes the Docker image.");
		dockerRemoveImage.setGroup(RootProjectConfigurator.DOCKER_GROUP);

		Property<Boolean> forceProperty = dockerRemoveImage.getForce();

		forceProperty.set(true);

		String dockerImageId = _getDockerImageId(project);

		SetProperty<String> setProperty = dockerBuildImage.getImages();

		setProperty.add(dockerImageId);

		Property<String> property = dockerRemoveImage.getImageId();

		property.set(dockerImageId);

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

		Task cleanTask = GradleUtil.getTask(
			project, LifecycleBasePlugin.CLEAN_TASK_NAME);

		cleanTask.dependsOn(dockerRemoveImage);
	}

	private TaskProvider<Zip> _baseConfigureClientExtensionProject(
		Project project, TaskProvider<Copy> assembleClientExtensionTaskProvider,
		TaskProvider<Zip> buildClientExtensionZipTaskProvider,
		TaskProvider<Zip> buildSiteInitializerZipTaskProvider,
		TaskProvider<CreateClientExtensionConfigTask>
			createClientExtensionConfigTaskProvider,
		TaskProvider<Task> validateClientExtensionIdsTaskProvider,
		TaskProvider<Task> validateClientExtensionTaskProvider,
		WorkspaceExtension workspaceExtension) {

		if (isDefaultRepositoryEnabled()) {
			GradleUtil.addDefaultRepositories(project);
		}

		GradleUtil.applyPlugin(project, BasePlugin.class);
		GradleUtil.applyPlugin(project, DockerRemoteApiPlugin.class);
		GradleUtil.applyPlugin(project, LiferayBasePlugin.class);

		LiferayExtension liferayExtension = GradleUtil.getExtension(
			project, LiferayExtension.class);

		configureLiferay(project, workspaceExtension);

		_configureLiferayExtension(project, liferayExtension);

		_configureConfigurationDefault(project);
		_configureTaskCheck(project);
		_configureTaskClean(project);
		_configureTaskDeploy(project, buildClientExtensionZipTaskProvider);

		_configureClientExtensionTasks(
			project, assembleClientExtensionTaskProvider,
			buildClientExtensionZipTaskProvider,
			buildSiteInitializerZipTaskProvider,
			createClientExtensionConfigTaskProvider,
			validateClientExtensionIdsTaskProvider,
			validateClientExtensionTaskProvider, workspaceExtension);

		addTaskDockerDeploy(
			project, buildClientExtensionZipTaskProvider,
			new File(workspaceExtension.getDockerDir(), "client-extensions"));

		_configureArtifacts(project, buildClientExtensionZipTaskProvider);
		_configureRootTaskDistBundle(
			project, buildClientExtensionZipTaskProvider);

		return buildClientExtensionZipTaskProvider;
	}

	private void _configureArtifacts(
		Project project,
		TaskProvider<Zip> buildClientExtensionZipTaskProvider) {

		ArtifactHandler artifacts = project.getArtifacts();

		artifacts.add(
			Dependency.ARCHIVES_CONFIGURATION,
			buildClientExtensionZipTaskProvider);
	}

	private void _configureAssembleClientExtensionTask(
		Project project, TaskProvider<Copy> assembleClientExtensionTaskProvider,
		JsonNode assembleJsonNode, String profileName) {

		if (assembleJsonNode.isNull()) {
			return;
		}

		ArrayNode assembleArrayNode = (ArrayNode)assembleJsonNode;

		if (assembleArrayNode.isEmpty()) {
			return;
		}

		assembleClientExtensionTaskProvider.configure(
			assembleClientExtensionCopy -> {
				if (!_isActiveProfile(project, profileName)) {
					return;
				}

				assembleArrayNode.forEach(
					copyJsonNode -> {
						JsonNode fromJsonNode = copyJsonNode.get("from");
						JsonNode fromTaskJsonNode = copyJsonNode.get(
							"fromTask");
						JsonNode hashifyJsonNode = copyJsonNode.get("hashify");
						JsonNode includeJsonNode = copyJsonNode.get("include");
						JsonNode intoJsonNode = copyJsonNode.get("into");

						Object fromPath = null;

						if (fromTaskJsonNode != null) {
							TaskContainer taskContainer = project.getTasks();

							fromPath = taskContainer.findByName(
								fromTaskJsonNode.asText());
						}

						if ((fromPath == null) && (fromJsonNode != null)) {
							fromPath = fromJsonNode.asText();
						}

						assembleClientExtensionCopy.from(
							(fromPath != null) ? fromPath : ".",
							copySpec -> {
								if (hashifyJsonNode != null) {
									copySpec.eachFile(
										new HashifyAction(
											hashifyJsonNode.asText()));
								}

								copySpec.exclude(
									"**/" +
										getClientExtensionBuildDir(project));

								if (includeJsonNode instanceof ArrayNode) {
									ArrayNode arrayNode =
										(ArrayNode)includeJsonNode;

									arrayNode.forEach(
										include -> copySpec.include(
											include.asText()));
								}
								else {
									if (includeJsonNode != null) {
										copySpec.include(
											includeJsonNode.asText());
									}
									else {
										copySpec.include("**/*");
									}
								}

								if (intoJsonNode != null) {
									copySpec.into(intoJsonNode.asText());
								}

								copySpec.setIncludeEmptyDirs(false);
							});
					});
			});
	}

	private Map<String, JsonNode> _configureClientExtensionJsonNodes(
		Project project, TaskProvider<?>... taskProviders) {

		if (_isLanguageProject(project)) {
			InputStream inputStream =
				WriteLanguageBatchEngineDataTask.class.getResourceAsStream(
					"dependencies/templates/language/client-extension.yaml");

			try {
				return Collections.singletonMap(
					"default", _yamlObjectMapper.readTree(inputStream));
			}
			catch (IOException ioException) {
				throw new UncheckedIOException(ioException);
			}
		}

		File clientExtensionYamlFile = project.file(_CLIENT_EXTENSION_YAML);

		if (!clientExtensionYamlFile.exists()) {
			return Collections.emptyMap();
		}

		Map<String, JsonNode> profileJsonNodes = new HashMap<>();

		JsonNode rootJsonNode = _getJsonNode(clientExtensionYamlFile);

		profileJsonNodes.put("default", rootJsonNode);

		File parentFile = clientExtensionYamlFile.getParentFile();

		for (File file : Objects.requireNonNull(parentFile.listFiles())) {
			Matcher matcher = _overrideClientExtensionYamlPattern.matcher(
				file.getName());

			if (!matcher.find()) {
				continue;
			}

			String profileName = matcher.group(1);

			if (Objects.equals(profileName, "default")) {
				Logger logger = project.getLogger();

				if (logger.isWarnEnabled()) {
					logger.warn(
						"Ignoring client-extension.default.yaml because " +
							"\"default\" is a reserved profile name.");
				}

				continue;
			}

			_configureDeployProfileTask(
				project, clientExtensionYamlFile, file, profileName);

			JsonNode jsonNode = rootJsonNode.deepCopy();

			JsonNodeUtil.overrideJsonNodeValues(jsonNode, _getJsonNode(file));

			profileJsonNodes.put(profileName, jsonNode);

			for (TaskProvider<?> taskProvider : taskProviders) {
				taskProvider.configure(
					task -> {
						if (_isActiveProfile(project, profileName)) {
							TaskInputs taskInputs = task.getInputs();

							taskInputs.file(file);
						}
					});
			}
		}

		return profileJsonNodes;
	}

	private void _configureClientExtensionTasks(
		Project project, TaskProvider<Copy> assembleClientExtensionTaskProvider,
		TaskProvider<Zip> buildClientExtensionZipTaskProvider,
		TaskProvider<Zip> buildSiteInitializerZipTaskProvider,
		TaskProvider<CreateClientExtensionConfigTask>
			createClientExtensionConfigTaskProvider,
		TaskProvider<Task> validateClientExtensionIdsTaskProvider,
		TaskProvider<Task> validateClientExtensionTaskProvider,
		WorkspaceExtension workspaceExtension) {

		File clientExtensionYamlFile = project.file(_CLIENT_EXTENSION_YAML);

		createClientExtensionConfigTaskProvider.configure(
			createClientExtensionConfigTask -> {
				createClientExtensionConfigTask.dependsOn(
					ASSEMBLE_CLIENT_EXTENSION_TASK_NAME,
					VALIDATE_CLIENT_EXTENSION_IDS_TASK_NAME,
					VALIDATE_CLIENT_EXTENSIONS_TASK_NAME);

				if (clientExtensionYamlFile.exists()) {
					TaskInputs taskInputs =
						createClientExtensionConfigTask.getInputs();

					taskInputs.file(clientExtensionYamlFile);
				}
			});

		ProjectLayout projectLayout = project.getLayout();

		DirectoryProperty buildDirectoryProperty =
			projectLayout.getBuildDirectory();

		Provider<Directory> clientExtensionBuildDirProvider =
			buildDirectoryProperty.dir(getClientExtensionBuildDir(project));

		assembleClientExtensionTaskProvider.configure(
			copy -> {
				if (clientExtensionYamlFile.exists()) {
					TaskInputs taskInputs = copy.getInputs();

					taskInputs.file(clientExtensionYamlFile);
				}

				copy.into(clientExtensionBuildDirProvider);

				copy.doFirst(
					new Action<Task>() {

						@Override
						public void execute(Task task) {
							Copy copy1 = (Copy)task;

							project.delete(copy1.getDestinationDir());
						}

					});
				copy.doLast(
					new Action<Task>() {

						@Override
						public void execute(Task task) {
							Copy copy1 = (Copy)task;

							if (!copy1.getDidWork()) {
								return;
							}

							CreateClientExtensionConfigTask
								createClientExtensionConfigTask =
									createClientExtensionConfigTaskProvider.
										get();

							TaskOutputs taskOutputs =
								createClientExtensionConfigTask.getOutputs();

							taskOutputs.upToDateWhen(task1 -> false);
						}

					});
			});

		buildClientExtensionZipTaskProvider.configure(
			zip -> {
				zip.dependsOn(CREATE_CLIENT_EXTENSION_CONFIG_TASK_NAME);

				DirectoryProperty destinationDirectoryProperty =
					zip.getDestinationDirectory();

				destinationDirectoryProperty.set(
					new File(project.getProjectDir(), "dist"));

				Property<String> archiveBaseNameProperty =
					zip.getArchiveBaseName();

				archiveBaseNameProperty.set(
					project.provider(
						new Callable<String>() {

							@Override
							public String call() throws Exception {
								return StringUtil.suffixIfNotBlank(
									project.getName(),
									workspaceExtension.getVirtualInstanceId());
							}

						}));

				Property<String> archiveVersion = zip.getArchiveVersion();

				archiveVersion.set("");

				zip.from(clientExtensionBuildDirProvider);
				zip.include("**/*");
			});

		validateClientExtensionIdsTaskProvider.configure(
			task -> {
				task.setDescription(
					"Validates that this project's client extension IDs are " +
						"unique among all projects.");
				task.setGroup(LifecycleBasePlugin.VERIFICATION_GROUP);

				if (clientExtensionYamlFile.exists()) {
					TaskInputs taskInputs = task.getInputs();

					taskInputs.file(clientExtensionYamlFile);
				}

				TaskOutputs taskOutputs = task.getOutputs();

				taskOutputs.upToDateWhen(task1 -> true);

				task.doFirst(
					new Action<Task>() {

						@Override
						public void execute(
							Task validateClientExtensionIdsTask1) {

							StringBuilder sb = new StringBuilder();

							File rootDir = project.getRootDir();

							Path rootDirPath = rootDir.toPath();

							for (Map.Entry<String, Set<Project>> entry :
									_clientExtensionIds.entrySet()) {

								Set<Project> projects = entry.getValue();

								if ((projects.size() > 1) &&
									projects.contains(project)) {

									sb.append(
										"Duplicate client extension ID \"");
									sb.append(entry.getKey());
									sb.append("\" found in these projects:\n");

									for (Project curProject : projects) {
										File projectDir =
											curProject.getProjectDir();

										sb.append(
											rootDirPath.relativize(
												projectDir.toPath()));

										sb.append(StringUtil.COMMA_AND_SPACE);
									}

									sb.append(StringUtil.COMMA_AND_SPACE);
								}
							}

							if (sb.length() > 0) {
								throw new GradleException(sb.toString());
							}
						}

					});
			});

		validateClientExtensionTaskProvider.configure(
			task -> {
				if (clientExtensionYamlFile.exists()) {
					TaskInputs taskInputs = task.getInputs();

					taskInputs.file(clientExtensionYamlFile);
				}

				TaskOutputs taskOutputs = task.getOutputs();

				taskOutputs.upToDateWhen(task1 -> true);
			});

		buildSiteInitializerZipTaskProvider.configure(
			zip -> {
				DirectoryProperty destinationDirectoryProperty =
					zip.getDestinationDirectory();

				Provider<Directory> siteInitializerBuildDirProvider =
					clientExtensionBuildDirProvider.map(
						directory -> directory.dir("site-initializer"));

				destinationDirectoryProperty.set(
					siteInitializerBuildDirProvider);

				Property<String> archiveBaseNameProperty =
					zip.getArchiveBaseName();

				archiveBaseNameProperty.set("site-initializer");
			});
	}

	private void _configureConfigurationDefault(Project project) {
		Configuration defaultConfiguration = GradleUtil.getConfiguration(
			project, Dependency.DEFAULT_CONFIGURATION);

		Configuration archivesConfiguration = GradleUtil.getConfiguration(
			project, Dependency.ARCHIVES_CONFIGURATION);

		defaultConfiguration.extendsFrom(archivesConfiguration);
	}

	private void _configureDeployProfileTask(
		Project project, File clientExtensionYamlFile,
		File overrideClientExtensionYamlFile, String profileName) {

		TaskProvider<Task> deployProfileTaskProvider =
			GradleUtil.addTaskProvider(
				project, "deploy" + StringUtil.capitalize(profileName),
				Task.class);

		deployProfileTaskProvider.configure(
			new Action<Task>() {

				@Override
				public void execute(Task deployProfileTask) {
					GradleUtil.setProperty(project, "profileName", profileName);

					deployProfileTask.finalizedBy("deploy");
					deployProfileTask.setDescription(
						"Assembles the project and deploys it to Liferay " +
							"with the \"" + profileName + "\" client " +
								"extension profile.");
					deployProfileTask.setGroup(BasePlugin.BUILD_GROUP);

					TaskInputs taskInputs = deployProfileTask.getInputs();

					taskInputs.files(
						clientExtensionYamlFile,
						overrideClientExtensionYamlFile);
				}

			});
	}

	private void _configureLanguageProject(Project project) {
		TaskProvider<BuildLangTask> buildLangTaskProvider =
			GradleUtil.getTaskProvider(
				project, LangBuilderPlugin.BUILD_LANG_TASK_NAME,
				BuildLangTask.class);

		buildLangTaskProvider.configure(
			task -> {
				task.setLangDir(project.getProjectDir());

				Project rootProject = project.getRootProject();

				task.setWorkingDir(rootProject.getProjectDir());
			});

		TaskProvider<WriteLanguageBatchEngineDataTask>
			writeLanguageBatchEngineDataTaskProvider =
				GradleUtil.addTaskProvider(
					project, WRITE_LANGUAGE_BATCH_ENGINE_DATA_TASK_NAME,
					WriteLanguageBatchEngineDataTask.class);

		writeLanguageBatchEngineDataTaskProvider.configure(
			task -> task.dependsOn(buildLangTaskProvider));
	}

	private void _configureLiferayExtension(
		Project project, LiferayExtension liferayExtension) {

		liferayExtension.setDeployDir(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File dir = new File(
						liferayExtension.getAppServerParentDir(),
						"osgi/client-extensions");

					dir.mkdirs();

					return GradleUtil.getProperty(
						project, "auto.deploy.dir", dir);
				}

			});
	}

	private void _configureLiferayRoutes(
		Project project, WorkspaceExtension workspaceExtension) {

		Map<String, String> environmentVariables = new HashMap<>();

		String liferayVirtualInstanceId =
			workspaceExtension.getVirtualInstanceId();

		if (StringUtil.isBlank(liferayVirtualInstanceId)) {
			liferayVirtualInstanceId = "default";
		}

		environmentVariables.put(
			_ENV_LIFERAY_ROUTES_CLIENT_EXTENSION,
			String.format(
				"%s/routes/%s/%s", workspaceExtension.getHomeDir(),
				liferayVirtualInstanceId, project.getName()));
		environmentVariables.put(
			_ENV_LIFERAY_ROUTES_DXP,
			String.format(
				"%s/routes/%s/dxp", workspaceExtension.getHomeDir(),
				liferayVirtualInstanceId));

		Gradle gradle = project.getGradle();

		TaskExecutionGraph taskGraph = gradle.getTaskGraph();

		taskGraph.addTaskExecutionListener(
			new TaskExecutionListener() {

				@Override
				public void afterExecute(Task task, TaskState taskState) {
				}

				@Override
				public void beforeExecute(Task task) {
					if (Objects.equals(project, task.getProject()) &&
						(task instanceof ExecuteNodeTask ||
						 task instanceof ProcessForkOptions)) {

						if (task instanceof ProcessForkOptions) {
							ProcessForkOptions processForkOptions =
								(ProcessForkOptions)task;

							processForkOptions.environment(
								environmentVariables);
						}
						else {
							ExecuteNodeTask executeNodeTask =
								(ExecuteNodeTask)task;

							executeNodeTask.environment(environmentVariables);
						}

						Logger logger = task.getLogger();

						if (logger.isInfoEnabled()) {
							logger.info(
								StringUtil.concat(
									"Injecting Liferay routes configuration ",
									"paths as environment variables into the ",
									"process invoked by the task ",
									task.getPath()));

							for (Map.Entry<String, String> entry :
									environmentVariables.entrySet()) {

								logger.info(
									"{}: {}", entry.getKey(), entry.getValue());
							}
						}
					}
				}

			});
	}

	private void _configureRootTaskDistBundle(
		Project project,
		TaskProvider<Zip> buildClientExtensionZipTaskProvider) {

		Task assembleTask = GradleUtil.getTask(
			project, BasePlugin.ASSEMBLE_TASK_NAME);

		Copy copy = (Copy)GradleUtil.getTask(
			project.getRootProject(),
			RootProjectConfigurator.DIST_BUNDLE_TASK_NAME);

		copy.dependsOn(assembleTask);

		copy.into(
			"osgi/client-extensions",
			new Closure<Void>(project) {

				public void doCall(CopySpec copySpec) {
					Project project = assembleTask.getProject();

					ConfigurableFileCollection configurableFileCollection =
						project.files(buildClientExtensionZipTaskProvider);

					configurableFileCollection.builtBy(assembleTask);

					copySpec.from(buildClientExtensionZipTaskProvider);
				}

			});
	}

	private void _configureSpringBootPlugin(Project project) {
		PluginManager pluginManager = project.getPluginManager();

		Map<String, String> environmentMap = Collections.singletonMap(
			"JDK_JAVA_OPTIONS",
			StringUtil.concat(
				"--add-opens=java.base/java.lang=ALL-UNNAMED ",
				"--add-opens=java.base/java.lang.invoke=ALL-UNNAMED ",
				"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED ",
				"--add-opens=java.base/java.net=ALL-UNNAMED ",
				"--add-opens=java.base/sun.net.www.protocol.http=ALL-UNNAMED ",
				"--add-opens=java.base/sun.net.www.protocol.https=ALL-UNNAMED ",
				"--add-opens=java.base/sun.util.calendar=ALL-UNNAMED ",
				"--add-opens=jdk.zipfs/jdk.nio.zipfs=ALL-UNNAMED"));

		pluginManager.withPlugin(
			"org.springframework.boot",
			appliedPlugin -> {
				TaskContainer taskContainer = project.getTasks();

				taskContainer.withType(
					JavaExec.class,
					javaExecTask -> {
						javaExecTask.environment(environmentMap);

						Logger logger = javaExecTask.getLogger();

						if (!logger.isInfoEnabled()) {
							return;
						}

						logger.info(
							StringUtil.concat(
								"Injected the environment variable ",
								" \"JDK_JAVA_OPTIONS\" into the process ",
								"invoked by the task {}"),
							javaExecTask.getPath());

						for (Map.Entry<String, String> entry :
								environmentMap.entrySet()) {

							logger.info(
								"{}: {}", entry.getKey(), entry.getValue());
						}
					});
			});
	}

	private void _configureTaskCheck(Project project) {
		Task checkTask = GradleUtil.getTask(
			project, LifecycleBasePlugin.CHECK_TASK_NAME);

		checkTask.dependsOn(VALIDATE_CLIENT_EXTENSION_IDS_TASK_NAME);
	}

	private void _configureTaskClean(Project project) {
		Delete delete = (Delete)GradleUtil.getTask(
			project, BasePlugin.CLEAN_TASK_NAME);

		delete.delete("build", "dist");
	}

	private void _configureTaskDeploy(
		Project project,
		TaskProvider<Zip> buildClientExtensionZipTaskProvider) {

		Copy copy = (Copy)GradleUtil.getTask(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME);

		copy.from(buildClientExtensionZipTaskProvider);
	}

	private String _getDockerImageId(Project project) {
		String propertyName = "imageId";

		if (project.hasProperty(propertyName)) {
			Object property = project.property(propertyName);

			return property.toString();
		}

		return project.getName() + ":latest";
	}

	private JsonNode _getJsonNode(File file) {
		if (!file.exists()) {
			return _yamlObjectMapper.createObjectNode();
		}

		try {
			return _yamlObjectMapper.readTree(file);
		}
		catch (IOException ioException) {
			throw new GradleException(
				String.format("Unable to parse %s.", file.getName()),
				ioException);
		}
	}

	private boolean _isActiveProfile(Project project, String profileName) {
		return Objects.equals(
			profileName,
			GradleUtil.getProperty(project, "profileName", "default"));
	}

	private boolean _isLanguageProject(File rootDir, File projectDir) {
		Path dirPath = projectDir.toPath();

		if (Objects.equals(rootDir.toPath(), dirPath.getParent()) &&
			dirPath.endsWith(Paths.get("language")) &&
			Files.exists(
				Paths.get(dirPath.toString(), "Language.properties"))) {

			return true;
		}

		return false;
	}

	private boolean _isLanguageProject(Project project) {
		return _isLanguageProject(
			project.getRootDir(), project.getProjectDir());
	}

	private void _registerClientExtensionId(
		Project project, String clientExtensionId) {

		_clientExtensionIds.compute(
			clientExtensionId,
			(key, value) -> {
				if (value == null) {
					value = new HashSet<>();
				}

				value.add(project);

				return value;
			});
	}

	private void _validateClientExtension(
		ClientExtension clientExtension, Project project) {

		if (Objects.equals(clientExtension.type, "batch")) {
			if (!_isLanguageProject(project)) {
				_validateRequiredDirectory(clientExtension, project, "batch");
			}

			_validateRequiredTypeSettingsKeys(
				clientExtension, "oAuthApplicationHeadlessServer");
		}
		else if (Objects.equals(clientExtension.type, "globalCSS")) {
			_validateTypeSettingsValues(
				clientExtension, "scope", "company", "layout");
		}
		else if (Objects.equals(clientExtension.type, "globalJS")) {
			_validateGlobalJSScriptElementAttributes(clientExtension);
			_validateTypeSettingsValues(
				clientExtension, "scope", "company", "layout");
			_validateTypeSettingsValues(
				clientExtension, "scriptLocation", "bottom", "head");
		}
		else if (Objects.equals(clientExtension.type, "instanceSettings")) {
			_validateRequiredTypeSettingsKeys(clientExtension, "pid");
		}
		else if (Objects.equals(clientExtension.type, "siteInitializer")) {
			_validateRequiredDirectory(
				clientExtension, project, "site-initializer");
			_validateRequiredTypeSettingsKeys(
				clientExtension, "oAuthApplicationHeadlessServer",
				"siteExternalReferenceCode", "siteName");
			_validateTypeSettingsValues(
				clientExtension, "builtInTemplateType", "site-initializer",
				"site-template");
			_validateTypeSettingsValues(
				clientExtension, "membershipType", "open", "private",
				"restricted");
			_validateSiteInitializerSiteNameI18n(clientExtension);
		}
		else if (Objects.equals(clientExtension.type, "themeCSS")) {
			_validateTypeSettingsValues(
				clientExtension, "scope", "controlPanel", "layout");
		}
	}

	private void _validateGlobalJSScriptElementAttributes(
		ClientExtension clientExtension) {

		Map<String, Object> typeSettings = clientExtension.typeSettings;

		if (!typeSettings.containsKey("scriptElementAttributes")) {
			return;
		}

		Object scriptElementAttributes = typeSettings.get(
			"scriptElementAttributes");

		if (!(scriptElementAttributes instanceof Map)) {
			throw new GradleException(
				"The property 'scriptElementAttributes' must be an object");
		}

		Map<String, Object> scriptElementAttributesMap =
			(Map<String, Object>)scriptElementAttributes;

		for (Map.Entry<String, Object> entry :
				scriptElementAttributesMap.entrySet()) {

			if (Objects.equals(entry.getKey(), "src")) {
				throw new GradleException(
					"The key 'src' is not allowed as a script element " +
						"attribute");
			}

			Object value = entry.getValue();

			if (value == null) {
				throw new GradleException(
					String.format(
						"The value for the script element attribute '%s' " +
							"must be specified",
						entry.getKey()));
			}

			if (value instanceof List || value instanceof Map) {
				throw new GradleException(
					String.format(
						"The value for the script element attribute '%s' " +
							"must be a scalar",
						entry.getKey()));
			}
		}
	}

	private void _validateRequiredDirectory(
			ClientExtension clientExtension, Project project,
			String requiredDirectoryName)
		throws GradleException {

		File file = project.file(requiredDirectoryName);

		if (file.isDirectory()) {
			return;
		}

		throw new GradleException(
			String.format(
				"A %s directory is required for client extension %s with " +
					"type %s",
				StringUtil.quote(requiredDirectoryName), clientExtension.id,
				clientExtension.type));
	}

	private void _validateRequiredTypeSettingsKeys(
			ClientExtension clientExtension, String... requiredTypeSettingsKeys)
		throws GradleException {

		for (String requiredTypeSettingsKey : requiredTypeSettingsKeys) {
			if (clientExtension.typeSettings.containsKey(
					requiredTypeSettingsKey)) {

				continue;
			}

			throw new GradleException(
				String.format(
					"Client extension %s with type %s must define the " +
						"property %s",
					clientExtension.id, clientExtension.type,
					StringUtil.quote(requiredTypeSettingsKey)));
		}
	}

	private void _validateSiteInitializerSiteNameI18n(
		ClientExtension clientExtension) {

		Map<String, Object> typeSettings = clientExtension.typeSettings;

		if (!typeSettings.containsKey("siteName_i18n")) {
			return;
		}

		Object siteNameI18n = typeSettings.get("siteName_i18n");

		if (!(siteNameI18n instanceof Map)) {
			throw new GradleException(
				"The property 'siteName_i18n' must be an object");
		}

		Map<String, Object> siteNameI18nMap = (Map<String, Object>)siteNameI18n;

		for (Map.Entry<String, Object> entry : siteNameI18nMap.entrySet()) {
			Object value = entry.getValue();

			if (value == null) {
				throw new GradleException(
					String.format(
						"The value for the language key '%s' must be specified",
						entry.getKey()));
			}

			if (!(value instanceof String)) {
				throw new GradleException(
					String.format(
						"The value for the language key '%s' must be a string",
						entry.getKey()));
			}
		}
	}

	private void _validateTypeSettingsValues(
			ClientExtension clientExtension, String typeSettingsKey,
			String... validValues)
		throws GradleException {

		Object typeSettingsValue = clientExtension.typeSettings.get(
			typeSettingsKey);

		if ((typeSettingsValue == null) ||
			ArrayUtil.contains(validValues, typeSettingsValue)) {

			return;
		}

		throw new GradleException(
			String.format(
				"Client extension %s has an invalid value %s for the " +
					"property %s. Valid values are: %s.",
				clientExtension.id, StringUtil.quote(typeSettingsValue),
				StringUtil.quote(typeSettingsKey),
				StringUtil.join(StringUtil.COMMA_AND_SPACE, validValues)));
	}

	private static final String _CLIENT_EXTENSION_YAML =
		"client-extension.yaml";

	private static final boolean _DEFAULT_REPOSITORY_ENABLED = true;

	private static final String _ENV_LIFERAY_ROUTES_CLIENT_EXTENSION =
		"LIFERAY_ROUTES_CLIENT_EXTENSION";

	private static final String _ENV_LIFERAY_ROUTES_DXP = "LIFERAY_ROUTES_DXP";

	private static final Pattern _overrideClientExtensionYamlPattern =
		Pattern.compile("^client-extension\\.([a-z]+)\\.yaml$");

	private final Map<String, Set<Project>> _clientExtensionIds =
		new HashMap<>();
	private final boolean _defaultRepositoryEnabled;
	private final NodeBuildConfigurer _nodeBuildConfigurer =
		new NodeBuildConfigurer();
	private final ThemeCSSTypeConfigurer _themeCSSTypeConfigurer =
		new ThemeCSSTypeConfigurer();
	private final ObjectMapper _yamlObjectMapper = new ObjectMapper(
		new YAMLFactory());

}