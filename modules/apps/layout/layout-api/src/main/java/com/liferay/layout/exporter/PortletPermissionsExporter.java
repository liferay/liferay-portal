/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.exporter;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public interface PortletPermissionsExporter {

	public Map<String, String[]> getPortletPermissions(
		long plid, String portletId);

}