/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.navigation.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.wiki.exception.NoSuchNodeException;
import com.liferay.wiki.navigation.web.internal.constants.WikiNavigationPortletKeys;
import com.liferay.wiki.service.WikiNodeService;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Sergio González
 */
@Component(
	property = "jakarta.portlet.name=" + WikiNavigationPortletKeys.TREE_MENU,
	service = ConfigurationAction.class
)
public class WikiNavigationTreeMenuConfigurationAction
	extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/tree_menu/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		validateNode(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	protected void validateNode(ActionRequest actionRequest) throws Exception {
		long selNodeId = GetterUtil.getLong(
			getParameter(actionRequest, "selNodeId"));

		try {
			_wikiNodeService.getNode(selNodeId);
		}
		catch (NoSuchNodeException noSuchNodeException) {
			SessionErrors.add(actionRequest, noSuchNodeException.getClass());
		}
	}

	@Reference
	private WikiNodeService _wikiNodeService;

}