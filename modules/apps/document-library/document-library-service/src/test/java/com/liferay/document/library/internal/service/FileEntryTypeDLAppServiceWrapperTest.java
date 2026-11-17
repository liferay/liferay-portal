/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.service;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Akhash Ramprakash
 */
public class FileEntryTypeDLAppServiceWrapperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testMoveFileEntry() throws Exception {
		FileEntryTypeDLAppServiceWrapper fileEntryTypeDLAppServiceWrapper =
			new FileEntryTypeDLAppServiceWrapper();

		DLAppService dlAppService = Mockito.mock(DLAppService.class);

		fileEntryTypeDLAppServiceWrapper.setWrappedService(dlAppService);

		ReflectionTestUtil.setFieldValue(
			fileEntryTypeDLAppServiceWrapper, "_dlFileEntryLocalService",
			Mockito.mock(DLFileEntryLocalService.class));

		long fileEntryId = RandomTestUtil.randomLong();
		long newFolderId = RandomTestUtil.randomLong();
		ServiceContext serviceContext = new ServiceContext();

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			dlAppService.moveFileEntry(fileEntryId, newFolderId, serviceContext)
		).thenReturn(
			fileEntry
		);

		Assert.assertSame(
			fileEntry,
			fileEntryTypeDLAppServiceWrapper.moveFileEntry(
				fileEntryId, newFolderId, serviceContext));

		Assert.assertNull(serviceContext.getAttribute("fileEntryTypeId"));
	}

}