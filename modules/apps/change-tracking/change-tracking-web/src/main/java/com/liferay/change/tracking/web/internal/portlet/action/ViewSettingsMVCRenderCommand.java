/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.context.PublicationsConfigurationDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/view_settings"
	},
	service = MVCRenderCommand.class
)
public class ViewSettingsMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			PortletPermissionUtil.check(
				PermissionThreadLocal.getPermissionChecker(),
				CTPortletKeys.PUBLICATIONS, ActionKeys.CONFIGURATION);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		PublicationsConfigurationDisplayContext
			publicationsConfigurationDisplayContext =
				new PublicationsConfigurationDisplayContext(
					_ctSettingsConfigurationHelper,
					_portal.getHttpServletRequest(renderRequest),
					renderResponse);

		renderRequest.setAttribute(
			CTWebKeys.PUBLICATIONS_CONFIGURATION_DISPLAY_CONTEXT,
			publicationsConfigurationDisplayContext);

		return "/publications/view_settings.jsp";
	}

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Portal _portal;

}