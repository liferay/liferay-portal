/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Marco Leo
 */
public class ObjectEntryExpirationDateException extends PortalException {

	public ObjectEntryExpirationDateException(
		String message, String messageKey) {

		super(message);

		_messageKey = messageKey;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	private final String _messageKey;

}