/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetVocabularyGroupRelGroupIdException;
import com.liferay.asset.kernel.exception.SystemVocabularyException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.asset.util.AssetVocabularySettingsHelper;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class AssetVocabularyGroupRelLocalServiceTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_assetVocabulary = _addAssetVocabulary();
	}

	@Test
	public void testGetAssetVocabularyGroupRelsByGroupId() throws Exception {
		AssetVocabulary assetVocabulary1 = _addAssetVocabulary();
		AssetVocabulary assetVocabulary2 = _addAssetVocabulary();

		long[] assetVocabularyIds = {
			assetVocabulary1.getVocabularyId(),
			assetVocabulary2.getVocabularyId()
		};

		Group group = GroupTestUtil.addGroup();

		for (long assetVocabularyId : assetVocabularyIds) {
			_assetVocabularyGroupRelLocalService.addAssetVocabularyGroupRel(
				group.getGroupId(), assetVocabularyId, DepotConstants.TYPE_ANY);
		}

		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByGroupId(group.getGroupId());

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), assetVocabularyIds.length,
			assetVocabularyGroupRels.size());

		for (AssetVocabularyGroupRel assetVocabularyGroupRel :
				assetVocabularyGroupRels) {

			Assert.assertEquals(
				group.getGroupId(), assetVocabularyGroupRel.getGroupId());
			Assert.assertTrue(
				ArrayUtil.contains(
					assetVocabularyIds,
					assetVocabularyGroupRel.getVocabularyId()));
		}

		GroupTestUtil.deleteGroup(group);

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByGroupId(group.getGroupId());

		Assert.assertTrue(assetVocabularyGroupRels.isEmpty());
	}

	@Test
	public void testGetAssetVocabularyGroupRelsByVocabularyId()
		throws Exception {

		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		long[] groupIds = {group1.getGroupId(), group2.getGroupId()};

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			_assetVocabulary.getVocabularyId(), groupIds,
			DepotConstants.TYPE_ANY);

		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyId(
					_assetVocabulary.getVocabularyId());

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), groupIds.length,
			assetVocabularyGroupRels.size());

		for (AssetVocabularyGroupRel assetVocabularyGroupRel :
				assetVocabularyGroupRels) {

			Assert.assertEquals(
				_assetVocabulary.getVocabularyId(),
				assetVocabularyGroupRel.getVocabularyId());
			Assert.assertTrue(
				ArrayUtil.contains(
					groupIds, assetVocabularyGroupRel.getGroupId()));
		}

		_assetVocabularyLocalService.deleteVocabulary(_assetVocabulary);

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyId(
					_assetVocabulary.getVocabularyId());

		Assert.assertTrue(assetVocabularyGroupRels.isEmpty());
	}

	@Test
	@TestInfo("LPD-83676")
	public void testGetAssetVocabularyGroupRelsCount() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		long[] groupIds = {group1.getGroupId(), group2.getGroupId()};

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			_assetVocabulary.getVocabularyId(), groupIds,
			DepotConstants.TYPE_ANY);

		Assert.assertEquals(
			2,
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsCount(
					_assetVocabulary.getVocabularyId()));

		GroupTestUtil.deleteGroup(group2);

		Assert.assertEquals(
			1,
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsCount(
					_assetVocabulary.getVocabularyId()));

		_assetVocabularyLocalService.deleteVocabulary(_assetVocabulary);

		Assert.assertEquals(
			0,
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsCount(
					_assetVocabulary.getVocabularyId()));
	}

	@Test
	public void testSetAssetVocabularyGroupRels() throws Exception {
		_testSetAssetVocabularyGroupRelsReplacingGroups();
		_testSetAssetVocabularyGroupRelsSystem();
		_testSetAssetVocabularyGroupRelsWithoutGroupIds();
	}

	private AssetVocabulary _addAssetVocabulary() throws Exception {
		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext());
	}

	private AssetVocabulary _addSystemVocabulary() throws Exception {
		AssetVocabularySettingsHelper assetVocabularySettingsHelper =
			new AssetVocabularySettingsHelper();

		assetVocabularySettingsHelper.setMultiValued(true);
		assetVocabularySettingsHelper.setSystem(true);

		return _assetVocabularyLocalService.addVocabulary(
			null, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			RandomTestUtil.randomString(), null,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			null, assetVocabularySettingsHelper.toString(),
			AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext());
	}

	private void _assertAssetVocabularyGroupRel(
			AssetVocabularyGroupRel assetVocabularyGroupRel,
			long expectedAssetVocabularyId, long expectedGroupId)
		throws Exception {

		Assert.assertEquals(
			expectedAssetVocabularyId,
			assetVocabularyGroupRel.getVocabularyId());
		Assert.assertEquals(
			expectedGroupId, assetVocabularyGroupRel.getGroupId());
	}

	private void _testSetAssetVocabularyGroupRelsReplacingGroups()
		throws Exception {

		Group group1 = GroupTestUtil.addGroup();

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			_assetVocabulary.getVocabularyId(),
			new long[] {group1.getGroupId()}, DepotConstants.TYPE_ANY);

		List<AssetVocabularyGroupRel> assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyId(
					_assetVocabulary.getVocabularyId());

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 1,
			assetVocabularyGroupRels.size());

		_assertAssetVocabularyGroupRel(
			assetVocabularyGroupRels.get(0), _assetVocabulary.getVocabularyId(),
			group1.getGroupId());

		Group group2 = GroupTestUtil.addGroup();

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			_assetVocabulary.getVocabularyId(),
			new long[] {group2.getGroupId()}, DepotConstants.TYPE_ANY);

		assetVocabularyGroupRels =
			_assetVocabularyGroupRelLocalService.
				getAssetVocabularyGroupRelsByVocabularyId(
					_assetVocabulary.getVocabularyId());

		Assert.assertEquals(
			assetVocabularyGroupRels.toString(), 1,
			assetVocabularyGroupRels.size());

		_assertAssetVocabularyGroupRel(
			assetVocabularyGroupRels.get(0), _assetVocabulary.getVocabularyId(),
			group2.getGroupId());
	}

	private void _testSetAssetVocabularyGroupRelsSystem() throws Exception {
		AssetVocabulary assetVocabulary = _addSystemVocabulary();

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			assetVocabulary.getVocabularyId(),
			new long[] {GroupConstants.GROUP_ID_ALL}, DepotConstants.TYPE_ANY);

		Group group = GroupTestUtil.addGroup();

		AssertUtils.assertFailure(
			SystemVocabularyException.MustNotChangeGroupRels.class,
			StringBundler.concat(
				"Group rels of vocabulary ", assetVocabulary.getVocabularyId(),
				" cannot be changed"),
			() ->
				_assetVocabularyGroupRelLocalService.
					setAssetVocabularyGroupRels(
						assetVocabulary.getVocabularyId(),
						new long[] {group.getGroupId()},
						DepotConstants.TYPE_ANY));
	}

	private void _testSetAssetVocabularyGroupRelsWithoutGroupIds()
		throws Exception {

		try {
			_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
				_assetVocabulary.getVocabularyId(), new long[0],
				DepotConstants.TYPE_ANY);

			Assert.fail();
		}
		catch (AssetVocabularyGroupRelGroupIdException
					assetVocabularyGroupRelGroupIdException) {

			Assert.assertNotNull(assetVocabularyGroupRelGroupIdException);
		}
	}

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}