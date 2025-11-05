/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Eudaldo Alonso
 */
public class LayoutStructureTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testAddLayoutStructureItemAddFragmentStyledLayoutStructureItem() {
		LayoutStructure layoutStructure = LayoutStructure.of(StringPool.BLANK);

		Assert.assertTrue(
			MapUtil.isEmpty(layoutStructure.getFragmentLayoutStructureItems()));
		Assert.assertTrue(
			ListUtil.isEmpty(
				layoutStructure.getFormStyledLayoutStructureItems()));

		FragmentStyledLayoutStructureItem fragmentStyledLayoutStructureItem =
			new FragmentStyledLayoutStructureItem(
				layoutStructure.getMainItemId());

		long fragmentEntryLinkId = RandomTestUtil.randomLong();

		fragmentStyledLayoutStructureItem.setFragmentEntryLinkId(
			fragmentEntryLinkId);

		layoutStructure.addLayoutStructureItem(
			fragmentStyledLayoutStructureItem);

		Assert.assertFalse(
			MapUtil.isEmpty(layoutStructure.getFragmentLayoutStructureItems()));
		Assert.assertNotNull(
			layoutStructure.getLayoutStructureItemByFragmentEntryLinkId(
				fragmentEntryLinkId));
		Assert.assertTrue(
			ListUtil.isEmpty(
				layoutStructure.getFormStyledLayoutStructureItems()));
	}

	@Test
	@TestInfo("LPD-70708")
	public void testAddLayoutStructureItemWithExistingItemId() {
		LayoutStructure layoutStructure = LayoutStructure.of(StringPool.BLANK);

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		List<LayoutStructureItem> layoutStructureItems =
			layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			layoutStructureItems.toString(), 1, layoutStructureItems.size());

		LayoutStructureItem layoutStructureItem1 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 0);

		LayoutStructureItem layoutStructureItem2 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructure.getMainItemId(), 1);

		LayoutStructureItem layoutStructureItem3 =
			layoutStructure.addContainerStyledLayoutStructureItem(
				layoutStructureItem2.getItemId(), 0);

		layoutStructureItems = layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			layoutStructureItems.toString(), 4, layoutStructureItems.size());

		List<String> rootLayoutStructureItemChildrenItemIds =
			rootLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			rootLayoutStructureItemChildrenItemIds.toString(), 2,
			rootLayoutStructureItemChildrenItemIds.size());

		List<String> childrenItemIds =
			layoutStructureItem2.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 1, childrenItemIds.size());

		layoutStructure.addContainerStyledLayoutStructureItem(
			layoutStructureItem1.getItemId(), layoutStructure.getMainItemId(),
			0);
		layoutStructure.addContainerStyledLayoutStructureItem(
			layoutStructureItem3.getItemId(), layoutStructure.getMainItemId(),
			1);

		rootLayoutStructureItemChildrenItemIds =
			rootLayoutStructureItem.getChildrenItemIds();

		Assert.assertEquals(
			rootLayoutStructureItemChildrenItemIds.toString(), 3,
			rootLayoutStructureItemChildrenItemIds.size());

		childrenItemIds = layoutStructureItem2.getChildrenItemIds();

		Assert.assertEquals(
			childrenItemIds.toString(), 0, childrenItemIds.size());

		layoutStructureItems = layoutStructure.getLayoutStructureItems();

		Assert.assertEquals(
			layoutStructureItems.toString(), 4, layoutStructureItems.size());
	}

}