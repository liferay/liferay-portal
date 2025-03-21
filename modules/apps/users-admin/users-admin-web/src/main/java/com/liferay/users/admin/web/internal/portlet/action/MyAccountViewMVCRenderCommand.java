/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portlet.LiferayPortletUtil;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + UsersAdminPortletKeys.MY_ACCOUNT,
		"mvc.command.name=/", "mvc.command.name=/my_account/view"
	},
	service = MVCRenderCommand.class
)
public class MyAccountViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		if (renderRequest.getRemoteUser() == null) {
			return "/null.jsp";
		}

		try {
			User user = _portal.getUser(renderRequest);

			LiferayPortletRequest liferayPortletRequest =
				LiferayPortletUtil.getLiferayPortletRequest(renderRequest);

			DynamicServletRequest dynamicServletRequest =
				(DynamicServletRequest)
					liferayPortletRequest.getHttpServletRequest();

			dynamicServletRequest.setParameter(
				"p_u_i_d", String.valueOf(user.getUserId()));
			dynamicServletRequest.setParameter(
				"ctCollectionId",
				String.valueOf(CTConstants.CT_COLLECTION_ID_PRODUCTION));

			return "/edit_user.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}
	}

	@Reference
	private Portal _portal;

}