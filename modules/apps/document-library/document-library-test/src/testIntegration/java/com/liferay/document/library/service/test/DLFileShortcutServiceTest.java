/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileShortcut;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileShortcutLocalServiceUtil;
import com.liferay.document.library.kernel.service.DLFileShortcutServiceUtil;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jan Brychta
 */
@RunWith(Arquillian.class)
public class DLFileShortcutServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetGroupFileShortcutsFiltersByPermission()
		throws Exception {

		DLFileShortcut hiddenDLFileShortcut = _addDLFileShortcut(false);
		DLFileShortcut visibleDLFileShortcut = _addDLFileShortcut(true);

		List<DLFileShortcut> dlFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId());

		Assert.assertEquals(
			SetUtil.fromArray(hiddenDLFileShortcut, visibleDLFileShortcut),
			SetUtil.fromCollection(dlFileShortcuts));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		List<DLFileShortcut> filteredDLFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId());

		Assert.assertEquals(
			Collections.singletonList(visibleDLFileShortcut),
			filteredDLFileShortcuts);
	}

	@Test
	public void testGetGroupFileShortcutsPaginatedFiltersByPermission()
		throws Exception {

		DLFileShortcut hiddenDLFileShortcut = _addDLFileShortcut(false);
		DLFileShortcut visibleDLFileShortcut = _addDLFileShortcut(true);

		List<DLFileShortcut> dlFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId(), 0, 2);

		Assert.assertEquals(
			SetUtil.fromArray(hiddenDLFileShortcut, visibleDLFileShortcut),
			SetUtil.fromCollection(dlFileShortcuts));

		Assert.assertEquals(
			2,
			DLFileShortcutServiceUtil.getGroupFileShortcutsCount(
				_group.getGroupId()));

		UserTestUtil.setUser(
			UserTestUtil.addGroupUser(_group, RoleConstants.SITE_MEMBER));

		List<DLFileShortcut> filteredDLFileShortcuts =
			DLFileShortcutServiceUtil.getGroupFileShortcuts(
				_group.getGroupId(), 0, 2);

		Assert.assertEquals(
			Collections.singletonList(visibleDLFileShortcut),
			filteredDLFileShortcuts);

		Assert.assertEquals(
			1,
			DLFileShortcutServiceUtil.getGroupFileShortcutsCount(
				_group.getGroupId()));
	}

	private DLFileShortcut _addDLFileShortcut(boolean addDefaultPermissions)
		throws Exception {

		DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

		DLFileEntry dlFileEntry = DLTestUtil.addDLFileEntry(
			dlFolder.getFolderId());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		serviceContext.setAddGroupPermissions(addDefaultPermissions);
		serviceContext.setAddGuestPermissions(addDefaultPermissions);

		return DLFileShortcutLocalServiceUtil.addFileShortcut(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			dlFileEntry.getFileEntryId(), serviceContext);
	}

	@DeleteAfterTestRun
	private Group _group;

}