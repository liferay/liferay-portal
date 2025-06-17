/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.asset;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
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
public class SearchableAssetClassNamesProviderImplTest {

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
		searchableAssetClassNamesProviderImpl =
			new SearchableAssetClassNamesProviderImpl() {
				{
					searchEngineHelper = _searchEngineHelper;
				}
			};

		_mockAssetRendererFactoryGetClassName(
			assetRendererFactory1, CLASS_NAME_1);

		_mockAssetRendererFactoryGetClassName(
			assetRendererFactory2, CLASS_NAME_2);

		_mockAssetRendererFactoryIsSearchable(assetRendererFactory1, true);

		_mockAssetRendererFactoryIsSearchable(assetRendererFactory2, true);
	}

	@Test
	public void testGetAssetTypes() {
		_mockAssetRendererFactoryRegistry(
			assetRendererFactory1, assetRendererFactory2);

		String[] entryClassNames = {CLASS_NAME_1, CLASS_NAME_2};

		_mockSearchEngineHelperEntryClassNames(entryClassNames);

		Assert.assertArrayEquals(
			entryClassNames,
			searchableAssetClassNamesProviderImpl.getClassNames(
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTypesNotInRegistry() {
		_mockAssetRendererFactoryRegistry(assetRendererFactory2);

		String[] entryClassNames = {CLASS_NAME_1, CLASS_NAME_2};

		_mockSearchEngineHelperEntryClassNames(entryClassNames);

		Assert.assertArrayEquals(
			new String[] {CLASS_NAME_2},
			searchableAssetClassNamesProviderImpl.getClassNames(
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTypesNotInSearchEngineHelper() {
		_mockAssetRendererFactoryRegistry(
			assetRendererFactory1, assetRendererFactory2);

		String[] entryClassNames = {CLASS_NAME_1};

		_mockSearchEngineHelperEntryClassNames(entryClassNames);

		Assert.assertArrayEquals(
			entryClassNames,
			searchableAssetClassNamesProviderImpl.getClassNames(
				RandomTestUtil.randomLong()));
	}

	@Test
	public void testGetAssetTypesNotSearchable() {
		_mockAssetRendererFactoryIsSearchable(assetRendererFactory1, false);

		_mockAssetRendererFactoryRegistry(
			assetRendererFactory1, assetRendererFactory2);

		String[] entryClassNames = {CLASS_NAME_1, CLASS_NAME_2};

		_mockSearchEngineHelperEntryClassNames(entryClassNames);

		Assert.assertArrayEquals(
			new String[] {CLASS_NAME_2},
			searchableAssetClassNamesProviderImpl.getClassNames(
				RandomTestUtil.randomLong()));
	}

	protected static final String CLASS_NAME_1 = "com.liferay.model.Model1";

	protected static final String CLASS_NAME_2 = "com.liferay.model.Model2";

	protected static MockedStatic<AssetRendererFactoryRegistryUtil>
		assetRendererFactoryRegistryUtilMockedStatic;

	protected AssetRendererFactory<?> assetRendererFactory1 = Mockito.mock(
		AssetRendererFactory.class);
	protected AssetRendererFactory<?> assetRendererFactory2 = Mockito.mock(
		AssetRendererFactory.class);
	protected SearchableAssetClassNamesProviderImpl
		searchableAssetClassNamesProviderImpl;

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

	private void _mockSearchEngineHelperEntryClassNames(
		String[] entryClassNames) {

		Mockito.when(
			_searchEngineHelper.getEntryClassNames()
		).thenReturn(
			entryClassNames
		);
	}

	private final SearchEngineHelper _searchEngineHelper = Mockito.mock(
		SearchEngineHelper.class);

}