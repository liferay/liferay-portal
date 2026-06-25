/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.security.permission.resource;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.io.Serializable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Balazs Breier
 */
public class DSRDefaultPermissionObjectEntryModelResourcePermissionTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_dsrDefaultPermissionObjectEntryModelResourcePermission =
			new DSRDefaultPermissionObjectEntryModelResourcePermission(
				_modelResourcePermission, _objectEntryLocalService);

		Mockito.when(
			_objectDefinition.getClassName()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Mockito.when(
			_objectEntry.getObjectDefinition()
		).thenReturn(
			_objectDefinition
		);
	}

	@Test
	public void testCheck() throws Exception {
		String modelName = RandomTestUtil.randomString();

		_mockRoom(WorkflowConstants.STATUS_INACTIVE, 0L);

		Mockito.when(
			_modelResourcePermission.getModelName()
		).thenReturn(
			modelName
		);

		long objectEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_objectEntry.getObjectEntryId()
		).thenReturn(
			objectEntryId
		);

		long userId = RandomTestUtil.randomLong();

		Mockito.when(
			_permissionChecker.getUserId()
		).thenReturn(
			userId
		);

		try {
			_dsrDefaultPermissionObjectEntryModelResourcePermission.check(
				_permissionChecker, _objectEntry, ActionKeys.VIEW);

			Assert.fail();
		}
		catch (PrincipalException.MustHavePermission principalException) {
			Assert.assertEquals(
				String.format(
					"User %s must have %s permission for %s %s", userId,
					ActionKeys.VIEW, modelName, objectEntryId),
				principalException.getMessage());
		}
	}

	@Test
	public void testContains() throws Exception {
		long siteId = RandomTestUtil.randomLong();

		_mockRoom(WorkflowConstants.STATUS_INACTIVE, siteId);

		Mockito.when(
			_permissionChecker.isGroupMember(siteId)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.VIEW));

		Assert.assertFalse(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.UPDATE));

		Mockito.when(
			_permissionChecker.isGroupOwner(siteId)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.UPDATE));

		_mockRoom(WorkflowConstants.STATUS_INACTIVE, 0L);

		Assert.assertFalse(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.UPDATE));

		Mockito.when(
			_permissionChecker.isCompanyAdmin()
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.UPDATE));

		_mockRoom(
			WorkflowConstants.STATUS_APPROVED, RandomTestUtil.randomLong());

		Mockito.when(
			_modelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.UPDATE)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.UPDATE));

		siteId = RandomTestUtil.randomLong();

		_mockRoom(WorkflowConstants.STATUS_APPROVED, siteId);

		Mockito.when(
			_permissionChecker.isGroupMember(siteId)
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.VIEW));
		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.ADD_DISCUSSION));

		_mockRoom(
			WorkflowConstants.STATUS_APPROVED, RandomTestUtil.randomLong());

		Mockito.when(
			_permissionChecker.hasPermission(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyString())
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.VIEW));

		_mockRoom(
			WorkflowConstants.STATUS_APPROVED, RandomTestUtil.randomLong());

		Mockito.when(
			_permissionChecker.hasOwnerPermission(
				Mockito.anyLong(), Mockito.anyString(), Mockito.anyLong(),
				Mockito.anyLong(), Mockito.anyString())
		).thenReturn(
			true
		);

		Assert.assertTrue(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, _objectEntry, ActionKeys.VIEW));

		long primaryKey = RandomTestUtil.randomLong();

		Mockito.when(
			_objectEntryLocalService.fetchObjectEntry(primaryKey)
		).thenReturn(
			null
		);

		Assert.assertFalse(
			_dsrDefaultPermissionObjectEntryModelResourcePermission.contains(
				_permissionChecker, primaryKey, ActionKeys.VIEW));
	}

	private void _mockRoom(int roomStatus, long siteId) {
		Mockito.when(
			_objectEntry.getValues()
		).thenReturn(
			HashMapBuilder.<String, Serializable>put(
				"roomStatus", roomStatus
			).put(
				"siteId", siteId
			).build()
		);
	}

	private DSRDefaultPermissionObjectEntryModelResourcePermission
		_dsrDefaultPermissionObjectEntryModelResourcePermission;
	private final ModelResourcePermission<ObjectEntry>
		_modelResourcePermission = Mockito.mock(ModelResourcePermission.class);
	private final ObjectDefinition _objectDefinition = Mockito.mock(
		ObjectDefinition.class);
	private final ObjectEntry _objectEntry = Mockito.mock(ObjectEntry.class);
	private final ObjectEntryLocalService _objectEntryLocalService =
		Mockito.mock(ObjectEntryLocalService.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}