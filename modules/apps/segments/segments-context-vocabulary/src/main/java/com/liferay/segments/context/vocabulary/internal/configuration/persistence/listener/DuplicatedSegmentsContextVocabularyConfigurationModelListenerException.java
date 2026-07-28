/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.context.vocabulary.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;

import java.util.Dictionary;

/**
 * @author Cristina González
 */
public class
	DuplicatedSegmentsContextVocabularyConfigurationModelListenerException
		extends ConfigurationModelListenerException {

	public DuplicatedSegmentsContextVocabularyConfigurationModelListenerException(
		String causeMessage, Class<?> configurationClass,
		Class<?> configurationModelListenerClass,
		Dictionary<String, Object> properties) {

		super(
			causeMessage, configurationClass, configurationModelListenerClass,
			properties);
	}

}