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
import com.github.dockerjava.api.command.InspectImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;

import com.liferay.gradle.plugins.workspace.internal.client.extension.ClientExtensionValues;
import com.liferay.gradle.plugins.workspace.internal.client.extension.HelmCLI;
import com.liferay.gradle.plugins.workspace.internal.util.FileUtil;

import java.io.File;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.CopySpec;
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

		_chart = objectFactory.property(String.class);
		_chartVersion = objectFactory.property(String.class);
		_clientExtensionConfigFile = objectFactory.fileProperty();
		_dockerfile = objectFactory.fileProperty();
		_helm = objectFactory.property(String.class);
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

	@Input
	public Property<String> getChart() {
		return _chart;
	}

	@Input
	@Optional
	public Property<String> getChartVersion() {
		return _chartVersion;
	}

	@InputFile
	public RegularFileProperty getClientExtensionConfigFile() {
		return _clientExtensionConfigFile;
	}

	@InputFile
	public RegularFileProperty getDockerfile() {
		return _dockerfile;
	}

	@Internal
	public Property<String> getHelm() {
		return _helm;
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
		String baseImage = ClientExtensionValues.getBaseImage(
			FileUtil.read(_getFile(_dockerfile)));

		String imageRepository = null;
		String imageTag = null;

		if (!ClientExtensionValues.isConfigurationOnly(baseImage)) {
			SetProperty<String> imagesSetProperty = getImages();

			Set<String> images = imagesSetProperty.get();

			if (images.isEmpty()) {
				throw new GradleException(
					"Set the liferay.workspace.client.extension.registry " +
						"property and a version to publish \"" +
							getProject().getName() + "\"");
			}

			super.runRemoteCommand();

			Iterator<String> iterator = images.iterator();

			String image = iterator.next();

			imageRepository = ClientExtensionValues.getRepository(image);

			imageTag =
				image.substring(imageRepository.length() + 1) + "@" +
					_getDigest(image, imageRepository);
		}

		Project project = getProject();

		String registry = _registry.getOrNull();
		String version = _version.getOrNull();

		if ((registry == null) || (version == null)) {
			throw new GradleException(
				"Set the liferay.workspace.client.extension.registry " +
					"property and a version to publish \"" + project.getName() +
						"\"");
		}

		File workDir = getTemporaryDir();

		project.delete(workDir.listFiles());

		HelmCLI helmCLI = new HelmCLI(_helm.get());

		File chartDir = _getChartDir(helmCLI, workDir);

		File valuesFile = new File(chartDir, "values.yaml");

		_yamlObjectMapper.writeValue(
			valuesFile,
			_merge(
				_readYAML(valuesFile),
				ClientExtensionValues.derive(
					baseImage, _readJSON(_clientExtensionConfigFile),
					imageRepository, imageTag, _readJSON(_lcpJsonFile),
					project.getName())));

		File chartYamlFile = new File(chartDir, "Chart.yaml");

		Map<String, Object> chartYamlMap = _readYAML(chartYamlFile);

		chartYamlMap.put("appVersion", version);
		chartYamlMap.put("name", project.getName());
		chartYamlMap.put("version", version);

		_yamlObjectMapper.writeValue(chartYamlFile, chartYamlMap);

		File outputDir = _getFile(_outputDir);

		project.delete(outputDir);

		outputDir.mkdirs();

		String repository = "oci://" + registry + "/charts";

		String digest = helmCLI.push(
			helmCLI.packageChart(chartDir, outputDir), repository);

		Logger logger = getLogger();

		logger.lifecycle(
			"Published Helm chart {}/{} version {} ({})", repository,
			project.getName(), version, digest);
	}

	private File _getChartDir(HelmCLI helmCLI, File workDir) throws Exception {
		String chart = _chart.get();

		if (chart.startsWith("oci://")) {
			return helmCLI.pull(chart, _chartVersion.getOrNull(), workDir);
		}

		Project project = getProject();

		Project rootProject = project.getRootProject();

		File chartDir = rootProject.file(chart);

		if (!chartDir.isDirectory()) {
			return helmCLI.pull(chart, _chartVersion.getOrNull(), workDir);
		}

		File copyDir = new File(workDir, chartDir.getName());

		project.copy(
			new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					copySpec.from(chartDir);
					copySpec.into(copyDir);
				}

			});

		return copyDir;
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

	private Map<String, Object> _merge(
		Map<String, Object> defaultValuesMap,
		Map<String, Object> derivedValuesMap) {

		Map<String, Object> valuesMap = new LinkedHashMap<>(defaultValuesMap);

		for (Map.Entry<String, Object> entry : derivedValuesMap.entrySet()) {
			Object defaultValue = valuesMap.get(entry.getKey());
			Object value = entry.getValue();

			if ((defaultValue instanceof Map<?, ?>) &&
				(value instanceof Map<?, ?>)) {

				value = _merge(
					(Map<String, Object>)defaultValue,
					(Map<String, Object>)value);
			}

			valuesMap.put(entry.getKey(), value);
		}

		return valuesMap;
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

	private Map<String, Object> _readYAML(File file) throws Exception {
		if (!file.exists()) {
			return new LinkedHashMap<>();
		}

		return _yamlObjectMapper.readValue(
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

	private final Property<String> _chart;
	private final Property<String> _chartVersion;
	private final RegularFileProperty _clientExtensionConfigFile;
	private final RegularFileProperty _dockerfile;
	private final Property<String> _helm;
	private final RegularFileProperty _lcpJsonFile;
	private final DirectoryProperty _outputDir;
	private final Property<String> _registry;
	private final Property<String> _version;

}