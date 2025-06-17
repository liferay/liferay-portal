/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.constants.AssetListEntryTypeConstants;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntrySegmentsEntryRel;
import com.liferay.asset.list.service.AssetListEntryLocalService;
import com.liferay.asset.list.service.AssetListEntrySegmentsEntryRelLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Joshua Cords
 */
@RunWith(Arquillian.class)
public class GroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
		_segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			TestPropsValues.getGroupId());
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		AssetListEntry assetListEntry1 = _addAssetListEntry(
			new long[] {_group.getGroupId()});
		AssetListEntry assetListEntry2 = _addAssetListEntry(
			new long[] {_group.getGroupId(), TestPropsValues.getGroupId()});

		GroupLocalServiceUtil.deleteGroup(_group.getGroupId());

		Assert.assertEquals(0, _getGroupId(assetListEntry1));
		Assert.assertEquals(
			TestPropsValues.getGroupId(), _getGroupId(assetListEntry2));
	}

	private AssetListEntry _addAssetListEntry(long[] groupIds)
		throws Exception {

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			TestPropsValues.getGroupId(),
			AssetListEntryTypeConstants.TYPE_DYNAMIC);

		AssetListEntrySegmentsEntryRel assetListEntrySegmentsEntryRel =
			AssetListTestUtil.addAssetListEntrySegmentsEntryRel(
				TestPropsValues.getGroupId(), assetListEntry,
				_segmentsEntry.getSegmentsEntryId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).put(
			"groupIds", StringUtil.merge(groupIds)
		).build();

		assetListEntrySegmentsEntryRel.setTypeSettings(
			unicodeProperties.toString());

		_assetListEntrySegmentsEntryRelLocalService.
			updateAssetListEntrySegmentsEntryRelTypeSettings(
				assetListEntry.getAssetListEntryId(),
				_segmentsEntry.getSegmentsEntryId(),
				unicodeProperties.toString());

		return assetListEntry;
	}

	private long _getGroupId(AssetListEntry assetListEntry) throws Exception {
		assetListEntry = _assetListEntryLocalService.getAssetListEntry(
			assetListEntry.getAssetListEntryId());

		UnicodeProperties unicodeProperties = UnicodePropertiesBuilder.create(
			true
		).fastLoad(
			assetListEntry.getTypeSettings(_segmentsEntry.getSegmentsEntryId())
		).build();

		return GetterUtil.getLong(unicodeProperties.get("groupIds"));
	}

	@Inject
	private AssetListEntryLocalService _assetListEntryLocalService;

	@Inject
	private AssetListEntrySegmentsEntryRelLocalService
		_assetListEntrySegmentsEntryRelLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private SegmentsEntry _segmentsEntry;

}