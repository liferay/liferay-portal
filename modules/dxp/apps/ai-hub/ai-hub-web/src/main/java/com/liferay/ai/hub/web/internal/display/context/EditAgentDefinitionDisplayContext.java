/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.ai.hub.util.AccountEntryUtil;
import com.liferay.ai.hub.web.internal.util.DisplayContextUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.constants.WorkflowPortletKeys;

import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author Davyson Melo
 */
public class EditAgentDefinitionDisplayContext {

	public EditAgentDefinitionDisplayContext(
		GroupLocalService groupLocalService,
		HttpServletRequest httpServletRequest, Portal portal) {

		_groupLocalService = groupLocalService;
		_httpServletRequest = httpServletRequest;
		_portal = portal;

		_themeDisplay = (ThemeDisplay)httpServletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);
	}

	public Map<String, Object> getReactData() throws Exception {
		AccountEntry accountEntry = AccountEntryUtil.getUserAccountEntry(
			_themeDisplay.getUserId());

		Company company = _themeDisplay.getCompany();
		Group group = _groupLocalService.getGroup(
			_themeDisplay.getScopeGroupId());

		String aiHubURL = StringBundler.concat(
			company.getPortalURL(GroupConstants.DEFAULT_PARENT_GROUP_ID),
			"/web", group.getFriendlyURL());

		return HashMapBuilder.<String, Object>put(
			"accountEntryExternalReferenceCode",
			() -> {
				if (accountEntry == null) {
					return null;
				}

				return accountEntry.getExternalReferenceCode();
			}
		).put(
			"backURL",
			() -> {
				String backURL = PortalUtil.escapeRedirect(
					_httpServletRequest.getParameter("backURL"));

				if (Validator.isNotNull(backURL)) {
					return backURL;
				}

				return aiHubURL + "/agent-builder";
			}
		).put(
			"editAgentDefinitionURL", aiHubURL + "/agent"
		).put(
			"externalReferenceCode",
			_httpServletRequest.getParameter("externalReferenceCode")
		).put(
			"kaleoDesignerNamespace",
			_portal.getPortletNamespace(WorkflowPortletKeys.KALEO_DESIGNER)
		).put(
			"readOnly",
			DisplayContextUtil.isReadOnly(
				_themeDisplay.getCompanyId(),
				_httpServletRequest.getParameter("externalReferenceCode"),
				"L_AI_HUB_AGENT_DEFINITION")
		).put(
			"workflowDefinitionURL",
			() -> {
				String namespace = _portal.getPortletNamespace(
					WorkflowPortletKeys.KALEO_DESIGNER);

				String url = _addGroupExternalReferenceCodeParameter(
					accountEntry, namespace, aiHubURL + "/workflow-definition");

				return HttpComponentsUtil.addParameters(
					_addNameParameter(namespace, url), "p_p_id",
					WorkflowPortletKeys.KALEO_DESIGNER, "p_p_lifecycle", "0",
					"p_p_state", WindowState.MAXIMIZED.toString(), "p_p_mode",
					PortletMode.VIEW.toString(), namespace + "mvcPath",
					"/designer/edit_workflow_definition.jsp",
					namespace + "redirect",
					_portal.getPortalURL(_httpServletRequest) +
						_portal.getCurrentURL(_httpServletRequest),
					namespace + "clearSessionMessage", true,
					namespace + "scope", WorkflowDefinitionConstants.SCOPE_AI);
			}
		).build();
	}

	private String _addGroupExternalReferenceCodeParameter(
			AccountEntry accountEntry, String namespace, String url)
		throws PortalException {

		if (accountEntry == null) {
			return url;
		}

		Group group = _groupLocalService.getGroup(
			accountEntry.getAccountEntryGroupId());

		return HttpComponentsUtil.addParameter(
			url, namespace + "groupExternalReferenceCode",
			group.getExternalReferenceCode());
	}

	private String _addNameParameter(String namespace, String url) {
		String workflowDefinitionName = _httpServletRequest.getParameter(
			"workflowDefinitionName");

		if (workflowDefinitionName == null) {
			return url;
		}

		return HttpComponentsUtil.addParameter(
			url, namespace + "name", workflowDefinitionName);
	}

	private final GroupLocalService _groupLocalService;
	private final HttpServletRequest _httpServletRequest;
	private final Portal _portal;
	private final ThemeDisplay _themeDisplay;

}