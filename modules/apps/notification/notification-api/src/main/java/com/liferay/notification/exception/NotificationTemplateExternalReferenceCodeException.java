/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.exception;

import com.liferay.notification.constants.NotificationTemplateConstants;
import com.liferay.portal.kernel.exception.PortalException;

import java.util.Collections;
import java.util.List;

/**
 * @author Gabriel Albuquerque
 */
public class NotificationTemplateExternalReferenceCodeException
	extends PortalException {

	public List<Object> getArguments() {
		return _arguments;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	public static class MustNotStartWithPrefix
		extends NotificationTemplateExternalReferenceCodeException {

		public MustNotStartWithPrefix() {
			super(
				Collections.singletonList(
					NotificationTemplateConstants.
						EXTERNAL_REFERENCE_CODE_PREFIX_SYSTEM_NOTIFICATION_TEMPLATE),
				"The prefix L_ is reserved", "the-prefix-x-is-reserved");
		}

	}

	private NotificationTemplateExternalReferenceCodeException(
		List<Object> arguments, String message, String messageKey) {

		super(message);

		_arguments = arguments;
		_messageKey = messageKey;
	}

	private final List<Object> _arguments;
	private final String _messageKey;

}