/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;
import com.liferay.site.cms.site.initializer.util.CMSUserUtil;

import java.util.List;
import java.util.Locale;

/**
 * @author José Abelenda
 */
public class ProjectSponsorSelectionFDSFilter extends BaseSelectionFDSFilter {

	public ProjectSponsorSelectionFDSFilter(CMSUsersProvider cmsUsersProvider) {
		_cmsUsersProvider = cmsUsersProvider;
	}

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.INTEGER;
	}

	@Override
	public String getId() {
		return "cmpProjectSponsorUserId";
	}

	@Override
	public String getLabel() {
		return "sponsor";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return TransformUtil.transform(
			CMSUserUtil.getUsers(_cmsUsersProvider),
			user -> new SelectionFDSFilterItem(
				user.getFullName(), user.getUserId()));
	}

	private final CMSUsersProvider _cmsUsersProvider;

}