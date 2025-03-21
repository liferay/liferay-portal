/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.facet.portlet;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.facet.Facet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.custom.facet.constants.CustomFacetPortletKeys;
import com.liferay.portal.search.web.internal.custom.facet.display.context.CustomFacetDisplayContext;
import com.liferay.portal.search.web.internal.custom.facet.display.context.builder.CustomFacetDisplayContextBuilder;
import com.liferay.portal.search.web.internal.custom.facet.util.CustomFacetUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Wade Cao
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-custom-facet",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Custom Facet",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/custom/facet/view.jsp",
		"jakarta.portlet.name=" + CustomFacetPortletKeys.CUSTOM_FACET,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CustomFacetPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		CustomFacetDisplayContext customFacetDisplayContext =
			_createCustomFacetDisplayContext(
				portletSharedSearchResponse, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, customFacetDisplayContext);

		if (customFacetDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private CustomFacetDisplayContext _createCustomFacetDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		try {
			CustomFacetPortletPreferences customFacetPortletPreferences =
				new CustomFacetPortletPreferencesImpl(
					portletSharedSearchResponse.getPortletPreferences(
						renderRequest));

			CustomFacetDisplayContextBuilder customFacetDisplayContextBuilder =
				new CustomFacetDisplayContextBuilder(
					customFacetPortletPreferences.getAggregationType(),
					CustomFacetUtil.getHttpServletRequest(renderRequest));

			String parameterName = CustomFacetUtil.getParameterName(
				customFacetPortletPreferences);

			SearchResponse searchResponse =
				portletSharedSearchResponse.getSearchResponse();

			return customFacetDisplayContextBuilder.currentURL(
				_portal.getCurrentURL(renderRequest)
			).customDisplayCaption(
				customFacetPortletPreferences.getCustomHeading()
			).facet(
				_getFacet(
					portletSharedSearchResponse, customFacetPortletPreferences,
					renderRequest)
			).aggregationField(
				customFacetPortletPreferences.getAggregationField()
			).frequenciesVisible(
				customFacetPortletPreferences.isFrequenciesVisible()
			).frequencyThreshold(
				customFacetPortletPreferences.getFrequencyThreshold()
			).fromParameterValue(
				portletSharedSearchResponse.getParameter(
					parameterName + "From", renderRequest)
			).maxTerms(
				customFacetPortletPreferences.getMaxTerms()
			).order(
				customFacetPortletPreferences.getOrder()
			).paginationStartParameterName(
				_getPaginationStartParameterName(portletSharedSearchResponse)
			).parameterName(
				parameterName
			).parameterValues(
				portletSharedSearchResponse.getParameterValues(
					parameterName, renderRequest)
			).showInputRange(
				customFacetPortletPreferences.isShowInputRange()
			).toParameterValue(
				portletSharedSearchResponse.getParameter(
					parameterName + "To", renderRequest)
			).totalHits(
				searchResponse.getTotalHits()
			).build();
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private Facet _getFacet(
		PortletSharedSearchResponse portletSharedSearchResponse,
		CustomFacetPortletPreferences customFacetPortletPreferences,
		RenderRequest renderRequest) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getFederatedSearchResponse(
				customFacetPortletPreferences.getFederatedSearchKey());

		return searchResponse.withFacetContextGet(
			facetContext -> facetContext.getFacet(
				_getPortletId(renderRequest)));
	}

	private String _getPaginationStartParameterName(
		PortletSharedSearchResponse portletSharedSearchResponse) {

		SearchResponse searchResponse =
			portletSharedSearchResponse.getSearchResponse();

		SearchRequest searchRequest = searchResponse.getRequest();

		return searchRequest.getPaginationStartParameterName();
	}

	private String _getPortletId(RenderRequest renderRequest) {
		return _portal.getPortletId(renderRequest);
	}

	@Reference
	private Portal _portal;

}