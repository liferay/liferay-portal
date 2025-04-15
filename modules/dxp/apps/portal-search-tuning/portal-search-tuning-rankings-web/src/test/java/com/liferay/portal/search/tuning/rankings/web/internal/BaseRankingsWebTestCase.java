/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal;

import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.FastDateFormatFactory;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DeleteDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DocumentResponse;
import com.liferay.portal.search.engine.adapter.document.GetDocumentRequest;
import com.liferay.portal.search.engine.adapter.document.GetDocumentResponse;
import com.liferay.portal.search.engine.adapter.document.IndexDocumentRequest;
import com.liferay.portal.search.engine.adapter.index.CreateIndexRequest;
import com.liferay.portal.search.engine.adapter.index.DeleteIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.filter.ComplexQueryPart;
import com.liferay.portal.search.filter.ComplexQueryPartBuilder;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.IdsQuery;
import com.liferay.portal.search.query.MatchAllQuery;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.tuning.rankings.helper.RankingHelper;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexName;
import com.liferay.portal.search.tuning.rankings.index.name.RankingIndexNameBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.helper.RankingHelperImpl;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreterProvider;

import jakarta.portlet.ActionResponse;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.text.SimpleDateFormat;

import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.function.Consumer;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.mockito.AdditionalAnswers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Wade Cao
 */
public abstract class BaseRankingsWebTestCase {

	@BeforeClass
	public static void setUpClass() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		documentBuilderFactoryServiceRegistration =
			bundleContext.registerService(
				DocumentBuilderFactory.class, documentBuilderFactory, null);

		searchResultInterpreterProviderServiceRegistration =
			bundleContext.registerService(
				SearchResultInterpreterProvider.class,
				searchResultInterpreterProvider, null);
	}

	@AfterClass
	public static void tearDownClass() {
		frameworkUtilMockedStatic.close();

		documentBuilderFactoryServiceRegistration.unregister();

		searchResultInterpreterProviderServiceRegistration.unregister();
	}

	public ComplexQueryPartBuilderFactory complexQueryPartBuilderFactory =
		Mockito.mock(ComplexQueryPartBuilderFactory.class);

	protected ComplexQueryPartBuilder setUpComplexQueryPartBuilder() {
		ComplexQueryPartBuilder complexQueryPartBuilder = Mockito.mock(
			ComplexQueryPartBuilder.class);

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilder
		).additive(
			Mockito.anyBoolean()
		);

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilder
		).query(
			Mockito.any()
		);

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilder
		).occur(
			Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(ComplexQueryPart.class)
		).when(
			complexQueryPartBuilder
		).build();

		return complexQueryPartBuilder;
	}

	protected void setUpComplexQueryPartBuilderFactory(
		ComplexQueryPartBuilder complexQueryPartBuilder) {

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilder
		).additive(
			Mockito.anyBoolean()
		);

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilder
		).query(
			Mockito.any()
		);

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilder
		).occur(
			Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(ComplexQueryPart.class)
		).when(
			complexQueryPartBuilder
		).build();

		Mockito.doReturn(
			complexQueryPartBuilder
		).when(
			complexQueryPartBuilderFactory
		).builder();
	}

	protected void setUpDLAppLocalService() throws PortalException {
		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.doReturn(
			"imageFile"
		).when(
			fileEntry
		).getMimeType();

		Mockito.doReturn(
			fileEntry
		).when(
			dlAppLocalService
		).getFileEntry(
			Mockito.anyLong()
		);
	}

	protected Document setUpDocumentWithGetString() {
		Document document = Mockito.mock(Document.class);

		Mockito.when(
			document.getString(Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> {
				String argument = (String)invocationOnMock.getArguments()[0];

				if (argument.equals("clicks")) {
					return "theClicks";
				}
				else if (argument.equals("createDate")) {
					return "2003-09-01 09:00:00";
				}
				else if (argument.equals("entryClassName")) {
					return DLFileEntryConstants.getClassName();
				}
				else if (argument.equals("title")) {
					return "theTitle";
				}
				else if (argument.equals("uid")) {
					return "theUID";
				}
				else if (argument.equals("userName")) {
					return "theAuthor";
				}

				return "undefined";
			}
		);

		return document;
	}

	protected void setUpFastDateFormatFactory() {
		Mockito.doReturn(
			new SimpleDateFormat("yyyyMMddHHmmss")
		).when(
			fastDateFormatFactory
		).getDateTime(
			Mockito.anyInt(), Mockito.anyInt(), Mockito.any(), Mockito.any()
		);
	}

	protected GetDocumentResponse setUpGetDocumentResponse() {
		GetDocumentResponse getDocumentResponse = Mockito.mock(
			GetDocumentResponse.class);

		Mockito.doReturn(
			true
		).when(
			getDocumentResponse
		).isExists();

		return getDocumentResponse;
	}

	protected GetDocumentResponse setUpGetDocumentResponseGetDocument(
		Document document, GetDocumentResponse getDocumentResponse) {

		Mockito.doReturn(
			document
		).when(
			getDocumentResponse
		).getDocument();

		return getDocumentResponse;
	}

	protected void setUpGroupLocalServiceFetchGroup() throws PortalException {
		Group group = Mockito.mock(Group.class);

		Mockito.doReturn(
			"groupExternalReferenceCode"
		).when(
			group
		).getExternalReferenceCode();

		Mockito.doReturn(
			12345L
		).when(
			group
		).getGroupId();

		Mockito.doReturn(
			group
		).when(
			groupLocalService
		).fetchGroup(
			Mockito.anyLong()
		);

		Mockito.doReturn(
			group
		).when(
			groupLocalService
		).fetchGroupByExternalReferenceCode(
			Mockito.anyString(), Mockito.anyLong()
		);
	}

	protected void setUpHttpServletRequestAttribute(
		HttpServletRequest httpServletRequest, String paramName,
		Object object) {

		Mockito.doReturn(
			object
		).when(
			httpServletRequest
		).getAttribute(
			Mockito.eq(paramName)
		);
	}

	protected void setUpHttpServletRequestGetAttribute(
		HttpServletRequest httpServletRequest, String param,
		Object returnValue) {

		Mockito.doReturn(
			returnValue
		).when(
			httpServletRequest
		).getAttribute(
			Mockito.eq(param)
		);
	}

	protected void setUpHttpServletRequestParamValue(
		HttpServletRequest httpServletRequest, String paramName,
		String returnValue) {

		Mockito.doReturn(
			returnValue
		).when(
			httpServletRequest
		).getParameter(
			Mockito.eq(paramName)
		);
	}

	protected void setUpHttpServletRequestParamValues(
		HttpServletRequest httpServletRequest, String paramName,
		String[] returnValue) {

		Mockito.doReturn(
			returnValue
		).when(
			httpServletRequest
		).getParameterValues(
			Mockito.eq(paramName)
		);
	}

	protected void setUpLanguageUtil(String returnValue) throws Exception {
		Mockito.doReturn(
			returnValue
		).when(
			language
		).get(
			Mockito.any(Locale.class), Mockito.anyString(), Mockito.anyString()
		);

		LanguageUtil languageUtil = new LanguageUtil();

		languageUtil.setLanguage(language);
	}

	protected void setUpPortal() {
		Mockito.doReturn(
			111L
		).when(
			portal
		).getCompanyId(
			Mockito.any(PortletRequest.class)
		);

		HttpServletRequest httpServletRequest =
			setUpPortalGetHttpServletRequest();

		setUpHttpServletRequestGetAttribute(
			httpServletRequest, WebKeys.THEME_DISPLAY,
			Mockito.mock(ThemeDisplay.class));

		Mockito.doReturn(
			"currentURL"
		).when(
			portal
		).getCurrentURL(
			Mockito.any(HttpServletRequest.class)
		);

		LiferayPortletResponse liferayPortletResponse = Mockito.mock(
			LiferayPortletResponse.class);

		Mockito.doReturn(
			Mockito.mock(PortletURL.class)
		).when(
			liferayPortletResponse
		).createRenderURL();

		Mockito.doReturn(
			liferayPortletResponse
		).when(
			portal
		).getLiferayPortletResponse(
			Mockito.any(ActionResponse.class)
		);

		Mockito.doReturn(
			Mockito.mock(HttpSession.class)
		).when(
			httpServletRequest
		).getSession();

		Mockito.doReturn(
			httpServletRequest
		).when(
			portal
		).getOriginalServletRequest(
			Mockito.any(HttpServletRequest.class)
		);

		Mockito.doReturn(
			Mockito.mock(LiferayPortletRequest.class)
		).when(
			portal
		).getLiferayPortletRequest(
			Mockito.any(PortletRequest.class)
		);

		Mockito.doReturn(
			"url"
		).when(
			portal
		).escapeRedirect(
			Mockito.anyString()
		);
	}

	protected HttpServletRequest setUpPortalGetHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.doReturn(
			httpServletRequest
		).when(
			portal
		).getHttpServletRequest(
			Mockito.any(PortletRequest.class)
		);

		return httpServletRequest;
	}

	protected PortletURL setUpPortalPortletURL() {
		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.doReturn(
			portletURL
		).when(
			portal
		).getControlPanelPortletURL(
			Mockito.nullable(HttpServletRequest.class),
			Mockito.nullable(Group.class), Mockito.nullable(String.class),
			Mockito.anyLong(), Mockito.anyLong(), Mockito.nullable(String.class)
		);

		return portletURL;
	}

	protected void setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(portal);
	}

	protected void setUpPortletPreferencesFactoryUtil() throws Exception {
		PortletPreferencesFactoryUtil portletPreferencesFactoryUtil =
			new PortletPreferencesFactoryUtil();

		PortletPreferencesFactory portletPreferencesFactory = Mockito.mock(
			PortletPreferencesFactory.class);

		portletPreferencesFactoryUtil.setPortletPreferencesFactory(
			portletPreferencesFactory);

		PortalPreferences portalPreferences = Mockito.mock(
			PortalPreferences.class);

		Mockito.when(
			portletPreferencesFactory.getPortalPreferences(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			portalPreferences
		);

		Mockito.when(
			portalPreferences.getValue(
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())
		).then(
			AdditionalAnswers.returnsLastArg()
		);
	}

	protected void setUpPortletRequestParamValue(
		PortletRequest portletRequest, String paramName, String returnValue) {

		Mockito.doReturn(
			returnValue
		).when(
			portletRequest
		).getParameter(
			Mockito.eq(paramName)
		);
	}

	protected void setUpPropsUtil() {
		Props props = Mockito.mock(Props.class);

		Mockito.doReturn(
			""
		).when(
			props
		).get(
			Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(Properties.class)
		).when(
			props
		).getProperties(
			Mockito.anyString(), Mockito.anyBoolean()
		);

		PropsUtil.setProps(props);
	}

	protected void setUpQuery() {
		IdsQuery idsQuery = Mockito.mock(IdsQuery.class);

		Mockito.doNothing(
		).when(
			idsQuery
		).addIds(
			Mockito.any()
		);

		Mockito.doNothing(
		).when(
			idsQuery
		).setBoost(
			Mockito.anyFloat()
		);

		Mockito.doReturn(
			Mockito.mock(BooleanQuery.class)
		).when(
			queries
		).booleanQuery();

		Mockito.doReturn(
			idsQuery
		).when(
			queries
		).ids();

		Mockito.doReturn(
			Mockito.mock(MatchAllQuery.class)
		).when(
			queries
		).matchAll();
	}

	protected void setUpRankingHelper() {
		Mockito.doReturn(
			StringPool.BLANK
		).when(
			rankingHelper
		).getDocumentId(
			Mockito.anyString()
		);
	}

	protected void setUpRankingIndexNameBuilder() {
		Mockito.doReturn(
			Mockito.mock(RankingIndexName.class)
		).when(
			rankingIndexNameBuilder
		).getRankingIndexName(
			Mockito.anyLong()
		);
	}

	protected void setUpRenderResponse(MimeResponse mimeResponse) {
		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.doReturn(
			""
		).when(
			portletURL
		).toString();

		Mockito.doReturn(
			portletURL
		).when(
			mimeResponse
		).createRenderURL();
	}

	protected void setUpResourceRequest() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			null
		).when(
			themeDisplay
		).getLocale();

		Mockito.doReturn(
			themeDisplay
		).when(
			resourceRequest
		).getAttribute(
			Mockito.anyString()
		);
	}

	protected void setUpSearchEngineAdapter(DocumentResponse documentResponse) {
		Mockito.doReturn(
			null
		).when(
			searchEngineAdapter
		).execute(
			(CreateIndexRequest)Mockito.any()
		);

		Mockito.doReturn(
			null
		).when(
			searchEngineAdapter
		).execute(
			(DeleteIndexRequest)Mockito.any()
		);

		Mockito.doReturn(
			documentResponse
		).when(
			searchEngineAdapter
		).execute(
			(GetDocumentRequest)Mockito.any()
		);

		Mockito.doReturn(
			documentResponse
		).when(
			searchEngineAdapter
		).execute(
			(IndexDocumentRequest)Mockito.any()
		);

		Mockito.doReturn(
			documentResponse
		).when(
			searchEngineAdapter
		).execute(
			(DeleteDocumentRequest)Mockito.any()
		);

		IndicesExistsIndexResponse indicesExistsIndexResponse = Mockito.mock(
			IndicesExistsIndexResponse.class);

		Mockito.doReturn(
			true
		).when(
			indicesExistsIndexResponse
		).isExists();

		Mockito.doReturn(
			indicesExistsIndexResponse
		).when(
			searchEngineAdapter
		).execute(
			(IndicesExistsIndexRequest)Mockito.any()
		);
	}

	protected SearchHits setUpSearchEngineAdapter(SearchHits searchHits) {
		Mockito.doReturn(
			3L
		).when(
			searchHits
		).getTotalHits();

		SearchSearchResponse searchSearchResponse = setUpSearchSearchResponse();

		Mockito.doReturn(
			searchHits
		).when(
			searchSearchResponse
		).getSearchHits();

		Mockito.doReturn(
			searchSearchResponse
		).when(
			searchEngineAdapter
		).execute(
			(SearchSearchRequest)Mockito.any()
		);

		return searchHits;
	}

	protected void setUpSearcher(SearchResponse searchResponse) {
		Mockito.doReturn(
			searchResponse
		).when(
			searcher
		).search(
			Mockito.any()
		);
	}

	@SuppressWarnings("unchecked")
	protected SearchRequestBuilder setUpSearchRequestBuilder() {
		SearchRequestBuilder searchRequestBuilder = Mockito.mock(
			SearchRequestBuilder.class);

		Mockito.doReturn(
			Mockito.mock(SearchRequest.class)
		).when(
			searchRequestBuilder
		).build();

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).addComplexQueryPart(
			Mockito.any()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).from(
			Mockito.anyInt()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).queryString(
			Mockito.nullable(String.class)
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).size(
			Mockito.anyInt()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).withSearchContext(
			Mockito.nullable(Consumer.class)
		);

		return searchRequestBuilder;
	}

	protected void setUpSearchRequestBuilderFactory(
		SearchRequestBuilder searchRequestBuilder) {

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilderFactory
		).builder();

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilderFactory
		).builder(
			Mockito.any()
		);
	}

	protected SearchResponse setUpSearchResponse(Document document) {
		SearchResponse searchResponse = Mockito.mock(SearchResponse.class);

		Mockito.doReturn(
			Collections.singletonList(document)
		).when(
			searchResponse
		).getDocuments();

		Mockito.doReturn(
			1
		).when(
			searchResponse
		).getTotalHits();

		return searchResponse;
	}

	protected SearchSearchResponse setUpSearchSearchResponse() {
		return Mockito.mock(SearchSearchResponse.class);
	}

	protected static final DocumentBuilderFactory documentBuilderFactory =
		Mockito.mock(DocumentBuilderFactory.class);
	protected static ServiceRegistration<DocumentBuilderFactory>
		documentBuilderFactoryServiceRegistration;
	protected static final MockedStatic<FrameworkUtil>
		frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);
	protected static final SearchResultInterpreterProvider
		searchResultInterpreterProvider = Mockito.mock(
			SearchResultInterpreterProvider.class);
	protected static ServiceRegistration<SearchResultInterpreterProvider>
		searchResultInterpreterProviderServiceRegistration;

	protected DLAppLocalService dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	protected FastDateFormatFactory fastDateFormatFactory = Mockito.mock(
		FastDateFormatFactory.class);
	protected GroupLocalService groupLocalService = Mockito.mock(
		GroupLocalService.class);
	protected Language language = Mockito.mock(Language.class);
	protected Portal portal = Mockito.mock(Portal.class);
	protected Queries queries = Mockito.mock(Queries.class);
	protected RankingHelper rankingHelper = Mockito.spy(
		RankingHelperImpl.class);
	protected RankingIndexNameBuilder rankingIndexNameBuilder = Mockito.mock(
		RankingIndexNameBuilder.class);
	protected ResourceRequest resourceRequest = Mockito.mock(
		ResourceRequest.class);
	protected ResourceResponse resourceResponse = Mockito.mock(
		ResourceResponse.class);
	protected SearchEngineAdapter searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);
	protected Searcher searcher = Mockito.mock(Searcher.class);
	protected SearchRequestBuilderFactory searchRequestBuilderFactory =
		Mockito.mock(SearchRequestBuilderFactory.class);

}