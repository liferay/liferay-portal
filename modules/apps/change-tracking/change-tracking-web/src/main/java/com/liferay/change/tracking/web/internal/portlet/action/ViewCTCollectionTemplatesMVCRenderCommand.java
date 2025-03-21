/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTActionKeys;
import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.service.CTCollectionTemplateService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.context.ViewTemplatesDisplayContext;
import com.liferay.change.tracking.web.internal.security.permission.resource.CTPermission;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author David Truong
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/view_ct_collection_templates"
	},
	service = MVCRenderCommand.class
)
public class ViewCTCollectionTemplatesMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		if (!CTPermission.contains(
				PermissionThreadLocal.getPermissionChecker(),
				CTActionKeys.ADD_TEMPLATE)) {

			return null;
		}

		ViewTemplatesDisplayContext viewTemplatesDisplayContext =
			new ViewTemplatesDisplayContext(
				_ctCollectionTemplateService, _ctSettingsConfigurationHelper,
				_portal.getHttpServletRequest(renderRequest), _language,
				renderRequest, renderResponse);

		renderRequest.setAttribute(
			CTWebKeys.VIEW_TEMPLATES_DISPLAY_CONTEXT,
			viewTemplatesDisplayContext);

		return "/publications/view_ct_collection_templates.jsp";
	}

	@Reference
	private CTCollectionTemplateService _ctCollectionTemplateService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}