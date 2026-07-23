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
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

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
public class DepotEntryModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = _groupLocalService.getGroup(
			TestPropsValues.getCompanyId(), GroupConstants.CMS);
	}

	@Test
	@TestInfo("LPD-83676")
	public void testOnBeforeRemoveUpdateAssetVocabularyGroupRel()
		throws Exception {

		_testOnBeforeRemoveUpdateAssetVocabularyGroupRel(
			DepotConstants.TYPE_PROJECT);
		_testOnBeforeRemoveUpdateAssetVocabularyGroupRel(
			DepotConstants.TYPE_SPACE);
	}

	private void _testOnBeforeRemoveUpdateAssetVocabularyGroupRel(
			int depotEntryType)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyIdAndDepotEntryType(
					assetVocabulary.getVocabularyId(), depotEntryType);

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 1,
			assetVocabularyGroupRels.size());

		AssetVocabularyGroupRel assetVocabularyGroupRel =
			assetVocabularyGroupRels.get(0);

		Assert.assertEquals(
			GroupConstants.ANY_PARENT_GROUP_ID,
			assetVocabularyGroupRel.getGroupId());

		DepotEntry depotEntry1 = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, depotEntryType,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		DepotEntry depotEntry2 = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, depotEntryType,
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));

		long[] groupIds = {depotEntry1.getGroupId(), depotEntry2.getGroupId()};

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary.getVocabularyId(), groupIds, depotEntryType);

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyIdAndDepotEntryType(
					assetVocabulary.getVocabularyId(), depotEntryType);

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 2,
			assetVocabularyGroupRels.size());

		assetVocabularyGroupRel = assetVocabularyGroupRels.get(0);

		Assert.assertEquals(
			depotEntry1.getGroupId(), assetVocabularyGroupRel.getGroupId());

		assetVocabularyGroupRel = assetVocabularyGroupRels.get(1);

		Assert.assertEquals(
			depotEntry2.getGroupId(), assetVocabularyGroupRel.getGroupId());

		_depotEntryLocalService.deleteDepotEntry(depotEntry1);

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyIdAndDepotEntryType(
					assetVocabulary.getVocabularyId(), depotEntryType);

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 1,
			assetVocabularyGroupRels.size());

		assetVocabularyGroupRel = assetVocabularyGroupRels.get(0);

		Assert.assertEquals(
			depotEntry2.getGroupId(), assetVocabularyGroupRel.getGroupId());

		_depotEntryLocalService.deleteDepotEntry(depotEntry2);

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyIdAndDepotEntryType(
					assetVocabulary.getVocabularyId(), depotEntryType);

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 1,
			assetVocabularyGroupRels.size());

		assetVocabularyGroupRel = assetVocabularyGroupRels.get(0);

		Assert.assertEquals(
			GroupConstants.ANY_PARENT_GROUP_ID,
			assetVocabularyGroupRel.getGroupId());

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByGroupId(depotEntry1.getGroupId());

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 0,
			assetVocabularyGroupRels.size());

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByGroupId(depotEntry2.getGroupId());

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 0,
			assetVocabularyGroupRels.size());
	}

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

}