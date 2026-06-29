/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.scope.Scope;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Lourdes Fernández Besada
 */
public class ItemScopeUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@AfterClass
	public static void tearDownClass() {
		_groupLocalServiceUtilMockedStatic.close();
	}

	@Test
	@TestInfo("LPD-96103")
	public void testGetItemGroupId() {
		Group liveGroup = _getGroup();
		Group stagingGroup = _getGroup();

		_setUpStagingGroup(stagingGroup, liveGroup);

		Group group = _getGroup();

		Assert.assertEquals(
			Long.valueOf(liveGroup.getGroupId()),
			ItemScopeUtil.getItemGroupId(
				_COMPANY_ID, Scope.of(liveGroup), group.getGroupId()));

		Scope scope = Scope.of(stagingGroup);

		Assert.assertEquals(
			Long.valueOf(liveGroup.getGroupId()),
			ItemScopeUtil.getItemGroupId(
				_COMPANY_ID, scope, group.getGroupId()));

		Mockito.when(
			group.isStagingGroup()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			Long.valueOf(stagingGroup.getGroupId()),
			ItemScopeUtil.getItemGroupId(
				_COMPANY_ID, scope, group.getGroupId()));
	}

	@Test
	@TestInfo("LPD-96103")
	public void testGetItemScope() {
		Group group = _getGroup();

		Assert.assertNull(
			ItemScopeUtil.getItemScope(
				_COMPANY_ID, group.getExternalReferenceCode(),
				group.getGroupId()));

		Scope scope = ItemScopeUtil.getItemScope(
			_COMPANY_ID, group.getExternalReferenceCode(),
			RandomTestUtil.randomLong());

		Assert.assertEquals(
			group.getExternalReferenceCode(), scope.getExternalReferenceCode());
		Assert.assertNull(scope.getLiveExternalReferenceCode());

		Group stagingGroup = _getGroup();

		_setUpStagingGroup(stagingGroup, group);

		scope = ItemScopeUtil.getItemScope(
			_COMPANY_ID, stagingGroup.getExternalReferenceCode(),
			RandomTestUtil.randomLong());

		Assert.assertEquals(
			stagingGroup.getExternalReferenceCode(),
			scope.getExternalReferenceCode());
		Assert.assertEquals(
			group.getExternalReferenceCode(),
			scope.getLiveExternalReferenceCode());
	}

	@Test
	@TestInfo("LPD-96103")
	public void testGetItemScopeExternalReferenceCode() throws Exception {
		Group liveGroup = _getGroup();
		Group stagingGroup = _getGroup();

		_setUpStagingGroup(stagingGroup, liveGroup);

		Scope liveGroupScope = Scope.of(liveGroup);

		Group group = _getGroup();

		Assert.assertEquals(
			liveGroup.getExternalReferenceCode(),
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				liveGroupScope, group.getGroupId()));

		Scope stagingGroupScope = Scope.of(stagingGroup);

		Assert.assertEquals(
			liveGroup.getExternalReferenceCode(),
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				stagingGroupScope, group.getGroupId()));

		Mockito.when(
			group.isStagingGroup()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			stagingGroup.getExternalReferenceCode(),
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				stagingGroupScope, group.getGroupId()));

		Assert.assertNull(
			ItemScopeUtil.getItemScopeExternalReferenceCode(
				liveGroupScope, liveGroup.getGroupId()));
	}

	private Group _getGroup() {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getExternalReferenceCode()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroup(group.getGroupId())
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.getGroup(group.getGroupId())
		).thenReturn(
			group
		);

		_groupLocalServiceUtilMockedStatic.when(
			() -> GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
				group.getExternalReferenceCode(), _COMPANY_ID)
		).thenReturn(
			group
		);

		return group;
	}

	private void _setUpStagingGroup(Group group, Group liveGroup) {
		Mockito.when(
			group.getLiveGroup()
		).thenReturn(
			liveGroup
		);

		Mockito.when(
			group.isStagingGroup()
		).thenReturn(
			true
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private static final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);

}