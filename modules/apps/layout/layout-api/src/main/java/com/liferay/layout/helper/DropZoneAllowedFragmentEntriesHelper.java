/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.helper;

import com.liferay.layout.util.structure.DropZoneLayoutStructureItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;

import java.util.List;

/**
 * @author Lourdes Fernández Besada
 */
public interface DropZoneAllowedFragmentEntriesHelper {

	public List<String> getFragmentEntryKeys(
		long companyId, DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		long scopeGroupId);

	public boolean isAllowedFragmentEntryKey(
		long companyId, DropZoneLayoutStructureItem dropZoneLayoutStructureItem,
		String fragmentEntryKey, long scopeGroupId);

	public JSONObject normalizeItemConfigJSONObject(
			long companyId, JSONObject itemConfigJSONObject, long scopeGroupId)
		throws PortalException;

}