/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.item.selector.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Alessio Antonio Rendina
 */
public class OrganizationItemSelectorChecker extends EmptyOnClickRowChecker {

	public OrganizationItemSelectorChecker(
		RenderResponse renderResponse, long[] checkedOrganizationIds) {

		super(renderResponse);

		_checkedOrganizationIds = checkedOrganizationIds;
	}

	@Override
	public boolean isChecked(Object object) {
		Organization organization = (Organization)object;

		return ArrayUtil.contains(
			_checkedOrganizationIds, organization.getOrganizationId());
	}

	@Override
	public boolean isDisabled(Object object) {
		return isChecked(object);
	}

	private final long[] _checkedOrganizationIds;

}