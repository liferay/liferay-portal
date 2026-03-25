/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.exception.AssetVocabularyGroupRelGroupIdException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyGroupRel;
import com.liferay.asset.kernel.service.AssetVocabularyGroupRelLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

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
				group.getGroupId(), assetVocabularyId);
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
			_assetVocabulary.getVocabularyId(), groupIds);

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
	public void testGetAssetVocabularyGroupRelsCount() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		long[] groupIds = {group1.getGroupId(), group2.getGroupId()};

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			_assetVocabulary.getVocabularyId(), groupIds);

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
		try {
			_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
				_assetVocabulary.getVocabularyId(), new long[0]);

			Assert.fail();
		}
		catch (AssetVocabularyGroupRelGroupIdException
					assetVocabularyGroupRelGroupIdException) {

			Assert.assertNotNull(assetVocabularyGroupRelGroupIdException);
		}

		Group group1 = GroupTestUtil.addGroup();

		_assetVocabularyGroupRelLocalService.setAssetVocabularyGroupRels(
			_assetVocabulary.getVocabularyId(),
			new long[] {group1.getGroupId()});

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
			new long[] {group2.getGroupId()});

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

	private AssetVocabulary _addAssetVocabulary() throws Exception {
		return _assetVocabularyLocalService.addVocabulary(
			TestPropsValues.getUserId(), GroupConstants.DEFAULT_PARENT_GROUP_ID,
			RandomTestUtil.randomString(),
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

	private AssetVocabulary _assetVocabulary;

	@Inject
	private AssetVocabularyGroupRelLocalService
		_assetVocabularyGroupRelLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

}