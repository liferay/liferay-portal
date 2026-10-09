/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.defaults.internal.util;

import com.liferay.gradle.util.Validator;

import java.io.File;

import java.lang.reflect.Method;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.artifacts.DependencySubstitutions;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.artifacts.ResolutionStrategy;
import org.gradle.api.artifacts.component.ComponentSelector;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.BasePluginConvention;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskOutputs;

/**
 * @author Andrea Di Giorgi
 */
public class GradleUtil extends com.liferay.gradle.util.GradleUtil {

	public static MavenArtifactRepository addMavenArtifactRepository(
		RepositoryHandler repositoryHandler, final Object url) {

		return repositoryHandler.maven(
			new Action<MavenArtifactRepository>() {

				@Override
				public void execute(
					MavenArtifactRepository mavenArtifactRepository) {

					mavenArtifactRepository.setUrl(url);
				}

			});
	}

	public static void excludeTasksWithProperty(
		Project project, String propertyName, boolean defaultValue,
		String... taskNames) {

		if (!project.hasProperty(propertyName) ||
			!getProperty(project, propertyName, defaultValue)) {

			return;
		}

		for (String taskName : taskNames) {
			Task task = getTask(project, taskName);

			task.setDependsOn(Collections.emptySet());
			task.setEnabled(false);
			task.setFinalizedBy(Collections.emptySet());
		}
	}

	public static Configuration fetchConfiguration(
		Project project, String name) {

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		return configurationContainer.findByName(name);
	}

	public static String getArchivesBaseName(Project project) {
		BasePluginConvention basePluginConvention = getConvention(
			project, BasePluginConvention.class);

		return basePluginConvention.getArchivesBaseName();
	}

	public static File getMavenLocalDir(Project project) {
		RepositoryHandler repositoryHandler = project.getRepositories();

		ArtifactRepository artifactRepository = repositoryHandler.findByName(
			ArtifactRepositoryContainer.DEFAULT_MAVEN_LOCAL_REPO_NAME);

		if (!(artifactRepository instanceof MavenArtifactRepository)) {
			return null;
		}

		MavenArtifactRepository mavenArtifactRepository =
			(MavenArtifactRepository)artifactRepository;

		return new File(mavenArtifactRepository.getUrl());
	}

	public static File getMavenLocalFile(
		Project project, String group, String name, String version) {

		File dir = getMavenLocalDir(project);

		if (dir == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append(group.replace('.', File.separatorChar));
		sb.append(File.separatorChar);
		sb.append(name);
		sb.append(File.separatorChar);
		sb.append(version);
		sb.append(File.separatorChar);
		sb.append(name);
		sb.append('-');
		sb.append(version);
		sb.append(".jar");

		return new File(dir, sb.toString());
	}

	public static File getOutputFile(Task task) {
		TaskOutputs taskOutputs = task.getOutputs();

		FileCollection fileCollection = taskOutputs.getFiles();

		return fileCollection.getSingleFile();
	}

	public static Project getProject(Project rootProject, String name) {
		for (Project project : rootProject.getAllprojects()) {
			if (!name.equals(project.getName())) {
				continue;
			}

			Set<Project> subprojects = project.getSubprojects();

			if (subprojects.isEmpty()) {
				return project;
			}
		}

		return null;
	}

	public static String getProjectGroup(Project project, String defaultValue) {
		String projectPath = project.getPath();

		if (projectPath.startsWith(":apps:commerce:") ||
			projectPath.startsWith(":dxp:apps:commerce:") ||
			projectPath.startsWith(":private:apps:commerce:")) {

			return "com.liferay.commerce";
		}

		return getGradlePropertiesValue(project, "project.group", defaultValue);
	}

	public static Object getProperty(Object object, String name) {
		try {
			Class<?> clazz = object.getClass();

			Method hasPropertyMethod = clazz.getMethod(
				"hasProperty", String.class);

			boolean hasProperty = (boolean)hasPropertyMethod.invoke(
				object, name);

			if (!hasProperty) {
				return null;
			}

			Method getPropertyMethod = clazz.getMethod(
				"getProperty", String.class);

			Object value = getPropertyMethod.invoke(object, name);

			if ((value instanceof String) && Validator.isNull((String)value)) {
				value = null;
			}

			return value;
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new GradleException(
				"Unable to get property", reflectiveOperationException);
		}
	}

	public static boolean getProperty(
		Object object, String name, boolean defaultValue) {

		Object value = getProperty(object, name);

		if (value == null) {
			return defaultValue;
		}

		if (value instanceof Boolean) {
			return (Boolean)value;
		}

		if (value instanceof String) {
			return Boolean.parseBoolean((String)value);
		}

		return defaultValue;
	}

	public static String getProperty(
		Object object, String name, String defaultValue) {

		Object value = getProperty(object, name);

		if (value == null) {
			return defaultValue;
		}

		return toString(value);
	}

	public static File getSrcDir(SourceDirectorySet sourceDirectorySet) {
		Set<File> srcDirs = sourceDirectorySet.getSrcDirs();

		Iterator<File> iterator = srcDirs.iterator();

		return iterator.next();
	}

	public static boolean hasDependency(
		DependencySet dependencySet, String group, String name) {

		for (ModuleDependency moduleDependency :
				dependencySet.withType(ModuleDependency.class)) {

			if (group.equals(moduleDependency.getGroup()) &&
				name.equals(moduleDependency.getName())) {

				return true;
			}
		}

		return false;
	}

	public static boolean hasPlugin(
		Project project, Class<? extends Plugin<?>> pluginClass) {

		PluginContainer pluginContainer = project.getPlugins();

		return pluginContainer.hasPlugin(pluginClass);
	}

	public static boolean hasStartParameterTask(
		Project project, String taskName) {

		Gradle gradle = project.getGradle();

		StartParameter startParameter = gradle.getStartParameter();

		List<String> taskNames = startParameter.getTaskNames();

		if (taskNames.contains(taskName) ||
			taskNames.contains(project.getPath() + ":" + taskName)) {

			return true;
		}

		return false;
	}

	public static boolean isFromMavenLocal(Project project, File file) {
		File mavenLocalDir = getMavenLocalDir(project);

		if ((mavenLocalDir != null) && FileUtil.isChild(file, mavenLocalDir)) {
			return true;
		}

		return false;
	}

	public static void substituteModuleDependencyWithProject(
		Configuration configuration,
		ModuleVersionSelector moduleVersionSelector, Project project) {

		ResolutionStrategy resolutionStrategy =
			configuration.getResolutionStrategy();

		DependencySubstitutions dependencySubstitutions =
			resolutionStrategy.getDependencySubstitution();

		ComponentSelector moduleComponentSelector =
			dependencySubstitutions.module(
				_getDependencyNotation(moduleVersionSelector));

		DependencySubstitutions.Substitution substitution =
			dependencySubstitutions.substitute(moduleComponentSelector);

		ComponentSelector projectComponentSelector =
			dependencySubstitutions.project(project.getPath());

		substitution.using(projectComponentSelector);
	}

	public static <P extends Plugin<? extends Project>> void withPlugin(
		Project project, Class<P> pluginClass, Action<P> action) {

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.withType(pluginClass, action);
	}

	private static String _getDependencyNotation(
		ModuleVersionSelector moduleVersionSelector) {

		StringBuilder sb = new StringBuilder();

		sb.append(moduleVersionSelector.getGroup());
		sb.append(':');
		sb.append(moduleVersionSelector.getName());
		sb.append(':');
		sb.append(moduleVersionSelector.getVersion());

		return sb.toString();
	}

}