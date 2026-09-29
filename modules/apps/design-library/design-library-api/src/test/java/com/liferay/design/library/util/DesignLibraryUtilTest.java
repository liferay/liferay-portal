/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.design.library.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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
 * @author Lourdes Fernández Besada
 * @author Georgel Pop
 * @author Javier Moral
 */
public class DesignLibraryUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			DesignLibraryUtil.class, "_depotEntryLocalServiceSnapshot",
			new Snapshot<DepotEntryLocalService>(
				DesignLibraryUtil.class, DepotEntryLocalService.class) {

				@Override
				public DepotEntryLocalService get() {
					return _depotEntryLocalService;
				}

			});
	}

	@After
	public void tearDown() {
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

		Mockito.when(
			_depotEntryLocalService.getGroupConnectedDepotEntries(
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
	@TestInfo("LPD-105566")
	public void testIsConnectedDesignLibraryGroupId() throws Exception {
		long companyId = RandomTestUtil.randomLong();

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")
		).thenReturn(
			true
		);

		long designLibraryGroupId = RandomTestUtil.randomLong();

		List<DepotEntry> depotEntries = Collections.singletonList(
			_getDepotEntry(designLibraryGroupId));

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_depotEntryLocalService.getGroupConnectedDepotEntries(
				groupId, DepotConstants.TYPE_DESIGN_LIBRARY, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS)
		).thenReturn(
			depotEntries
		);

		Assert.assertFalse(
			DesignLibraryUtil.isConnectedDesignLibraryGroupId(
				companyId, RandomTestUtil.randomLong(), groupId));
		Assert.assertTrue(
			DesignLibraryUtil.isConnectedDesignLibraryGroupId(
				companyId, designLibraryGroupId, groupId));
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

		Mockito.when(
			_depotEntryLocalService.fetchGroupDepotEntry(group.getGroupId())
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

		Mockito.when(
			_depotEntryLocalService.fetchGroupDepotEntry(group.getGroupId())
		).thenReturn(
			depotEntry
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(group));

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_DESIGN_LIBRARY
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(group));

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			depotEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")
		).thenReturn(
			true
		);

		Assert.assertTrue(DesignLibraryUtil.isDesignLibraryScope(group));
	}

	@Test
	public void testIsDesignLibraryScopeWithGroupId() {
		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId)
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

		Mockito.when(
			_depotEntryLocalService.fetchGroupDepotEntry(groupId)
		).thenReturn(
			depotEntry
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(groupId));

		Mockito.when(
			depotEntry.getType()
		).thenReturn(
			DepotConstants.TYPE_DESIGN_LIBRARY
		);

		Assert.assertFalse(DesignLibraryUtil.isDesignLibraryScope(groupId));

		long companyId = RandomTestUtil.randomLong();

		Mockito.when(
			depotEntry.getCompanyId()
		).thenReturn(
			companyId
		);

		_featureFlagManagerUtilMockedStatic.when(
			() -> FeatureFlagManagerUtil.isEnabled(companyId, "LPD-57283")
		).thenReturn(
			true
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

	private final DepotEntryLocalService _depotEntryLocalService = Mockito.mock(
		DepotEntryLocalService.class);
	private final MockedStatic<FeatureFlagManagerUtil>
		_featureFlagManagerUtilMockedStatic = Mockito.mockStatic(
			FeatureFlagManagerUtil.class);

}