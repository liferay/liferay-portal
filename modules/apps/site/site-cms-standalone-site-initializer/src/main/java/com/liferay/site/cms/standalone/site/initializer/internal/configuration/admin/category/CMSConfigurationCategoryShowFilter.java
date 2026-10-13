/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.standalone.site.initializer.internal.configuration.admin.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;
import com.liferay.configuration.admin.category.ConfigurationCategoryShowFilter;
import com.liferay.portal.kernel.util.SetUtil;

import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 */
@Component(service = ConfigurationCategoryShowFilter.class)
public class CMSConfigurationCategoryShowFilter
	implements ConfigurationCategoryShowFilter {

	@Override
	public boolean isShow(ConfigurationCategory configurationCategory) {
		return _visibleCategoryKeys.contains(
			configurationCategory.getCategoryKey());
	}

	private static final Set<String> _visibleCategoryKeys = SetUtil.fromArray(
		"accessibility", "ai-creator", "ai-hub", "batch-engine", "comments",
		"community-tools", "default-permissions", "documents-and-media",
		"email", "feature-flags", "instance-configuration", "ldap",
		"localization", "login", "marketplace", "oauth2", "object", "scim-name",
		"security-tools", "sso", "translation", "user-authentication",
		"web-api");

}