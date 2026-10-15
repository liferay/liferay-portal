/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JUnitAxisTestClassGroup extends AxisTestClassGroup {

	@Override
	public List<DownstreamBuildReport> getCachedDownstreamBuildReports() {
		if (!isBuildCachingEnabled() || !isResultsCached()) {
			return null;
		}

		Set<DownstreamBuildReport> cachedDownstreamBuildReports =
			new HashSet<>();

		for (JUnitTestClass jUnitTestClass : getJUnitTestClasses()) {
			DownstreamBuildReport downstreamBuildReport =
				jUnitTestClass.getCachedDownstreamBuildReport(
					getBatchTestClassGroup());

			cachedDownstreamBuildReports.add(downstreamBuildReport);
		}

		return new ArrayList<>(cachedDownstreamBuildReports);
	}

	public List<JUnitTestClass> getJUnitTestClasses() {
		List<JUnitTestClass> jUnitTestClasses = new ArrayList<>();

		for (TestClass testClass : getTestClasses()) {
			if (!(testClass instanceof JUnitTestClass)) {
				continue;
			}

			jUnitTestClasses.add((JUnitTestClass)testClass);
		}

		return jUnitTestClasses;
	}

	@Override
	public boolean isResultsCached() {
		if (!isBuildCachingEnabled()) {
			return false;
		}

		for (JUnitTestClass jUnitTestClass : getJUnitTestClasses()) {
			List<TestClassReport> cachedTestClassReports =
				jUnitTestClass.getCachedTestClassReports(
					getBatchTestClassGroup());

			if ((cachedTestClassReports == null) ||
				cachedTestClassReports.isEmpty()) {

				return false;
			}
		}

		return true;
	}

	protected JUnitAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);
	}

	protected JUnitAxisTestClassGroup(
		JUnitBatchTestClassGroup jUnitBatchTestClassGroup) {

		super(jUnitBatchTestClassGroup);
	}

}