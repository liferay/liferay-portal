/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.preview.audio.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.exception.NoSuchFileException;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.AudioProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.kernel.store.Store;
import com.liferay.document.library.kernel.util.DLUtil;
import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileVersion;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.constants.TestDataConstants;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jürgen Kappler
 */
@RunWith(Arquillian.class)
public class AudioProcessorUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testGetPreviewAsStream() throws Exception {
		FileVersion fileVersion = _addFileVersion();

		byte[] bytes = RandomTestUtil.randomBytes();

		_addPreview(fileVersion, "mp3", bytes);

		try (InputStream inputStream = AudioProcessorUtil.getPreviewAsStream(
				fileVersion, "mp3")) {

			Assert.assertArrayEquals(
				bytes, StreamUtil.toByteArray(inputStream));
		}
	}

	@Test(expected = NoSuchFileException.class)
	public void testGetPreviewAsStreamWithInvalidType() throws Exception {
		FileVersion fileVersion = _addFileVersion();

		_addPreview(fileVersion, "wav", RandomTestUtil.randomBytes());

		AudioProcessorUtil.getPreviewAsStream(fileVersion, "wav");
	}

	@Test
	public void testGetPreviewFileSize() throws Exception {
		FileVersion fileVersion = _addFileVersion();

		byte[] bytes = RandomTestUtil.randomBytes();

		_addPreview(fileVersion, "mp3", bytes);

		Assert.assertEquals(
			bytes.length,
			AudioProcessorUtil.getPreviewFileSize(fileVersion, "mp3"));
	}

	@Test(expected = NoSuchFileException.class)
	public void testGetPreviewFileSizeWithInvalidType() throws Exception {
		FileVersion fileVersion = _addFileVersion();

		_addPreview(fileVersion, "wav", RandomTestUtil.randomBytes());

		AudioProcessorUtil.getPreviewFileSize(fileVersion, "wav");
	}

	private FileVersion _addFileVersion() throws Exception {
		FileEntry fileEntry = DLAppServiceUtil.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt", ContentTypes.TEXT_PLAIN,
			RandomTestUtil.randomString() + ".txt", null, null, null,
			TestDataConstants.TEST_BYTE_ARRAY, null, null, null,
			_serviceContext);

		return fileEntry.getFileVersion();
	}

	private void _addPreview(FileVersion fileVersion, String type, byte[] bytes)
		throws Exception {

		_store.addFile(
			fileVersion.getCompanyId(), CompanyConstants.SYSTEM,
			StringBundler.concat(
				"document_preview/", fileVersion.getGroupId(),
				DLUtil.getDividedPath(fileVersion.getFileEntryId()), "/",
				fileVersion.getFileVersionId(), ".", type),
			Store.VERSION_DEFAULT, new UnsyncByteArrayInputStream(bytes));
	}

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

	@Inject(
		filter = "(&(objectClass=com.liferay.document.library.kernel.store.Store)(default=true))"
	)
	private Store _store;

}