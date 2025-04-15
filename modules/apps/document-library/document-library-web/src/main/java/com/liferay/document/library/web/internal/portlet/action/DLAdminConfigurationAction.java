/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY_ADMIN,
	service = ConfigurationAction.class
)
public class DLAdminConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/document_library_admin/configuration_browse.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		if (Validator.isNotNull(cmd)) {
			_validate(actionRequest);
		}

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _validate(ActionRequest actionRequest) {
		validateEmail(actionRequest, "emailFileEntryAdded");
		validateEmail(actionRequest, "emailFileEntryUpdated");
	}

	@Reference
	private ItemSelector _itemSelector;

}