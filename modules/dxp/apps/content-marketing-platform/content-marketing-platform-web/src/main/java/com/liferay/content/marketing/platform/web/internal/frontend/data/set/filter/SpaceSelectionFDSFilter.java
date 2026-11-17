/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;

/**
 * @author Veronica Gonzalez
 */
public class SpaceSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getAPIURL() {
		return "/o/headless-asset-library/v1.0/asset-libraries?filter=type " +
			"eq 'Space'";
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "scopeGroupId";
	}

	@Override
	public String getItemKey() {
		return "siteId";
	}

	@Override
	public String getItemLabel() {
		return "assetLibraryKey";
	}

	@Override
	public String getLabel() {
		return "space";
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

}