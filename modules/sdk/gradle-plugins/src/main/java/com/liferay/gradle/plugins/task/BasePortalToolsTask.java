/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.task;

import com.liferay.gradle.plugins.internal.util.FileUtil;
import com.liferay.gradle.plugins.internal.util.GradleUtil;

import java.io.InputStream;

import java.util.List;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.JavaExec;
import org.gradle.process.JavaExecSpec;

/**
 * @author Andrea Di Giorgi
 */
public abstract class BasePortalToolsTask extends JavaExec {

	public BasePortalToolsTask() {
		project = getProject();

		_addConfiguration();
	}

	@Override
	public JavaExecSpec args(Iterable<?> args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaExec args(Object... args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaExec classpath(Object... paths) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void exec() {
		doExec(getArgs());
	}

	@Override
	public abstract List<String> getArgs();

	@Override
	public FileCollection getClasspath() {
		return GradleUtil.getConfiguration(project, getConfigurationName());
	}

	@Override
	public JavaExec setArgs(Iterable<?> args) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaExec setClasspath(FileCollection fileCollection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public JavaExec setStandardInput(InputStream inputStream) {
		throw new UnsupportedOperationException();
	}

	protected void addDependencies() {
		addDependency(
			"com.liferay.portal", "com.liferay.portal.impl", "default");
		addDependency(
			"com.liferay.portal", "com.liferay.portal.kernel", "default");
		addDependency("com.liferay.portal", "com.liferay.util.java", "default");
		addDependency("com.thoughtworks.xstream", "xstream", "1.4.19");
		addDependency("commons-configuration", "commons-configuration", "1.6");
		addDependency("commons-io", "commons-io", "2.1");
		addDependency("commons-lang", "commons-lang", "2.6");
		addDependency("easyconf", "easyconf", "0.9.5", false);
		addDependency("jakarta.servlet", "jakarta.servlet-api", "6.0.0");
	}

	protected void addDependency(String group, String name, String version) {
		addDependency(group, name, version, true);
	}

	protected void addDependency(
		String group, String name, String version, boolean transitive) {

		GradleUtil.addDependency(
			project, getConfigurationName(), group, name, version, transitive);
	}

	protected void doExec(List<String> args) {
		super.setArgs(args);
		super.setClasspath(FileUtil.shrinkClasspath(project, getClasspath()));
		super.setErrorOutput(System.err);

		super.exec();
	}

	@Input
	protected String getConfigurationName() {
		return "portalTools" + getToolName();
	}

	@Input
	protected abstract String getToolName();

	protected final Project project;

	private Configuration _addConfiguration() {
		ConfigurationContainer configurationContainer =
			project.getConfigurations();

		Configuration configuration = configurationContainer.findByName(
			getConfigurationName());

		if (configuration != null) {
			return configuration;
		}

		configuration = GradleUtil.addConfiguration(
			project, getConfigurationName());

		configuration.setDescription(
			"Configures the " + getToolName() + " tool for this project.");
		configuration.setVisible(false);

		GradleUtil.executeIfEmpty(
			configuration,
			new Action<Configuration>() {

				@Override
				public void execute(Configuration configuration) {
					addDependencies();
				}

			});

		return configuration;
	}

}