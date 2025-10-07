/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JUnitSegmentTestClassGroup extends SegmentTestClassGroup {

	@Override
	public String getTestCasePropertiesContent() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getTestCasePropertiesContent());

		List<String> axisIndexes = new ArrayList<>();

		for (int axisIndex = 0; axisIndex < getAxisCount(); axisIndex++) {
			axisIndexes.add(String.valueOf(axisIndex));

			AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup(
				axisIndex);

			List<TestClass> testClasses = axisTestClassGroup.getTestClasses();

			sb.append("TEST_CLASS_GROUP_");
			sb.append(axisIndex);
			sb.append("=");

			for (TestClass testClass : testClasses) {
				Matcher matcher = _pattern.matcher(
					String.valueOf(testClass.getTestClassFile()));

				if (!matcher.find()) {
					continue;
				}

				JUnitTestClass jUnitTestClass = (JUnitTestClass)testClass;

				String testClassFileName = matcher.group("testClassFileName");

				testClassFileName = testClassFileName.replace(
					".java", ".class");

				List<String> testClassMethodNames =
					jUnitTestClass.getTestClassMethodNames();

				if ((testClassMethodNames != null) &&
					!testClassMethodNames.isEmpty()) {

					for (String testClassMethodName : testClassMethodNames) {
						sb.append(testClassFileName);
						sb.append("#");
						sb.append(testClassMethodName);
						sb.append(",");
					}
				}
				else {
					sb.append(testClassFileName);

					sb.append(",");
				}
			}

			if (!testClasses.isEmpty()) {
				sb.setLength(sb.length() - 1);
			}

			sb.append("\n");
		}

		sb.append("TEST_CLASS_GROUPS=");
		sb.append(JenkinsResultsParserUtil.join(" ", axisIndexes));
		sb.append("\n");

		return sb.toString();
	}

	protected JUnitSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		super(batchTestClassGroup);
	}

	protected JUnitSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);
	}

	private static final Pattern _pattern = Pattern.compile(
		".*/(?<testClassFileName>com/.*)");

}