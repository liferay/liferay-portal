/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter.category.display.context;

import com.liferay.asset.kernel.service.AssetCategoryService;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyService;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alberto Chaparro
 */
public class FragmentCollectionFilterCategoryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-59838")
	public void testGetAssetCategoryTreeNodeTitleWhereAssetIsNull()
		throws Exception {

		AssetCategoryService assetCategoryService = Mockito.mock(
			AssetCategoryService.class);

		ReflectionTestUtil.setFieldValue(
			AssetCategoryServiceUtil.class, "_service", assetCategoryService);
		Mockito.when(
			assetCategoryService.fetchCategory(1)
		).thenReturn(
			null
		);

		_testGetAssetCategoryTreeNodeTitleNonexisting("Category");

		AssetVocabularyService assetVocabularyService = Mockito.mock(
			AssetVocabularyService.class);

		ReflectionTestUtil.setFieldValue(
			AssetVocabularyServiceUtil.class, "_service",
			assetVocabularyService);
		Mockito.when(
			assetVocabularyService.fetchVocabulary(1)
		).thenReturn(
			null
		);

		_testGetAssetCategoryTreeNodeTitleNonexisting("Vocabulary");
	}

	private void _testGetAssetCategoryTreeNodeTitleNonexisting(
			String assetCategoryTreeNodeType)
		throws Exception {

		FragmentCollectionFilterCategoryDisplayContext
			fragmentCollectionFilterCategoryDisplayContext =
				new FragmentCollectionFilterCategoryDisplayContext(
					null, null, new DefaultFragmentRendererContext(null));

		ReflectionTestUtil.setFieldValue(
			fragmentCollectionFilterCategoryDisplayContext,
			"_assetCategoryTreeNodeId", Long.valueOf(1));
		ReflectionTestUtil.setFieldValue(
			fragmentCollectionFilterCategoryDisplayContext,
			"_assetCategoryTreeNodeType", assetCategoryTreeNodeType);

		Assert.assertEquals(
			StringPool.BLANK,
			fragmentCollectionFilterCategoryDisplayContext.
				getAssetCategoryTreeNodeTitle());
	}

}