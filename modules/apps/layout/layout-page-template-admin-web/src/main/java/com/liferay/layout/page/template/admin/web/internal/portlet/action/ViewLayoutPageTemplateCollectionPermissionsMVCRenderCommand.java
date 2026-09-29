/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.admin.web.internal.portlet.action;

import com.liferay.layout.page.template.admin.constants.LayoutPageTemplateAdminPortletKeys;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionService;
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
 * @author Georgel Pop
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutPageTemplateAdminPortletKeys.LAYOUT_PAGE_TEMPLATES,
		"mvc.command.name=/layout_page_template_admin/view_layout_page_template_collection_permissions"
	},
	service = MVCRenderCommand.class
)
public class ViewLayoutPageTemplateCollectionPermissionsMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			LayoutPageTemplateCollection layoutPageTemplateCollection =
				_getLayoutPageTemplateCollection(renderRequest, themeDisplay);

			if (layoutPageTemplateCollection == null) {
				return "/view.jsp";
			}

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			httpServletResponse.sendRedirect(
				PermissionsURLTag.doTag(
					StringPool.BLANK,
					LayoutPageTemplateCollection.class.getName(),
					layoutPageTemplateCollection.getName(), null,
					String.valueOf(
						layoutPageTemplateCollection.
							getLayoutPageTemplateCollectionId()),
					LiferayWindowState.POP_UP.toString(), null,
					_portal.getHttpServletRequest(renderRequest)));

			return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private LayoutPageTemplateCollection _getLayoutPageTemplateCollection(
			RenderRequest renderRequest, ThemeDisplay themeDisplay)
		throws PortalException {

		String externalReferenceCode = ParamUtil.getString(
			renderRequest, "layoutPageTemplateCollectionExternalReferenceCode");

		if (Validator.isNull(externalReferenceCode)) {
			return null;
		}

		return _layoutPageTemplateCollectionService.
			fetchLayoutPageTemplateCollection(
				externalReferenceCode, themeDisplay.getScopeGroupId());
	}

	@Reference
	private LayoutPageTemplateCollectionService
		_layoutPageTemplateCollectionService;

	@Reference
	private Portal _portal;

}