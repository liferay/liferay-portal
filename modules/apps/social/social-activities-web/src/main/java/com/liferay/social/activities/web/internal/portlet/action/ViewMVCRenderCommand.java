/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.activities.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.social.activities.constants.SocialActivitiesPortletKeys;
import com.liferay.social.activities.web.internal.constants.SocialActivitiesWebKeys;
import com.liferay.social.activities.web.internal.helper.SocialActivitiesQueryHelper;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SocialActivitiesPortletKeys.SOCIAL_ACTIVITIES,
		"mvc.command.name=/"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			SocialActivitiesWebKeys.SOCIAL_ACTIVITIES_QUERY_HELPER,
			_socialActivitiesQueryHelper);

		return "/view.jsp";
	}

	@Reference
	private SocialActivitiesQueryHelper _socialActivitiesQueryHelper;

}