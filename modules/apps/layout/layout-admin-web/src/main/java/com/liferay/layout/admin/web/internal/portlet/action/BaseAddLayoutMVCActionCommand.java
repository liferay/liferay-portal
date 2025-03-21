/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCActionCommand;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletURL;
import jakarta.portlet.WindowState;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Pavel Savinov
 */
public abstract class BaseAddLayoutMVCActionCommand
	extends BaseMVCActionCommand {

	protected String getContentRedirectURL(
			ActionRequest actionRequest, Layout layout)
		throws PortalException {

		ThemeDisplay themeDisplay = (ThemeDisplay)actionRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		String layoutFullURL = portal.getLayoutFullURL(layout, themeDisplay);

		Layout draftLayout = layout.fetchDraftLayout();

		if (draftLayout != null) {
			layoutFullURL = portal.getLayoutFullURL(draftLayout, themeDisplay);
		}

		layoutFullURL = HttpComponentsUtil.setParameter(
			layoutFullURL, "p_l_mode", Constants.EDIT);

		String backURL = ParamUtil.getString(actionRequest, "backURL");

		if (Validator.isNotNull(backURL)) {
			layoutFullURL = HttpComponentsUtil.addParameters(
				layoutFullURL, "p_l_back_url", backURL, "p_l_back_url_title",
				language.get(themeDisplay.getLocale(), "pages"));
		}

		return layoutFullURL;
	}

	protected String getRedirectURL(
		ActionRequest actionRequest, ActionResponse actionResponse,
		Layout layout) {

		LiferayPortletResponse liferayPortletResponse =
			portal.getLiferayPortletResponse(actionResponse);

		return PortletURLBuilder.createRenderURL(
			liferayPortletResponse
		).setMVCRenderCommandName(
			"/layout_admin/edit_layout"
		).setRedirect(
			() -> {
				String backURL = ParamUtil.getString(actionRequest, "backURL");

				if (Validator.isNull(backURL)) {
					PortletURL redirectURL =
						liferayPortletResponse.createRenderURL();

					backURL = HttpComponentsUtil.setParameter(
						redirectURL.toString(), "p_p_state",
						WindowState.NORMAL.toString());
				}

				return backURL;
			}
		).setPortletResource(
			ParamUtil.getString(actionRequest, "portletResource")
		).setParameter(
			"backURLTitle",
			() -> {
				ThemeDisplay themeDisplay =
					(ThemeDisplay)actionRequest.getAttribute(
						WebKeys.THEME_DISPLAY);

				return language.get(themeDisplay.getLocale(), "pages");
			}
		).setParameter(
			"groupId", layout.getGroupId()
		).setParameter(
			"privateLayout", layout.isPrivateLayout()
		).setParameter(
			"selPlid", layout.getPlid()
		).buildString();
	}

	@Reference
	protected Language language;

	@Reference
	protected LayoutLocalService layoutLocalService;

	@Reference
	protected Portal portal;

}