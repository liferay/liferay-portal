/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.memberships.web.internal.search;

import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.service.OrganizationLocalServiceUtil;

import jakarta.portlet.RenderResponse;

/**
 * @author Charles May
 */
public class OrganizationSiteMembershipChecker extends EmptyOnClickRowChecker {

	public OrganizationSiteMembershipChecker(
		RenderResponse renderResponse, Group group) {

		super(renderResponse);

		_group = group;
	}

	@Override
	public boolean isChecked(Object object) {
		Organization organization = (Organization)object;

		try {
			if (OrganizationLocalServiceUtil.hasGroupOrganization(
					_group.getGroupId(), organization.getOrganizationId()) ||
				(_group.getOrganizationId() ==
					organization.getOrganizationId())) {

				return true;
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		return false;
	}

	@Override
	public boolean isDisabled(Object object) {
		Organization organization = (Organization)object;

		if (_group.getOrganizationId() == organization.getOrganizationId()) {
			return true;
		}

		return isChecked(object);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		OrganizationSiteMembershipChecker.class);

	private final Group _group;

}