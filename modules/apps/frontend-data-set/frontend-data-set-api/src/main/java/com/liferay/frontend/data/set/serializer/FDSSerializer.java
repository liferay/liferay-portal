/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set.serializer;

import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.model.FDSActionDropdownItem;
import com.liferay.frontend.data.set.model.FDSSortItem;
import com.liferay.frontend.taglib.clay.servlet.taglib.util.CreationMenu;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Daniel Sanz
 */
public interface FDSSerializer {

	public static final String[] FDS_TYPES = {
		FDSSerializer.TYPE_CUSTOM, FDSSerializer.TYPE_SYSTEM
	};

	public static final String TYPE_CUSTOM = "custom";

	public static final String TYPE_SYSTEM = "system";

	public boolean isAvailable(
		String fdsName, HttpServletRequest httpServletRequest);

	public default String serializeAdditionalAPIURLParameters(
		String fdsName, HttpServletRequest httpServletRequest) {

		return serializeAdditionalAPIURLParameters(
			fdsName, httpServletRequest, true, null);
	}

	public String serializeAdditionalAPIURLParameters(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject);

	public default String serializeAPIURL(
		String fdsName, HttpServletRequest httpServletRequest) {

		return serializeAPIURL(fdsName, httpServletRequest, true, null);
	}

	public String serializeAPIURL(
		String fdsName, HttpServletRequest httpServletRequest,
		boolean interpolate, JSONObject tokenResolutionsJSONObject);

	public List<FDSActionDropdownItem> serializeBulkActions(
		String fdsName, HttpServletRequest httpServletRequest);

	public CreationMenu serializeCreationMenu(
		String fdsName, HttpServletRequest httpServletRequest);

	public JSONArray serializeFilters(
		List<FDSFilter> fdsFilters, String fdsName,
		HttpServletRequest httpServletRequest);

	public JSONArray serializeFilters(
		String fdsName, HttpServletRequest httpServletRequest);

	public List<FDSActionDropdownItem> serializeItemsActions(
		String fdsName, HttpServletRequest httpServletRequest);

	public JSONObject serializePagination(
		String fdsName, HttpServletRequest httpServletRequest);

	public String serializePropsTransformer(
		String fdsName, HttpServletRequest httpServletRequest);

	public List<FDSSortItem> serializeSorts(
		String fdsName, HttpServletRequest httpServletRequest);

	public JSONArray serializeViews(
		String fdsName, HttpServletRequest httpServletRequest);

}