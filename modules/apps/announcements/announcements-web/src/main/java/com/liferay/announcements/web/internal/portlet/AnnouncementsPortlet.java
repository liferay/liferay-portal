/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.portlet;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.web.internal.display.context.AnnouncementsDisplayContext;
import com.liferay.announcements.web.internal.display.context.helper.AnnouncementsRequestHelper;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.segments.SegmentsEntryRetriever;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.context.RequestContextMapper;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-announcements",
		"com.liferay.portlet.display-category=category.news",
		"com.liferay.portlet.header-portlet-css=/announcements/css/main.css",
		"com.liferay.portlet.icon=/announcements/icons/announcements.png",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Announcements",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.always-display-default-configuration-icons=true",
		"jakarta.portlet.init-param.mvc-command-names-default-views=/announcements/view",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class AnnouncementsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new AnnouncementsDisplayContext(
				new AnnouncementsRequestHelper(httpServletRequest),
				httpServletRequest, AnnouncementsPortletKeys.ANNOUNCEMENTS,
				renderRequest, renderResponse, _requestContextMapper,
				_segmentsEntryRetriever, _segmentsConfigurationProvider));

		super.render(renderRequest, renderResponse);
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.announcements.web)(&(release.schema.version>=2.0.0)(!(release.schema.version>=3.0.0))))"
	)
	private Release _release;

	@Reference
	private RequestContextMapper _requestContextMapper;

	@Reference
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Reference
	private SegmentsEntryRetriever _segmentsEntryRetriever;

}