/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.users.admin.constants.UsersAdminManagementToolbarKeys;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;
import com.liferay.users.admin.user.action.contributor.UserActionContributor;
import com.liferay.users.admin.web.internal.constants.UsersAdminWebKeys;
import com.liferay.users.admin.web.internal.users.admin.management.toolbar.FilterContributorRegistryUtil;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.SERVICE_ACCOUNTS,
		"mvc.command.name=/",
		"mvc.command.name=/users_admin/view_service_accounts"
	},
	service = MVCRenderCommand.class
)
public class ViewServiceAccountsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			UsersAdminWebKeys.MANAGEMENT_TOOLBAR_FILTER_CONTRIBUTORS,
			FilterContributorRegistryUtil.getFilterContributors(
				UsersAdminManagementToolbarKeys.VIEW_SERVICE_ACCOUNTS));
		renderRequest.setAttribute(
			UsersAdminWebKeys.USER_ACTION_CONTRIBUTORS,
			_serviceTrackerList.toArray(new UserActionContributor[0]));

		return "/view.jsp";
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, UserActionContributor.class);
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();
	}

	private ServiceTrackerList<UserActionContributor> _serviceTrackerList;

}