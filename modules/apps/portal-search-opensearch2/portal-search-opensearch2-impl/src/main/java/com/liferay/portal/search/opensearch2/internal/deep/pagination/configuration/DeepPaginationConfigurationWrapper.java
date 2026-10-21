/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.deep.pagination.configuration;

import com.liferay.portal.search.opensearch2.configuration.DeepPaginationConfiguration;

/**
 * @author Petteri Karttunen
 */
public interface DeepPaginationConfigurationWrapper {

	public DeepPaginationConfiguration getDeepPaginationConfiguration(
		long companyId);

}