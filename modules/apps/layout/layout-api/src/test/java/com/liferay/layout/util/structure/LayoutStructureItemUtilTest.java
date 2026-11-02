/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.structure;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Akhash Ramprakash
 */
public class LayoutStructureItemUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetChildrenItemIds() {
		LayoutStructure layoutStructure = LayoutStructure.of(StringPool.BLANK);

		LayoutStructureItem rootLayoutStructureItem =
			layoutStructure.addRootLayoutStructureItem();

		LayoutStructureItem containerLayoutStructureItem =
			layoutStructure.addContainerStyledLayoutStructureItem(
				rootLayoutStructureItem.getItemId(), 0);

		String missingItemId = RandomTestUtil.randomString();

		rootLayoutStructureItem.addChildrenItem(missingItemId);

		LayoutStructureItem fragmentLayoutStructureItem =
			layoutStructure.addFragmentStyledLayoutStructureItem(
				RandomTestUtil.randomLong(),
				rootLayoutStructureItem.getItemId(), 2);

		Assert.assertEquals(
			Arrays.asList(
				containerLayoutStructureItem.getItemId(), missingItemId,
				fragmentLayoutStructureItem.getItemId()),
			LayoutStructureItemUtil.getChildrenItemIds(
				rootLayoutStructureItem.getItemId(), layoutStructure));
	}

}