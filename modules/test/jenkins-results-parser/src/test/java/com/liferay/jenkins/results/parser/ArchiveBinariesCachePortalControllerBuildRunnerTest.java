/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class ArchiveBinariesCachePortalControllerBuildRunnerTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_currentTimeMillis = JenkinsResultsParserUtil.getCurrentTimeMillis();
	}

	@Test
	public void testGetAllowedBuildStartTime() {
		int allowedBuildAgeInDays =
			Math.abs(RandomTestUtil.randomInt() % 30) + 1;

		_testGetAllowedBuildStartTime(
			"", _currentTimeMillis - (7 * _MILLISECONDS_PER_DAY));
		_testGetAllowedBuildStartTime("0", _currentTimeMillis);
		_testGetAllowedBuildStartTime(
			String.valueOf(allowedBuildAgeInDays),
			_currentTimeMillis -
				(allowedBuildAgeInDays * _MILLISECONDS_PER_DAY));
		_testGetAllowedBuildStartTime(
			null, _currentTimeMillis - (7 * _MILLISECONDS_PER_DAY));
	}

	@Test(expected = NumberFormatException.class)
	public void testGetAllowedBuildStartTimeNotInteger() {
		_testGetAllowedBuildStartTime(RandomTestUtil.randomString(), 0);
	}

	@Test
	public void testPreviousBuildHasCurrentSHA() {
		long allowedBuildStartTime = RandomTestUtil.randomInt();
		String portalBranchSHA = RandomTestUtil.randomString();

		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, false, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					"EXPIRE " + portalBranchSHA, allowedBuildStartTime + 1)));
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, false, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					"SKIPPED " + portalBranchSHA, allowedBuildStartTime + 1),
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					allowedBuildStartTime - 1)));
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, false, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					allowedBuildStartTime - 1)));
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, false, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					allowedBuildStartTime)));
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, false, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					RandomTestUtil.randomString(), allowedBuildStartTime + 1)));
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, false, portalBranchSHA,
			Collections.<JSONObject>emptyList());
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, true, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					allowedBuildStartTime + 1)));
		_testPreviousBuildHasCurrentSHA(
			allowedBuildStartTime, true, portalBranchSHA,
			Arrays.asList(
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					allowedBuildStartTime - 1),
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					allowedBuildStartTime + 1)));
	}

	@Test
	public void testPreviousBuildHasCurrentSHARetiredBuild() {
		long allowedBuildStartTime =
			_currentTimeMillis - (7 * _MILLISECONDS_PER_DAY);

		_testPreviousBuildHasCurrentSHARetiredBuild(allowedBuildStartTime);
		_testPreviousBuildHasCurrentSHARetiredBuild(allowedBuildStartTime + 1);
		_testPreviousBuildHasCurrentSHARetiredBuild(allowedBuildStartTime - 1);
	}

	private JSONObject _newBuildJSONObject(String description, long timestamp) {
		return new JSONObject(
		).put(
			"description", description
		).put(
			"timestamp", timestamp
		).put(
			"url", RandomTestUtil.randomString()
		);
	}

	private void _testGetAllowedBuildStartTime(
		String allowedBuildAgeInDays, long expectedAllowedBuildStartTime) {

		Map<String, String> environmentMap = new HashMap<>();

		if (allowedBuildAgeInDays != null) {
			environmentMap.put(
				"ALLOWED_BUILD_AGE_IN_DAYS", allowedBuildAgeInDays);
		}

		mockEnvironment(environmentMap);

		ArchiveBinariesCachePortalControllerBuildRunner<?>
			archiveBinariesCachePortalControllerBuildRunner = Mockito.mock(
				ArchiveBinariesCachePortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getAllowedBuildStartTime();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				_currentTimeMillis
			);

			testEquals(
				expectedAllowedBuildStartTime,
				archiveBinariesCachePortalControllerBuildRunner.
					getAllowedBuildStartTime());
		}
	}

	private void _testPreviousBuildHasCurrentSHA(
		long allowedBuildStartTime, boolean expected, String portalBranchSHA,
		List<JSONObject> previousBuildJSONObjects) {

		ArchiveBinariesCachePortalControllerBuildRunner<?>
			archiveBinariesCachePortalControllerBuildRunner = Mockito.mock(
				ArchiveBinariesCachePortalControllerBuildRunner.class);

		Mockito.doReturn(
			allowedBuildStartTime
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getAllowedBuildStartTime();

		Mockito.doReturn(
			portalBranchSHA
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getPortalBranchAbbreviatedSHA();

		Mockito.doReturn(
			previousBuildJSONObjects
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getPreviousBuildJSONObjects();

		Mockito.doCallRealMethod(
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).previousBuildHasCurrentSHA();

		testEquals(
			expected,
			archiveBinariesCachePortalControllerBuildRunner.
				previousBuildHasCurrentSHA());
	}

	private void _testPreviousBuildHasCurrentSHARetiredBuild(long timestamp) {
		String portalBranchSHA = RandomTestUtil.randomString();

		ArchiveBinariesCachePortalControllerBuildRunner<?>
			archiveBinariesCachePortalControllerBuildRunner = Mockito.mock(
				ArchiveBinariesCachePortalControllerBuildRunner.class);

		Mockito.doCallRealMethod(
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getAllowedBuildStartTime();

		Mockito.doReturn(
			portalBranchSHA
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getPortalBranchAbbreviatedSHA();

		Mockito.doReturn(
			Arrays.asList(
				_newBuildJSONObject(
					RandomTestUtil.randomString() + " " + portalBranchSHA,
					timestamp))
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).getPreviousBuildJSONObjects();

		Mockito.doCallRealMethod(
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).previousBuildHasCurrentSHA();

		Mockito.doCallRealMethod(
		).when(
			archiveBinariesCachePortalControllerBuildRunner
		).retirePreviousBuilds();

		try (MockedStatic<JenkinsResultsParserUtil>
				jenkinsResultsParserUtilMockedStatic = Mockito.mockStatic(
					JenkinsResultsParserUtil.class,
					Mockito.CALLS_REAL_METHODS)) {

			jenkinsResultsParserUtilMockedStatic.when(
				JenkinsResultsParserUtil::getCurrentTimeMillis
			).thenReturn(
				_currentTimeMillis
			);

			AtomicBoolean retired = new AtomicBoolean();

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.keepJenkinsBuild(
					Mockito.eq(false), Mockito.anyInt(), Mockito.anyString(),
					Mockito.anyString())
			).thenAnswer(
				invocation -> {
					retired.set(true);

					return null;
				}
			);

			jenkinsResultsParserUtilMockedStatic.when(
				() -> JenkinsResultsParserUtil.toJSONObject(Mockito.anyString())
			).thenReturn(
				new JSONObject(
				).put(
					"envMap",
					new JSONObject(
					).put(
						"BUILD_NUMBER",
						String.valueOf(RandomTestUtil.randomInt())
					).put(
						"HOSTNAME", RandomTestUtil.randomString()
					).put(
						"JOB_NAME", RandomTestUtil.randomString()
					)
				)
			);

			archiveBinariesCachePortalControllerBuildRunner.
				retirePreviousBuilds();

			Assert.assertNotEquals(
				retired.get(),
				archiveBinariesCachePortalControllerBuildRunner.
					previousBuildHasCurrentSHA());
		}
	}

	private static final long _MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;

	private long _currentTimeMillis;

}