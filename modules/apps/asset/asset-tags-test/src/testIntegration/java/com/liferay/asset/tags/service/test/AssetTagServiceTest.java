/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.asset.kernel.service.AssetTagLocalServiceUtil;
import com.liferay.asset.kernel.service.AssetTagServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Máté Thurzó
 */
@RunWith(Arquillian.class)
public class AssetTagServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDeleteGroupTags() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		int initialTagsCount = AssetTagLocalServiceUtil.getGroupTagsCount(
			_group.getGroupId());

		AssetTagLocalServiceUtil.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);
		AssetTagLocalServiceUtil.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), serviceContext);

		Assert.assertEquals(
			initialTagsCount + 2,
			AssetTagLocalServiceUtil.getGroupTagsCount(_group.getGroupId()));

		AssetTagLocalServiceUtil.deleteGroupTags(_group.getGroupId());

		Assert.assertEquals(
			initialTagsCount,
			AssetTagLocalServiceUtil.getGroupTagsCount(_group.getGroupId()));
	}

	@Test
	public void testGetGroupsTagsWithNoGroupIds() throws Exception {
		_addTag();

		List<AssetTag> assetTags = AssetTagServiceUtil.getGroupsTags(
			new long[0]);

		Assert.assertTrue(assetTags.toString(), assetTags.isEmpty());
	}

	@Test
	public void testGetTagsCountWithNoGroupIds() throws Exception {
		AssetTag assetTag = _addTag();

		Assert.assertEquals(
			0, AssetTagServiceUtil.getTagsCount(new long[0], null));
		Assert.assertEquals(
			0,
			AssetTagServiceUtil.getTagsCount(new long[0], assetTag.getName()));
	}

	@Test
	public void testGetTagsWithNoGroupIds() throws Exception {
		AssetTag assetTag = _addTag();

		List<AssetTag> assetTags = AssetTagServiceUtil.getTags(
			new long[0], null, QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		Assert.assertTrue(assetTags.toString(), assetTags.isEmpty());

		assetTags = AssetTagServiceUtil.getTags(
			new long[0], assetTag.getName(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertTrue(assetTags.toString(), assetTags.isEmpty());
	}

	private AssetTag _addTag() throws Exception {
		return AssetTagLocalServiceUtil.addTag(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	@DeleteAfterTestRun
	private Group _group;

}