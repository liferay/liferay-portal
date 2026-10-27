/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.document.library.internal.search.spi.model.index.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFileVersion;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.processor.RawMetadataProcessorUtil;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.text.DLFileEntryTextProvider;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.File;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.TextExtractor;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mikel Lorza
 */
@RunWith(Arquillian.class)
public class DLFileEntryModelDocumentContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testFileEntryMetadataAttributes() throws Exception {
		_testFileEntryMetadataAttributesBasicFileEntry();

		TextExtractor testTextExtractor = new TextExtractor() {

			@Override
			public String extractText(
				InputStream inputStream, int maxStringLength) {

				throw new NoSuchMethodError();
			}

		};

		TextExtractor originalTextExtractor =
			ReflectionTestUtil.getAndSetFieldValue(
				_dlFileEntryTextProvider, "_textExtractor", testTextExtractor);

		Class<?> clazz = _dlFileEntryTextProvider.getClass();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				clazz.getName(), LoggerTestUtil.WARN)) {

			DLFileVersion dlFileVersion =
				_testFileEntryMetadataAttributesBasicFileEntry();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				"Unable to extract text from file version " +
					dlFileVersion.getFileVersionId(),
				logEntry.getMessage());
		}
		finally {
			ReflectionTestUtil.setFieldValue(
				_dlFileEntryTextProvider, "_textExtractor",
				originalTextExtractor);
		}

		_testFileEntryMetadataAttributesImageFileEntry(
			"square", "dependencies/225x225.jpeg", ContentTypes.IMAGE_JPEG);
		_testFileEntryMetadataAttributesImageFileEntry(
			"tall", "dependencies/183x275.jpeg", ContentTypes.IMAGE_JPEG);
		_testFileEntryMetadataAttributesImageFileEntry(
			"wide", "dependencies/277x182.jpeg", ContentTypes.IMAGE_JPEG);
		_testFileEntryMetadataAttributesImageFileEntry(
			"wide", "dependencies/693x612.png", ContentTypes.IMAGE_PNG);
	}

	private DLFileEntry _addDLFileEntry() throws Exception {
		String content = StringUtil.randomString();

		return _addDLFileEntry(
			ContentTypes.APPLICATION_OCTET_STREAM,
			content.getBytes(StandardCharsets.UTF_8));
	}

	private DLFileEntry _addDLFileEntry(String mimeType, byte[] bytes)
		throws Exception {

		return _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			_group.getGroupId(), DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), mimeType, StringUtil.randomString(),
			null, StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new UnsyncByteArrayInputStream(bytes), bytes.length, null,
			null, null, ServiceContextTestUtil.getServiceContext());
	}

	private DLFileVersion _testFileEntryMetadataAttributesBasicFileEntry()
		throws Exception {

		DLFileEntry dlFileEntry = _addDLFileEntry();

		FileEntry fileEntry = new LiferayFileEntry(dlFileEntry);

		RawMetadataProcessorUtil.generateMetadata(fileEntry.getFileVersion());

		Document document = new DocumentImpl();

		_dlFileEntryModelDocumentContributor.contribute(document, dlFileEntry);

		Assert.assertEquals(
			0L, GetterUtil.getLong(document.get("imageLength")));
		Assert.assertEquals(0L, GetterUtil.getLong(document.get("imageWidth")));

		return dlFileEntry.getFileVersion();
	}

	private void _testFileEntryMetadataAttributesImageFileEntry(
			String expectedAspectRatio, String fileName, String mimeType)
		throws Exception {

		DLFileEntry dlFileEntry = _addDLFileEntry(
			mimeType, _file.getBytes(getClass(), fileName));

		FileEntry fileEntry = new LiferayFileEntry(dlFileEntry);

		RawMetadataProcessorUtil.generateMetadata(fileEntry.getFileVersion());

		Document document = new DocumentImpl();

		_dlFileEntryModelDocumentContributor.contribute(document, dlFileEntry);

		Assert.assertEquals(expectedAspectRatio, document.get("aspectRatio"));
	}

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.document.library.internal.search.spi.model.index.contributor.DLFileEntryModelDocumentContributor"
	)
	private ModelDocumentContributor<DLFileEntry>
		_dlFileEntryModelDocumentContributor;

	@Inject
	private DLFileEntryTextProvider _dlFileEntryTextProvider;

	@Inject
	private File _file;

	@DeleteAfterTestRun
	private Group _group;

}