/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.project.templates;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

import com.liferay.project.templates.extensions.ProjectTemplatesArgs;
import com.liferay.project.templates.extensions.ProjectTemplatesArgsExt;
import com.liferay.project.templates.extensions.constants.ProjectTemplatesConstants;
import com.liferay.project.templates.extensions.util.FileUtil;
import com.liferay.project.templates.extensions.util.ProjectTemplatesUtil;
import com.liferay.project.templates.extensions.util.StringUtil;
import com.liferay.project.templates.extensions.util.Validator;
import com.liferay.project.templates.extensions.util.WorkspaceUtil;
import com.liferay.project.templates.internal.ProjectGenerator;

import java.beans.Statement;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.maven.archetype.ArchetypeGenerationResult;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 */
public class ProjectTemplates {

	public static Map<String, String> getTemplates() throws Exception {
		return getTemplates(Collections.emptySet());
	}

	public static Map<String, String> getTemplates(
			Collection<File> templatesFiles)
		throws Exception {

		Map<String, String> templates = new TreeMap<>();

		for (File templatesFile : templatesFiles) {
			if (templatesFile.isDirectory()) {
				try (DirectoryStream<Path> directoryStream =
						Files.newDirectoryStream(
							templatesFile.toPath(), "*.jar")) {

					Iterator<Path> iterator = directoryStream.iterator();

					while (iterator.hasNext()) {
						Path path = iterator.next();

						try {
							String bundleSymbolicName =
								FileUtil.getManifestProperty(
									path.toFile(), "Bundle-SymbolicName");

							String templateName =
								ProjectTemplatesUtil.getTemplateName(
									bundleSymbolicName);

							if (templateName.startsWith(
									WorkspaceUtil.WORKSPACE)) {

								continue;
							}

							String bundleDescription =
								FileUtil.getManifestProperty(
									path.toFile(), "Bundle-Description");

							if (bundleDescription == null) {
								continue;
							}

							try (ZipFile zipFile = new ZipFile(path.toFile())) {
								ZipEntry zipEntry = zipFile.getEntry(
									_ARCHETYPE_METADATA_XML);

								if (Objects.nonNull(zipEntry)) {
									templates.put(
										templateName, bundleDescription);
								}
							}
						}
						catch (IOException ioException) {
						}
					}
				}

				continue;
			}

			try (JarFile jarFile = new JarFile(templatesFile)) {
				Enumeration<JarEntry> enumeration = jarFile.entries();

				while (enumeration.hasMoreElements()) {
					JarEntry jarEntry = enumeration.nextElement();

					if (jarEntry.isDirectory()) {
						continue;
					}

					String template = jarEntry.getName();

					if (!template.startsWith(
							ProjectTemplatesConstants.TEMPLATE_BUNDLE_PREFIX)) {

						continue;
					}

					template = ProjectTemplatesUtil.getTemplateName(template);

					if (!template.startsWith(WorkspaceUtil.WORKSPACE)) {
						continue;
					}

					try (InputStream inputStream = jarFile.getInputStream(
							jarEntry);

						JarInputStream jarInputStream = new JarInputStream(
							inputStream)) {

						Manifest manifest = jarInputStream.getManifest();

						Attributes attributes = manifest.getMainAttributes();

						String bundleDescription = attributes.getValue(
							"Bundle-Description");

						templates.put(template, bundleDescription);
					}
				}
			}
		}

		List<String> archetypeJarNames =
			ProjectTemplatesUtil.getArchetypeJarNames();

		for (String projectTemplateJarName : archetypeJarNames) {
			String templateName = ProjectTemplatesUtil.getTemplateName(
				projectTemplateJarName);

			if (templateName.startsWith(WorkspaceUtil.WORKSPACE)) {
				continue;
			}

			try (InputStream inputStream =
					ProjectTemplates.class.getResourceAsStream(
						projectTemplateJarName);

				JarInputStream jarInputStream = new JarInputStream(
					inputStream)) {

				Manifest manifest = jarInputStream.getManifest();

				Attributes attributes = manifest.getMainAttributes();

				String bundleDescription = attributes.getValue(
					"Bundle-Description");

				templates.put(templateName, bundleDescription);
			}
		}

		return templates;
	}

	public static Map<String, String> getTemplates(File templateDirectory)
		throws Exception {

		return getTemplates(Arrays.asList(templateDirectory));
	}

	public static void main(String[] args) throws Exception {
		ProjectTemplatesArgs projectTemplatesArgs = new ProjectTemplatesArgs();

		JCommander.Builder builder = JCommander.newBuilder();

		builder.addObject(projectTemplatesArgs);

		JCommander jCommander = builder.build();

		jCommander.setAcceptUnknownOptions(true);

		jCommander.parseWithoutValidation(args);

		String template = projectTemplatesArgs.getTemplate();

		if (template.equals("portlet")) {
			template = "mvc-portlet";

			projectTemplatesArgs.setTemplate(template);
		}

		File templateFile = ProjectTemplatesUtil.getTemplateFile(
			projectTemplatesArgs);

		Thread thread = Thread.currentThread();

		ClassLoader oldContextClassLoader = thread.getContextClassLoader();

		URI uri = templateFile.toURI();

		thread.setContextClassLoader(
			new URLClassLoader(new URL[] {uri.toURL()}));

		ProjectTemplatesArgsExt projectTemplatesArgsExt =
			_getProjectTemplateArgsExt(
				projectTemplatesArgs.getTemplate(), templateFile);

		builder = JCommander.newBuilder();

		projectTemplatesArgs = new ProjectTemplatesArgs();

		builder = builder.addObject(projectTemplatesArgs);

		if (projectTemplatesArgsExt != null) {
			builder = builder.addObject(projectTemplatesArgsExt);
		}

		jCommander = builder.build();

		if (projectTemplatesArgsExt != null) {
			projectTemplatesArgs.setProjectTemplatesArgsExt(
				projectTemplatesArgsExt);
		}

		try {
			Path jarPath = FileUtil.getJarPath();

			if (Files.isDirectory(jarPath)) {
				jCommander.setProgramName(ProjectTemplates.class.getName());
			}
			else {
				jCommander.setProgramName("java -jar " + jarPath.getFileName());
			}

			jCommander.parse(args);

			template = projectTemplatesArgs.getTemplate();

			if (template.equals("portlet")) {
				template = "mvc-portlet";

				projectTemplatesArgs.setTemplate(template);
			}

			if (projectTemplatesArgs.isHelp()) {
				_printHelp(jCommander, projectTemplatesArgs);
			}
			else if (projectTemplatesArgs.isList()) {
				_printList(projectTemplatesArgs);
			}
			else {
				new ProjectTemplates(projectTemplatesArgs);
			}
		}
		catch (ParameterException parameterException) {
			System.err.println(parameterException.getMessage());

			_printHelp(jCommander, projectTemplatesArgs);
		}
		finally {
			thread.setContextClassLoader(oldContextClassLoader);
		}
	}

	public ProjectTemplates(ProjectTemplatesArgs projectTemplatesArgs)
		throws Exception {

		this(projectTemplatesArgs, null);
	}

	public ProjectTemplates(
			ProjectTemplatesArgs projectTemplatesArgs,
			Map<String, String> propertiesMap)
		throws Exception {

		ProjectTemplatesArgsExt projectTemplatesArgsExt =
			projectTemplatesArgs.getProjectTemplatesArgsExt();

		Thread thread = Thread.currentThread();

		ClassLoader oldContextClassLoader = thread.getContextClassLoader();

		boolean changedClassLoader = false;

		try {
			if (projectTemplatesArgsExt == null) {
				File templateFile = ProjectTemplatesUtil.getTemplateFile(
					projectTemplatesArgs);

				URI uri = templateFile.toURI();

				changedClassLoader = true;
				thread.setContextClassLoader(
					new URLClassLoader(new URL[] {uri.toURL()}));

				projectTemplatesArgsExt = _getProjectTemplateArgsExt(
					projectTemplatesArgs.getTemplate(), templateFile);

				projectTemplatesArgs.setProjectTemplatesArgsExt(
					projectTemplatesArgsExt);
			}

			if (propertiesMap != null) {
				for (Map.Entry<String, String> entry :
						propertiesMap.entrySet()) {

					_setIfPresent(
						projectTemplatesArgsExt, entry.getKey(),
						entry.getValue());
				}
			}

			_checkArgs(projectTemplatesArgs);

			File destinationDir = projectTemplatesArgs.getDestinationDir();

			ProjectGenerator projectGenerator = new ProjectGenerator();

			ArchetypeGenerationResult archetypeGenerationResult =
				projectGenerator.generateProject(
					projectTemplatesArgs, destinationDir);

			if (archetypeGenerationResult != null) {
				Exception exception = archetypeGenerationResult.getCause();

				if (exception != null) {
					throw exception;
				}
			}

			Path templateDirPath = destinationDir.toPath();

			templateDirPath = templateDirPath.resolve(
				projectTemplatesArgs.getName());

			if (WorkspaceUtil.isWorkspace(destinationDir)) {
				Files.deleteIfExists(
					templateDirPath.resolve("settings.gradle"));
			}
			else {
				if (projectTemplatesArgs.isGradle()) {
					FileUtil.extractDirectory(
						"gradle-wrapper", templateDirPath);

					FileUtil.setPosixFilePermissions(
						templateDirPath.resolve("gradlew"),
						_wrapperPosixFilePermissions);
				}

				if (projectTemplatesArgs.isMaven()) {
					FileUtil.extractDirectory("maven-wrapper", templateDirPath);

					FileUtil.setPosixFilePermissions(
						templateDirPath.resolve("mvnw"),
						_wrapperPosixFilePermissions);
				}
			}

			if (!projectTemplatesArgs.isGradle()) {
				FileUtil.deleteFiles(
					templateDirPath, "build.gradle", "settings.gradle");
			}

			if (!projectTemplatesArgs.isMaven()) {
				FileUtil.deleteFiles(templateDirPath, "pom.xml");
			}
		}
		finally {
			if (changedClassLoader) {
				thread.setContextClassLoader(oldContextClassLoader);
			}
		}
	}

	private static ProjectTemplatesArgsExt _getProjectTemplateArgsExt(
			String templateName, File archetypeFile)
		throws MalformedURLException {

		if (archetypeFile == null) {
			return null;
		}

		ServiceLoader<ProjectTemplatesArgsExt> serviceLoader =
			ServiceLoader.load(ProjectTemplatesArgsExt.class);

		Iterator<ProjectTemplatesArgsExt> iterator = serviceLoader.iterator();

		while (iterator.hasNext()) {
			ProjectTemplatesArgsExt projectTemplatesArgsExt = iterator.next();

			if (templateName.equals(
					projectTemplatesArgsExt.getTemplateName())) {

				return projectTemplatesArgsExt;
			}
		}

		return null;
	}

	private static void _printHelp(
			JCommander jCommander, ProjectTemplatesArgs projectTemplatesArgs)
		throws Exception {

		System.out.println();

		System.out.println(
			"Create a new Liferay module project from several available " +
				"templates:");

		Map<String, String> templates = getTemplates(
			projectTemplatesArgs.getArchetypesDirs());

		int lineLength = 0;

		Set<String> templateNames = templates.keySet();

		Iterator<String> iterator = templateNames.iterator();

		while (iterator.hasNext()) {
			String template = iterator.next();

			if ((lineLength + template.length() + 1) >
					jCommander.getColumnSize()) {

				System.out.println();

				lineLength = 0;
			}

			System.out.print(template);

			lineLength += template.length();

			if (!iterator.hasNext()) {
				continue;
			}

			System.out.print(", ");

			lineLength += 2;
		}

		System.out.println();
		System.out.println();

		jCommander.usage();
	}

	private static void _printList(ProjectTemplatesArgs projectTemplatesArgs)
		throws Exception {

		Map<String, String> templates = getTemplates(
			projectTemplatesArgs.getArchetypesDirs());

		for (Map.Entry<String, String> entry : templates.entrySet()) {
			System.out.println(entry.getKey() + " - " + entry.getValue());
		}
	}

	private void _checkArgs(ProjectTemplatesArgs projectTemplatesArgs) {
		if (Validator.isNull(projectTemplatesArgs.getAuthor())) {
			throw new IllegalArgumentException("Author is required");
		}

		if (Validator.isNull(projectTemplatesArgs.getTemplate())) {
			throw new IllegalArgumentException("Template is required");
		}

		String template = projectTemplatesArgs.getTemplate();

		String name = projectTemplatesArgs.getName();

		if (Validator.isNull(name) &&
			!template.equals(WorkspaceUtil.WORKSPACE)) {

			throw new IllegalArgumentException("Name is required");
		}

		File destinationDir = projectTemplatesArgs.getDestinationDir();

		if (destinationDir == null) {
			throw new IllegalArgumentException("Destination dir is required");
		}

		File dir = destinationDir;

		if (Validator.isNotNull(name)) {
			dir = new File(dir, name);
		}

		if (!projectTemplatesArgs.isForce() && dir.exists()) {
			String[] fileNames = dir.list();

			if ((fileNames == null) || (fileNames.length > 0)) {
				throw new IllegalArgumentException(
					dir + " is not empty or it is a file");
			}
		}

		String className = projectTemplatesArgs.getClassName();

		if (Validator.isNull(className) && Validator.isNotNull(name)) {
			className = _getClassName(name);
		}

		if (template.equals("activator") && !className.endsWith("Activator")) {
			className += "Activator";
		}
		else if ((template.equals("freemarker-portlet") ||
				  template.equals("mvc-portlet") ||
				  template.equals("npm-react-portlet") ||
				  template.equals("npm-vuejs-portlet") ||
				  template.equals("spring-mvc-portlet") ||
				  template.equals("portlet")) &&
				 (className.length() > 7) && className.endsWith("Portlet")) {

			className = className.substring(0, className.length() - 7);
		}

		projectTemplatesArgs.setClassName(className);

		if (Validator.isNull(projectTemplatesArgs.getPackageName()) &&
			Validator.isNotNull(name)) {

			projectTemplatesArgs.setPackageName(_getPackageName(name));
		}
	}

	private String _getCapitalizedName(String name) {
		name = name.replace('-', ' ');
		name = name.replace('.', ' ');

		return StringUtil.capitalize(name, ' ');
	}

	private String _getClassName(String name) {
		name = _getCapitalizedName(name);

		return StringUtil.removeChar(name, ' ');
	}

	private String _getPackageName(String name) {
		name = name.replace('-', '.');
		name = name.replace(' ', '.');

		return name.toLowerCase();
	}

	private boolean _hasMethod(Object object, String methodName) {
		if (object != null) {
			Class<?> clazz = object.getClass();
			Method method = null;

			try {
				method = clazz.getMethod(
					methodName, new Class<?>[] {String.class});
			}
			catch (Exception exception) {
				return false;
			}

			if (method != null) {
				return true;
			}

			return false;
		}

		return false;
	}

	private void _setIfPresent(Object object, String methodName, String value) {
		if (_hasMethod(object, methodName)) {
			Statement statement = new Statement(
				object, methodName, new Object[] {value});

			try {
				statement.execute();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
	}

	private static final String _ARCHETYPE_METADATA_XML =
		"META-INF/maven/archetype-metadata.xml";

	private static final Set<PosixFilePermission> _wrapperPosixFilePermissions =
		PosixFilePermissions.fromString("rwxrwxr--");

}