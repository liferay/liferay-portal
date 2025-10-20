/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.search.spi.model.query.contributor;

import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.search.spi.model.query.contributor.HighlightFieldNamesQueryConfigContributor;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bryan Engler
 */
@Component(
	property = "indexer.class.name=com.liferay.portal.kernel.model.User",
	service = HighlightFieldNamesQueryConfigContributor.class
)
public class UserHighlightFieldNamesQueryConfigContributor
	implements HighlightFieldNamesQueryConfigContributor {

	@Override
	public String[] getHighlightFieldNames(SearchContext searchContext) {
		return new String[] {"fullName"};
	}

}