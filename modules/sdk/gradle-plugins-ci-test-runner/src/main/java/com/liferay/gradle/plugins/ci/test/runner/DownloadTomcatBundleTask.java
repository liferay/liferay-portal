/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.ci.test.runner;

import com.liferay.ant.mirrors.get.MirrorsGetTask;
import com.liferay.gradle.plugins.extensions.LiferayExtension;
import com.liferay.gradle.plugins.extensions.TomcatAppServer;
import com.liferay.gradle.util.GradleUtil;

import org.apache.tools.ant.Project;

/**
 * @author Calum Ragan
 */
@CacheableTask
public class DownloadTomcatBundleTask extends JavaExec {

	public DownloadTomcatBundleTask() {
		Property<String> mainClass = getMainClass();

		mainClass.set("com.liferay.gradle.plugins.ci.test.runner.CITestRunner");

		LiferayExtension liferayExtension = extensionContainer.getByType(
			LiferayExtension.class);

		TomcatAppServer tomcatAppServer =
			(TomcatAppServer)liferayExtension.getAppServer("tomcat");

		setSrc(tomcatAppServer.getZipUrl());

		setVerbose(true);

		setParentDir(liferayExtension.getAppServerParentDir());
		setZipName(tomcatAppServer.getZipName());

		File tomcatAppServerZipFile = new File(getParentDir(), getZipName());

		setDest(tomcatAppServerZipFile);
	}

	@Override
	public void exec() {
		MirrorsGetTask mirrorsGetTask = new MirrorsGetTask();

		Project antProject = new Project();

		antProject.init();

		mirrorsGetTask.setProject(antProject);

		mirrorsGetTask.setVerbose(getVerbose());

		mirrorsGetTask.setSrc(getSrc());

		mirrorsGetTask.setDest(getDest());

		try {
			mirrorsGetTask.execute();
		}
		catch (BuildException buildException) {
			throw new RuntimeException(buildException);
		}

		super.exec();
	}

	@Input
	public File getDest() {
		return GradleUtil.toFile(_dest);
	}

	@Input
	public File getParentDir() {
		return GradleUtil.toFile(_parentDir);
	}

	@Input
	public String getSHA() {
		return GradleUtil.toString(_sha);
	}

	@Input
	public String getSrc() {
		return GradleUtil.toString(_src);
	}

	@Input
	public String getZipName() {
		return GradleUtil.toString(_zipName);
	}

	@Input
	public Boolean isVerbose() {
		return _verbose;
	}

	public void setDest(Object dest) {
		_dest = dest;
	}

	public void setParentDir(Object parentDir) {
		_parentDir = parentDir;
	}

	public void setSHA(Object sha) {
		_sha = sha;
	}

	public void setSrc(Object src) {
		_src = src;
	}

	public void setVerbose(Boolean verbose) {
		_verbose = verbose;
	}

	public void setZipName(Object zipName) {
		_zipName = zipName;
	}

	private Object _dest;
	private Object _sha;
	private Object _src;
	private Boolean _verbose;
	private Object _zipName;

}