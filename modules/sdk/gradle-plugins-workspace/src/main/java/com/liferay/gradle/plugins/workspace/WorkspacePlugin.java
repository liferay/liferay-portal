/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace;

import com.liferay.gradle.plugins.util.PortalTools;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;
import com.liferay.release.util.ReleaseUtil;

import groovy.lang.Closure;

import java.io.File;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import net.saliman.gradle.plugin.properties.PropertiesPlugin;

import org.gradle.StartParameter;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.api.plugins.PluginAware;

/**
 * @author David Truong
 * @author Andrea Di Giorgi
 */
public class WorkspacePlugin implements Plugin<Settings> {

	public static final String EXTENSION_NAME = "liferayWorkspace";

	public static final String PROPERTY_PREFIX = "liferay.workspace.";

	@Override
	@SuppressWarnings("serial")
	public void apply(Settings settings) {
		String refreshLiferayReleases = System.getProperty(
			"liferay.workspace.refresh.liferay.releases");

		if (refreshLiferayReleases != null) {
			ReleaseUtil.initialize(0);
		}

		Gradle gradle = settings.getGradle();
		File rootDir = settings.getRootDir();

		_applyPlugins(settings, settings);

		final WorkspaceExtension workspaceExtension = _addWorkspaceExtension(
			settings);

		Path rootDirPath = rootDir.toPath();

		FileSystem fileSystem = rootDirPath.getFileSystem();

		StartParameter startParameter = gradle.getStartParameter();

		File currentDir = startParameter.getCurrentDir();

		gradle.beforeProject(
			new Closure<Void>(settings) {

				@SuppressWarnings("unused")
				public void doCall(Project project) {
					_setPortalVersion(project, workspaceExtension);

					Plugin<Project> plugin = null;

					if (project.getParent() == null) {
						_applyPlugins(project, settings);

						for (ProjectConfigurator projectConfigurator :
								workspaceExtension.getProjectConfigurators()) {

							projectConfigurator.configureRootProject(
								project, workspaceExtension);
						}

						plugin =
							workspaceExtension.getRootProjectConfigurator();
					}
					else {
						plugin = _projectConfiguratorsMap.get(
							project.getPath());
					}

					if (plugin != null) {
						plugin.apply(project);
					}
				}

			});

		gradle.settingsEvaluated(
			new Action<Settings>() {

				@Override
				public void execute(Settings settings) {
					for (ProjectConfigurator projectConfigurator :
							workspaceExtension.getProjectConfigurators()) {

						for (File defaultRootDir :
								projectConfigurator.getDefaultRootDirs()) {

							_includeProjects(
								projectConfigurator, defaultRootDir);
						}
					}
				}

				private void _includeProjects(
					ProjectConfigurator projectConfigurator,
					File defaultRootDir) {

					Iterable<File> projectDirs =
						projectConfigurator.getProjectDirs(defaultRootDir);

					Iterator<File> iterator = projectDirs.iterator();

					while (iterator.hasNext()) {
						File projectDir = iterator.next();

						if (Objects.equals(currentDir, projectDir)) {
							continue;
						}

						for (String glob :
								workspaceExtension.getDirExcludesGlobs()) {

							Path relativeProjectPath = rootDirPath.relativize(
								projectDir.toPath());

							PathMatcher pathMatcher = fileSystem.getPathMatcher(
								"glob:" + glob);

							if (!pathMatcher.matches(relativeProjectPath)) {
								continue;
							}

							if (_logger.isInfoEnabled()) {
								_logger.info(
									"Skipping project evaluation for {} " +
										"because it matches the exclude " +
											"pattern {}.",
									relativeProjectPath, glob);
							}

							iterator.remove();
						}
					}

					for (File projectDir : projectDirs) {
						String projectPath = GradleUtil.getProjectPath(
							projectDir, rootDir);

						settings.include(new String[] {projectPath});

						_projectConfiguratorsMap.put(
							projectPath, projectConfigurator);
					}
				}

			});
	}

	private WorkspaceExtension _addWorkspaceExtension(Settings settings) {
		ExtensionAware extensionAware = (ExtensionAware)settings.getGradle();

		ExtensionContainer extensionContainer = extensionAware.getExtensions();

		return extensionContainer.create(
			EXTENSION_NAME, WorkspaceExtension.class, settings);
	}

	private void _applyPlugins(PluginAware pluginAware, Settings settings) {
		if (GradleUtil.getProperty(
				settings,
				WorkspacePlugin.PROPERTY_PREFIX +
					"feature.net.saliman.properties.plugin.enabled",
				true)) {

			pluginAware.apply(
				Collections.singletonMap("plugin", PropertiesPlugin.class));
		}
	}

	private void _setPortalVersion(
		Project project, WorkspaceExtension workspaceExtension) {

		String portalVersion = GradleUtil.getProperty(
			project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, (String)null);

		if (Validator.isNotNull(portalVersion)) {
			return;
		}

		String bundleUrl = workspaceExtension.getBundleUrl();

		if (Objects.isNull(bundleUrl)) {
			return;
		}

		if (bundleUrl.contains("7.0.")) {
			GradleUtil.setProperty(
				project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, "7.0.x");
		}
		else if (bundleUrl.contains("7.1.")) {
			GradleUtil.setProperty(
				project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, "7.1.x");
		}
		else if (bundleUrl.contains("7.2.")) {
			GradleUtil.setProperty(
				project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, "7.2.x");
		}
		else if (bundleUrl.contains("7.3.")) {
			GradleUtil.setProperty(
				project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, "7.3.x");
		}
		else {
			GradleUtil.setProperty(
				project, PortalTools.PORTAL_VERSION_PROPERTY_NAME, "7.4.x");
		}
	}

	private static final Logger _logger = Logging.getLogger(
		WorkspacePlugin.class);

	private static final Map<String, ProjectConfigurator>
		_projectConfiguratorsMap = new HashMap<>();

}