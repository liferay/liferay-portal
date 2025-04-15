/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.on.demand.admin.web.internal.portlet.action;

import com.liferay.on.demand.admin.constants.OnDemandAdminPortletKeys;
import com.liferay.on.demand.admin.manager.OnDemandAdminManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OnDemandAdminPortletKeys.ON_DEMAND_ADMIN,
		"mvc.command.name=/on_demand_admin/request_admin_access"
	},
	service = MVCActionCommand.class
)
public class RequestAdminAccessMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long companyId = ParamUtil.getLong(actionRequest, "companyId");

		String loginURL = _onDemandAdminManager.getLoginURL(
			_companyLocalService.getCompany(companyId), actionRequest,
			_portal.getUserId(actionRequest));

		if (Validator.isNotNull(loginURL)) {
			sendRedirect(actionRequest, actionResponse, loginURL);
		}
	}

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private OnDemandAdminManager _onDemandAdminManager;

	@Reference
	private Portal _portal;

}