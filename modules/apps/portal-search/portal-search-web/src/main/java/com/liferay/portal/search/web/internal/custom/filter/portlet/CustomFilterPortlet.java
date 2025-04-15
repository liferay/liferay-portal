/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.custom.filter.portlet;

import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.custom.filter.constants.CustomFilterPortletKeys;
import com.liferay.portal.search.web.internal.custom.filter.display.context.CustomFilterDisplayContext;
import com.liferay.portal.search.web.internal.custom.filter.display.context.builder.CustomFilterDisplayContextBuilder;
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
 * @author Igor Nazar
 * @author Luan Maoski
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-custom-filter",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Custom Filter",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/custom/filter/view.jsp",
		"jakarta.portlet.name=" + CustomFilterPortletKeys.CUSTOM_FILTER,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CustomFilterPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		CustomFilterPortletPreferences customFilterPortletPreferences =
			new CustomFilterPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		CustomFilterDisplayContext customFilterDisplayContext =
			_createCustomFilterDisplayContext(
				customFilterPortletPreferences, portletSharedSearchResponse,
				renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, customFilterDisplayContext);

		if (customFilterDisplayContext.isRenderNothing() ||
			customFilterPortletPreferences.isImmutable() ||
			customFilterPortletPreferences.isInvisible()) {

			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	@Reference
	protected Portal portal;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	private CustomFilterDisplayContext _buildDisplayContext(
			CustomFilterPortletPreferences customFilterPortletPreferences,
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest)
		throws ConfigurationException {

		String parameterName = CustomFilterPortletUtil.getParameterName(
			customFilterPortletPreferences);

		SearchResponse searchResponse = _getSearchResponse(
			portletSharedSearchResponse, customFilterPortletPreferences);

		SearchRequest searchRequest = searchResponse.getRequest();

		return CustomFilterDisplayContextBuilder.builder(
		).customHeading(
			customFilterPortletPreferences.getCustomHeading()
		).disabled(
			customFilterPortletPreferences.isDisabled()
		).filterField(
			customFilterPortletPreferences.getFilterField()
		).immutable(
			customFilterPortletPreferences.isImmutable()
		).filterValue(
			customFilterPortletPreferences.getFilterValue()
		).parameterName(
			parameterName
		).parameterValue(
			portletSharedSearchResponse.getParameter(
				parameterName, renderRequest)
		).queryName(
			customFilterPortletPreferences.getQueryName()
		).renderNothing(
			_isRenderNothing(searchRequest)
		).themeDisplay(
			portletSharedSearchResponse.getThemeDisplay(renderRequest)
		).build();
	}

	private CustomFilterDisplayContext _createCustomFilterDisplayContext(
		CustomFilterPortletPreferences customFilterPortletPreferences,
		PortletSharedSearchResponse portletSharedSearchResponse,
		RenderRequest renderRequest) {

		try {
			return _buildDisplayContext(
				customFilterPortletPreferences, portletSharedSearchResponse,
				renderRequest);
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private SearchResponse _getSearchResponse(
		PortletSharedSearchResponse portletSharedSearchResponse,
		CustomFilterPortletPreferences customFilterPortletPreferences) {

		return portletSharedSearchResponse.getFederatedSearchResponse(
			customFilterPortletPreferences.getFederatedSearchKey());
	}

	private boolean _isRenderNothing(SearchRequest searchRequest) {
		if ((searchRequest.getQueryString() == null) &&
			!searchRequest.isEmptySearchEnabled()) {

			return true;
		}

		return false;
	}

}