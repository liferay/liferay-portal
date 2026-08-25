/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.util;

import com.liferay.object.constants.ObjectActionConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.service.ObjectActionLocalServiceWrapper;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Shuyang Zhou
 */
public class ObjectActionStatusUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testUpdateStatusAfterCommitLogsFailedWrite() throws Exception {

		// With no transaction, the callback runs inline, so a failed write
		// must be logged rather than thrown

		long objectActionId = RandomTestUtil.randomLong();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				ObjectActionStatusUtil.class.getName(), LoggerTestUtil.ERROR)) {

			ObjectActionStatusUtil.updateStatusAfterCommit(
				new ObjectActionLocalServiceWrapper() {

					@Override
					public ObjectAction updateStatus(
						long curObjectActionId, int status) {

						throw new SystemException();
					}

				},
				objectActionId, ObjectActionConstants.STATUS_FAILED);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to update the status of object action " +
					objectActionId,
				logEntry.getMessage());
		}
	}

}