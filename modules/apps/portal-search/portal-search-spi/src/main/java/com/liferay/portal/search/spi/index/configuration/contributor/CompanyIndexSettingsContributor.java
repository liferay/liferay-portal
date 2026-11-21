/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.spi.index.configuration.contributor;

/**
 * Implement this interface to add settings to company indexes in the search
 * engine.
 *
 * @author Shuyang Zhou
 */
public interface CompanyIndexSettingsContributor {

	/**
	 * Returns the settings to add to the company index.
	 *
	 * @param  companyId the company ID of the index targeted by the settings
	 * @return the settings as a JSON source, or <code>null</code> to add none
	 */
	public String contributeSettings(long companyId);

}