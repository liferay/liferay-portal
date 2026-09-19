/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.app.docker;

import com.bmuschko.gradle.docker.DockerRemoteApiPlugin;
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage;
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage;
import com.bmuschko.gradle.docker.tasks.image.DockerTagImage;

import com.liferay.gradle.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import java.io.File;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.tools.ant.filters.FixCrLfFilter;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.PublishArtifactSet;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCopyDetails;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.publish.plugins.PublishingPlugin;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Sync;
import org.gradle.api.tasks.bundling.War;

/**
 * @author Andrea Di Giorgi
 */
public class AppDockerPlugin implements Plugin<Project> {

	public static final String BUILD_APP_DOCKER_IMAGE_TASK_NAME =
		"buildAppDockerImage";

	public static final String PLUGIN_NAME = "appDocker";

	public static final String PREPARE_APP_DOCKER_IMAGE_INPUT_DIR_TASK_NAME =
		"prepareAppDockerImageInputDir";

	public static final String PUSH_APP_DOCKER_IMAGE_TASK_NAME =
		"pushAppDockerImage";

	public static final String TAG_APP_DOCKER_IMAGE_TASK_NAME =
		"tagAppDockerImage";

	@Override
	public void apply(Project project) {
		GradleUtil.applyPlugin(project, DockerRemoteApiPlugin.class);

		final AppDockerExtension appDockerExtension = GradleUtil.addExtension(
			project, PLUGIN_NAME, AppDockerExtension.class);

		final Sync prepareAppDockerImageInputDirTask =
			_addTaskPrepareAppDockerImageInputDir(project, appDockerExtension);

		final DockerBuildImage buildAppDockerImageTask =
			_addTaskBuildAppDockerImage(
				prepareAppDockerImageInputDirTask, appDockerExtension);

		final DockerPushImage pushAppDockerImageTask =
			_addTaskPushAppDockerImage(
				buildAppDockerImageTask, appDockerExtension);

		project.afterEvaluate(
			new Action<Project>() {

				@Override
				public void execute(Project project) {
					_addTasksPushAppDockerImage(
						buildAppDockerImageTask, pushAppDockerImageTask,
						appDockerExtension);
				}

			});

		Gradle gradle = project.getGradle();

		gradle.afterProject(
			new Closure<Void>(project) {

				@SuppressWarnings("unused")
				public void doCall(Project subproject) {
					_configureTaskPrepareAppDockerImageInputDir(
						prepareAppDockerImageInputDirTask, appDockerExtension,
						subproject);
				}

			});
	}

	private DockerBuildImage _addTaskBuildAppDockerImage(
		Sync prepareAppDockerImageInputDirTask,
		AppDockerExtension appDockerExtension) {

		DockerBuildImage dockerBuildImage = GradleUtil.addTask(
			prepareAppDockerImageInputDirTask.getProject(),
			BUILD_APP_DOCKER_IMAGE_TASK_NAME, DockerBuildImage.class);

		dockerBuildImage.setDependsOn(
			Collections.singleton(prepareAppDockerImageInputDirTask));
		dockerBuildImage.setDescription("Builds the Docker image of the app.");
		dockerBuildImage.setGroup(BasePlugin.BUILD_GROUP);

		SetProperty<String> setProperty = dockerBuildImage.getImages();

		setProperty.add(_getImageRepository(appDockerExtension));

		DirectoryProperty directoryProperty = dockerBuildImage.getInputDir();

		directoryProperty.set(
			prepareAppDockerImageInputDirTask.getDestinationDir());

		return dockerBuildImage;
	}

	private Sync _addTaskPrepareAppDockerImageInputDir(
		final Project project, final AppDockerExtension appDockerExtension) {

		Sync sync = GradleUtil.addTask(
			project, PREPARE_APP_DOCKER_IMAGE_INPUT_DIR_TASK_NAME, Sync.class);

		sync.filesMatching(
			"**/*.sh",
			new Action<FileCopyDetails>() {

				@Override
				public void execute(FileCopyDetails fileCopyDetails) {
					fileCopyDetails.filter(_fixCrLfArgs, FixCrLfFilter.class);
				}

			});

		sync.from(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return appDockerExtension.getInputDir();
				}

			});

		sync.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return new File(project.getBuildDir(), "docker");
				}

			});

		sync.setDescription(
			"Copies all the subproject artifacts and other resources to a " +
				"temporary directory that will be used to build the Docker " +
					"image of the app.");

		return sync;
	}

	private DockerPushImage _addTaskPushAppDockerImage(
		DockerBuildImage buildAppDockerImageTask,
		AppDockerExtension appDockerExtension) {

		DockerPushImage dockerPushImage = GradleUtil.addTask(
			buildAppDockerImageTask.getProject(),
			PUSH_APP_DOCKER_IMAGE_TASK_NAME, DockerPushImage.class);

		dockerPushImage.setDependsOn(
			Collections.singleton(buildAppDockerImageTask));
		dockerPushImage.setDescription(
			"Pushes the Docker image of the app to the registry.");
		dockerPushImage.setGroup(PublishingPlugin.PUBLISH_TASK_GROUP);

		SetProperty<String> setProperty = dockerPushImage.getImages();

		setProperty.add(_getImageRepository(appDockerExtension));

		return dockerPushImage;
	}

	private DockerPushImage _addTaskPushAppDockerImage(
		DockerTagImage dockerTagImage) {

		Property<String> repositoryProperty = dockerTagImage.getRepository();
		Property<String> tagProperty = dockerTagImage.getTag();

		String imageRepositoryAndTag = _getImageRepositoryAndTag(
			repositoryProperty.get(), tagProperty.get());

		DockerPushImage dockerPushImage = GradleUtil.addTask(
			dockerTagImage.getProject(),
			_getTaskName(
				PUSH_APP_DOCKER_IMAGE_TASK_NAME, imageRepositoryAndTag),
			DockerPushImage.class);

		dockerPushImage.setDependsOn(Collections.singleton(dockerTagImage));
		dockerPushImage.setDescription(
			"Pushes the Docker image \"" + imageRepositoryAndTag +
				"\" to the registry.");

		SetProperty<String> setProperty = dockerPushImage.getImages();

		setProperty.add(imageRepositoryAndTag);

		return dockerPushImage;
	}

	private DockerTagImage _addTaskTagAppDockerImage(
		final DockerBuildImage buildAppDockerImagetask, String imageRepository,
		String imageTag) {

		Project project = buildAppDockerImagetask.getProject();

		String imageRepositoryAndTag = _getImageRepositoryAndTag(
			imageRepository, imageTag);

		DockerTagImage dockerTagImage = GradleUtil.addTask(
			project,
			_getTaskName(TAG_APP_DOCKER_IMAGE_TASK_NAME, imageRepositoryAndTag),
			DockerTagImage.class);

		dockerTagImage.setDependsOn(
			Collections.singleton(buildAppDockerImagetask));
		dockerTagImage.setDescription(
			"Creates the tag \"" + imageRepositoryAndTag +
				"\" which refers to the Docker image of the app.");

		Property<String> repositoryProperty = dockerTagImage.getRepository();

		repositoryProperty.set(imageRepository);

		Property<String> tagProperty = dockerTagImage.getTag();

		tagProperty.set(imageTag);

		dockerTagImage.targetImageId(
			new Closure<String>(project) {

				@SuppressWarnings("unused")
				public String doCall() {
					Property<String> imageIdProperty =
						buildAppDockerImagetask.getImageId();

					return imageIdProperty.get();
				}

			});

		return dockerTagImage;
	}

	private void _addTasksPushAppDockerImage(
		DockerBuildImage buildAppDockerImageTask,
		DockerPushImage pushAppDockerImageTask,
		AppDockerExtension appDockerExtension) {

		String imageRepository = _getImageRepository(appDockerExtension);

		for (Object imageTagObject : appDockerExtension.getImageTags()) {
			String imageTag = GradleUtil.toString(imageTagObject);

			if (Validator.isNull(imageTag)) {
				continue;
			}

			DockerTagImage dockerTagImage = _addTaskTagAppDockerImage(
				buildAppDockerImageTask, imageRepository, imageTag);

			DockerPushImage dockerPushImage = _addTaskPushAppDockerImage(
				dockerTagImage);

			pushAppDockerImageTask.dependsOn(dockerPushImage);
		}
	}

	private void _configureTaskPrepareAppDockerImageInputDir(
		Sync prepareAppDockerImageInputDirTask,
		AppDockerExtension appDockerExtension, Project subproject) {

		Logger logger = prepareAppDockerImageInputDirTask.getLogger();

		Set<Project> subprojects = appDockerExtension.getSubprojects();

		if (!subprojects.contains(subproject)) {
			if (logger.isInfoEnabled()) {
				logger.info(
					"Excluding {} from {}", subproject,
					prepareAppDockerImageInputDirTask);
			}

			return;
		}

		Spec<Project> spec = appDockerExtension.getOnlyIf();

		if (!spec.isSatisfiedBy(subproject)) {
			if (logger.isInfoEnabled()) {
				logger.info(
					"Explicitly excluding {} from {}", subproject,
					prepareAppDockerImageInputDirTask);
			}

			return;
		}

		PluginContainer pluginContainer = subproject.getPlugins();

		if (pluginContainer.hasPlugin(WarPlugin.class)) {
			War war = (War)GradleUtil.getTask(
				subproject, WarPlugin.WAR_TASK_NAME);

			prepareAppDockerImageInputDirTask.from(war);
		}
		else {
			Configuration configuration = GradleUtil.getConfiguration(
				subproject, Dependency.DEFAULT_CONFIGURATION);

			PublishArtifactSet publishArtifactSet =
				configuration.getAllArtifacts();

			prepareAppDockerImageInputDirTask.from(
				publishArtifactSet.getFiles());
		}
	}

	private String _getImageRepository(AppDockerExtension appDockerExtension) {
		String imageUser = appDockerExtension.getImageUser();
		String imageName = appDockerExtension.getImageName();

		if (Validator.isNull(imageUser)) {
			return imageName;
		}

		return imageUser + '/' + imageName;
	}

	private String _getImageRepositoryAndTag(
		String imageRepository, String imageTag) {

		if (Validator.isNull(imageTag)) {
			return imageRepository;
		}

		return imageRepository + ':' + imageTag;
	}

	private String _getTaskName(String... values) {
		StringBuilder sb = new StringBuilder();

		for (String value : values) {
			String s = value;

			s = s.replace(':', '_');
			s = s.replace('/', '_');

			sb.append(s);

			sb.append('_');
		}

		sb.setLength(sb.length() - 1);

		return sb.toString();
	}

	private static final Map<String, Object> _fixCrLfArgs =
		new HashMap<String, Object>() {
			{
				put("eof", FixCrLfFilter.AddAsisRemove.newInstance("remove"));
				put("eol", FixCrLfFilter.CrLf.newInstance("lf"));
				put("fixlast", false);
			}
		};

}