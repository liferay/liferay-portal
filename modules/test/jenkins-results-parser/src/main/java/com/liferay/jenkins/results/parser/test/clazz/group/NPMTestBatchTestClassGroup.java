/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalTestClassJob;
import com.liferay.jenkins.results.parser.test.clazz.NPMTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class NPMTestBatchTestClassGroup extends BatchTestClassGroup {

	@Override
	public AxisTestClassGroup getAxisTestClassGroup(int axisId) {
		if (axisId != 0) {
			throw new IllegalArgumentException("axisId is not 0");
		}

		AxisTestClassGroup axisTestClassGroup = axisTestClassGroups.get(axisId);

		if (axisTestClassGroup != null) {
			return axisTestClassGroups.get(axisId);
		}

		return TestClassGroupFactory.newAxisTestClassGroup(this);
	}

	public void writeTestCSVReportFile() throws Exception {
		CSVReport csvReport = new CSVReport(
			new CSVReport.Row(
				"Module Name", "Class Name", "Method Name", "Ignored",
				"File Path"));

		for (NPMTestClass npmTestClass : TestClassFactory.getNPMTestClasses()) {
			File moduleTestClassFile = npmTestClass.getTestClassFile();

			String moduleName = moduleTestClassFile.getName();

			List<TestClassMethod> jsTestClassMethods =
				npmTestClass.getJSTestClassMethods();

			for (TestClassMethod jsTestClassMethod : jsTestClassMethods) {
				String classMethodName = jsTestClassMethod.getName();

				int colonIndex = classMethodName.indexOf(
					_TOKEN_CLASS_METHOD_SEPARATOR);

				String filePath = classMethodName.substring(0, colonIndex);

				String className = filePath.substring(
					filePath.lastIndexOf("/") + 1);

				String methodName = classMethodName.substring(
					colonIndex + _TOKEN_CLASS_METHOD_SEPARATOR.length());

				CSVReport.Row csvReportRow = new CSVReport.Row();

				csvReportRow.add(moduleName);
				csvReportRow.add(className);
				csvReportRow.add(StringEscapeUtils.escapeCsv(methodName));

				if (jsTestClassMethod.isIgnored()) {
					csvReportRow.add("TRUE");
				}
				else {
					csvReportRow.add("");
				}

				csvReportRow.add(filePath);

				csvReport.addRow(csvReportRow);
			}
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

		File csvReportFile = new File(
			JenkinsResultsParserUtil.combine(
				"Report_js_", simpleDateFormat.format(new Date()), ".csv"));

		try {
			JenkinsResultsParserUtil.write(csvReportFile, csvReport.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected NPMTestBatchTestClassGroup(
		JSONObject jsonObject, PortalTestClassJob portalTestClassJob) {

		super(jsonObject, portalTestClassJob);
	}

	protected NPMTestBatchTestClassGroup(
		String batchName, PortalTestClassJob portalTestClassJob) {

		super(batchName, portalTestClassJob);

		if (ignore()) {
			return;
		}

		List<File> moduleDirs;

		try {
			if (testRelevantChanges &&
				!(includeStableTestSuite && isStableTestSuiteBatch())) {

				moduleDirs =
					portalGitWorkingDirectory.
						getModifiedNPMTestModuleDirsList();
			}
			else {
				moduleDirs =
					portalGitWorkingDirectory.getNPMTestModuleDirsList();
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		if (moduleDirs.isEmpty()) {
			return;
		}

		AxisTestClassGroup axisTestClassGroup =
			TestClassGroupFactory.newAxisTestClassGroup(this);

		for (File moduleDir : moduleDirs) {
			TestClass testClass = TestClassFactory.newTestClass(
				this, moduleDir);

			if (!testClass.hasTestClassMethods()) {
				continue;
			}

			addTestClass(testClass);

			axisTestClassGroup.addTestClass(testClass);
		}

		axisTestClassGroups.add(0, axisTestClassGroup);
	}

	private static final String _TOKEN_CLASS_METHOD_SEPARATOR = "::";

}