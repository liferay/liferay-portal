/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.capabilities;

import com.liferay.portal.search.capabilities.SearchCapabilities;
import com.liferay.portal.search.engine.SearchEngineInformation;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(service = SearchCapabilities.class)
public class SearchCapabilitiesImpl implements SearchCapabilities {

	@Override
	public boolean isAnalyticsSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isCommerceSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isConcurrentModeSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isMCPToolSearchSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isResultRankingsSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isSearchExperiencesSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isSynonymsSupported() {
		return !_isSearchEngineSolr();
	}

	@Override
	public boolean isWorkflowMetricsSupported() {
		return !_isSearchEngineSolr();
	}

	private boolean _isSearchEngineSolr() {
		return Objects.equals(
			_searchEngineInformation.getVendorString(), "Solr");
	}

	@Reference
	private SearchEngineInformation _searchEngineInformation;

}