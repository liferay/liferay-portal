/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates;

import aQute.bnd.differ.DiffPluginImpl;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.diff.Tree;

import com.liferay.maven.executor.MavenExecutor;
import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.VersionUtil;
import com.liferay.project.templates.extensions.util.WorkspaceUtil;
import com.liferay.project.templates.util.FileTestUtil;
import com.liferay.project.templates.util.StringTestUtil;
import com.liferay.project.templates.util.XMLTestUtil;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.net.URI;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.diibadaaba.zipdiff.DifferenceCalculator;
import net.diibadaaba.zipdiff.Differences;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.BuildTask;
import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;

import org.junit.Assert;
import org.junit.rules.TemporaryFolder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Lawrence Lee
 */
public interface BaseProjectTemplatesTestCase {

	public static final String BUILD_GRADLE_FILE_NAME = "build.gradle";

	public static final String BUILD_PROJECTS = System.getProperty(
		"project.templates.test.builds");

	public static final String BUNDLES_DIFF_IGNORES = StringTestUtil.merge(
		Arrays.asList(
			"*.js.map", "*_jsp.class", "*manifest.json", "*pom.properties",
			"*pom.xml", "*package.json", "Archiver-Version", "Build-Jdk",
			"Build-Jdk-Spec", "Built-By", "Javac-Debug", "Javac-Deprecation",
			"Javac-Encoding", "Created-By", "Tool", "Javac-Debug",
			"Javac-Deprecation", "Javac-Encoding"),
		',');

	public static final String DEPENDENCY_JAVAX_PORTLET_API =
		"compileOnly group: \"javax.portlet\", name: \"portlet-api\"";

	public static final String DEPENDENCY_JAVAX_SERVLET_API =
		"compileOnly group: \"javax.servlet\", name: \"javax.servlet-api\"";

	public static final String DEPENDENCY_ORG_OSGI_ANNOTATIONS =
		"compileOnly group: \"org.osgi\", name: " +
			"\"org.osgi.service.component.annotations\"";

	public static final String DEPENDENCY_PORTAL_KERNEL =
		"compileOnly group: \"com.liferay.portal\", name: " +
			"\"com.liferay.portal.kernel\"";

	public static final String DEPENDENCY_RELEASE_DXP_API =
		"compileOnly group: \"com.liferay.portal\", name: \"release.dxp.api\"";

	public static final String DEPENDENCY_RELEASE_PORTAL_API =
		"compileOnly group: \"com.liferay.portal\", name: " +
			"\"release.portal.api\"";

	public static final String FREEMARKER_PORTLET_VIEW_FTL_PREFIX =
		"<#include \"init.ftl\">";

	public static final String GRADLE_PROPERTIES_FILE_NAME =
		"gradle.properties";

	public static final String GRADLE_TASK_PATH_BUILD = ":build";

	public static final String GRADLE_TASK_PATH_BUILD_SERVICE = ":buildService";

	public static final String GRADLE_TASK_PATH_DEPLOY = ":deploy";

	public static final String[] GRADLE_WRAPPER_FILE_NAMES = {
		"gradlew", "gradlew.bat", "gradle/wrapper/gradle-wrapper.jar",
		"gradle/wrapper/gradle-wrapper.properties"
	};

	public static final String GRADLE_WRAPPER_VERSION = "8.5";

	public static final String MAVEN_GOAL_BUILD_REST = "rest-builder:build";

	public static final String MAVEN_GOAL_BUILD_SERVICE =
		"service-builder:build";

	public static final String MAVEN_GOAL_PACKAGE = "package";

	public static final String[] MAVEN_WRAPPER_FILE_NAMES = {
		"mvnw", "mvnw.cmd", ".mvn/wrapper/maven-wrapper.jar",
		".mvn/wrapper/maven-wrapper.properties"
	};

	public static final String NODEJS_NPM_CI_REGISTRY = System.getProperty(
		"nodejs.npm.ci.registry");

	public static final String NODEJS_NPM_CI_SASS_BINARY_SITE =
		System.getProperty("nodejs.npm.ci.sass.binary.site");

	public static final String OUTPUT_FILE_NAME_GLOB_REGEX = "*.{jar,war}";

	public static final String REPOSITORY_CDN_URL =
		"https://repository-cdn.liferay.com/nexus/content/groups/public";

	public static final String SETTINGS_GRADLE_FILE_NAME = "settings.gradle";

	public static final boolean TEST_DEBUG_BUNDLE_DIFFS = Boolean.getBoolean(
		"test.debug.bundle.diffs");

	public static final String UNRELEASED_QUARTERLY_LIFERAY_VERSION = "2025.q3";

	public static final String WORKSPACE_QUARTERLY_LIFERAY_VERSION =
		"2025.q2.1";

	public static final Pattern antBndPluginVersionPattern = Pattern.compile(
		".*com\\.liferay\\.ant\\.bnd[:-]([0-9]+\\.[0-9]+\\.[0-9]+).*",
		Pattern.DOTALL | Pattern.MULTILINE);
	public static final Pattern gradlePluginVersionPattern = Pattern.compile(
		".*com\\.liferay\\.gradle\\.plugins:([0-9]+\\.[0-9]+\\.[0-9]+).*",
		Pattern.DOTALL | Pattern.MULTILINE);
	public static final Pattern portalToolsBundleSupportVersionPattern =
		Pattern.compile(
			".*com\\.liferay\\.portal\\.tools\\.bundle\\.support-" +
				"([0-9]+\\.[0-9]+\\.[0-9]+).*",
			Pattern.DOTALL | Pattern.MULTILINE);

	public static File findParentFile(
		File dir, String[] fileNames, boolean checkParents) {

		if (dir == null) {
			return null;
		}

		if (Objects.equals(dir.toString(), ".") || !dir.isAbsolute()) {
			try {
				dir = dir.getCanonicalFile();
			}
			catch (Exception exception) {
				dir = dir.getAbsoluteFile();
			}
		}

		for (String fileName : fileNames) {
			File file = new File(dir, fileName);

			if (file.exists()) {
				return dir;
			}
		}

		if (checkParents) {
			return findParentFile(dir.getParentFile(), fileNames, checkParents);
		}

		return null;
	}

	public static boolean isWindows() {
		String osName = System.getProperty("os.name");

		osName = osName.toLowerCase();

		return osName.contains("win");
	}

	public default void addCssBuilderConfigurationElement(
		Document document, String configurationName, String configurationText) {

		Element projectElement = document.getDocumentElement();

		Element buildElement = XMLTestUtil.getChildElement(
			projectElement, "build");

		Element pluginsElement = XMLTestUtil.getChildElement(
			buildElement, "plugins");

		List<Element> pluginElementList = XMLTestUtil.getChildElements(
			pluginsElement);

		for (Element pluginElement : pluginElementList) {
			Element artifactIdElement = XMLTestUtil.getChildElement(
				pluginElement, "artifactId");

			Node node = artifactIdElement.getFirstChild();

			String artifactId = node.getNodeValue();

			if (artifactId.equals("com.liferay.css.builder")) {
				Element configurationElement = XMLTestUtil.getChildElement(
					pluginElement, "configuration");

				Element newElement = document.createElement(configurationName);

				newElement.appendChild(
					document.createTextNode(configurationText));

				configurationElement.appendChild(newElement);
			}
		}
	}

	public default void addGradleDependency(
			File buildFile, String... dependencies)
		throws IOException {

		Path buildFilePath = buildFile.toPath();

		List<String> lines = Files.readAllLines(
			buildFilePath, StandardCharsets.UTF_8);

		try (BufferedWriter bufferedWriter = Files.newBufferedWriter(
				buildFilePath, StandardCharsets.UTF_8)) {

			for (String line : lines) {
				FileTestUtil.write(bufferedWriter, line);

				if (line.contains("dependencies {")) {
					FileTestUtil.write(bufferedWriter, dependencies);
				}
			}
		}
	}

	public default void addMavenDependencyElement(
		Document document, String groupId, String artifactId, String scope) {

		Element projectElement = document.getDocumentElement();

		Element dependenciesElement = XMLTestUtil.getChildElement(
			projectElement, "dependencies");

		Element artifactIdElement = document.createElement("artifactId");
		Element dependencyElement = document.createElement("dependency");
		Element groupdIdElement = document.createElement("groupId");
		Element scopeElement = document.createElement("scope");

		groupdIdElement.appendChild(document.createTextNode(groupId));

		dependencyElement.appendChild(groupdIdElement);

		artifactIdElement.appendChild(document.createTextNode(artifactId));

		dependencyElement.appendChild(artifactIdElement);

		scopeElement.appendChild(document.createTextNode(scope));

		dependencyElement.appendChild(scopeElement);

		dependenciesElement.appendChild(dependencyElement);
	}

	public default void addNexusRepositoriesElement(
		Document document, String parentElementName, String elementName) {

		String repositoryUrl =
			ProjectTemplatesTest.mavenExecutor.getRepositoryUrl();

		if (Validator.isNull(repositoryUrl)) {
			repositoryUrl = REPOSITORY_CDN_URL;
		}

		addNexusRepositoriesElement(
			document, parentElementName, elementName, repositoryUrl);
	}

	public default void addNexusRepositoriesElement(
		Document document, String parentElementName, String elementName,
		String elementValue) {

		Element projectElement = document.getDocumentElement();

		Element repositoriesElement = XMLTestUtil.getChildElement(
			projectElement, parentElementName);

		if (repositoriesElement == null) {
			repositoriesElement = document.createElement(parentElementName);

			projectElement.appendChild(repositoriesElement);
		}

		Element repositoryElement = document.createElement(elementName);

		Element idElement = document.createElement("id");

		idElement.appendChild(document.createTextNode(System.nanoTime() + ""));

		Element urlElement = document.createElement("url");

		Text urlText = document.createTextNode(elementValue);

		urlElement.appendChild(urlText);

		repositoryElement.appendChild(idElement);
		repositoryElement.appendChild(urlElement);

		repositoriesElement.appendChild(repositoryElement);
	}

	public default void addNpmrc(File projectDir) throws IOException {
		File npmrcFile = new File(projectDir, ".npmrc");

		String content = "sass_binary_site=" + NODEJS_NPM_CI_SASS_BINARY_SITE;

		Files.write(
			npmrcFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
	}

	public default void buildProjects(
			URI gradleDistribution, MavenExecutor mavenExecutor,
			File gradleProjectDir, File mavenProjectDir)
		throws Exception {

		File gradleOutputDir = new File(gradleProjectDir, "build/libs");
		File mavenOutputDir = new File(mavenProjectDir, "target");

		buildProjects(
			gradleDistribution, mavenExecutor, gradleProjectDir,
			mavenProjectDir, gradleOutputDir, mavenOutputDir,
			GRADLE_TASK_PATH_BUILD);
	}

	public default void buildProjects(
			URI gradleDistribution, MavenExecutor mavenExecutor,
			File gradleProjectDir, File mavenProjectDir, File gradleOutputDir,
			File mavenOutputDir, String... gradleTaskPath)
		throws Exception {

		if (!isBuildProjects()) {
			return;
		}

		executeGradle(gradleProjectDir, gradleDistribution, gradleTaskPath);

		Path gradleOutputPath = FileTestUtil.getFile(
			gradleOutputDir.toPath(), OUTPUT_FILE_NAME_GLOB_REGEX, 1);

		Assert.assertNotNull(gradleOutputPath);

		Assert.assertTrue(Files.exists(gradleOutputPath));

		File gradleOutputFile = gradleOutputPath.toFile();

		String gradleOutputFileName = gradleOutputFile.getName();

		executeMaven(mavenProjectDir, mavenExecutor, MAVEN_GOAL_PACKAGE);

		Path mavenOutputPath = FileTestUtil.getFile(
			mavenOutputDir.toPath(), OUTPUT_FILE_NAME_GLOB_REGEX, 1);

		Assert.assertNotNull(mavenOutputPath);

		Assert.assertTrue(Files.exists(mavenOutputPath));

		File mavenOutputFile = mavenOutputPath.toFile();

		String mavenOutputFileName = mavenOutputFile.getName();

		try {
			if (gradleOutputFileName.endsWith(".jar")) {
				testBundlesDiff(gradleOutputFile, mavenOutputFile);
			}
			else if (gradleOutputFileName.endsWith(".war")) {
				testWarsDiff(gradleOutputFile, mavenOutputFile);
			}
		}
		catch (Throwable throwable) {
			if (TEST_DEBUG_BUNDLE_DIFFS) {
				Path dirPath = Paths.get("build");

				Files.copy(
					gradleOutputFile.toPath(),
					dirPath.resolve(gradleOutputFileName));
				Files.copy(
					mavenOutputFile.toPath(),
					dirPath.resolve(mavenOutputFileName));
			}

			throw throwable;
		}
	}

	public default File buildTemplateWithGradle(
			File destinationDir, String template, String name, boolean gradle,
			boolean maven, String... args)
		throws Exception {

		List<String> completeArgs = new ArrayList<>(args.length + 6);

		completeArgs.add("--destination");
		completeArgs.add(destinationDir.getPath());

		completeArgs.add("--gradle");
		completeArgs.add(String.valueOf(gradle));

		completeArgs.add("--maven");
		completeArgs.add(String.valueOf(maven));

		if (Validator.isNotNull(name)) {
			completeArgs.add("--name");
			completeArgs.add(name);
		}

		if (Validator.isNotNull(template)) {
			completeArgs.add("--template");
			completeArgs.add(template);
		}

		for (String arg : args) {
			completeArgs.add(arg);
		}

		ProjectTemplates.main(completeArgs.toArray(new String[0]));

		File projectDir = new File(destinationDir, name);

		testExists(projectDir, ".gitignore");

		if (gradle) {
			testExists(projectDir, "build.gradle");
		}
		else {
			testNotExists(projectDir, "build.gradle");
		}

		if (maven) {
			testExists(projectDir, "pom.xml");
		}
		else {
			testNotExists(projectDir, "pom.xml");
		}

		boolean workspace = WorkspaceUtil.isWorkspace(destinationDir);

		if (gradle && !workspace) {
			for (String fileName : GRADLE_WRAPPER_FILE_NAMES) {
				testExists(projectDir, fileName);
			}

			testExecutable(projectDir, "gradlew");
		}
		else {
			for (String fileName : GRADLE_WRAPPER_FILE_NAMES) {
				testNotExists(projectDir, fileName);
			}

			testNotExists(projectDir, "settings.gradle");
		}

		if (maven && !workspace) {
			for (String fileName : MAVEN_WRAPPER_FILE_NAMES) {
				testExists(projectDir, fileName);
			}

			testExecutable(projectDir, "mvnw");
		}
		else {
			for (String fileName : MAVEN_WRAPPER_FILE_NAMES) {
				testNotExists(projectDir, fileName);
			}
		}

		return projectDir;
	}

	public default File buildTemplateWithGradle(
			File destinationDir, String template, String name, String... args)
		throws Exception {

		return buildTemplateWithGradle(
			destinationDir, template, name, true, false, args);
	}

	public default File buildTemplateWithGradle(
			TemporaryFolder temporaryFolder, String template, String name,
			String... args)
		throws Exception {

		File destinationDir = temporaryFolder.newFolder("gradle" + name);

		return buildTemplateWithGradle(destinationDir, template, name, args);
	}

	public default File buildTemplateWithMaven(
			File parentDir, File destinationDir, String template, String name,
			String groupId, MavenExecutor mavenExecutor, String... args)
		throws Exception {

		List<String> completeArgs = new ArrayList<>();

		completeArgs.add("archetype:generate");
		completeArgs.add("--batch-mode");

		String archetypeArtifactId =
			"com.liferay.project.templates." + template.replace('-', '.');

		if (archetypeArtifactId.equals(
				"com.liferay.project.templates.portlet")) {

			archetypeArtifactId = "com.liferay.project.templates.mvc.portlet";
		}

		completeArgs.add("-DarchetypeArtifactId=" + archetypeArtifactId);
		completeArgs.add("-DarchetypeCatalog=internal");
		completeArgs.add("-DarchetypeGroupId=com.liferay");

		String projectTemplateVersion =
			ProjectTemplatesUtil.getArchetypeVersion(archetypeArtifactId);

		Assert.assertTrue(
			"Unable to get project template version",
			Validator.isNotNull(projectTemplateVersion));

		completeArgs.add("-DarchetypeVersion=" + projectTemplateVersion);

		completeArgs.add("-DartifactId=" + name);
		completeArgs.add("-Dauthor=" + System.getProperty("user.name"));
		completeArgs.add("-DgroupId=" + groupId);
		completeArgs.add("-Dversion=1.0.0");

		String liferayVersion = "";
		boolean liferayVersionSet = false;
		boolean projectTypeSet = false;

		for (String arg : args) {
			completeArgs.add(arg);

			if (arg.startsWith("-DliferayVersion=")) {
				liferayVersion = arg.substring("-DliferayVersion=".length());
				liferayVersionSet = true;
			}
			else if (arg.startsWith("-DprojectType=")) {
				projectTypeSet = true;
			}
		}

		if (!liferayVersionSet) {
			liferayVersion = getDefaultLiferayVersion();

			completeArgs.add("-DliferayVersion=" + liferayVersion);
		}

		if (!projectTypeSet) {
			completeArgs.add("-DprojectType=standalone");
		}

		if (VersionUtil.isJakartaCompatibleVersion(liferayVersion)) {
			completeArgs.add("-DjakartaCompatible=true");
		}
		else {
			completeArgs.add("-DjakartaCompatible=false");
		}

		executeMaven(
			destinationDir, mavenExecutor, completeArgs.toArray(new String[0]));

		File projectDir = new File(destinationDir, name);

		testExists(projectDir, "pom.xml");
		testNotExists(projectDir, "gradlew");
		testNotExists(projectDir, "gradlew.bat");
		testNotExists(projectDir, "gradle/wrapper/gradle-wrapper.jar");
		testNotExists(projectDir, "gradle/wrapper/gradle-wrapper.properties");

		return projectDir;
	}

	public default File buildTemplateWithMaven(
			TemporaryFolder temporaryFolder, String template, String name,
			String groupId, MavenExecutor mavenExecutor, String... args)
		throws Exception {

		File mavenDir = temporaryFolder.newFolder("maven" + name);

		return buildTemplateWithMaven(
			mavenDir, mavenDir, template, name, groupId, mavenExecutor, args);
	}

	public default File buildWorkspace(
			TemporaryFolder temporaryFolder, String liferayVersion)
		throws Exception {

		if (liferayVersion.startsWith(UNRELEASED_QUARTERLY_LIFERAY_VERSION)) {
			liferayVersion = WORKSPACE_QUARTERLY_LIFERAY_VERSION;
		}

		String name = "test-workspace";

		File destinationDir = temporaryFolder.newFolder(
			"gradleWorkspace" + name);

		File workspaceDir = buildTemplateWithGradle(
			destinationDir, WorkspaceUtil.WORKSPACE, name, "--liferay-version",
			liferayVersion);

		writeM2TmpForGradleWorkspace(workspaceDir);

		return workspaceDir;
	}

	public default File buildWorkspace(
			TemporaryFolder temporaryFolder, String buildType, String name,
			String liferayVersion, MavenExecutor mavenExecutor)
		throws Exception {

		if (liferayVersion.startsWith(UNRELEASED_QUARTERLY_LIFERAY_VERSION)) {
			liferayVersion = WORKSPACE_QUARTERLY_LIFERAY_VERSION;
		}

		File workspaceDir;

		if (buildType.equals("gradle")) {
			workspaceDir = buildWorkspace(temporaryFolder, liferayVersion);

			writeM2TmpForGradleWorkspace(workspaceDir);

			writeGradlePropertiesInWorkspace(
				workspaceDir,
				"liferay.workspace.target.platform.version=" + liferayVersion);
		}
		else {
			File destinationDir = temporaryFolder.newFolder("mavenWorkspace");
			String groupId = "com.test";

			workspaceDir = buildTemplateWithMaven(
				destinationDir, destinationDir, "workspace", name, groupId,
				mavenExecutor, "-DliferayVersion=" + liferayVersion,
				"-Dpackage=com.test");

			writeM2TmpForMavenWorkspace(workspaceDir);

			if (VersionUtil.getMinorVersion(liferayVersion) < 3) {
				updateMavenPomProperties(
					workspaceDir, "liferay.bom.version", "liferay.bom.version",
					liferayVersion);

				updateMavenPomElementText(
					workspaceDir, "//artifactId[text()='release.portal.bom']",
					"release.dxp.bom");
				updateMavenPomElementText(
					workspaceDir,
					"//artifactId[text()='release.portal.bom.compile.only']",
					"release.dxp.bom.compile.only");
				updateMavenPomElementText(
					workspaceDir,
					"//artifactId[text()='release.portal.bom.third.party']",
					"release.dxp.bom.third.party");
			}
		}

		return workspaceDir;
	}

	public default void compareDiff(
		Diff diff, File bundleFile1, File bundleFile2) {

		Collection<? extends Diff> diffs = diff.getChildren();

		if (diffs.isEmpty()) {
			Assert.assertEquals(
				"Bundle " + bundleFile1 + " " + diff.getNewer() + " and " +
					bundleFile2 + " " + diff.getOlder() + " do not match",
				aQute.bnd.service.diff.Delta.UNCHANGED.toString(),
				String.valueOf(diff.getDelta()));

			return;
		}

		for (Diff curDiff : diffs) {
			compareDiff(curDiff, bundleFile1, bundleFile2);
		}
	}

	public default void configureExecutePackageManagerTask(File projectDir)
		throws Exception {

		File buildGradleFile = new File(projectDir, "build.gradle");

		StringBuilder sb = new StringBuilder();

		String lineSeparator = System.lineSeparator();

		sb.append(lineSeparator);

		sb.append("plugins.withId('com.liferay.node') {");
		sb.append(lineSeparator);

		sb.append("\ttasks.named('packageRunBuild') {");
		sb.append(lineSeparator);

		sb.append("\t\tregistry = '");
		sb.append(NODEJS_NPM_CI_REGISTRY);
		sb.append('\'');
		sb.append(lineSeparator);

		sb.append("\t}");
		sb.append(lineSeparator);

		sb.append('}');
		sb.append(lineSeparator);

		sb.append("node {");
		sb.append(lineSeparator);

		sb.append("\tuseNpm = true");
		sb.append(lineSeparator);

		sb.append("}");

		String executePackageManagerTaskScript = sb.toString();

		Files.write(
			buildGradleFile.toPath(),
			executePackageManagerTaskScript.getBytes(StandardCharsets.UTF_8),
			StandardOpenOption.APPEND);
	}

	public default void configurePomNpmConfiguration(
			File projectDir, String nodePackageManager)
		throws Exception {

		File pomXmlFile = testExists(projectDir, "pom.xml");

		if (Objects.equals(nodePackageManager, "npm")) {
			editXml(
				pomXmlFile,
				document -> {
					try {
						modifyElementText(
							document,
							"//goal[contains(text(), 'install-node-and-yarn')]",
							"install-node-and-npm");
						modifyElementText(
							document, "//goal[contains(text(), 'yarn')]",
							"npm");
						modifyElementText(
							document,
							"//id[contains(text(), 'install-node-and-yarn')]",
							"install-node-and-npm");
						modifyElementText(
							document, "//id[contains(text(), 'yarn-install')]",
							"npm-install");
						modifyElementText(
							document,
							"//id[contains(text(), 'yarn-run-build')]",
							"npm-run-build");

						replaceElementByName(
							document, "yarnVersion", "npmVersion", "8.1.0");
					}
					catch (XPathExpressionException xPathExpressionException) {
						throw new RuntimeException(xPathExpressionException);
					}
				});
		}

		editXml(
			pomXmlFile,
			document -> {
				try {
					XPathFactory xPathFactory = XPathFactory.newInstance();

					XPath xPath = xPathFactory.newXPath();

					XPathExpression pomXmlYarnInstallXPathExpression =
						xPath.compile(
							"//id[contains(text(),'" + nodePackageManager +
								"-install')]/parent::*");

					NodeList nodeList =
						(NodeList)pomXmlYarnInstallXPathExpression.evaluate(
							document, XPathConstants.NODESET);

					Node executionNode = nodeList.item(0);

					Element configurationElement = document.createElement(
						"configuration");

					executionNode.appendChild(configurationElement);

					Element argumentsElement = document.createElement(
						"arguments");

					configurationElement.appendChild(argumentsElement);

					Text text = document.createTextNode(
						"install --registry=" + NODEJS_NPM_CI_REGISTRY);

					argumentsElement.appendChild(text);
				}
				catch (XPathExpressionException xPathExpressionException) {
					throw new RuntimeException(xPathExpressionException);
				}
			});
	}

	public default void createNewFiles(String fileName, File... dirs)
		throws IOException {

		for (File dir : dirs) {
			File file = new File(dir, fileName);

			File parentDir = file.getParentFile();

			if (!parentDir.isDirectory()) {
				Assert.assertTrue(parentDir.mkdirs());
			}

			Assert.assertTrue(file.createNewFile());
		}
	}

	public default void editXml(File xmlFile, Consumer<Document> consumer)
		throws Exception {

		TransformerFactory transformerFactory =
			TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();

		DocumentBuilderFactory documentBuilderFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder documentBuilder =
			documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(xmlFile);

		consumer.accept(document);

		DOMSource domSource = new DOMSource(document);

		transformer.transform(domSource, new StreamResult(xmlFile));
	}

	public default Optional<String> executeGradle(
			File projectDir, boolean debug, boolean buildAndFail,
			URI gradleDistribution, String... taskPaths)
		throws IOException {

		final String repositoryUrl =
			ProjectTemplatesTest.mavenExecutor.getRepositoryUrl();

		String projectPath = projectDir.getPath();

		if (projectPath.contains("workspace")) {
			File workspaceBuildFile = new File(
				getWorkspaceDir(projectDir), "build.gradle");

			Path buildFilePath = workspaceBuildFile.toPath();

			String content = FileUtil.read(buildFilePath);

			if (!content.contains("allprojects")) {
				String m2TmpPathString = String.valueOf(
					Paths.get(System.getProperty("maven.repo.local") + "-tmp"));

				if (isWindows()) {
					m2TmpPathString = m2TmpPathString.replaceAll("\\\\", "/");
				}

				StringBuilder sb = new StringBuilder();

				sb.append("allprojects {\n\trepositories {\n\t\tmavenLocal()");
				sb.append("\n\t\tmaven {\n\t\t\turl file(\"" + m2TmpPathString);
				sb.append("\").toURI()\n\t\t}\n\t\tmaven {\n\t\t\t");
				sb.append("credentials {\n\t\t\t\tusername \"");
				sb.append(System.getProperty("repository.private.username"));
				sb.append("\"\n\t\t\t\tpassword \"");
				sb.append(System.getProperty("repository.private.password"));
				sb.append("\"\n\t\t\t}\n\t\t\turl \"https://repository");
				sb.append(".liferay.com/nexus/content/repositories/xanadu\"");
				sb.append("\n\t\t}\n\t}\n\tconfigurations.all {\n\t\t");
				sb.append("resolutionStrategy.force 'javax.servlet:javax");
				sb.append(".servlet-api:3.0.1'\n\t}\n}");

				content += System.lineSeparator() + sb.toString();

				Files.write(
					buildFilePath, content.getBytes(StandardCharsets.UTF_8));
			}
		}

		Files.walkFileTree(
			projectDir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path path, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(path.getFileName());

					if (fileName.equals("build.gradle") ||
						fileName.equals("settings.gradle")) {

						String content = FileUtil.read(path);

						if (Validator.isNotNull(repositoryUrl)) {
							content = content.replace(
								"\"" + REPOSITORY_CDN_URL + "\"",
								"\"" + repositoryUrl + "\"");
						}

						if (!content.contains("mavenLocal()")) {
							String mavenRepoString = System.getProperty(
								"maven.repo.local");

							Path m2TmpPath = Paths.get(
								mavenRepoString + "-tmp");

							if (Files.exists(m2TmpPath)) {
								String m2TmpPathString = m2TmpPath.toString();

								if (isWindows()) {
									m2TmpPathString =
										m2TmpPathString.replaceAll("\\\\", "/");
								}

								content = content.replace(
									"repositories {",
									"repositories {\n\t\tmavenLocal()\n\t\t" +
										"maven {\n\t\t\turl \"" +
											m2TmpPathString + "\"\n\t\t}");
							}
						}

						Files.write(
							path, content.getBytes(StandardCharsets.UTF_8));
					}

					return FileVisitResult.CONTINUE;
				}

			});

		GradleRunner gradleRunner = GradleRunner.create();

		List<String> arguments = new ArrayList<>(taskPaths.length + 4);

		String httpProxyHost =
			ProjectTemplatesTest.mavenExecutor.getHttpProxyHost();
		int httpProxyPort =
			ProjectTemplatesTest.mavenExecutor.getHttpProxyPort();

		if (Validator.isNotNull(httpProxyHost) && (httpProxyPort > 0)) {
			arguments.add("-Dhttp.proxyHost=" + httpProxyHost);
			arguments.add("-Dhttp.proxyPort=" + httpProxyPort);
		}

		arguments.add("clean");

		if (debug) {
			arguments.add("--debug");
		}
		else {
			arguments.add("--stacktrace");
		}

		for (String taskPath : taskPaths) {
			arguments.add(taskPath);
		}

		String stdOutput = null;

		StringWriter stringWriter = new StringWriter();

		if (debug) {
			gradleRunner.forwardStdOutput(stringWriter);
		}

		gradleRunner.withArguments(arguments);

		gradleRunner.withGradleDistribution(gradleDistribution);
		gradleRunner.withProjectDir(projectDir);

		BuildResult buildResult = null;

		if (buildAndFail) {
			buildResult = gradleRunner.buildAndFail();

			stdOutput = buildResult.getOutput();
		}
		else {
			buildResult = gradleRunner.build();

			for (String taskPath : taskPaths) {
				BuildTask buildTask = buildResult.task(taskPath);

				Assert.assertNotNull(
					"Build task \"" + taskPath + "\" not found", buildTask);

				Assert.assertEquals(
					"Unexpected outcome for task \"" + buildTask.getPath() +
						"\"",
					TaskOutcome.SUCCESS, buildTask.getOutcome());
			}
		}

		if (debug) {
			stdOutput = stringWriter.toString();

			stringWriter.close();
		}

		return Optional.ofNullable(stdOutput);
	}

	public default Optional<String> executeGradle(
			File projectDir, boolean debug, URI gradleDistribution,
			String... taskPaths)
		throws IOException {

		return executeGradle(
			projectDir, debug, false, gradleDistribution, taskPaths);
	}

	public default void executeGradle(
			File projectDir, URI gradleDistribution, String... taskPaths)
		throws IOException {

		executeGradle(projectDir, false, gradleDistribution, taskPaths);
	}

	public default String executeMaven(
			File projectDir, boolean buildAndFail, MavenExecutor mavenExecutor,
			String... args)
		throws Exception {

		String[] completeArgs = new String[args.length + 5];

		System.arraycopy(args, 0, completeArgs, 0, args.length);

		completeArgs[args.length] = "--update-snapshots";
		completeArgs[args.length + 1] =
			"-Drepository.private.username=" +
				System.getProperty("repository.private.username");
		completeArgs[args.length + 2] =
			"-Drepository.private.password=" +
				System.getProperty("repository.private.password");

		String javaVersion = System.getProperty("java.version");

		if (javaVersion.startsWith("17")) {
			javaVersion = "17";
		}

		if (javaVersion.startsWith("21")) {
			javaVersion = "21";
		}

		completeArgs[args.length + 3] =
			"-Djava.compiler.source.version=" + javaVersion;
		completeArgs[args.length + 4] =
			"-Djava.compiler.target.version=" + javaVersion;

		MavenExecutor.Result result = mavenExecutor.execute(
			projectDir, completeArgs);

		if (buildAndFail) {
			Assert.assertFalse(
				"Expected build to fail. " + result.exitCode,
				result.exitCode == 0);
		}
		else {
			Assert.assertEquals(result.output, 0, result.exitCode);
		}

		return result.output;
	}

	public default String executeMaven(
			File projectDir, MavenExecutor mavenExecutor, String... args)
		throws Exception {

		return executeMaven(projectDir, false, mavenExecutor, args);
	}

	public default String getDefaultLiferayVersion() {
		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		return projectTemplatesArgs.getLiferayVersion();
	}

	public default String getJavaxOrJakartaPackagePrefix(
		String liferayVersion) {

		if (VersionUtil.isJakartaCompatibleVersion(liferayVersion)) {
			return "jakarta";
		}

		return "javax";
	}

	public default String getLiferayWorkspaceProduct(String liferayVersion) {
		if (liferayVersion.startsWith("20")) {
			return "dxp-2024.q1.1";
		}
		else if (liferayVersion.startsWith("7.0")) {
			return "dxp-7.0-sp17";
		}
		else if (liferayVersion.startsWith("7.1")) {
			return "dxp-7.1-sp7";
		}
		else if (liferayVersion.startsWith("7.2")) {
			return "dxp-7.2-sp7";
		}
		else if (liferayVersion.startsWith("7.3")) {
			return "portal-7.3-ga8";
		}
		else if (liferayVersion.startsWith("7.4")) {
			return "portal-7.4-ga56";
		}

		return null;
	}

	public default File getWorkspaceDir(File dir) {
		File gradleParent = findParentFile(
			dir,
			new String[] {
				SETTINGS_GRADLE_FILE_NAME, GRADLE_PROPERTIES_FILE_NAME
			},
			true);

		if ((gradleParent != null) && gradleParent.exists()) {
			return gradleParent;
		}

		FilenameFilter gradleFilter = (file, name) ->
			SETTINGS_GRADLE_FILE_NAME.equals(name) ||
			GRADLE_PROPERTIES_FILE_NAME.equals(name);

		File[] matches = dir.listFiles(gradleFilter);

		if (Objects.nonNull(matches) && (matches.length > 0)) {
			return dir;
		}

		return null;
	}

	public default boolean isBuildProjects() {
		if (Validator.isNotNull(BUILD_PROJECTS) &&
			BUILD_PROJECTS.equals("true")) {

			return true;
		}

		return false;
	}

	public default void modifyElementText(
			Document document, String expression, String newText)
		throws XPathExpressionException {

		XPathFactory xPathFactory = XPathFactory.newInstance();

		XPath xPath = xPathFactory.newXPath();

		XPathExpression xPathExpression = xPath.compile(expression);

		NodeList nodeList = (NodeList)xPathExpression.evaluate(
			document, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			node.setTextContent(newText);
		}
	}

	public default File removeGradlePropertiesInWorkspace(
			File workspaceDir, String gradleProperties)
		throws IOException {

		File gradlePropertiesFile = new File(workspaceDir, "gradle.properties");

		List<String> originalPropertiesList = Files.readAllLines(
			gradlePropertiesFile.toPath());

		StringBuilder sb = new StringBuilder();

		for (String property : originalPropertiesList) {
			if (property.equals(gradleProperties)) {
				continue;
			}

			sb.append(property);
			sb.append(System.lineSeparator());
		}

		String propertiesContent = sb.toString();

		Files.write(
			gradlePropertiesFile.toPath(), propertiesContent.getBytes(),
			StandardOpenOption.TRUNCATE_EXISTING);

		return gradlePropertiesFile;
	}

	public default void replaceElementByName(
			Document document, String oldElementName, String newElementName,
			String textString)
		throws XPathExpressionException {

		XPathFactory xPathFactory = XPathFactory.newInstance();

		XPath xPath = xPathFactory.newXPath();

		XPathExpression expression = xPath.compile("//" + oldElementName);

		NodeList nodeList = (NodeList)expression.evaluate(
			document, XPathConstants.NODESET);

		if (nodeList.getLength() > 0) {
			Node node = nodeList.item(0);

			Node parentNode = node.getParentNode();

			parentNode.removeChild(node);

			Element newElement = document.createElement(newElementName);

			parentNode.appendChild(newElement);

			if (textString != null) {
				Text text = document.createTextNode(textString);

				newElement.appendChild(text);
			}
		}
	}

	public default List<String> sanitizeLines(List<String> lines) {
		List<String> sanitizedLines = new ArrayList<>();

		for (String line : lines) {
			line = line.replaceAll("\\?t=[0-9]+", "");

			sanitizedLines.add(line);
		}

		return sanitizedLines;
	}

	public default void testBuildTemplateNpm(
			TemporaryFolder temporaryFolder, MavenExecutor mavenExecutor,
			String template, String name, String packageName, String className,
			String liferayProduct, String liferayVersion,
			String nodePackageManager, URI gradleDistribution)
		throws Exception {

		File gradleWorkspaceDir = buildWorkspace(
			temporaryFolder, "gradle", "gradleWS", liferayVersion,
			mavenExecutor);

		String liferayWorkspaceProduct = getLiferayWorkspaceProduct(
			liferayVersion);

		if (liferayWorkspaceProduct != null) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.product=" + liferayWorkspaceProduct);
		}

		File gradleWorkspaceModulesDir = new File(
			gradleWorkspaceDir, "modules");

		File gradleProjectDir = buildTemplateWithGradle(
			gradleWorkspaceModulesDir, template, name, "--liferay-product",
			liferayProduct, "--liferay-version", liferayVersion);

		testGradlePortalReleaseDependency(gradleProjectDir, liferayVersion);

		testContains(
			gradleProjectDir, "package.json",
			"build/resources/main/META-INF/resources", "esbuild\": \"^0.20.2",
			"\"main\": \"js/index.js\"");

		testNotContains(
			gradleProjectDir, "package.json",
			"target/classes/META-INF/resources");

		if (VersionUtil.isJakartaCompatibleVersion(liferayVersion)) {
			testPortletUpdatedForJakarta(
				gradleProjectDir, packageName, className);
		}

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", liferayVersion, mavenExecutor);

		File mavenModulesDir = new File(mavenWorkspaceDir, "modules");

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, name, "com.test",
			mavenExecutor, "-DclassName=" + className,
			"-DliferayProduct=" + liferayProduct,
			"-DliferayVersion=" + liferayVersion, "-Dpackage=" + packageName);

		testContains(
			mavenProjectDir, "package.json",
			"target/classes/META-INF/resources");

		testNotContains(
			mavenProjectDir, "package.json",
			"build/resources/main/META-INF/resources");

		if (Validator.isNotNull(System.getenv("JENKINS_HOME"))) {
			addNpmrc(gradleProjectDir);
			addNpmrc(mavenProjectDir);
			configureExecutePackageManagerTask(gradleProjectDir);
			configurePomNpmConfiguration(mavenProjectDir, nodePackageManager);
		}

		if (isBuildProjects()) {
			File gradleOutputDir = new File(gradleProjectDir, "build/libs");
			File mavenOutputDir = new File(mavenProjectDir, "target");

			buildProjects(
				gradleDistribution, mavenExecutor, gradleWorkspaceDir,
				mavenProjectDir, gradleOutputDir, mavenOutputDir,
				":modules:" + name + GRADLE_TASK_PATH_BUILD);
		}
	}

	public default File testBuildTemplatePortlet(
			TemporaryFolder temporaryFolder, String template, String name,
			String packageName, String liferayProduct, String liferayVersion,
			MavenExecutor mavenExecutor, URI gradleDistribution)
		throws Exception {

		String className;

		if (name.equals("portlet-portlet")) {
			className = "Portlet";
			packageName = "portlet.portlet";
		}
		else {
			String firstChar = name.substring(0, 1);

			className = firstChar.toUpperCase() + name.substring(1);
		}

		File gradleWorkspaceDir = buildWorkspace(
			temporaryFolder, "gradle", "gradleWS", liferayVersion,
			mavenExecutor);

		String liferayWorkspaceProduct = getLiferayWorkspaceProduct(
			liferayVersion);

		if (liferayWorkspaceProduct != null) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.product=" + liferayWorkspaceProduct);
		}

		String modulesDir = "modules";

		File gradleWorkspaceModulesDir = new File(
			gradleWorkspaceDir, modulesDir);

		File gradleProjectDir = buildTemplateWithGradle(
			gradleWorkspaceModulesDir, template, name, "--liferay-product",
			liferayProduct, "--liferay-version", liferayVersion,
			"--package-name", packageName);

		String[] resourceFileNames;

		if (template.equals("freemarker-portlet")) {
			resourceFileNames = new String[] {
				"resources/templates/init.ftl", "resources/templates/view.ftl",
				"resources/META-INF/resources/css/main.scss"
			};

			testStartsWith(
				gradleProjectDir, "src/main/resources/templates/view.ftl",
				FREEMARKER_PORTLET_VIEW_FTL_PREFIX);
		}
		else if (template.equals("mvc-portlet")) {
			resourceFileNames = new String[] {
				"resources/META-INF/resources/init.jsp",
				"resources/META-INF/resources/view.jsp",
				"resources/META-INF/resources/css/main.scss"
			};

			if (VersionUtil.isJakartaCompatibleVersion(liferayVersion)) {
				testPortletUpdatedForJakarta(
					gradleProjectDir, packageName, className);
			}
		}
		else {
			resourceFileNames = new String[] {
				"webapp/init.jsp", "webapp/view.jsp", "webapp/css/main.scss"
			};
		}

		for (String resourceFileName : resourceFileNames) {
			testExists(gradleProjectDir, "src/main/" + resourceFileName);
		}

		if ((VersionUtil.getMinorVersion(liferayVersion) < 3) ||
			(VersionUtil.getMajorVersion(liferayVersion) > 7)) {

			testContains(
				gradleProjectDir, "build.gradle", DEPENDENCY_RELEASE_DXP_API);
		}
		else {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_RELEASE_PORTAL_API);
		}

		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", liferayVersion, mavenExecutor);

		File mavenModulesDir = new File(mavenWorkspaceDir, modulesDir);

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, name, "com.test",
			mavenExecutor, "-DclassName=" + className,
			"-DliferayProduct=" + liferayProduct,
			"-DliferayVersion=" + liferayVersion, "-Dpackage=" + packageName);

		if (!liferayVersion.startsWith("7.0") && !template.contains("war")) {
			testContains(
				mavenProjectDir, "bnd.bnd",
				"-contract: JavaPortlet,JavaServlet");
		}

		if (isBuildProjects()) {
			File gradleOutputDir = new File(gradleProjectDir, "build/libs");
			File mavenOutputDir = new File(mavenProjectDir, "target");

			buildProjects(
				gradleDistribution, mavenExecutor, gradleWorkspaceDir,
				mavenProjectDir, gradleOutputDir, mavenOutputDir,
				":" + modulesDir + ":" + name + GRADLE_TASK_PATH_BUILD);
		}

		return gradleProjectDir;
	}

	public default File testBuildTemplateProjectWarInWorkspace(
			TemporaryFolder temporaryFolder, URI gradleDistribution,
			MavenExecutor mavenExecutor, String template, String name,
			String liferayVersion)
		throws Exception {

		File gradleWorkspaceDir = buildWorkspace(
			temporaryFolder, liferayVersion);

		String liferayProduct = "portal";

		if (liferayVersion.startsWith("20")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.target.platform.version=2024.q1.1");

			liferayProduct = "dxp";
		}
		else if (liferayVersion.startsWith("7.0")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.target.platform.version=7.0.10.17");

			liferayProduct = "dxp";
		}
		else if (liferayVersion.startsWith("7.1")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.target.platform.version=7.1.10.7");

			liferayProduct = "dxp";
		}
		else if (liferayVersion.startsWith("7.2")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.target.platform.version=7.2.10.7");

			liferayProduct = "dxp";
		}
		else if (liferayVersion.startsWith("7.3")) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.target.platform.version=7.3.7");
		}
		else {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.target.platform.version=7.4.3.56");
		}

		File modulesDir = new File(gradleWorkspaceDir, "modules");

		File gradleProjectDir = buildTemplateWithGradle(
			modulesDir, template, name, "--dependency-management-enabled",
			"--liferay-product", liferayProduct, "--liferay-version",
			liferayVersion);

		if (!template.equals("war-hook") && !template.equals("theme")) {
			testContains(
				gradleProjectDir, "build.gradle", "buildscript {",
				"cssBuilder group", "portalCommonCSS group");
		}

		if (template.equals("theme")) {
			testContains(
				gradleProjectDir, "build.gradle", "buildscript {",
				"apply plugin: \"com.liferay.portal.tools.theme.builder\"",
				"repositories {");
		}

		testNotContains(
			gradleProjectDir, "build.gradle", "apply plugin: \"war\"");
		testNotContains(
			gradleProjectDir, "build.gradle", true, "^repositories \\{.*");
		testNotContains(gradleProjectDir, "build.gradle", "version: \"[0-9].*");

		if (VersionUtil.isJakartaCompatibleVersion(liferayVersion)) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/web.xml",
				"version=\"6.0\" xmlns=\"https://jakarta.ee/xml/ns/jakartaee",
				"xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee " +
					"https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd");
		}

		File mavenWorkspaceDir = buildWorkspace(
			temporaryFolder, "maven", "mavenWS", liferayVersion, mavenExecutor);

		String liferayWorkspaceProduct = getLiferayWorkspaceProduct(
			liferayVersion);

		if (liferayWorkspaceProduct != null) {
			writeGradlePropertiesInWorkspace(
				gradleWorkspaceDir,
				"liferay.workspace.product=" + liferayWorkspaceProduct);
		}

		File mavenModulesDir = new File(mavenWorkspaceDir, "modules");

		File mavenProjectDir = buildTemplateWithMaven(
			mavenModulesDir, mavenModulesDir, template, name, "com.test",
			mavenExecutor, "-DclassName=" + name,
			"-DliferayProduct=" + liferayProduct,
			"-DliferayVersion=" + liferayVersion,
			"-Dpackage=" + name.toLowerCase());

		if (isBuildProjects()) {
			File gradleOutputDir = new File(gradleProjectDir, "build/libs");
			File mavenOutputDir = new File(mavenProjectDir, "target");

			buildProjects(
				gradleDistribution, mavenExecutor, gradleWorkspaceDir,
				mavenProjectDir, gradleOutputDir, mavenOutputDir,
				":modules:" + name + GRADLE_TASK_PATH_BUILD);
		}

		return gradleProjectDir;
	}

	public default void testBuildTemplateServiceBuilder(
			File gradleProjectDir, File mavenProjectDir, final File rootProject,
			String name, String packageName, final String projectPath,
			URI gradleDistribution, MavenExecutor mavenExecutor)
		throws Exception {

		String apiProjectName = name + "-api";
		final String serviceProjectName = name + "-service";

		testContains(
			gradleProjectDir, apiProjectName + "/bnd.bnd", "Export-Package:\\",
			packageName + ".exception,\\", packageName + ".model,\\",
			packageName + ".service,\\", packageName + ".service.persistence");
		testContains(
			gradleProjectDir, serviceProjectName + "/bnd.bnd",
			"Liferay-Service: true");

		if (!isBuildProjects()) {
			return;
		}

		testChangePortletModelHintsXml(
			gradleProjectDir, serviceProjectName,
			new Callable<Void>() {

				@Override
				public Void call() throws Exception {
					executeGradle(
						rootProject, gradleDistribution,
						projectPath + ":" + serviceProjectName +
							GRADLE_TASK_PATH_BUILD_SERVICE);

					return null;
				}

			});

		executeGradle(
			rootProject, gradleDistribution,
			projectPath + ":" + serviceProjectName + GRADLE_TASK_PATH_BUILD);

		File gradleApiBundleFile = testExists(
			gradleProjectDir,
			apiProjectName + "/build/libs/" + packageName + ".api-1.0.0.jar");
		File gradleServiceBundleFile = testExists(
			gradleProjectDir,
			serviceProjectName + "/build/libs/" + packageName +
				".service-1.0.0.jar");

		if (!name.contains("sample")) {
			testChangePortletModelHintsXml(
				mavenProjectDir, serviceProjectName,
				new Callable<Void>() {

					@Override
					public Void call() throws Exception {
						executeMaven(
							new File(mavenProjectDir, serviceProjectName),
							mavenExecutor, MAVEN_GOAL_BUILD_SERVICE);

						return null;
					}

				});

			File gradleServicePropertiesFile = new File(
				gradleProjectDir,
				serviceProjectName + "/src/main/resources/service.properties");
			File mavenServicePropertiesFile = new File(
				mavenProjectDir,
				serviceProjectName + "/src/main/resources/service.properties");

			Files.copy(
				gradleServicePropertiesFile.toPath(),
				mavenServicePropertiesFile.toPath(),
				StandardCopyOption.REPLACE_EXISTING);

			executeMaven(mavenProjectDir, mavenExecutor, MAVEN_GOAL_PACKAGE);

			File mavenApiBundleFile = testExists(
				mavenProjectDir,
				apiProjectName + "/target/" + name + "-api-1.0.0.jar");
			File mavenServiceBundleFile = testExists(
				mavenProjectDir,
				serviceProjectName + "/target/" + name + "-service-1.0.0.jar");

			testBundlesDiff(gradleApiBundleFile, mavenApiBundleFile);
			testBundlesDiff(gradleServiceBundleFile, mavenServiceBundleFile);
		}
	}

	public default File testBuildTemplateWithWorkspace(
			TemporaryFolder temporaryFolder, URI gradleDistribution,
			String template, String name, String jarFilePath, String... args)
		throws Exception {

		List<String> argsList = Arrays.asList(args);

		File workspaceDir = null;

		if (argsList.contains("7.0.6-2")) {
			workspaceDir = buildWorkspace(temporaryFolder, "7.0.6-2");

			writeGradlePropertiesInWorkspace(
				workspaceDir,
				"liferay.workspace.target.platform.version=7.0.6-2");
		}
		else if (argsList.contains("7.1.3-1")) {
			workspaceDir = buildWorkspace(temporaryFolder, "7.1.3-1");

			writeGradlePropertiesInWorkspace(
				workspaceDir,
				"liferay.workspace.target.platform.version=7.1.3-1");
		}
		else if (argsList.contains("7.2.1-1")) {
			workspaceDir = buildWorkspace(temporaryFolder, "7.2.1-1");

			writeGradlePropertiesInWorkspace(
				workspaceDir,
				"liferay.workspace.target.platform.version=7.2.1-1");
		}
		else if (argsList.contains("7.3.7")) {
			workspaceDir = buildWorkspace(temporaryFolder, "7.3.7");

			writeGradlePropertiesInWorkspace(
				workspaceDir,
				"liferay.workspace.target.platform.version=7.3.7");
		}
		else {
			workspaceDir = buildWorkspace(
				temporaryFolder, getDefaultLiferayVersion());

			writeGradlePropertiesInWorkspace(
				workspaceDir,
				"liferay.workspace.target.platform.version=7.4.3.56");
		}

		File modulesDir = new File(workspaceDir, "modules");

		File workspaceProjectDir = buildTemplateWithGradle(
			modulesDir, template, name, args);

		testNotContains(
			workspaceProjectDir, "build.gradle", true, "^repositories \\{.*");
		testNotContains(
			workspaceProjectDir, "build.gradle", "version: \"[0-9].*");

		if (isBuildProjects()) {
			executeGradle(
				workspaceDir, gradleDistribution,
				":modules:" + name + GRADLE_TASK_PATH_BUILD);

			testExists(workspaceProjectDir, jarFilePath);
		}

		return workspaceProjectDir;
	}

	public default void testBundlesDiff(File bundleFile1, File bundleFile2)
		throws Exception {

		DiffPluginImpl diffPluginImpl = new DiffPluginImpl();

		diffPluginImpl.setIgnore(BUNDLES_DIFF_IGNORES);

		Tree tree1 = diffPluginImpl.tree(bundleFile1);
		Tree tree2 = diffPluginImpl.tree(bundleFile2);

		compareDiff(tree1.diff(tree2), bundleFile1, bundleFile2);
	}

	public default void testChangePortletModelHintsXml(
			File projectDir, String serviceProjectName,
			Callable<Void> buildServiceCallable)
		throws Exception {

		buildServiceCallable.call();

		File file = testExists(
			projectDir,
			serviceProjectName +
				"/src/main/resources/META-INF/portlet-model-hints.xml");

		Path path = file.toPath();

		String content = FileUtil.read(path);

		String newContent = content.replace(
			"<field name=\"field5\" type=\"String\" />",
			"<field name=\"field5\" type=\"String\">\n\t\t\t<hint-collection " +
				"name=\"CLOB\" />\n\t\t</field>");

		Assert.assertNotEquals("Unexpected " + file, content, newContent);

		Files.write(path, newContent.getBytes(StandardCharsets.UTF_8));

		buildServiceCallable.call();

		Assert.assertEquals(
			"Changes in " + file + " incorrectly overridden", newContent,
			FileUtil.read(path));
	}

	public default File testContains(
			File dir, String fileName, boolean regex, String... strings)
		throws IOException {

		return testContainsOrNot(dir, fileName, regex, true, strings);
	}

	public default File testContains(
			File dir, String fileName, String... strings)
		throws IOException {

		return testContains(dir, fileName, false, strings);
	}

	public default File testContainsOrNot(
			File dir, String fileName, boolean regex, boolean contains,
			String... strings)
		throws IOException {

		File file = testExists(dir, fileName);

		String content = FileUtil.read(file.toPath());

		for (String s : strings) {
			boolean found;

			if (regex) {
				Pattern pattern = Pattern.compile(
					s, Pattern.DOTALL | Pattern.MULTILINE);

				Matcher matcher = pattern.matcher(content);

				found = matcher.matches();
			}
			else {
				found = content.contains(s);
			}

			if (contains) {
				Assert.assertTrue("Not found in " + fileName + ": " + s, found);
			}
			else {
				Assert.assertFalse("Found in " + fileName + ": " + s, found);
			}
		}

		return file;
	}

	public default File testExecutable(File dir, String fileName) {
		File file = testExists(dir, fileName);

		Assert.assertTrue(fileName + " is not executable", file.canExecute());

		return file;
	}

	public default File testExists(File dir, String fileName) {
		File file = new File(dir, fileName);

		Assert.assertTrue("Missing " + fileName, file.exists());

		return file;
	}

	public default void testExists(ZipFile zipFile, String name) {
		Assert.assertNotNull("Missing " + name, zipFile.getEntry(name));
	}

	public default void testFileUpdatedForJakarta(
			File gradleProjectDir, String filePath)
		throws IOException {

		testNotContains(gradleProjectDir, filePath, "javax");
		testContains(gradleProjectDir, filePath, "jakarta");

		if (filePath.endsWith(".jsp")) {
			testNotContains(
				gradleProjectDir, filePath,
				"http://java.sun.com/jsp/jstl/core");
			testContains(gradleProjectDir, filePath, "jakarta.tags.core");
		}
	}

	public default void testGradlePortalReleaseDependency(
			File gradleProjectDir, String liferayVersion)
		throws IOException {

		if (VersionUtil.isLiferayQuarterlyVersion(liferayVersion) ||
			(VersionUtil.getMinorVersion(liferayVersion) < 3)) {

			testContains(
				gradleProjectDir, "build.gradle", DEPENDENCY_RELEASE_DXP_API);
		}
		else {
			testContains(
				gradleProjectDir, "build.gradle",
				DEPENDENCY_RELEASE_PORTAL_API);
		}
	}

	public default File testNotContains(
			File dir, String fileName, boolean regex, String... strings)
		throws IOException {

		return testContainsOrNot(dir, fileName, regex, false, strings);
	}

	public default File testNotContains(
			File dir, String fileName, String... strings)
		throws IOException {

		return testNotContains(dir, fileName, false, strings);
	}

	public default File testNotExists(File dir, String fileName) {
		File file = new File(dir, fileName);

		Assert.assertFalse("Unexpected " + fileName, file.exists());

		return file;
	}

	public default void testPortletUpdatedForJakarta(
			File gradleProjectDir, String packageName, String className)
		throws IOException {

		String portletFolderPath =
			"src/main/java/" + packageName.replace('.', '/') + "/portlet/";

		testFileUpdatedForJakarta(
			gradleProjectDir, portletFolderPath + className + "Portlet.java");

		testFileUpdatedForJakarta(
			gradleProjectDir, "src/main/resources/META-INF/resources/init.jsp");
		testFileUpdatedForJakarta(
			gradleProjectDir, "src/main/resources/content/Language.properties");
	}

	public default File testStartsWith(File dir, String fileName, String prefix)
		throws IOException {

		File file = testExists(dir, fileName);

		String content = FileUtil.read(file.toPath());

		Assert.assertTrue(
			fileName + " must start with \"" + prefix + "\"",
			content.startsWith(prefix));

		return file;
	}

	public default void testTemplateWarHookDTD(
			File gradleProjectDir, String liferayVersion)
		throws Exception {

		if (liferayVersion.startsWith("7.0")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
				"liferay-hook_7_0_0.dtd");
		}
		else if (liferayVersion.startsWith("7.1")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
				"liferay-hook_7_1_0.dtd");
		}
		else if (liferayVersion.startsWith("7.2")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
				"liferay-hook_7_2_0.dtd");
		}
		else if (liferayVersion.startsWith("7.3")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
				"liferay-hook_7_3_0.dtd");
		}
		else if (liferayVersion.startsWith("7.4")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-hook.xml",
				"liferay-hook_7_4_0.dtd");
		}
	}

	public default void testTemplateWarPortletDTD(
			File gradleProjectDir, String liferayVersion)
		throws Exception {

		if (liferayVersion.startsWith("20") ||
			liferayVersion.startsWith("7.4")) {

			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
				"liferay-display_7_4_0.dtd");

			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
				"liferay-portlet-app_7_4_0.dtd");
		}
		else if (liferayVersion.startsWith("7.0")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
				"liferay-display_7_0_0.dtd");

			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
				"liferay-portlet-app_7_0_0.dtd");
		}
		else if (liferayVersion.startsWith("7.1")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
				"liferay-display_7_1_0.dtd");

			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
				"liferay-portlet-app_7_1_0.dtd");
		}
		else if (liferayVersion.startsWith("7.2")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
				"liferay-display_7_2_0.dtd");

			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
				"liferay-portlet-app_7_2_0.dtd");
		}
		else if (liferayVersion.startsWith("7.3")) {
			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-display.xml",
				"liferay-display_7_3_0.dtd");

			testContains(
				gradleProjectDir, "src/main/webapp/WEB-INF/liferay-portlet.xml",
				"liferay-portlet-app_7_3_0.dtd");
		}
	}

	public default void testTLDUpdatedForJakarta(
			File gradleProjectDir, String tldFilePath)
		throws IOException {

		testFileUpdatedForJakarta(gradleProjectDir, tldFilePath);

		testContains(
			gradleProjectDir, tldFilePath,
			"xmlns=\"https://jakarta.ee/xml/ns/jakartaee\"",
			"xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee " +
				"https://jakarta.ee/xml/ns/jakartaee" +
					"/web-jsptaglibrary_3_0.xsd");
	}

	public default void testWarsDiff(File warFile1, File warFile2)
		throws IOException {

		DifferenceCalculator differenceCalculator = new DifferenceCalculator(
			warFile1, warFile2);

		differenceCalculator.setFilenameRegexToIgnore(
			Collections.singleton(".*META-INF.*"));
		differenceCalculator.setIgnoreTimestamps(true);

		Differences differences = differenceCalculator.getDifferences();

		if (!differences.hasDifferences()) {
			return;
		}

		StringBuilder message = new StringBuilder();

		message.append("WAR ");
		message.append(warFile1);
		message.append(" and ");
		message.append(warFile2);
		message.append(" do not match:");
		message.append(System.lineSeparator());

		boolean realChange;

		Map<String, ZipArchiveEntry> added = differences.getAdded();
		Map<String, ZipArchiveEntry[]> changed = differences.getChanged();
		Map<String, ZipArchiveEntry> removed = differences.getRemoved();

		if (added.isEmpty() && !changed.isEmpty() && removed.isEmpty()) {
			realChange = false;

			ZipFile zipFile1 = null;
			ZipFile zipFile2 = null;

			try {
				zipFile1 = new ZipFile(warFile1);
				zipFile2 = new ZipFile(warFile2);

				for (Map.Entry<String, ZipArchiveEntry[]> entry :
						changed.entrySet()) {

					ZipArchiveEntry[] zipArchiveEntries = entry.getValue();

					ZipArchiveEntry zipArchiveEntry1 = zipArchiveEntries[0];
					ZipArchiveEntry zipArchiveEntry2 = zipArchiveEntries[0];

					if (zipArchiveEntry1.isDirectory() &&
						zipArchiveEntry2.isDirectory() &&
						(zipArchiveEntry1.getSize() ==
							zipArchiveEntry2.getSize()) &&
						(zipArchiveEntry1.getCompressedSize() <= 2) &&
						(zipArchiveEntry2.getCompressedSize() <= 2)) {

						// Skip zipdiff bug

						continue;
					}

					try (InputStream inputStream1 = zipFile1.getInputStream(
							zipFile1.getEntry(zipArchiveEntry1.getName()));
						InputStream inputStream2 = zipFile2.getInputStream(
							zipFile2.getEntry(zipArchiveEntry2.getName()))) {

						List<String> lines1 = StringTestUtil.readLines(
							inputStream1);
						List<String> lines2 = StringTestUtil.readLines(
							inputStream2);

						lines1 = sanitizeLines(lines1);
						lines2 = sanitizeLines(lines2);

						Patch<String> diff = DiffUtils.diff(lines1, lines2);

						List<Delta<String>> deltas = diff.getDeltas();

						if (deltas.isEmpty()) {
							continue;
						}

						message.append(System.lineSeparator());

						message.append("--- ");
						message.append(zipArchiveEntry1.getName());
						message.append(System.lineSeparator());

						message.append("+++ ");
						message.append(zipArchiveEntry2.getName());
						message.append(System.lineSeparator());

						for (Delta<String> delta : deltas) {
							message.append('\t');
							message.append(delta.getOriginal());
							message.append(System.lineSeparator());

							message.append('\t');
							message.append(delta.getRevised());
							message.append(System.lineSeparator());
						}
					}

					realChange = true;

					break;
				}
			}
			finally {
				ZipFile.closeQuietly(zipFile1);
				ZipFile.closeQuietly(zipFile2);
			}
		}
		else {
			realChange = true;
		}

		Assert.assertFalse(message.toString() + differences, realChange);
	}

	public default File updateGradlePropertiesInWorkspace(
			File workspaceDir, String propertyKey, String propertyValue)
		throws IOException {

		File gradlePropertiesFile = new File(workspaceDir, "gradle.properties");

		Properties gradleProperties = new Properties();

		gradleProperties.load(new FileInputStream(gradlePropertiesFile));

		if (gradleProperties.get(propertyKey) != null) {
			gradleProperties.setProperty(propertyKey, propertyValue);

			try (FileOutputStream fileOutputStream = new FileOutputStream(
					gradlePropertiesFile)) {

				gradleProperties.store(fileOutputStream, null);
			}
		}
		else {
			gradlePropertiesFile = writeGradlePropertiesInWorkspace(
				workspaceDir, propertyKey + "=" + propertyValue);
		}

		return gradlePropertiesFile;
	}

	public default File updateMavenPomElementText(
			File projectDir, String expression, String newText)
		throws Exception {

		File mavenPomFile = new File(projectDir, "pom.xml");

		editXml(
			mavenPomFile,
			document -> {
				try {
					modifyElementText(document, expression, newText);
				}
				catch (XPathExpressionException xPathExpressionException) {
					throw new RuntimeException(xPathExpressionException);
				}
			});

		return mavenPomFile;
	}

	public default File updateMavenPomProperties(
			File projectDir, String oldElementName, String newElementName,
			String text)
		throws Exception {

		File mavenPomFile = new File(projectDir, "pom.xml");

		editXml(
			mavenPomFile,
			document -> {
				try {
					replaceElementByName(
						document, oldElementName, newElementName, text);
				}
				catch (XPathExpressionException xPathExpressionException) {
					throw new RuntimeException(xPathExpressionException);
				}
			});

		return mavenPomFile;
	}

	public default File writeGradlePropertiesInWorkspace(
			File workspaceDir, String gradleProperties)
		throws IOException {

		File gradlePropertiesFile = new File(workspaceDir, "gradle.properties");

		gradleProperties = System.lineSeparator() + gradleProperties;

		Files.write(
			gradlePropertiesFile.toPath(), gradleProperties.getBytes(),
			StandardOpenOption.APPEND);

		return gradlePropertiesFile;
	}

	public default void writeM2TmpForGradleWorkspace(File projectDir)
		throws Exception {

		File settingsGradleFile = new File(
			getWorkspaceDir(projectDir), "settings.gradle");

		List<String> settingsGradleContents = FileUtils.readLines(
			settingsGradleFile, "UTF-8");

		File m2TmpDir = new File("../../../../", ".m2-tmp");

		File m2TmpCanonicalDir = m2TmpDir.getCanonicalFile();

		if (!settingsGradleContents.contains(m2TmpCanonicalDir.toURI())) {
			for (int i = 0; i < settingsGradleContents.size(); i++) {
				String content = settingsGradleContents.get(i);

				if (content.contains("mavenLocal()")) {
					settingsGradleContents.add(i + 1, "\t\tmaven {");

					settingsGradleContents.add(
						i + 2,
						"\t\t\turl \"" + m2TmpCanonicalDir.toURI() + "\"");
					settingsGradleContents.add(i + 3, "\t\t}");

					break;
				}
			}

			FileUtils.writeLines(
				settingsGradleFile, null, settingsGradleContents, null);
		}
	}

	public default void writeM2TmpForMavenWorkspace(File projectDir)
		throws Exception {

		File gettingStartedFile = new File(projectDir, "GETTING_STARTED.md");
		File pomXmlFile = new File(projectDir, "pom.xml");

		if (gettingStartedFile.exists() && pomXmlFile.exists()) {
			File m2TmpDir = new File("../../../../", ".m2-tmp");

			File m2TmpCanonicalDir = m2TmpDir.getCanonicalFile();

			editXml(
				pomXmlFile,
				document -> {
					addNexusRepositoriesElement(
						document, "repositories", "repository",
						String.valueOf(m2TmpCanonicalDir.toURI()));
					addNexusRepositoriesElement(
						document, "repositories", "repository");
					addNexusRepositoriesElement(
						document, "pluginRepositories", "pluginRepository");
					addNexusRepositoriesElement(
						document, "pluginRepositories", "pluginRepository",
						String.valueOf(m2TmpCanonicalDir.toURI()));
				});
		}
	}

}