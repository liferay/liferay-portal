/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.security.PermissionsURLTag;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Javier Moral
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/view_display_page_permissions"
	},
	service = MVCRenderCommand.class
)
public class ViewDisplayPagePermissionsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			LayoutPageTemplateEntry layoutPageTemplateEntry =
				_getLayoutPageTemplateEntry(renderRequest, themeDisplay);

			if (layoutPageTemplateEntry == null) {
				return "/view.jsp";
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				PermissionsURLTag.doTag(
					StringPool.BLANK, LayoutPageTemplateEntry.class.getName(),
					layoutPageTemplateEntry.getName(), null,
					String.valueOf(
						layoutPageTemplateEntry.getLayoutPageTemplateEntryId()),
					LiferayWindowState.POP_UP.toString(), null,
					_portal.getHttpServletRequest(renderRequest)));

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private LayoutPageTemplateEntry _getLayoutPageTemplateEntry(
			RenderRequest renderRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		String externalReferenceCode = ParamUtil.getString(
			renderRequest, "displayPageTemplateExternalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return _layoutPageTemplateEntryService.
			fetchLayoutPageTemplateEntryByExternalReferenceCode(
				externalReferenceCode, themeDisplay.getScopeGroupId());
	}

	@Reference
	private LayoutPageTemplateEntryService _layoutPageTemplateEntryService;

	@Reference
	private Portal _portal;

}