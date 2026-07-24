/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.trash.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectEntryFolder;
import com.liferay.object.service.ObjectEntryFolderLocalService;
import com.liferay.object.test.util.ObjectEntryFolderTestUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.service.TrashEntryLocalServiceUtil;

import java.util.Date;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
public class ObjectEntryFolderTrashHandlerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-91679")
	public void testCheckEntries() throws Exception {
		_testCheckEntriesWithoutPermissions();
		_testCheckEntriesWithPermissions();
	}

	private ObjectEntryFolder _addExpiredTrashedObjectEntryFolder()
		throws Exception {

		ObjectEntryFolder objectEntryFolder =
			ObjectEntryFolderTestUtil.addObjectEntryFolder();

		_objectEntryFolderLocalService.moveObjectEntryFolderToTrash(
			TestPropsValues.getUserId(), objectEntryFolder,
			ServiceContextTestUtil.getServiceContext());

		TrashEntry trashEntry = TrashEntryLocalServiceUtil.getEntry(
			ObjectEntryFolder.class.getName(),
			objectEntryFolder.getObjectEntryFolderId());

		Date createDate = trashEntry.getCreateDate();

		trashEntry.setCreateDate(new Date(createDate.getTime() - Time.YEAR));

		TrashEntryLocalServiceUtil.updateTrashEntry(trashEntry);

		return objectEntryFolder;
	}

	private void _testCheckEntriesWithoutPermissions() throws Exception {
		ObjectEntryFolder objectEntryFolder =
			_addExpiredTrashedObjectEntryFolder();

		PermissionChecker originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		try {
			PermissionThreadLocal.setPermissionChecker(
				PermissionCheckerFactoryUtil.create(
					UserLocalServiceUtil.getGuestUser(
						TestPropsValues.getCompanyId())));

			TrashEntryLocalServiceUtil.checkEntries();
		}
		finally {
			PermissionThreadLocal.setPermissionChecker(
				originalPermissionChecker);
		}

		Assert.assertNull(
			TrashEntryLocalServiceUtil.fetchEntry(
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId()));
	}

	private void _testCheckEntriesWithPermissions() throws Exception {
		ObjectEntryFolder objectEntryFolder =
			_addExpiredTrashedObjectEntryFolder();

		TrashEntryLocalServiceUtil.checkEntries();

		Assert.assertNull(
			TrashEntryLocalServiceUtil.fetchEntry(
				ObjectEntryFolder.class.getName(),
				objectEntryFolder.getObjectEntryFolderId()));
	}

	@Inject
	private ObjectEntryFolderLocalService _objectEntryFolderLocalService;

}