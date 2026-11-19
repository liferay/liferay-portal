/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import com.liferay.gradle.plugins.workspace.internal.client.extension.ClientExtensionDescriptor;
import com.liferay.gradle.plugins.workspace.internal.util.FileUtil;

import java.io.File;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskOutputs;

/**
 * @author Gregory Amerson
 */
public class CreateClientExtensionDescriptorTask extends DefaultTask {

	public CreateClientExtensionDescriptorTask() {
		Project project = getProject();

		ObjectFactory objectFactory = project.getObjects();

		_clientExtensionConfigFile = objectFactory.fileProperty();
		_image = objectFactory.property(String.class);
		_imageDigestFile = objectFactory.fileProperty();
		_oAuthApplicationHeadlessServerERC = objectFactory.property(
			String.class);
		_outputDir = objectFactory.directoryProperty();
		_type = objectFactory.property(String.class);

		ProjectLayout projectLayout = project.getLayout();

		DirectoryProperty buildDirectoryProperty =
			projectLayout.getBuildDirectory();

		_outputDir.convention(
			buildDirectoryProperty.dir("client-extension-descriptor"));

		TaskOutputs taskOutputs = getOutputs();

		taskOutputs.upToDateWhen(task -> false);
	}

	@TaskAction
	public void createClientExtensionDescriptor() throws Exception {
		String type = _type.get();

		String imageDigest = null;
		String imageRepository = null;
		String imageTag = null;

		if (!type.equals(ClientExtensionDescriptor.TYPE_CONFIGURATION) &&
			_image.isPresent()) {

			String image = _image.get();

			imageRepository = ClientExtensionDescriptor.getRepository(image);

			imageTag = image.substring(imageRepository.length() + 1);

			if (_imageDigestFile.isPresent()) {
				String imageDigestFileText = FileUtil.read(
					_getFile(_imageDigestFile));

				imageDigest = imageDigestFileText.trim();
			}
		}

		File outputDir = _getFile(_outputDir);

		Project project = getProject();

		project.delete(outputDir);

		outputDir.mkdirs();

		ObjectWriter objectWriter =
			_objectMapper.writerWithDefaultPrettyPrinter();

		objectWriter.writeValue(
			new File(outputDir, ClientExtensionDescriptor.FILE_NAME),
			ClientExtensionDescriptor.derive(
				_readJSON(_clientExtensionConfigFile), imageDigest,
				imageRepository, imageTag,
				_oAuthApplicationHeadlessServerERC.getOrNull(), type));

		String dockerfileContent = String.format(
			"FROM scratch\n\nLABEL %s=\"%d\"\n\nCOPY %s /\n",
			ClientExtensionDescriptor.LABEL_VERSION,
			ClientExtensionDescriptor.VERSION,
			ClientExtensionDescriptor.FILE_NAME);

		File dockerfile = new File(outputDir, "Dockerfile");

		Path dockerfilePath = dockerfile.toPath();

		Files.write(
			dockerfilePath, dockerfileContent.getBytes(StandardCharsets.UTF_8));
	}

	@InputFile
	public RegularFileProperty getClientExtensionConfigFile() {
		return _clientExtensionConfigFile;
	}

	@Input
	@Optional
	public Property<String> getImage() {
		return _image;
	}

	@Internal
	public RegularFileProperty getImageDigestFile() {
		return _imageDigestFile;
	}

	@Input
	@Optional
	public Property<String> getOAuthApplicationHeadlessServerERC() {
		return _oAuthApplicationHeadlessServerERC;
	}

	@OutputDirectory
	public DirectoryProperty getOutputDir() {
		return _outputDir;
	}

	@Input
	public Property<String> getType() {
		return _type;
	}

	private File _getFile(DirectoryProperty directoryProperty) {
		Directory directory = directoryProperty.get();

		return directory.getAsFile();
	}

	private File _getFile(RegularFileProperty regularFileProperty) {
		RegularFile regularFile = regularFileProperty.get();

		return regularFile.getAsFile();
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

	private final RegularFileProperty _clientExtensionConfigFile;
	private final Property<String> _image;
	private final RegularFileProperty _imageDigestFile;
	private final Property<String> _oAuthApplicationHeadlessServerERC;
	private final DirectoryProperty _outputDir;
	private final Property<String> _type;

}