/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.rest.internal.search.index;

/**
 * @author Petteri Karttunen
 */
public interface MCPToolIndexInvalidator {

	public void invalidate(long companyId);

	public void invalidate(long companyId, String restContextPath);

}