/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.license.test;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.log.LogEntry;

import java.io.PrintWriter;

import java.util.List;

/**
 * @author Tina Tian
 */
public class LogEntriesException extends Exception {

	public LogEntriesException(List<LogEntry> logEntries, String payload) {
		_logEntries = logEntries;
		_payload = payload;
	}

	public List<LogEntry> getLogEntries() {
		return _logEntries;
	}

	public String getPayload() {
		return _payload;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("Log Entries: ");
		sb.append(_logEntries.size());
		sb.append(StringPool.NEW_LINE);

		for (int i = 0; i < _logEntries.size(); i++) {
			sb.append("Log Entry ");
			sb.append(i + 1);
			sb.append(":");

			LogEntry logEntry = _logEntries.get(i);

			sb.append(logEntry.getMessage());

			Throwable throwable = logEntry.getThrowable();

			if (throwable == null) {
				continue;
			}

			UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

			try (PrintWriter printWriter = new PrintWriter(
					unsyncStringWriter)) {

				throwable.printStackTrace(printWriter);
			}

			sb.append(unsyncStringWriter.toString());
		}

		return sb.toString();
	}

	private final List<LogEntry> _logEntries;
	private final String _payload;

}