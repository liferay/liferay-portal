/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.web.internal.portlet.action;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.portlet.JSONPortletResponseUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.redirect.model.RedirectEntry;
import com.liferay.redirect.service.RedirectEntryLocalService;
import com.liferay.redirect.web.internal.constants.RedirectPortletKeys;
import com.liferay.redirect.web.internal.util.RedirectUtil;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + RedirectPortletKeys.REDIRECT,
		"mvc.command.name=/redirect/get_redirect_entry_chain_cause"
	},
	service = MVCResourceCommand.class
)
public class GetRedirectEntryChainCauseMVCResourceCommand
	extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		JSONPortletResponseUtil.writeJSON(
			resourceRequest, resourceResponse,
			JSONUtil.put(
				"redirectEntryChainCause",
				_getRedirectEntryChainCause(resourceRequest)));
	}

	private String _getRedirectEntryChainCause(
		ResourceRequest resourceRequest) {

		String sourceURL = ParamUtil.getString(resourceRequest, "sourceURL");

		ThemeDisplay themeDisplay = (ThemeDisplay)resourceRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (ListUtil.isNotEmpty(
				_redirectEntryLocalService.getRedirectEntries(
					themeDisplay.getScopeGroupId(),
					RedirectUtil.getGroupBaseURL(themeDisplay) +
						StringPool.FORWARD_SLASH + sourceURL))) {

			return "sourceURL";
		}

		String destinationURL = ParamUtil.getString(
			resourceRequest, "destinationURL");

		RedirectEntry redirectEntry =
			_redirectEntryLocalService.fetchRedirectEntry(
				themeDisplay.getScopeGroupId(),
				StringUtil.removeSubstring(
					destinationURL,
					RedirectUtil.getGroupBaseURL(themeDisplay) +
						StringPool.SLASH));

		if (redirectEntry != null) {
			return "destinationURL";
		}

		return null;
	}

	@Reference
	private RedirectEntryLocalService _redirectEntryLocalService;

}