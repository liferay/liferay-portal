/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util;

import com.liferay.portal.kernel.json.JSONArray;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Set;

/**
 * @author Akos Thurzo
 */
public interface LayoutsTree {

	public JSONArray getLayoutsJSONArray(
			Set<Long> expandedLayoutIds, long groupId,
			HttpServletRequest httpServletRequest, boolean includeActions,
			boolean incomplete, boolean loadMore, long parentLayoutId,
			boolean privateLayout, String treeId)
		throws Exception;

}