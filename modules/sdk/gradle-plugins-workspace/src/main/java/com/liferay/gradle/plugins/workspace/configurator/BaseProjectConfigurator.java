/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.configurator;

import com.liferay.gradle.plugins.LiferayBasePlugin;
import com.liferay.gradle.plugins.extensions.AppServer;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.extensions.TomcatAppServer;
import com.liferay.gradle.plugins.workspace.ProjectConfigurator;
import com.liferay.gradle.plugins.workspace.WorkspaceExtension;
import com.liferay.gradle.plugins.workspace.WorkspacePlugin;
import com.liferay.gradle.plugins.workspace.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import java.io.File;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import org.gradle.api.GradleException;
import org.gradle.api.NamedDomainObjectCollection;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.initialization.Settings;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.TaskProvider;

/**
 * @author Andrea Di Giorgi
 * @author Gregory Amerson
 */
public abstract class BaseProjectConfigurator implements ProjectConfigurator {

	public BaseProjectConfigurator(Settings settings) {
		String defaultRootDirNames = GradleUtil.getProperty(
			settings, getDefaultRootDirPropertyName(), (String)null);
		File rootDir = settings.getRootDir();

		if (Validator.isNotNull(defaultRootDirNames)) {
			if (defaultRootDirNames.equals("*")) {
				_defaultRootDirs = Collections.singleton(rootDir);
			}
			else {
				_defaultRootDirs = new HashSet<>();

				for (String dirName : defaultRootDirNames.split("\\s*,\\s*")) {
					File dir = new File(rootDir, dirName);

					_defaultRootDirs.add(dir);
				}
			}
		}
		else {
			_defaultRootDirs = Collections.singleton(rootDir);
		}
	}

	@Override
	public void configureRootProject(
		Project project, WorkspaceExtension workspaceExtension) {
	}

	@Override
	public Iterable<File> getDefaultRootDirs() {
		return _defaultRootDirs;
	}

	@Override
	public Iterable<File> getProjectDirs(File rootDir) {
		try {
			if (!rootDir.exists()) {
				return Collections.emptySet();
			}

			return doGetProjectDirs(rootDir);
		}
		catch (Exception exception) {
			throw new GradleException(
				"Unable to get project directories from " + rootDir, exception);
		}
	}

	protected Copy addTaskDockerDeploy(
		Project project, Object sourcePath, File dockerDeployDir) {

		if (GradleUtil.hasTask(
				project, RootProjectConfigurator.DOCKER_DEPLOY_TASK_NAME)) {

			TaskProvider<Copy> taskProvider = GradleUtil.getTaskProvider(
				project, RootProjectConfigurator.DOCKER_DEPLOY_TASK_NAME,
				Copy.class);

			return taskProvider.get();
		}

		Copy copy = GradleUtil.addTask(
			project, RootProjectConfigurator.DOCKER_DEPLOY_TASK_NAME,
			Copy.class);

		copy.setDescription(
			"Assembles the project and deploys it to the Liferay Docker " +
				"container.");
		copy.setGroup(RootProjectConfigurator.DOCKER_GROUP);

		copy.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);

		copy.from(sourcePath);

		copy.into(
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					return dockerDeployDir;
				}

			});

		Task deployTask = GradleUtil.getTask(
			project, LiferayBasePlugin.DEPLOY_TASK_NAME);

		deployTask.finalizedBy(copy);

		Task buildDockerImageTask = GradleUtil.getTask(
			project.getRootProject(),
			RootProjectConfigurator.BUILD_DOCKER_IMAGE_TASK_NAME);

		buildDockerImageTask.dependsOn(copy);

		return copy;
	}

	protected Copy addTaskDockerDeploy(
		Project project, Object sourcePath,
		WorkspaceExtension workspaceExtension) {

		File dockerDir = workspaceExtension.getDockerDir();

		File dockerDeployDir = new File(dockerDir, "deploy");

		return addTaskDockerDeploy(project, sourcePath, dockerDeployDir);
	}

	protected void configureLiferay(
		Project project, WorkspaceExtension workspaceExtension) {

		LiferayExtension liferayExtension = GradleUtil.getExtension(
			project, LiferayExtension.class);

		liferayExtension.setAppServerParentDir(workspaceExtension.getHomeDir());

		String version = GradleUtil.getProperty(
			project, "app.server.tomcat.version", (String)null);

		File dir = workspaceExtension.getHomeDir();

		if ((version == null) && dir.exists()) {
			for (String fileName : dir.list()) {
				if (!fileName.startsWith("tomcat-")) {
					continue;
				}

				version = fileName.substring(fileName.indexOf("-") + 1);

				NamedDomainObjectCollection<AppServer>
					namedDomainObjectCollection =
						liferayExtension.getAppServers();

				TomcatAppServer tomcatAppServer =
					(TomcatAppServer)namedDomainObjectCollection.getByName(
						"tomcat");

				tomcatAppServer.setVersion(version);
			}
		}
	}

	protected abstract Iterable<File> doGetProjectDirs(File rootDir)
		throws Exception;

	protected String getDefaultRootDirName() {
		return getName();
	}

	protected String getDefaultRootDirPropertyName() {
		return WorkspacePlugin.PROPERTY_PREFIX + getName() + ".dir";
	}

	protected boolean isExcludedDirName(String dirName) {
		if (dirName == null) {
			return false;
		}

		if (dirName.equals(".gradle") || dirName.equals("build") ||
			dirName.equals("build_gradle") || dirName.equals("dist") ||
			dirName.equals("gradle") || dirName.equals("node_modules") ||
			dirName.equals("node_modules_cache") || dirName.equals("src")) {

			return true;
		}

		return false;
	}

	private final Set<File> _defaultRootDirs;

}