/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPOptionsSearchFacetDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.display.context.builder.CPOptionsSearchFacetDisplayContextBuilder;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Leo
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-cp-option-facets",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Option Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/option_facets/view.jsp",
		"jakarta.portlet.name=" + CPPortletKeys.CP_OPTION_FACETS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CPOptionFacetsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CPOptionsSearchFacetDisplayContext cpOptionsSearchFacetDisplayContext =
			_buildCPOptionsSearchFacetDisplayContext(renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			cpOptionsSearchFacetDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	private CPOptionsSearchFacetDisplayContext
		_buildCPOptionsSearchFacetDisplayContext(RenderRequest renderRequest) {

		CPOptionsSearchFacetDisplayContextBuilder
			cpOptionsSearchFacetDisplayContextBuilder =
				new CPOptionsSearchFacetDisplayContextBuilder(renderRequest);

		cpOptionsSearchFacetDisplayContextBuilder.configurationProvider(
			_configurationProvider);
		cpOptionsSearchFacetDisplayContextBuilder.cpOptionLocalService(
			_cpOptionLocalService);
		cpOptionsSearchFacetDisplayContextBuilder.groupLocalService(
			_groupLocalService);
		cpOptionsSearchFacetDisplayContextBuilder.portal(_portal);
		cpOptionsSearchFacetDisplayContextBuilder.portletSharedSearchRequest(
			_portletSharedSearchRequest);

		return cpOptionsSearchFacetDisplayContextBuilder.build();
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPOptionLocalService _cpOptionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

}