/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.configuration.persistence.listener;

import com.liferay.object.configuration.ObjectConfiguration;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.ResourceBundleUtil;

import java.util.Dictionary;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jhosseph Gonzalez
 */
@Component(
	property = "model.class.name=com.liferay.object.configuration.ObjectEntryScheduleConfiguration",
	service = ConfigurationModelListener.class
)
public class ObjectEntryScheduleConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		int checkInterval = GetterUtil.getInteger(
			properties.get("checkInterval"));

		if (checkInterval < 1) {
			throw new ConfigurationModelListenerException(
				ResourceBundleUtil.getString(
					ResourceBundleUtil.getBundle(
						"content.Language", LocaleUtil.getMostRelevantLocale(),
						getClass()),
					"the-object-entry-check-interval-field-cannot-be-less-" +
						"than-1"),
				ObjectConfiguration.class, getClass(), properties);
		}
	}

}