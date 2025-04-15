/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.portlet.action;

import com.liferay.knowledge.base.constants.KBPortletKeys;
import com.liferay.portal.kernel.portlet.BaseJSPSettingsConfigurationAction;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletConfig;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Shin
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "jakarta.portlet.name=" + KBPortletKeys.KNOWLEDGE_BASE_ADMIN,
	service = ConfigurationAction.class
)
public class AdminConfigurationAction
	extends BaseJSPSettingsConfigurationAction {

	@Override
	public String getJspPath(HttpServletRequest httpServletRequest) {
		return "/admin/configuration_browse.jsp";
	}

	@Override
	public void processAction(
			PortletConfig portletConfig, ActionRequest actionRequest,
			ActionResponse actionResponse)
		throws Exception {

		validateEmail(actionRequest, "emailKBArticleAdded");
		validateEmail(actionRequest, "emailKBArticleExpired");
		validateEmail(actionRequest, "emailKBArticleReview");
		validateEmail(actionRequest, "emailKBArticleSuggestionInProgress");
		validateEmail(actionRequest, "emailKBArticleSuggestionReceived");
		validateEmail(actionRequest, "emailKBArticleSuggestionResolved");
		validateEmail(actionRequest, "emailKBArticleUpdated");

		super.processAction(portletConfig, actionRequest, actionResponse);
	}

	@Override
	protected void validateEmail(
		ActionRequest actionRequest, String emailParam) {

		boolean emailEnabled = GetterUtil.getBoolean(
			getParameter(actionRequest, emailParam + "Enabled"));

		if (!emailEnabled) {
			return;
		}

		String emailSubject = getParameter(
			actionRequest, emailParam + "Subject");
		String emailBody = getParameter(actionRequest, emailParam + "Body");

		if (Validator.isNull(emailSubject)) {
			SessionErrors.add(actionRequest, emailParam + "Subject");
		}
		else if (Validator.isNull(emailBody)) {
			SessionErrors.add(actionRequest, emailParam + "Body");
		}
	}

}