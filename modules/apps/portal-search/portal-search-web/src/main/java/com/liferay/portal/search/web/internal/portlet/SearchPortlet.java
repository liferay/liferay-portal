/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Release;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.OpenSearch;
import com.liferay.portal.kernel.servlet.ServletResponseUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.constants.SearchPortletKeys;
import com.liferay.portal.search.web.internal.display.context.SearchDisplayContext;
import com.liferay.portal.search.web.internal.display.context.SearchDisplayContextFactory;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.ResourceURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search",
		"com.liferay.portlet.display-category=category.tools",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Search",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + SearchPortletKeys.SEARCH,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SearchPortlet extends MVCPortlet {

	/**
	 * @deprecated As of Judson (7.1.x), replaced by search pages and widgets
	 */
	@Deprecated
	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		SearchDisplayContext searchDisplayContext =
			searchDisplayContextFactory.create(
				renderRequest, renderResponse, renderRequest.getPreferences());

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, searchDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	@Override
	public void serveResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException, PortletException {

		String resourceID = GetterUtil.getString(
			resourceRequest.getResourceID());

		if (resourceID.equals("getOpenSearchXML")) {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(resourceRequest);

			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(resourceResponse);

			try {
				ServletResponseUtil.sendFile(
					httpServletRequest, httpServletResponse, null,
					_getXML(resourceRequest, resourceResponse),
					ContentTypes.TEXT_XML_UTF8);
			}
			catch (Exception exception) {
				try {
					_portal.sendError(
						exception, httpServletRequest, httpServletResponse);
				}
				catch (ServletException servletException) {
					if (_log.isDebugEnabled()) {
						_log.debug(servletException);
					}
				}
			}
		}
		else {
			super.serveResource(resourceRequest, resourceResponse);
		}
	}

	@Reference
	protected SearchDisplayContextFactory searchDisplayContextFactory;

	private byte[] _getXML(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		ResourceURL openSearchResourceURL =
			resourceResponse.createResourceURL();

		openSearchResourceURL.setResourceID("getOpenSearchXML");

		long groupId = ParamUtil.getLong(resourceRequest, "groupId");

		ResourceURL openSearchDescriptionXMLURL =
			resourceResponse.createResourceURL();

		openSearchDescriptionXMLURL.setParameter(
			"mvcPath", "/open_search_description.jsp");
		openSearchDescriptionXMLURL.setParameter(
			"groupId", String.valueOf(groupId));

		OpenSearch openSearch = new PortalOpenSearchImpl(
			openSearchResourceURL.toString(),
			openSearchDescriptionXMLURL.toString());

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			resourceRequest);

		String xml = openSearch.search(
			httpServletRequest,
			openSearchResourceURL.toString() + StringPool.QUESTION +
				httpServletRequest.getQueryString());

		return xml.getBytes();
	}

	private static final Log _log = LogFactoryUtil.getLog(SearchPortlet.class);

	@Reference
	private Portal _portal;

	@Reference(
		target = "(&(release.bundle.symbolic.name=com.liferay.portal.search.web)(&(release.schema.version>=2.0.0)(!(release.schema.version>=3.0.0))))"
	)
	private Release _release;

}