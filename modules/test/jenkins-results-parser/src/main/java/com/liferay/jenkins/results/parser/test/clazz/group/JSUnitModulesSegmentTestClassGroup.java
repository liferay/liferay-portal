/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSUnitModulesSegmentTestClassGroup
	extends ModulesSegmentTestClassGroup {

	protected JSUnitModulesSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		super(batchTestClassGroup);
	}

	protected JSUnitModulesSegmentTestClassGroup(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);
	}

	@Override
	protected String getTestTaskNames(int axisIndex) {
		StringBuilder sb = new StringBuilder();

		AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup(
			axisIndex);

		for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
			sb.append(testClass.getTestTaskName());
			sb.append(",");
		}

		if (sb.length() > 0) {
			sb.setLength(sb.length() - 1);
		}

		return sb.toString();
	}

}