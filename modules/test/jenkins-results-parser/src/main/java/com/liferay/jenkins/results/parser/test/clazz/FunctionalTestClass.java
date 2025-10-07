/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.poshi.core.PoshiContext;

import java.io.File;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class FunctionalTestClass extends BaseTestClass {

	@Override
	public int compareTo(TestClass testClass) {
		if (testClass == null) {
			throw new NullPointerException("Test class is null");
		}

		if (!(testClass instanceof FunctionalTestClass)) {
			throw new NullPointerException(
				"Test class is not an instance of FunctionalTestClass");
		}

		FunctionalTestClass functionalTestClass =
			(FunctionalTestClass)testClass;

		return _testClassMethodName.compareTo(
			functionalTestClass.getTestClassMethodName());
	}

	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (!isBuildCachingEnabled()) {
			return null;
		}

		TestReport cachedTestReport = getCachedTestReport();

		if (cachedTestReport == null) {
			return null;
		}

		return cachedTestReport.getDownstreamBuildReport();
	}

	public TestReport getCachedTestReport() {
		if (!isBuildCachingEnabled() || _cachedTestReportSearched) {
			return _cachedTestReport;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		_cachedTestReport = batchTestClassGroup.getCachedTestReport(
			getTestClassMethodName());

		_cachedTestReportSearched = true;

		return _cachedTestReport;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		jsonObject.put(
			"poshi_properties", _poshiProperties
		).put(
			"test_class_method_name", _testClassMethodName
		);

		return jsonObject;
	}

	@Override
	public String getName() {
		String name = super.getName();
		String testClassMethodName = getTestClassMethodName();

		return JenkinsResultsParserUtil.combine(
			name.replaceAll("(.+/)[^/]+", "$1"),
			testClassMethodName.replaceAll("([^\\.]+\\.)?(.*)", "$2"));
	}

	public Properties getPoshiProperties() {
		return _poshiProperties;
	}

	public String getTestClassMethodName() {
		return _testClassMethodName;
	}

	protected FunctionalTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		_testClassMethodName = jsonObject.getString("test_class_method_name");

		_poshiProperties = new Properties();

		JSONObject poshiPropertiesJSONObject = jsonObject.getJSONObject(
			"poshi_properties");

		if (poshiPropertiesJSONObject == null) {
			return;
		}

		for (String key : poshiPropertiesJSONObject.keySet()) {
			_poshiProperties.setProperty(
				key, poshiPropertiesJSONObject.getString(key));
		}
	}

	protected FunctionalTestClass(
		BatchTestClassGroup batchTestClassGroup, String testClassMethodName) {

		super(batchTestClassGroup, _getTestClassFile(testClassMethodName));

		addTestClassMethod(testClassMethodName);

		_testClassMethodName = testClassMethodName;

		_poshiProperties = PoshiContext.getNamespacedClassCommandNameProperties(
			getTestClassMethodName());
	}

	@Override
	protected String getTestName() {
		return getTestClassMethodName();
	}

	private static File _getTestClassFile(String testClassMethodName) {
		Matcher matcher = _poshiTestCasePattern.matcher(testClassMethodName);

		if (!matcher.find()) {
			throw new RuntimeException(
				"Invalid test class method name " + testClassMethodName);
		}

		String className = matcher.group("className");
		String namespace = matcher.group("namespace");

		File testClassFile = null;

		try {
			testClassFile = new File(
				PoshiContext.getFilePathFromFileName(
					className + ".testcase", namespace));
		}
		catch (Exception exception) {
			testClassFile = new File(
				PoshiContext.getFilePathFromFileName(
					className + ".prose", namespace));
		}

		return testClassFile;
	}

	private static final Pattern _poshiTestCasePattern = Pattern.compile(
		"(?<namespace>[^\\.]+)\\.(?<className>[^\\#]+)\\#(?<methodName>.*)");

	private TestReport _cachedTestReport;
	private boolean _cachedTestReportSearched;
	private final Properties _poshiProperties;
	private final String _testClassMethodName;

}