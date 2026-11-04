/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.configuration.admin.category;

import com.liferay.configuration.admin.category.ConfigurationCategory;

import org.osgi.service.component.annotations.Component;

/**
 * @author Beni Herrero
 */
@Component(service = ConfigurationCategory.class)
public class MCPServerConfigurationCategory implements ConfigurationCategory {

	@Override
	public String getCategoryIcon() {
		return "mcp";
	}

	@Override
	public String getCategoryKey() {
		return "mcp-server";
	}

	@Override
	public String getCategorySection() {
		return "platform";
	}

}