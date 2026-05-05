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
public class LocalizedLDAPConfigurationModelListenerException
	extends ConfigurationModelListenerException {

	public LocalizedLDAPConfigurationModelListenerException(
		String causeMessage, String messageKey, Object[] messageArguments,
		Class<?> configurationClass, Class<?> listenerClass,
		Dictionary<String, Object> properties) {

		super(causeMessage, configurationClass, listenerClass, properties);

		_messageKey = messageKey;
		_messageArguments = messageArguments;
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