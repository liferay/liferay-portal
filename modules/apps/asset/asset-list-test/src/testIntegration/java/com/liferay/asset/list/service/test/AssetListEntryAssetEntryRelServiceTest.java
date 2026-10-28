/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.exception.AssetListEntryAssetEntryRelAudienceEntryERCAndSegmentsEntryIdException;
import com.liferay.asset.list.exception.AssetListEntryAssetEntryRelPostionException;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAssetEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.persistence.AssetListEntryAssetEntryRelUtil;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.asset.test.util.asset.renderer.factory.TestAssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kyle Miho
 */
@RunWith(Arquillian.class)
public class AssetListEntryAssetEntryRelServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testAddAssetListEntryAssetEntryRel() throws Exception {
		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntryAssetEntryRel assetListEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry, assetListEntry,
				RandomTestUtil.nextLong());

		AssetListEntryAssetEntryRel persistedAssetListEntryRel =
			AssetListEntryAssetEntryRelUtil.findByUUID_G(
				assetListEntryRel.getUuid(), assetListEntryRel.getGroupId());

		_assertSameAssetListEntryAssetEntryRel(
			assetListEntryRel, persistedAssetListEntryRel);

		Assert.assertEquals(
			assetListEntry.getAssetListEntryId(),
			assetListEntryRel.getAssetListEntryId());

		Assert.assertEquals(
			assetEntry.getEntryId(), assetListEntryRel.getAssetEntryId());
	}

	@Test
	public void testAddAssetListEntryAssetEntryRelToPosition()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		long segmentsEntryId = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel assetListEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry, assetListEntry,
				segmentsEntryId, 1);

		AssetListEntryAssetEntryRel persistedAssetListEntryRel =
			AssetListEntryAssetEntryRelUtil.findByA_AEERC_S_P(
				assetListEntryRel.getAssetListEntryId(), StringPool.BLANK,
				segmentsEntryId, 1);

		_assertSameAssetListEntryAssetEntryRel(
			assetListEntryRel, persistedAssetListEntryRel);

		Assert.assertEquals(
			assetListEntry.getAssetListEntryId(),
			assetListEntryRel.getAssetListEntryId());

		Assert.assertEquals(
			assetEntry.getEntryId(), assetListEntryRel.getAssetEntryId());
	}

	@Test
	public void testAddAssetListEntryAssetEntryRelWithAudienceEntryERC()
		throws Exception {

		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		_assetListEntryAssetEntryRelLocalService.addAssetListEntryAssetEntryRel(
			assetListEntry.getAssetListEntryId(), assetEntry1.getEntryId(), 0,
			serviceContext);

		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		String audienceEntryERC = RandomTestUtil.randomString();

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			_assetListEntryAssetEntryRelLocalService.
				addAssetListEntryAssetEntryRel(
					assetListEntry.getAssetListEntryId(),
					assetEntry2.getEntryId(), audienceEntryERC, 0,
					serviceContext);

		Assert.assertEquals(
			audienceEntryERC,
			assetListEntryAssetEntryRel.getAudienceEntryERC());
		Assert.assertEquals(0, assetListEntryAssetEntryRel.getPosition());

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(), 0, QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAssetEntryRels.toString(), 1,
			assetListEntryAssetEntryRels.size());

		assetListEntryAssetEntryRel = assetListEntryAssetEntryRels.get(0);

		Assert.assertEquals(
			assetEntry1.getEntryId(),
			assetListEntryAssetEntryRel.getAssetEntryId());

		assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(), audienceEntryERC, 0,
					QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAssetEntryRels.toString(), 1,
			assetListEntryAssetEntryRels.size());

		assetListEntryAssetEntryRel = assetListEntryAssetEntryRels.get(0);

		Assert.assertEquals(
			assetEntry2.getEntryId(),
			assetListEntryAssetEntryRel.getAssetEntryId());
	}

	@Test(
		expected = AssetListEntryAssetEntryRelAudienceEntryERCAndSegmentsEntryIdException.class
	)
	public void testAddAssetListEntryAssetEntryRelWithAudienceEntryERCAndSegmentsEntryId()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());
		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		_assetListEntryAssetEntryRelLocalService.addAssetListEntryAssetEntryRel(
			assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@Test(expected = AssetListEntryAssetEntryRelPostionException.class)
	public void testAddAssetListEntryAssetEntryRelWrongPosition()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId());

		long segmentsEntryId = RandomTestUtil.nextLong();

		_assetListEntryAssetEntryRelLocalService.addAssetListEntryAssetEntryRel(
			assetListEntry.getAssetListEntryId(), segmentsEntryId,
			assetEntry.getEntryId(), 1, serviceContext);

		_assetListEntryAssetEntryRelLocalService.addAssetListEntryAssetEntryRel(
			assetListEntry.getAssetListEntryId(), segmentsEntryId,
			assetEntry.getEntryId(), serviceContext);
	}

	@Test
	public void testCountAssetListEntryAssetEntryRel() throws Exception {
		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		int originalAssetListEntryAssetEntryRelsCount =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId());

		Assert.assertEquals(0, originalAssetListEntryAssetEntryRelsCount);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry, assetListEntry,
			RandomTestUtil.nextLong());

		int actualAssetListEntryAssetEntryRelsCount =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId());

		Assert.assertEquals(
			originalAssetListEntryAssetEntryRelsCount + 1,
			actualAssetListEntryAssetEntryRelsCount);
	}

	@Test
	public void testDeleteAssetListEntryAssetEntryRelByAssetListEntryId()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry, assetListEntry,
			RandomTestUtil.nextLong());

		_assetListEntryAssetEntryRelLocalService.
			deleteAssetListEntryAssetEntryRelByAssetListEntryId(
				assetListEntry.getAssetListEntryId());

		Assert.assertNull(
			AssetListEntryAssetEntryRelUtil.fetchByAssetListEntryId_First(
				assetListEntry.getAssetListEntryId(), null));
	}

	@Test
	public void testDeleteAssetListEntryAssetEntryRelByPosition()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		long segmentsEntryId = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel assetListEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry, assetListEntry,
				segmentsEntryId, 1);

		_assetListEntryAssetEntryRelLocalService.
			deleteAssetListEntryAssetEntryRel(
				assetListEntry.getAssetListEntryId(), segmentsEntryId, 1);

		Assert.assertNull(
			AssetListEntryAssetEntryRelUtil.fetchByUUID_G(
				assetListEntryRel.getUuid(), assetListEntryRel.getGroupId()));
	}

	@Test
	public void testDeleteAssetListEntryAssetEntryRelWithAudienceEntryERC()
		throws Exception {

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel1 =
			_addAssetListEntryAssetEntryRel(assetListEntry, StringPool.BLANK);

		String audienceEntryERC = RandomTestUtil.randomString();

		_addAssetListEntryAssetEntryRel(assetListEntry, audienceEntryERC);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel2 =
			_addAssetListEntryAssetEntryRel(assetListEntry, audienceEntryERC);

		_assetListEntryAssetEntryRelLocalService.
			deleteAssetListEntryAssetEntryRel(
				assetListEntry.getAssetListEntryId(), audienceEntryERC, 0, 0);

		assetListEntryAssetEntryRel1 =
			AssetListEntryAssetEntryRelUtil.findByPrimaryKey(
				assetListEntryAssetEntryRel1.
					getAssetListEntryAssetEntryRelId());

		Assert.assertEquals(0, assetListEntryAssetEntryRel1.getPosition());

		assetListEntryAssetEntryRel2 =
			AssetListEntryAssetEntryRelUtil.findByPrimaryKey(
				assetListEntryAssetEntryRel2.
					getAssetListEntryAssetEntryRelId());

		Assert.assertEquals(0, assetListEntryAssetEntryRel2.getPosition());

		Assert.assertEquals(
			1,
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					assetListEntry.getAssetListEntryId(), audienceEntryERC, 0));
	}

	@Test
	public void testGetAssetListEntryAssetEntryRels() throws Exception {
		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		long segmentsEntryId = RandomTestUtil.nextLong();

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry1, assetListEntry, segmentsEntryId);

		AssetListTestUtil.addAssetListEntryAssetEntryRel(
			_group.getGroupId(), assetEntry2, assetListEntry, segmentsEntryId);

		List<AssetListEntryAssetEntryRel> assetListEntryAssetEntryRels =
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRels(
					assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			assetListEntryAssetEntryRels.get(0);

		Assert.assertEquals(
			assetListEntryAssetEntryRel.getAssetEntryId(),
			assetEntry1.getEntryId());

		assetListEntryAssetEntryRel = assetListEntryAssetEntryRels.get(1);

		Assert.assertEquals(
			assetListEntryAssetEntryRel.getAssetEntryId(),
			assetEntry2.getEntryId());
	}

	@Test
	public void testMoveAssetListEntryAssetEntryRelToInvalidPosition()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		long segmentsEntryId = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel assetListEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry, assetListEntry,
				segmentsEntryId);

		int currentPosition = assetListEntryRel.getPosition();

		AssetListEntryAssetEntryRel assetListEntryRelNegativePosition =
			_assetListEntryAssetEntryRelLocalService.
				moveAssetListEntryAssetEntryRel(
					assetListEntry.getAssetListEntryId(), segmentsEntryId,
					currentPosition, -1);

		Assert.assertEquals(
			assetListEntryRel.getPosition(),
			assetListEntryRelNegativePosition.getPosition());

		int highIndex = AssetListEntryAssetEntryRelUtil.countByAssetListEntryId(
			assetListEntry.getAssetListEntryId());

		AssetListEntryAssetEntryRel assetListEntryRelHighIndexPosition =
			_assetListEntryAssetEntryRelLocalService.
				moveAssetListEntryAssetEntryRel(
					assetListEntry.getAssetListEntryId(), segmentsEntryId,
					currentPosition, highIndex);

		Assert.assertEquals(
			assetListEntryRel.getPosition(),
			assetListEntryRelHighIndexPosition.getPosition());
	}

	@Test
	public void testMoveAssetListEntryAssetEntryRelWithAudienceEntryERC()
		throws Exception {

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel1 =
			_addAssetListEntryAssetEntryRel(assetListEntry, StringPool.BLANK);

		String audienceEntryERC = RandomTestUtil.randomString();

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel2 =
			_addAssetListEntryAssetEntryRel(assetListEntry, audienceEntryERC);
		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel3 =
			_addAssetListEntryAssetEntryRel(assetListEntry, audienceEntryERC);

		_assetListEntryAssetEntryRelLocalService.
			moveAssetListEntryAssetEntryRel(
				assetListEntry.getAssetListEntryId(), audienceEntryERC, 0, 0,
				1);

		assetListEntryAssetEntryRel1 =
			AssetListEntryAssetEntryRelUtil.findByPrimaryKey(
				assetListEntryAssetEntryRel1.
					getAssetListEntryAssetEntryRelId());

		Assert.assertEquals(0, assetListEntryAssetEntryRel1.getPosition());

		assetListEntryAssetEntryRel2 =
			AssetListEntryAssetEntryRelUtil.findByPrimaryKey(
				assetListEntryAssetEntryRel2.
					getAssetListEntryAssetEntryRelId());

		Assert.assertEquals(1, assetListEntryAssetEntryRel2.getPosition());

		assetListEntryAssetEntryRel3 =
			AssetListEntryAssetEntryRelUtil.findByPrimaryKey(
				assetListEntryAssetEntryRel3.
					getAssetListEntryAssetEntryRelId());

		Assert.assertEquals(0, assetListEntryAssetEntryRel3.getPosition());
	}

	@Test
	public void testUpdateAssetListEntryAssetEntryRel() throws Exception {
		AssetEntry assetEntry1 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry1 = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		long segmentsEntryId = RandomTestUtil.nextLong();

		AssetListEntryAssetEntryRel assetListEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry1, assetListEntry1,
				segmentsEntryId);

		int position = assetListEntryRel.getPosition();

		AssetEntry assetEntry2 = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		AssetListEntry assetListEntry2 = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		_assetListEntryAssetEntryRelLocalService.
			updateAssetListEntryAssetEntryRel(
				assetListEntryRel.getAssetListEntryAssetEntryRelId(),
				assetListEntry2.getAssetListEntryId(), assetEntry2.getEntryId(),
				segmentsEntryId, position + 1);

		assetListEntryRel = AssetListEntryAssetEntryRelUtil.findByPrimaryKey(
			assetListEntryRel.getAssetListEntryAssetEntryRelId());

		Assert.assertEquals(
			assetListEntry2.getAssetListEntryId(),
			assetListEntryRel.getAssetListEntryId());
		Assert.assertEquals(
			assetEntry2.getEntryId(), assetListEntryRel.getAssetEntryId());
		Assert.assertEquals(assetListEntryRel.getPosition(), position + 1);
	}

	@Test(
		expected = AssetListEntryAssetEntryRelAudienceEntryERCAndSegmentsEntryIdException.class
	)
	public void testUpdateAssetListEntryAssetEntryRelWithAudienceEntryERCAndSegmentsEntryId()
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());
		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		AssetListEntryAssetEntryRel assetListEntryAssetEntryRel =
			AssetListTestUtil.addAssetListEntryAssetEntryRel(
				_group.getGroupId(), assetEntry, assetListEntry, 0);

		_assetListEntryAssetEntryRelLocalService.
			updateAssetListEntryAssetEntryRel(
				assetListEntryAssetEntryRel.getAssetListEntryAssetEntryRelId(),
				assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
				assetListEntryAssetEntryRel.getPosition());
	}

	private AssetListEntryAssetEntryRel _addAssetListEntryAssetEntryRel(
			AssetListEntry assetListEntry, String audienceEntryERC)
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		return _assetListEntryAssetEntryRelLocalService.
			addAssetListEntryAssetEntryRel(
				assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
				audienceEntryERC, 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertSameAssetListEntryAssetEntryRel(
		AssetListEntryAssetEntryRel assetListEntryRel1,
		AssetListEntryAssetEntryRel assetListEntryRel2) {

		Assert.assertEquals(
			assetListEntryRel1.getAssetEntryId(),
			assetListEntryRel2.getAssetEntryId());
		Assert.assertEquals(
			assetListEntryRel1.getAssetListEntryId(),
			assetListEntryRel2.getAssetListEntryId());
		Assert.assertEquals(
			assetListEntryRel1.getAssetListEntryAssetEntryRelId(),
			assetListEntryRel2.getAssetListEntryAssetEntryRelId());
		Assert.assertEquals(
			assetListEntryRel1.getUuid(), assetListEntryRel2.getUuid());
		Assert.assertEquals(
			assetListEntryRel1.getPosition(), assetListEntryRel2.getPosition());
		Assert.assertEquals(
			assetListEntryRel1.getSegmentsEntryId(),
			assetListEntryRel2.getSegmentsEntryId());
	}

	@Inject
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@DeleteAfterTestRun
	private Group _group;

}