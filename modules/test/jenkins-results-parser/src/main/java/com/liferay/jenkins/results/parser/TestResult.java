/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import org.dom4j.Element;

import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public interface TestResult {

	public Build getBuild();

	public String getClassName();

	public String getDisplayName();

	public long getDuration();

	public String getErrorDetails();

	public String getErrorStackTrace();

	public Element getGitHubElement();

	public String getPackageName();

	public String getSimpleClassName();

	public String getStatus();

	public TestClass getTestClass();

	public TestClassResult getTestClassResult();

	public TestHistory getTestHistory();

	public String getTestName();

	public JSONObject getTestReportJSONObject();

	public String getTestReportURL();

	public boolean isFailing();

	public boolean isSkipped();

	public boolean isUniqueFailure();

}