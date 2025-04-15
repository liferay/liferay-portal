/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.content.search.web.internal.portlet;

import com.liferay.commerce.product.constants.CPPortletKeys;
import com.liferay.commerce.product.content.search.web.internal.display.context.CPSpecificationOptionFacetsDisplayContext;
import com.liferay.commerce.product.content.search.web.internal.display.context.builder.CPSpecificationOptionsFacetDisplayContextBuilder;
import com.liferay.commerce.product.service.CPSpecificationOptionLocalService;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
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
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-cp-specification-option-facets",
		"com.liferay.portlet.display-category=commerce",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Specification Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/specification_option_facets/view.jsp",
		"jakarta.portlet.name=" + CPPortletKeys.CP_SPECIFICATION_OPTION_FACETS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CPSpecificationOptionFacetsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		try {
			CPSpecificationOptionFacetsDisplayContext
				cpSpecificationOptionSearchFacetDisplayContext =
					_buildCPSpecificationOptionFacetsDisplayContext(
						renderRequest);

			renderRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				cpSpecificationOptionSearchFacetDisplayContext);
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private CPSpecificationOptionFacetsDisplayContext
			_buildCPSpecificationOptionFacetsDisplayContext(
				RenderRequest renderRequest)
		throws PortalException {

		CPSpecificationOptionsFacetDisplayContextBuilder
			cpSpecificationOptionsFacetDisplayBuilder =
				new CPSpecificationOptionsFacetDisplayContextBuilder();

		cpSpecificationOptionsFacetDisplayBuilder.configurationProvider(
			_configurationProvider);
		cpSpecificationOptionsFacetDisplayBuilder.
			cpSpecificationOptionLocalService(
				_cpSpecificationOptionLocalService);
		cpSpecificationOptionsFacetDisplayBuilder.groupLocalService(
			_groupLocalService);
		cpSpecificationOptionsFacetDisplayBuilder.portal(portal);
		cpSpecificationOptionsFacetDisplayBuilder.portletSharedSearchRequest(
			portletSharedSearchRequest);
		cpSpecificationOptionsFacetDisplayBuilder.renderRequest(renderRequest);

		return cpSpecificationOptionsFacetDisplayBuilder.build();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CPSpecificationOptionFacetsPortlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private CPSpecificationOptionLocalService
		_cpSpecificationOptionLocalService;

	@Reference
	private GroupLocalService _groupLocalService;

}