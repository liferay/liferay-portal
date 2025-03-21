/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.sample.web.internal.portlet;

import com.liferay.frontend.taglib.sample.web.internal.constants.SamplePortletKeys;
import com.liferay.frontend.taglib.sample.web.internal.constants.SampleWebKeys;
import com.liferay.frontend.taglib.sample.web.internal.display.context.SampleDisplayContext;
import com.liferay.frontend.taglib.sample.web.internal.display.context.SearchIteratorDisplayContext;
import com.liferay.frontend.taglib.sample.web.internal.display.context.SearchPaginatorDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Miguel Arroyo
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-sample",
		"com.liferay.portlet.display-category=category.sample",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Taglib Sample",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SamplePortletKeys.SAMPLE_PORTLET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SamplePortlet extends MVCPortlet {

	@Override
	public void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		renderRequest.setAttribute(
			SampleWebKeys.SAMPLE_DISPLAY_CONTEXT,
			new SampleDisplayContext(renderRequest, renderResponse));
		renderRequest.setAttribute(
			SampleWebKeys.SEARCH_ITERATOR_DISPLAY_CONTEXT,
			new SearchIteratorDisplayContext(
				_portal, renderRequest, renderResponse));
		renderRequest.setAttribute(
			SampleWebKeys.SEARCH_PAGINATOR_DISPLAY_CONTEXT,
			new SearchPaginatorDisplayContext(
				_portal, renderRequest, renderResponse));

		super.doDispatch(renderRequest, renderResponse);
	}

	@Reference
	private Portal _portal;

}