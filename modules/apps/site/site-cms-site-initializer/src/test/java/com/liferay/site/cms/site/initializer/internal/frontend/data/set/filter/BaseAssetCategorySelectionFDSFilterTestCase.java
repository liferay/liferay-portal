/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.frontend.data.set.filter.SelectionFDSFilterItem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Fábio Alves
 */
public abstract class BaseAssetCategorySelectionFDSFilterTestCase {

	@Before
	public void setUp() throws Exception {
		_baseAssetCategorySelectionFDSFilter =
			getBaseAssetCategorySelectionFDSFilter();

		_setUpAssetCategoryLocalService();
		_setUpAssetVocabularyLocalService();
		_setUpGroupLocalService();
	}

	@Test
	public void testGetSelectionFDSFilterItems() {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(_COMPANY_ID)) {

			List<SelectionFDSFilterItem> selectionFDSFilterItems =
				_baseAssetCategorySelectionFDSFilter.getSelectionFDSFilterItems(
					_locale);

			Assert.assertEquals(
				selectionFDSFilterItems.toString(), 1,
				selectionFDSFilterItems.size());

			SelectionFDSFilterItem selectionFDSFilterItem =
				selectionFDSFilterItems.get(0);

			Assert.assertEquals(
				_ASSET_CATEGORY_TITLE, selectionFDSFilterItem.getLabel());
			Assert.assertEquals(
				_ASSET_CATEGORY_ID, selectionFDSFilterItem.getValue());
		}
	}

	protected abstract String getAssetVocabularyExternalReferenceCode();

	protected abstract BaseAssetCategorySelectionFDSFilter
		getBaseAssetCategorySelectionFDSFilter();

	private void _setUpAssetCategoryLocalService() {
		Mockito.when(
			_assetCategory.getCategoryId()
		).thenReturn(
			_ASSET_CATEGORY_ID
		);

		Mockito.when(
			_assetCategory.getTitle(_locale)
		).thenReturn(
			_ASSET_CATEGORY_TITLE
		);

		Mockito.when(
			_assetCategoryLocalService.getVocabularyCategories(
				Mockito.eq(_ASSET_VOCABULARY_ID), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.any())
		).thenReturn(
			List.of(_assetCategory)
		);

		ReflectionTestUtil.setFieldValue(
			_baseAssetCategorySelectionFDSFilter, "assetCategoryLocalService",
			_assetCategoryLocalService);
	}

	private void _setUpAssetVocabularyLocalService() {
		Mockito.when(
			_assetVocabulary.getVocabularyId()
		).thenReturn(
			_ASSET_VOCABULARY_ID
		);

		Mockito.when(
			_assetVocabularyLocalService.
				fetchAssetVocabularyByExternalReferenceCode(
					getAssetVocabularyExternalReferenceCode(), _GROUP_ID)
		).thenReturn(
			_assetVocabulary
		);

		ReflectionTestUtil.setFieldValue(
			_baseAssetCategorySelectionFDSFilter, "assetVocabularyLocalService",
			_assetVocabularyLocalService);
	}

	private void _setUpGroupLocalService() {
		Mockito.when(
			_group.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_groupLocalService.fetchGroup(_COMPANY_ID, GroupConstants.CMS)
		).thenReturn(
			_group
		);

		ReflectionTestUtil.setFieldValue(
			_baseAssetCategorySelectionFDSFilter, "groupLocalService",
			_groupLocalService);
	}

	private static final long _ASSET_CATEGORY_ID = RandomTestUtil.randomLong();

	private static final String _ASSET_CATEGORY_TITLE =
		RandomTestUtil.randomString();

	private static final long _ASSET_VOCABULARY_ID =
		RandomTestUtil.randomLong();

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private final AssetCategory _assetCategory = Mockito.mock(
		AssetCategory.class);
	private final AssetCategoryLocalService _assetCategoryLocalService =
		Mockito.mock(AssetCategoryLocalService.class);
	private final AssetVocabulary _assetVocabulary = Mockito.mock(
		AssetVocabulary.class);
	private final AssetVocabularyLocalService _assetVocabularyLocalService =
		Mockito.mock(AssetVocabularyLocalService.class);
	private BaseAssetCategorySelectionFDSFilter
		_baseAssetCategorySelectionFDSFilter;
	private final Group _group = Mockito.mock(Group.class);
	private final GroupLocalService _groupLocalService = Mockito.mock(
		GroupLocalService.class);
	private final Locale _locale = LocaleUtil.US;

}