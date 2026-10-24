/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.engine.client.exception;

/**
 * @author Leslie Wong
 */
public class HeadlessRequestRejectedException
	extends FaroEngineClientException {

	public HeadlessRequestRejectedException(
		Long limit, String message, String messageKey, Long retryAfterSeconds) {

		super(message);

		_limit = limit;
		_messageKey = messageKey;
		_retryAfterSeconds = retryAfterSeconds;
	}

	public Long getLimit() {
		return _limit;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public Long getRetryAfterSeconds() {
		return _retryAfterSeconds;
	}

	private final Long _limit;
	private final String _messageKey;
	private final Long _retryAfterSeconds;

}