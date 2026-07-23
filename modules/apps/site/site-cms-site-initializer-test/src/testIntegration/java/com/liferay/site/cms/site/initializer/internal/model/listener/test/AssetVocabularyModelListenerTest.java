/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.site.cms.site.initializer.test.util.CMSTestUtil;

import java.util.List;

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
public class AssetVocabularyModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = CMSTestUtil.getOrAddGroup(
			AssetVocabularyModelListenerTest.class);
	}

	@Test
	@TestInfo("LPD-93226")
	public void testOnAfterCreate() throws Exception {
		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		List<AssetVocabularyGroupRel> projectAssetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyIdAndDepotEntryType(
					assetVocabulary.getVocabularyId(),
					DepotConstants.TYPE_PROJECT);

		Assert.assertEquals(
			projectAssetVocabularyGroupRels.toString(), 1,
			projectAssetVocabularyGroupRels.size());

		AssetVocabularyGroupRel projectAssetVocabularyGroupRel =
			projectAssetVocabularyGroupRels.get(0);

		Assert.assertEquals(
			GroupConstants.GROUP_ID_ALL,
			projectAssetVocabularyGroupRel.getGroupId());

		List<AssetVocabularyGroupRel> spaceAssetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyIdAndDepotEntryType(
					assetVocabulary.getVocabularyId(),
					DepotConstants.TYPE_SPACE);

		Assert.assertEquals(
			spaceAssetVocabularyGroupRels.toString(), 1,
			spaceAssetVocabularyGroupRels.size());

		AssetVocabularyGroupRel spaceAssetVocabularyGroupRel =
			spaceAssetVocabularyGroupRels.get(0);

		Assert.assertEquals(
			GroupConstants.GROUP_ID_ALL,
			spaceAssetVocabularyGroupRel.getGroupId());
	}

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Group _group;

}