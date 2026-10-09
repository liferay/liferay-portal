/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightSegmentTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.SegmentTestClassGroup;

import java.io.File;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class BaseJobTest extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetWorkspaceNames() {
		String workspaceName1 = RandomTestUtil.randomString();
		String workspaceName2 = RandomTestUtil.randomString();

		_testGetWorkspaceNames(
			new TreeSet<>(Arrays.asList(workspaceName1, workspaceName2)),
			Arrays.asList(
				Arrays.asList(workspaceName1, null, workspaceName2),
				Arrays.asList(workspaceName1)));

		_testGetWorkspaceNames(
			Collections.emptySet(), Arrays.asList(Arrays.asList("", null)));
		_testGetWorkspaceNames(Collections.emptySet(), Collections.emptyList());
	}

	@Test
	public void testGetWorkspaceNamesSegmentTypes() throws Exception {
		Set<String> expectedWorkspaceNames = new TreeSet<>();
		List<SegmentTestClassGroup> segmentTestClassGroups = new ArrayList<>();

		for (Class<?> clazz : _getSegmentTestClassGroupClasses()) {
			SegmentTestClassGroup segmentTestClassGroup =
				(SegmentTestClassGroup)Mockito.mock(clazz);

			if (segmentTestClassGroup instanceof
					PlaywrightSegmentTestClassGroup) {

				String workspaceName = RandomTestUtil.randomString();

				Mockito.doReturn(
					workspaceName
				).when(
					(PlaywrightSegmentTestClassGroup)segmentTestClassGroup
				).getWorkspaceName();

				expectedWorkspaceNames.add(workspaceName);
			}

			segmentTestClassGroups.add(segmentTestClassGroup);
		}

		Assert.assertTrue(expectedWorkspaceNames.size() > 1);
		Assert.assertTrue(
			segmentTestClassGroups.size() > expectedWorkspaceNames.size());

		testEquals(
			expectedWorkspaceNames,
			_getWorkspaceNames(
				Collections.singletonList(segmentTestClassGroups)));
	}

	private List<Class<?>> _getSegmentTestClassGroupClasses() throws Exception {
		List<Class<?>> segmentTestClassGroupClasses = new ArrayList<>();

		URL url = SegmentTestClassGroup.class.getResource(
			"SegmentTestClassGroup.class");

		File file = new File(url.toURI());

		File parentFile = file.getParentFile();

		Package pkg = SegmentTestClassGroup.class.getPackage();

		for (String fileName :
				new TreeSet<>(Arrays.asList(parentFile.list()))) {

			if (!fileName.endsWith(".class") || fileName.contains("$")) {
				continue;
			}

			Class<?> clazz = Class.forName(
				JenkinsResultsParserUtil.combine(
					pkg.getName(), ".",
					fileName.substring(0, fileName.length() - 6)));

			if (!SegmentTestClassGroup.class.isAssignableFrom(clazz)) {
				continue;
			}

			segmentTestClassGroupClasses.add(clazz);
		}

		return segmentTestClassGroupClasses;
	}

	private Set<String> _getWorkspaceNames(
		List<List<SegmentTestClassGroup>> segmentTestClassGroupsList) {

		List<BatchTestClassGroup> batchTestClassGroups = new ArrayList<>();

		for (List<SegmentTestClassGroup> segmentTestClassGroups :
				segmentTestClassGroupsList) {

			BatchTestClassGroup batchTestClassGroup = Mockito.mock(
				BatchTestClassGroup.class);

			Mockito.doReturn(
				segmentTestClassGroups
			).when(
				batchTestClassGroup
			).getSegmentTestClassGroups();

			batchTestClassGroups.add(batchTestClassGroup);
		}

		BaseJob baseJob = Mockito.mock(BaseJob.class);

		Mockito.doReturn(
			batchTestClassGroups
		).when(
			baseJob
		).getBatchTestClassGroups();

		Mockito.doCallRealMethod(
		).when(
			baseJob
		).getSegmentTestClassGroups();

		Mockito.doCallRealMethod(
		).when(
			baseJob
		).getWorkspaceNames();

		return baseJob.getWorkspaceNames();
	}

	private void _testGetWorkspaceNames(
		Set<String> expectedWorkspaceNames,
		List<List<String>> workspaceNamesList) {

		List<List<SegmentTestClassGroup>> segmentTestClassGroupsList =
			new ArrayList<>();

		for (List<String> workspaceNames : workspaceNamesList) {
			List<SegmentTestClassGroup> segmentTestClassGroups =
				new ArrayList<>();

			for (String workspaceName : workspaceNames) {
				PlaywrightSegmentTestClassGroup
					playwrightSegmentTestClassGroup = Mockito.mock(
						PlaywrightSegmentTestClassGroup.class);

				if (workspaceName != null) {
					Mockito.doReturn(
						workspaceName
					).when(
						playwrightSegmentTestClassGroup
					).getWorkspaceName();
				}

				segmentTestClassGroups.add(playwrightSegmentTestClassGroup);
			}

			segmentTestClassGroupsList.add(segmentTestClassGroups);
		}

		testEquals(
			expectedWorkspaceNames,
			_getWorkspaceNames(segmentTestClassGroupsList));
	}

}