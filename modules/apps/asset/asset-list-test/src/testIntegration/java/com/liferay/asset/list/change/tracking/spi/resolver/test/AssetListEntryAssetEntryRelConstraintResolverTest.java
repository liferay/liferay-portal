/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.change.tracking.spi.resolver.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.change.tracking.conflict.ConflictInfo;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTProcessLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Noor Najjar
 */
@RunWith(Arquillian.class)
public class AssetListEntryAssetEntryRelConstraintResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		for (int i = 0; i < 4; i++) {
			AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
				_group.getGroupId());

			_assetListEntryAssetEntryRels.add(
				AssetListTestUtil.addAssetListEntryAssetEntryRel(
					_group.getGroupId(), assetEntry, _assetListEntry,
					SegmentsEntryConstants.ID_DEFAULT));
		}

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
	}

	@Test
	public void testResolveConflict() throws Exception {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_assetListEntryLocalService.moveAssetEntrySelection(
				_assetListEntry.getAssetListEntryId(),
				SegmentsEntryConstants.ID_DEFAULT, 0, 1);
		}

		_assetListEntryLocalService.moveAssetEntrySelection(
			_assetListEntry.getAssetListEntryId(),
			SegmentsEntryConstants.ID_DEFAULT, 2, 1);

		_assertResolvedConflictInfos();

		_assertPublish();

		_assertAssetListEntryAssetEntryRels(1, 2, 0, 3);
	}

	@Test
	public void testResolveConflictWithAddedAssetListEntryAssetEntryRel()
		throws Exception {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_assetListEntryLocalService.moveAssetEntrySelection(
				_assetListEntry.getAssetListEntryId(),
				SegmentsEntryConstants.ID_DEFAULT, 0, 1);
		}

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		_assetListEntryAssetEntryRels.add(
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry, _assetListEntry,
				SegmentsEntryConstants.ID_DEFAULT));

		_assetListEntryLocalService.moveAssetEntrySelection(
			_assetListEntry.getAssetListEntryId(),
			SegmentsEntryConstants.ID_DEFAULT, 4, 1);

		_assertResolvedConflictInfos();

		_assertPublish();

		_assertAssetListEntryAssetEntryRels(1, 4, 2, 3, 0);
	}

	@Test
	public void testResolveConflictWithDeletedAssetListEntryAssetEntryRel()
		throws Exception {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_assetListEntryLocalService.deleteAssetEntrySelection(
				_assetListEntry.getAssetListEntryId(),
				SegmentsEntryConstants.ID_DEFAULT, 1);
		}

		_assetListEntryLocalService.moveAssetEntrySelection(
			_assetListEntry.getAssetListEntryId(),
			SegmentsEntryConstants.ID_DEFAULT, 0, 1);

		_assertResolvedConflictInfos();

		_assertPublish();

		_assertAssetListEntryAssetEntryRels(2, 0, 3);
	}

	private void _assertAssetListEntryAssetEntryRels(int... indexes) {
		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					_assetListEntry.getAssetListEntryId(),
					SegmentsEntryConstants.ID_DEFAULT, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAssetEntryRels.toString(), indexes.length,
			assetListEntryAssetEntryRels.size());

		for (int i = 0; i < indexes.length; i++) {
			AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
				assetListEntryAssetEntryRels.get(i);

			AssetListEntryAssetEntryRel expectedAssetListEntryAssetEntryRel =
				_assetListEntryAssetEntryRels.get(indexes[i]);

			Assert.assertEquals(
				expectedAssetListEntryAssetEntryRel.
					getAssetListEntryAssetEntryRelId(),
				assetListEntryAssetEntryRel.getAssetListEntryAssetEntryRelId());

			Assert.assertEquals(i, assetListEntryAssetEntryRel.getPosition());
		}
	}

	private void _assertPublish() throws Exception {
		_ctProcessLocalService.addCTProcess(
			_ctCollection.getUserId(), _ctCollection.getCtCollectionId());

		CTCollection ctCollection = _ctCollectionLocalService.getCTCollection(
			_ctCollection.getCtCollectionId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_APPROVED, ctCollection.getStatus());
	}

	private void _assertResolvedConflictInfos() throws Exception {
		Map<Long, List<ConflictInfo>> conflictInfosMap =
			_ctCollectionLocalService.checkConflicts(_ctCollection);

		Assert.assertFalse(
			conflictInfosMap.toString(), conflictInfosMap.isEmpty());

		for (List<ConflictInfo> conflictInfos : conflictInfosMap.values()) {
			for (ConflictInfo conflictInfo : conflictInfos) {
				Assert.assertTrue(
					conflictInfo.toString(), conflictInfo.isResolved());
			}
		}
	}

	private AssetListEntry _assetListEntry;

	@Inject
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	private final List<AssetListEntryAssetEntryRel>
		_assetListEntryAssetEntryRels = new ArrayList<>();

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTProcessLocalService _ctProcessLocalService;

	@DeleteAfterTestRun
	private Group _group;

}