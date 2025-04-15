/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.insights.portlet;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.web.internal.search.insights.constants.SearchInsightsPortletKeys;
import com.liferay.portal.search.web.internal.search.insights.display.context.SearchInsightsDisplayContext;
import com.liferay.portal.search.web.internal.util.SearchPortletPermissionUtil;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Bryan Engler
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search-insights",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.show-portlet-access-denied=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Search Insights",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/search/insights/view.jsp",
		"jakarta.portlet.name=" + SearchInsightsPortletKeys.SEARCH_INSIGHTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SearchInsightsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			_portletSharedSearchRequest.search(renderRequest);

		SearchInsightsPortletPreferences searchInsightsPortletPreferences =
			new SearchInsightsPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			_buildDisplayContext(
				portletSharedSearchResponse, searchInsightsPortletPreferences,
				renderRequest));

		if (!SearchPortletPermissionUtil.containsConfiguration(
				renderRequest, _portal)) {

			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		super.render(renderRequest, renderResponse);
	}

	private SearchInsightsDisplayContext _buildDisplayContext(
		PortletSharedSearchResponse portletSharedSearchResponse,
		SearchInsightsPortletPreferences searchInsightsPortletPreferences,
		RenderRequest renderRequest) {

		SearchInsightsDisplayContext searchInsightsDisplayContext =
			new SearchInsightsDisplayContext();

		SearchResponse searchResponse =
			portletSharedSearchResponse.getFederatedSearchResponse(
				searchInsightsPortletPreferences.getFederatedSearchKey());

		if (_isCompanyAdmin() &&
			(_isRequestStringPresent(searchResponse) ||
			 _isResponseStringPresent(searchResponse))) {

			searchInsightsDisplayContext.setRequestString(
				_buildRequestString(searchResponse));
			searchInsightsDisplayContext.setResponseString(
				_buildResponseString(searchResponse));
			searchInsightsDisplayContext.setSearchEngineVendor(
				_searchEngineInformation.getVendorString());
		}
		else {
			searchInsightsDisplayContext.setHelpMessage(
				_getHelpMessage(renderRequest));
		}

		return searchInsightsDisplayContext;
	}

	private String _buildRequestString(SearchResponse searchResponse) {
		String requestString = StringUtil.trim(
			searchResponse.getRequestString());

		if (Validator.isBlank(requestString)) {
			return StringPool.BLANK;
		}

		return requestString;
	}

	private String _buildResponseString(SearchResponse searchResponse) {
		String responseString = StringUtil.trim(
			searchResponse.getResponseString());

		if (Validator.isBlank(responseString)) {
			return StringPool.BLANK;
		}

		return responseString;
	}

	private String _getHelpMessage(RenderRequest renderRequest) {
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(
			"content.Language", renderRequest.getLocale(), getClass());

		return _language.get(resourceBundle, "search-insights-help");
	}

	private boolean _isCompanyAdmin() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		return permissionChecker.isCompanyAdmin();
	}

	private boolean _isRequestStringPresent(SearchResponse searchResponse) {
		return !Validator.isBlank(searchResponse.getRequestString());
	}

	private boolean _isResponseStringPresent(SearchResponse searchResponse) {
		return !Validator.isBlank(searchResponse.getResponseString());
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private PortletSharedSearchRequest _portletSharedSearchRequest;

	@Reference
	private SearchEngineInformation _searchEngineInformation;

}