/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.util.BulkLayoutConverter;
import com.liferay.layout.util.template.LayoutConversionResult;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.servlet.MultiSessionMessages;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/add_layout_conversion_preview"
	},
	service = MVCActionCommand.class
)
public class AddLayoutConversionPreviewMVCActionCommand
	extends BaseMVCActionCommand {

	@Override
	protected void doProcessAction(
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		long plid = ParamUtil.getLong(actionRequest, "selPlid");

		try {
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(actionResponse);

			ThemeDisplay themeDisplay =
				(ThemeDisplay)actionRequest.getAttribute(WebKeys.THEME_DISPLAY);

			LayoutConversionResult layoutConversionResult =
				_bulkLayoutConverter.generatePreviewLayout(
					plid, themeDisplay.getLocale());

			String layoutFullURL = _portal.getLayoutFullURL(
				layoutConversionResult.getDraftLayout(), themeDisplay);

			String redirect = ParamUtil.getString(actionRequest, "redirect");

			layoutFullURL = HttpComponentsUtil.addParameters(
				layoutFullURL, "p_l_back_url", redirect, "p_l_back_url_title",
				_language.get(themeDisplay.getLocale(), "pages"), "p_l_mode",
				Constants.EDIT);

			MultiSessionMessages.add(
				actionRequest, "layoutConversionWarningMessages",
				layoutConversionResult.getConversionWarningMessages());

			httpServletResponse.sendRedirect(layoutFullURL);
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AddLayoutConversionPreviewMVCActionCommand.class);

	@Reference
	private BulkLayoutConverter _bulkLayoutConverter;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}