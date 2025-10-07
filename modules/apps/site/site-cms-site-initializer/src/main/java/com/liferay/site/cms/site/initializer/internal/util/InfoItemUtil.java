/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.info.constants.InfoDisplayWebKeys;
import com.liferay.object.model.ObjectEntry;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Roberto Díaz
 */
public class InfoItemUtil {

	public static long getDepotEntryId(HttpServletRequest httpServletRequest) {
		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		DepotEntry depotEntry =
			object instanceof DepotEntry ? (DepotEntry)object : null;

		if (depotEntry != null) {
			return depotEntry.getDepotEntryId();
		}

		return 0;
	}

	public static long getGroupId(HttpServletRequest httpServletRequest) {
		Object object = httpServletRequest.getAttribute(
			InfoDisplayWebKeys.INFO_ITEM);

		DepotEntry depotEntry =
			object instanceof DepotEntry ? (DepotEntry)object : null;

		if (depotEntry != null) {
			return depotEntry.getGroupId();
		}

		ObjectEntry objectEntry =
			object instanceof ObjectEntry ? (ObjectEntry)object : null;

		if (objectEntry != null) {
			return objectEntry.getGroupId();
		}

		return 0;
	}

}