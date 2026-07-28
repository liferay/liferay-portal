/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.configuration.persistence.listener;

import java.io.IOException;

import java.util.Dictionary;

/**
 * @author Drew Brokke
 */
public class ConfigurationModelListenerException extends IOException {

	public ConfigurationModelListenerException(
		Exception exception, Class<?> configurationClass,
		Class<?> configurationModelListenerClass,
		Dictionary<String, Object> properties) {

		super(
			String.format(
				"The listener %s was unable to save configuration %s: %s",
				configurationModelListenerClass.getName(),
				configurationClass.getName(), exception.getMessage()),
			exception);

		causeMessage = exception.getMessage();
		this.configurationClass = configurationClass;
		this.configurationModelListenerClass = configurationModelListenerClass;
		this.properties = properties;
	}

	public ConfigurationModelListenerException(
		String causeMessage, Class<?> configurationClass,
		Class<?> configurationModelListenerClass,
		Dictionary<String, Object> properties) {

		super(
			String.format(
				"The listener %s was unable to save configuration %s: %s",
				configurationModelListenerClass.getName(),
				configurationClass.getName(), causeMessage));

		this.causeMessage = causeMessage;
		this.configurationClass = configurationClass;
		this.configurationModelListenerClass = configurationModelListenerClass;
		this.properties = properties;
	}

	public final String causeMessage;
	public final Class<?> configurationClass;
	public final Class<?> configurationModelListenerClass;
	public final Dictionary<String, Object> properties;

}