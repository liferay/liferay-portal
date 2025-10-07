/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.test.clazz.ServiceBuilderAntTargetTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class ModulesSegmentTestClassGroup extends SegmentTestClassGroup {

	@Override
	public String getTestCasePropertiesContent() {
		StringBuilder sb = new StringBuilder();

		sb.append(super.getTestCasePropertiesContent());

		List<String> axisIndexes = new ArrayList<>();

		for (int axisIndex = 0; axisIndex < getAxisCount(); axisIndex++) {
			axisIndexes.add(String.valueOf(axisIndex));

			sb.append("TEST_CLASS_GROUP_");
			sb.append(axisIndex);
			sb.append("=");
			sb.append(getTestTaskNames(axisIndex));
			sb.append("\n");
		}

		sb.append("TEST_CLASS_GROUPS=");
		sb.append(JenkinsResultsParserUtil.join(" ", axisIndexes));
		sb.append("\n");

		return sb.toString();
	}

	protected ModulesSegmentTestClassGroup(
		BatchTestClassGroup parentBatchTestClassGroup) {

		super(parentBatchTestClassGroup);
	}

	protected ModulesSegmentTestClassGroup(
		BatchTestClassGroup parentBatchTestClassGroup, JSONObject jsonObject) {

		super(parentBatchTestClassGroup, jsonObject);
	}

	protected String getTestTaskNames(int axisIndex) {
		StringBuilder sb = new StringBuilder();

		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup(
			axisIndex);

		for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
			if (testClass instanceof ServiceBuilderAntTargetTestClass) {
				continue;
			}

			for (TestClassMethod testClassMethod :
					testClass.getTestClassMethods()) {

				sb.append(testClassMethod.getName());
				sb.append(",");
			}
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

}