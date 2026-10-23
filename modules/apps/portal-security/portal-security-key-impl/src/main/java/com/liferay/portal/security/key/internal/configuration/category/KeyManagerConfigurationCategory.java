/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.key.internal.configuration.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 * @author Christopher Kian
 */
@Component(service = ConfigurationCategory.class)
public class KeyManagerConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getBundleSymbolicName() {
		return "com.liferay.portal.security.key.impl";
	}

	@Override
	public String getCategoryIcon() {
		return "key";
	}

	@Override
	public String getCategoryKey() {
		return "key-manager";
	}

	@Override
	public String getCategorySection() {
		return "security";
	}

}