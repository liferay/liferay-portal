/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.converter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.VideoProcessorUtil;
import com.liferay.document.library.kernel.service.DLAppServiceUtil;
import com.liferay.document.library.preview.processor.BasePreviewableDLProcessor;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.awt.image.BufferedImage;

import java.util.Dictionary;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DLVideoFFMPEGVideoConverterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), 0);
	}

	@Test
	public void testDoesNotGenerateVideoPreview() throws Exception {
		_withDLVideoFFMPEGVideoConverterConfiguration(
			false,
			() -> {
				FileEntry fileEntry = _createVideoFileEntry("video.mp4");

				Assert.assertFalse(
					VideoProcessorUtil.hasVideo(fileEntry.getFileVersion()));
			});
	}

	@Test
	public void testDoesNotGenerateVideoPreviewIfTheVideoIsCorrupt()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.document.library.preview.video.internal." +
					"processor.VideoPreviewableDLProcessor",
				LoggerTestUtil.ERROR)) {

			_withDLVideoFFMPEGVideoConverterConfiguration(
				true,
				() -> {
					FileEntry fileEntry = _createVideoFileEntry(
						"video_corrupt.mp4");

					Assert.assertFalse(
						VideoProcessorUtil.hasVideo(
							fileEntry.getFileVersion()));
				});

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.isEmpty());

			_assertLogMessage(logEntries);
		}
	}

	@Test
	public void testGeneratesVideoPreviewForOGVIfEnabled() throws Exception {
		_withDLVideoFFMPEGVideoConverterConfiguration(
			true,
			() -> {
				FileEntry fileEntry = _createVideoFileEntry("video.ogv");

				Assert.assertTrue(
					VideoProcessorUtil.hasVideo(fileEntry.getFileVersion()));

				long mp4PreviewFileSize = VideoProcessorUtil.getPreviewFileSize(
					fileEntry.getFileVersion(), "mp4");

				Assert.assertTrue(mp4PreviewFileSize > 0);

				long ogvPreviewFileSize = VideoProcessorUtil.getPreviewFileSize(
					fileEntry.getFileVersion(), "ogv");

				Assert.assertTrue(ogvPreviewFileSize > 0);
			});
	}

	@Test
	public void testGeneratesVideoPreviewIfEnabled() throws Exception {
		_withDLVideoFFMPEGVideoConverterConfiguration(
			true,
			() -> {
				FileEntry fileEntry = _createVideoFileEntry("video.mp4");

				Assert.assertTrue(
					VideoProcessorUtil.hasVideo(fileEntry.getFileVersion()));

				long mp4PreviewFileSize = VideoProcessorUtil.getPreviewFileSize(
					fileEntry.getFileVersion(), "mp4");

				Assert.assertTrue(mp4PreviewFileSize > 0);

				long ogvPreviewFileSize = VideoProcessorUtil.getPreviewFileSize(
					fileEntry.getFileVersion(), "ogv");

				Assert.assertTrue(ogvPreviewFileSize > 0);
			});
	}

	@Test
	public void testGeneratesVideoPreviewIfTheVideoHasOnlyAudio()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.document.library.preview.video.internal." +
					"processor.VideoPreviewableDLProcessor",
				LoggerTestUtil.ERROR)) {

			_withDLVideoFFMPEGVideoConverterConfiguration(
				true,
				() -> {
					FileEntry fileEntry = _createVideoFileEntry(
						"video_only_audio.mp4");

					Assert.assertTrue(
						VideoProcessorUtil.hasVideo(
							fileEntry.getFileVersion()));

					long mp4PreviewFileSize =
						VideoProcessorUtil.getPreviewFileSize(
							fileEntry.getFileVersion(), "mp4");

					Assert.assertTrue(mp4PreviewFileSize > 0);

					long ogvPreviewFileSize =
						VideoProcessorUtil.getPreviewFileSize(
							fileEntry.getFileVersion(), "ogv");

					Assert.assertTrue(ogvPreviewFileSize > 0);
				});

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

	@Test
	@TestInfo("LPD-111060")
	public void testGeneratesVideoThumbnail() throws Exception {
		_withDLVideoFFMPEGVideoConverterConfiguration(
			true,
			() -> {
				_assertVideoThumbnailSize(360, 640);

				try (SafeCloseable safeCloseable =
						PropsValuesTestUtil.swapWithSafeCloseable(
							"DL_FILE_ENTRY_PREVIEW_VIDEO_WIDTH", 0)) {

					_assertVideoThumbnailSize(720, 1080);
				}
			});
	}

	private void _assertLogMessage(List<LogEntry> logEntries) {
		for (LogEntry logEntry : logEntries) {
			String logEntryMessage = logEntry.getMessage();

			Assert.assertTrue(logEntryMessage.contains("Unable to process"));

			Throwable throwable = logEntry.getThrowable();

			String throwableMessage = throwable.getMessage();

			Assert.assertTrue(
				throwableMessage.contains("java.io.FileNotFoundException") ||
				throwableMessage.contains("ffmpeg"));
		}
	}

	private void _assertVideoThumbnailSize(int height, int width)
		throws Exception {

		FileEntry fileEntry = _createVideoFileEntry("video.mp4");

		BufferedImage bufferedImage = ImageIO.read(
			VideoProcessorUtil.getThumbnailAsStream(
				fileEntry.getFileVersion(),
				BasePreviewableDLProcessor.THUMBNAIL_INDEX_DEFAULT));

		Assert.assertEquals(height, bufferedImage.getHeight());
		Assert.assertEquals(width, bufferedImage.getWidth());

		DLAppServiceUtil.deleteFileEntry(fileEntry.getFileEntryId());
	}

	private FileEntry _createVideoFileEntry(String fileName) throws Exception {
		return DLAppServiceUtil.addFileEntry(
			null, _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, fileName,
			MimeTypesUtil.getContentType(fileName), "video",
			StringUtil.randomString(), StringUtil.randomString(),
			StringUtil.randomString(),
			FileUtil.getBytes(getClass(), "dependencies/" + fileName), null,
			null, null, _serviceContext);
	}

	private void _withDLVideoFFMPEGVideoConverterConfiguration(
			boolean enabled, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		Dictionary<String, Object> dictionary =
			HashMapDictionaryBuilder.<String, Object>put(
				"enabled", enabled
			).build();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					"com.liferay.document.library.video.internal." +
						"configuration." +
							"DLVideoFFMPEGVideoConverterConfiguration",
					dictionary)) {

			unsafeRunnable.run();
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

}