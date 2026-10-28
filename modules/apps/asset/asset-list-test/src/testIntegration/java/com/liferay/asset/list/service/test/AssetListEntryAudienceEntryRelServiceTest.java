/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryAudienceEntryRel;
import com.liferay.asset.list.service.AssetListEntryAssetEntryRelLocalService;
import com.liferay.asset.list.service.AssetListEntryAudienceEntryRelLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.asset.test.util.AssetTestUtil;
import com.liferay.asset.test.util.asset.renderer.factory.TestAssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class AssetListEntryAudienceEntryRelServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.asset.list.service"));

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testAddAssetListEntryAudienceEntryRel() throws Exception {
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel1 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel2 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());

		Assert.assertEquals(0, assetListEntryAudienceEntryRel1.getPriority());
		Assert.assertEquals(1, assetListEntryAudienceEntryRel2.getPriority());

		Assert.assertEquals(
			assetListEntryAudienceEntryRel1,
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(),
					assetListEntryAudienceEntryRel1.getAudienceEntryERC()));
	}

	@Test
	public void testDeleteAssetListEntryAudienceEntryRel() throws Exception {
		String audienceEntryERC = RandomTestUtil.randomString();

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, audienceEntryERC);

		_addAssetListEntryAssetEntryRel(StringPool.BLANK);
		_addAssetListEntryAssetEntryRel(audienceEntryERC);

		_assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRel(
				assetListEntryAudienceEntryRel);

		Assert.assertNull(
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(), audienceEntryERC));
		Assert.assertEquals(
			1,
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					_assetListEntry.getAssetListEntryId(), StringPool.BLANK,
					0));
		Assert.assertEquals(
			0,
			_assetListEntryAssetEntryRelLocalService.
				getAssetListEntryAssetEntryRelsCount(
					_assetListEntry.getAssetListEntryId(), audienceEntryERC,
					0));
	}

	@Test
	public void testDeleteAssetListEntryAudienceEntryRelByAudienceEntryERC()
		throws Exception {

		String audienceEntryERC = RandomTestUtil.randomString();

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel1 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, audienceEntryERC);

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel2 =
			_addAssetListEntryAudienceEntryRel(
				assetListEntry, audienceEntryERC);

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel3 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());

		_assetListEntryAudienceEntryRelLocalService.
			deleteAssetListEntryAudienceEntryRelByAudienceEntryERC(
				_group.getCompanyId(), audienceEntryERC);

		Assert.assertNull(
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					assetListEntryAudienceEntryRel1.
						getAssetListEntryAudienceEntryRelId()));
		Assert.assertNull(
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					assetListEntryAudienceEntryRel2.
						getAssetListEntryAudienceEntryRelId()));
		Assert.assertNotNull(
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					assetListEntryAudienceEntryRel3.
						getAssetListEntryAudienceEntryRelId()));
	}

	@Test
	public void testFetchAssetListEntryAudienceEntryRel() throws Exception {
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel1 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel2 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel3 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());

		Assert.assertEquals(
			assetListEntryAudienceEntryRel1,
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(),
					new String[] {
						assetListEntryAudienceEntryRel3.getAudienceEntryERC(),
						assetListEntryAudienceEntryRel1.getAudienceEntryERC()
					}));

		Assert.assertEquals(
			assetListEntryAudienceEntryRel2,
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(),
					new String[] {
						RandomTestUtil.randomString(),
						assetListEntryAudienceEntryRel2.getAudienceEntryERC()
					}));
		Assert.assertNull(
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(), new String[0]));
		Assert.assertNull(
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(),
					new String[] {RandomTestUtil.randomString()}));
	}

	@Test
	public void testGetAssetListEntryAudienceEntryRels() throws Exception {
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel1 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel2 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());

		_addAssetListEntryAudienceEntryRel(
			AssetListTestUtil.addAssetListEntry(_group.getGroupId()),
			RandomTestUtil.randomString());

		List<AssetListEntryAudienceEntryRel> assetListEntryAudienceEntryRels =
			_assetListEntryAudienceEntryRelLocalService.
				getAssetListEntryAudienceEntryRels(
					_assetListEntry.getAssetListEntryId(), QueryUtil.ALL_POS,
					QueryUtil.ALL_POS);

		Assert.assertEquals(
			assetListEntryAudienceEntryRels.toString(), 2,
			assetListEntryAudienceEntryRels.size());
		Assert.assertTrue(
			assetListEntryAudienceEntryRels.contains(
				assetListEntryAudienceEntryRel1));
		Assert.assertTrue(
			assetListEntryAudienceEntryRels.contains(
				assetListEntryAudienceEntryRel2));
	}

	@Test
	public void testGetAssetListEntryAudienceEntryRelsCount() throws Exception {
		Assert.assertEquals(
			0,
			_assetListEntryAudienceEntryRelLocalService.
				getAssetListEntryAudienceEntryRelsCount(
					_assetListEntry.getAssetListEntryId()));

		_addAssetListEntryAudienceEntryRel(
			_assetListEntry, RandomTestUtil.randomString());
		_addAssetListEntryAudienceEntryRel(
			_assetListEntry, RandomTestUtil.randomString());

		Assert.assertEquals(
			2,
			_assetListEntryAudienceEntryRelLocalService.
				getAssetListEntryAudienceEntryRelsCount(
					_assetListEntry.getAssetListEntryId()));
	}

	@Test
	public void testUpdateAssetListEntryAudienceEntryRelTypeSettings()
		throws Exception {

		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());

		String typeSettings = RandomTestUtil.randomString();

		_assetListEntryAudienceEntryRelLocalService.
			updateAssetListEntryAudienceEntryRelTypeSettings(
				_assetListEntry.getAssetListEntryId(),
				assetListEntryAudienceEntryRel.getAudienceEntryERC(),
				typeSettings);

		assetListEntryAudienceEntryRel =
			_assetListEntryAudienceEntryRelLocalService.
				getAssetListEntryAudienceEntryRel(
					assetListEntryAudienceEntryRel.
						getAssetListEntryAudienceEntryRelId());

		Assert.assertEquals(
			typeSettings, assetListEntryAudienceEntryRel.getTypeSettings());
	}

	@Test
	public void testUpdatePriorities() throws Exception {
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel1 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());
		AssetListEntryAudienceEntryRel assetListEntryAudienceEntryRel2 =
			_addAssetListEntryAudienceEntryRel(
				_assetListEntry, RandomTestUtil.randomString());

		_assetListEntryAudienceEntryRelLocalService.updatePriorities(
			new long[] {
				assetListEntryAudienceEntryRel2.
					getAssetListEntryAudienceEntryRelId(),
				assetListEntryAudienceEntryRel1.
					getAssetListEntryAudienceEntryRelId()
			});

		Assert.assertEquals(
			assetListEntryAudienceEntryRel2,
			_assetListEntryAudienceEntryRelLocalService.
				fetchAssetListEntryAudienceEntryRel(
					_assetListEntry.getAssetListEntryId(),
					new String[] {
						assetListEntryAudienceEntryRel1.getAudienceEntryERC(),
						assetListEntryAudienceEntryRel2.getAudienceEntryERC()
					}));
	}

	private void _addAssetListEntryAssetEntryRel(String audienceEntryERC)
		throws Exception {

		AssetEntry assetEntry = AssetTestUtil.addAssetEntry(
			_group.getGroupId(), null,
			TestAssetRendererFactory.class.getName());

		_assetListEntryAssetEntryRelLocalService.addAssetListEntryAssetEntryRel(
			_assetListEntry.getAssetListEntryId(), assetEntry.getEntryId(),
			audienceEntryERC, 0, _serviceContext);
	}

	private AssetListEntryAudienceEntryRel _addAssetListEntryAudienceEntryRel(
			AssetListEntry assetListEntry, String audienceEntryERC)
		throws Exception {

		return _assetListEntryAudienceEntryRelLocalService.
			addAssetListEntryAudienceEntryRel(
				TestPropsValues.getUserId(), _group.getGroupId(),
				assetListEntry.getAssetListEntryId(), audienceEntryERC,
				RandomTestUtil.randomString(), _serviceContext);
	}

	private AssetListEntry _assetListEntry;

	@Inject
	private AssetListEntryAssetEntryRelLocalService
		_assetListEntryAssetEntryRelLocalService;

	@Inject
	private AssetListEntryAudienceEntryRelLocalService
		_assetListEntryAudienceEntryRelLocalService;

	private Group _group;
	private ServiceContext _serviceContext;

}