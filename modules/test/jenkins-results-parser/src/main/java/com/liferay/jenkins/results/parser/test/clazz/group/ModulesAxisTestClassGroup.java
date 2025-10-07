/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.ModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class ModulesAxisTestClassGroup extends AxisTestClassGroup {

	public List<ModulesTestClass> getModulesTestClasses() {
		List<ModulesTestClass> modulesTestClasses = new ArrayList<>();

		for (TestClass testClass : getTestClasses()) {
			if (!(testClass instanceof ModulesTestClass)) {
				continue;
			}

			modulesTestClasses.add((ModulesTestClass)testClass);
		}

		return modulesTestClasses;
	}

	@Override
	public boolean isResultsCached() {
		if (_resultsCached != null) {
			return _resultsCached;
		}

		if (!isBuildCachingEnabled()) {
			_resultsCached = false;

			return _resultsCached;
		}

		for (ModulesTestClass modulesTestClass : getModulesTestClasses()) {
			TestClassReport cachedTestClassReport =
				modulesTestClass.getCachedTestClassReport();

			if (cachedTestClassReport == null) {
				_resultsCached = false;

				return _resultsCached;
			}
		}

		_resultsCached = true;

		return _resultsCached;
	}

	protected ModulesAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);
	}

	protected ModulesAxisTestClassGroup(
		ModulesBatchTestClassGroup modulesBatchTestClassGroup) {

		super(modulesBatchTestClassGroup);
	}

	private Boolean _resultsCached;

}