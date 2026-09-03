/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.util;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalServiceUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.ClassNameLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Tancredi Covioli
 */
public class DSRRoomUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		Mockito.when(
			_permissionChecker.getCompanyId()
		).thenReturn(
			_COMPANY_ID
		);
	}

	@Test
	public void testGetGroupIds() throws Exception {
		try (MockedStatic<ClassNameLocalServiceUtil>
				classNameLocalServiceUtilMockedStatic = Mockito.mockStatic(
					ClassNameLocalServiceUtil.class);
			MockedStatic<GroupServiceUtil> groupServiceUtilMockedStatic =
				Mockito.mockStatic(GroupServiceUtil.class);
			MockedStatic<ObjectDefinitionLocalServiceUtil>
				objectDefinitionLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						ObjectDefinitionLocalServiceUtil.class)) {

			// No object definition

			Assert.assertArrayEquals(
				new String[0],
				DSRRoomUtil.getGroupIds(null, _permissionChecker));

			groupServiceUtilMockedStatic.verifyNoInteractions();

			String className = RandomTestUtil.randomString();

			Mockito.when(
				_objectDefinition.getClassName()
			).thenReturn(
				className
			);

			objectDefinitionLocalServiceUtilMockedStatic.when(
				() ->
					ObjectDefinitionLocalServiceUtil.
						fetchObjectDefinitionByExternalReferenceCode(
							"L_DSR_ROOM", _COMPANY_ID)
			).thenReturn(
				_objectDefinition
			);

			classNameLocalServiceUtilMockedStatic.when(
				() -> ClassNameLocalServiceUtil.getClassNameId(className)
			).thenReturn(
				RandomTestUtil.randomLong()
			);

			// No search results

			_mockGroups(groupServiceUtilMockedStatic);

			Assert.assertArrayEquals(
				new String[0],
				DSRRoomUtil.getGroupIds(null, _permissionChecker));

			// No owned groups

			_mockGroups(groupServiceUtilMockedStatic, 1L, 2L, 3L);

			Assert.assertArrayEquals(
				new String[0],
				DSRRoomUtil.getGroupIds(null, _permissionChecker));

			// Owned groups

			Mockito.when(
				_permissionChecker.isGroupOwner(1L)
			).thenReturn(
				true
			);

			Mockito.when(
				_permissionChecker.isGroupOwner(3L)
			).thenReturn(
				true
			);

			Assert.assertArrayEquals(
				new String[] {"1", "3"},
				DSRRoomUtil.getGroupIds(null, _permissionChecker));

			// Requested group IDs intersected with the owned ones

			Assert.assertArrayEquals(
				new String[] {"1"},
				DSRRoomUtil.getGroupIds(
					new String[] {"1", "2", "999"}, _permissionChecker));

			// Company admin sees every group

			Mockito.when(
				_permissionChecker.isCompanyAdmin()
			).thenReturn(
				true
			);

			Assert.assertArrayEquals(
				new String[] {"1", "2", "3"},
				DSRRoomUtil.getGroupIds(null, _permissionChecker));

			ArgumentCaptor<LinkedHashMap<String, Object>> argumentCaptor =
				ArgumentCaptor.forClass(LinkedHashMap.class);

			groupServiceUtilMockedStatic.verify(
				() -> GroupServiceUtil.search(
					Mockito.eq(_COMPANY_ID), Mockito.any(long[].class),
					Mockito.any(), argumentCaptor.capture(), Mockito.anyInt(),
					Mockito.anyInt(), Mockito.any()),
				Mockito.atLeastOnce());

			LinkedHashMap<String, Object> params = argumentCaptor.getValue();

			Assert.assertEquals(ActionKeys.VIEW, params.get("actionId"));
			Assert.assertEquals(Boolean.TRUE, params.get("active"));
			Assert.assertEquals(Boolean.TRUE, params.get("site"));
		}
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

	private void _mockGroups(
		MockedStatic<GroupServiceUtil> groupServiceUtilMockedStatic,
		long... groupIds) {

		List<Group> groups = new ArrayList<>();

		for (long groupId : groupIds) {
			groups.add(_mockGroup(groupId));
		}

		groupServiceUtilMockedStatic.when(
			() -> GroupServiceUtil.search(
				Mockito.eq(_COMPANY_ID), Mockito.any(long[].class),
				Mockito.any(), Mockito.any(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.any())
		).thenReturn(
			groups
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}