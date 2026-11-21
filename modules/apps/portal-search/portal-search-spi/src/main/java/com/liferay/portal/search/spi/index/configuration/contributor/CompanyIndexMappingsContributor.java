/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.spi.index.configuration.contributor;

/**
 * Implement this interface to add mappings to company indexes in the search
 * engine.
 *
 * @author Shuyang Zhou
 */
public interface CompanyIndexMappingsContributor {

	/**
	 * Returns the mappings to add to every company index.
	 *
	 * @return the mappings as a JSON source
	 */
	public String contributeMappings();

}