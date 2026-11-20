/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.list.web.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.list.model.AssetListEntry;
import com.liferay.asset.list.model.AssetListEntryUsage;
import com.liferay.asset.list.service.AssetListEntryUsageLocalService;
import com.liferay.asset.list.test.util.AssetListTestUtil;
import com.liferay.fragment.entry.processor.constants.FragmentEntryProcessorConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class FragmentEntryLinkModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testOnAfterCreateWithSameCollectionInTwoFields()
		throws Exception {

		AssetListEntry assetListEntry = AssetListTestUtil.addAssetListEntry(
			_group.getGroupId());

		JSONObject collectionJSONObject = JSONUtil.put(
			"classPK", String.valueOf(assetListEntry.getAssetListEntryId()));

		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		FragmentEntryLink fragmentEntryLink =
			_fragmentEntryLinkLocalService.addFragmentEntryLink(
				null, TestPropsValues.getUserId(), _group.getGroupId(), null,
				RandomTestUtil.randomString(), null,
				_segmentsExperienceLocalService.
					fetchDefaultSegmentsExperienceId(layout.getPlid()),
				layout.getPlid(), StringPool.BLANK, StringPool.BLANK,
				StringPool.BLANK,
				JSONUtil.put(
					"fieldSets",
					JSONUtil.put(
						JSONUtil.put(
							"fields",
							JSONUtil.putAll(
								JSONUtil.put(
									"name", "collection1"
								).put(
									"type", "collectionSelector"
								),
								JSONUtil.put(
									"name", "collection2"
								).put(
									"type", "collectionSelector"
								))))
				).toString(),
				JSONUtil.put(
					FragmentEntryProcessorConstants.
						KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
					JSONUtil.put(
						"collection1", collectionJSONObject
					).put(
						"collection2", collectionJSONObject
					)
				).toString(),
				StringPool.BLANK, 0, StringPool.BLANK, 0,
				ServiceContextTestUtil.getServiceContext(
					_group.getGroupId(), TestPropsValues.getUserId()));

		List<AssetListEntryUsage> assetListEntryUsages =
			_assetListEntryUsageLocalService.getAssetListEntryUsages(
				String.valueOf(fragmentEntryLink.getFragmentEntryLinkId()),
				_portal.getClassNameId(FragmentEntryLink.class),
				layout.getPlid());

		Assert.assertEquals(
			assetListEntryUsages.toString(), 1, assetListEntryUsages.size());

		AssetListEntryUsage assetListEntryUsage = assetListEntryUsages.get(0);

		Assert.assertEquals(
			_portal.getClassNameId(AssetListEntry.class),
			assetListEntryUsage.getClassNameId());
		Assert.assertEquals(
			String.valueOf(assetListEntry.getAssetListEntryId()),
			assetListEntryUsage.getKey());
	}

	@Inject
	private AssetListEntryUsageLocalService _assetListEntryUsageLocalService;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private Portal _portal;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}