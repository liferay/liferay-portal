/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.layout.display.page.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.InfoItemReference;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class AssetCategoryLayoutDisplayPageProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetLayoutDisplayPageObjectProvider() throws Exception {
		_testGetLayoutDisplayPageObjectProviderERCInfoItemIdentifier();
		_testGetLayoutDisplayPageObjectProviderLocalizedAssetCategory();
		_testGetLayoutDisplayPageObjectProviderNestedAssetCategory();
	}

	@Test
	public void testGetURLTitle() throws Exception {
		_testGetURLTitleWithEncodedAssetVocabularyName(
			"vocabulary name", "vocabulary%20name");
		_testGetURLTitleWithEncodedAssetVocabularyName(
			"vocabulario ñ", "vocabulario%20%C3%B1");
		_testGetURLTitleWithMaximumLengthExceeded();
		_testGetURLTitleWithMaximumLengthNotExceeded();
	}

	private AssetCategory _addAssetCategory(
			long assetVocabularyId, long parentAssetCategoryId, String title)
		throws Exception {

		return _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			parentAssetCategoryId,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), title
			).build(),
			new HashMap<>(), assetVocabularyId, false, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private AssetVocabulary _addAssetVocabulary() throws Exception {
		return AssetTestUtil.addVocabulary(
			_group.getGroupId(),
			StringUtil.toLowerCase(StringUtil.randomString()));
	}

	private LayoutDisplayPageObjectProvider<?>
		_getLayoutDisplayPageObjectProvider(AssetCategory assetCategory) {

		return _layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
			assetCategory.getGroupId(),
			new InfoItemReference(
				AssetCategory.class.getName(),
				new ERCInfoItemIdentifier(
					assetCategory.getExternalReferenceCode())));
	}

	private void _testGetLayoutDisplayPageObjectProviderERCInfoItemIdentifier()
		throws Exception {

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), StringUtil.randomString()
			).build(),
			AssetVocabularyConstants.EMPTY_VOCABULARY_ID, false, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		LayoutDisplayPageObjectProvider layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				assetCategory.getGroupId(),
				new InfoItemReference(
					AssetCategory.class.getName(),
					new ERCInfoItemIdentifier(
						assetCategory.getExternalReferenceCode())));

		Assert.assertEquals(
			assetCategory, layoutDisplayPageObjectProvider.getDisplayObject());

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		Group companyGroup = company.getGroup();

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					AssetCategory.class.getName(),
					new ERCInfoItemIdentifier(
						assetCategory.getExternalReferenceCode(),
						_group.getExternalReferenceCode())));

		Assert.assertEquals(
			assetCategory, layoutDisplayPageObjectProvider.getDisplayObject());

		layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				companyGroup.getGroupId(),
				new InfoItemReference(
					AssetCategory.class.getName(),
					new ERCInfoItemIdentifier(
						assetCategory.getExternalReferenceCode())));

		Assert.assertNull(layoutDisplayPageObjectProvider);
	}

	private void _testGetLayoutDisplayPageObjectProviderLocalizedAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		String spanishURLTitle = StringUtil.toLowerCase(
			StringUtil.randomString());

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			HashMapBuilder.put(
				LocaleUtil.getDefault(),
				StringUtil.toLowerCase(StringUtil.randomString())
			).put(
				LocaleUtil.SPAIN, spanishURLTitle
			).build(),
			new HashMap<>(), assetVocabulary.getVocabularyId(), false, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		String friendlyURL = StringBundler.concat(
			assetVocabulary.getName(), StringPool.SLASH, spanishURLTitle);

		Locale themeDisplayLocale = LocaleThreadLocal.getThemeDisplayLocale();

		try {
			LocaleThreadLocal.setThemeDisplayLocale(LocaleUtil.SPAIN);

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_group.getGroupId(), friendlyURL);

			Assert.assertEquals(
				assetCategory,
				layoutDisplayPageObjectProvider.getDisplayObject());
		}
		finally {
			LocaleThreadLocal.setThemeDisplayLocale(themeDisplayLocale);
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(), friendlyURL);

		Assert.assertEquals(
			assetCategory, layoutDisplayPageObjectProvider.getDisplayObject());
	}

	private void _testGetLayoutDisplayPageObjectProviderNestedAssetCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		String urlTitle1 = StringUtil.toLowerCase(StringUtil.randomString());

		AssetCategory assetCategory1 = _addAssetCategory(
			assetVocabulary.getVocabularyId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, urlTitle1);

		String urlTitle2 = StringUtil.toLowerCase(StringUtil.randomString());

		AssetCategory assetCategory2 = _addAssetCategory(
			assetVocabulary.getVocabularyId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, urlTitle2);

		String urlTitle3 = StringUtil.toLowerCase(StringUtil.randomString());

		AssetCategory assetCategory3 = _addAssetCategory(
			assetVocabulary.getVocabularyId(), assetCategory1.getCategoryId(),
			urlTitle3);

		AssetCategory assetCategory4 = _addAssetCategory(
			assetVocabulary.getVocabularyId(), assetCategory2.getCategoryId(),
			urlTitle3);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider1 =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(),
				StringBundler.concat(
					assetVocabulary.getName(), StringPool.SLASH, urlTitle1,
					StringPool.SLASH, urlTitle3));

		Assert.assertEquals(
			assetCategory3,
			layoutDisplayPageObjectProvider1.getDisplayObject());

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider2 =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(),
				StringBundler.concat(
					assetVocabulary.getName(), StringPool.SLASH, urlTitle2,
					StringPool.SLASH, urlTitle3));

		Assert.assertEquals(
			assetCategory4,
			layoutDisplayPageObjectProvider2.getDisplayObject());
	}

	private void _testGetURLTitleWithEncodedAssetVocabularyName(
			String assetVocabularyName, String encodedAssetVocabularyName)
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(), assetVocabularyName);

		String assetCategoryURLTitle = StringUtil.toLowerCase(
			StringUtil.randomString());

		AssetCategory assetCategory = _addAssetCategory(
			assetVocabulary.getVocabularyId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			assetCategoryURLTitle);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider1 =
			_getLayoutDisplayPageObjectProvider(assetCategory);

		String urlTitle = layoutDisplayPageObjectProvider1.getURLTitle(
			LocaleUtil.getDefault());

		Assert.assertEquals(
			StringBundler.concat(
				encodedAssetVocabularyName, StringPool.SLASH,
				assetCategoryURLTitle),
			urlTitle);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider2 =
			_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
				_group.getGroupId(), urlTitle);

		Assert.assertEquals(
			assetCategory, layoutDisplayPageObjectProvider2.getDisplayObject());
	}

	private void _testGetURLTitleWithMaximumLengthExceeded() throws Exception {
		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		long parentCategoryId =
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID;

		AssetCategory assetCategory = null;

		int count = (Http.URL_MAXIMUM_LENGTH / _CATEGORY_TITLE_LENGTH) + 2;

		for (int i = 0; i < count; i++) {
			assetCategory = _addAssetCategory(
				assetVocabulary.getVocabularyId(), parentCategoryId,
				StringUtil.toLowerCase(
					StringUtil.randomString(_CATEGORY_TITLE_LENGTH)));

			parentCategoryId = assetCategory.getCategoryId();
		}

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(assetCategory);

		Assert.assertEquals(
			String.valueOf(assetCategory.getCategoryId()),
			layoutDisplayPageObjectProvider.getURLTitle(
				LocaleUtil.getDefault()));
	}

	private void _testGetURLTitleWithMaximumLengthNotExceeded()
		throws Exception {

		AssetVocabulary assetVocabulary = _addAssetVocabulary();

		String urlTitle = StringUtil.toLowerCase(StringUtil.randomString());

		AssetCategory assetCategory = _addAssetCategory(
			assetVocabulary.getVocabularyId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, urlTitle);

		LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
			_getLayoutDisplayPageObjectProvider(assetCategory);

		Assert.assertEquals(
			StringBundler.concat(
				assetVocabulary.getName(), StringPool.SLASH, urlTitle),
			layoutDisplayPageObjectProvider.getURLTitle(
				LocaleUtil.getDefault()));
	}

	private static final int _CATEGORY_TITLE_LENGTH = 250;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject(
		filter = "component.name=com.liferay.asset.categories.internal.layout.display.page.AssetCategoryLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<AssetCategory> _layoutDisplayPageProvider;

}