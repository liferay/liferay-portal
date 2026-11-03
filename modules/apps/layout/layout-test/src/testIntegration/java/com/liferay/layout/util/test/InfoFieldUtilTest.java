/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.fragment.service.FragmentEntryLinkService;
import com.liferay.layout.provider.LayoutStructureProvider;
import com.liferay.layout.test.util.ContentLayoutTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.InfoFieldUtil;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.segments.service.SegmentsExperienceLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Víctor Galán
 */
@RunWith(Arquillian.class)
public class InfoFieldUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testForEachInfoField() throws Exception {
		Group group = GroupTestUtil.addGroup();

		Layout layout = LayoutTestUtil.addTypeContentLayout(group);

		long segmentsExperienceId =
			_segmentsExperienceLocalService.fetchDefaultSegmentsExperienceId(
				layout.getPlid());

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink(
			layout, null, 0, segmentsExperienceId);
		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink(
			layout, null, 1, segmentsExperienceId);

		JSONObject jsonObject = ContentLayoutTestUtil.addItemToLayout(
			"{}", LayoutDataItemTypeConstants.TYPE_CONTAINER, layout,
			_layoutStructureProvider, segmentsExperienceId);

		FragmentEntryLink nestedFragmentEntryLink = _addFragmentEntryLink(
			layout, jsonObject.getString("addedItemId"), 0,
			segmentsExperienceId);

		FragmentEntryLink deletedFragmentEntryLink = _addFragmentEntryLink(
			layout, null, 3, segmentsExperienceId);
		FragmentEntryLink fragmentEntryLink3 = _addFragmentEntryLink(
			layout, null, 0, segmentsExperienceId);

		_fragmentEntryLinkLocalService.updateDeleted(
			TestPropsValues.getUserId(),
			deletedFragmentEntryLink.getFragmentEntryLinkId(), true);

		_fragmentEntryLinkService.addFragmentEntryLink(
			null, layout.getGroupId(), null, null, null, segmentsExperienceId,
			layout.getPlid(), StringPool.BLANK, _HTML, StringPool.BLANK,
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, 0,
			StringPool.BLANK, FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext(
				layout.getGroupId(), TestPropsValues.getUserId()));

		List<String> names = new ArrayList<>();

		InfoFieldUtil.forEachInfoField(
			null, layout, segmentsExperienceId,
			(infoField, type, unsafeSupplier) -> names.add(
				infoField.getName()));

		Assert.assertEquals(
			Arrays.asList(
				_getName(fragmentEntryLink3), _getName(nestedFragmentEntryLink),
				_getName(fragmentEntryLink1), _getName(fragmentEntryLink2)),
			names);
	}

	private FragmentEntryLink _addFragmentEntryLink(
			Layout layout, String parentItemId, int position,
			long segmentsExperienceId)
		throws Exception {

		return ContentLayoutTestUtil.addFragmentEntryLinkToLayout(
			StringPool.BLANK, StringPool.BLANK, StringPool.BLANK, null, null,
			_HTML, StringPool.BLANK, layout, StringPool.BLANK,
			FragmentConstants.TYPE_COMPONENT, parentItemId, position,
			segmentsExperienceId);
	}

	private String _getName(FragmentEntryLink fragmentEntryLink) {
		return fragmentEntryLink.getFragmentEntryLinkId() + ":element-text";
	}

	private static final String _HTML =
		"<div data-lfr-editable-id=\"element-text\" " +
			"data-lfr-editable-type=\"text\"></div>";

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@Inject
	private FragmentEntryLinkService _fragmentEntryLinkService;

	@Inject
	private LayoutStructureProvider _layoutStructureProvider;

	@Inject
	private SegmentsExperienceLocalService _segmentsExperienceLocalService;

}