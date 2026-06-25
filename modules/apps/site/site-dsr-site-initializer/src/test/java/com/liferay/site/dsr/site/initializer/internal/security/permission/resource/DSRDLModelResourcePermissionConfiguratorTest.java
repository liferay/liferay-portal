/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.site.initializer.internal.security.permission.resource;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.portal.kernel.model.GroupedModel;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermissionLogic;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.site.dsr.site.initializer.util.DSRRoomUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Balazs Breier
 */
public class DSRDLModelResourcePermissionConfiguratorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		List<ModelResourcePermissionLogic<GroupedModel>>
			modelResourcePermissionLogics = new ArrayList<>();

		DSRDLModelResourcePermissionConfigurator
			dsrDLModelResourcePermissionConfigurator =
				new DSRDLModelResourcePermissionConfigurator();

		dsrDLModelResourcePermissionConfigurator.
			configureModelResourcePermissionLogics(
				Mockito.mock(ModelResourcePermission.class),
				modelResourcePermissionLogics::add);

		_modelResourcePermissionLogic = modelResourcePermissionLogics.get(0);
	}

	@Test
	public void testContains() throws Exception {
		Assert.assertNull(
			_modelResourcePermissionLogic.contains(
				_permissionChecker, RandomTestUtil.randomString(), _dlFileEntry,
				ActionKeys.ACCESS));
		Assert.assertNull(
			_modelResourcePermissionLogic.contains(
				_permissionChecker, RandomTestUtil.randomString(), _dlFileEntry,
				ActionKeys.VIEW));
		Assert.assertNull(
			_modelResourcePermissionLogic.contains(
				_permissionChecker, RandomTestUtil.randomString(), _dlFolder,
				ActionKeys.ACCESS));
		Assert.assertNull(
			_modelResourcePermissionLogic.contains(
				_permissionChecker, RandomTestUtil.randomString(), _dlFolder,
				ActionKeys.VIEW));

		long groupId = RandomTestUtil.randomLong();

		Mockito.when(
			_dlFileEntry.getGroupId()
		).thenReturn(
			groupId
		);

		Mockito.when(
			_dlFolder.getGroupId()
		).thenReturn(
			groupId
		);

		try (MockedStatic<DSRRoomUtil> dsrRoomUtilMockedStatic =
				Mockito.mockStatic(DSRRoomUtil.class)) {

			dsrRoomUtilMockedStatic.when(
				() -> DSRRoomUtil.isReadOnly(groupId, _permissionChecker)
			).thenReturn(
				false
			);

			Assert.assertNull(
				_modelResourcePermissionLogic.contains(
					_permissionChecker, RandomTestUtil.randomString(),
					_dlFileEntry, ActionKeys.UPDATE));

			Assert.assertNull(
				_modelResourcePermissionLogic.contains(
					_permissionChecker, RandomTestUtil.randomString(),
					_dlFolder, ActionKeys.ADD_DOCUMENT));

			dsrRoomUtilMockedStatic.when(
				() -> DSRRoomUtil.isReadOnly(groupId, _permissionChecker)
			).thenReturn(
				true
			);

			Assert.assertFalse(
				_modelResourcePermissionLogic.contains(
					_permissionChecker, RandomTestUtil.randomString(),
					_dlFileEntry, ActionKeys.DELETE));
			Assert.assertFalse(
				_modelResourcePermissionLogic.contains(
					_permissionChecker, RandomTestUtil.randomString(),
					_dlFileEntry, ActionKeys.UPDATE));

			Assert.assertFalse(
				_modelResourcePermissionLogic.contains(
					_permissionChecker, RandomTestUtil.randomString(),
					_dlFolder, ActionKeys.ADD_DOCUMENT));
			Assert.assertFalse(
				_modelResourcePermissionLogic.contains(
					_permissionChecker, RandomTestUtil.randomString(),
					_dlFolder, ActionKeys.UPDATE));
		}
	}

	private final DLFileEntry _dlFileEntry = Mockito.mock(DLFileEntry.class);
	private final DLFolder _dlFolder = Mockito.mock(DLFolder.class);
	private ModelResourcePermissionLogic<GroupedModel>
		_modelResourcePermissionLogic;
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);

}