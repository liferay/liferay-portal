/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.dao.search.DisplayTerms;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.legacy.document.DocumentBuilderFactory;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.constants.SearchResultsPortletKeys;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactoryImpl;
import com.liferay.portal.search.web.internal.display.context.SearchResultPreferences;
import com.liferay.portal.search.web.internal.document.DocumentFormPermissionCheckerImpl;
import com.liferay.portal.search.web.internal.portlet.shared.search.NullPortletURL;
import com.liferay.portal.search.web.internal.result.display.context.SearchResultSummaryDisplayContext;
import com.liferay.portal.search.web.internal.result.display.context.builder.SearchResultSummaryDisplayContextBuilder;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author André de Oliveira
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=portlet-search-results",
		"com.liferay.portlet.display-category=category.search",
		"com.liferay.portlet.icon=/icons/search.png",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.restore-current-view=false",
		"com.liferay.portlet.use-default-template=true",
		"jakarta.portlet.display-name=Search Results",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/search/results/view.jsp",
		"jakarta.portlet.name=" + SearchResultsPortletKeys.SEARCH_RESULTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=guest,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SearchResultsPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		PortletSharedSearchResponse portletSharedSearchResponse =
			portletSharedSearchRequest.search(renderRequest);

		SearchResultsPortletDisplayContext searchResultsPortletDisplayContext =
			_buildDisplayContext(
				portletSharedSearchResponse, renderRequest, renderResponse);

		if (searchResultsPortletDisplayContext.isRenderNothing()) {
			renderRequest.setAttribute(
				WebKeys.PORTLET_CONFIGURATOR_VISIBILITY, Boolean.TRUE);
		}

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			searchResultsPortletDisplayContext);

		super.render(renderRequest, renderResponse);
	}

	protected String getCurrentURL(RenderRequest renderRequest) {
		return _portal.getCurrentURL(renderRequest);
	}

	protected HttpServletRequest getHttpServletRequest(
		RenderRequest renderRequest) {

		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);

		return liferayPortletRequest.getHttpServletRequest();
	}

	protected PortletURL getPortletURL(
		RenderRequest renderRequest, String paginationStartParameterName) {

		final String urlString = _getURLString(
			renderRequest, paginationStartParameterName);

		return new NullPortletURL() {

			@Override
			public String toString() {
				return urlString;
			}

		};
	}

	protected PortletURLFactory getPortletURLFactory(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return new PortletURLFactoryImpl(renderRequest, renderResponse);
	}

	protected boolean isRenderNothing(
		RenderRequest renderRequest, SearchRequest searchRequest) {

		long assetEntryId = ParamUtil.getLong(renderRequest, "assetEntryId");

		if (assetEntryId != 0) {
			return false;
		}

		if ((searchRequest.getQueryString() == null) &&
			!searchRequest.isEmptySearchEnabled()) {

			return true;
		}

		return false;
	}

	@Reference
	protected AssetEntryLocalService assetEntryLocalService;

	protected AssetRendererFactoryLookup assetRendererFactoryLookup;

	@Reference
	protected DocumentBuilderFactory documentBuilderFactory;

	@Reference
	protected FastDateFormatFactory fastDateFormatFactory;

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected IndexerRegistry indexerRegistry;

	@Reference
	protected Language language;

	@Reference
	protected ObjectDefinitionLocalService objectDefinitionLocalService;

	@Reference
	protected PortletSharedSearchRequest portletSharedSearchRequest;

	@Reference
	protected ResourceActions resourceActions;

	@Reference
	protected SummaryBuilderFactory summaryBuilderFactory;

	@Reference
	protected UserLocalService userLocalService;

	private SearchResultsPortletDisplayContext _buildDisplayContext(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SearchResultsPortletDisplayContext searchResultsPortletDisplayContext =
			_createSearchResultsPortletDisplayContext(renderRequest);

		SearchResultsSummariesHolder searchResultsSummariesHolder =
			_buildSummaries(
				portletSharedSearchResponse, renderRequest, renderResponse);

		List<Document> documents = new ArrayList<>(
			searchResultsSummariesHolder.getDocuments());

		searchResultsPortletDisplayContext.setDocuments(documents);

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		SearchResponse searchResponse = _getSearchResponse(
			portletSharedSearchResponse, searchResultsPortletPreferences);

		SearchRequest searchRequest = searchResponse.getRequest();

		searchResultsPortletDisplayContext.setKeywords(
			GetterUtil.getString(searchRequest.getQueryString()));
		searchResultsPortletDisplayContext.setRenderNothing(
			isRenderNothing(renderRequest, searchRequest));

		searchResultsPortletDisplayContext.setSearchContainer(
			_buildSearchContainer(
				documents, searchResponse.getTotalHits(),
				GetterUtil.getInteger(
					portletSharedSearchResponse.getParameter(
						searchResultsPortletPreferences.
							getPaginationStartParameterName(),
						renderRequest)),
				searchResultsPortletPreferences.
					getPaginationStartParameterName(),
				GetterUtil.getInteger(
					portletSharedSearchResponse.getParameter(
						searchResultsPortletPreferences.
							getPaginationDeltaParameterName(),
						renderRequest),
					searchResultsPortletPreferences.getPaginationDelta()),
				searchResultsPortletPreferences.
					getPaginationDeltaParameterName(),
				renderRequest));
		searchResultsPortletDisplayContext.setSearchResultsSummariesHolder(
			searchResultsSummariesHolder);
		searchResultsPortletDisplayContext.
			setSearchResultSummaryDisplayContexts(
				searchResultsPortletDisplayContext.
					translateSearchResultSummaryDisplayContexts(documents));
		searchResultsPortletDisplayContext.setShowEmptyResultMessage(
			searchResultsPortletPreferences.isShowEmptyResultMessage());
		searchResultsPortletDisplayContext.setShowPagination(
			searchResultsPortletPreferences.isShowPagination());
		searchResultsPortletDisplayContext.setTotalHits(
			searchResponse.getTotalHits());

		return searchResultsPortletDisplayContext;
	}

	private SearchContainer<Document> _buildSearchContainer(
			List<Document> documents, int totalHits, int paginationStart,
			String paginationStartParameterName, int paginationDelta,
			String paginationDeltaParameterName, RenderRequest renderRequest)
		throws PortletException {

		PortletRequest portletRequest = renderRequest;
		DisplayTerms displayTerms = null;
		DisplayTerms searchTerms = null;
		String curParam = paginationStartParameterName;
		int cur = paginationStart;
		int delta = paginationDelta;
		PortletURL portletURL = getPortletURL(
			renderRequest, paginationStartParameterName);
		List<String> headerNames = null;
		String emptyResultsMessage = null;
		String cssClass = null;

		SearchContainer<Document> searchContainer = new SearchContainer<>(
			portletRequest, displayTerms, searchTerms, curParam, cur, delta,
			portletURL, headerNames, emptyResultsMessage, cssClass);

		searchContainer.setDeltaParam(paginationDeltaParameterName);
		searchContainer.setResultsAndTotal(() -> documents, totalHits);

		return searchContainer;
	}

	private SearchResultsSummariesHolder _buildSummaries(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			return _doBuildSummaries(
				portletSharedSearchResponse, renderRequest, renderResponse);
		}
		catch (PortletException portletException) {
			throw portletException;
		}
		catch (RuntimeException runtimeException) {
			throw runtimeException;
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}
	}

	private SearchResultSummaryDisplayContext _buildSummary(
			Document document, RenderRequest renderRequest,
			RenderResponse renderResponse, ThemeDisplay themeDisplay,
			PortletURLFactory portletURLFactory,
			SearchResultsPortletPreferences searchResultsPortletPreferences,
			SearchResultPreferences searchResultPreferences)
		throws Exception {

		SearchResultSummaryDisplayContextBuilder
			searchResultSummaryDisplayContextBuilder =
				new SearchResultSummaryDisplayContextBuilder();

		searchResultSummaryDisplayContextBuilder.setAssetEntryLocalService(
			assetEntryLocalService
		).setAssetRendererFactoryLookup(
			assetRendererFactoryLookup
		).setClassNameLocalService(
			_classNameLocalService
		).setCurrentURL(
			getCurrentURL(renderRequest)
		).setDocument(
			document
		).setDocumentBuilderFactory(
			documentBuilderFactory
		).setFastDateFormatFactory(
			fastDateFormatFactory
		).setGroupLocalService(
			groupLocalService
		).setHighlightEnabled(
			searchResultsPortletPreferences.isHighlightEnabled()
		).setImageRequested(
			true
		).setIndexerRegistry(
			indexerRegistry
		).setLanguage(
			language
		).setLocale(
			themeDisplay.getLocale()
		).setObjectDefinitionLocalService(
			objectDefinitionLocalService
		).setPortletURLFactory(
			portletURLFactory
		).setRenderRequest(
			renderRequest
		).setRenderResponse(
			renderResponse
		).setRequest(
			getHttpServletRequest(renderRequest)
		).setResourceActions(
			resourceActions
		).setSearchResultPreferences(
			searchResultPreferences
		).setSummaryBuilderFactory(
			summaryBuilderFactory
		).setThemeDisplay(
			themeDisplay
		).setUserLocalService(
			userLocalService
		);

		return searchResultSummaryDisplayContextBuilder.build();
	}

	private SearchResultsPortletDisplayContext
		_createSearchResultsPortletDisplayContext(RenderRequest renderRequest) {

		try {
			return new SearchResultsPortletDisplayContext(
				getHttpServletRequest(renderRequest));
		}
		catch (ConfigurationException configurationException) {
			throw new RuntimeException(configurationException);
		}
	}

	private SearchResultsSummariesHolder _doBuildSummaries(
			PortletSharedSearchResponse portletSharedSearchResponse,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		SearchResultsPortletPreferences searchResultsPortletPreferences =
			new SearchResultsPortletPreferencesImpl(
				portletSharedSearchResponse.getPortletPreferences(
					renderRequest));

		ThemeDisplay themeDisplay = portletSharedSearchResponse.getThemeDisplay(
			renderRequest);

		SearchResponse searchResponse = _getSearchResponse(
			portletSharedSearchResponse, searchResultsPortletPreferences);

		List<Document> documents = searchResponse.getDocuments71();

		SearchResultsSummariesHolder searchResultsSummariesHolder =
			new SearchResultsSummariesHolder(documents.size());

		PortletURLFactory portletURLFactory = getPortletURLFactory(
			renderRequest, renderResponse);

		SearchResultPreferences searchResultPreferences =
			new SearchResultPreferencesImpl(
				searchResultsPortletPreferences,
				new DocumentFormPermissionCheckerImpl(themeDisplay));

		for (Document document : documents) {
			SearchResultSummaryDisplayContext
				searchResultSummaryDisplayContext = _buildSummary(
					document, renderRequest, renderResponse, themeDisplay,
					portletURLFactory, searchResultsPortletPreferences,
					searchResultPreferences);

			if ((searchResultSummaryDisplayContext != null) &&
				!searchResultSummaryDisplayContext.isTemporarilyUnavailable()) {

				searchResultsSummariesHolder.put(
					document, searchResultSummaryDisplayContext);
			}
		}

		return searchResultsSummariesHolder;
	}

	private SearchResponse _getSearchResponse(
		PortletSharedSearchResponse portletSharedSearchResponse,
		SearchResultsPortletPreferences searchResultsPortletPreferences) {

		return portletSharedSearchResponse.getFederatedSearchResponse(
			searchResultsPortletPreferences.getFederatedSearchKey());
	}

	private String _getURLString(
		RenderRequest renderRequest, String paginationStartParameterName) {

		return HttpComponentsUtil.removeParameter(
			_portal.getCurrentURL(renderRequest), paginationStartParameterName);
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private Portal _portal;

}