/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.task;

import aQute.bnd.osgi.Constants;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.common.collect.Sets;

import com.liferay.gradle.plugins.workspace.configurator.ClientExtensionProjectConfigurator;
import com.liferay.gradle.plugins.workspace.internal.client.extension.ClientExtension;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.plugins.workspace.internal.util.JsonNodeUtil;
import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;
import com.liferay.release.util.ResourceUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileTree;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFile;
import org.gradle.api.logging.Logger;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskInputs;
import org.gradle.api.tasks.TaskOutputs;

/**
 * @author Gregory Amerson
 */
public class CreateClientExtensionConfigTask extends DefaultTask {

	public CreateClientExtensionConfigTask() {
		_buildDir =
			ClientExtensionProjectConfigurator.getClientExtensionBuildDir(
				_project);
		_clientExtensionConfigFile = _addTaskOutputFile(
			_project.getName() + ".client-extension-config.json");

		_dockerFile = _addTaskOutputFile("Dockerfile");
		_lcpJsonFile = _addTaskOutputFile("LCP.json");
		_pluginPackagePropertiesFile = _addTaskOutputFile(
			"WEB-INF/liferay-plugin-package.properties");
	}

	public void addClientExtension(ClientExtension clientExtension) {
		_clientExtensions.add(clientExtension);

		if (Objects.equals(clientExtension.type, "siteInitializer") &&
			(_siteInitializerJsonFile == null)) {

			_siteInitializerJsonFile = _addTaskOutputFile(
				"site-initializer/site-initializer.json");
		}

		if (Objects.equals(clientExtension.type, "themeCSS") &&
			clientExtension.typeSettings.containsKey(
				"frontendTokenDefinitionJSON")) {

			TaskInputs taskInputs = getInputs();

			ProviderFactory providerFactory = _project.getProviders();

			taskInputs.file(
				providerFactory.provider(
					() -> clientExtension.typeSettings.get(
						"frontendTokenDefinitionJSON")));
		}
	}

	@TaskAction
	public void createClientExtensionConfig() {
		Properties pluginPackageProperties = _getPluginPackageProperties();

		String classificationGrouping = _validateAndGetClassificationGrouping(
			_clientExtensions);

		Map<String, Object> jsonMap = new HashMap<>();

		String batchType = null;

		for (ClientExtension clientExtension : _clientExtensions) {
			String type = clientExtension.type;

			if (Objects.equals(type, "batch")) {
				pluginPackageProperties.put(
					"Liferay-Client-Extension-Batch", "batch/");

				_setLiferayVirtualInstanceId(pluginPackageProperties);

				batchType = "batch";
			}
			else if (Objects.equals(type, "globalJS")) {
				_mapGlobalJSScriptElementAttributesToJSONString(
					clientExtension);
			}
			else if (Objects.equals(type, "siteInitializer")) {
				pluginPackageProperties.put(
					"Liferay-Client-Extension-Site-Initializer",
					"site-initializer/");

				_setLiferayVirtualInstanceId(pluginPackageProperties);

				batchType = StringUtil.getDockerSafeName(type);

				_createSiteInitializerJsonFile(clientExtension);

				if (_virtualInstanceId != null) {
					pluginPackageProperties.put(
						"Liferay-Virtual-Instance-Id", _virtualInstanceId);
				}
			}
			else if (Objects.equals(type, "themeCSS")) {
				_inlineFrontendTokenDefinitionJSON(clientExtension);
			}

			if (Objects.equals(
					clientExtension.getClassification(), "frontend")) {

				_expandWildcards(clientExtension.typeSettings);

				pluginPackageProperties.put(
					"Liferay-Client-Extension-Frontend", "static/");
			}

			jsonMap.putAll(clientExtension.toJSONMap(_virtualInstanceId));
		}

		Map<String, String> substitutionMap = new HashMap<>();

		for (ClientExtension clientExtension : _clientExtensions) {
			for (Map.Entry<String, Object> entry :
					clientExtension.typeSettings.entrySet()) {

				String newKey = String.format(
					"__%s.%s__", _getIdOrBatchType(clientExtension),
					entry.getKey());

				substitutionMap.put(newKey, String.valueOf(entry.getValue()));
			}
		}

		if (batchType != null) {
			substitutionMap.put("__BATCH_TYPE__", batchType);

			if (Objects.equals(batchType, "batch")) {
				_processBatchJSONFiles();
			}
		}

		String projectId = StringUtil.toAlphaNumericLowerCase(
			_project.getName());

		substitutionMap.put("__PROJECT_ID__", projectId);

		pluginPackageProperties.put(
			Constants.BUNDLE_SYMBOLICNAME,
			StringUtil.suffixIfNotBlank(projectId, _virtualInstanceId));

		pluginPackageProperties.put(
			"name",
			StringUtil.suffixIfNotBlank(
				_project.getName(), _virtualInstanceId));

		pluginPackageProperties.putIfAbsent("module-group-id", "liferay");

		_writeToOutputFile(
			ResourceUtil.readString(
				ResourceUtil.getLocalFileResolver(getInputDockerfileFile()),
				ResourceUtil.getClassLoaderResolver(
					CreateClientExtensionConfigTask.class,
					_getTemplatePath(classificationGrouping, "Dockerfile"))),
			getDockerFile(), substitutionMap);
		_writeToOutputFile(
			_getLCPJsonFileContent(classificationGrouping), getLcpJsonFile(),
			substitutionMap);

		_addRequiredDeploymentContexts(
			pluginPackageProperties, getLcpJsonFile());

		_storePluginPackageProperties(pluginPackageProperties);

		_createClientExtensionConfigFile(jsonMap);
	}

	@OutputFile
	public File getClientExtensionConfigFile() {
		return GradleUtil.toFile(_project, _clientExtensionConfigFile);
	}

	@OutputFile
	public File getDockerFile() {
		return GradleUtil.toFile(_project, _dockerFile);
	}

	@InputFiles
	public File getInputDockerfileFile() {
		return GradleUtil.toFile(_project, "Dockerfile");
	}

	@InputFiles
	public File getInputLcpJsonFile() {
		return GradleUtil.toFile(_project, "LCP.json");
	}

	@InputFiles
	public File getInputPluginPackagePropertiesFile() {
		return GradleUtil.toFile(_project, "liferay-plugin-package.properties");
	}

	@OutputFile
	public File getLcpJsonFile() {
		return GradleUtil.toFile(_project, _lcpJsonFile);
	}

	@OutputFile
	public File getPluginPackagePropertiesFile() {
		return GradleUtil.toFile(_project, _pluginPackagePropertiesFile);
	}

	@Optional
	@OutputFile
	public File getSiteInitializerJsonFile() {
		return GradleUtil.toFile(_project, _siteInitializerJsonFile);
	}

	@Input
	public String getType() {
		return _type;
	}

	public void setDockerFile(Object dockerFile) {
		_dockerFile = dockerFile;
	}

	public void setLcpJsonFile(Object lcpJsonFile) {
		_lcpJsonFile = lcpJsonFile;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setVirtualInstanceId(String virtualInstanceId) {
		_virtualInstanceId = virtualInstanceId;
	}

	private void _addRequiredDeploymentContexts(
		Properties pluginPackageProperties, File lcpJsonFile) {

		try {
			JsonNode jsonNode = _objectMapper.readTree(lcpJsonFile);

			if (jsonNode.has("dependencies")) {
				List<String> dependencies = new ArrayList<>();

				for (JsonNode dependencyJsonNode :
						jsonNode.get("dependencies")) {

					dependencies.add(
						StringUtil.suffixIfNotBlank(
							dependencyJsonNode.textValue(),
							_virtualInstanceId));
				}

				pluginPackageProperties.put(
					"required-deployment-contexts",
					StringUtil.join(StringUtil.COMMA, dependencies));
			}
		}
		catch (IOException ioException) {
			throw new GradleException(
				"Unable to parse " + lcpJsonFile.getName(), ioException);
		}
	}

	private Provider<RegularFile> _addTaskOutputFile(String path) {
		ProjectLayout projectLayout = _project.getLayout();

		DirectoryProperty buildDirectoryProperty =
			projectLayout.getBuildDirectory();

		Path buildFilePath = Paths.get(_buildDir, path);

		Provider<RegularFile> buildFileProvider = buildDirectoryProperty.file(
			buildFilePath.toString());

		TaskOutputs taskOutputs = getOutputs();

		taskOutputs.files(buildFileProvider);

		return buildFileProvider;
	}

	private void _createClientExtensionConfigFile(Map<String, Object> jsonMap) {
		File clientExtensionConfigFile = getClientExtensionConfigFile();

		try {
			ObjectMapper objectMapper = new ObjectMapper();

			objectMapper.configure(
				SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

			ObjectWriter objectWriter =
				objectMapper.writerWithDefaultPrettyPrinter();

			String json = objectWriter.writeValueAsString(jsonMap);

			Files.write(clientExtensionConfigFile.toPath(), json.getBytes());
		}
		catch (Exception exception) {
			throw new GradleException(exception.getMessage(), exception);
		}
	}

	private void _createSiteInitializerJsonFile(
		ClientExtension clientExtension) {

		Map<String, Object> typeSettings = clientExtension.typeSettings;

		File siteInitializerJsonFile = getSiteInitializerJsonFile();

		try {
			HashMap<String, Object> jsonMap = new HashMap<>();

			for (Map.Entry<String, Object> entry : typeSettings.entrySet()) {
				String jsonMapKey =
					_typeSettingsToSiteInitializerJsonKeyMap.get(
						entry.getKey());

				if (jsonMapKey != null) {
					jsonMap.put(jsonMapKey, entry.getValue());
				}
			}

			ObjectMapper objectMapper = new ObjectMapper();

			objectMapper.configure(
				SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

			ObjectWriter objectWriter =
				objectMapper.writerWithDefaultPrettyPrinter();

			String json = objectWriter.writeValueAsString(jsonMap);

			Files.write(siteInitializerJsonFile.toPath(), json.getBytes());
		}
		catch (Exception exception) {
			throw new GradleException(exception.getMessage(), exception);
		}
	}

	private void _expandWildcards(Map<String, Object> typeSettings) {
		File clientExtensionBuildDir = new File(
			_project.getBuildDir(), _buildDir);

		File staticDir = new File(clientExtensionBuildDir, "static");

		if (!staticDir.exists()) {
			return;
		}

		Path staticDirPath = staticDir.toPath();

		for (Map.Entry<String, Object> entry : typeSettings.entrySet()) {
			Object currentValue = entry.getValue();

			String key = StringUtil.toLowerCase(entry.getKey());

			if (currentValue instanceof String) {
				String currentValueString = (String)currentValue;

				if (key.contains("url") &&
					_isWildcardValue(currentValueString)) {

					entry.setValue(
						_getMatchingPaths(staticDirPath, (String)currentValue));
				}
			}

			if (currentValue instanceof List) {
				List<String> values = new ArrayList<>();

				for (String value : (List<String>)currentValue) {
					if (key.contains("url") && _isWildcardValue(value)) {
						values.addAll(_getMatchingPaths(staticDirPath, value));
					}
					else {
						values.add(value);
					}
				}

				entry.setValue(values);
			}
		}
	}

	private String _getFileContent(File file) {
		if (file.exists()) {
			try {
				return new String(Files.readAllBytes(file.toPath()));
			}
			catch (IOException ioException) {
				throw new GradleException(
					ioException.getMessage(), ioException);
			}
		}

		return null;
	}

	private String _getIdOrBatchType(ClientExtension clientExtension) {
		String id = clientExtension.id;

		if (Objects.equals(clientExtension.getClassification(), "batch")) {
			id = "batch";
		}

		return id;
	}

	private String _getLCPJsonFileContent(String classificationGrouping) {
		String lcpJsonContent = ResourceUtil.readString(
			ResourceUtil.getLocalFileResolver(getInputLcpJsonFile()));
		String templateLCPJsonContent = ResourceUtil.readString(
			ResourceUtil.getClassLoaderResolver(
				CreateClientExtensionConfigTask.class,
				_getTemplatePath(classificationGrouping, "LCP.json")));

		if (StringUtil.isBlank(lcpJsonContent) &&
			StringUtil.isBlank(templateLCPJsonContent)) {

			return null;
		}

		if (StringUtil.isBlank(lcpJsonContent)) {
			return templateLCPJsonContent;
		}

		if (StringUtil.isBlank(templateLCPJsonContent)) {
			return lcpJsonContent;
		}

		try {
			Logger logger = _project.getLogger();

			if (logger.isInfoEnabled()) {
				logger.info("Merging LCP.json with the default values");
			}

			JsonNode templateJsonNode = _objectMapper.readTree(
				templateLCPJsonContent);

			JsonNodeUtil.overrideJsonNodeValues(
				templateJsonNode, _objectMapper.readTree(lcpJsonContent));

			ObjectWriter objectWriter =
				_objectMapper.writerWithDefaultPrettyPrinter();

			String content = objectWriter.writeValueAsString(templateJsonNode);

			if (logger.isInfoEnabled()) {
				File buildDir = _project.getBuildDir();

				File projectDir = _project.getProjectDir();

				Path projectDirPath = projectDir.toPath();

				logger.info(
					"See {}/{}/LCP.json for the merged file",
					projectDirPath.relativize(buildDir.toPath()), _buildDir);
			}

			return content;
		}
		catch (IOException ioException) {
			throw new GradleException("LCP.json is not valid JSON");
		}
	}

	private List<String> _getMatchingPaths(Path basePath, String glob) {
		FileSystem fileSystem = basePath.getFileSystem();

		AtomicReference<String> queryStringAtomicReference =
			new AtomicReference<>("");

		int index = glob.indexOf("?");

		if (index != -1) {
			queryStringAtomicReference.set(glob.substring(index));

			glob = glob.substring(0, index);
		}

		PathMatcher pathMatcher = fileSystem.getPathMatcher("glob:" + glob);

		try (Stream<Path> files = Files.walk(basePath)) {
			List<String> matchingPaths = files.map(
				basePath::relativize
			).filter(
				pathMatcher::matches
			).map(
				path -> path + queryStringAtomicReference.get()
			).collect(
				Collectors.toList()
			);

			if (matchingPaths.isEmpty()) {
				throw new GradleException(
					"No paths matched the glob pattern \"" + glob + "\"");
			}

			Collections.sort(matchingPaths);

			return matchingPaths;
		}
		catch (IOException ioException) {
			throw new GradleException(
				"Unable to expand wildcard paths", ioException);
		}
	}

	private Properties _getPluginPackageProperties() {
		Properties pluginPackageProperties = new Properties();

		try {
			String pluginPackagePropertiesFileContent = _getFileContent(
				getInputPluginPackagePropertiesFile());

			if (pluginPackagePropertiesFileContent != null) {
				pluginPackageProperties.load(
					new StringReader(pluginPackagePropertiesFileContent));
			}
		}
		catch (IOException ioException) {
			throw new GradleException(ioException.getMessage(), ioException);
		}

		return pluginPackageProperties;
	}

	private String _getTemplatePath(
		String classificationGrouping, String fileName) {

		return String.format(
			"dependencies/templates/%s/%s.tpl", classificationGrouping,
			fileName);
	}

	private void _inlineFrontendTokenDefinitionJSON(
		ClientExtension clientExtension) {

		Map<String, Object> typeSettings = clientExtension.typeSettings;

		Object frontendTokenDefinitionFile = typeSettings.remove(
			"frontendTokenDefinitionJSON");

		if (frontendTokenDefinitionFile == null) {
			return;
		}

		String json = ResourceUtil.readString(
			ResourceUtil.getLocalFileResolver(
				_project.file(frontendTokenDefinitionFile)));

		if (StringUtil.isBlank(json)) {
			json = "{}";
		}

		try {
			typeSettings.put(
				"frontendTokenDefinitionJSON",
				_objectMapper.writeValueAsString(
					_objectMapper.readValue(json, Map.class)));
		}
		catch (JsonParseException jsonParseException) {
			throw new GradleException(
				"Unable to JSON from file " + frontendTokenDefinitionFile,
				jsonParseException);
		}
		catch (JsonProcessingException jsonProcessingException) {
			throw new GradleException(
				"Unable to write JSON", jsonProcessingException);
		}
	}

	private boolean _isWildcardValue(String value) {
		if (value.contains(StringUtil.STAR) && !StringUtil.isUrl(value)) {
			return true;
		}

		return false;
	}

	private void _mapGlobalJSScriptElementAttributesToJSONString(
		ClientExtension clientExtension) {

		Map<String, Object> typeSettings = clientExtension.typeSettings;

		Map<String, Object> scriptElementAttributesMap =
			(Map<String, Object>)typeSettings.get("scriptElementAttributes");

		if (scriptElementAttributesMap == null) {
			return;
		}

		Set<Map.Entry<String, Object>> entrySet =
			scriptElementAttributesMap.entrySet();

		ObjectNode scriptElementAttributesObjectNode =
			_objectMapper.createObjectNode();

		for (Map.Entry<String, Object> entry : entrySet) {
			Object value = entry.getValue();

			if (value instanceof Boolean) {
				scriptElementAttributesObjectNode.put(
					entry.getKey(), (Boolean)value);
			}
			else {
				scriptElementAttributesObjectNode.put(
					entry.getKey(), (String)value);
			}
		}

		typeSettings.remove("scriptElementAttributes");

		typeSettings.put(
			"scriptElementAttributesJSON",
			scriptElementAttributesObjectNode.toString());
	}

	private void _processBatchJSONFile(File file) throws IOException {
		JsonNode rootJsonNode = _objectMapper.readTree(file);

		File parentFile = file.getParentFile();

		JsonNode configurationJsonNode = rootJsonNode.findValue(
			"configuration");

		if (configurationJsonNode == null) {
			return;
		}

		JsonNode classNameJsonNode = configurationJsonNode.findValue(
			"className");

		if ((classNameJsonNode == null) ||
			!Objects.equals(
				classNameJsonNode.asText(),
				"com.liferay.object.rest.dto.v1_0.ObjectEntry")) {

			return;
		}

		JsonNode itemsJsonNode = rootJsonNode.findValue("items");

		if (itemsJsonNode == null) {
			return;
		}

		boolean modified = false;

		for (JsonNode itemJsonNode : itemsJsonNode) {
			JsonNode externalReferenceCodeJsonNode = itemJsonNode.findValue(
				"externalReferenceCode");

			if (externalReferenceCodeJsonNode == null) {
				continue;
			}

			for (JsonNode childJsonNode : itemJsonNode) {
				if (!childJsonNode.isObject()) {
					continue;
				}

				JsonNode fileBase64JsonNode = childJsonNode.findValue(
					"fileBase64");

				if ((fileBase64JsonNode == null) ||
					!Objects.equals(
						fileBase64JsonNode.asText(),
						"@batch_object_entry_file_base64@")) {

					continue;
				}

				JsonNode nameJsonNode = childJsonNode.findValue("name");

				if (nameJsonNode == null) {
					throw new GradleException(
						String.format(
							"No name field found with token %s",
							"@batch_object_entry_file_base64@"));
				}

				File attachmentFile = new File(
					parentFile,
					String.format(
						"attachments/%s/%s",
						externalReferenceCodeJsonNode.asText(),
						nameJsonNode.asText()));

				if (!attachmentFile.exists()) {
					throw new GradleException(
						String.format(
							"Attachment file %s does not exist",
							attachmentFile));
				}

				ObjectNode objectNode = (ObjectNode)childJsonNode;

				objectNode.put(
					"fileBase64",
					_base64Encoder.encodeToString(
						Files.readAllBytes(attachmentFile.toPath())));

				modified = true;
			}
		}

		if (!modified) {
			return;
		}

		File projectDir = _project.getProjectDir();

		Path projectDirPath = projectDir.toPath();

		Path relativeTargetFilePath = projectDirPath.relativize(file.toPath());

		Path cxBuildDirPath = Paths.get(
			String.valueOf(_project.getBuildDir()), _buildDir);

		Path resolvedTargetPath = cxBuildDirPath.resolve(
			relativeTargetFilePath);

		ObjectWriter objectWriter = _objectMapper.writer();

		Files.write(
			resolvedTargetPath, objectWriter.writeValueAsBytes(rootJsonNode));

		Logger logger = getLogger();

		if (logger.isInfoEnabled()) {
			logger.info("Replaced base64 tokens in {}", file);
		}
	}

	private void _processBatchJSONFiles() {
		ConfigurableFileTree fileTree = _project.fileTree("batch");

		fileTree.include("**/*.json");

		for (File file : fileTree.getFiles()) {
			try {
				_processBatchJSONFile(file);
			}
			catch (IOException ioException) {
				throw new GradleException(
					String.format("Unable to read file %s", file));
			}
		}
	}

	private void _setLiferayVirtualInstanceId(
		Properties pluginPackageProperties) {

		if ((pluginPackageProperties != null) && (_virtualInstanceId != null)) {
			pluginPackageProperties.put(
				"Liferay-Virtual-Instance-Id", _virtualInstanceId);
		}
	}

	private void _storePluginPackageProperties(
		Properties pluginPackageProperties) {

		File pluginPackagePropertiesFile = getPluginPackagePropertiesFile();

		try {
			File parentFile = pluginPackagePropertiesFile.getParentFile();

			parentFile.mkdirs();

			BufferedWriter bufferedWriter = Files.newBufferedWriter(
				pluginPackagePropertiesFile.toPath(),
				StandardOpenOption.CREATE);

			pluginPackageProperties.store(bufferedWriter, null);
		}
		catch (IOException ioException) {
			throw new GradleException(ioException.getMessage(), ioException);
		}
	}

	private String _validateAndGetClassificationGrouping(
		Set<ClientExtension> clientExtensions) {

		Set<String> classifications = new HashSet<>();

		clientExtensions.forEach(
			clientExtension -> classifications.add(
				clientExtension.getClassification()));

		if (_groupConfiguration.containsAll(classifications)) {

			// Configuration must be first. The rest can be sorted.

			return "configuration";
		}

		if (_groupBatch.containsAll(classifications)) {
			Stream<ClientExtension> stream = clientExtensions.stream();

			Map<String, Long> typeCountMap = stream.collect(
				Collectors.groupingBy(
					clientExtension -> clientExtension.type,
					Collectors.counting()));

			long batchTypeCount = typeCountMap.getOrDefault("batch", 0L);
			long siteInitializerTypeCount = typeCountMap.getOrDefault(
				"siteInitializer", 0L);

			if ((batchTypeCount + siteInitializerTypeCount) > 1) {
				throw new GradleException(
					"A client extension project must not contain more than " +
						"one batch or siteInitializer type client extension");
			}

			Long oAuthApplicationHeadlessServerTypeCount =
				typeCountMap.getOrDefault("oAuthApplicationHeadlessServer", 0L);

			if (oAuthApplicationHeadlessServerTypeCount != 1) {
				throw new GradleException(
					"A batch or siteInitializer type client extension " +
						"requires exactly one oAuthApplicationHeadlessServer " +
							"type client extension");
			}

			return "batch";
		}
		else if (_groupFrontend.containsAll(classifications)) {
			return "frontend";
		}
		else if (_groupMicroservice.containsAll(classifications)) {
			return "microservice";
		}
		else if (!classifications.isEmpty()) {
			throw new GradleException(
				StringUtil.concat(
					"The combination of client extensions in ", classifications,
					" cannot be grouped in a single project. The following ",
					"groupings are allowed: ", _groupBatch, _groupFrontend,
					_groupMicroservice));
		}

		return "frontend";
	}

	private void _writeToOutputFile(
		String content, File outputFile, Map<String, String> substitutionMap) {

		if (content == null) {
			throw new GradleException(
				String.format(
					"Required file %s not found in project %s",
					StringUtil.quote(outputFile.getName()),
					StringUtil.quote(_project.getName())));
		}

		try {
			for (Map.Entry<String, String> entry : substitutionMap.entrySet()) {
				content = content.replace(entry.getKey(), entry.getValue());
			}

			Files.write(outputFile.toPath(), content.getBytes());
		}
		catch (IOException ioException) {
			throw new GradleException(
				String.format(
					"Unable to write file %s",
					StringUtil.quote(outputFile.getName())),
				ioException);
		}
	}

	private static final Set<String> _groupBatch = Sets.newHashSet(
		"batch", "configuration");
	private static final Set<String> _groupConfiguration = Sets.newHashSet(
		"configuration");
	private static final Set<String> _groupFrontend = Sets.newHashSet(
		"configuration", "frontend");
	private static final Set<String> _groupMicroservice = Sets.newHashSet(
		"configuration", "microservice");
	private static final Map<String, String>
		_typeSettingsToSiteInitializerJsonKeyMap =
			new HashMap<String, String>() {
				{
					put("builtInTemplateKey", "templateKey");
					put("builtInTemplateType", "templateType");
					put("membershipType", "membershipType");
					put("parentSiteKey", "parentSiteKey");
					put("siteExternalReferenceCode", "externalReferenceCode");
					put("siteName", "name");
					put("siteName_i18n", "name_i18n");
				}
			};

	private final Base64.Encoder _base64Encoder = Base64.getEncoder();
	private final String _buildDir;
	private final Object _clientExtensionConfigFile;
	private final Set<ClientExtension> _clientExtensions = new HashSet<>();
	private Object _dockerFile;
	private Object _lcpJsonFile;
	private final ObjectMapper _objectMapper = new ObjectMapper();
	private final Object _pluginPackagePropertiesFile;
	private final Project _project = getProject();
	private Object _siteInitializerJsonFile;
	private String _type = "frontend";
	private String _virtualInstanceId;

}