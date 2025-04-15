/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.type.facet.portlet.shared.search;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.asset.SearchableAssetClassNamesProvider;
import com.liferay.portal.search.facet.type.TypeFacetSearchContributor;
import com.liferay.portal.search.internal.searcher.SearchRequestBuilderFactoryImpl;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.web.internal.type.facet.portlet.TypeFacetPortletPreferences;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchSettings;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Gustavo Lima
 */
public class TypeFacetPortletSharedSearchContributorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testContribute() {
		_setUpPortletSharedSearchSettings();
		_setUpTypeFacetPortletSharedContributor();

		_testContribute(Collections.emptyList(), Collections.emptyList());

		List<String> entryClassNames1 = Collections.singletonList(
			RandomTestUtil.randomString());
		List<String> entryClassNames2 = Collections.singletonList(
			RandomTestUtil.randomString());

		_testContribute(entryClassNames1, entryClassNames1);
		_testContribute(
			entryClassNames1, entryClassNames1, Collections.emptyList());

		List<String> entryClassNames3 = new ArrayList<>(entryClassNames2);

		Collections.addAll(
			entryClassNames3, entryClassNames1.toArray(new String[1]));

		_testContribute(entryClassNames3, entryClassNames2, entryClassNames1);
	}

	private void _setUpPortletSharedSearchSettings() {
		Mockito.when(
			_portletSharedSearchSettings.getPortletPreferences()
		).thenReturn(
			_portletPreferences
		);

		Mockito.when(
			_portletSharedSearchSettings.getThemeDisplay()
		).thenReturn(
			Mockito.mock(ThemeDisplay.class)
		);
	}

	private void _setUpSearchableAssetTypes(
		List<String> searchableAssetTypes,
		SearchRequestBuilder searchRequestBuilder) {

		Mockito.when(
			_portletSharedSearchSettings.getSearchRequestBuilder()
		).thenReturn(
			searchRequestBuilder
		);

		Mockito.doReturn(
			String.join(StringPool.COMMA, searchableAssetTypes)
		).when(
			_portletPreferences
		).getValue(
			TypeFacetPortletPreferences.PREFERENCE_KEY_ASSET_TYPES,
			StringPool.BLANK
		);
	}

	private SearchRequestBuilder _setUpSearchRequestBuilder(
		Consumer<SearchContext> searchContextConsumer) {

		SearchRequestBuilderFactory searchRequestBuilderFactory =
			new SearchRequestBuilderFactoryImpl();

		SearchRequestBuilder searchRequestBuilder =
			searchRequestBuilderFactory.builder();

		searchRequestBuilder.withSearchContext(searchContextConsumer);

		return searchRequestBuilder;
	}

	private void _setUpTypeFacetPortletSharedContributor() {
		_typeFacetPortletSharedSearchContributor =
			new TypeFacetPortletSharedSearchContributor();

		ReflectionTestUtil.setFieldValue(
			_typeFacetPortletSharedSearchContributor,
			"typeFacetSearchContributor",
			Mockito.mock(TypeFacetSearchContributor.class));

		SearchableAssetClassNamesProvider searchableAssetClassNamesProvider =
			Mockito.mock(SearchableAssetClassNamesProvider.class);

		ReflectionTestUtil.setFieldValue(
			_typeFacetPortletSharedSearchContributor,
			"searchableAssetClassNamesProvider",
			searchableAssetClassNamesProvider);

		Mockito.when(
			searchableAssetClassNamesProvider.getClassNames(Mockito.anyLong())
		).thenReturn(
			new String[0]
		);
	}

	private void _testContribute(
		List<String> expectedEntryClassNames,
		List<String> searchableAssetTypes) {

		_testContribute(
			expectedEntryClassNames, Collections.emptyList(),
			searchableAssetTypes);
	}

	private void _testContribute(
		List<String> expectedEntryClassNames,
		List<String> initialEntryClassNames,
		List<String> searchableAssetTypes) {

		Collections.sort(expectedEntryClassNames);

		SearchRequestBuilder searchRequestBuilder = _setUpSearchRequestBuilder(
			searchContext -> searchContext.setEntryClassNames(
				initialEntryClassNames.toArray(new String[0])));

		_setUpSearchableAssetTypes(searchableAssetTypes, searchRequestBuilder);

		_typeFacetPortletSharedSearchContributor.contribute(
			_portletSharedSearchSettings);

		SearchRequest searchRequest = searchRequestBuilder.build();

		List<String> actualEntryClassNames = new ArrayList<>(
			searchRequest.getEntryClassNames());

		Collections.sort(actualEntryClassNames);

		Assert.assertEquals(expectedEntryClassNames, actualEntryClassNames);
	}

	private static final PortletPreferences _portletPreferences = Mockito.mock(
		PortletPreferences.class);
	private static final PortletSharedSearchSettings
		_portletSharedSearchSettings = Mockito.mock(
			PortletSharedSearchSettings.class);
	private static TypeFacetPortletSharedSearchContributor
		_typeFacetPortletSharedSearchContributor;

}