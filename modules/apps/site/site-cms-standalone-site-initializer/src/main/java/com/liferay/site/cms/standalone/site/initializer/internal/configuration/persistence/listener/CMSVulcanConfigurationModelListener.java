/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = "model.class.name=com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration",
	service = ConfigurationModelListener.class
)
public class CMSVulcanConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> properties)
		throws ConfigurationModelListenerException {

		if (!Objects.equals(properties.get("path"), "/headless-delivery")) {
			return;
		}

		if (GetterUtil.getBoolean(properties.get("graphQLEnabled"), true) ||
			GetterUtil.getBoolean(properties.get("restEnabled"), true)) {

			throw new ConfigurationModelListenerException(
				"Remote access to the headless delivery API cannot be " +
					"enabled in the standalone CMS",
				getClass(), getClass(), properties);
		}
	}

}