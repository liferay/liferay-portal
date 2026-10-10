/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.document.library.internal.model.listener.test;

import com.liferay.adaptive.media.image.configuration.AMImageConfigurationEntry;
import com.liferay.adaptive.media.image.configuration.AMImageConfigurationHelper;
import com.liferay.adaptive.media.image.service.AMImageEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.model.DLVersionNumberIncrease;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.document.library.kernel.service.DLFileVersionLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Saurasish Basak
 */
@RunWith(Arquillian.class)
public class DLFileVersionModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_amImageConfigurationEntry =
			_amImageConfigurationHelper.addAMImageConfigurationEntry(
				_group.getCompanyId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				HashMapBuilder.put(
					"max-height", String.valueOf(RandomTestUtil.randomInt())
				).put(
					"max-width", String.valueOf(RandomTestUtil.randomInt())
				).build());

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@After
	public void tearDown() throws Exception {
		_amImageConfigurationHelper.forceDeleteAMImageConfigurationEntry(
			_group.getCompanyId(), _amImageConfigurationEntry.getUUID());
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		_testOnAfterRemoveWhenCancelCheckOut();
		_testOnAfterRemoveWhenCancelCheckOutInCTCollection();
		_testOnAfterRemoveWhenCheckInFileEntryWithoutVersionNumberIncrease();
		_testOnAfterRemoveWhenDeleteFileEntry();
		_testOnAfterRemoveWhenDeleteFileVersion();
		_testOnAfterRemoveWhenImportIsInProcess();
	}

	private FileEntry _addFileEntry() throws Exception {
		FileEntry fileEntry = _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString(), ContentTypes.IMAGE_JPEG,
			_getImageBytes(), null, null, null, _serviceContext);

		FileVersion fileVersion = fileEntry.getFileVersion();

		Assert.assertNotNull(
			_amImageEntryLocalService.fetchAMImageEntry(
				_amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId()));

		return fileEntry;
	}

	private FileVersion _addPrivateWorkingCopyFileVersion() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		_dlAppService.checkOutFileEntry(
			fileEntry.getFileEntryId(), _serviceContext);

		fileEntry = _dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			ContentTypes.IMAGE_JPEG, fileEntry.getTitle(), null,
			StringPool.BLANK, StringPool.BLANK,
			DLVersionNumberIncrease.AUTOMATIC, _getImageBytes(), null, null,
			null, _serviceContext);

		FileVersion fileVersion = fileEntry.getLatestFileVersion(true);

		Assert.assertNotNull(
			_amImageEntryLocalService.fetchAMImageEntry(
				_amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId()));

		return fileVersion;
	}

	private void _assertRemoved(FileVersion fileVersion) {
		Assert.assertNull(
			_amImageEntryLocalService.fetchAMImageEntry(
				_amImageConfigurationEntry.getUUID(),
				fileVersion.getFileVersionId()));
		Assert.assertNull(
			_dlFileVersionLocalService.fetchDLFileVersion(
				fileVersion.getFileVersionId()));
	}

	private byte[] _getImageBytes() throws Exception {
		return FileUtil.getBytes(
			DLFileVersionModelListenerTest.class, "dependencies/liferay.jpg");
	}

	private void _testOnAfterRemoveWhenCancelCheckOut() throws Exception {
		FileVersion fileVersion = _addPrivateWorkingCopyFileVersion();

		_dlAppService.cancelCheckOut(fileVersion.getFileEntryId());

		_assertRemoved(fileVersion);
	}

	private void _testOnAfterRemoveWhenCancelCheckOutInCTCollection()
		throws Exception {

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, _group.getCompanyId(), TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), null);

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			FileVersion fileVersion = _addPrivateWorkingCopyFileVersion();

			_dlAppService.cancelCheckOut(fileVersion.getFileEntryId());

			_assertRemoved(fileVersion);
		}
	}

	private void _testOnAfterRemoveWhenCheckInFileEntryWithoutVersionNumberIncrease()
		throws Exception {

		FileVersion fileVersion = _addPrivateWorkingCopyFileVersion();

		_dlAppService.checkInFileEntry(
			fileVersion.getFileEntryId(), DLVersionNumberIncrease.NONE,
			StringPool.BLANK, _serviceContext);

		_assertRemoved(fileVersion);
	}

	private void _testOnAfterRemoveWhenDeleteFileEntry() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		FileVersion fileVersion = fileEntry.getFileVersion();

		_dlAppService.deleteFileEntry(fileEntry.getFileEntryId());

		_assertRemoved(fileVersion);
	}

	private void _testOnAfterRemoveWhenDeleteFileVersion() throws Exception {
		FileEntry fileEntry = _addFileEntry();

		FileVersion fileVersion = fileEntry.getFileVersion();

		_dlAppService.updateFileEntry(
			fileEntry.getFileEntryId(), fileEntry.getFileName(),
			ContentTypes.IMAGE_JPEG, fileEntry.getTitle(), null,
			StringPool.BLANK, StringPool.BLANK, DLVersionNumberIncrease.MAJOR,
			_getImageBytes(), null, null, null, _serviceContext);

		_dlAppService.deleteFileVersion(
			fileEntry.getFileEntryId(), fileVersion.getVersion());

		_assertRemoved(fileVersion);
	}

	private void _testOnAfterRemoveWhenImportIsInProcess() throws Exception {
		FileVersion fileVersion = _addPrivateWorkingCopyFileVersion();

		ExportImportThreadLocal.setPortletImportInProcess(true);

		try {
			_dlAppService.cancelCheckOut(fileVersion.getFileEntryId());
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(false);
		}

		_assertRemoved(fileVersion);
	}

	private AMImageConfigurationEntry _amImageConfigurationEntry;

	@Inject
	private AMImageConfigurationHelper _amImageConfigurationHelper;

	@Inject
	private AMImageEntryLocalService _amImageEntryLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@Inject
	private DLAppService _dlAppService;

	@Inject
	private DLFileVersionLocalService _dlFileVersionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

}