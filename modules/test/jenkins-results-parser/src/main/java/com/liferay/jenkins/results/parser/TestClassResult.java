/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.history.TestClassHistory;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.util.List;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public interface TestClassResult {

	public Build getBuild();

	public String getClassName();

	public long getDuration();

	public Element getGitHubElement();

	public Element getGitHubElement(Boolean uniqueFailures);

	public String getPackageName();

	public String getSimpleClassName();

	public String getStatus();

	public TestClass getTestClass();

	public TestClassHistory getTestClassHistory();

	public String getTestClassReportURL();

	public String getTestClassResultKey();

	public TestResult getTestResult(String testName);

	public List<TestResult> getTestResults();

	public boolean isFailing();

	public boolean isSkipped();

}