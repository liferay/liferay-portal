/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.TestReport;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightTestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class PlaywrightAxisTestClassGroup extends AxisTestClassGroup {

	@Override
	public List<DownstreamBuildReport> getCachedDownstreamBuildReports() {
		if (!isBuildCachingEnabled() || !isResultsCached()) {
			return null;
		}

		List<DownstreamBuildReport> cachedDownstreamBuildReports =
			new ArrayList<>();

		for (PlaywrightTestClassMethod playwrightTestClassMethod :
				getPlaywrightTestClassMethods()) {

			DownstreamBuildReport downstreamBuildReport =
				playwrightTestClassMethod.getCachedDownstreamBuildReport();

			if (cachedDownstreamBuildReports.contains(downstreamBuildReport)) {
				continue;
			}

			cachedDownstreamBuildReports.add(downstreamBuildReport);
		}

		return cachedDownstreamBuildReports;
	}

	@Override
	public Integer getMinimumSlaveRAM() {
		List<TestClass> testClasses = getTestClasses();

		if (testClasses.isEmpty()) {
			return super.getMinimumSlaveRAM();
		}

		TestClass testClass = testClasses.get(0);

		if (!(testClass instanceof PlaywrightJUnitTestClass)) {
			return super.getMinimumSlaveRAM();
		}

		PlaywrightJUnitTestClass playwrightJUnitTestClass =
			(PlaywrightJUnitTestClass)testClass;

		Integer minimumSlaveRAM = playwrightJUnitTestClass.getMinimumSlaveRAM();

		if (minimumSlaveRAM == null) {
			return super.getMinimumSlaveRAM();
		}

		return minimumSlaveRAM;
	}

	public List<PlaywrightTestClassMethod> getPlaywrightTestClassMethods() {
		List<PlaywrightTestClassMethod> playwrightTestClassMethods =
			new ArrayList<>();

		for (TestClass testClass : getTestClasses()) {
			for (TestClassMethod testClassMethod :
					testClass.getTestClassMethods()) {

				if (!(testClassMethod instanceof PlaywrightTestClassMethod)) {
					continue;
				}

				playwrightTestClassMethods.add(
					(PlaywrightTestClassMethod)testClassMethod);
			}
		}

		return playwrightTestClassMethods;
	}

	@Override
	public String getSlaveLabel() {
		List<TestClass> testClasses = getTestClasses();

		if (testClasses.isEmpty()) {
			return super.getSlaveLabel();
		}

		TestClass testClass = testClasses.get(0);

		if (!(testClass instanceof PlaywrightJUnitTestClass)) {
			return super.getSlaveLabel();
		}

		PlaywrightJUnitTestClass playwrightJUnitTestClass =
			(PlaywrightJUnitTestClass)testClass;

		String slaveLabel = playwrightJUnitTestClass.getSlaveLabel();

		if (slaveLabel == null) {
			return super.getSlaveLabel();
		}

		return slaveLabel;
	}

	public Boolean isAnalyticsCloudEnabled() {
		List<TestClass> testClasses = getTestClasses();

		if (testClasses.isEmpty()) {
			return false;
		}

		for (TestClass testClass : testClasses) {
			PlaywrightJUnitTestClass playwrightJUnitTestClass =
				(PlaywrightJUnitTestClass)testClass;

			if (playwrightJUnitTestClass.isAnalyticsCloudEnabled()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isResultsCached() {
		if (!isBuildCachingEnabled()) {
			return false;
		}

		for (PlaywrightTestClassMethod playwrightTestClassMethod :
				getPlaywrightTestClassMethods()) {

			TestReport cachedTestReport =
				playwrightTestClassMethod.getCachedTestReport();

			if (cachedTestReport == null) {
				return false;
			}
		}

		return true;
	}

	protected PlaywrightAxisTestClassGroup(
		BatchTestClassGroup batchTestClassGroup) {

		super(batchTestClassGroup);
	}

	protected PlaywrightAxisTestClassGroup(
		JSONObject jsonObject, SegmentTestClassGroup segmentTestClassGroup) {

		super(jsonObject, segmentTestClassGroup);
	}

}