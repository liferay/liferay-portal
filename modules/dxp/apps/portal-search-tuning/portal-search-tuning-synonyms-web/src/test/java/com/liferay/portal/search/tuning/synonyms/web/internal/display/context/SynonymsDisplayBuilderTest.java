/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.synonyms.web.internal.display.context;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.query.Queries;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexName;
import com.liferay.portal.search.tuning.synonyms.index.name.SynonymSetIndexNameBuilder;
import com.liferay.portal.search.tuning.synonyms.web.internal.BaseSynonymsWebTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class SynonymsDisplayBuilderTest extends BaseSynonymsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		setUpPortletPreferencesFactoryUtil();

		_synonymsDisplayBuilder = new SynonymsDisplayBuilder(
			_httpServletRequest, _language, portal, _queries, _renderRequest,
			_renderResponse, searchEngineAdapter, _searchEngineInformation,
			_sorts, _synonymSetIndexNameBuilder);
	}

	@Test
	public void testBuilder() throws Exception {
		setUpHttpServletRequestAttribute(
			_httpServletRequest, WebKeys.THEME_DISPLAY,
			Mockito.mock(ThemeDisplay.class));
		setUpPortal(_httpServletRequest);
		setUpRenderResponse(_renderResponse);
		setUpSearchEngineAdapter(setUpSearchHits("car,automobile"));

		Mockito.when(
			_synonymSetIndexNameBuilder.getSynonymSetIndexName(
				Mockito.anyLong())
		).thenReturn(
			Mockito.mock(SynonymSetIndexName.class)
		);

		SynonymsDisplayContext synonymsDisplayContext =
			_synonymsDisplayBuilder.build();

		Assert.assertNotNull(synonymsDisplayContext.getCreationMenu());
		Assert.assertEquals(1, synonymsDisplayContext.getItemsTotal());
		Assert.assertNotNull(synonymsDisplayContext.getSearchContainer());
		Assert.assertFalse(synonymsDisplayContext.isDisabledManagementBar());
	}

	@Test
	public void testGetDisplayedSynonymSet() {
		_synonymsDisplayBuilder.getDisplayedSynonymSet("car,automobile");

		Assert.assertEquals(
			"car, automobile",
			_synonymsDisplayBuilder.getDisplayedSynonymSet("car,automobile"));
	}

	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final Language _language = Mockito.mock(Language.class);
	private final Queries _queries = Mockito.mock(Queries.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);
	private final SearchEngineInformation _searchEngineInformation =
		Mockito.mock(SearchEngineInformation.class);
	private final Sorts _sorts = Mockito.mock(Sorts.class);
	private SynonymsDisplayBuilder _synonymsDisplayBuilder;
	private final SynonymSetIndexNameBuilder _synonymSetIndexNameBuilder =
		Mockito.mock(SynonymSetIndexNameBuilder.class);

}