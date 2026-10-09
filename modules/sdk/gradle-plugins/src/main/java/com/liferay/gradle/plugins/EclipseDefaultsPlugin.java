/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins;

import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import groovy.util.Node;

import java.io.File;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.XmlProvider;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.plugins.ide.api.FileContentMerger;
import org.gradle.plugins.ide.api.XmlFileContentMerger;
import org.gradle.plugins.ide.eclipse.EclipsePlugin;
import org.gradle.plugins.ide.eclipse.model.AbstractClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry;
import org.gradle.plugins.ide.eclipse.model.EclipseClasspath;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.gradle.plugins.ide.eclipse.model.EclipseProject;

/**
 * @author Andrea Di Giorgi
 */
public class EclipseDefaultsPlugin extends BaseDefaultsPlugin<EclipsePlugin> {

	public static final Plugin<Project> INSTANCE = new EclipseDefaultsPlugin();

	@Override
	protected void applyPluginDefaults(
		Project project, EclipsePlugin eclipsePlugin) {

		// Extensions

		ExtensionContainer extensionContainer = project.getExtensions();

		final EclipseModel eclipseModelExtension = extensionContainer.getByType(
			EclipseModel.class);

		// Tasks

		TaskProvider<Task> eclipseTaskProvider = GradleUtil.getTaskProvider(
			project, _ECLIPSE_TASK_NAME);

		_configureTaskEclipseProvider(eclipseTaskProvider);

		// Containers

		PluginContainer pluginContainer = project.getPlugins();

		pluginContainer.configureEach(
			new Action<Plugin>() {

				@Override
				public void execute(Plugin plugin) {
					if (plugin instanceof JavaPlugin) {
						_configurePluginJava(project, eclipseModelExtension);
					}
				}

			});

		// Other

		File portalRootDir = GradleUtil.getRootDir(
			project.getRootProject(), "portal-impl");

		_configureEclipseClasspath(project, eclipseModelExtension);
		_configureEclipseProject(
			project, eclipseModelExtension, eclipseTaskProvider, portalRootDir);
	}

	@Override
	protected Class<EclipsePlugin> getPluginClass() {
		return EclipsePlugin.class;
	}

	private EclipseDefaultsPlugin() {
	}

	private void _configureEclipseClasspath(
		Project project, EclipseModel eclipseModelExtension) {

		EclipseClasspath eclipseClasspath =
			eclipseModelExtension.getClasspath();

		FileContentMerger fileContentMerger = eclipseClasspath.getFile();

		@SuppressWarnings("serial")
		Closure<Void> closure = new Closure<Void>(project) {

			@SuppressWarnings("unused")
			public void doCall(Classpath classpath) {
				List<ClasspathEntry> classpathEntries = classpath.getEntries();

				Iterator<ClasspathEntry> iterator = classpathEntries.iterator();

				while (iterator.hasNext()) {
					ClasspathEntry classpathEntry = iterator.next();

					if (!(classpathEntry instanceof AbstractClasspathEntry)) {
						continue;
					}

					AbstractClasspathEntry abstractClasspathEntry =
						(AbstractClasspathEntry)classpathEntry;

					String kind = abstractClasspathEntry.getKind();
					String path = abstractClasspathEntry.getPath();

					if (!kind.equals("lib") || !path.endsWith(".pom")) {
						continue;
					}

					iterator.remove();
				}
			}

		};

		fileContentMerger.whenMerged(closure);
	}

	private void _configureEclipseProject(
		Project project, EclipseModel eclipseModelExtension,
		TaskProvider<Task> eclipseTaskProvider, File portalRootDir) {

		EclipseProject eclipseProject = eclipseModelExtension.getProject();

		String eclipseProjectName = project.getName();

		String gitWorkingBranch = GradleUtil.getTaskPrefixedProperty(
			project.getPath(), eclipseTaskProvider.getName(),
			"git.working.branch");

		if (Boolean.parseBoolean(gitWorkingBranch) && (portalRootDir != null) &&
			portalRootDir.exists()) {

			String gitWorkingBranchName = GradleUtil.getProperty(
				project, "git.working.branch.name", (String)null);

			if (Validator.isNotNull(gitWorkingBranchName)) {
				eclipseProjectName =
					eclipseProjectName + '-' + gitWorkingBranchName;
			}
		}

		eclipseProject.setName(eclipseProjectName);

		List<String> natures = eclipseProject.getNatures();

		natures.add("com.liferay.ide.core.liferayNature");

		Action<XmlProvider> action = new Action<XmlProvider>() {

			@Override
			public void execute(XmlProvider xmlProvider) {
				Node projectDescriptionNode = xmlProvider.asNode();

				Node filteredResourcesNode = projectDescriptionNode.appendNode(
					"filteredResources");

				Node filterNode = filteredResourcesNode.appendNode("filter");

				filterNode.appendNode("id", System.currentTimeMillis());
				filterNode.appendNode("name");
				filterNode.appendNode("type", "26");

				Node matcherNode = filterNode.appendNode("matcher");

				matcherNode.appendNode(
					"id", "org.eclipse.ui.ide.orFilterMatcher");

				Node argumentsNode = matcherNode.appendNode("arguments");

				for (String filteredDirName : _FILTERED_DIR_NAMES) {
					Node curMatcherNode = argumentsNode.appendNode("matcher");

					curMatcherNode.appendNode(
						"arguments",
						"1.0-name-matches-false-false-" + filteredDirName);
					curMatcherNode.appendNode(
						"id", "org.eclipse.ui.ide.multiFilter");
				}
			}

		};

		XmlFileContentMerger xmlFileContentMerger = eclipseProject.getFile();

		xmlFileContentMerger.withXml(action);
	}

	private void _configurePluginJava(
		Project project, EclipseModel eclipseModelExtension) {

		// Configurations

		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration compileOnlyConfiguration =
			configurationContainer.getByName(
				JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME);

		compileOnlyConfiguration.setCanBeResolved(true);

		// Other

		EclipseClasspath eclipseClasspath =
			eclipseModelExtension.getClasspath();

		Collection<Configuration> configurations =
			eclipseClasspath.getPlusConfigurations();

		configurations.add(compileOnlyConfiguration);
	}

	private void _configureTaskEclipseProvider(
		TaskProvider<Task> eclipseTaskProvider) {

		eclipseTaskProvider.configure(
			new Action<Task>() {

				@Override
				public void execute(Task eclipseTask) {
					eclipseTask.dependsOn(_CLEAN_ECLIPSE_TASK_NAME);
				}

			});
	}

	private static final String _CLEAN_ECLIPSE_TASK_NAME = "cleanEclipse";

	private static final String _ECLIPSE_TASK_NAME = "eclipse";

	private static final String[] _FILTERED_DIR_NAMES = {
		".git", ".gradle", "build", "node_modules", "node_modules_cache", "tmp"
	};

}