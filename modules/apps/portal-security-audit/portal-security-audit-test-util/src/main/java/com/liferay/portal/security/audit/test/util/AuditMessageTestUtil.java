/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.test.util;

import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Lucas Miranda
 */
public class AuditMessageTestUtil {

	public static void assertLogEntries(
			AuditMessage auditMessage, LogCapture logCapture)
		throws Exception {

		List<LogEntry> logEntries = new ArrayList<>();

		for (LogEntry logEntry : logCapture.getLogEntries()) {
			String message = logEntry.getMessage();

			if (!message.contains(
					"\"className\":\"" + auditMessage.getClassName() + "\"")) {

				continue;
			}

			logEntries.add(logEntry);
		}

		Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

		LogEntry logEntry = logEntries.get(0);

		JSONAssert.assertEquals(
			String.valueOf(auditMessage.toJSONObject()), logEntry.getMessage(),
			JSONCompareMode.NON_EXTENSIBLE);
	}

}