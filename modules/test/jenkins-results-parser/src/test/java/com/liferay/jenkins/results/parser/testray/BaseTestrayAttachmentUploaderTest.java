/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import java.io.File;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class BaseTestrayAttachmentUploaderTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testPrepareFiles() throws Exception {
		File preparedFilesBaseDir = _createTempDir();

		File recordedFilesBaseDir = new File(
			preparedFilesBaseDir, "recorded_logs");

		TestrayAttachmentRecorder testrayAttachmentRecorder = Mockito.mock(
			TestrayAttachmentRecorder.class);

		Mockito.when(
			testrayAttachmentRecorder.getRecordedFilesBaseDir()
		).thenReturn(
			recordedFilesBaseDir
		);

		BaseTestrayAttachmentUploader baseTestrayAttachmentUploader =
			Mockito.mock(BaseTestrayAttachmentUploader.class);

		Mockito.when(
			baseTestrayAttachmentUploader.getPreparedFilesBaseDir()
		).thenReturn(
			preparedFilesBaseDir
		);

		Mockito.when(
			baseTestrayAttachmentUploader.getTestrayAttachmentRecorder()
		).thenReturn(
			testrayAttachmentRecorder
		);

		Mockito.doCallRealMethod(
		).when(
			baseTestrayAttachmentUploader
		).getPreparedFiles();

		Mockito.doCallRealMethod(
		).when(
			baseTestrayAttachmentUploader
		).prepareFiles();

		baseTestrayAttachmentUploader.prepareFiles();

		List<File> preparedFiles =
			baseTestrayAttachmentUploader.getPreparedFiles();

		Assert.assertTrue(preparedFiles.isEmpty());
	}

	private File _createTempDir() throws Exception {
		File dir = File.createTempFile("testray-uploader-", null);

		dir.delete();

		dir.mkdir();

		return dir;
	}

}