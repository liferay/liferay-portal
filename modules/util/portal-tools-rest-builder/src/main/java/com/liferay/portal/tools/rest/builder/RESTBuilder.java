/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.CamelCaseUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.StringUtil_IW;
import com.liferay.portal.kernel.util.TextFormatter;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.Validator_IW;
import com.liferay.portal.tools.ArgumentsUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.FreeMarkerTool;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.JavaMethodSignature;
import com.liferay.portal.tools.rest.builder.internal.freemarker.tool.java.parser.util.OpenAPIParserUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.ConfigUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.FreeMarkerUtil;
import com.liferay.portal.tools.rest.builder.internal.freemarker.util.OpenAPIUtil;
import com.liferay.portal.tools.rest.builder.internal.typescript.TypeScriptClientUtil;
import com.liferay.portal.tools.rest.builder.internal.util.FileUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.YAMLUtil;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.Application;
import com.liferay.portal.tools.rest.builder.internal.yaml.config.ConfigYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.exception.OpenAPIValidatorException;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Components;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Content;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Info;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Items;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.License;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.OpenAPIYAML;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Operation;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Parameter;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.PathItem;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.RequestBody;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Response;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.ResponseCode;
import com.liferay.portal.tools.rest.builder.internal.yaml.openapi.Schema;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.security.CodeSource;
import java.security.ProtectionDomain;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Peter Shin
 */
public class RESTBuilder {

	public static void main(String[] args) throws Exception {
		Map<String, String> arguments = null;

		try {
			arguments = ArgumentsUtil.parseArguments(args);
		}
		catch (IllegalArgumentException illegalArgumentException) {
		}

		if (arguments != null) {
			String restConfigDirName = arguments.get("rest.config.dirs");

			if (Validator.isNotNull(restConfigDirName)) {
				List<String> baselineTasks = _processRESTConfigFiles(
					restConfigDirName);

				String baselineOutputFileName = arguments.get(
					"rest.builder.baseline.output.file");

				if (Validator.isNotNull(baselineOutputFileName) &&
					!baselineTasks.isEmpty()) {

					Files.write(
						Paths.get(baselineOutputFileName),
						StringUtil.merge(
							baselineTasks, StringPool.SPACE
						).getBytes(
							StandardCharsets.UTF_8
						));
				}

				return;
			}
		}

		RESTBuilderArgs restBuilderArgs = new RESTBuilderArgs();

		JCommander jCommander = new JCommander(restBuilderArgs);

		try {
			ProtectionDomain protectionDomain =
				RESTBuilder.class.getProtectionDomain();

			CodeSource codeSource = protectionDomain.getCodeSource();

			URL url = codeSource.getLocation();

			File jarFile = new File(url.toURI());

			if (jarFile.isFile()) {
				jCommander.setProgramName("java -jar " + jarFile.getName());
			}
			else {
				jCommander.setProgramName(RESTBuilder.class.getName());
			}

			jCommander.parse(args);

			if (restBuilderArgs.isHelp()) {
				_printHelp(jCommander);
			}
			else {
				RESTBuilder restBuilder = new RESTBuilder(restBuilderArgs);

				restBuilder.build();
			}
		}
		catch (ParameterException parameterException) {
			_printHelp(jCommander);

			throw new RuntimeException(
				parameterException.getMessage(), parameterException);
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Error generating REST API\n" + exception.getMessage(),
				exception);
		}
	}

	public RESTBuilder(
			File copyrightFile, File configDir,
			Boolean forceClientVersionDescription,
			Boolean forcePredictableOperationId, String javaEEPackage)
		throws Exception {

		_copyrightFile = copyrightFile;

		_configDir = configDir.getCanonicalFile();

		File configFile = new File(_configDir, "rest-config.yaml");

		try {
			_configYAML = YAMLUtil.loadConfigYAML(
				_configDir.getPath(), configFile);

			if (forceClientVersionDescription != null) {
				_configYAML.setForceClientVersionDescription(
					forceClientVersionDescription);
			}

			if (_configYAML.getForceObjectMethodNameSuffix() == null) {
				_configYAML.setForceObjectMethodNameSuffix(
					ConfigUtil.isVersionCompatible(_configYAML, 9));
			}

			if (forcePredictableOperationId != null) {
				_configYAML.setForcePredictableOperationId(
					forcePredictableOperationId);
			}

			if (javaEEPackage != null) {
				_configYAML.setJavaEEPackage(javaEEPackage);
			}
		}
		catch (Exception exception) {
			throw new RuntimeException(
				"Error in file \"rest-config.yaml\": " +
					exception.getMessage());
		}
	}

	public RESTBuilder(RESTBuilderArgs restBuilderArgs) throws Exception {
		this(
			restBuilderArgs.getCopyrightFile(),
			restBuilderArgs.getRESTConfigDir(),
			restBuilderArgs.isForceClientVersionDescription(),
			restBuilderArgs.isForcePredictableOperationId(),
			restBuilderArgs.getJavaEEPackage());
	}

	public void build() throws Exception {
		_checkOpenAPIYAMLFiles();

		try (AutoCloseable closeable = build(null)) {
		}
	}

	public AutoCloseable build(ExecutorService executorService)
		throws Exception {

		Collection<Future<?>> futures = new ConcurrentLinkedQueue<>();

		FreeMarkerTool freeMarkerTool = FreeMarkerTool.getInstance();

		Map<String, Object> context = HashMapBuilder.<String, Object>put(
			"configYAML", _configYAML
		).put(
			"freeMarkerTool", freeMarkerTool
		).put(
			"stringUtil", StringUtil_IW.getInstance()
		).put(
			"validator", Validator_IW.getInstance()
		).build();

		_submit(
			executorService, futures, context,
			safeContext -> {
				if (_configYAML.isGenerateREST() &&
					(_configYAML.getApplication() != null)) {

					_createApplicationFile(safeContext);
				}

				if (Validator.isNotNull(_configYAML.getClientDir())) {
					_createClientAggregationFile(safeContext);
					_createClientBaseJSONParserFile(safeContext);
					_createClientFacetFile(safeContext);
					_createClientHttpInvokerFile(safeContext);
					_createClientPageFile(safeContext);
					_createClientPaginationFile(safeContext);
					_createClientPermissionFile(safeContext);
					_createClientProblemFile(safeContext);
					_createClientUnsafeSupplierFile(safeContext);
				}
			});

		boolean createClientCustomFieldFiles = true;
		boolean createClientScopeFiles = true;
		boolean createLiberalPermissionCheckerFile = true;
		List<String> validationErrorMessages = new ArrayList<>();

		for (File openAPIYAMLFile :
				FileUtil.getFiles(_configDir, "rest-openapi", ".yaml")) {

			String yamlString = FileUtil.read(openAPIYAMLFile);

			if (!_validateOpenAPIYAML(
					openAPIYAMLFile.getName(), yamlString,
					validationErrorMessages)) {

				continue;
			}

			OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(
				yamlString);

			Map<String, Schema> allSchemas = OpenAPIUtil.getAllSchemas(
				_configYAML, openAPIYAML);

			Map<String, Schema> allExternalSchemas =
				OpenAPIUtil.getAllExternalSchemas(_configYAML, openAPIYAML);

			context.put("allExternalSchemas", allExternalSchemas);

			context.put("allSchemas", allSchemas);

			String escapedVersion = OpenAPIUtil.escapeVersion(openAPIYAML);

			context.put("escapedVersion", escapedVersion);

			Map<String, Schema> globalEnumSchemas =
				OpenAPIUtil.getGlobalEnumSchemas(_configYAML, openAPIYAML);

			context.put("globalEnumSchemas", globalEnumSchemas);

			Map<String, String> javaDataTypeMap =
				OpenAPIParserUtil.getJavaDataTypeMap(_configYAML, openAPIYAML);

			context.put("javaDataTypeMap", javaDataTypeMap);

			context.put("openAPIYAML", openAPIYAML);

			if (_configYAML.isGenerateGraphQL() &&
				(_configYAML.getApplication() != null)) {

				_submit(
					executorService, futures, context,
					safeContext -> {
						_createGraphQLMutationFile(safeContext, escapedVersion);
						_createGraphQLQueryFile(safeContext, escapedVersion);
						_createGraphQLServletDataFile(
							safeContext, escapedVersion);
					});
			}

			context.put("schemaName", "openapi");

			if (_configYAML.isGenerateOpenAPI() &&
				(_configYAML.getResourceApplicationSelect() == null)) {

				_submit(
					executorService, futures, context,
					safeContext -> {
						_createOpenAPIResourceFile(safeContext, escapedVersion);
						_createPropertiesFile(
							safeContext, escapedVersion, "openapi");
					});
			}

			Map<String, Schema> schemas = freeMarkerTool.getSchemas(
				openAPIYAML);

			_createExternalSchemaFiles(
				allExternalSchemas, context, escapedVersion);

			for (Map.Entry<String, Schema> entry : allSchemas.entrySet()) {
				Schema schema = entry.getValue();
				String schemaName = entry.getKey();

				_putSchema(
					context, escapedVersion, javaDataTypeMap, schema,
					schemaName, Collections.emptySet());

				_submit(
					executorService, futures, context,
					safeContext -> {
						_createDTOFile(safeContext, escapedVersion, schemaName);

						if (Validator.isNotNull(_configYAML.getClientDir())) {
							_createClientDTOFile(
								safeContext, escapedVersion, schemaName);
							_createClientSerDesFile(
								safeContext, escapedVersion, schemaName);
						}
					});
			}

			for (Map.Entry<String, Schema> entry :
					globalEnumSchemas.entrySet()) {

				_putSchema(
					context, escapedVersion, javaDataTypeMap, entry.getValue(),
					entry.getKey(), Collections.emptySet());

				_submit(
					executorService, futures, context,
					safeContext -> {
						_createEnumFile(
							safeContext, escapedVersion, entry.getKey());

						if (Validator.isNotNull(_configYAML.getClientDir())) {
							_createClientEnumFile(
								safeContext, escapedVersion, entry.getKey());
						}
					});
			}

			schemas = freeMarkerTool.getAllSchemas(
				allExternalSchemas, openAPIYAML, schemas);

			for (Map.Entry<String, Schema> entry : schemas.entrySet()) {
				Schema schema = entry.getValue();
				String schemaName = entry.getKey();

				if (Validator.isNotNull(_configYAML.getClientDir())) {
					if (createClientCustomFieldFiles &&
						_containsVulcanCustomField(schema)) {

						_createClientCustomFieldFiles(context);

						createClientCustomFieldFiles = false;
					}

					if (createClientScopeFiles &&
						_containsVulcanScope(schema)) {

						_createClientScopeFile(context);

						createClientScopeFiles = false;
					}
				}

				List<JavaMethodSignature> javaMethodSignatures =
					freeMarkerTool.getResourceJavaMethodSignatures(
						_configYAML, openAPIYAML, schemaName);

				if (javaMethodSignatures.isEmpty()) {
					continue;
				}

				_putSchema(
					context, escapedVersion, javaDataTypeMap, schema,
					schemaName,
					_getRelatedSchemaNames(allSchemas, javaMethodSignatures));

				if (createLiberalPermissionCheckerFile) {
					_createLiberalPermissionCheckerFile(context);

					createLiberalPermissionCheckerFile = false;
				}

				_submit(
					executorService, futures, context,
					safeContext -> {
						_createBaseResourceImplFile(
							safeContext, escapedVersion, schemaName);
						_createPropertiesFile(
							safeContext, escapedVersion,
							String.valueOf(safeContext.get("schemaPath")));

						if (_configYAML.getApplication() != null) {
							_createResourceFactoryImplFile(
								safeContext, escapedVersion, schemaName);
						}

						_createResourceFile(
							safeContext, escapedVersion, schemaName);
						_createResourceImplFile(
							safeContext, escapedVersion, schemaName);

						if (Validator.isNotNull(_configYAML.getClientDir())) {
							_createClientResourceFile(
								safeContext, escapedVersion, schemaName);
						}

						if (Validator.isNotNull(_configYAML.getTestDir())) {
							_createBaseResourceTestCaseFile(
								safeContext, escapedVersion, schemaName);
							_createResourceTestFile(
								safeContext, escapedVersion, schemaName);
						}

						if (_configYAML.isGenerateActionProviders()) {
							_createBaseDTOActionMetadataProviderFile(
								safeContext, escapedVersion, schemaName);
							_createDTOActionMetadataProviderFile(
								safeContext, escapedVersion, schemaName);
							_createDTOActionProviderFile(
								safeContext, escapedVersion, schemaName);
						}
					});
			}

			if (_configYAML.isGenerateClientJS() &&
				Validator.isNotNull(_configYAML.getClientDir())) {

				TypeScriptClientUtil.generateTypeScriptClient(
					_configDir, _configYAML, _copyrightFile, yamlString);
			}
		}

		if (!validationErrorMessages.isEmpty()) {
			String validationErrorMessagesString = StringUtil.merge(
				validationErrorMessages, StringPool.NEW_LINE);

			throw new RuntimeException(
				"OpenAPI validation errors:\n" + validationErrorMessagesString);
		}

		return () -> {
			for (Future<?> future : futures) {
				future.get();
			}

			FileUtil.deleteFiles(_configYAML.getApiDir(), _files);

			if (Validator.isNotNull(_configYAML.getClientDir())) {
				FileUtil.deleteFiles(_configYAML.getClientDir(), _files);
			}

			FileUtil.deleteFiles(_configYAML.getImplDir(), _files);
			FileUtil.deleteFiles(
				_configYAML.getImplDir() + "/../resources/OSGI-INF/", _files);

			if (Validator.isNotNull(_configYAML.getTestDir())) {
				FileUtil.deleteFiles(_configYAML.getTestDir(), _files);
			}
		};
	}

	private static String _getBaselineTask(
		Path baseDirPath, Path restConfigYamlPath) {

		try {
			String content = new String(
				Files.readAllBytes(restConfigYamlPath), StandardCharsets.UTF_8);

			int index = content.indexOf("apiDir:");

			if (index == -1) {
				return null;
			}

			int startIndex = index + "apiDir:".length();

			int endIndex = content.indexOf('\n', startIndex);

			if (endIndex == -1) {
				endIndex = content.length();
			}

			String apiDirValue = content.substring(startIndex, endIndex);

			Path apiDirPath = restConfigYamlPath.resolveSibling(
				apiDirValue.trim());

			Path apiModuleDirPath = apiDirPath.getParent();

			while (apiModuleDirPath != null) {
				if (Files.exists(apiModuleDirPath.resolve("bnd.bnd"))) {
					break;
				}

				apiModuleDirPath = apiModuleDirPath.getParent();
			}

			if (apiModuleDirPath == null) {
				return null;
			}

			Path relativePath = baseDirPath.relativize(
				apiModuleDirPath.normalize());

			String gradleProjectPath = StringUtil.replace(
				relativePath.toString(), File.separatorChar, ':');

			return ":" + gradleProjectPath + ":baseline";
		}
		catch (IOException ioException) {
			return null;
		}
	}

	private static void _printHelp(JCommander jCommander) {
		jCommander.usage();
	}

	private static Map.Entry<RESTBuilder, AutoCloseable> _processRESTConfigFile(
			ExecutorService executorService, Path restConfigYamlPath)
		throws Exception {

		Path moduleDirPath = restConfigYamlPath.getParent();

		System.out.println("Processing " + moduleDirPath.getFileName());

		RESTBuilder restBuilder = new RESTBuilder(
			null, moduleDirPath.toFile(), null, null, null);

		return new AbstractMap.SimpleImmutableEntry<>(
			restBuilder, restBuilder.build(executorService));
	}

	private static List<String> _processRESTConfigFiles(String baseDirName)
		throws Exception {

		Path baseDirPath = Paths.get(baseDirName);

		Map<Path, Long> openAPIYAMLFileSizes = new HashMap<>();

		List<Path> restConfigYamlPaths = new ArrayList<>();

		Files.walkFileTree(
			baseDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dir, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String dirName = String.valueOf(dir.getFileName());

					if (dirName.equals("build") || dirName.equals("classes") ||
						dirName.equals("node_modules") ||
						dirName.equals("src") ||
						dirName.equals("test-classes") ||
						dirName.startsWith(".")) {

						return FileVisitResult.SKIP_SUBTREE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path file, BasicFileAttributes basicFileAttributes)
					throws IOException {

					if (!Objects.equals(
							String.valueOf(file.getFileName()),
							"rest-config.yaml") ||
						!Files.exists(file.resolveSibling("build.gradle"))) {

						return FileVisitResult.CONTINUE;
					}

					restConfigYamlPaths.add(file);

					openAPIYAMLFileSizes.put(
						file,
						Files.size(file.resolveSibling("rest-openapi.yaml")));

					return FileVisitResult.SKIP_SIBLINGS;
				}

			});

		if (restConfigYamlPaths.isEmpty()) {
			return Collections.emptyList();
		}

		restConfigYamlPaths.sort(
			Comparator.comparingLong(
				openAPIYAMLFileSizes::get
			).reversed());

		Runtime runtime = Runtime.getRuntime();

		ExecutorService executorService = Executors.newFixedThreadPool(
			runtime.availableProcessors());

		List<Future<?>> checkFutures = new ArrayList<>();

		for (Path restConfigYamlPath : restConfigYamlPaths) {
			checkFutures.add(
				executorService.submit(
					() -> {
						Path moduleDirPath = restConfigYamlPath.getParent();

						RESTBuilder restBuilder = new RESTBuilder(
							null, moduleDirPath.toFile(), null, null, null);

						restBuilder._checkOpenAPIYAMLFiles();

						return null;
					}));
		}

		for (Future<?> future : checkFutures) {
			future.get();
		}

		List<Future<Map.Entry<RESTBuilder, AutoCloseable>>> restBuilderFutures =
			new ArrayList<>();

		for (Path restConfigYamlPath : restConfigYamlPaths) {
			restBuilderFutures.add(
				executorService.submit(
					() -> _processRESTConfigFile(
						executorService, restConfigYamlPath)));
		}

		List<String> baselineTasks = new ArrayList<>();
		List<Exception> exceptions = new ArrayList<>();

		for (int i = 0; i < restBuilderFutures.size(); i++) {
			Future<Map.Entry<RESTBuilder, AutoCloseable>> future =
				restBuilderFutures.get(i);

			try {
				Map.Entry<RESTBuilder, AutoCloseable> entry = future.get();

				try (AutoCloseable autoCloseable = entry.getValue()) {
				}

				RESTBuilder restBuilder = entry.getKey();

				if (!restBuilder._hasApiModifications()) {
					continue;
				}

				Path restConfigYamlPath = restConfigYamlPaths.get(i);

				String baselineTask = _getBaselineTask(
					baseDirPath, restConfigYamlPath);

				if (baselineTask == null) {
					continue;
				}

				Path moduleDirPath = restConfigYamlPath.getParent();

				System.out.println(
					StringBundler.concat(
						"Baseline will be invoked for ",
						moduleDirPath.getFileName(), " via ", baselineTask));

				baselineTasks.add(baselineTask);
			}
			catch (ExecutionException executionException) {
				Throwable throwable = executionException.getCause();

				if (throwable instanceof Exception) {
					exceptions.add((Exception)throwable);
				}
				else {
					exceptions.add(executionException);
				}
			}
			catch (Exception exception) {
				exceptions.add(exception);
			}
		}

		executorService.shutdown();

		if (!exceptions.isEmpty()) {
			RuntimeException runtimeException = new RuntimeException(
				"Error processing REST config files");

			for (Exception exception : exceptions) {
				runtimeException.addSuppressed(exception);
			}

			throw runtimeException;
		}

		return baselineTasks;
	}

	private String _addClientVersionDescription(String yamlString) {
		String clientMavenGroupId = _getClientMavenGroupId(
			_configYAML.getApiPackagePath());
		String clientVersion = _getClientVersion();

		int licenseIndex = yamlString.indexOf("    license:");

		if ((clientMavenGroupId == null) || (clientVersion == null) ||
			(licenseIndex == -1)) {

			return yamlString;
		}

		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		Info info = openAPIYAML.getInfo();

		String description = info.getDescription();

		if (description == null) {
			return yamlString;
		}

		String clientMessage = StringBundler.concat(
			"A Java client JAR is available for use with the group ID '",
			clientMavenGroupId, "', artifact ID '",
			_configYAML.getApiPackagePath(), ".client', and version '");

		if (description.contains(clientMessage)) {
			description = StringUtil.removeSubstring(
				description,
				description.substring(description.indexOf(clientMessage)));
		}

		if (!description.isEmpty() && !description.endsWith(". ")) {
			description = StringBundler.concat(
				description, ". ", clientMessage, clientVersion, "'.");
		}
		else {
			description = StringBundler.concat(
				description, clientMessage, clientVersion, "'.");
		}

		String descriptionBlock = StringBundler.concat(
			"    description:\n", "        \"", description, "\"\n");

		return StringUtil.replace(
			yamlString,
			yamlString.substring(
				yamlString.indexOf(
					"    description:", yamlString.indexOf("info:")),
				licenseIndex),
			descriptionBlock);
	}

	private void _checkOpenAPIYAMLFile(FreeMarkerTool freeMarkerTool, File file)
		throws Exception {

		String originalYamlString = FileUtil.read(file);

		String yamlString = _fixOpenAPILicense(originalYamlString);

		yamlString = _fixOpenAPIPaths(yamlString);

		yamlString = _fixOpenAPIPathParameters(yamlString);

		if (_configYAML.isForcePredictableSchemaPropertyName()) {
			yamlString = _fixOpenAPISchemaPropertyNames(
				freeMarkerTool, yamlString);
		}

		if (_configYAML.isForcePredictableOperationId()) {
			yamlString = _fixOpenAPIOperationIds(
				_configYAML, freeMarkerTool, yamlString);
		}

		if (_configYAML.isForcePredictableContentApplicationXML()) {
			yamlString = _fixOpenAPIContentApplicationXML(yamlString);
		}

		if (_configYAML.isForceClientVersionDescription()) {
			yamlString = _addClientVersionDescription(yamlString);
		}

		if (_configYAML.isWarningsEnabled()) {
			_validate(yamlString);
		}

		if (!originalYamlString.equals(yamlString)) {
			FileUtil.write(file, yamlString, _modifiedFiles);
		}
	}

	private void _checkOpenAPIYAMLFiles() throws Exception {
		FreeMarkerTool freeMarkerTool = FreeMarkerTool.getInstance();

		for (File openAPIYAMLFile :
				FileUtil.getFiles(_configDir, "rest-openapi", ".yaml")) {

			try {
				_checkOpenAPIYAMLFile(freeMarkerTool, openAPIYAMLFile);
			}
			catch (Exception exception) {
				_log.error(exception);

				throw new RuntimeException(
					StringBundler.concat(
						"Error in file \"", openAPIYAMLFile.getName(), "\": ",
						exception.getMessage()));
			}
		}
	}

	private boolean _containsVulcanCustomField(Schema schema) {
		Map<String, Schema> propertySchemas = schema.getPropertySchemas();

		if (MapUtil.isEmpty(propertySchemas)) {
			return false;
		}

		for (Schema propertySchema : propertySchemas.values()) {
			if (Objects.equals(propertySchema.getType(), "array")) {
				Items items = propertySchema.getItems();

				if ((items != null) &&
					Objects.equals(items.getType(), "customField")) {

					return true;
				}
			}
			else if (Objects.equals(propertySchema.getType(), "customField")) {
				return true;
			}
		}

		return false;
	}

	private boolean _containsVulcanScope(Schema schema) {
		Map<String, Schema> propertySchemas = schema.getPropertySchemas();

		if (MapUtil.isEmpty(propertySchemas)) {
			return false;
		}

		for (Schema propertySchema : propertySchemas.values()) {
			if (Objects.equals(propertySchema.getType(), "scope")) {
				return true;
			}
		}

		return false;
	}

	private void _createApplicationFile(Map<String, Object> context)
		throws Exception {

		StringBundler sb = new StringBundler(6);

		sb.append(_configYAML.getImplDir());
		sb.append("/");
		sb.append(
			StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'));
		sb.append("/internal/jaxrs/application/");

		Application application = _configYAML.getApplication();

		sb.append(application.getClassName());

		sb.append(".java");

		File file = new File(sb.toString());

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "application",
			context, file, _modifiedFiles);
	}

	private void _createBaseDTOActionMetadataProviderFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/dto/", escapedVersion, "/action/metadata/Base",
				schemaName, "DTOActionMetadataProvider.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"base_dto_action_metadata_provider", context, file, _modifiedFiles);
	}

	private void _createBaseResourceImplFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/resource/", escapedVersion, "/Base", schemaName,
				"ResourceImpl.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"base_resource_impl", context, file, _modifiedFiles);
	}

	private void _createBaseResourceTestCaseFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getTestDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/resource/", escapedVersion, "/test/Base", schemaName,
				"ResourceTestCase.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"base_resource_test_case", context, file, _modifiedFiles);
	}

	private void _createClientAggregationFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "aggregation", "Aggregation", "client_aggregation");
	}

	private void _createClientBaseJSONParserFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "json", "BaseJSONParser", "client_base_json_parser");
	}

	private void _createClientCustomFieldFiles(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "custom/field", "CustomField", "client_custom_field");
		_createClientFile(
			context, "", "custom/field", "CustomValue", "client_custom_value");
		_createClientFile(context, "", "custom/field", "Geo", "client_geo");
	}

	private void _createClientDTOFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		_createClientFile(
			context, escapedVersion, "dto", schemaName, "client_dto");
	}

	private void _createClientEnumFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		_createClientFile(
			context, escapedVersion, "constant", schemaName, "client_enum");
	}

	private void _createClientFacetFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(context, "", "aggregation", "Facet", "client_facet");
	}

	private void _createClientFile(
			Map<String, Object> context, String escapedVersion,
			String javaDirName, String javaFileName, String templateName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getClientDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/client/", javaDirName, "/", escapedVersion, "/", javaFileName,
				".java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), templateName,
			context, file, _modifiedFiles);
	}

	private void _createClientHttpInvokerFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "http", "HttpInvoker", "client_http_invoker");
	}

	private void _createClientPageFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(context, "", "pagination", "Page", "client_page");
	}

	private void _createClientPaginationFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "pagination", "Pagination", "client_pagination");
	}

	private void _createClientPermissionFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "permission", "Permission", "client_permission");
	}

	private void _createClientProblemFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(context, "", "problem", "Problem", "client_problem");
	}

	private void _createClientResourceFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		_createClientFile(
			context, escapedVersion, "resource", schemaName + "Resource",
			"client_resource");
	}

	private void _createClientScopeFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(context, "", "scope", "Scope", "client_scope");
	}

	private void _createClientSerDesFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		_createClientFile(
			context, escapedVersion, "serdes", schemaName + "SerDes",
			"client_serdes");
	}

	private void _createClientUnsafeSupplierFile(Map<String, Object> context)
		throws Exception {

		_createClientFile(
			context, "", "function", "UnsafeSupplier",
			"client_unsafe_supplier");
	}

	private void _createDTOActionMetadataProviderFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/dto/", escapedVersion, "/action/metadata/",
				schemaName, "DTOActionMetadataProvider.java"));

		_files.add(file);

		if (file.exists()) {
			return;
		}

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"dto_action_metadata_provider", context, file, _modifiedFiles,
			false);
	}

	private void _createDTOActionProviderFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/dto/", escapedVersion, "/action/", schemaName,
				"DTOActionProvider.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"dto_action_provider", context, file, _modifiedFiles);
	}

	private void _createDTOFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getApiDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/dto/", escapedVersion, "/", schemaName, ".java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "dto", context,
			file, _modifiedFiles);
	}

	private void _createEnumFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getApiDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/constant/", escapedVersion, "/", schemaName, ".java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "enum", context,
			file, _modifiedFiles);
	}

	private void _createExternalSchemaFiles(
			Map<String, Schema> allExternalSchemas, Map<String, Object> context,
			String escapedVersion)
		throws Exception {

		for (Map.Entry<String, Schema> entry : allExternalSchemas.entrySet()) {
			String schemaName = entry.getKey();

			_putSchema(
				context, escapedVersion,
				Collections.singletonMap(schemaName, schemaName),
				entry.getValue(), schemaName, Collections.emptySet());

			if (Validator.isNotNull(_configYAML.getClientDir())) {
				_createClientDTOFile(context, escapedVersion, schemaName);
				_createClientSerDesFile(context, escapedVersion, schemaName);
			}
		}
	}

	private void _createGraphQLMutationFile(
			Map<String, Object> context, String escapedVersion)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/graphql/mutation/", escapedVersion,
				"/Mutation.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "graphql_mutation",
			context, file, _modifiedFiles);
	}

	private void _createGraphQLQueryFile(
			Map<String, Object> context, String escapedVersion)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/graphql/query/", escapedVersion, "/Query.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "graphql_query",
			context, file, _modifiedFiles);
	}

	private void _createGraphQLServletDataFile(
			Map<String, Object> context, String escapedVersion)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/graphql/servlet/", escapedVersion,
				"/ServletDataImpl.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"graphql_servlet_data", context, file, _modifiedFiles);
	}

	private void _createLiberalPermissionCheckerFile(
			Map<String, Object> context)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/security/permission/LiberalPermissionChecker.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"liberal_permission_checker", context, file, _modifiedFiles);
	}

	private void _createOpenAPIResourceFile(
			Map<String, Object> context, String escapedVersion)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/resource/", escapedVersion,
				"/OpenAPIResourceImpl.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"openapi_resource_impl", context, file, _modifiedFiles);
	}

	private void _createPropertiesFile(
			Map<String, Object> context, String escapedVersion,
			String schemaPath)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(),
				"/../resources/OSGI-INF/liferay/rest/", escapedVersion, "/",
				StringUtil.toLowerCase(schemaPath), ".properties"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			null, null, "properties", context, file, _modifiedFiles);
	}

	private void _createResourceFactoryImplFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/resource/", escapedVersion, "/factory/", schemaName,
				"ResourceFactoryImpl.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file),
			"resource_factory_impl", context, file, _modifiedFiles);
	}

	private void _createResourceFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getApiDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/resource/", escapedVersion, "/", schemaName,
				"Resource.java"));

		_files.add(file);

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "resource",
			context, file, _modifiedFiles);
	}

	private void _createResourceImplFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getImplDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/internal/resource/", escapedVersion, "/", schemaName,
				"ResourceImpl.java"));

		_files.add(file);

		if (file.exists()) {
			return;
		}

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "resource_impl",
			context, file, _modifiedFiles, false);
	}

	private void _createResourceTestFile(
			Map<String, Object> context, String escapedVersion,
			String schemaName)
		throws Exception {

		File file = new File(
			StringBundler.concat(
				_configYAML.getTestDir(), "/",
				StringUtil.replace(_configYAML.getApiPackagePath(), '.', '/'),
				"/resource/", escapedVersion, "/test/", schemaName,
				"ResourceTest.java"));

		_files.add(file);

		if (file.exists()) {
			return;
		}

		FreeMarkerUtil.processTemplate(
			_copyrightFile, FileUtil.getCopyrightYear(file), "resource_test",
			context, file, _modifiedFiles, false);
	}

	private String _fixOpenAPIContentApplicationXML(
		Map<String, Content> contents, int index, String s) {

		if (contents == null) {
			return s;
		}

		Set<String> mediaTypes = contents.keySet();

		if (!mediaTypes.contains("application/json") ||
			mediaTypes.contains("application/xml")) {

			return s;
		}

		StringBuilder sb = new StringBuilder();

		int startIndex =
			s.lastIndexOf("\n", s.indexOf("application/json", index)) + 1;

		int endIndex = _getLineEndIndex(s, startIndex);

		String line = s.substring(startIndex, endIndex);

		String leadingWhitespace = line.replaceAll("^(\\s+).+", "$1");

		while (line.startsWith(leadingWhitespace)) {
			sb.append(line);
			sb.append("\n");

			startIndex = endIndex + 1;

			endIndex = _getLineEndIndex(s, startIndex);

			line = s.substring(Math.min(startIndex, endIndex), endIndex);
		}

		sb.setLength(sb.length() - 1);

		String oldSub = sb.toString();

		String replacement = "\n";

		replacement += StringUtil.replace(
			oldSub, "application/json", "application/xml");

		return StringUtil.replaceFirst(s, oldSub, oldSub + replacement, index);
	}

	private String _fixOpenAPIContentApplicationXML(String yamlString) {
		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		Map<String, PathItem> pathItems = openAPIYAML.getPathItems();

		if (pathItems == null) {
			return yamlString;
		}

		for (Map.Entry<String, PathItem> entry1 : pathItems.entrySet()) {
			String path = entry1.getKey();

			int x = yamlString.indexOf(StringUtil.quote(path, '"') + ":");

			if (x == -1) {
				x = yamlString.indexOf(path + ":");
			}

			for (Operation operation :
					OpenAPIParserUtil.getOperations(entry1.getValue())) {

				RequestBody requestBody = operation.getRequestBody();

				String httpMethod = OpenAPIParserUtil.getHTTPMethod(operation);

				int y = yamlString.indexOf(httpMethod + ":", x);

				if (requestBody != null) {
					Map<String, Content> contents = requestBody.getContent();
					int index = yamlString.indexOf("requestBody:", y);

					yamlString = _fixOpenAPIContentApplicationXML(
						contents, index, yamlString);
				}

				Map<ResponseCode, Response> responses =
					operation.getResponses();

				for (Map.Entry<ResponseCode, Response> entry2 :
						responses.entrySet()) {

					Response response = entry2.getValue();

					if (response == null) {
						continue;
					}

					Map<String, Content> contents = response.getContent();

					int index = yamlString.indexOf(entry2.getKey() + ":", y);

					yamlString = _fixOpenAPIContentApplicationXML(
						contents, index, yamlString);
				}
			}
		}

		return yamlString;
	}

	private String _fixOpenAPILicense(String yamlString) {
		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		String licenseName = _configYAML.getLicenseName();
		String licenseURL = _configYAML.getLicenseURL();

		StringBundler licenseSB = new StringBundler(6);

		licenseSB.append("        name: \"");
		licenseSB.append(licenseName);
		licenseSB.append("\"\n");
		licenseSB.append("        url: \"");
		licenseSB.append(licenseURL);
		licenseSB.append("\"");

		Info info = openAPIYAML.getInfo();

		if (info == null) {
			return StringBundler.concat("info:\n", licenseSB, '\n', yamlString);
		}

		License license = info.getLicense();

		if ((license != null) && licenseName.equals(license.getName()) &&
			licenseURL.equals(license.getUrl())) {

			return yamlString;
		}

		int x = yamlString.indexOf("\ninfo:");

		int y = yamlString.indexOf('\n', x + 1);

		String line = yamlString.substring(
			y + 1, yamlString.indexOf("\n", y + 1));

		String leadingWhiteSpace = line.replaceAll("^(\\s+).+", "$1");

		Map<String, String> fieldMap = new TreeMap<>();

		String fieldName = "";
		String fieldValue = "";

		while (line.matches("^" + leadingWhiteSpace + ".*")) {
			if (line.matches("^" + leadingWhiteSpace + "\\w.*")) {
				if (Validator.isNotNull(fieldName)) {
					fieldMap.put(fieldName, fieldValue);

					fieldValue = "";
				}

				fieldName = line.replaceAll("^\\s+(\\w+):.*", "$1");
				fieldValue = line.replaceAll("^\\s+\\w+:\\s*(.*)\\s*", "$1");
			}
			else if (Validator.isNull(fieldValue)) {
				fieldValue = line;
			}
			else {
				fieldValue = fieldValue + '\n' + line;
			}

			if (yamlString.indexOf('\n', y + 1) == -1) {
				y = yamlString.length();

				break;
			}

			line = yamlString.substring(y + 1, yamlString.indexOf('\n', y + 1));

			y = yamlString.indexOf('\n', y + 1);
		}

		if (Validator.isNull(fieldName)) {
			return yamlString;
		}

		fieldMap.put(fieldName, fieldValue);

		fieldMap.put("license", licenseSB.toString());

		StringBundler sb = new StringBundler();

		sb.append(yamlString.substring(0, yamlString.indexOf('\n', x + 1) + 1));

		for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
			sb.append(leadingWhiteSpace);
			sb.append(entry.getKey());

			String value = entry.getValue();

			if (value.matches("(?s)^\\s*\\w+:.*")) {
				sb.append(":\n");
			}
			else {
				sb.append(": ");
			}

			sb.append(value);
			sb.append('\n');
		}

		sb.append(
			yamlString.substring(yamlString.lastIndexOf('\n', y - 1) + 1));

		return sb.toString();
	}

	private String _fixOpenAPIOperationIds(
			ConfigYAML configYAML, FreeMarkerTool freeMarkerTool,
			String yamlString)
		throws Exception {

		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		yamlString = yamlString.replaceAll("\n\\s+operationId:.+", "");

		Map<String, Schema> allExternalSchemas =
			OpenAPIUtil.getAllExternalSchemas(configYAML, openAPIYAML);
		Map<String, Schema> schemas = freeMarkerTool.getSchemas(openAPIYAML);

		MapUtil.merge(allExternalSchemas, schemas);

		for (String schemaName : schemas.keySet()) {
			Set<String> operationIds = new HashSet<>();

			List<JavaMethodSignature> javaMethodSignatures =
				freeMarkerTool.getResourceJavaMethodSignatures(
					_configYAML, openAPIYAML, schemaName);

			for (JavaMethodSignature javaMethodSignature :
					javaMethodSignatures) {

				Operation operation = javaMethodSignature.getOperation();

				String operationId = operation.getOperationId();

				if (operationId == null) {
					operationId = javaMethodSignature.getMethodName();
				}

				if (operationIds.contains(operationId) ||
					operationId.endsWith("Batch")) {

					continue;
				}

				operationIds.add(operationId);

				int x = yamlString.indexOf(
					StringUtil.quote(javaMethodSignature.getPath(), '"') + ":");

				if (x == -1) {
					x = yamlString.indexOf(
						" " + javaMethodSignature.getPath() + ":");

					x = x + 1;
				}

				String pathLine = yamlString.substring(
					yamlString.lastIndexOf("\n", x) + 1,
					yamlString.indexOf("\n", x));

				String httpMethod = OpenAPIParserUtil.getHTTPMethod(
					javaMethodSignature.getOperation());

				int y = yamlString.indexOf(httpMethod + ":", x);

				String httpMethodLine = yamlString.substring(
					yamlString.lastIndexOf("\n", y) + 1,
					yamlString.indexOf("\n", y));

				String leadingWhiteSpace =
					pathLine.replaceAll("^(\\s+).+", "$1") +
						httpMethodLine.replaceAll("^(\\s+).+", "$1");

				int z = yamlString.indexOf('\n', y);

				String line = yamlString.substring(
					z + 1, yamlString.indexOf("\n", z + 1));

				while (line.startsWith(leadingWhiteSpace)) {
					if (line.matches(leadingWhiteSpace + "\\w.*")) {
						String text = line.trim();

						if ((text.compareTo("operationId:") > 0) ||
							(yamlString.indexOf('\n', z + 1) == -1)) {

							break;
						}
					}

					z = yamlString.indexOf('\n', z + 1);

					line = yamlString.substring(
						z + 1, yamlString.indexOf("\n", z + 1));
				}

				yamlString = StringBundler.concat(
					yamlString.substring(0, z + 1), leadingWhiteSpace,
					"operationId: ", operationId, "\n",
					yamlString.substring(z + 1));
			}
		}

		return yamlString;
	}

	private String _fixOpenAPIPathParameters(String yamlString) {
		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		Map<String, PathItem> pathItems = openAPIYAML.getPathItems();

		if (pathItems == null) {
			return yamlString;
		}

		for (Map.Entry<String, PathItem> entry : pathItems.entrySet()) {
			String path = entry.getKey();

			int x = yamlString.indexOf(StringUtil.quote(path, '"') + ":");

			if (x == -1) {
				x = yamlString.indexOf(path + ":");
			}

			String pathLine = yamlString.substring(
				yamlString.lastIndexOf("\n", x) + 1,
				yamlString.indexOf("\n", x));

			// /blogs/{blog-id}/blogs --> /blogs/{blogId}/blogs

			for (Operation operation :
					OpenAPIParserUtil.getOperations(entry.getValue())) {

				int y = yamlString.indexOf(
					OpenAPIParserUtil.getHTTPMethod(operation) + ":", x);

				for (Parameter parameter : operation.getParameters()) {
					String in = parameter.getIn();
					String parameterName = parameter.getName();

					if (in.equals("path") && parameterName.contains("-")) {
						String newParameterName = CamelCaseUtil.toCamelCase(
							parameterName);

						int z = yamlString.indexOf(
							" " + parameterName + "\n", y);

						yamlString = StringBundler.concat(
							yamlString.substring(0, z + 1), newParameterName,
							"\n",
							yamlString.substring(
								z + parameterName.length() + 2));

						String newPathLine = StringUtil.replace(
							pathLine, "{" + parameterName + "}",
							"{" + newParameterName + "}");

						yamlString = StringUtil.replace(
							yamlString, pathLine, newPathLine);
					}
				}
			}

			// /blogs/{blogId}/blogs --> /blogs/{parentBlogId}/blogs

			List<String> pathSegments = new ArrayList<>();

			for (String pathSegment : path.split("/")) {
				if (Validator.isNotNull(pathSegment)) {
					pathSegments.add(pathSegment);
				}
			}

			if ((pathSegments.size() != 3) ||
				Objects.equals(pathSegments.get(1), "{id}") ||
				!StringUtil.startsWith(pathSegments.get(1), "{") ||
				!StringUtil.endsWith(pathSegments.get(1), "Id}")) {

				continue;
			}

			String selParameterName = pathSegments.get(1);

			selParameterName = selParameterName.substring(
				1, selParameterName.length() - 1);

			String text = CamelCaseUtil.fromCamelCase(selParameterName);

			text = OpenAPIUtil.formatPlural(
				_configYAML, text.substring(0, text.length() - 3));

			StringBuilder sb = new StringBuilder();

			sb.append('/');
			sb.append(text);
			sb.append('/');
			sb.append(pathSegments.get(1));
			sb.append('/');
			sb.append(text);

			if (!path.equals(sb.toString()) &&
				!path.equals(sb.toString() + "/")) {

				continue;
			}

			String newParameterName =
				"parent" + StringUtil.upperCaseFirstLetter(selParameterName);

			for (Operation operation :
					OpenAPIParserUtil.getOperations(entry.getValue())) {

				int y = yamlString.indexOf(
					OpenAPIParserUtil.getHTTPMethod(operation) + ":", x);

				for (Parameter parameter : operation.getParameters()) {
					String in = parameter.getIn();
					String parameterName = parameter.getName();

					if (in.equals("path") &&
						parameterName.equals(selParameterName)) {

						int z = yamlString.indexOf(
							" " + parameterName + "\n", y);

						sb.setLength(0);

						sb.append(yamlString.substring(0, z + 1));
						sb.append(newParameterName);
						sb.append("\n");
						sb.append(
							yamlString.substring(
								z + parameterName.length() + 2));

						yamlString = sb.toString();

						String newPathLine = StringUtil.replace(
							pathLine, "{" + parameterName + "}",
							"{" + newParameterName + "}");

						yamlString = StringUtil.replace(
							yamlString, pathLine, newPathLine);
					}
				}
			}

			String newPathLine = StringUtil.replace(
				pathLine, "{" + selParameterName + "}",
				"{" + newParameterName + "}");

			yamlString = StringUtil.replace(yamlString, pathLine, newPathLine);
		}

		return yamlString;
	}

	private String _fixOpenAPIPaths(String yamlString) {
		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		Map<String, PathItem> pathItems = openAPIYAML.getPathItems();

		if (pathItems == null) {
			return yamlString;
		}

		for (Map.Entry<String, PathItem> entry : pathItems.entrySet()) {
			String path = entry.getKey();

			if (!path.endsWith("/")) {
				continue;
			}

			String newPath = path.substring(0, path.length() - 1);

			int x = yamlString.indexOf(StringUtil.quote(path, '"') + ":");

			if (x != -1) {
				String newSub = StringUtil.quote(newPath, '"');
				String oldSub = StringUtil.quote(path, '"');

				yamlString = StringUtil.replaceFirst(
					yamlString, oldSub, newSub, x);

				continue;
			}

			x = yamlString.indexOf(path + ":");

			if (x != -1) {
				yamlString = StringUtil.replaceFirst(
					yamlString, path, newPath, x);
			}
		}

		return yamlString;
	}

	private String _fixOpenAPISchemaPropertyNames(
		FreeMarkerTool freeMarkerTool, String yamlString) {

		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		Map<String, Schema> schemas = freeMarkerTool.getSchemas(openAPIYAML);

		for (Map.Entry<String, Schema> entry1 : schemas.entrySet()) {
			Schema schema = entry1.getValue();

			Map<String, Schema> propertySchemas = schema.getPropertySchemas();

			if (propertySchemas == null) {
				continue;
			}

			for (Map.Entry<String, Schema> entry2 :
					propertySchemas.entrySet()) {

				Schema propertySchema = entry2.getValue();

				String description = propertySchema.getDescription();

				String reference = null;

				if (StringUtil.startsWith(
						description, "https://www.schema.org/")) {

					reference = description;
				}
				else if (propertySchema.getItems() != null) {
					Items items = propertySchema.getItems();

					reference = items.getReference();
				}

				if (reference == null) {
					continue;
				}

				String propertyName = entry2.getKey();
				String schemaVarName = _getSchemaVarName(
					freeMarkerTool, reference);

				int x = yamlString.indexOf(' ' + entry1.getKey() + ':');

				int y = yamlString.indexOf(' ' + entry2.getKey() + ':', x);

				int z = yamlString.indexOf(':', y);

				if (Objects.equals(propertySchema.getType(), "array")) {
					String plural = OpenAPIUtil.formatPlural(
						_configYAML, schemaVarName);

					if (propertyName.endsWith(
							StringUtil.upperCaseFirstLetter(plural)) &&
						propertyName.matches("[a-zA-Z]+")) {

						continue;
					}

					yamlString =
						yamlString.substring(0, y + 1) + plural +
							yamlString.substring(z);
				}
				else {
					if (propertyName.endsWith(
							StringUtil.upperCaseFirstLetter(schemaVarName)) &&
						propertyName.matches("[a-zA-Z]+")) {

						continue;
					}

					yamlString =
						yamlString.substring(0, y + 1) + schemaVarName +
							yamlString.substring(z);
				}
			}
		}

		return yamlString;
	}

	private String _getClientMavenGroupId(String apiPackagePath) {
		if (apiPackagePath.startsWith("com.liferay.commerce")) {
			return "com.liferay.commerce";
		}
		else if (apiPackagePath.startsWith("com.liferay")) {
			return "com.liferay";
		}

		return _configYAML.getClientMavenGroupId();
	}

	private String _getClientVersion() {
		try {
			String directory = StringUtil.removeSubstring(
				_configYAML.getClientDir(), "src/main/java");

			for (String line :
					Files.readAllLines(
						Paths.get(directory + "/bnd.bnd"),
						StandardCharsets.UTF_8)) {

				if (!line.startsWith("Bundle-Version: ")) {
					continue;
				}

				return StringUtil.removeSubstring(line, "Bundle-Version: ");
			}

			return null;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return null;
		}
	}

	private int _getLineEndIndex(String s, int startIndex) {
		int endIndex = s.indexOf("\n", startIndex);

		if (endIndex < 0) {
			endIndex = s.length();
		}

		return endIndex;
	}

	private Set<String> _getRelatedSchemaNames(
		Map<String, Schema> schemas,
		List<JavaMethodSignature> javaMethodSignatures) {

		Set<String> relatedSchemaNames = new TreeSet<>();

		for (JavaMethodSignature javaMethodSignature : javaMethodSignatures) {
			String returnType = javaMethodSignature.getReturnType();

			String[] returnTypeParts = returnType.split("\\.");

			if (returnTypeParts.length > 0) {
				String string = returnTypeParts[returnTypeParts.length - 1];

				if (!string.equals(javaMethodSignature.getSchemaName()) &&
					schemas.containsKey(string)) {

					relatedSchemaNames.add(string);
				}
			}
		}

		return relatedSchemaNames;
	}

	private String _getSchemaVarName(
		FreeMarkerTool freeMarkerTool, String reference) {

		int index = Math.max(
			reference.lastIndexOf('#'), reference.lastIndexOf('/'));

		return freeMarkerTool.getSchemaVarName(reference.substring(index + 1));
	}

	private boolean _hasApiModifications() {
		String apiDirName = _configYAML.getApiDir();

		for (File modifiedFile : _modifiedFiles) {
			String path = modifiedFile.getPath();

			if (path.startsWith(apiDirName)) {
				return true;
			}
		}

		return false;
	}

	private void _putSchema(
		Map<String, Object> context, String escapedVersion,
		Map<String, String> javaDataTypeMap, Schema schema, String schemaName,
		Set<String> relatedSchemaNames) {

		context.put("schema", schema);

		String javaType = javaDataTypeMap.get(schemaName);

		if (javaType == null) {
			context.put("schemaClientJavaType", "Object");
			context.put("schemaJavaType", "Object");
		}
		else {
			context.put(
				"schemaClientJavaType",
				StringBundler.concat(
					_configYAML.getApiPackagePath(), ".client.dto.",
					escapedVersion, ".", schemaName));
			context.put("schemaJavaType", javaType);
		}

		context.put("schemaName", schemaName);
		context.put(
			"schemaNames", OpenAPIUtil.formatPlural(_configYAML, schemaName));
		context.put(
			"schemaPath", TextFormatter.format(schemaName, TextFormatter.K));

		String schemaVarName = OpenAPIParserUtil.getSchemaVarName(schemaName);

		context.put("schemaVarName", schemaVarName);
		context.put(
			"schemaVarNames",
			OpenAPIUtil.formatPlural(_configYAML, schemaVarName));

		context.put("relatedSchemaNames", relatedSchemaNames);
	}

	private void _submit(
		ExecutorService executorService, Collection<Future<?>> futures,
		Map<String, Object> context,
		UnsafeConsumer<Map<String, Object>, Exception> unsafeConsumer) {

		if (executorService == null) {
			try {
				unsafeConsumer.accept(context);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}

			return;
		}

		Map<String, Object> safeContext = new HashMap<>(context);

		futures.add(
			executorService.submit(
				() -> {
					unsafeConsumer.accept(safeContext);

					return null;
				}));
	}

	private void _validate(String yamlString) {
		OpenAPIYAML openAPIYAML = OpenAPIParserUtil.loadOpenAPIYAML(yamlString);

		Components components = openAPIYAML.getComponents();

		if (components == null) {
			return;
		}

		Map<String, Schema> schemas = components.getSchemas();

		for (Map.Entry<String, Schema> entry1 : schemas.entrySet()) {
			Schema schema = entry1.getValue();

			Map<String, Schema> propertySchemas = schema.getPropertySchemas();

			if (propertySchemas == null) {
				continue;
			}

			for (Map.Entry<String, Schema> entry2 :
					propertySchemas.entrySet()) {

				Schema propertySchema = entry2.getValue();

				if (Objects.equals(propertySchema.getType(), "number") &&
					!Objects.equals(propertySchema.getFormat(), "bigdecimal") &&
					!Objects.equals(propertySchema.getFormat(), "double") &&
					!Objects.equals(propertySchema.getFormat(), "float")) {

					System.out.println(
						StringBundler.concat(
							"The property \"", entry1.getKey(), '.',
							entry2.getKey(),
							"\" should use \"type: integer\" instead of ",
							"\"type: number\""));
				}
			}

			if (schema.getRequiredPropertySchemaNames() == null) {
				continue;
			}

			List<String> requiredPropertySchemaNames =
				schema.getRequiredPropertySchemaNames();

			Set<String> propertySchemaNames = propertySchemas.keySet();

			for (String requiredPropertySchemaName :
					requiredPropertySchemaNames) {

				if (!propertySchemaNames.contains(requiredPropertySchemaName)) {
					System.out.println(
						StringBundler.concat(
							"The required property \"",
							requiredPropertySchemaName, "\" is not defined in ",
							entry1.getKey()));
				}
			}
		}
	}

	private boolean _validateOpenAPIYAML(
		String fileName, String yamlString, List<String> validationErrors) {

		try {
			YAMLUtil.validateOpenAPIYAML(fileName, yamlString);

			return true;
		}
		catch (OpenAPIValidatorException openAPIValidatorException) {
			validationErrors.add(openAPIValidatorException.getMessage());

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(RESTBuilder.class);

	private final File _configDir;
	private final ConfigYAML _configYAML;
	private final File _copyrightFile;
	private final Collection<File> _files = new ConcurrentLinkedQueue<>();
	private final Collection<File> _modifiedFiles =
		new ConcurrentLinkedQueue<>();

}