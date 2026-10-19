/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates;

import aQute.bnd.osgi.Constants;
import aQute.bnd.version.VersionRange;

import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.WorkspaceUtil;
import com.liferay.project.templates.util.FileTestUtil;
import com.liferay.project.templates.util.StringTestUtil;
import com.liferay.project.templates.util.XMLTestUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Andrea Di Giorgi
 */
public class ProjectTemplateFilesTest {

	@Test
	public void testProjectTemplateFiles() throws Exception {
		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		try (DirectoryStream<Path> directoryStream =
				FileTestUtil.getProjectTemplatesDirectoryStream()) {

			for (Path path : directoryStream) {
				_testProjectTemplateFiles(path, documentBuilder);
			}
		}
	}

	private static void _addXmlDeclaration(
			String fileName, String templateFileName)
		throws IOException {

		String template = FileTestUtil.read(
			"com/liferay/project/templates/dependencies/" + templateFileName);

		int pos = template.indexOf("[$XML$]");

		template = template.substring(0, pos);

		_xmlDeclarations.put(fileName, template);
	}

	private boolean _expressionContainedInList(
		Collection<String> list, String expression) {

		Stream<String> stream = list.stream();

		return stream.anyMatch(expression::contains);
	}

	private List<BuildGradleDependency> _getBuildGradleDependencies(
			Path buildGradlePath)
		throws Exception {

		List<BuildGradleDependency> buildGradleDependencies = new ArrayList<>();

		String buildGradle = new String(
			Files.readAllBytes(buildGradlePath), StandardCharsets.UTF_8);

		Matcher matcher = _buildGradleDependencyPattern.matcher(buildGradle);

		while (matcher.find()) {
			String configuration = matcher.group(1);
			String dependencyGroup = matcher.group(2);
			String dependencyName = matcher.group(3);
			String dependencyVersion = matcher.group(5);

			boolean provided = false;

			if (configuration.equals("compileOnly")) {
				provided = true;
			}

			buildGradleDependencies.add(
				new BuildGradleDependency(
					dependencyGroup, dependencyName, dependencyVersion,
					provided));
		}

		return buildGradleDependencies;
	}

	private boolean _isInJavaSrcDir(Path path) throws IOException {
		path = path.toRealPath();

		String pathString = path.toString();

		if (File.separatorChar != '/') {
			pathString = pathString.replace(File.separatorChar, '/');
		}

		for (String sourceSetName : _SOURCESET_NAMES) {
			if (pathString.contains("/src/" + sourceSetName + "/java/")) {
				return true;
			}
		}

		return false;
	}

	private boolean _isTextFile(String fileName, String extension) {
		if (fileName.equals("gitignore") ||
			_textFileExtensions.contains(extension)) {

			return true;
		}

		return false;
	}

	private void _testArchetypeMetadataXml(
			Path projectTemplateDirPath, String projectTemplateDirName,
			Path archetypeResourcesDirPath, Properties bndProperties,
			boolean requireAuthorProperty, String archetypePostGenerateGroovy,
			Set<String> archetypeResourceExpressions,
			DocumentBuilder documentBuilder)
		throws Exception {

		Path archetypeMetadataXmlPath = projectTemplateDirPath.resolve(
			"src/main/resources/META-INF/maven/archetype-metadata.xml");

		Assert.assertTrue(
			"Missing " + archetypeMetadataXmlPath,
			Files.exists(archetypeMetadataXmlPath));

		String archetypeDescriptorName = projectTemplateDirName.substring(
			FileTestUtil.PROJECT_TEMPLATE_DIR_PREFIX.length());

		if (archetypeDescriptorName.equals(WorkspaceUtil.WORKSPACE)) {
			archetypeDescriptorName = "liferay-" + archetypeDescriptorName;
		}
		else {
			archetypeDescriptorName =
				"liferay-module-" + archetypeDescriptorName;
		}

		String archetypeMetadataXml = FileUtil.read(archetypeMetadataXmlPath);

		Assert.assertTrue(
			"Incorrect archetype descriptor name in " +
				archetypeMetadataXmlPath,
			archetypeMetadataXml.startsWith(
				"<?xml version=\"1.0\"?>\n\n<archetype-descriptor name=\"" +
					archetypeDescriptorName + "\">"));

		Matcher matcher = _archetypeMetadataXmlIncludePattern.matcher(
			archetypeMetadataXml);

		while (matcher.find()) {
			String fileName = matcher.group(1);

			if (fileName.equals(".gitignore")) {
				fileName = "gitignore";
			}

			Path path = archetypeResourcesDirPath.resolve(fileName);

			Assert.assertTrue(
				"Included file " + path + " in " + archetypeMetadataXmlPath +
					" not found",
				Files.isRegularFile(path));
		}

		List<String> requiredPropertyNames = new ArrayList<>();

		matcher = _archetypeMetadataXmlRequiredPropertyPattern.matcher(
			archetypeMetadataXml);

		while (matcher.find()) {
			String name = matcher.group(1);

			if (!requiredPropertyNames.isEmpty()) {
				String previousName = requiredPropertyNames.get(
					requiredPropertyNames.size() - 1);

				Assert.assertTrue(
					"Required properties in " + archetypeMetadataXmlPath +
						" are duplicated or not sorted",
					name.compareTo(previousName) > 0);
			}

			Assert.assertFalse(
				"Forbidden \"" + name + "\" property in " +
					archetypeMetadataXmlPath,
				_archetypeMetadataXmlDefaultPropertyNames.contains(name));

			requiredPropertyNames.add(name);
		}

		boolean authorProperty = requiredPropertyNames.contains("author");

		if (requireAuthorProperty) {
			Assert.assertTrue(
				"Missing \"author\" required property in " +
					archetypeMetadataXmlPath,
				authorProperty);
		}
		else {
			Assert.assertFalse(
				"Forbidden \"author\" required property in " +
					archetypeMetadataXmlPath,
				authorProperty);
		}

		for (String name : requiredPropertyNames) {
			Stream<String> stream = archetypeResourceExpressions.stream();

			boolean anyMatch = stream.anyMatch(
				expression -> {
					if (expression.contains(name) ||
						archetypePostGenerateGroovy.contains(
							"request.properties.get(\"" + name + "\")")) {

						return true;
					}

					return false;
				});

			String message =
				"Unused \"" + name + "\" required property in " +
					archetypeMetadataXmlPath;

			Assert.assertTrue(message, anyMatch);
		}

		requiredPropertyNames.addAll(_archetypeMetadataXmlDefaultPropertyNames);

		List<Path> definitionsVmPaths = new ArrayList<>();

		Path definitionsVmPath = projectTemplateDirPath.resolve(
			"src/main/resources/definitions.vm");

		if (Files.exists(definitionsVmPath)) {
			definitionsVmPaths.add(definitionsVmPath);
		}

		String includeResource = bndProperties.getProperty(
			Constants.INCLUDERESOURCE);

		if (Validator.isNotNull(includeResource)) {
			for (String fileName : includeResource.split(",")) {
				if (!fileName.endsWith("/definitions.vm")) {
					continue;
				}

				definitionsVmPath = projectTemplateDirPath.resolve(fileName);

				if (!Files.exists(definitionsVmPath)) {
					continue;
				}

				definitionsVmPaths.add(definitionsVmPath);
			}
		}

		Set<String> declaredVariables = new HashSet<>();

		StringBuilder messageSuffix = new StringBuilder(
			archetypeMetadataXmlPath.toString());

		for (int i = 0; i < definitionsVmPaths.size(); i++) {
			definitionsVmPath = definitionsVmPaths.get(i);

			String definitionsVm = FileUtil.read(definitionsVmPath);

			matcher = _velocitySetDirectivePattern.matcher(definitionsVm);

			while (matcher.find()) {
				declaredVariables.add(matcher.group(1));
			}

			messageSuffix.append(", ");

			if (i != (definitionsVmPaths.size() - 1)) {
				continue;
			}

			messageSuffix.append("or ");
		}

		Path archetypePomXmlPath = projectTemplateDirPath.resolve(
			"src/main/resources/archetype-resources/pom.xml");

		String pathString = archetypePomXmlPath.toString();

		if (pathString.contains("ext")) {
			return;
		}

		Document document = documentBuilder.parse(archetypePomXmlPath.toFile());

		Element projectElement = document.getDocumentElement();

		Element propertiesElement = XMLTestUtil.getChildElement(
			projectElement, "properties");

		List<Element> propertyElements = XMLTestUtil.getChildElements(
			propertiesElement);

		for (Element element : propertyElements) {
			declaredVariables.add(element.getNodeName());
		}

		for (String expression : archetypeResourceExpressions) {
			Assert.assertTrue(
				"Undeclared \"" + expression + "\" property. Please add to " +
					archetypeMetadataXmlPath,
				_expressionContainedInList(declaredVariables, expression) ||
				_expressionContainedInList(requiredPropertyNames, expression));
		}
	}

	private String _testArchetypePostGenerateGroovy(Path projectTemplateDirPath)
		throws Exception {

		Path path = projectTemplateDirPath.resolve(
			"src/main/resources/META-INF/archetype-post-generate.groovy");

		if (Files.notExists(path)) {
			return "";
		}

		return _testTextFile(path);
	}

	private Properties _testBndBnd(Path projectTemplateDirPath)
		throws Exception {

		Path bndBndPath = projectTemplateDirPath.resolve("bnd.bnd");

		Properties properties = FileUtil.readProperties(bndBndPath);

		String bundleDescription = properties.getProperty(
			Constants.BUNDLE_DESCRIPTION);

		Assert.assertTrue(
			"Missing \"" + Constants.BUNDLE_DESCRIPTION + "\" header in " +
				bndBndPath,
			Validator.isNotNull(bundleDescription));

		Matcher matcher = _bundleDescriptionPattern.matcher(bundleDescription);

		Assert.assertTrue(
			"Header \"" + Constants.BUNDLE_DESCRIPTION + "\" in " + bndBndPath +
				" must match pattern \"" + _bundleDescriptionPattern.pattern() +
					"\"",
			matcher.matches());

		String liferayVersions = properties.getProperty("Liferay-Versions");

		Assert.assertTrue(
			"Missing \"Liferay Versions\" header in " + bndBndPath,
			Validator.isNotNull(liferayVersions));

		VersionRange versionRange = new VersionRange(liferayVersions);

		Assert.assertTrue(
			"\"Liferay-Versions\" header in " + bndBndPath + " must be a " +
				"valid OSGi version range",
			versionRange.isRange());

		return properties;
	}

	private void _testBuildGradle(Path archetypeResourcesDirPath)
		throws Exception {

		Path buildGradlePath = archetypeResourcesDirPath.resolve(
			"build.gradle");

		Assert.assertTrue(
			"Missing " + buildGradlePath, Files.exists(buildGradlePath));

		String buildGradle = FileUtil.read(buildGradlePath);

		Assert.assertFalse(
			buildGradlePath + " contains \"latest.release\". Please use a " +
				"tokenized version from /modules/build.gradle",
			buildGradle.contains("latest.release"));

		Assert.assertFalse(
			buildGradlePath + " contains \"mavenLocal()\". Please remove it.",
			buildGradle.contains("mavenLocal()"));
	}

	private void _testGitIgnore(
			String projectTemplateDirName, Path archetypeResourcesDirPath)
		throws Exception {

		Path dotGitIgnorePath = archetypeResourcesDirPath.resolve(".gitignore");
		Path gitIgnorePath = archetypeResourcesDirPath.resolve("gitignore");

		Assert.assertFalse(
			"Rename " + dotGitIgnorePath + " to " + gitIgnorePath +
				" to bypass GRADLE-1883",
			Files.exists(dotGitIgnorePath));

		Assert.assertTrue(
			"Missing " + gitIgnorePath, Files.exists(gitIgnorePath));

		if (!projectTemplateDirName.equals(
				FileTestUtil.PROJECT_TEMPLATE_DIR_PREFIX +
					WorkspaceUtil.WORKSPACE)) {

			Set<String> gitIgnoreLines = new TreeSet<>(_gitIgnoreLines);

			if (Files.exists(
					archetypeResourcesDirPath.resolve("package.json"))) {

				gitIgnoreLines.add("node_modules/");
			}

			if (FileTestUtil.containsFile(
					archetypeResourcesDirPath, "*.scss")) {

				gitIgnoreLines.add(".sass-cache/");
			}

			Assert.assertEquals(
				"Incorrect " + gitIgnorePath,
				StringTestUtil.merge(gitIgnoreLines, '\n'),
				FileUtil.read(gitIgnorePath));
		}
	}

	private void _testGradleWrapper(Path archetypeResourcesDirPath) {
		Assert.assertFalse(
			"Forbidden Gradle Wrapper in " + archetypeResourcesDirPath,
			Files.exists(archetypeResourcesDirPath.resolve("gradlew")));
	}

	private void _testLanguageProperties(Path path) throws IOException {
		try (BufferedReader bufferedReader = Files.newBufferedReader(
				path, StandardCharsets.UTF_8)) {

			String line = null;
			String previousKey = null;

			while ((line = bufferedReader.readLine()) != null) {
				Assert.assertFalse(
					"Forbidden empty line in " + path, line.isEmpty());
				Assert.assertFalse(
					"Forbidden comments in " + path, line.startsWith("##"));
				Assert.assertFalse(
					"Forbidden whitespace leading character in " + path,
					Character.isWhitespace(line.charAt(0)));

				if (line.startsWith("#")) {
					continue;
				}

				int pos = line.indexOf('=');

				Assert.assertNotEquals(
					"Incorrect line \"" + line + "\" in " + path, -1, pos);

				String key = line.substring(0, pos);

				Assert.assertTrue(
					path + " contains duplicate lines or is not sorted",
					(previousKey == null) ||
					(_languagePropertiesKeyComparator.compare(
						key, previousKey) > 0));

				previousKey = key;
			}
		}
	}

	private void _testLiferayPluginPackageProperties(Path path)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		try (BufferedReader bufferedReader = Files.newBufferedReader(
				path, StandardCharsets.UTF_8)) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith("#set")) {
					continue;
				}

				line = line.replace("${symbol_escape}", "\\");
				line = line.replace("${symbol_pound}", "#");

				if (sb.length() > 0) {
					sb.append('\n');
				}

				sb.append(line);
			}
		}

		Properties properties = new Properties();

		properties.load(new StringReader(sb.toString()));

		_testPropertyValue(path, properties, "author", "${author}");
		_testPropertyValue(path, properties, "change-log", "");
		_testPropertyValue(path, properties, "licenses", "LGPL");
		_testPropertyValue(
			path, properties, "liferay-versions",
			"7.0.0+,7.1.0+,7.2.0+,7.3.0+,7.4.0+");
		_testPropertyValue(path, properties, "long-description", "");
		_testPropertyValue(path, properties, "module-group-id", "liferay");
		_testPropertyValue(path, properties, "module-incremental-version", "1");
		_testPropertyValue(path, properties, "name", "${artifactId}");
		_testPropertyValue(
			path, properties, "page-url", "http://www.liferay.com");
		_testPropertyValue(path, properties, "short-description", "");
		_testPropertyValue(path, properties, "tags", "");
	}

	private void _testMavenWrapper(Path archetypeResourcesDirPath) {
		Assert.assertFalse(
			"Forbidden Maven Wrapper in " + archetypeResourcesDirPath,
			Files.exists(archetypeResourcesDirPath.resolve("mvnw")));
	}

	private void _testPomXml(
			Path archetypeResourcesDirPath, DocumentBuilder documentBuilder)
		throws Exception {

		Path pomXmlPath = archetypeResourcesDirPath.resolve("pom.xml");

		Assert.assertTrue("Missing " + pomXmlPath, Files.exists(pomXmlPath));

		Document document = documentBuilder.parse(pomXmlPath.toFile());

		Element projectElement = document.getDocumentElement();

		Element packagingElement = XMLTestUtil.getChildElement(
			projectElement, "packaging");

		if (packagingElement != null) {
			Assert.assertNotEquals(
				"Incorrect packaging in " + pomXmlPath, "jar",
				packagingElement.getTextContent());
		}

		Element propertiesElement = XMLTestUtil.getChildElement(
			projectElement, "properties");

		Assert.assertNotNull(
			"Missing \"properties\" element in " + pomXmlPath,
			propertiesElement);

		String sourceEncoding = null;

		Element sourceEncodingElement = XMLTestUtil.getChildElement(
			propertiesElement, "project.build.sourceEncoding");

		if (sourceEncodingElement != null) {
			sourceEncoding = sourceEncodingElement.getTextContent();
		}

		Assert.assertEquals(
			"Incorrect property \"project.build.sourceEncoding\" in " +
				pomXmlPath,
			sourceEncoding, StandardCharsets.UTF_8.name());

		NodeList executionNodeList = projectElement.getElementsByTagName(
			"execution");

		for (int i = 0; i < executionNodeList.getLength(); i++) {
			Element executionElement = (Element)executionNodeList.item(i);

			Element idElement = XMLTestUtil.getChildElement(
				executionElement, "id");

			if (idElement == null) {
				continue;
			}

			String id = idElement.getTextContent();

			Assert.assertFalse(
				"Execution ID \"" + id + "\" in " + pomXmlPath +
					" cannot start with \"default-\"",
				id.startsWith("default-"));

			Matcher matcher = _pomXmlExecutionIdPattern.matcher(id);

			Assert.assertTrue(
				"Execution ID \"" + id + "\" in " + pomXmlPath +
					" must match pattern \"" +
						_pomXmlExecutionIdPattern.pattern() + "\"",
				matcher.matches());
		}

		Path buildGradlePath = archetypeResourcesDirPath.resolve(
			"build.gradle");

		List<BuildGradleDependency> buildGradleDependencies =
			_getBuildGradleDependencies(buildGradlePath);

		Element dependenciesElement = XMLTestUtil.getChildElement(
			projectElement, "dependencies");

		List<Element> dependencyElements;

		if (dependenciesElement != null) {
			dependencyElements = XMLTestUtil.getChildElements(
				dependenciesElement);
		}
		else {
			dependencyElements = Collections.emptyList();
		}

		for (int i = 0; i < buildGradleDependencies.size(); i++) {
			BuildGradleDependency buildGradleDependency =
				buildGradleDependencies.get(i);

			Element dependencyElement = dependencyElements.get(i);

			List<Element> dependencyChildElements =
				XMLTestUtil.getChildElements(dependencyElement);

			String dependencyElementString = XMLTestUtil.toString(
				dependencyElement);

			XMLTestUtil.testXmlElement(
				pomXmlPath, dependencyElementString, dependencyChildElements, 0,
				"groupId", buildGradleDependency.group);
			XMLTestUtil.testXmlElement(
				pomXmlPath, dependencyElementString, dependencyChildElements, 1,
				"artifactId", buildGradleDependency.name);

			if (buildGradleDependency.version != null) {
				XMLTestUtil.testXmlElement(
					pomXmlPath, dependencyElementString,
					dependencyChildElements, 2, "version",
					buildGradleDependency.version);

				if (buildGradleDependency.provided) {
					XMLTestUtil.testXmlElement(
						pomXmlPath, dependencyElementString,
						dependencyChildElements, 3, "scope", "provided");
				}
			}
			else if (buildGradleDependency.provided) {
				XMLTestUtil.testXmlElement(
					pomXmlPath, dependencyElementString,
					dependencyChildElements, 2, "scope", "provided");
			}
		}

		_testPomXmlVersions(pomXmlPath, projectElement, "dependency");
		_testPomXmlVersions(pomXmlPath, projectElement, "plugin");
	}

	private void _testPomXmlVersions(
		Path pomXmlPath, Element projectElement, String name) {

		Properties systemProperties = System.getProperties();

		NodeList nodeList = projectElement.getElementsByTagName(name);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element)nodeList.item(i);

			Element artifactIdElement = XMLTestUtil.getChildElement(
				element, "artifactId");

			String artifactId = artifactIdElement.getTextContent();

			String pomXml = pomXmlPath.toString();

			if (artifactId.equals("com.liferay.css.builder") &&
				pomXml.contains("project-templates-theme/")) {

				continue;
			}

			String key = artifactId + ".version";

			if (!systemProperties.containsKey(key)) {
				continue;
			}

			Element versionElement = XMLTestUtil.getChildElement(
				element, "version");

			Assert.assertEquals(
				"Incorrect version of " + name + " \"" + artifactId + "\" in " +
					pomXmlPath,
				"@" + key + "@", versionElement.getTextContent());
		}
	}

	private void _testProjectTemplateCustomizer(
			String projectTemplateDirName, Path projectTemplateDirPath)
		throws Exception {

		Path projectTemplateCustomizerPath = projectTemplateDirPath.resolve(
			"src/main/resources/META-INF/services" +
				"/com.liferay.project.templates.ProjectTemplateCustomizer");

		if (Files.notExists(projectTemplateCustomizerPath)) {
			return;
		}

		StringBuffer sb = new StringBuffer();

		sb.append("com.liferay.");
		sb.append(projectTemplateDirName.replace('-', '.'));
		sb.append(".internal.");

		Path bndBndPath = projectTemplateDirPath.resolve("bnd.bnd");

		Properties properties = FileUtil.readProperties(bndBndPath);

		String bundleName = properties.getProperty(Constants.BUNDLE_NAME);

		bundleName = bundleName.replaceAll("[^\\w\\s]", "");

		Matcher matcher = _bundleNameSeparatorPattern.matcher(
			bundleName.substring(
				FileTestUtil.PROJECT_TEMPLATE_BUNDLE_NAME_PREFIX.length()));

		while (matcher.find()) {
			String initial = matcher.group(1);

			matcher.appendReplacement(sb, initial.toUpperCase());
		}

		matcher.appendTail(sb);

		sb.append("ProjectTemplateCustomizer");

		String className = sb.toString();

		Assert.assertEquals(
			"Incorrect " + projectTemplateCustomizerPath, className,
			FileUtil.read(projectTemplateCustomizerPath));

		Path path = projectTemplateDirPath.resolve(
			"src/main/java/" + className.replace('.', '/') + ".java");

		Assert.assertTrue("Missing " + path, Files.exists(path));
	}

	private void _testProjectTemplateFiles(
			Path projectTemplateDirPath, DocumentBuilder documentBuilder)
		throws Exception {

		Path archetypeResourcesDirPath = projectTemplateDirPath.resolve(
			"src/main/resources/archetype-resources");

		Assert.assertTrue(
			"Missing " + archetypeResourcesDirPath,
			Files.isDirectory(archetypeResourcesDirPath));

		String projectTemplateDirName = String.valueOf(
			projectTemplateDirPath.getFileName());

		Properties bndProperties = _testBndBnd(projectTemplateDirPath);

		_testBuildGradle(archetypeResourcesDirPath);
		_testGitIgnore(projectTemplateDirName, archetypeResourcesDirPath);
		_testGradleWrapper(archetypeResourcesDirPath);
		_testMavenWrapper(archetypeResourcesDirPath);

		String pathString = archetypeResourcesDirPath.toString();

		if (!pathString.contains("ext") && !pathString.contains("spring-mvc")) {
			_testPomXml(archetypeResourcesDirPath, documentBuilder);
		}

		_testProjectTemplateCustomizer(
			projectTemplateDirName, projectTemplateDirPath);

		final AtomicBoolean requireAuthorProperty = new AtomicBoolean();
		final Set<String> archetypeResourceExpressions = new HashSet<>();

		Files.walkFileTree(
			archetypeResourcesDirPath,
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(
						Path dirPath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					Path languagePropertiesPath = dirPath.resolve(
						"Language.properties");

					if (Files.exists(languagePropertiesPath)) {
						_testLanguageProperties(languagePropertiesPath);

						String glob = "Language_*.properties";

						Assert.assertNull(
							"Forbidden " + dirPath + File.separator + glob,
							FileUtil.getFile(dirPath, glob));
					}

					Path liferayPluginPackagePropertiesPath = dirPath.resolve(
						"liferay-plugin-package.properties");

					if (Files.exists(liferayPluginPackagePropertiesPath)) {
						requireAuthorProperty.set(true);

						_testLiferayPluginPackageProperties(
							liferayPluginPackagePropertiesPath);
					}

					Path restConfigYAMLPath = dirPath.resolve(
						"rest-config.yaml");

					if (Files.exists(restConfigYAMLPath)) {
						requireAuthorProperty.set(true);
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					String extension = FileTestUtil.getExtension(fileName);

					boolean javaFile = extension.equals("java");

					if (javaFile) {
						requireAuthorProperty.set(true);
					}

					if (!fileName.equals(".gitkeep") &&
						(_isInJavaSrcDir(path) != javaFile)) {

						Assert.fail("Wrong source directory " + path);
					}

					if (_isTextFile(fileName, extension)) {
						_testTextFile(
							path, fileName, extension,
							archetypeResourceExpressions);
					}

					return FileVisitResult.CONTINUE;
				}

			});

		String archetypePostGenerateGroovy = _testArchetypePostGenerateGroovy(
			projectTemplateDirPath);

		_testArchetypeMetadataXml(
			projectTemplateDirPath, projectTemplateDirName,
			archetypeResourcesDirPath, bndProperties,
			requireAuthorProperty.get(), archetypePostGenerateGroovy,
			archetypeResourceExpressions, documentBuilder);
	}

	private void _testPropertyValue(
		Path path, Properties properties, String key, String expectedValue) {

		Assert.assertEquals(
			"Incorrect value of \"" + key + "\" in " + path, expectedValue,
			properties.getProperty(key));
	}

	private String _testTextFile(Path path) throws IOException {
		String text = FileUtil.read(path);

		Assert.assertEquals(
			"Forbidden leading or trailing whitespaces in " + path, text.trim(),
			text);

		try (BufferedReader bufferedReader = new BufferedReader(
				new StringReader(text))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				Assert.assertFalse(
					"Forbidden whitespace trailing character in " + path,
					!line.isEmpty() &&
					Character.isWhitespace(line.charAt(line.length() - 1)));
			}
		}

		return text;
	}

	private void _testTextFile(
			Path path, String fileName, String extension,
			Set<String> archetypeResourceExpressions)
		throws IOException {

		String text = _testTextFile(path);

		Matcher matcher = _velocityDirectivePattern.matcher(text);

		while (matcher.find()) {
			String directive = matcher.group(1);
			String content = matcher.group(2);

			Assert.assertEquals(
				"Source formatting error in " + path,
				"#" + directive + " (" + content.trim() + ")", matcher.group());
		}

		if (extension.equals("java")) {
			Assert.assertTrue(
				"Missing @author tag in " + path,
				text.contains("* @author ${author}"));
		}

		String pathString = path.toString();

		if (!pathString.contains("spring-mvc-portlet") &&
			!fileName.equals("portlet") && extension.equals("xml") &&
			Validator.isNotNull(text)) {

			String xmlDeclaration = _xmlDeclarations.get(fileName);

			if (xmlDeclaration == null) {
				xmlDeclaration = _xmlDeclarations.get(null);
			}

			Assert.assertTrue(
				"Incorrect XML declaration in " + path,
				text.startsWith(xmlDeclaration));
		}

		if (!fileName.endsWith(".js")) {
			matcher = _archetypeResourceExpressionPattern.matcher(text);

			while (matcher.find()) {
				String name = matcher.group(1);

				if (text.contains("#set ($" + name + " = ")) {
					continue;
				}

				archetypeResourceExpressions.add(name);
			}
		}
	}

	private static final String[] _SOURCESET_NAMES = {
		"main", "test", "testIntegration"
	};

	private static final List<String>
		_archetypeMetadataXmlDefaultPropertyNames = Arrays.asList(
			"artifactId", "groupId", "package", "project", "replacestring",
			"version");
	private static final Pattern _archetypeMetadataXmlIncludePattern =
		Pattern.compile("<include>([^\\*]+?)<\\/include>");
	private static final Pattern _archetypeMetadataXmlRequiredPropertyPattern =
		Pattern.compile("<requiredProperty key=\"(\\w+)\">");
	private static final Pattern _archetypeResourceExpressionPattern =
		Pattern.compile("\\$\\{([^\\}]*)\\}");
	private static final Pattern _buildGradleDependencyPattern =
		Pattern.compile(
			"(compile(?:Only)?) \\(?group: \\\"(.+)\\\", name: \\\"([a-zA-Z" +
				"0-9\\.\\-\\{\\}\\$]+)\\\"((?:, version: )\\\"([0-9\\.]+)" +
					"\\\")?(, transitive: (?: true|false))?(\\) \\{force = " +
						"true\\})?");
	private static final Pattern _bundleDescriptionPattern = Pattern.compile(
		"Creates a .+\\.");
	private static final Pattern _bundleNameSeparatorPattern = Pattern.compile(
		"(?:^|\\s)(\\w)");
	private static final List<String> _gitIgnoreLines = Arrays.asList(
		".gradle/", "build/", "target/");

	private static final Comparator<String> _languagePropertiesKeyComparator =
		new Comparator<String>() {

			@Override
			public int compare(String key1, String key2) {
				boolean key1StartsWithLetter = Character.isLetter(
					key1.charAt(0));
				boolean key2StartsWithLetter = Character.isLetter(
					key2.charAt(0));

				if (key1StartsWithLetter && !key2StartsWithLetter) {
					return -1;
				}

				if (!key1StartsWithLetter && key2StartsWithLetter) {
					return 1;
				}

				return key1.compareTo(key2);
			}

		};

	private static final Pattern _pomXmlExecutionIdPattern = Pattern.compile(
		"[a-z]+(?:-[a-z]+)*");
	private static final Set<String> _textFileExtensions = new HashSet<>(
		Arrays.asList(
			"bnd", "gradle", "java", "js", "json", "jsp", "jspf", "properties",
			"vm", "xml", "yaml"));
	private static final Pattern _velocityDirectivePattern = Pattern.compile(
		"#(if|set)\\s*\\(\\s*(.+)\\s*\\)");
	private static final Pattern _velocitySetDirectivePattern = Pattern.compile(
		"#set\\s*\\(\\s*\\$(\\S+)\\s*=");
	private static final Map<String, String> _xmlDeclarations = new HashMap<>();

	static {
		try {
			_addXmlDeclaration(null, "xml_declaration.tmpl");
			_addXmlDeclaration(
				"liferay-display.xml", "liferay_display_xml_declaration.tmpl");
			_addXmlDeclaration(
				"liferay-hook.xml", "liferay_hook_xml_declaration.tmpl");
			_addXmlDeclaration(
				"liferay-layout-templates.xml",
				"liferay_layout_templates_xml_declaration.tmpl");
			_addXmlDeclaration(
				"liferay-portlet.xml", "liferay_portlet_xml_declaration.tmpl");
			_addXmlDeclaration("pom.xml", "pom_xml_declaration.tmpl");
			_addXmlDeclaration("portlet.xml", "portlet_xml_declaration.tmpl");
			_addXmlDeclaration("service.xml", "service_xml_declaration.tmpl");
			_addXmlDeclaration("web.xml", "web_xml_declaration.tmpl");
		}
		catch (IOException ioException) {
			throw new ExceptionInInitializerError(ioException);
		}
	}

	private static class BuildGradleDependency {

		public BuildGradleDependency(
			String group, String name, String version, boolean provided) {

			this.group = group;
			this.name = name;
			this.version = version;
			this.provided = provided;
		}

		public final String group;
		public final String name;
		public final boolean provided;
		public final String version;

	}

}