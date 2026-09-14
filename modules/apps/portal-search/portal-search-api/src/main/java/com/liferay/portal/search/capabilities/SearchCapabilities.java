/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.capabilities;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Bryan Engler
 */
@ProviderType
public interface SearchCapabilities {

	public boolean isAnalyticsSupported();

	public boolean isCommerceSupported();

	public boolean isConcurrentModeSupported();

	public boolean isMCPToolSearchSupported();

	public boolean isResultRankingsSupported();

	public boolean isSearchExperiencesSupported();

	public boolean isSynonymsSupported();

	public boolean isWorkflowMetricsSupported();

}