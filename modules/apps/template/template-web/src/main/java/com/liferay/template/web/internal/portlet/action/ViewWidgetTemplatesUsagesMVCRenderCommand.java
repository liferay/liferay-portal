/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.template.web.internal.portlet.action;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.template.constants.TemplatePortletKeys;
import com.liferay.template.web.internal.display.context.WidgetTemplatesTemplateViewUsagesDisplayContext;
import com.liferay.template.web.internal.security.permissions.resource.DDMTemplatePermission;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + TemplatePortletKeys.TEMPLATE,
		"mvc.command.name=/template/view_widget_templates_usages"
	},
	service = MVCRenderCommand.class
)
public class ViewWidgetTemplatesUsagesMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		long ddmTemplateId = ParamUtil.getLong(renderRequest, "ddmTemplateId");

		if (ddmTemplateId > 0) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			try {
				DDMTemplatePermission.check(
					themeDisplay.getPermissionChecker(), ddmTemplateId,
					ActionKeys.VIEW);
			}
			catch (PortalException portalException) {
				throw new PortletException(portalException);
			}
		}

		renderRequest.setAttribute(
			WidgetTemplatesTemplateViewUsagesDisplayContext.class.getName(),
			new WidgetTemplatesTemplateViewUsagesDisplayContext(
				_portal.getHttpServletRequest(renderRequest), renderRequest,
				renderResponse));

		return "/view_widget_templates_usages.jsp";
	}

	@Reference
	private Portal _portal;

}