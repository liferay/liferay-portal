/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.feature.flag.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetCategoryConstants;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.friendly.url.constants.FriendlyURLEntryConstants;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.friendly.url.service.FriendlyURLEntryLocalService;
import com.liferay.layout.display.page.LayoutDisplayPageObjectProvider;
import com.liferay.layout.display.page.LayoutDisplayPageProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class AssetCategoryFriendlyURLFeatureFlagListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testOnValueMigratesLegacyCategoryFriendlyURLs()
		throws Exception {

		AssetVocabulary assetVocabulary = AssetTestUtil.addVocabulary(
			_group.getGroupId(),
			StringUtil.toLowerCase(StringUtil.randomString()));

		String urlTitle1 = StringUtil.toLowerCase(StringUtil.randomString());

		AssetCategory assetCategory1 = _addAssetCategory(
			assetVocabulary.getVocabularyId(),
			AssetCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, urlTitle1);

		String urlTitle2 = StringUtil.toLowerCase(StringUtil.randomString());

		AssetCategory assetCategory2 = _addAssetCategory(
			assetVocabulary.getVocabularyId(), assetCategory1.getCategoryId(),
			urlTitle2);

		long classNameId = _classNameLocalService.getClassNameId(
			AssetCategory.class);

		_addFriendlyURLEntry(assetCategory1, urlTitle1);
		_addFriendlyURLEntry(assetCategory2, urlTitle2);

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-70396"),
					Boolean.TRUE.toString())) {

			Assert.assertEquals(
				FriendlyURLEntryConstants.
					FRIENDLY_URL_ENTRY_PARENT_CLASS_PK_DEFAULT,
				_getParentClassPK(classNameId, assetCategory1.getCategoryId()));
			Assert.assertNull(
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_group.getGroupId(),
					StringBundler.concat(
						assetVocabulary.getName(), StringPool.SLASH,
						urlTitle1)));

			FeatureFlagTestUtil.invokeFeatureFlagListeners(
				_group.getCompanyId(), true, "LPD-70396");

			Assert.assertEquals(
				assetVocabulary.getVocabularyId(),
				_getParentClassPK(classNameId, assetCategory1.getCategoryId()));
			Assert.assertEquals(
				assetCategory1.getCategoryId(),
				_getParentClassPK(classNameId, assetCategory2.getCategoryId()));

			LayoutDisplayPageObjectProvider<?> layoutDisplayPageObjectProvider =
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_group.getGroupId(),
					StringBundler.concat(
						assetVocabulary.getName(), StringPool.SLASH,
						urlTitle1));

			Assert.assertEquals(
				assetCategory1,
				layoutDisplayPageObjectProvider.getDisplayObject());

			layoutDisplayPageObjectProvider =
				_layoutDisplayPageProvider.getLayoutDisplayPageObjectProvider(
					_group.getGroupId(),
					StringBundler.concat(
						assetVocabulary.getName(), StringPool.SLASH, urlTitle1,
						StringPool.SLASH, urlTitle2));

			Assert.assertEquals(
				assetCategory2,
				layoutDisplayPageObjectProvider.getDisplayObject());
		}
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
			new HashMap<>(), assetVocabularyId, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _addFriendlyURLEntry(
			AssetCategory assetCategory, String urlTitle)
		throws Exception {

		long classNameId = _classNameLocalService.getClassNameId(
			AssetCategory.class);

		_friendlyURLEntryLocalService.deleteFriendlyURLEntry(
			assetCategory.getGroupId(), classNameId,
			assetCategory.getCategoryId());

		_friendlyURLEntryLocalService.addFriendlyURLEntry(
			assetCategory.getGroupId(), classNameId,
			assetCategory.getCategoryId(),
			HashMapBuilder.put(
				assetCategory.getDefaultLanguageId(), urlTitle
			).build(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private long _getParentClassPK(long classNameId, long classPK) {
		FriendlyURLEntry friendlyURLEntry =
			_friendlyURLEntryLocalService.fetchMainFriendlyURLEntry(
				classNameId, classPK);

		return friendlyURLEntry.getParentClassPK();
	}

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private FriendlyURLEntryLocalService _friendlyURLEntryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "component.name=com.liferay.asset.categories.internal.layout.display.page.AssetCategoryLayoutDisplayPageProvider"
	)
	private LayoutDisplayPageProvider<?> _layoutDisplayPageProvider;

}