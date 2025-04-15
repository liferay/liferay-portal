/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.portlet.action;

import com.liferay.document.library.constants.DLPortletKeys;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContext;
import com.liferay.document.library.web.internal.display.context.DLAdminDisplayContextProvider;
import com.liferay.item.selector.ItemSelector;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + DLPortletKeys.DOCUMENT_LIBRARY,
	service = ConfigurationAction.class
)
public class DLConfigurationAction
	extends BaseValidateRootFolderConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/document_library/configuration.jsp";
	}

	@Override
	public void include(
			PortletConfig portletConfig, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		httpServletRequest.setAttribute(
			DLAdminDisplayContext.class.getName(),
			_dlAdminDisplayContextProvider.getDLAdminDisplayContext(
				httpServletRequest, httpServletResponse));
		httpServletRequest.setAttribute(
			ItemSelector.class.getName(), _itemSelector);

		super.include(portletConfig, httpServletRequest, httpServletResponse);
	}

	@Override
	protected void validate(ActionRequest actionRequest)
		throws PortalException {

		_validateDisplayStyleViews(actionRequest);

		super.validate(actionRequest);
	}

	private void _validateDisplayStyleViews(ActionRequest actionRequest) {
		String displayViews = GetterUtil.getString(
			getParameter(actionRequest, "displayViews"));

		if (Validator.isNull(displayViews)) {
			SessionErrors.add(actionRequest, "displayViewsInvalid");
		}
	}

	@Reference
	private DLAdminDisplayContextProvider _dlAdminDisplayContextProvider;

	@Reference
	private ItemSelector _itemSelector;

}