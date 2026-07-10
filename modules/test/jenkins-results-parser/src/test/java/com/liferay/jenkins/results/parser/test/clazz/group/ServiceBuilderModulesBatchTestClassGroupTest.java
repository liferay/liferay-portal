/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kenji Heigel
 */
public class ServiceBuilderModulesBatchTestClassGroupTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetBuildType() {
		_testGetBuildType(
			ServiceBuilderModulesBatchTestClassGroup.BuildType.CORE,
			"portal-impl/" + RandomTestUtil.randomString());
		_testGetBuildType(
			ServiceBuilderModulesBatchTestClassGroup.BuildType.CORE,
			"portal-kernel/" + RandomTestUtil.randomString());
		_testGetBuildType(
			ServiceBuilderModulesBatchTestClassGroup.BuildType.FULL,
			"modules/util/portal-tools-service-builder/" +
				RandomTestUtil.randomString());
		_testGetBuildType(
			ServiceBuilderModulesBatchTestClassGroup.BuildType.FULL,
			"modules/util/portal-tools-service-builder/" +
				RandomTestUtil.randomString(),
			"portal-impl/" + RandomTestUtil.randomString());
		_testGetBuildType(null);
		_testGetBuildType(
			null, "modules/apps/" + RandomTestUtil.randomString());
	}

	private void _testGetBuildType(
		ServiceBuilderModulesBatchTestClassGroup.BuildType expectedBuildType,
		String... modifiedFilePaths) {

		ServiceBuilderModulesBatchTestClassGroup
			serviceBuilderModulesBatchTestClassGroup =
				BatchTestClassGroupTestUtil.
					newServiceBuilderModulesBatchTestClassGroup(
						modifiedFilePaths);

		Assert.assertEquals(
			expectedBuildType,
			serviceBuilderModulesBatchTestClassGroup.getBuildType());

		List<File> expectedTestClassFiles = new ArrayList<>();

		if (expectedBuildType != null) {
			PortalGitWorkingDirectory portalGitWorkingDirectory =
				serviceBuilderModulesBatchTestClassGroup.
					getPortalGitWorkingDirectory();

			expectedTestClassFiles.add(
				new File(
					portalGitWorkingDirectory.getWorkingDirectory(),
					"portal-impl/build.xml"));
		}

		Assert.assertEquals(
			expectedTestClassFiles,
			serviceBuilderModulesBatchTestClassGroup.getTestClassFiles());
	}

}