/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.display.context;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.context.SearchContextFactory;
import com.liferay.portal.search.internal.legacy.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.legacy.searcher.SearchResponseBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.SearchResponseBuilder;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.constants.SearchPortletParameterNames;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portlet.portletconfiguration.util.ConfigurationRenderRequest;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author André de Oliveira
 */
public class SearchDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_frameworkUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		Mockito.when(
			FrameworkUtil.getBundle(Mockito.any())
		).thenReturn(
			bundleContext.getBundle()
		);

		themeDisplay = _createThemeDisplay();

		_setUpHttpServletRequest();
		_setUpPortletURLFactory();
		_setUpRenderRequest();
		_setUpSearchContextFactory();
		_setUpSearcher();
		_setUpSearchResponseBuilderFactory();
	}

	@Test
	public void testConfigurationKeywordsEmptySkipsSearch() throws Exception {
		_assertSearchSkippedAndNullResults(
			null,
			new ConfigurationRenderRequest(renderRequest, portletPreferences));
	}

	@Test
	public void testNoScopeParameter() throws Exception {
		portletPreferences.setValue("searchScope", "let-the-user-choose");

		_assertSearchKeywords(StringPool.DOUBLE_SPACE, StringPool.BLANK);
	}

	@Test
	public void testSearchKeywordsBlank() throws Exception {
		_assertSearchKeywords(StringPool.BLANK, StringPool.BLANK);
	}

	@Test
	public void testSearchKeywordsNullWord() throws Exception {
		_assertSearchKeywords(StringPool.NULL, StringPool.NULL);
	}

	@Test
	public void testSearchKeywordsSpaces() throws Exception {
		_assertSearchKeywords(StringPool.DOUBLE_SPACE, StringPool.BLANK);
	}

	protected HttpServletRequest httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	protected PortletPreferences portletPreferences =
		new MockPortletPreferences();
	protected PortletURLFactory portletURLFactory = Mockito.mock(
		PortletURLFactory.class);
	protected RenderRequest renderRequest = Mockito.mock(RenderRequest.class);
	protected SearchContextFactory searchContextFactory = Mockito.mock(
		SearchContextFactory.class);
	protected Searcher searcher = Mockito.mock(Searcher.class);
	protected SearchRequestBuilderFactory searchRequestBuilderFactory =
		new SearchRequestBuilderFactoryImpl();
	protected SearchResponse searchResponse = Mockito.mock(
		SearchResponse.class);
	protected SearchResponseBuilder searchResponseBuilder = Mockito.mock(
		SearchResponseBuilder.class);
	protected SearchResponseBuilderFactory searchResponseBuilderFactory =
		Mockito.mock(SearchResponseBuilderFactory.class);
	protected ThemeDisplay themeDisplay;

	private void _assertSearchKeywords(
			String requestKeywords, String searchDisplayContextKeywords)
		throws Exception {

		SearchDisplayContext searchDisplayContext = _createSearchDisplayContext(
			requestKeywords, renderRequest);

		Assert.assertEquals(
			searchDisplayContextKeywords, searchDisplayContext.getKeywords());

		Assert.assertNotNull(searchDisplayContext.getHits());
		Assert.assertNotNull(searchDisplayContext.getSearchContainer());
		Assert.assertNotNull(searchDisplayContext.getSearchContext());

		SearchContext searchContext = searchDisplayContext.getSearchContext();

		Mockito.verify(
			searcher
		).search(
			Mockito.any()
		);

		Assert.assertEquals(
			searchDisplayContextKeywords, searchContext.getKeywords());
	}

	private void _assertSearchSkippedAndNullResults(
			String requestKeywords, RenderRequest renderRequest)
		throws Exception {

		SearchDisplayContext searchDisplayContext = _createSearchDisplayContext(
			requestKeywords, renderRequest);

		Assert.assertNull(searchDisplayContext.getHits());
		Assert.assertNull(searchDisplayContext.getKeywords());
		Assert.assertNull(searchDisplayContext.getSearchContainer());
		Assert.assertNull(searchDisplayContext.getSearchContext());

		Mockito.verifyNoMoreInteractions(searcher);
	}

	private JSONArray _createJSONArray() {
		JSONArray jsonArray = Mockito.mock(JSONArray.class);

		Mockito.doReturn(
			1
		).when(
			jsonArray
		).length();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			jsonArray
		).getString(
			0
		);

		return jsonArray;
	}

	private JSONFactory _createJSONFactory() {
		JSONFactory jsonFactory = Mockito.mock(JSONFactory.class);

		Mockito.doReturn(
			_createJSONObject()
		).when(
			jsonFactory
		).createJSONObject();

		return jsonFactory;
	}

	private JSONObject _createJSONObject() {
		JSONObject jsonObject = Mockito.mock(JSONObject.class);

		Mockito.doReturn(
			true
		).when(
			jsonObject
		).has(
			"values"
		);

		Mockito.doReturn(
			_createJSONArray()
		).when(
			jsonObject
		).getJSONArray(
			"values"
		);

		return jsonObject;
	}

	private Portal _createPortal(RenderRequest renderRequest) throws Exception {
		Portal portal = Mockito.mock(Portal.class);

		Mockito.doReturn(
			httpServletRequest
		).when(
			portal
		).getHttpServletRequest(
			renderRequest
		);

		return portal;
	}

	private SearchDisplayContext _createSearchDisplayContext(
			String keywords, RenderRequest renderRequest)
		throws Exception {

		_setUpRequestKeywords(keywords);

		PropsTestUtil.setProps(Collections.emptyMap());

		return new SearchDisplayContext(
			renderRequest, portletPreferences, _createPortal(renderRequest),
			Mockito.mock(Language.class), searcher,
			Mockito.mock(IndexSearchPropsValues.class), portletURLFactory,
			Mockito.mock(SummaryBuilderFactory.class), searchContextFactory,
			searchRequestBuilderFactory, _createJSONFactory());
	}

	private ThemeDisplay _createThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(Mockito.mock(Company.class));
		themeDisplay.setUser(Mockito.mock(User.class));

		return themeDisplay;
	}

	private void _setUpHttpServletRequest() throws Exception {
		Mockito.doReturn(
			themeDisplay
		).when(
			httpServletRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);
	}

	private void _setUpPortletURLFactory() throws Exception {
		Mockito.doReturn(
			Mockito.mock(PortletURL.class)
		).when(
			portletURLFactory
		).getPortletURL();
	}

	private void _setUpRenderRequest() throws Exception {
		Mockito.doReturn(
			themeDisplay
		).when(
			renderRequest
		).getAttribute(
			WebKeys.THEME_DISPLAY
		);
	}

	private void _setUpRequestKeywords(String keywords) {
		Mockito.doReturn(
			keywords
		).when(
			httpServletRequest
		).getParameter(
			SearchPortletParameterNames.KEYWORDS
		);

		Mockito.doReturn(
			keywords
		).when(
			renderRequest
		).getParameter(
			SearchPortletParameterNames.KEYWORDS
		);
	}

	private void _setUpSearchContextFactory() throws Exception {
		Mockito.doReturn(
			new SearchContext()
		).when(
			searchContextFactory
		).getSearchContext(
			Mockito.any(), Mockito.any(), Mockito.anyLong(), Mockito.any(),
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyLong(),
			Mockito.any(), Mockito.anyLong()
		);
	}

	private void _setUpSearcher() throws Exception {
		Mockito.doReturn(
			Mockito.mock(Hits.class)
		).when(
			searchResponse
		).withHitsGet(
			Mockito.any()
		);

		Mockito.doReturn(
			searchResponse
		).when(
			searcher
		).search(
			Mockito.any()
		);
	}

	private void _setUpSearchResponseBuilderFactory() {
		Mockito.doReturn(
			searchResponseBuilder
		).when(
			searchResponseBuilderFactory
		).builder(
			Mockito.any()
		);

		Mockito.doReturn(
			searchResponse
		).when(
			searchResponseBuilder
		).build();
	}

	private static final MockedStatic<FrameworkUtil>
		_frameworkUtilMockedStatic = Mockito.mockStatic(FrameworkUtil.class);

}