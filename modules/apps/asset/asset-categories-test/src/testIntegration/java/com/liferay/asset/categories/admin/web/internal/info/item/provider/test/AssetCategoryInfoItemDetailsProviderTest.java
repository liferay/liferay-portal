/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.admin.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.info.item.ERCInfoItemIdentifier;
import com.liferay.info.item.GroupKeyInfoItemIdentifier;
import com.liferay.info.item.GroupUrlTitleInfoItemIdentifier;
import com.liferay.info.item.InfoItemDetails;
import com.liferay.info.item.InfoItemReference;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemDetailsProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
public class AssetCategoryInfoItemDetailsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			StringUtil.randomString(),
			AssetVocabularyConstants.EMPTY_VOCABULARY_ID,
			ServiceContextTestUtil.getServiceContext());
	}

	@Test
	public void testGetInfoItemDetails() throws Exception {
		InfoItemDetailsProvider<AssetCategory> infoItemDetailsProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemDetailsProvider.class, AssetCategory.class.getName());

		InfoItemDetails classPKInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(_assetCategory);

		Assert.assertEquals(
			AssetCategory.class.getName(),
			classPKInfoItemDetails.getClassName());
		Assert.assertEquals(
			new InfoItemReference(
				AssetCategory.class.getName(), _assetCategory.getCategoryId()),
			classPKInfoItemDetails.getInfoItemReference());

		InfoItemDetails ercInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), ERCInfoItemIdentifier.class,
				_assetCategory);

		Assert.assertEquals(
			new InfoItemReference(
				AssetCategory.class.getName(),
				new ERCInfoItemIdentifier(
					_assetCategory.getExternalReferenceCode())),
			ercInfoItemDetails.getInfoItemReference());

		InfoItemDetails randomGroupERCInfoItemDetails =
			infoItemDetailsProvider.getInfoItemDetails(
				RandomTestUtil.randomLong(), ERCInfoItemIdentifier.class,
				_assetCategory);

		Assert.assertEquals(
			new InfoItemReference(
				AssetCategory.class.getName(),
				new ERCInfoItemIdentifier(
					_assetCategory.getExternalReferenceCode(),
					_group.getExternalReferenceCode())),
			randomGroupERCInfoItemDetails.getInfoItemReference());

		Assert.assertNull(
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupKeyInfoItemIdentifier.class,
				_assetCategory));
		Assert.assertNull(
			infoItemDetailsProvider.getInfoItemDetails(
				_group.getGroupId(), GroupUrlTitleInfoItemIdentifier.class,
				_assetCategory));
	}

	private AssetCategory _assetCategory;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

}