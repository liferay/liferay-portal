/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.helper;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Tancredi Covioli
 */
public class DSRRoomGroupIdsHelperImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		String className = RandomTestUtil.randomString();

		Mockito.when(
			_classNameLocalService.getClassNameId(className)
		).thenReturn(
			RandomTestUtil.randomLong()
		);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			className
		);

		Mockito.when(
			_permissionChecker.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);

		PermissionThreadLocal.setPermissionChecker(_permissionChecker);
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(null);
	}

	@Test
	public void testFilterVisibleGroupIds() throws Exception {

		// No object definition

		Assert.assertArrayEquals(
			new String[0],
			_dsrRoomGroupIdsHelperImpl.filterVisibleGroupIds(null));

		Mockito.verifyNoInteractions(_groupService);

		Mockito.when(
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", _COMPANY_ID)
		).thenReturn(
			_objectDefinition
		);

		// No search results

		_mockGroups();

		Assert.assertArrayEquals(
			new String[0],
			_dsrRoomGroupIdsHelperImpl.filterVisibleGroupIds(null));

		// No owned groups

		_mockGroups(1L, 2L, 3L);

		Assert.assertArrayEquals(
			new String[0],
			_dsrRoomGroupIdsHelperImpl.filterVisibleGroupIds(null));

		// Owned groups

		_mockOwnedGroups(1L, 3L);

		Assert.assertEquals(
			new HashSet<>(Arrays.asList("1", "3")),
			new HashSet<>(
				Arrays.asList(
					_dsrRoomGroupIdsHelperImpl.filterVisibleGroupIds(null))));

		// Requested group IDs intersected with the owned ones

		Assert.assertArrayEquals(
			new String[] {"1"},
			_dsrRoomGroupIdsHelperImpl.filterVisibleGroupIds(
				new String[] {"1", "2", "999"}));

		// Company admin sees every group

		Mockito.when(
			_permissionChecker.isCompanyAdmin()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			new HashSet<>(Arrays.asList("1", "2", "3")),
			new HashSet<>(
				Arrays.asList(
					_dsrRoomGroupIdsHelperImpl.filterVisibleGroupIds(null))));

		ArgumentCaptor<LinkedHashMap<String, Object>> argumentCaptor =
			ArgumentCaptor.forClass(LinkedHashMap.class);

		Mockito.verify(
			_groupService, Mockito.atLeastOnce()
		).search(
			Mockito.eq(_COMPANY_ID), Mockito.any(long[].class), Mockito.any(),
			argumentCaptor.capture(), Mockito.anyInt(), Mockito.anyInt(),
			Mockito.any()
		);

		LinkedHashMap<String, Object> params = argumentCaptor.getValue();

		Assert.assertEquals(ActionKeys.VIEW, params.get("actionId"));
		Assert.assertEquals(Boolean.TRUE, params.get("active"));
		Assert.assertEquals(Boolean.TRUE, params.get("site"));
	}

	private Group _mockGroup(long groupId) {
		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			groupId
		);

		return group;
	}

	private void _mockGroups(long... groupIds) throws Exception {
		List<Group> groups = new ArrayList<>();

		for (long groupId : groupIds) {
			groups.add(_mockGroup(groupId));
		}

		Mockito.when(
			_groupService.search(
				Mockito.eq(_COMPANY_ID), Mockito.any(long[].class),
				Mockito.any(), Mockito.any(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.any())
		).thenReturn(
			groups
		);
	}

	private void _mockOwnedGroups(long... groupIds) {
		for (long groupId : groupIds) {
			Mockito.when(
				_permissionChecker.isGroupOwner(groupId)
			).thenReturn(
				true
			);
		}
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final ClassNameLocalService _classNameLocalService = Mockito.mock(
		ClassNameLocalService.class);

	@InjectMocks
	private DSRRoomGroupIdsHelperImpl _dsrRoomGroupIdsHelperImpl;

	private final GroupService _groupService = Mockito.mock(GroupService.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectDefinitionLocalService _objectDefinitionLocalService =
		Mockito.mock(ObjectDefinitionLocalService.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}