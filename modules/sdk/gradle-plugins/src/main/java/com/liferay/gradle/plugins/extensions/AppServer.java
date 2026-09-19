/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.extensions;

import com.liferay.gradle.plugins.internal.util.GradleUtil;
import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.OSDetector;

import java.io.File;
import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.gradle.api.Project;

/**
 * @author Andrea Di Giorgi
 */
public class AppServer {

	public AppServer(String name, Project project) {
		_name = name;

		this.project = project;
	}

	public void addAdditionalDependencies(String configurationName) {
	}

	public File getBinDir() {
		return GradleUtil.toFile(project, _binDir);
	}

	public String getCheckPath() {
		return GradleUtil.toString(_checkPath);
	}

	public File getDeployDir() {
		return GradleUtil.toFile(project, _deployDir);
	}

	public File getDir() {
		return GradleUtil.toFile(project, _dir);
	}

	public String getFileSuffixBat() {
		if (OSDetector.isWindows()) {
			return ".bat";
		}

		return ".sh";
	}

	public File getLibGlobalDir() {
		return GradleUtil.toFile(project, _libGlobalDir);
	}

	public String getName() {
		return _name;
	}

	public int getPortNumber() {
		return GradleUtil.toInteger(_portNumber);
	}

	public File getPortalDir() {
		return GradleUtil.toFile(project, _portalDir);
	}

	public File getShieldedContainerLibPortalDir() {
		return GradleUtil.toFile(project, _shieldedContainerLibPortalDir);
	}

	public String getStartExecutable() {
		return GradleUtil.toString(_startExecutable);
	}

	public List<String> getStartExecutableArgs() {
		return GradleUtil.toStringList(_startExecutableArgs);
	}

	public String getStopExecutable() {
		return GradleUtil.toString(_stopExecutable);
	}

	public List<String> getStopExecutableArgs() {
		return GradleUtil.toStringList(_stopExecutableArgs);
	}

	public String getVersion() {
		return GradleUtil.toString(_version);
	}

	public String getZipUrl() {
		return GradleUtil.toString(_zipUrl);
	}

	public boolean isReachable() {
		try {
			URL url = new URL(
				"http", "localhost", getPortNumber(), getCheckPath());

			HttpURLConnection httpURLConnection =
				(HttpURLConnection)url.openConnection();

			httpURLConnection.setRequestMethod("GET");

			int responseCode = httpURLConnection.getResponseCode();

			if ((responseCode > 0) && (responseCode < 400)) {
				return true;
			}
		}
		catch (IOException ioException) {
		}

		return false;
	}

	public void setBinDir(Object binDir) {
		_binDir = binDir;
	}

	public void setCheckPath(Object checkPath) {
		_checkPath = checkPath;
	}

	public void setDeployDir(Object deployDir) {
		_deployDir = deployDir;
	}

	public void setDir(Object dir) {
		_dir = dir;
	}

	public void setLibGlobalDir(Object libGlobalDir) {
		_libGlobalDir = libGlobalDir;
	}

	public void setPortNumber(Object portNumber) {
		_portNumber = portNumber;
	}

	public void setPortalDir(Object portalDir) {
		_portalDir = portalDir;
	}

	public void setShieldedContainerLibPortalDir(
		Object shieldedContainerLibPortalDir) {

		_shieldedContainerLibPortalDir = shieldedContainerLibPortalDir;
	}

	public void setStartExecutable(Object startExecutable) {
		_startExecutable = startExecutable;
	}

	@SuppressWarnings("unchecked")
	public void setStartExecutableArgs(Iterable<?> startExecutableArgs) {
		_startExecutableArgs.clear();

		GUtil.addToCollection(_startExecutableArgs, startExecutableArgs);
	}

	public void setStopExecutable(Object stopExecutable) {
		_stopExecutable = stopExecutable;
	}

	@SuppressWarnings("unchecked")
	public void setStopExecutableArgs(Iterable<?> stopExecutableArgs) {
		_stopExecutableArgs.clear();

		GUtil.addToCollection(_stopExecutableArgs, stopExecutableArgs);
	}

	public void setVersion(Object version) {
		_version = version;
	}

	public void setZipUrl(Object zipUrl) {
		_zipUrl = zipUrl;
	}

	protected final Project project;

	private Object _binDir;
	private Object _checkPath = "/web/guest";
	private Object _deployDir;
	private Object _dir;
	private Object _libGlobalDir;
	private final String _name;
	private Object _portNumber = 8080;
	private Object _portalDir;
	private Object _shieldedContainerLibPortalDir;
	private Object _startExecutable;
	private final List<Object> _startExecutableArgs = new ArrayList<>();
	private Object _stopExecutable;
	private final List<Object> _stopExecutableArgs = new ArrayList<>();
	private Object _version;
	private Object _zipUrl;

}