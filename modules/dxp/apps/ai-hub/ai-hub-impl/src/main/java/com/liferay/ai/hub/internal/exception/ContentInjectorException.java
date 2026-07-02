/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.internal.exception;

/**
 * @author Feliphe Marinho
 */
public class ContentInjectorException extends RuntimeException {

	public ContentInjectorException(String message) {
		super(message);
	}

	public ContentInjectorException(String message, String messageKey) {
		super(message);

		_messageKey = messageKey;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	private String _messageKey;

}