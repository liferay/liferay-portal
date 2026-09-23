/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/edit_display_page"
	},
	service = MVCRenderCommand.class
)
public class EditDisplayPageMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			Layout draftLayout = _getDraftLayout(renderRequest, themeDisplay);

			if (draftLayout == null) {
				return "/view.jsp";
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

			httpServletResponse.sendRedirect(
				HttpComponentsUtil.addParameters(
					_portal.getLayoutFullURL(draftLayout, themeDisplay),
					"p_l_back_url", _getBackURL(renderRequest, themeDisplay),
					"p_l_back_url_title",
					portletDisplay.getPortletDisplayName(), "p_l_mode",
					Constants.EDIT));

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}
		catch (IOException | PortalException exception) {
			throw new PortletException(exception);
		}
	}

	private String _getBackURL(
		RenderRequest renderRequest, ThemeDisplay themeDisplay) {

		String backURL = _portal.escapeRedirect(
			ParamUtil.getString(renderRequest, "redirect"));

		if (Validator.isNull(backURL)) {
			return themeDisplay.getURLCurrent();
		}

		return backURL;
	}

	private Layout _getDraftLayout(
			RenderRequest renderRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		String externalReferenceCode = ParamUtil.getString(
			renderRequest, "displayPageTemplateExternalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryService.
				fetchLayoutPageTemplateEntryByExternalReferenceCode(
					externalReferenceCode, themeDisplay.getScopeGroupId());

		if (layoutPageTemplateEntry == null) {
			return null;
		}

		return _layoutLocalService.fetchDraftLayout(
			layoutPageTemplateEntry.getPlid());
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}