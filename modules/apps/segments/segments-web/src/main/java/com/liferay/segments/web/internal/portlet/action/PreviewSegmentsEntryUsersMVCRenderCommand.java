/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.portlet.action;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.constants.SegmentsPortletKeys;
import com.liferay.segments.odata.retriever.ODataRetriever;
import com.liferay.segments.provider.SegmentsEntryProviderRegistry;
import com.liferay.segments.service.SegmentsEntryService;
import com.liferay.segments.web.internal.constants.SegmentsWebKeys;
import com.liferay.segments.web.internal.display.context.PreviewSegmentsEntryUsersDisplayContext;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletSession;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + SegmentsPortletKeys.SEGMENTS,
		"mvc.command.name=/segments/preview_segments_entry_users"
	},
	service = MVCRenderCommand.class
)
public class PreviewSegmentsEntryUsersMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		if (ParamUtil.getBoolean(httpServletRequest, "clearSessionCriteria")) {
			PortletSession portletSession = renderRequest.getPortletSession();

			portletSession.removeAttribute(
				SegmentsWebKeys.PREVIEW_SEGMENTS_ENTRY_CRITERIA);
		}

		renderRequest.setAttribute(
			PreviewSegmentsEntryUsersDisplayContext.class.getName(),
			new PreviewSegmentsEntryUsersDisplayContext(
				httpServletRequest, renderRequest, renderResponse,
				_segmentsEntryProviderRegistry, _segmentsEntryService,
				_userODataRetriever, _userLocalService));

		return "/preview_segments_entry_users.jsp";
	}

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsEntryProviderRegistry _segmentsEntryProviderRegistry;

	@Reference
	private SegmentsEntryService _segmentsEntryService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference(
		target = "(model.class.name=com.liferay.portal.kernel.model.User)"
	)
	private ODataRetriever<User> _userODataRetriever;

}