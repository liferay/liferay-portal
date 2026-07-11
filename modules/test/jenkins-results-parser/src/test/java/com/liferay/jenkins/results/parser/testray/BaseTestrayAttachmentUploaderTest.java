/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.BuildDatabase;

import java.io.File;

import java.net.URL;

import java.util.List;
import java.util.Properties;

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
		Build build = _mockBuild();

		File preparedFilesBaseDir = _createTempDir();

		File recordedFilesBaseDir = new File(
			preparedFilesBaseDir, "recorded_logs");

		FakeTestrayAttachmentUploader fakeTestrayAttachmentUploader =
			new FakeTestrayAttachmentUploader(
				build, preparedFilesBaseDir,
				new FakeTestrayAttachmentRecorder(build, recordedFilesBaseDir));

		fakeTestrayAttachmentUploader.prepareFiles();

		List<File> preparedFiles =
			fakeTestrayAttachmentUploader.getPreparedFiles();

		Assert.assertTrue(preparedFiles.isEmpty());
	}

	private File _createTempDir() throws Exception {
		File dir = File.createTempFile("testray-uploader-", null);

		dir.delete();

		dir.mkdir();

		return dir;
	}

	private Build _mockBuild() {
		Build build = Mockito.mock(Build.class);

		BuildDatabase buildDatabase = Mockito.mock(BuildDatabase.class);

		Mockito.when(
			build.getBuildDatabase()
		).thenReturn(
			buildDatabase
		);

		Mockito.when(
			buildDatabase.getProperties(Mockito.anyString())
		).thenReturn(
			new Properties()
		);

		return build;
	}

	private static class FakeTestrayAttachmentRecorder
		extends TestrayAttachmentRecorder {

		public FakeTestrayAttachmentRecorder(
			Build build, File recordedFilesBaseDir) {

			super(build);

			_recordedFilesBaseDir = recordedFilesBaseDir;
		}

		@Override
		public void record() {
		}

		@Override
		protected File getRecordedFilesBaseDir() {
			return _recordedFilesBaseDir;
		}

		private final File _recordedFilesBaseDir;

	}

	private static class FakeTestrayAttachmentUploader
		extends BaseTestrayAttachmentUploader {

		public FakeTestrayAttachmentUploader(
			Build build, File preparedFilesBaseDir,
			TestrayAttachmentRecorder testrayAttachmentRecorder) {

			super(build, null);

			_preparedFilesBaseDir = preparedFilesBaseDir;
			_testrayAttachmentRecorder = testrayAttachmentRecorder;
		}

		@Override
		public File getPreparedFilesBaseDir() {
			return _preparedFilesBaseDir;
		}

		@Override
		public TestrayAttachmentRecorder getTestrayAttachmentRecorder() {
			return _testrayAttachmentRecorder;
		}

		@Override
		public URL getTestrayServerLogsURL() {
			return null;
		}

		@Override
		public void upload() {
		}

		private final File _preparedFilesBaseDir;
		private final TestrayAttachmentRecorder _testrayAttachmentRecorder;

	}

}