/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.frontend.data.set.filter.BaseSelectionFDSFilter;
import com.liferay.frontend.data.set.filter.FDSFilter;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.site.cms.site.initializer.internal.constants.CMSSiteInitializerFDSNames;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;
import com.liferay.site.cms.site.initializer.util.CMSUserUtil;

import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Veronica Gonzalez
 */
@Component(
	property = {
		"frontend.data.set.name=" + CMSSiteInitializerFDSNames.TASKS_SECTION,
		"service.ranking:Integer=93"
	},
	service = FDSFilter.class
)
public class TaskAssigneeSelectionFDSFilter extends BaseSelectionFDSFilter {

	@Override
	public String getEntityFieldType() {
		return FDSEntityFieldTypes.COLLECTION_INTEGER;
	}

	@Override
	public String getId() {
		return "assigneeClassPKs";
	}

	@Override
	public String getLabel() {
		return "assignee";
	}

	@Override
	public List<SelectionFDSFilterItem> getSelectionFDSFilterItems(
		Locale locale) {

		return TransformUtil.transform(
			CMSUserUtil.getUsers(_cmsUsersProvider),
			user -> new SelectionFDSFilterItem(
				user.getFullName(), user.getUserId()));
	}

	@Override
	public boolean isAutocompleteEnabled() {
		return true;
	}

	@Reference
	private CMSUsersProvider _cmsUsersProvider;

}