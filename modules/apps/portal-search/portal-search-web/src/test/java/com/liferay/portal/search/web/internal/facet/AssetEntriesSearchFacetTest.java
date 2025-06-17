/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.facet;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.internal.asset.SearchableAssetClassNamesProviderImpl;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Adam Brandizzi
 */
public class AssetEntriesSearchFacetTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		assetRendererFactoryRegistryUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		assetRendererFactory1 = Mockito.mock(AssetRendererFactory.class);
		assetRendererFactory2 = Mockito.mock(AssetRendererFactory.class);

		_searchEngineHelper = Mockito.mock(SearchEngineHelper.class);

		assetEntriesSearchFacet = new AssetEntriesSearchFacet();

		ReflectionTestUtil.setFieldValue(
			assetEntriesSearchFacet, "_searchableAssetClassNamesProvider",
			new SearchableAssetClassNamesProviderImpl() {
				{
					searchEngineHelper = _searchEngineHelper;
				}
			});

		_mockAssetRendererFactoryGetClassName(
			assetRendererFactory1, CLASS_NAME_1);
		_mockAssetRendererFactoryIsSearchable(assetRendererFactory1, true);

		_mockAssetRendererFactoryGetClassName(
			assetRendererFactory2, CLASS_NAME_2);
		_mockAssetRendererFactoryIsSearchable(assetRendererFactory2, true);
	}

	@Test
	public void testGetAssetTypes() {
		_mockAssetRendererFactoryRegistry(
			assetRendererFactory1, assetRendererFactory2);

		String[] assetEntryClassNames = {CLASS_NAME_1, CLASS_NAME_2};

		_mockSearchEngineHelperassetEntryClassNames(assetEntryClassNames);

		Assert.assertArrayEquals(
			assetEntryClassNames,
			assetEntriesSearchFacet.getAssetTypes(RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTypesNotInRegistry() {
		_mockAssetRendererFactoryRegistry(assetRendererFactory2);

		String[] assetEntryClassNames = {CLASS_NAME_1, CLASS_NAME_2};

		_mockSearchEngineHelperassetEntryClassNames(assetEntryClassNames);

		Assert.assertArrayEquals(
			new String[] {CLASS_NAME_2},
			assetEntriesSearchFacet.getAssetTypes(RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTypesNotInSearchEngineHelper() {
		_mockAssetRendererFactoryRegistry(
			assetRendererFactory1, assetRendererFactory2);

		String[] assetEntryClassNames = {CLASS_NAME_1};

		_mockSearchEngineHelperassetEntryClassNames(assetEntryClassNames);

		Assert.assertArrayEquals(
			assetEntryClassNames,
			assetEntriesSearchFacet.getAssetTypes(RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTypesNotSearchable() {
		_mockAssetRendererFactoryIsSearchable(assetRendererFactory1, false);

		_mockAssetRendererFactoryRegistry(
			assetRendererFactory1, assetRendererFactory2);

		String[] assetEntryClassNames = {CLASS_NAME_1, CLASS_NAME_2};

		_mockSearchEngineHelperassetEntryClassNames(assetEntryClassNames);

		Assert.assertArrayEquals(
			new String[] {CLASS_NAME_2},
			assetEntriesSearchFacet.getAssetTypes(RandomTestUtil.randomLong()));
	}

	protected static final String CLASS_NAME_1 = "com.liferay.model.Model1";

	protected static final String CLASS_NAME_2 = "com.liferay.model.Model2";

	protected static MockedStatic<AssetRendererFactoryRegistryUtil>
		assetRendererFactoryRegistryUtilMockedStatic;

	protected AssetEntriesSearchFacet assetEntriesSearchFacet;
	protected AssetRendererFactory<?> assetRendererFactory1;
	protected AssetRendererFactory<?> assetRendererFactory2;

	private void _mockAssetRendererFactoryGetClassName(
		AssetRendererFactory<?> assetRendererFactory, String className) {

		Mockito.when(
			assetRendererFactory.getClassName()
		).thenReturn(
			className
		);
	}

	private void _mockAssetRendererFactoryIsSearchable(
		AssetRendererFactory<?> assetRendererFactory, boolean searchable) {

		Mockito.when(
			assetRendererFactory.isSearchable()
		).thenReturn(
			searchable
		);
	}

	private void _mockAssetRendererFactoryRegistry(
		AssetRendererFactory<?>... assetRendererFactories) {

		assetRendererFactoryRegistryUtilMockedStatic.when(
			() -> AssetRendererFactoryRegistryUtil.getAssetRendererFactories(
				Mockito.anyLong())
		).thenReturn(
			Arrays.asList(assetRendererFactories)
		);
	}

	private void _mockSearchEngineHelperassetEntryClassNames(
		String[] assetEntryClassNames) {

		Mockito.when(
			_searchEngineHelper.getEntryClassNames()
		).thenReturn(
			assetEntryClassNames
		);
	}

	private SearchEngineHelper _searchEngineHelper;

}