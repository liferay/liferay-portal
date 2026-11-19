/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import com.bmuschko.gradle.docker.internal.RegistryAuthLocator;
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.PushImageCmd;
import com.github.dockerjava.api.command.TagImageCmd;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.PushResponseItem;
import com.github.dockerjava.api.model.ResponseItem;

import com.liferay.gradle.plugins.workspace.internal.client.extension.ClientExtensionDescriptor;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskOutputs;

/**
 * @author Gregory Amerson
 */
public class PushClientExtensionImageTask extends DockerPushImage {

	public PushClientExtensionImageTask() {
		Project project = getProject();

		ObjectFactory objectFactory = project.getObjects();

		_imageDigestFile = objectFactory.fileProperty();

		ProjectLayout projectLayout = project.getLayout();

		DirectoryProperty buildDirectoryProperty =
			projectLayout.getBuildDirectory();

		_imageDigestFile.convention(
			buildDirectoryProperty.file("client-extension-image/digest"));

		TaskOutputs taskOutputs = getOutputs();

		taskOutputs.upToDateWhen(task -> false);
	}

	@OutputFile
	public RegularFileProperty getImageDigestFile() {
		return _imageDigestFile;
	}

	@Override
	public void runRemoteCommand() throws Exception {
		SetProperty<String> imagesSetProperty = getImages();

		Set<String> images = imagesSetProperty.get();

		if (images.isEmpty()) {
			Project project = getProject();

			throw new GradleException(
				StringUtil.concat(
					"Set the \"liferay.workspace.client.extension.registry\" ",
					"property and a version to publish \"", project.getName(),
					"\""));
		}

		super.runRemoteCommand();

		Iterator<String> iterator = images.iterator();

		String image = iterator.next();

		String imageRepository = ClientExtensionDescriptor.getRepository(image);

		String digest = _getDigest(image, imageRepository);

		RegularFile regularFile = _imageDigestFile.get();

		File file = regularFile.getAsFile();

		File parentFile = file.getParentFile();

		parentFile.mkdirs();

		Files.write(file.toPath(), digest.getBytes(StandardCharsets.UTF_8));

		String tag = image.substring(imageRepository.length() + 1);

		String digestHex = digest.substring(digest.indexOf(':') + 1);

		String digestTag = tag + "." + digestHex.substring(0, 12);

		DockerClient dockerClient = getDockerClient();

		TagImageCmd tagImageCmd = dockerClient.tagImageCmd(
			image, imageRepository, digestTag);

		tagImageCmd.exec();

		String digestImage = imageRepository + ":" + digestTag;

		_pushImage(digestImage);

		Logger logger = getLogger();

		logger.lifecycle("Pushed {}@{}", image, digest);
		logger.lifecycle("Pushed {}@{}", digestImage, digest);
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
			StringUtil.concat(
				"Unable to find the digest of \"", image,
				"\" after pushing it"));
	}

	private void _pushImage(String image) throws Exception {
		RegistryAuthLocator registryAuthLocator = getRegistryAuthLocator();

		AuthConfig authConfig = registryAuthLocator.lookupAuthConfig(
			image, getRegistryCredentials());

		DockerClient dockerClient = getDockerClient();

		PushImageCmd pushImageCmd = dockerClient.pushImageCmd(image);

		pushImageCmd.withAuthConfig(authConfig);

		ResultCallback.Adapter<PushResponseItem> resultCallback =
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
							StringUtil.concat(
								"Unable to push \"", image, "\": ", message));
					}
				}

			};

		pushImageCmd.exec(resultCallback);

		resultCallback.awaitCompletion();
	}

	private final RegularFileProperty _imageDigestFile;

}