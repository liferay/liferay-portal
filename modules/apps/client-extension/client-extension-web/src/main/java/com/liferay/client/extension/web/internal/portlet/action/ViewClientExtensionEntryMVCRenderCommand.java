/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.web.internal.portlet.action;

import com.liferay.client.extension.type.CET;
import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminPortletKeys;
import com.liferay.client.extension.web.internal.constants.ClientExtensionAdminWebKeys;
import com.liferay.client.extension.web.internal.display.context.ViewClientExtensionEntryDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ClientExtensionAdminPortletKeys.CLIENT_EXTENSION_ADMIN,
		"mvc.command.name=/client_extension_admin/view_client_extension_entry"
	},
	service = MVCRenderCommand.class
)
public class ViewClientExtensionEntryMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			CET cet = _cetManager.getCET(
				_portal.getCompanyId(renderRequest),
				ParamUtil.getString(renderRequest, "externalReferenceCode"));

			renderRequest.setAttribute(
				ClientExtensionAdminWebKeys.
					VIEW_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT,
				new ViewClientExtensionEntryDisplayContext(cet, renderRequest));

			return "/admin/view_client_extension_entry.jsp";
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private Portal _portal;

}