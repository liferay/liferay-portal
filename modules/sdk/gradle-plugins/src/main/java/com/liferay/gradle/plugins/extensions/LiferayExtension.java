/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.extensions;

import com.liferay.gradle.plugins.internal.extensions.AppServerFactory;
import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.util.Validator;

import groovy.lang.Closure;

import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ModuleVersionSelector;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.bundling.AbstractArchiveTask;

/**
 * @author Andrea Di Giorgi
 */
public class LiferayExtension {

	@SuppressWarnings("serial")
	public LiferayExtension(final Project project) {
		this.project = project;

		_appServerParentDir = new Callable<File>() {

			@Override
			public File call() throws Exception {
				File dir = GradleUtil.getProperty(
					project, "app.server.parent.dir", (File)null);

				if (dir == null) {
					Project rootProject = project.getRootProject();

					dir = rootProject.file("../bundles");
				}

				return dir;
			}

		};

		_appServerType = new Callable<String>() {

			@Override
			public String call() throws Exception {
				return GradleUtil.getProperty(
					project, "app.server.type", "tomcat");
			}

		};

		_appServers = project.container(
			AppServer.class, new AppServerFactory(project));

		_deployDir = new Callable<File>() {

			@Override
			public File call() throws Exception {
				File dir = GradleUtil.getProperty(
					project, "auto.deploy.dir", (File)null);

				if (dir == null) {
					dir = new File(getAppServerParentDir(), "deploy");
				}

				return dir;
			}

		};

		_deployedFileNameClosure = new Closure<String>(project) {

			@SuppressWarnings("unused")
			public String doCall(AbstractArchiveTask abstractArchiveTask) {
				Property<String> property =
					abstractArchiveTask.getArchiveBaseName();

				String fileName = property.get();

				property = abstractArchiveTask.getArchiveAppendix();

				if (property != null) {
					String archiveAppendix = property.getOrNull();

					if (Validator.isNotNull(archiveAppendix)) {
						fileName += "-" + archiveAppendix;
					}
				}

				property = abstractArchiveTask.getArchiveExtension();

				fileName += "." + property.get();

				return fileName;
			}

		};

		_jmxRemotePort = new Callable<Integer>() {

			@Override
			public Integer call() throws Exception {
				return GradleUtil.getProperty(project, "jmx.remote.port", 8099);
			}

		};

		_liferayHome = new Callable<File>() {

			@Override
			public File call() throws Exception {
				return GradleUtil.getProperty(
					project, "liferay.home", getAppServerParentDir());
			}

		};
	}

	public void appServers(Closure<?> closure) {
		_appServers.configure(closure);
	}

	public void defaultDependencyNotation(
		String group, String name, Object version) {

		_defaultVersions.put(_getDependencyNotation(group, name), version);
	}

	public AppServer getAppServer() {
		return getAppServer(getAppServerType());
	}

	public AppServer getAppServer(String type) {
		return _appServers.getAt(type);
	}

	public File getAppServerDeployDir() {
		AppServer appServer = getAppServer();

		return appServer.getDeployDir();
	}

	public File getAppServerDir() {
		AppServer appServer = getAppServer();

		return appServer.getDir();
	}

	public File getAppServerLibGlobalDir() {
		AppServer appServer = getAppServer();

		return appServer.getLibGlobalDir();
	}

	public File getAppServerParentDir() {
		return project.file(_appServerParentDir);
	}

	public File getAppServerPortalDir() {
		AppServer appServer = getAppServer();

		return appServer.getPortalDir();
	}

	public File getAppServerShieldedContainerLibPortalDir() {
		AppServer appServer = getAppServer();

		return appServer.getShieldedContainerLibPortalDir();
	}

	public String getAppServerType() {
		return GradleUtil.toString(_appServerType);
	}

	public NamedDomainObjectContainer<AppServer> getAppServers() {
		return _appServers;
	}

	public String getDefaultVersion(
		ModuleVersionSelector moduleVersionSelector) {

		return getDefaultVersion(
			moduleVersionSelector.getGroup(), moduleVersionSelector.getName());
	}

	public String getDefaultVersion(String group, String name) {
		return getDefaultVersion(group, name, "latest.release");
	}

	public String getDefaultVersion(
		String group, String name, String defaultVersion) {

		String dependencyNotation = _getDependencyNotation(group, name);

		String version = GradleUtil.toString(
			_defaultVersions.get(dependencyNotation));

		if (Validator.isNull(version)) {
			version = GradleUtil.getProperty(
				project, group + "." + name + ".version", (String)null);

			if (Validator.isNull(version)) {
				version = GradleUtil.getProperty(
					project, name + ".version", defaultVersion);
			}

			_defaultVersions.put(dependencyNotation, version);
		}

		return version;
	}

	public File getDeployDir() {
		return project.file(_deployDir);
	}

	public Closure<String> getDeployedFileNameClosure() {
		return _deployedFileNameClosure;
	}

	public int getJmxRemotePort() {
		Integer jmxRemotePort = GradleUtil.toInteger(_jmxRemotePort);

		if (jmxRemotePort != null) {
			return jmxRemotePort;
		}

		return 0;
	}

	public File getLiferayHome() {
		return project.file(_liferayHome);
	}

	public void setAppServerParentDir(Object appServerParentDir) {
		_appServerParentDir = appServerParentDir;
	}

	public void setAppServerType(String appServerType) {
		_appServerType = appServerType;
	}

	public void setDeployDir(Object deployDir) {
		_deployDir = deployDir;
	}

	public void setDeployedFileNameClosure(
		Closure<String> deployedFileNameClosure) {

		_deployedFileNameClosure = deployedFileNameClosure;
	}

	public void setJmxRemotePort(Object jmxRemotePort) {
		_jmxRemotePort = jmxRemotePort;
	}

	public void setLiferayHome(Object liferayHome) {
		_liferayHome = liferayHome;
	}

	protected final Project project;

	private String _getDependencyNotation(String group, String name) {
		return group + ":" + name;
	}

	private Object _appServerParentDir;
	private Object _appServerType;
	private final NamedDomainObjectContainer<AppServer> _appServers;
	private final Map<String, Object> _defaultVersions = new HashMap<>();
	private Object _deployDir;
	private Closure<String> _deployedFileNameClosure;
	private Object _jmxRemotePort;
	private Object _liferayHome;

}