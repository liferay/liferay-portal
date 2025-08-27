/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.poshi.runner;

import com.liferay.gradle.util.GUtil;
import com.liferay.gradle.util.GradleUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gradle.api.Project;

/**
 * @author Andrea Di Giorgi
 */
public class PoshiRunnerExtension {

	public PoshiRunnerExtension(Project project) {
		_project = project;
	}

	public File getBaseDir() {
		return GradleUtil.toFile(_project, _baseDir);
	}

	public String getOpenCVVersion() {
		return GradleUtil.toString(_openCVVersion);
	}

	public Map<String, Object> getPoshiProperties() {
		return _poshiProperties;
	}

	public File getPoshiPropertiesFile() {
		return GradleUtil.toFile(_project, _poshiPropertiesFile);
	}

	public List<File> getPoshiPropertiesFiles() {
		List<File> poshiPropertiesFiles = new ArrayList<>();

		for (Object object : _poshiPropertiesFiles) {
			File file = GradleUtil.toFile(_project, object);

			poshiPropertiesFiles.add(file);
		}

		return poshiPropertiesFiles;
	}

	public List<String> getTestNames() {
		return GradleUtil.toStringList(_testNames);
	}

	public String getTestRunType() {
		return GradleUtil.toString(_testRunType);
	}

	public String getVersion() {
		return GradleUtil.toString(_version);
	}

	public void poshiProperties(Map<String, ?> poshiProperties) {
		_poshiProperties.putAll(poshiProperties);
	}

	public void poshiProperty(String key, Object value) {
		_poshiProperties.put(key, value);
	}

	public void setBaseDir(Object baseDir) {
		_baseDir = baseDir;
	}

	public void setOpenCVVersion(Object openCVVersion) {
		_openCVVersion = openCVVersion;
	}

	public void setPoshiProperties(Map<String, ?> poshiProperties) {
		_poshiProperties.clear();

		poshiProperties(poshiProperties);
	}

	public void setPoshiPropertiesFile(Object poshiPropertiesFile) {
		_poshiPropertiesFile = poshiPropertiesFile;
	}

	public void setPoshiPropertiesFiles(List<Object> poshiPropertiesFiles) {
		_poshiPropertiesFiles = poshiPropertiesFiles;
	}

	public void setTestNames(Iterable<Object> testNames) {
		_testNames.clear();

		testNames(testNames);
	}

	public void setTestNames(Object... testNames) {
		setTestNames(Arrays.asList(testNames));
	}

	public void setTestRunType(Object testRunType) {
		_testRunType = testRunType;
	}

	public void setVersion(Object version) {
		_version = version;
	}

	public void testNames(Iterable<Object> testNames) {
		GUtil.addToCollection(_testNames, testNames);
	}

	public void testNames(Object... testNames) {
		testNames(Arrays.asList(testNames));
	}

	private Object _baseDir = "src/testFunctional";
	private Object _openCVVersion = "2.4.9-0.9";
	private final Map<String, Object> _poshiProperties = new HashMap<>();
	private Object _poshiPropertiesFile = "poshi.properties";
	private List<Object> _poshiPropertiesFiles = new ArrayList<>(
		Arrays.asList("poshi.properties"));
	private final Project _project;
	private final Set<Object> _testNames = new LinkedHashSet<>();
	private Object _testRunType = "sequential";
	private Object _version = "1.0.498";

}