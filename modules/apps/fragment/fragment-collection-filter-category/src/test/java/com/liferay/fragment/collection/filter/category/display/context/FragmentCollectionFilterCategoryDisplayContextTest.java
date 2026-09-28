/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.collection.filter.category.display.context;

import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryServiceUtil;
import com.liferay.asset.kernel.service.AssetVocabularyServiceUtil;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.DefaultFragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.util.configuration.FragmentEntryConfigurationParser;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import java.util.Locale;
import java.util.Objects;

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
 * @author Alberto Chaparro
 */
public class FragmentCollectionFilterCategoryDisplayContextTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(new PortalImpl());
	}

	@AfterClass
	public static void tearDownClass() {
		_assetCategoryServiceUtilMockedStatic.close();
		_assetVocabularyServiceUtilMockedStatic.close();
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Before
	public void setUp() {
		_assetCategoryServiceUtilMockedStatic.reset();
		_assetVocabularyServiceUtilMockedStatic.reset();
		_groupLocalServiceUtilMockedStatic.reset();
	}

	@Test
	@TestInfo("LPD-69226")
	public void testGetAssetCategoryTreeNodeTitle() throws Exception {
		long categoryTreeNodeId = RandomTestUtil.randomLong();
		String title = RandomTestUtil.randomString();

		_setUpAssetCategoryServiceUtil(categoryTreeNodeId, title);

		_testGetAssetCategoryTreeNodeTitle(
			"Category", categoryTreeNodeId, title);

		_testGetAssetCategoryTreeNodeTitle(
			"Category", RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());
		_testGetAssetCategoryTreeNodeTitle(
			"Category", RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(), null,
			0);

		_setUpAssetVocabularyServiceUtil(title, categoryTreeNodeId);

		_testGetAssetCategoryTreeNodeTitle(
			"Vocabulary", categoryTreeNodeId, title);
		_testGetAssetCategoryTreeNodeTitle(
			"Vocabulary", RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());
		_testGetAssetCategoryTreeNodeTitle(
			"Vocabulary", RandomTestUtil.randomLong(),
			RandomTestUtil.randomLong(), RandomTestUtil.randomString(), null,
			0);
	}

	@Test
	@TestInfo("LPD-59838")
	public void testGetAssetCategoryTreeNodeTitleWhereAssetIsNull()
		throws Exception {

		long categoryTreeNodeId = 1L;

		_setUpAssetCategoryServiceUtil(categoryTreeNodeId, null);

		_testGetAssetCategoryTreeNodeTitle(
			"Category", categoryTreeNodeId, StringPool.BLANK);

		_setUpAssetVocabularyServiceUtil(null, categoryTreeNodeId);

		_testGetAssetCategoryTreeNodeTitle(
			"Vocabulary", categoryTreeNodeId, StringPool.BLANK);
	}

	private void _setUpAssetCategoryServiceUtil(long categoryId, String title)
		throws Exception {

		AssetCategory assetCategory = null;

		if (Validator.isNotNull(title)) {
			assetCategory = Mockito.mock(AssetCategory.class);

			Mockito.when(
				assetCategory.getTitle(Mockito.any(Locale.class))
			).thenReturn(
				title
			);
		}

		Mockito.when(
			AssetCategoryServiceUtil.fetchCategory(categoryId)
		).thenReturn(
			assetCategory
		);
	}

	private void _setUpAssetVocabularyServiceUtil(
			String title, long vocabularyId)
		throws Exception {

		AssetVocabulary assetVocabulary = null;

		if (Validator.isNotNull(title)) {
			assetVocabulary = Mockito.mock(AssetVocabulary.class);

			Mockito.when(
				assetVocabulary.getTitle(Mockito.any(Locale.class))
			).thenReturn(
				title
			);
		}

		Mockito.when(
			AssetVocabularyServiceUtil.fetchVocabulary(vocabularyId)
		).thenReturn(
			assetVocabulary
		);
	}

	private void _testGetAssetCategoryTreeNodeTitle(
			FragmentEntryLink fragmentEntryLink, JSONObject jsonObject,
			String title)
		throws Exception {

		FragmentEntryConfigurationParser fragmentEntryConfigurationParser =
			Mockito.mock(FragmentEntryConfigurationParser.class);

		FragmentRendererContext fragmentRendererContext =
			new DefaultFragmentRendererContext(fragmentEntryLink);

		Mockito.when(
			fragmentEntryConfigurationParser.getFieldValue(
				null, null, fragmentRendererContext.getLocale(), "source")
		).thenReturn(
			jsonObject
		);

		FragmentCollectionFilterCategoryDisplayContext
			fragmentCollectionFilterCategoryDisplayContext =
				new FragmentCollectionFilterCategoryDisplayContext(
					null, fragmentEntryConfigurationParser,
					fragmentRendererContext);

		Assert.assertEquals(
			title,
			fragmentCollectionFilterCategoryDisplayContext.
				getAssetCategoryTreeNodeTitle());
	}

	private void _testGetAssetCategoryTreeNodeTitle(
			String assetCategoryTreeNodeType, long groupId, long companyId,
			String externalReferenceCode, String scopeExternalReferenceCode,
			long scopeGroupId)
		throws Exception {

		long assetCategoryGroupId = groupId;

		if (scopeGroupId > 0) {
			assetCategoryGroupId = scopeGroupId;

			Group group = Mockito.mock(Group.class);

			Mockito.when(
				group.getGroupId()
			).thenReturn(
				scopeGroupId
			);

			Mockito.when(
				GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					scopeExternalReferenceCode, companyId)
			).thenReturn(
				group
			);
		}

		String title = RandomTestUtil.randomString();

		if (Objects.equals(assetCategoryTreeNodeType, "Category")) {
			AssetCategory assetCategory = Mockito.mock(AssetCategory.class);

			Mockito.when(
				assetCategory.getTitle(Mockito.any(Locale.class))
			).thenReturn(
				title
			);

			Mockito.when(
				AssetCategoryServiceUtil.fetchCategoryByExternalReferenceCode(
					externalReferenceCode, assetCategoryGroupId)
			).thenReturn(
				assetCategory
			);
		}
		else if (Objects.equals(assetCategoryTreeNodeType, "Vocabulary")) {
			AssetVocabulary assetVocabulary = Mockito.mock(
				AssetVocabulary.class);

			Mockito.when(
				assetVocabulary.getTitle(Mockito.any(Locale.class))
			).thenReturn(
				title
			);

			Mockito.when(
				AssetVocabularyServiceUtil.
					fetchVocabularyByExternalReferenceCode(
						externalReferenceCode, assetCategoryGroupId)
			).thenReturn(
				assetVocabulary
			);
		}

		FragmentEntryLink fragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			fragmentEntryLink.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			fragmentEntryLink.getCompanyId()
		).thenReturn(
			companyId
		);

		_testGetAssetCategoryTreeNodeTitle(
			fragmentEntryLink,
			JSONUtil.put(
				"categoryTreeNodeType", assetCategoryTreeNodeType
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode", scopeExternalReferenceCode
			),
			title);
	}

	private void _testGetAssetCategoryTreeNodeTitle(
			String assetCategoryTreeNodeType, long categoryTreeNodeId,
			String title)
		throws Exception {

		_testGetAssetCategoryTreeNodeTitle(
			Mockito.mock(FragmentEntryLink.class),
			JSONUtil.put(
				"categoryTreeNodeId", categoryTreeNodeId
			).put(
				"categoryTreeNodeType", assetCategoryTreeNodeType
			),
			title);
	}

	private static final MockedStatic<AssetCategoryServiceUtil>
		_assetCategoryServiceUtilMockedStatic = Mockito.mockStatic(
			AssetCategoryServiceUtil.class);
	private static final MockedStatic<AssetVocabularyServiceUtil>
		_assetVocabularyServiceUtilMockedStatic = Mockito.mockStatic(
			AssetVocabularyServiceUtil.class);
	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

}