/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class DesignLibraryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_depotEntryLocalServiceUtilMockedStatic.close();
	}

	@Test
	public void testIsDesignLibraryScope() {
		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(null));

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			false
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(group));

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			group.isDepot()
		).thenReturn(
			true
		);

		_depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
				group.getGroupId())
		).thenReturn(
			null
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(group));

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_ASSET_LIBRARY
		);

		_depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(
				group.getGroupId())
		).thenReturn(
			depotEntry
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(group));

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_DESIGN_LIBRARY
		);

		Assert.assertTrue(DesignLibraryUtil.isDesignLibraryScope(group));
	}

	private final MockedStatic<DepotEntryLocalServiceUtil>
		_depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			DepotEntryLocalServiceUtil.class);

}