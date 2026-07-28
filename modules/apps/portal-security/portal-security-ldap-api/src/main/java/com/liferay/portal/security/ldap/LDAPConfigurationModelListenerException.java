/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;

import java.util.Dictionary;

/**
 * @author Jorge García Jiménez
 */
public class LDAPConfigurationModelListenerException
	extends ConfigurationModelListenerException {

	public LDAPConfigurationModelListenerException(
		String causeMessage, Class<?> configurationClass,
		Class<?> configurationModelListenerClass, Object[] messageArguments,
		String messageKey, Dictionary<String, Object> properties) {

		super(
			causeMessage, configurationClass, configurationModelListenerClass,
			properties);

		_messageArguments = messageArguments;
		_messageKey = messageKey;
	}

	public Object[] getMessageArguments() {
		return _messageArguments;
	}

	public String getMessageKey() {
		return _messageKey;
	}

	private final Object[] _messageArguments;
	private final String _messageKey;

}