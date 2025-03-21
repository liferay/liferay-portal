/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.search;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public interface OpenSearch {

	public String getClassName();

	public boolean isEnabled();

	public String search(
			HttpServletRequest httpServletRequest, long groupId, long userId,
			String keywords, int startPage, int itemsPerPage, String format)
		throws SearchException;

	public String search(
			HttpServletRequest httpServletRequest, long userId, String keywords,
			int startPage, int itemsPerPage, String format)
		throws SearchException;

	public String search(HttpServletRequest httpServletRequest, String url)
		throws SearchException;

}