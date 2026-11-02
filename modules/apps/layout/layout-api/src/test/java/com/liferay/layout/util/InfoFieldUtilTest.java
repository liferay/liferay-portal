/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.info.field.InfoField;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.LayoutPageTemplateStructureLocalServiceUtil;
import com.liferay.layout.util.structure.LayoutStructure;
import com.liferay.layout.util.structure.LayoutStructureItem;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Aswin Narayanadas
 */
public class InfoFieldUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_localeUtilMockedStatic.when(
			LocaleUtil::getSiteDefault
		).thenReturn(
			LocaleUtil.US
		);

		_layout = Mockito.mock(Layout.class);

		Mockito.when(
			_layout.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_layout.getPlid()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_layout.isTypeContent()
		).thenReturn(
			true
		);

		_layoutPageTemplateStructure = Mockito.mock(
			LayoutPageTemplateStructure.class);

		_layoutPageTemplateStructureLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateStructureLocalServiceUtil.
					fetchLayoutPageTemplateStructure(
						_layout.getGroupId(), _layout.getPlid())
		).thenReturn(
			_layoutPageTemplateStructure
		);
	}

	@After
	public void tearDown() {
		_fragmentEntryLinkLocalServiceUtilMockedStatic.close();
		_layoutPageTemplateStructureLocalServiceUtilMockedStatic.close();
		_localeUtilMockedStatic.close();
	}

	@Test
	public void testForEachInfoFieldAppendsFragmentsMissingFromTree()
		throws Exception {

		long fragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId2 = RandomTestUtil.randomLong();
		long missingFragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long missingFragmentEntryLinkId2 = RandomTestUtil.randomLong();
		long segmentsExperienceId = RandomTestUtil.randomLong();

		_mockFragmentEntryLinks(
			segmentsExperienceId,
			Arrays.asList(
				_createFragmentEntryLink(missingFragmentEntryLinkId1),
				_createFragmentEntryLink(fragmentEntryLinkId2),
				_createFragmentEntryLink(missingFragmentEntryLinkId2),
				_createFragmentEntryLink(fragmentEntryLinkId1)));
		_mockLayoutStructureData(
			segmentsExperienceId,
			_createFlatLayoutStructure(
				fragmentEntryLinkId1, fragmentEntryLinkId2));

		Assert.assertEquals(
			Arrays.asList(
				fragmentEntryLinkId1, fragmentEntryLinkId2,
				missingFragmentEntryLinkId1, missingFragmentEntryLinkId2),
			_collectFragmentEntryLinkIds(segmentsExperienceId));
	}

	@Test
	public void testForEachInfoFieldExcludesDeletedFragmentEntryLinks()
		throws Exception {

		long segmentsExperienceId = RandomTestUtil.randomLong();

		_mockFragmentEntryLinks(segmentsExperienceId, Collections.emptyList());

		InfoFieldUtil.forEachInfoField(
			null, _layout, segmentsExperienceId,
			(infoField, type, unsafeSupplier) -> {
			});

		_fragmentEntryLinkLocalServiceUtilMockedStatic.verify(
			() ->
				FragmentEntryLinkLocalServiceUtil.
					getFragmentEntryLinksBySegmentsExperienceId(
						_layout.getGroupId(), segmentsExperienceId,
						_layout.getPlid(), false));
	}

	@Test
	public void testForEachInfoFieldOrdersNestedFragmentsByTree()
		throws Exception {

		long fragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId2 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId3 = RandomTestUtil.randomLong();
		long segmentsExperienceId = RandomTestUtil.randomLong();

		LayoutStructure layoutStructure = LayoutStructure.of(StringPool.BLANK);

		layoutStructure.addRootLayoutStructureItem();

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId1, layoutStructure.getMainItemId(), -1);

		LayoutStructureItem containerLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 1);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId2, containerLayoutStructureItem.getItemId(), -1);

		layoutStructure.addFragmentStyledLayoutStructureItem(
			fragmentEntryLinkId3, layoutStructure.getMainItemId(), -1);

		_mockFragmentEntryLinks(
			segmentsExperienceId,
			Arrays.asList(
				_createFragmentEntryLink(fragmentEntryLinkId3),
				_createFragmentEntryLink(fragmentEntryLinkId2),
				_createFragmentEntryLink(fragmentEntryLinkId1)));
		_mockLayoutStructureData(segmentsExperienceId, layoutStructure);

		Assert.assertEquals(
			Arrays.asList(
				fragmentEntryLinkId1, fragmentEntryLinkId2,
				fragmentEntryLinkId3),
			_collectFragmentEntryLinkIds(segmentsExperienceId));
	}

	@Test
	public void testForEachInfoFieldOrdersSiblingFragmentsByTree()
		throws Exception {

		long fragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId2 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId3 = RandomTestUtil.randomLong();
		long segmentsExperienceId = RandomTestUtil.randomLong();

		_mockFragmentEntryLinks(
			segmentsExperienceId,
			Arrays.asList(
				_createFragmentEntryLink(fragmentEntryLinkId3),
				_createFragmentEntryLink(fragmentEntryLinkId1),
				_createFragmentEntryLink(fragmentEntryLinkId2)));
		_mockLayoutStructureData(
			segmentsExperienceId,
			_createFlatLayoutStructure(
				fragmentEntryLinkId1, fragmentEntryLinkId2,
				fragmentEntryLinkId3));

		Assert.assertEquals(
			Arrays.asList(
				fragmentEntryLinkId1, fragmentEntryLinkId2,
				fragmentEntryLinkId3),
			_collectFragmentEntryLinkIds(segmentsExperienceId));
	}

	@Test
	public void testForEachInfoFieldWhenDataIsBlank() throws Exception {
		long fragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId2 = RandomTestUtil.randomLong();
		long segmentsExperienceId = RandomTestUtil.randomLong();

		_mockFragmentEntryLinks(
			segmentsExperienceId,
			Arrays.asList(
				_createFragmentEntryLink(fragmentEntryLinkId1),
				_createFragmentEntryLink(fragmentEntryLinkId2)));

		Mockito.when(
			_layoutPageTemplateStructure.getData(segmentsExperienceId)
		).thenReturn(
			StringPool.BLANK
		);

		Assert.assertEquals(
			Arrays.asList(fragmentEntryLinkId1, fragmentEntryLinkId2),
			_collectFragmentEntryLinkIds(segmentsExperienceId));
	}

	@Test
	public void testForEachInfoFieldWhenInputSizeIsLessThanTwo()
		throws Exception {

		long fragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long segmentsExperienceId = RandomTestUtil.randomLong();

		_mockFragmentEntryLinks(
			segmentsExperienceId,
			Collections.singletonList(
				_createFragmentEntryLink(fragmentEntryLinkId1)));

		Assert.assertEquals(
			Collections.singletonList(fragmentEntryLinkId1),
			_collectFragmentEntryLinkIds(segmentsExperienceId));

		_layoutPageTemplateStructureLocalServiceUtilMockedStatic.
			verifyNoInteractions();
	}

	@Test
	public void testForEachInfoFieldWhenLayoutIsNotContent() throws Exception {
		Mockito.when(
			_layout.isTypeContent()
		).thenReturn(
			false
		);

		Assert.assertEquals(
			Collections.emptyList(),
			_collectFragmentEntryLinkIds(RandomTestUtil.randomLong()));
	}

	@Test
	public void testForEachInfoFieldWhenLayoutPageTemplateStructureIsNull()
		throws Exception {

		_layoutPageTemplateStructureLocalServiceUtilMockedStatic.when(
			() ->
				LayoutPageTemplateStructureLocalServiceUtil.
					fetchLayoutPageTemplateStructure(
						_layout.getGroupId(), _layout.getPlid())
		).thenReturn(
			null
		);

		long fragmentEntryLinkId1 = RandomTestUtil.randomLong();
		long fragmentEntryLinkId2 = RandomTestUtil.randomLong();
		long segmentsExperienceId = RandomTestUtil.randomLong();

		_mockFragmentEntryLinks(
			segmentsExperienceId,
			Arrays.asList(
				_createFragmentEntryLink(fragmentEntryLinkId1),
				_createFragmentEntryLink(fragmentEntryLinkId2)));

		Assert.assertEquals(
			Arrays.asList(fragmentEntryLinkId1, fragmentEntryLinkId2),
			_collectFragmentEntryLinkIds(segmentsExperienceId));
	}

	private List<Long> _collectFragmentEntryLinkIds(long segmentsExperienceId)
		throws Exception {

		List<Long> fragmentEntryLinkIds = new ArrayList<>();

		InfoFieldUtil.forEachInfoField(
			null, _layout, segmentsExperienceId,
			(infoField, type, unsafeSupplier) -> fragmentEntryLinkIds.add(
				_parseFragmentEntryLinkId(infoField)));

		return fragmentEntryLinkIds;
	}

	private LayoutStructure _createFlatLayoutStructure(
		long... fragmentEntryLinkIds) {

		LayoutStructure layoutStructure = LayoutStructure.of(StringPool.BLANK);

		layoutStructure.addRootLayoutStructureItem();

		for (long fragmentEntryLinkId : fragmentEntryLinkIds) {
			layoutStructure.addFragmentStyledLayoutStructureItem(
				fragmentEntryLinkId, layoutStructure.getMainItemId(), -1);
		}

		return layoutStructure;
	}

	private FragmentEntryLink _createFragmentEntryLink(
		long fragmentEntryLinkId) {

		FragmentEntryLink fragmentEntryLink = Mockito.mock(
			FragmentEntryLink.class);

		Mockito.when(
			fragmentEntryLink.getFragmentEntryLinkId()
		).thenReturn(
			fragmentEntryLinkId
		);

		Mockito.when(
			fragmentEntryLink.getHtml()
		).thenReturn(
			"<div data-lfr-editable-id=\"element-text\" " +
				"data-lfr-editable-type=\"text\"></div>"
		);

		return fragmentEntryLink;
	}

	private void _mockFragmentEntryLinks(
		long segmentsExperienceId, List<FragmentEntryLink> fragmentEntryLinks) {

		_fragmentEntryLinkLocalServiceUtilMockedStatic.when(
			() ->
				FragmentEntryLinkLocalServiceUtil.
					getFragmentEntryLinksBySegmentsExperienceId(
						_layout.getGroupId(), segmentsExperienceId,
						_layout.getPlid())
		).thenReturn(
			fragmentEntryLinks
		);

		_fragmentEntryLinkLocalServiceUtilMockedStatic.when(
			() ->
				FragmentEntryLinkLocalServiceUtil.
					getFragmentEntryLinksBySegmentsExperienceId(
						_layout.getGroupId(), segmentsExperienceId,
						_layout.getPlid(), false)
		).thenReturn(
			fragmentEntryLinks
		);
	}

	private void _mockLayoutStructureData(
		long segmentsExperienceId, LayoutStructure layoutStructure) {

		Mockito.when(
			_layoutPageTemplateStructure.getData(segmentsExperienceId)
		).thenReturn(
			layoutStructure.toString()
		);
	}

	private long _parseFragmentEntryLinkId(InfoField<?> infoField) {
		String name = infoField.getName();

		return GetterUtil.getLong(
			name.substring(0, name.indexOf(StringPool.COLON)));
	}

	private final MockedStatic<FragmentEntryLinkLocalServiceUtil>
		_fragmentEntryLinkLocalServiceUtilMockedStatic = Mockito.mockStatic(
			FragmentEntryLinkLocalServiceUtil.class);
	private Layout _layout;
	private LayoutPageTemplateStructure _layoutPageTemplateStructure;
	private final MockedStatic<LayoutPageTemplateStructureLocalServiceUtil>
		_layoutPageTemplateStructureLocalServiceUtilMockedStatic =
			Mockito.mockStatic(
				LayoutPageTemplateStructureLocalServiceUtil.class);
	private final MockedStatic<LocaleUtil> _localeUtilMockedStatic =
		Mockito.mockStatic(LocaleUtil.class);

}