/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.exception.NoSuchStorageQuotaException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.model.DLStorageQuota;
import com.liferay.document.library.service.persistence.DLStorageQuotaUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class DLAppDLStorageQuotaLocalServiceTest {

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
	public void testAddFileEntryIncreasesStorageSize() throws Exception {
		long initialStorageSize = _getStorageSize();

		int size1 = RandomTestUtil.randomInt(1, 100);

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(),
			new byte[size1], null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		int size2 = size1 + RandomTestUtil.randomInt(1, 100);

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(),
			new byte[size2], null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			initialStorageSize + size1 + size2, _getStorageSize());
	}

	@Test
	public void testDeleteFileEntryDecreasesStorageSize() throws Exception {
		long initialStorageSize = _getStorageSize();

		int size = RandomTestUtil.randomInt(1, 100);

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(), StringUtil.randomString(),
			new byte[size], null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			StringUtil.randomString(), StringPool.BLANK,
			StringUtil.randomString(), StringUtil.randomString(),
			new byte[size + RandomTestUtil.randomInt(1, 100)], null, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		_dlAppLocalService.deleteFileEntry(fileEntry.getFileEntryId());

		Assert.assertEquals(initialStorageSize + size, _getStorageSize());
	}

	private long _getStorageSize() throws Exception {
		try {
			DLStorageQuota dlStorageQuota = TransactionTestUtil.invoke(
				DLStorageQuotaUtil.getPersistence(),
				dlStorageQuotaPersistence ->
					dlStorageQuotaPersistence.findByCompanyId(
						TestPropsValues.getCompanyId()));

			return dlStorageQuota.getStorageSize();
		}
		catch (NoSuchStorageQuotaException noSuchStorageQuotaException) {
			return 0;
		}
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

}