/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatus;
import com.liferay.portal.kernel.backgroundtask.BackgroundTaskStatusRegistryUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jaime Leon
 */
public class BackgroundTaskUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_backgroundTaskStatusRegistryUtilMockedStatic.when(
			() -> BackgroundTaskStatusRegistryUtil.getBackgroundTaskStatus(
				_BACKGROUND_TASK_ID)
		).thenReturn(
			_backgroundTaskStatus
		);
	}

	@After
	public void tearDown() {
		_backgroundTaskStatusRegistryUtilMockedStatic.close();
	}

	@Test
	public void testGetPercentage() {
		_setCounters(50, 100);

		Assert.assertEquals(
			Integer.valueOf(50),
			BackgroundTaskUtil.getPercentage(_BACKGROUND_TASK_ID));
	}

	@Test
	public void testGetPercentageWhenBackgroundTaskStatusIsMissing() {
		Assert.assertNull(
			BackgroundTaskUtil.getPercentage(RandomTestUtil.randomLong()));
	}

	@Test
	@TestInfo("LPD-109895")
	public void testGetPercentageWithPhases() {
		_backgroundTaskStatus.setAttribute("phasesTotal", 3);

		_setCounters(50, 100);

		Assert.assertEquals(
			Integer.valueOf(16),
			BackgroundTaskUtil.getPercentage(_BACKGROUND_TASK_ID));

		_backgroundTaskStatus.setAttribute("phaseIndex", 1);

		_setCounters(0, 0);

		Assert.assertEquals(
			Integer.valueOf(33),
			BackgroundTaskUtil.getPercentage(_BACKGROUND_TASK_ID));

		_backgroundTaskStatus.setAttribute("phaseIndex", 2);

		_setCounters(100, 100);

		Assert.assertEquals(
			Integer.valueOf(100),
			BackgroundTaskUtil.getPercentage(_BACKGROUND_TASK_ID));
	}

	@Test
	@TestInfo("LPD-109895")
	public void testGetPercentageWithPhasesBeyondPhasesTotal() {
		_backgroundTaskStatus.setAttribute("phaseIndex", 3);
		_backgroundTaskStatus.setAttribute("phasesTotal", 3);

		_setCounters(50, 100);

		Assert.assertEquals(
			Integer.valueOf(100),
			BackgroundTaskUtil.getPercentage(_BACKGROUND_TASK_ID));
	}

	@Test
	public void testGetPercentageWithoutCounters() {
		Assert.assertEquals(
			Integer.valueOf(0),
			BackgroundTaskUtil.getPercentage(_BACKGROUND_TASK_ID));
	}

	private void _setCounters(long current, long all) {
		_backgroundTaskStatus.setAttribute(
			"allModelAdditionCountersTotal", all);
		_backgroundTaskStatus.setAttribute(
			"currentModelAdditionCountersTotal", current);
	}

	private static final long _BACKGROUND_TASK_ID = RandomTestUtil.randomLong();

	private final BackgroundTaskStatus _backgroundTaskStatus =
		new BackgroundTaskStatus();
	private final MockedStatic<BackgroundTaskStatusRegistryUtil>
		_backgroundTaskStatusRegistryUtilMockedStatic = Mockito.mockStatic(
			BackgroundTaskStatusRegistryUtil.class);

}