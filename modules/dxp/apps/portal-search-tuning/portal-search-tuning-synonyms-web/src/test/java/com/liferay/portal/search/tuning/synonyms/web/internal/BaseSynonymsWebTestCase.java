/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal;

import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.PortalPreferences;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactory;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.DocumentRequest;
import com.liferay.portal.search.engine.adapter.document.DocumentResponse;
import com.liferay.portal.search.engine.adapter.index.IndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndexResponse;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexRequest;
import com.liferay.portal.search.engine.adapter.index.IndicesExistsIndexResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSet;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetFields;
import com.liferay.portal.search.tuning.synonyms.web.internal.index.SynonymSetIndexReader;
import com.liferay.portal.search.tuning.synonyms.web.internal.storage.SynonymSetStorageAdapter;

import jakarta.portlet.ActionURL;
import jakarta.portlet.MimeResponse;
import jakarta.portlet.PortletConfig;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;

import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public abstract class BaseSynonymsWebTestCase {

	protected Document setUpDocument(String synonyms) {
		Document document = Mockito.mock(Document.class);

		Mockito.doReturn(
			synonyms
		).when(
			document
		).getString(
			Mockito.eq(SynonymSetFields.SYNONYMS)
		);

		return document;
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

	protected void setUpHttpServletRequestParameterValue(
		HttpServletRequest httpServletRequest, String paramName, String value) {

		Mockito.doReturn(
			value
		).when(
			httpServletRequest
		).getParameter(
			Mockito.eq(paramName)
		);
	}

	protected void setUpHttpServletRequestParameterValues(
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

	protected IndexResponse setUpIndexResponse() {
		return Mockito.mock(IndexResponse.class);
	}

	protected void setUpPortal(HttpServletRequest httpServletRequest) {
		setUpHttpServletRequestAttribute(
			httpServletRequest, WebKeys.THEME_DISPLAY,
			Mockito.mock(ThemeDisplay.class));

		_setUpPortalGetCurrentURL();
		_setUpPortalGetHttpServletRequest(httpServletRequest);
		_setUpPortalGetLiferayPortletRequest();
		_setUpPortalGetOriginalServletRequest(httpServletRequest);
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

	protected void setUpPortletRequest(PortletRequest portletRequest) {
		Layout layout = Mockito.mock(Layout.class);

		_setUpLayoutIsTypeControlPanel(layout, true);

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		_setUpThemeDisplayGetLayout(themeDisplay, layout);

		LayoutTypePortlet layoutTypePortlet = Mockito.mock(
			LayoutTypePortlet.class);

		_setUpLayoutTypePortletHasPortletId(layoutTypePortlet, true);

		_setUpThemeDisplayGetLayoutTypePortlet(themeDisplay, layoutTypePortlet);

		_setUpPortletRequestGetAttribute(
			portletRequest, Mockito.mock(PortletConfig.class),
			JavaConstants.JAVAX_PORTLET_CONFIG);
		_setUpPortletRequestGetAttribute(
			portletRequest, themeDisplay, WebKeys.THEME_DISPLAY);
	}

	@SuppressWarnings("deprecation")
	protected void setUpPortletRequestParameterValue(
		PortletRequest portletRequest, String paramName, String value) {

		Mockito.doReturn(
			value
		).when(
			portletRequest
		).getParameter(
			Mockito.eq(paramName)
		);
	}

	protected void setUpRenderResponse(MimeResponse mimeResponse) {
		RenderURL renderURL = Mockito.mock(RenderURL.class);

		Mockito.doReturn(
			""
		).when(
			renderURL
		).toString();

		Mockito.doReturn(
			Mockito.mock(ActionURL.class)
		).when(
			mimeResponse
		).createActionURL();

		Mockito.doReturn(
			renderURL
		).when(
			mimeResponse
		).createRenderURL();

		Mockito.doReturn(
			"namespace-"
		).when(
			mimeResponse
		).getNamespace();
	}

	@SuppressWarnings("unchecked")
	protected void setUpSearchEngineAdapter() {
		Mockito.doReturn(
			setUpIndexResponse()
		).when(
			searchEngineAdapter
		).execute(
			(IndexRequest<IndexResponse>)Mockito.any()
		);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	protected void setUpSearchEngineAdapter(DocumentResponse documentResponse) {
		Mockito.doReturn(
			documentResponse
		).when(
			searchEngineAdapter
		).execute(
			(DocumentRequest)Mockito.any()
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

		SearchSearchResponse searchSearchResponse =
			_setUpSearchSearchResponse();

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

	protected SearchHits setUpSearchHits(String synonyms) {
		Document document = setUpDocument(synonyms);

		SearchHit searchHit = Mockito.mock(SearchHit.class);

		Mockito.doReturn(
			document
		).when(
			searchHit
		).getDocument();

		Mockito.doReturn(
			"id"
		).when(
			searchHit
		).getId();

		SearchHits searchHits = Mockito.mock(SearchHits.class);

		Mockito.doReturn(
			Arrays.asList(searchHit)
		).when(
			searchHits
		).getSearchHits();

		return searchHits;
	}

	protected void setUpSynonymSetIndexNameBuilder() {
		Mockito.doReturn(
			Mockito.mock(SynonymSetIndexName.class)
		).when(
			synonymSetIndexNameBuilder
		).getSynonymSetIndexName(
			Mockito.anyLong()
		);
	}

	protected void setUpSynonymSetIndexReader(boolean exists) {
		Mockito.doReturn(
			exists
		).when(
			synonymSetIndexReader
		).isExists(
			Mockito.any()
		);
	}

	protected void setUpSynonymSetIndexReader(String id, String synonyms) {
		SynonymSet.SynonymSetBuilder synonymSetBuilder =
			new SynonymSet.SynonymSetBuilder();

		Mockito.doReturn(
			synonymSetBuilder.synonyms(
				synonyms
			).synonymSetDocumentId(
				id
			).build()
		).when(
			synonymSetIndexReader
		).fetch(
			Mockito.any(), Mockito.anyString()
		);

		Mockito.doReturn(
			Arrays.asList(
				synonymSetBuilder.synonyms(
					synonyms
				).synonymSetDocumentId(
					id
				).build())
		).when(
			synonymSetIndexReader
		).search(
			Mockito.any()
		);
	}

	protected Portal portal = Mockito.mock(Portal.class);
	protected SearchEngineAdapter searchEngineAdapter = Mockito.mock(
		SearchEngineAdapter.class);
	protected SynonymSetIndexNameBuilder synonymSetIndexNameBuilder =
		Mockito.mock(SynonymSetIndexNameBuilder.class);
	protected SynonymSetIndexReader synonymSetIndexReader = Mockito.mock(
		SynonymSetIndexReader.class);
	protected SynonymSetStorageAdapter synonymSetStorageAdapter = Mockito.mock(
		SynonymSetStorageAdapter.class);

	private void _setUpLayoutIsTypeControlPanel(
		Layout layout, boolean returnValue) {

		Mockito.doReturn(
			returnValue
		).when(
			layout
		).isTypeControlPanel();
	}

	private void _setUpLayoutTypePortletHasPortletId(
		LayoutTypePortlet layoutTypePortlet, boolean returnValue) {

		Mockito.doReturn(
			returnValue
		).when(
			layoutTypePortlet
		).hasPortletId(
			Mockito.anyString()
		);
	}

	private void _setUpPortalGetCurrentURL() {
		Mockito.doReturn(
			"currentURL"
		).when(
			portal
		).getCurrentURL(
			Mockito.any(HttpServletRequest.class)
		);
	}

	private void _setUpPortalGetHttpServletRequest(
		HttpServletRequest httpServletRequest) {

		Mockito.doReturn(
			httpServletRequest
		).when(
			portal
		).getHttpServletRequest(
			Mockito.any(PortletRequest.class)
		);
	}

	private void _setUpPortalGetLiferayPortletRequest() {
		Mockito.doReturn(
			Mockito.mock(LiferayPortletRequest.class)
		).when(
			portal
		).getLiferayPortletRequest(
			Mockito.any(PortletRequest.class)
		);
	}

	private void _setUpPortalGetOriginalServletRequest(
		HttpServletRequest httpServletRequest) {

		Mockito.doReturn(
			httpServletRequest
		).when(
			portal
		).getOriginalServletRequest(
			Mockito.any(HttpServletRequest.class)
		);
	}

	private void _setUpPortletRequestGetAttribute(
		PortletRequest portletRequest, Object object, String keyValue) {

		Mockito.doReturn(
			object
		).when(
			portletRequest
		).getAttribute(
			Mockito.eq(keyValue)
		);
	}

	private SearchSearchResponse _setUpSearchSearchResponse() {
		return Mockito.mock(SearchSearchResponse.class);
	}

	private void _setUpThemeDisplayGetLayout(
		ThemeDisplay themeDisplay, Layout layout) {

		Mockito.doReturn(
			layout
		).when(
			themeDisplay
		).getLayout();
	}

	private void _setUpThemeDisplayGetLayoutTypePortlet(
		ThemeDisplay themeDisplay, LayoutTypePortlet layoutTypePortlet) {

		Mockito.doReturn(
			layoutTypePortlet
		).when(
			themeDisplay
		).getLayoutTypePortlet();
	}

}