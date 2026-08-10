/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.data.set;

import com.liferay.portal.kernel.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Daniel Sanz
 */
public interface SystemFDSEntry {

	public default String getAdditionalAPIURLParameters(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	public default int getDefaultItemsPerPage() {
		return PropsValues.SEARCH_CONTAINER_PAGE_DEFAULT_DELTA;
	}

	public String getDescription();

	public default boolean getHideManagementBarInEmptyState() {
		return false;
	}

	public default int[] getListOfItemsPerPage() {
		return PropsValues.SEARCH_CONTAINER_PAGE_DELTA_VALUES;
	}

	public String getName();

	public default String getPropsTransformer() {
		return null;
	}

	public String getRESTApplication();

	public String getRESTEndpoint();

	public String getRESTSchema();

	public default boolean getSearchAsYouType() {
		return false;
	}

	public default boolean getShowSearch() {
		return true;
	}

	public default boolean getSnapshotsEnabled() {
		return false;
	}

	public default String getSymbol() {
		return "dynamic-data-list";
	}

	public String getTitle();

}