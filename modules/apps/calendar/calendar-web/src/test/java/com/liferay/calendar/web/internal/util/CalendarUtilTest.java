/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.web.internal.util;

import com.liferay.calendar.workflow.constants.CalendarBookingWorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Brian Wulbern
 */
public class CalendarUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetStatusMessage() {
		Assert.assertEquals(
			"approved",
			CalendarUtil.getStatusMessage(WorkflowConstants.STATUS_APPROVED));
		Assert.assertEquals(
			"denied",
			CalendarUtil.getStatusMessage(WorkflowConstants.STATUS_DENIED));
		Assert.assertEquals(
			"pending",
			CalendarUtil.getStatusMessage(WorkflowConstants.STATUS_PENDING));
	}

	@Test
	public void testGetStatusMessageMaybe() {
		Assert.assertEquals(
			"maybe",
			CalendarUtil.getStatusMessage(
				CalendarBookingWorkflowConstants.STATUS_MAYBE));
	}

}