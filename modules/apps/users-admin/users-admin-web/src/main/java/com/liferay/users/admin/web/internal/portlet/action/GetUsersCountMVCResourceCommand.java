/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gavin Wan
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.SERVICE_ACCOUNTS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/get_users_count"
	},
	service = MVCResourceCommand.class
)
public class GetUsersCountMVCResourceCommand implements MVCResourceCommand {

	@Override
	public boolean serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws PortletException {

		try {
			PrintWriter printWriter = resourceResponse.getWriter();

			printWriter.write(_getText(resourceRequest));

			return false;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private int _getOrganizationUsersCount(
			long companyId, long[] organizationIds, int status)
		throws Exception {

		int count = 0;

		for (long organizationId : organizationIds) {
			count += _userLocalService.searchCount(
				companyId, null, status,
				LinkedHashMapBuilder.<String, Object>put(
					"usersOrgs", organizationId
				).build());
		}

		return count;
	}

	private String _getText(ResourceRequest resourceRequest) throws Exception {
		long companyId = _portal.getCompanyId(resourceRequest);

		String className = ParamUtil.getString(resourceRequest, "className");
		long[] ids = StringUtil.split(
			ParamUtil.getString(resourceRequest, "ids"), 0L);
		int status = ParamUtil.getInteger(resourceRequest, "status");

		int count = 0;

		if (className.equals(Organization.class.getName())) {
			count = _getOrganizationUsersCount(companyId, ids, status);
		}
		else if (className.equals(UserGroup.class.getName())) {
			count = _getUserGroupUsersCount(companyId, ids, status);
		}

		return String.valueOf(count);
	}

	private int _getUserGroupUsersCount(
			long companyId, long[] userGroupIds, int status)
		throws Exception {

		int count = 0;

		for (long userGroupId : userGroupIds) {
			count += _userLocalService.searchCount(
				companyId, null, status,
				LinkedHashMapBuilder.<String, Object>put(
					"usersUserGroups", userGroupId
				).build());
		}

		return count;
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

}