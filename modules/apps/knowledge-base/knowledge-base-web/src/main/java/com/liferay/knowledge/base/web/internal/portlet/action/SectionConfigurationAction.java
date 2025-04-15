/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.ArrayUtil;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Shin
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_SECTION,
	service = ConfigurationAction.class
)
public class SectionConfigurationAction extends DefaultConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/section/configuration.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		_updateKBArticlesSections(actionRequest);

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	private void _updateKBArticlesSections(ActionRequest actionRequest) {
		String[] kbArticlesSections = actionRequest.getParameterValues(
			"kbArticlesSections");

		if (ArrayUtil.isEmpty(kbArticlesSections)) {
			SessionErrors.add(actionRequest, "kbArticlesSections");
		}

		if (SessionErrors.isEmpty(actionRequest)) {
			setPreference(
				actionRequest, "kbArticlesSections", kbArticlesSections);
		}
	}

}