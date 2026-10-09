/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.util;

import groovy.lang.Closure;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ResolvableDependencies;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.Convention;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.util.GUtil;
import org.gradle.util.internal.NameMatcher;

/**
 * @author Andrea Di Giorgi
 */
public class GradleUtil {

	public static Configuration addConfiguration(Project project, String name) {
		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		return configurationContainer.create(name);
	}

	public static Dependency addDependency(
		Project project, String configurationName, File file) {

		return _addDependency(project, configurationName, project.files(file));
	}

	public static Dependency addDependency(
		Project project, String configurationName,
		FileCollection fileCollection) {

		return _addDependency(project, configurationName, fileCollection);
	}

	public static Dependency addDependency(
		Project project, String configurationName, String dependencyNotation) {

		return _addDependency(project, configurationName, dependencyNotation);
	}

	public static Dependency addDependency(
		Project project, String configurationName, String group, String name,
		String version) {

		return addDependency(
			project, configurationName, group, name, version, true);
	}

	public static Dependency addDependency(
		Project project, String configurationName, String group, String name,
		String version, boolean transitive) {

		return addDependency(
			project, configurationName, group, name, version, null, transitive);
	}

	public static Dependency addDependency(
		Project project, String configurationName, String group, String name,
		String version, String classifier, boolean transitive) {

		Map<String, Object> dependencyNotation = new HashMap<>();

		if (Validator.isNotNull(classifier)) {
			dependencyNotation.put("classifier", classifier);
		}

		dependencyNotation.put("group", group);
		dependencyNotation.put("name", name);
		dependencyNotation.put("transitive", transitive);
		dependencyNotation.put("version", version);

		return _addDependency(project, configurationName, dependencyNotation);
	}

	public static <T> T addExtension(
		ExtensionAware extensionAware, String name, Class<T> clazz) {

		ExtensionContainer extensionContainer = extensionAware.getExtensions();

		return extensionContainer.create(name, clazz, extensionAware);
	}

	public static SourceSet addSourceSet(Project project, String name) {
		JavaPluginConvention javaPluginConvention = getConvention(
			project, JavaPluginConvention.class);

		SourceSetContainer sourceSetContainer =
			javaPluginConvention.getSourceSets();

		return sourceSetContainer.create(name);
	}

	public static <T extends Task> T addTask(
		Project project, String name, Class<T> clazz) {

		Map<String, Class<T>> args = Collections.singletonMap(
			Task.TASK_TYPE, clazz);

		return (T)project.task(args, name);
	}

	public static <T extends Task> TaskProvider<T> addTaskProvider(
		Project project, String name, Class<T> clazz) {

		TaskContainer taskContainer = project.getTasks();

		return taskContainer.register(name, clazz);
	}

	public static <T extends Plugin<? extends Project>> void applyPlugin(
		Project project, Class<T> clazz) {

		Map<String, Class<T>> args = Collections.singletonMap("plugin", clazz);

		project.apply(args);
	}

	public static void applyScript(
		Project project, String name, Object object) {

		ClassLoader classLoader = GradleUtil.class.getClassLoader();

		URL url = classLoader.getResource(name);

		if (url == null) {
			throw new GradleException("Unable to apply script " + name);
		}

		Map<String, Object> args = new HashMap<>();

		args.put("from", url);

		if (object != null) {
			args.put("to", object);
		}

		project.apply(args);
	}

	public static void executeIfEmpty(
		final Configuration configuration, final Action<Configuration> action) {

		ResolvableDependencies resolvableDependencies =
			configuration.getIncoming();

		resolvableDependencies.beforeResolve(
			new Action<ResolvableDependencies>() {

				@Override
				public void execute(
					ResolvableDependencies resolvableDependencies) {

					Set<Dependency> dependencies =
						configuration.getDependencies();
					Set<Configuration> parentConfigurations =
						configuration.getExtendsFrom();

					if (dependencies.isEmpty() &&
						parentConfigurations.isEmpty()) {

						action.execute(configuration);
					}
				}

			});
	}

	public static Task fetchTask(Project project, String name) {
		TaskContainer taskContainer = project.getTasks();

		return taskContainer.findByName(name);
	}

	public static TaskProvider<Task> fetchTaskProvider(
		Project project, String name) {

		try {
			return getTaskProvider(project, name);
		}
		catch (UnknownTaskException unknownTaskException) {
			return null;
		}
	}

	public static <T extends Task> TaskProvider<T> fetchTaskProvider(
		Project project, String name, Class<T> clazz) {

		try {
			return getTaskProvider(project, name, clazz);
		}
		catch (UnknownTaskException unknownTaskException) {
			return null;
		}
	}

	public static Configuration getConfiguration(Project project, String name) {
		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		return configurationContainer.getByName(name);
	}

	public static <T> T getConvention(Project project, Class<T> clazz) {
		Convention convention = project.getConvention();

		return convention.getPlugin(clazz);
	}

	public static <T> T getExtension(
		ExtensionAware extensionAware, Class<T> clazz) {

		ExtensionContainer extensionContainer = extensionAware.getExtensions();

		return extensionContainer.getByType(clazz);
	}

	public static FileTree getFilteredFileTree(
		FileTree fileTree, String[] excludes, String[] includes) {

		PatternFilterable patternFilterable = new PatternSet();

		if (ArrayUtil.isNotEmpty(excludes)) {
			patternFilterable.setExcludes(Arrays.asList(excludes));
		}

		if (ArrayUtil.isNotEmpty(includes)) {
			patternFilterable.setIncludes(Arrays.asList(includes));
		}

		return fileTree.matching(patternFilterable);
	}

	public static Properties getGradleProperties(File gradlePropertiesDir) {
		File gradlePropertiesFile = new File(
			gradlePropertiesDir, "gradle.properties");

		if (!gradlePropertiesFile.exists()) {
			return null;
		}

		Properties properties = GUtil.loadProperties(gradlePropertiesFile);

		File gradleExtPropertiesFile = new File(
			gradlePropertiesFile.getParentFile(), "gradle-ext.properties");

		if (gradleExtPropertiesFile.exists()) {
			properties.putAll(GUtil.loadProperties(gradleExtPropertiesFile));
		}

		return properties;
	}

	public static Properties getGradleProperties(Project project) {
		return getGradleProperties(getRootDir(project, "gradle.properties"));
	}

	public static String getGradlePropertiesValue(
		File gradlePropertiesDir, String key) {

		return getGradlePropertiesValue(gradlePropertiesDir, key, "");
	}

	public static String getGradlePropertiesValue(
		File gradlePropertiesDir, String key, String defaultValue) {

		if (gradlePropertiesDir == null) {
			return defaultValue;
		}

		Properties properties = getGradleProperties(gradlePropertiesDir);

		return properties.getProperty(key, defaultValue);
	}

	public static String getGradlePropertiesValue(Project project, String key) {
		return getGradlePropertiesValue(project, key, "");
	}

	public static String getGradlePropertiesValue(
		Project project, String key, String defaultValue) {

		return getGradlePropertiesValue(
			getRootDir(project, "gradle.properties"), key, defaultValue);
	}

	public static Project getProject(Project rootProject, File projectDir) {
		for (Project project : rootProject.getAllprojects()) {
			if (projectDir.equals(project.getProjectDir())) {
				return project;
			}
		}

		return null;
	}

	public static Object getProperty(
		ExtensionAware extensionAware, String name) {

		ExtensionContainer extensionContainer = extensionAware.getExtensions();

		ExtraPropertiesExtension extraPropertiesExtension =
			extensionContainer.getExtraProperties();

		if (!extraPropertiesExtension.has(name)) {
			return null;
		}

		Object value = extraPropertiesExtension.get(name);

		if ((value instanceof String) && Validator.isNull((String)value)) {
			value = null;
		}

		return value;
	}

	public static boolean getProperty(
		ExtensionAware extensionAware, String name, boolean defaultValue) {

		Object value = getProperty(extensionAware, name);

		if (value instanceof Boolean) {
			return (Boolean)value;
		}

		if (value instanceof String) {
			return Boolean.parseBoolean((String)value);
		}

		return defaultValue;
	}

	public static String getProperty(
		ExtensionAware extensionAware, String name, String defaultValue) {

		Object value = getProperty(extensionAware, name);

		if (value == null) {
			return defaultValue;
		}

		return toString(value);
	}

	public static File getProperty(
		Project project, String name, File defaultValue) {

		Object value = getProperty(project, name);

		if (value == null) {
			return defaultValue;
		}

		return toFile(project, value);
	}

	public static File getRootDir(File dir, String markerFileName) {
		while (true) {
			File markerFile = new File(dir, markerFileName);

			if (markerFile.exists()) {
				return dir;
			}

			dir = dir.getParentFile();

			if (dir == null) {
				return null;
			}
		}
	}

	public static File getRootDir(Project project, String markerFileName) {
		return getRootDir(project.getProjectDir(), markerFileName);
	}

	public static SourceSet getSourceSet(Project project, String name) {
		JavaPluginConvention javaPluginConvention = getConvention(
			project, JavaPluginConvention.class);

		SourceSetContainer sourceSetContainer =
			javaPluginConvention.getSourceSets();

		return sourceSetContainer.getByName(name);
	}

	public static Task getTask(Project project, String name) {
		TaskContainer taskContainer = project.getTasks();

		return taskContainer.getByName(name);
	}

	public static String getTaskName(String prefix, File file) {
		String fileName = FileUtil.stripExtension(file.getName());

		fileName = fileName.replaceAll("\\W", "");

		return prefix + StringUtil.capitalize(fileName);
	}

	public static Set<String> getTaskNames(
		Project project, StartParameter startParameter) {

		Set<String> fullyQualifiedTaskNames = new HashSet<>();

		TaskContainer taskContainer = project.getTasks();

		for (String taskName : startParameter.getTaskNames()) {
			int pos = taskName.lastIndexOf(':');

			if (pos != -1) {
				taskName = taskName.substring(pos + 1);
			}

			String s = _nameMatcher.find(taskName, taskContainer.getNames());

			if (s == null) {
				continue;
			}

			fullyQualifiedTaskNames.add(s);
		}

		return fullyQualifiedTaskNames;
	}

	public static String getTaskPrefixedProperty(
		String projectPath, String taskName, String suffix) {

		String value = System.getProperty(
			projectPath + ':' + taskName + '.' + suffix);

		if (Validator.isNull(value)) {
			value = System.getProperty(taskName + '.' + suffix);
		}

		return value;
	}

	public static String getTaskPrefixedProperty(Task task, String name) {
		String suffix = "." + name;

		String value = System.getProperty(task.getPath() + suffix);

		if (Validator.isNull(value)) {
			value = System.getProperty(task.getName() + suffix);
		}

		return value;
	}

	public static TaskProvider<Task> getTaskProvider(
		Project project, String name) {

		TaskContainer taskContainer = project.getTasks();

		return taskContainer.named(name);
	}

	public static <T extends Task> TaskProvider<T> getTaskProvider(
		Project project, String name, Class<T> clazz) {

		TaskContainer taskContainer = project.getTasks();

		return taskContainer.named(name, clazz);
	}

	public static void removeDependencies(
		Project project, String configurationName,
		String[] dependencyNotations) {

		Configuration configuration = getConfiguration(
			project, configurationName);

		Set<Dependency> dependencies = configuration.getDependencies();

		Iterator<Dependency> iterator = dependencies.iterator();

		while (iterator.hasNext()) {
			Dependency dependency = iterator.next();

			if (!ArrayUtil.contains(
					dependencyNotations, _getDependencyNotation(dependency))) {

				continue;
			}

			iterator.remove();
		}
	}

	public static void setProperty(
		ExtensionAware extensionAware, String name, Object value) {

		ExtensionContainer extensionContainer = extensionAware.getExtensions();

		ExtraPropertiesExtension extraPropertiesExtension =
			extensionContainer.getExtraProperties();

		extraPropertiesExtension.set(name, value);
	}

	public static File toFile(File parentFile, Object object) {
		object = toObject(object);

		if (object == null) {
			return null;
		}

		File file = new File(object.toString());

		if (file.isAbsolute()) {
			return file;
		}

		return new File(parentFile, object.toString());
	}

	public static File toFile(Project project, Object object) {
		if (object == null) {
			return null;
		}

		return toFile(project.getProjectDir(), object);
	}

	public static Integer toInteger(Object object) {
		object = toObject(object);

		if (object instanceof Integer) {
			return (Integer)object;
		}

		if (object instanceof Number) {
			Number number = (Number)object;

			return number.intValue();
		}

		if (object instanceof String) {
			return Integer.parseInt((String)object);
		}

		return null;
	}

	public static Object toObject(Object object) {
		if (object instanceof Callable<?>) {
			Callable<?> callable = (Callable<?>)object;

			try {
				object = callable.call();
			}
			catch (Exception exception) {
				throw new GradleException(exception.getMessage(), exception);
			}
		}
		else if (object instanceof Closure<?>) {
			Closure<?> closure = (Closure<?>)object;

			object = closure.call();
		}

		return object;
	}

	public static String toString(Object object) {
		object = toObject(object);

		if (object == null) {
			return null;
		}

		return object.toString();
	}

	public static List<String> toStringList(Iterable<?> iterable) {
		List<String> list = new ArrayList<>();

		for (Object object : iterable) {
			list.add(toString(object));
		}

		return list;
	}

	public static boolean waitFor(
			Callable<Boolean> callable, long checkInterval, long timeout)
		throws Exception {

		long end = System.currentTimeMillis() + timeout;

		while (System.currentTimeMillis() < end) {
			if (callable.call()) {
				return true;
			}

			Thread.sleep(checkInterval);
		}

		return false;
	}

	private static Dependency _addDependency(
		Project project, String configurationName, Object dependencyNotation) {

		DependencyHandler dependencyHandler = project.getDependencies();

		return dependencyHandler.add(configurationName, dependencyNotation);
	}

	private static String _getDependencyNotation(Dependency dependency) {
		StringBuilder sb = new StringBuilder();

		if (Validator.isNotNull(dependency.getGroup())) {
			sb.append(dependency.getGroup());
			sb.append(":");
		}

		sb.append(dependency.getName());

		if (Validator.isNotNull(dependency.getVersion())) {
			sb.append(":");
			sb.append(dependency.getVersion());
		}

		return sb.toString();
	}

	private static final NameMatcher _nameMatcher = new NameMatcher();

}