/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;

/**
 * @author Jose Luis Navarro
 */
public class MCPServerWebDisplayContext {

	public Map<String, Object> getDataMaskListProps() {
		return HashMapBuilder.<String, Object>put(
			"activeTab", "data-masks"
		).build();
	}

}