/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.maven.plugin.builder.task;

import com.liferay.gradle.plugins.maven.plugin.builder.internal.util.GradleUtil;
import com.liferay.gradle.plugins.maven.plugin.builder.internal.util.XMLUtil;
import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.StringUtil;
import com.liferay.gradle.util.Validator;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ProjectDependency;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.file.CopySpec;
import org.gradle.api.file.FileCollection;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.PathSensitive;
import org.gradle.api.tasks.PathSensitivity;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.JavaExecSpec;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andrea Di Giorgi
 */
@CacheableTask
public class BuildPluginDescriptorTask extends DefaultTask {

	public BuildPluginDescriptorTask() {
		_configurationScopeMappings.put(
			JavaPlugin.API_CONFIGURATION_NAME, "compile");

		_pomRepositories.put(
			"liferay-public",
			"http://repository.liferay.com/nexus/content/groups/public");
	}

	@TaskAction
	public void buildPluginDescriptor() {
		Project project = getProject();

		File pomFile = project.file(System.currentTimeMillis() + ".xml");

		File preparedSourceDir = null;

		try {
			if (isUseSetterComments()) {
				preparedSourceDir = new File(
					getTemporaryDir(), "prepared-source");

				_prepareSources(preparedSourceDir);
			}

			_buildPomFile(pomFile, preparedSourceDir);

			_buildPluginDescriptor(pomFile);

			_readdForcedExclusions();
		}
		catch (Exception exception) {
			throw new GradleException(exception.getMessage(), exception);
		}
		finally {
			if (preparedSourceDir != null) {
				project.delete(preparedSourceDir);
			}

			project.delete(pomFile);
		}
	}

	public void configurationScopeMapping(
		String configurationName, String scope) {

		_configurationScopeMappings.put(configurationName, scope);
	}

	public BuildPluginDescriptorTask forcedExclusions(
		Iterable<String> forcedExclusions) {

		GUtil.addToCollection(_forcedExclusions, forcedExclusions);

		return this;
	}

	public BuildPluginDescriptorTask forcedExclusions(
		String... forcedExclusions) {

		return forcedExclusions(Arrays.asList(forcedExclusions));
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getClassesDir() {
		return GradleUtil.toFile(getProject(), _classesDir);
	}

	@Input
	public Map<String, String> getConfigurationScopeMappings() {
		return _configurationScopeMappings;
	}

	@Input
	public Set<String> getForcedExclusions() {
		return _forcedExclusions;
	}

	@Input
	public String getGoalPrefix() {
		return GradleUtil.toString(_goalPrefix);
	}

	@InputFiles
	@PathSensitive(PathSensitivity.RELATIVE)
	public FileCollection getMavenEmbedderClasspath() {
		return _mavenEmbedderClasspath;
	}

	@Input
	public String getMavenEmbedderMainClassName() {
		return GradleUtil.toString(_mavenEmbedderMainClassName);
	}

	@Input
	public String getMavenPluginPluginVersion() {
		return GradleUtil.toString(_mavenPluginPluginVersion);
	}

	@InputFile
	@Optional
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getMavenSettingsFile() {
		return GradleUtil.toFile(getProject(), _mavenSettingsFile);
	}

	@OutputDirectory
	public File getOutputDir() {
		return GradleUtil.toFile(getProject(), _outputDir);
	}

	@Input
	public String getPomArtifactId() {
		return GradleUtil.toString(_pomArtifactId);
	}

	@Input
	public String getPomGroupId() {
		return GradleUtil.toString(_pomGroupId);
	}

	@Input
	public Map<String, Object> getPomRepositories() {
		return _pomRepositories;
	}

	@Input
	public String getPomVersion() {
		return GradleUtil.toString(_pomVersion);
	}

	@InputDirectory
	@PathSensitive(PathSensitivity.RELATIVE)
	public File getSourceDir() {
		return GradleUtil.toFile(getProject(), _sourceDir);
	}

	@Input
	public boolean isMavenDebug() {
		return _mavenDebug;
	}

	@Input
	public boolean isUseSetterComments() {
		return _useSetterComments;
	}

	public BuildPluginDescriptorTask pomRepositories(
		Map<String, ?> pomRepositories) {

		_pomRepositories.putAll(pomRepositories);

		return this;
	}

	public BuildPluginDescriptorTask pomRepository(String id, Object url) {
		_pomRepositories.put(id, url);

		return this;
	}

	public void setClassesDir(Object classesDir) {
		_classesDir = classesDir;
	}

	public void setForcedExclusions(Iterable<String> forcedExclusions) {
		_forcedExclusions.clear();

		forcedExclusions(forcedExclusions);
	}

	public void setForcedExclusions(String... forcedExclusions) {
		setForcedExclusions(Arrays.asList(forcedExclusions));
	}

	public void setGoalPrefix(Object goalPrefix) {
		_goalPrefix = goalPrefix;
	}

	public void setMavenDebug(boolean mavenDebug) {
		_mavenDebug = mavenDebug;
	}

	public void setMavenEmbedderClasspath(
		FileCollection mavenEmbedderClasspath) {

		_mavenEmbedderClasspath = mavenEmbedderClasspath;
	}

	public void setMavenEmbedderMainClassName(
		Object mavenEmbedderMainClassName) {

		_mavenEmbedderMainClassName = mavenEmbedderMainClassName;
	}

	public void setMavenPluginPluginVersion(Object mavenPluginPluginVersion) {
		_mavenPluginPluginVersion = mavenPluginPluginVersion;
	}

	public void setMavenSettingsFile(Object mavenSettingsFile) {
		_mavenSettingsFile = mavenSettingsFile;
	}

	public void setOutputDir(Object outputDir) {
		_outputDir = outputDir;
	}

	public void setPomArtifactId(Object pomArtifactId) {
		_pomArtifactId = pomArtifactId;
	}

	public void setPomGroupId(Object pomGroupId) {
		_pomGroupId = pomGroupId;
	}

	public void setPomRepositories(Map<String, ?> pomRepositories) {
		_pomRepositories.clear();

		pomRepositories(pomRepositories);
	}

	public void setPomVersion(Object pomVersion) {
		_pomVersion = pomVersion;
	}

	public void setSourceDir(Object sourceDir) {
		_sourceDir = sourceDir;
	}

	public void setUseSetterComments(boolean useSetterComments) {
		_useSetterComments = useSetterComments;
	}

	private void _appendDependencyElements(
		Document document, Element dependenciesElement,
		String configurationName, String scope) {

		Project project = getProject();

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration configuration = configurationContainer.findByName(
			configurationName);

		if (configuration == null) {
			return;
		}

		Logger logger = getLogger();

		Set<String> forcedExclusions = getForcedExclusions();

		Configuration resolvableConfiguration = GradleUtil.addConfiguration(
			project, "resolvable" + StringUtil.capitalize(configurationName));

		resolvableConfiguration.extendsFrom(configuration);
		resolvableConfiguration.setVisible(false);

		ResolvedConfiguration resolvedConfiguration =
			resolvableConfiguration.getResolvedConfiguration();

		for (Dependency dependency :
				resolvableConfiguration.getAllDependencies()) {

			Element dependencyElement = document.createElement("dependency");

			dependenciesElement.appendChild(dependencyElement);

			final String dependencyGroup = dependency.getGroup();
			final String dependencyName = dependency.getName();

			String dependencyVersion = dependency.getVersion();

			Set<ResolvedDependency> resolvedDependencies =
				resolvedConfiguration.getFirstLevelModuleDependencies(
					new Spec<Dependency>() {

						@Override
						public boolean isSatisfiedBy(Dependency dependency) {
							if (dependencyGroup.equals(dependency.getGroup()) &&
								dependencyName.equals(dependency.getName())) {

								return true;
							}

							return false;
						}

					});

			if (!resolvedDependencies.isEmpty()) {
				Iterator<ResolvedDependency> iterator =
					resolvedDependencies.iterator();

				ResolvedDependency resolvedDependency = iterator.next();

				dependencyVersion = resolvedDependency.getModuleVersion();
			}
			else if (logger.isWarnEnabled()) {
				logger.warn(
					"Unable to find resolved module version for {}",
					dependency);
			}

			XMLUtil.appendElement(
				document, dependencyElement, "groupId", dependencyGroup);
			XMLUtil.appendElement(
				document, dependencyElement, "artifactId",
				_getDependencyName(dependency));
			XMLUtil.appendElement(
				document, dependencyElement, "version", dependencyVersion);

			XMLUtil.appendElement(document, dependencyElement, "scope", scope);

			if (forcedExclusions.isEmpty()) {
				continue;
			}

			Element exclusionsElement = document.createElement("exclusions");

			dependencyElement.appendChild(exclusionsElement);

			for (String dependencyNotation : forcedExclusions) {
				_appendDependencyExclusionElement(
					document, exclusionsElement, dependencyNotation);
			}
		}
	}

	private void _appendDependencyExclusionElement(
		Document document, Element exclusionsElement,
		String dependencyNotation) {

		String[] tokens = _parseDependencyNotation(dependencyNotation);

		String groupId = tokens[0];
		String artifactId = tokens[1];

		_appendDependencyExclusionElement(
			document, exclusionsElement, groupId, artifactId);
	}

	private void _appendDependencyExclusionElement(
		Document document, Element exclusionsElement, String groupId,
		String artifactId) {

		Element exclusionElement = document.createElement("exclusion");

		exclusionsElement.appendChild(exclusionElement);

		XMLUtil.appendElement(
			document, exclusionElement, "artifactId", artifactId);
		XMLUtil.appendElement(document, exclusionElement, "groupId", groupId);
	}

	private void _appendRepositoryElement(
		Document document, Element repositoriesElement, String id, String url) {

		Element repositoryElement = document.createElement("repository");

		repositoriesElement.appendChild(repositoryElement);

		XMLUtil.appendElement(document, repositoryElement, "id", id);
		XMLUtil.appendElement(document, repositoryElement, "url", url);
	}

	private void _buildPluginDescriptor(final File pomFile) throws Exception {
		final Project project = getProject();

		project.javaexec(
			new Action<JavaExecSpec>() {

				@Override
				public void execute(JavaExecSpec javaExecSpec) {
					Property<String> mainClass = javaExecSpec.getMainClass();

					mainClass.set(getMavenEmbedderMainClassName());

					javaExecSpec.args("--batch-mode", "--errors");

					Logger logger = getLogger();

					if (logger.isInfoEnabled()) {
						javaExecSpec.args("--debug");
					}
					else if (logger.isQuietEnabled()) {
						javaExecSpec.args("--quiet");
					}

					javaExecSpec.args("--file");
					javaExecSpec.args(project.relativePath(pomFile));

					File mavenSettingsFile = getMavenSettingsFile();

					if (mavenSettingsFile != null) {
						javaExecSpec.args("--settings");
						javaExecSpec.args(
							project.relativePath(mavenSettingsFile));
					}

					javaExecSpec.args("-Dencoding=UTF-8");

					javaExecSpec.args("plugin:descriptor");

					javaExecSpec.setClasspath(getMavenEmbedderClasspath());
					javaExecSpec.setDebug(isMavenDebug());

					javaExecSpec.systemProperty(
						"maven.multiModuleProjectDirectory",
						project.getProjectDir());
				}

			});

		File dir = new File(getClassesDir(), "META-INF/maven");

		Files.walkFileTree(
			dir.toPath(),
			new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(
						Path filePath, BasicFileAttributes basicFileAttributes)
					throws IOException {

					String fileName = String.valueOf(filePath.getFileName());

					if (fileName.endsWith(".xml")) {
						try {
							_formatXML(filePath);
						}
						catch (Exception exception) {
						}
					}

					return FileVisitResult.CONTINUE;
				}

			});

		File outputDir = getOutputDir();

		project.delete(outputDir);

		Files.move(dir.toPath(), outputDir.toPath());
	}

	private void _buildPomFile(File pomFile, File sourceDir) throws Exception {
		Project project = getProject();

		if (sourceDir == null) {
			sourceDir = getSourceDir();
		}

		Document document = XMLUtil.createDocument();

		Element projectElement = document.createElementNS(
			"http://maven.apache.org/POM/4.0.0", "project");

		document.appendChild(projectElement);

		XMLUtil.appendElement(
			document, projectElement, "modelVersion", "4.0.0");
		XMLUtil.appendElement(
			document, projectElement, "groupId", getPomGroupId());
		XMLUtil.appendElement(
			document, projectElement, "artifactId", getPomArtifactId());
		XMLUtil.appendElement(
			document, projectElement, "version", getPomVersion());
		XMLUtil.appendElement(
			document, projectElement, "packaging", "maven-plugin");

		// Build

		Element buildElement = document.createElement("build");

		projectElement.appendChild(buildElement);

		XMLUtil.appendElement(
			document, buildElement, "outputDirectory",
			project.relativePath(getClassesDir()));
		XMLUtil.appendElement(
			document, buildElement, "sourceDirectory",
			project.relativePath(sourceDir));

		Element pluginsElement = document.createElement("plugins");

		buildElement.appendChild(pluginsElement);

		Element pluginElement = document.createElement("plugin");

		pluginsElement.appendChild(pluginElement);

		XMLUtil.appendElement(
			document, pluginElement, "groupId", "org.apache.maven.plugins");
		XMLUtil.appendElement(
			document, pluginElement, "artifactId", "maven-plugin-plugin");
		XMLUtil.appendElement(
			document, pluginElement, "version", getMavenPluginPluginVersion());

		String goalPrefix = getGoalPrefix();

		if (Validator.isNotNull(goalPrefix)) {
			Element configurationElement = document.createElement(
				"configuration");

			pluginElement.appendChild(configurationElement);

			XMLUtil.appendElement(
				document, configurationElement, "goalPrefix", goalPrefix);
		}

		// Dependencies

		Element dependenciesElement = document.createElement("dependencies");

		projectElement.appendChild(dependenciesElement);

		Map<String, String> pomConfigurationScopeMappings =
			getConfigurationScopeMappings();

		for (Map.Entry<String, String> entry :
				pomConfigurationScopeMappings.entrySet()) {

			String configurationName = entry.getKey();
			String scope = entry.getValue();

			_appendDependencyElements(
				document, dependenciesElement, configurationName, scope);
		}

		// Repositories

		Map<String, Object> pomRepositories = getPomRepositories();

		if (!pomRepositories.isEmpty()) {
			Element repositoriesElement = document.createElement(
				"repositories");

			projectElement.appendChild(repositoriesElement);

			for (Map.Entry<String, Object> entry : pomRepositories.entrySet()) {
				String id = entry.getKey();
				String url = GradleUtil.toString(entry.getValue());

				_appendRepositoryElement(
					document, repositoriesElement, id, url);
			}
		}

		XMLUtil.write(document, pomFile);
	}

	private void _formatXML(Path filePath) throws Exception {
		String content = new String(
			Files.readAllBytes(filePath), StandardCharsets.UTF_8);

		SAXReader saxReader = new SAXReader();

		org.dom4j.Document document = saxReader.read(new StringReader(content));

		OutputFormat outputFormat = OutputFormat.createPrettyPrint();

		outputFormat.setExpandEmptyElements(false);
		outputFormat.setIndent("\t");
		outputFormat.setLineSeparator("\n");
		outputFormat.setTrimText(true);

		StringWriter stringWriter = new StringWriter();

		XMLWriter xmlWriter = new XMLWriter(stringWriter, outputFormat);

		xmlWriter.write(document);

		content = stringWriter.toString();

		content = content.trim();

		while (content.contains(" \n")) {
			content = content.replace(" \n", "\n");
		}

		content = content.replace("-->\n<", "-->\n\n<");

		content = content.replace(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>",
			"<?xml version=\"1.0\"?>");

		Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
	}

	private String _getComments(JavaMethod javaMethod) {
		String code = javaMethod.getCodeBlock();

		int start = code.indexOf("/**");

		if (start < 0) {
			throw new GradleException("Unable to find comments start " + code);
		}

		int end = code.indexOf("*/", start);

		if (end < 0) {
			throw new GradleException("Unable to find comments end " + code);
		}

		return code.substring(start, end + 2);
	}

	private String _getDependencyName(Dependency dependency) {
		if (dependency instanceof ProjectDependency) {
			ProjectDependency projectDependency = (ProjectDependency)dependency;

			Project dependencyProject =
				projectDependency.getDependencyProject();

			try {
				return GradleUtil.getArchivesBaseName(dependencyProject);
			}
			catch (IllegalStateException illegalStateException) {
				Logger logger = getLogger();

				if (logger.isWarnEnabled()) {
					logger.warn(
						"Unable to find name for " + dependency,
						illegalStateException);
				}
			}
		}

		return dependency.getName();
	}

	private String _getTypeName(Type type) {
		String name = type.getFullyQualifiedName();

		int pos = name.lastIndexOf('.');

		if (pos != -1) {
			name = name.substring(pos + 1);
		}

		return name;
	}

	private String[] _parseDependencyNotation(String dependencyNotation) {
		String[] tokens = dependencyNotation.split(":");

		if (tokens.length != 3) {
			throw new GradleException(
				"Unable to parse dependency notation " + dependencyNotation);
		}

		return tokens;
	}

	private void _prepareSource(JavaClass javaClass) throws Exception {
		StringBuilder sb = new StringBuilder();

		for (BeanProperty beanProperty : javaClass.getBeanProperties()) {
			JavaMethod javaMethod = beanProperty.getMutator();

			DocletTag parameterDocletTag = javaMethod.getTagByName("parameter");

			if (parameterDocletTag == null) {
				continue;
			}

			sb.append(_getComments(javaMethod));
			sb.append('\n');
			sb.append("private ");

			sb.append(_getTypeName(beanProperty.getType()));

			sb.append(' ');
			sb.append(beanProperty.getName());
			sb.append(';');
			sb.append('\n');
		}

		if (sb.length() == 0) {
			return;
		}

		JavaSource javaSource = javaClass.getSource();

		URL url = javaSource.getURL();

		Path path = Paths.get(url.toURI());

		String code = new String(
			Files.readAllBytes(path), StandardCharsets.UTF_8);

		int pos = code.lastIndexOf('}');

		code = code.substring(0, pos) + sb.toString() + code.substring(pos);

		Files.write(path, code.getBytes(StandardCharsets.UTF_8));
	}

	private void _prepareSources(final File preparedSourceDir)
		throws Exception {

		Project project = getProject();

		project.copy(
			new Action<CopySpec>() {

				@Override
				public void execute(CopySpec copySpec) {
					copySpec.from(getSourceDir());
					copySpec.include("**/*Mojo.java");
					copySpec.into(preparedSourceDir);
				}

			});

		JavaDocBuilder javaDocBuilder = new JavaDocBuilder();

		javaDocBuilder.addSourceTree(preparedSourceDir);

		for (JavaClass javaClass : javaDocBuilder.getClasses()) {
			_prepareSource(javaClass);
		}
	}

	private void _readdForcedExclusions() throws Exception {
		Set<String> forcedExclusions = getForcedExclusions();

		if (forcedExclusions.isEmpty()) {
			return;
		}

		File file = new File(getOutputDir(), "plugin.xml");

		Path path = file.toPath();

		String content = new String(
			Files.readAllBytes(path), StandardCharsets.UTF_8);

		int pos = content.lastIndexOf("</dependencies>");

		if (pos == -1) {
			Logger logger = getLogger();

			if (logger.isWarnEnabled()) {
				logger.warn("Unable to readd forced exclusions");
			}

			return;
		}

		String indent = "\t";

		while (true) {
			pos--;

			if (content.charAt(pos) != '\t') {
				break;
			}

			indent += "\t";
		}

		StringBuilder sb = new StringBuilder();

		sb.append(content, 0, pos);

		for (String dependencyNotation : forcedExclusions) {
			String[] tokens = _parseDependencyNotation(dependencyNotation);

			String groupId = tokens[0];
			String artifactId = tokens[1];
			String version = tokens[2];

			sb.append("\n");
			sb.append(indent);
			sb.append("<dependency>\n");

			sb.append(indent);
			sb.append("\t<groupId>");
			sb.append(groupId);
			sb.append("</groupId>\n");

			sb.append(indent);
			sb.append("\t<artifactId>");
			sb.append(artifactId);
			sb.append("</artifactId>\n");

			sb.append(indent);
			sb.append("\t<type>jar</type>\n");

			sb.append(indent);
			sb.append("\t<version>");
			sb.append(version);
			sb.append("</version>\n");

			sb.append(indent);
			sb.append("</dependency>");
		}

		sb.append(content, pos, content.length());

		content = sb.toString();

		Files.write(path, content.getBytes(StandardCharsets.UTF_8));
	}

	private Object _classesDir;
	private final Map<String, String> _configurationScopeMappings =
		new HashMap<>();
	private final Set<String> _forcedExclusions = new HashSet<>();
	private Object _goalPrefix;
	private boolean _mavenDebug;
	private FileCollection _mavenEmbedderClasspath;
	private Object _mavenEmbedderMainClassName =
		"org.apache.maven.cli.MavenCli";
	private Object _mavenPluginPluginVersion = "3.6.4";
	private Object _mavenSettingsFile;
	private Object _outputDir;
	private Object _pomArtifactId;
	private Object _pomGroupId;
	private final Map<String, Object> _pomRepositories = new LinkedHashMap<>();
	private Object _pomVersion;
	private Object _sourceDir;
	private boolean _useSetterComments = true;

}