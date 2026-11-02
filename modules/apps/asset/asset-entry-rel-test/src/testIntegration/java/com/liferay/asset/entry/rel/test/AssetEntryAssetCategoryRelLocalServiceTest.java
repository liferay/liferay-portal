/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.entry.rel.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.entry.rel.model.AssetEntryAssetCategoryRel;
import com.liferay.asset.entry.rel.service.AssetEntryAssetCategoryRelLocalService;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.friendly.url.model.FriendlyURLEntry;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jonathan McCann
 */
@RunWith(Arquillian.class)
public class AssetEntryAssetCategoryRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_initialAssetEntryAssetCategoryRelsCount =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount();
	}

	@Test
	public void testAddAssetEntryAssetCategoryRel() {
		long assetEntryId = RandomTestUtil.randomLong();
		long assetCategoryId = RandomTestUtil.randomLong();

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			_addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());
		Assert.assertEquals(
			assetEntryId, assetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			assetCategoryId, assetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(0, assetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testAddAssetEntryAssetCategoryRelWithPriority() {
		long assetEntryId = RandomTestUtil.randomLong();
		long assetCategoryId = RandomTestUtil.randomLong();
		int priority = RandomTestUtil.randomInt();

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			_addAssetEntryAssetCategoryRel(
				assetEntryId, assetCategoryId, priority);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());
		Assert.assertEquals(
			assetEntryId, assetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			assetCategoryId, assetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(priority, assetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testDeleteAssetEntryAssetCategoryRel() {
		long assetEntryId1 = RandomTestUtil.randomLong();
		long assetCategoryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(assetEntryId1, assetCategoryId);

		long assetEntryId2 = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(assetEntryId2, assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 2,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRel(assetEntryId1, assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetCategoryId(
					assetCategoryId);

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			assetEntryAssetCategoryRels.get(0);

		Assert.assertEquals(
			assetEntryId2, assetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			assetCategoryId, assetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(0, assetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testDeleteAssetEntryAssetCategoryRelByAssetCategoryId() {
		long assetCategoryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(
			RandomTestUtil.randomLong(), assetCategoryId);
		_addAssetEntryAssetCategoryRel(
			RandomTestUtil.randomLong(), assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 2,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRelByAssetCategoryId(assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());
	}

	@Test
	public void testDeleteAssetEntryAssetCategoryRelByAssetEntryId()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		_assetEntry1 = AssetTestUtil.addAssetEntry(_group.getGroupId());

		long assetCategoryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(
			_assetEntry1.getEntryId(), assetCategoryId);

		_assetEntry2 = AssetTestUtil.addAssetEntry(_group.getGroupId());

		_addAssetEntryAssetCategoryRel(
			_assetEntry2.getEntryId(), assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 2,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		_assetEntryAssetCategoryRelLocalService.
			deleteAssetEntryAssetCategoryRelByAssetEntryId(
				_assetEntry1.getEntryId());

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetCategoryId(
					assetCategoryId);

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			assetEntryAssetCategoryRels.get(0);

		Assert.assertEquals(
			_assetEntry2.getEntryId(),
			assetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			assetCategoryId, assetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(0, assetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testGetAssetCategoryPrimaryKeys() {
		long assetEntryId = RandomTestUtil.randomLong();
		long assetCategoryId1 = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId1);

		long assetCategoryId2 = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId2);

		long[] assetCategoryPrimaryKeys =
			_assetEntryAssetCategoryRelLocalService.getAssetCategoryPrimaryKeys(
				assetEntryId);

		Assert.assertEquals(
			Arrays.toString(assetCategoryPrimaryKeys), 2,
			assetCategoryPrimaryKeys.length);

		Assert.assertEquals(assetCategoryId1, assetCategoryPrimaryKeys[0]);
		Assert.assertEquals(assetCategoryId2, assetCategoryPrimaryKeys[1]);
	}

	@Test
	public void testGetAssetEntryAssetCategoryRelsByAssetCategoryId() {
		long assetEntryId = RandomTestUtil.randomLong();
		long assetCategoryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetCategoryId(
					assetCategoryId);

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			assetEntryAssetCategoryRels.get(0);

		Assert.assertEquals(
			assetEntryId, assetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			assetCategoryId, assetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(0, assetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testGetAssetEntryAssetCategoryRelsByAssetEntryId() {
		long assetEntryId = RandomTestUtil.randomLong();
		long assetCategoryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount());

		List<AssetEntryAssetCategoryRel> assetEntryAssetCategoryRels =
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsByAssetEntryId(assetEntryId);

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			assetEntryAssetCategoryRels.get(0);

		Assert.assertEquals(
			assetEntryId, assetEntryAssetCategoryRel.getAssetEntryId());
		Assert.assertEquals(
			assetCategoryId, assetEntryAssetCategoryRel.getAssetCategoryId());
		Assert.assertEquals(0, assetEntryAssetCategoryRel.getPriority());
	}

	@Test
	public void testGetAssetEntryAssetCategoryRelsCount() {
		long assetEntryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(
			assetEntryId, RandomTestUtil.randomLong());

		_addAssetEntryAssetCategoryRel(
			assetEntryId, RandomTestUtil.randomLong());

		Assert.assertEquals(
			2,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCount(assetEntryId));
	}

	@Test
	public void testGetAssetEntryAssetCategoryRelsCountByClassNameId() {
		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null, FriendlyURLEntry.class.getName());
		long assetCategoryId = RandomTestUtil.randomLong();

		_addAssetEntryAssetCategoryRel(
			assetEntry.getEntryId(), assetCategoryId);

		Assert.assertEquals(
			_initialAssetEntryAssetCategoryRelsCount + 1,
			_assetEntryAssetCategoryRelLocalService.
				getAssetEntryAssetCategoryRelsCountByClassNameId(
					assetCategoryId,
					_portal.getClassNameId(FriendlyURLEntry.class)));
	}

	private AssetEntryAssetCategoryRel _addAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId) {

		return _addAssetEntryAssetCategoryRel(assetEntryId, assetCategoryId, 0);
	}

	private AssetEntryAssetCategoryRel _addAssetEntryAssetCategoryRel(
		long assetEntryId, long assetCategoryId, int priority) {

		AssetEntryAssetCategoryRel assetEntryAssetCategoryRel =
			_assetEntryAssetCategoryRelLocalService.
				addAssetEntryAssetCategoryRel(
					assetEntryId, assetCategoryId, priority);

		_assetEntryAssetCategoryRels.add(assetEntryAssetCategoryRel);

		return assetEntryAssetCategoryRel;
	}

	@DeleteAfterTestRun
	private AssetEntry _assetEntry1;

	@DeleteAfterTestRun
	private AssetEntry _assetEntry2;

	@Inject
	private AssetEntryAssetCategoryRelLocalService
		_assetEntryAssetCategoryRelLocalService;

	@DeleteAfterTestRun
	private final List<AssetEntryAssetCategoryRel>
		_assetEntryAssetCategoryRels = new ArrayList<>();

	@DeleteAfterTestRun
	private Group _group;

	private int _initialAssetEntryAssetCategoryRelsCount;

	@Inject
	private Portal _portal;

}