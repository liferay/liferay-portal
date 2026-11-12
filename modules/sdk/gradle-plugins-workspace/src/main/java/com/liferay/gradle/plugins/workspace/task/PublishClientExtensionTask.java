/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import com.bmuschko.gradle.docker.tasks.image.DockerPushImage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.BuildImageResultCallback;
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.ResponseItem;

import com.liferay.gradle.plugins.workspace.internal.client.extension.ClientExtensionDescriptor;
import com.liferay.gradle.plugins.workspace.internal.util.FileUtil;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskOutputs;

import org.yaml.snakeyaml.DumperOptions;

/**
 * @author Gregory Amerson
 */
public class PublishClientExtensionTask extends DockerPushImage {

	public PublishClientExtensionTask() {
		Project project = getProject();

		ObjectFactory objectFactory = project.getObjects();

		_clientExtensionConfigFile = objectFactory.fileProperty();
		_dockerfile = objectFactory.fileProperty();
		_lcpJsonFile = objectFactory.fileProperty();
		_outputDir = objectFactory.directoryProperty();
		_registry = objectFactory.property(String.class);
		_version = objectFactory.property(String.class);

		ProjectLayout projectLayout = project.getLayout();

		DirectoryProperty buildDirectoryProperty =
			projectLayout.getBuildDirectory();

		_outputDir.convention(
			buildDirectoryProperty.dir("client-extension-publish"));

		TaskOutputs taskOutputs = getOutputs();

		taskOutputs.upToDateWhen(task -> false);
	}

	@InputFile
	public RegularFileProperty getClientExtensionConfigFile() {
		return _clientExtensionConfigFile;
	}

	@Internal
	public String getDescriptorImage() {
		return _registry.get() + "/descriptors/" + getProject().getName() +
			":" + _version.get();
	}

	@InputFile
	public RegularFileProperty getDockerfile() {
		return _dockerfile;
	}

	@Internal
	public RegularFileProperty getLcpJsonFile() {
		return _lcpJsonFile;
	}

	@OutputDirectory
	public DirectoryProperty getOutputDir() {
		return _outputDir;
	}

	@Input
	@Optional
	public Property<String> getRegistry() {
		return _registry;
	}

	@Input
	@Optional
	public Property<String> getVersion() {
		return _version;
	}

	@Override
	public void runRemoteCommand() throws Exception {
		Project project = getProject();

		if (!_registry.isPresent() || !_version.isPresent()) {
			throw new GradleException(
				"Set the liferay.workspace.client.extension.registry " +
					"property and a version to publish \"" + project.getName() +
						"\"");
		}

		String baseImage = ClientExtensionDescriptor.getBaseImage(
			FileUtil.read(_getFile(_dockerfile)));

		String imageDigest = null;
		String imageRepository = null;
		String imageTag = null;

		if (!ClientExtensionDescriptor.isConfigurationOnly(baseImage)) {
			SetProperty<String> imagesSetProperty = getImages();

			Set<String> images = imagesSetProperty.get();

			super.runRemoteCommand();

			Iterator<String> iterator = images.iterator();

			String image = iterator.next();

			imageRepository = ClientExtensionDescriptor.getRepository(image);

			imageDigest = _getDigest(image, imageRepository);
			imageTag = image.substring(imageRepository.length() + 1);
		}

		File outputDir = _getFile(_outputDir);

		project.delete(outputDir);

		outputDir.mkdirs();

		_yamlObjectMapper.writeValue(
			new File(outputDir, "client-extension.yaml"),
			ClientExtensionDescriptor.derive(
				baseImage, _readJSON(_clientExtensionConfigFile), imageDigest,
				imageRepository, imageTag, _readJSON(_lcpJsonFile)));

		Files.write(
			new File(
				outputDir, "Dockerfile"
			).toPath(),
			"FROM scratch\n\nCOPY client-extension.yaml /\n".getBytes(
				StandardCharsets.UTF_8));

		String descriptorImage = getDescriptorImage();

		_buildImage(outputDir, descriptorImage);

		_pushImage(descriptorImage);

		Logger logger = getLogger();

		logger.lifecycle(
			"Published client extension descriptor {}", descriptorImage);
	}

	private void _buildImage(File dir, String image) {
		DockerClient dockerClient = getDockerClient();

		BuildImageCmd buildImageCmd = dockerClient.buildImageCmd(dir);

		buildImageCmd.withTags(Collections.singleton(image));

		BuildImageResultCallback buildImageResultCallback = buildImageCmd.exec(
			new BuildImageResultCallback());

		buildImageResultCallback.awaitImageId();
	}

	private String _getDigest(String image, String imageRepository) {
		DockerClient dockerClient = getDockerClient();

		InspectImageCmd inspectImageCmd = dockerClient.inspectImageCmd(image);

		InspectImageResponse inspectImageResponse = inspectImageCmd.exec();

		List<String> repoDigests = inspectImageResponse.getRepoDigests();

		if (repoDigests != null) {
			for (String repoDigest : repoDigests) {
				if (repoDigest.startsWith(imageRepository + "@")) {
					return repoDigest.substring(imageRepository.length() + 1);
				}
			}
		}

		throw new GradleException(
			"Unable to find the digest of \"" + image + "\" after pushing it");
	}

	private File _getFile(DirectoryProperty directoryProperty) {
		Directory directory = directoryProperty.get();

		return directory.getAsFile();
	}

	private File _getFile(RegularFileProperty regularFileProperty) {
		RegularFile regularFile = regularFileProperty.get();

		return regularFile.getAsFile();
	}

	private void _pushImage(String image) throws Exception {
		AuthConfig authConfig = getRegistryAuthLocator().lookupAuthConfig(
			image, getRegistryCredentials());

		DockerClient dockerClient = getDockerClient();

		PushImageCmd pushImageCmd = dockerClient.pushImageCmd(image);

		pushImageCmd.withAuthConfig(authConfig);

		pushImageCmd.exec(
			new ResultCallback.Adapter<PushResponseItem>() {

				@Override
				public void onNext(PushResponseItem pushResponseItem) {
					if (pushResponseItem.isErrorIndicated()) {
						ResponseItem.ErrorDetail errorDetail =
							pushResponseItem.getErrorDetail();

						String message = pushResponseItem.getError();

						if (errorDetail != null) {
							message = errorDetail.getMessage();
						}

						throw new GradleException(
							"Unable to push \"" + image + "\": " + message);
					}
				}

			}
		).awaitCompletion();
	}

	private Map<String, Object> _readJSON(
			RegularFileProperty regularFileProperty)
		throws Exception {

		File file = _getFile(regularFileProperty);

		if (!file.exists()) {
			return new LinkedHashMap<>();
		}

		return _objectMapper.readValue(
			file,
			new TypeReference<LinkedHashMap<String, Object>>() {
			});
	}

	private static final ObjectMapper _objectMapper = new ObjectMapper();
	private static final ObjectMapper _yamlObjectMapper;

	static {
		DumperOptions dumperOptions = new DumperOptions();

		dumperOptions.setMaxSimpleKeyLength(1024);

		_yamlObjectMapper = new ObjectMapper(
			YAMLFactory.builder(
			).disable(
				YAMLGenerator.Feature.WRITE_DOC_START_MARKER
			).dumperOptions(
				dumperOptions
			).build());
	}

	private final RegularFileProperty _clientExtensionConfigFile;
	private final RegularFileProperty _dockerfile;
	private final RegularFileProperty _lcpJsonFile;
	private final DirectoryProperty _outputDir;
	private final Property<String> _registry;
	private final Property<String> _version;

}