/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 * @author Georgel Pop
 */
public class DesignLibraryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@After
	public void tearDown() {
		_depotEntryLocalServiceUtilMockedStatic.close();
		_featureFlagManagerUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-105565")
	public void testGetConnectedDesignLibraryGroupIds() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")
		).thenReturn(
			false
		);

		long groupId = RandomTestUtil.randomLong();

		Assert.assertArrayEquals(
			new long[0],
			DesignLibraryUtil.getConnectedDesignLibraryGroupIds(
				companyId, groupId));

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")
		).thenReturn(
			true
		);

		long designLibraryGroupId1 = RandomTestUtil.randomLong();
		long designLibraryGroupId2 = RandomTestUtil.randomLong();

		List<DepotEntry> depotEntries = Arrays.asList(
			_getDepotEntry(designLibraryGroupId1),
			_getDepotEntry(designLibraryGroupId2));

		_depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.getGroupConnectedDepotEntries(
				groupId, DepotConstants.TYPE_DESIGN_LIBRARY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS)
		).thenReturn(
			depotEntries
		);

		Assert.assertArrayEquals(
			new long[] {designLibraryGroupId1, designLibraryGroupId2},
			DesignLibraryUtil.getConnectedDesignLibraryGroupIds(
				companyId, groupId));
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

	@Test
	public void testIsDesignLibraryScopeWithGroupId() {
		long groupId = RandomTestUtil.randomLong();

		_depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(groupId)
		).thenReturn(
			null
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(groupId));

		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_ASSET_LIBRARY
		);

		_depotEntryLocalServiceUtilMockedStatic.when(
			() -> DepotEntryLocalServiceUtil.fetchGroupDepotEntry(groupId)
		).thenReturn(
			depotEntry
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(groupId));

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_DESIGN_LIBRARY
		);

		Assert.assertTrue(DesignLibraryUtil.isDesignLibraryScope(groupId));
	}

	private DepotEntry _getDepotEntry(long groupId) {
		DepotEntry depotEntry = Mockito.mock(DepotEntry.class);

		Mockito.when(
			depotEntry.getGroupId()
		).thenReturn(
			groupId
		);

		return depotEntry;
	}

	private final MockedStatic<DepotEntryLocalServiceUtil>
		_depotEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			DepotEntryLocalServiceUtil.class);
	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);

}