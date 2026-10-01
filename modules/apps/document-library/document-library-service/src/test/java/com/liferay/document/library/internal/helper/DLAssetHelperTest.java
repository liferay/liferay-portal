/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.internal.helper;

import com.liferay.document.library.helper.DLAssetHelper;
import com.liferay.document.library.kernel.model.DLFileEntryConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class DLAssetHelperTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testClassPKIsFileEntryIdIfApproved() {
		long fileEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_fileEntry.getFileEntryId()
		).thenReturn(
			fileEntryId
		);

		Mockito.when(
			_fileVersion.isApproved()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			fileEntryId,
			_dlAssetHelper.getAssetClassPK(_fileEntry, _fileVersion));
	}

	@Test
	public void testClassPKIsFileEntryIdIfDefaultVersion() {
		long fileEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_fileEntry.getFileEntryId()
		).thenReturn(
			fileEntryId
		);

		Mockito.when(
			_fileVersion.getVersion()
		).thenReturn(
			DLFileEntryConstants.VERSION_DEFAULT
		);

		Assert.assertEquals(
			fileEntryId,
			_dlAssetHelper.getAssetClassPK(_fileEntry, _fileVersion));
	}

	@Test
	public void testClassPKIsFileEntryIdIfInTrash() {
		long fileEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_fileEntry.getFileEntryId()
		).thenReturn(
			fileEntryId
		);

		Mockito.when(
			_fileVersion.getVersion()
		).thenReturn(
			DLFileEntryConstants.PRIVATE_WORKING_COPY_VERSION
		);

		Mockito.when(
			_fileEntry.isInTrash()
		).thenReturn(
			true
		);

		Assert.assertEquals(
			fileEntryId,
			_dlAssetHelper.getAssetClassPK(_fileEntry, _fileVersion));
	}

	@Test
	public void testClassPKIsFileEntryIdIfNullFileVersion() {
		long fileEntryId = RandomTestUtil.randomLong();

		Mockito.when(
			_fileEntry.getFileEntryId()
		).thenReturn(
			fileEntryId
		);

		Assert.assertEquals(
			fileEntryId, _dlAssetHelper.getAssetClassPK(_fileEntry, null));
	}

	@Test
	public void testClassPKIsZeroIfNullFileEntry() {
		Assert.assertEquals(
			0, _dlAssetHelper.getAssetClassPK(null, _fileVersion));
	}

	private final DLAssetHelper _dlAssetHelper = new DLAssetHelperImpl();
	private final FileEntry _fileEntry = Mockito.mock(FileEntry.class);
	private final FileVersion _fileVersion = Mockito.mock(FileVersion.class);

}