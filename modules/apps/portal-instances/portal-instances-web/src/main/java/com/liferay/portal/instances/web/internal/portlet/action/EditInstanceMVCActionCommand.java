/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instances.web.internal.portlet.action;

import com.liferay.portal.instances.web.internal.constants.PortalInstancesPortletKeys;
import com.liferay.portal.kernel.exception.CompanyMxException;
import com.liferay.portal.kernel.exception.CompanyVirtualHostException;
import com.liferay.portal.kernel.exception.CompanyWebIdException;
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.exception.RequiredCompanyException;
import com.liferay.portal.kernel.instance.PortalInstancePool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.CompanyService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortalInstancesPortletKeys.PORTAL_INSTANCES,
		"mvc.command.name=/portal_instances/edit_instance"
	},
	service = MVCActionCommand.class
)
public class EditInstanceMVCActionCommand extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		try {
			_updateInstance(actionRequest);

			sendRedirect(actionRequest, actionResponse);
		}
		catch (Exception exception) {
			String mvcPath = "/error.jsp";

			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			if (exception instanceof NoSuchCompanyException ||
				exception instanceof PrincipalException) {

				SessionErrors.add(actionRequest, exception.getClass());
			}
			else if (exception instanceof CompanyMxException ||
					 exception instanceof CompanyVirtualHostException ||
					 exception instanceof CompanyWebIdException) {

				long companyId = ParamUtil.getLong(actionRequest, "companyId");

				Company company = _companyLocalService.fetchCompanyById(
					companyId);

				if (company != null) {
					actionRequest.setAttribute(WebKeys.SEL_COMPANY, company);
				}

				SessionErrors.add(actionRequest, exception.getClass());

				SessionMessages.add(
					actionRequest,
					_portal.getPortletId(actionRequest) +
						SessionMessages.KEY_SUFFIX_HIDE_DEFAULT_ERROR_MESSAGE);

				mvcPath = "/edit_instance.jsp";
			}
			else if (exception instanceof RequiredCompanyException) {
				SessionErrors.add(actionRequest, exception.getClass());
			}
			else {
				_log.error(exception);

				throw exception;
			}

			actionResponse.setRenderParameter("mvcPath", mvcPath);
		}
	}

	private void _updateInstance(ActionRequest actionRequest) throws Exception {
		long companyId = ParamUtil.getLong(actionRequest, "companyId");

		String virtualHostname = ParamUtil.getString(
			actionRequest, "virtualHostname");
		String mx = ParamUtil.getString(actionRequest, "mx");
		int maxUsers = ParamUtil.getInteger(actionRequest, "maxUsers");

		boolean active = ParamUtil.getBoolean(actionRequest, "active");

		if (companyId == PortalInstancePool.getDefaultCompanyId()) {
			active = true;
		}

		_companyService.updateCompany(
			companyId, virtualHostname, mx, maxUsers, active);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		EditInstanceMVCActionCommand.class);

	@Reference
	private CompanyLocalService _companyLocalService;

	@Reference
	private CompanyService _companyService;

	@Reference
	private Portal _portal;

}