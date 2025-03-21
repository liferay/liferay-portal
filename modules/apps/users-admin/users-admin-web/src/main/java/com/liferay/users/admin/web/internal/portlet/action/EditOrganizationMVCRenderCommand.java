/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.portal.kernel.portlet.LiferayRenderRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	configurationPid = "com.liferay.users.admin.configuration.UserFileUploadsConfiguration",
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ORGANIZATIONS,
		"jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN,
		"mvc.command.name=/users_admin/edit_organization"
	},
	service = MVCRenderCommand.class
)
public class EditOrganizationMVCRenderCommand
	extends BaseOrganizationMVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		LiferayRenderRequest liferayRenderRequest =
			(LiferayRenderRequest)renderRequest;

		DynamicServletRequest dynamicServletRequest =
			(DynamicServletRequest)liferayRenderRequest.getHttpServletRequest();

		dynamicServletRequest.setParameter(
			"ctCollectionId",
			String.valueOf(CTConstants.CT_COLLECTION_ID_PRODUCTION));

		return super.render(renderRequest, renderResponse);
	}

	@Override
	protected String getPath() {
		return "/edit_organization.jsp";
	}

}