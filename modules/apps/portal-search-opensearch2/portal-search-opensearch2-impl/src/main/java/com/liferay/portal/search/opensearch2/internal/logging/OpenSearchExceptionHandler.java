/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.logging;

import com.liferay.portal.kernel.log.Log;

/**
 * @author Adam Brandizzi
 */
public class OpenSearchExceptionHandler {

	public OpenSearchExceptionHandler(Log log, boolean logExceptionsOnly) {
		_log = log;
		_logExceptionsOnly = logExceptionsOnly;
	}

	public <T extends Throwable> void handleDeleteDocumentException(T t)
		throws T {

		if (_isIndexNotFound(t)) {
			if (_log.isInfoEnabled()) {
				_log.info(t, t);
			}
		}
		else {
			logOrThrow(t);
		}
	}

	public <T extends Throwable> void logOrThrow(T t) throws T {
		if (_logExceptionsOnly) {
			_log.error(t, t);
		}
		else {
			throw t;
		}
	}

	protected static final String INDEX_NOT_FOUND_EXCEPTION_MESSAGE =
		"index_not_found_exception";

	private boolean _isIndexNotFound(Throwable throwable) {
		String message = throwable.getMessage();

		return message.contains(INDEX_NOT_FOUND_EXCEPTION_MESSAGE);
	}

	private final Log _log;
	private final boolean _logExceptionsOnly;

}