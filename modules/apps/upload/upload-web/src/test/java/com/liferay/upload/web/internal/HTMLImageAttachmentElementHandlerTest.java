/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.FileEntryWrapper;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class HTMLImageAttachmentElementHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_htmlImageAttachmentElementHandler, "_dlAppService", _dlAppService);
		ReflectionTestUtil.setFieldValue(
			_htmlImageAttachmentElementHandler, "_portletFileRepository",
			_portletFileRepository);

		Mockito.when(
			_portletFileRepository.getPortletFileEntryURL(
				Mockito.nullable(ThemeDisplay.class),
				Mockito.any(FileEntry.class), Mockito.eq(StringPool.BLANK))
		).thenAnswer(
			invocationOnMock -> {
				FileEntry fileEntry = invocationOnMock.getArgument(
					1, FileEntry.class);

				return "/final/" + fileEntry.getFileEntryId();
			}
		);
	}

	@Test
	public void testReplaceAttachmentElements() throws Exception {
		Mockito.when(
			_dlAppService.getFileEntry(Mockito.anyLong())
		).thenAnswer(
			invocationOnMock -> _createFileEntry(
				invocationOnMock.getArgument(0, Long.class))
		);

		_testReplaceAttachmentElementsWithHyphenatedAttribute();
		_testReplaceAttachmentElementsWithMultipleImages();
		_testReplaceAttachmentElementsWithRepeatedImage();
	}

	@Test
	public void testReplaceAttachmentElementsSavesEachFileEntryOnce()
		throws Exception {

		Mockito.when(
			_dlAppService.getFileEntry(Mockito.anyLong())
		).thenReturn(
			Mockito.mock(FileEntry.class)
		);

		List<FileEntry> savedFileEntries = new ArrayList<>();

		String content =
			_htmlImageAttachmentElementHandler.replaceAttachmentElements(
				StringBundler.concat(
					"<img alt=\"a\" ", EditorConstants.ATTRIBUTE_DATA_IMAGE_ID,
					"=\"7\" /><img alt=\"b\" ",
					EditorConstants.ATTRIBUTE_DATA_IMAGE_ID, "=\"7\" />"),
				fileEntry -> {
					savedFileEntries.add(fileEntry);

					return fileEntry;
				});

		Assert.assertEquals(
			savedFileEntries.toString(), 1, savedFileEntries.size());

		Assert.assertTrue(content, content.contains("alt=\"a\""));
		Assert.assertTrue(content, content.contains("alt=\"b\""));

		savedFileEntries.clear();

		_htmlImageAttachmentElementHandler.replaceAttachmentElements(
			_getContent(7) + _getContent(7),
			fileEntry -> {
				savedFileEntries.add(fileEntry);

				return fileEntry;
			});

		Assert.assertEquals(
			savedFileEntries.toString(), 1, savedFileEntries.size());
	}

	@Test
	@TestInfo("LPD-107148")
	public void testReplaceAttachmentElementsWhenFileEntryNotViewable()
		throws Exception {

		Mockito.when(
			_dlAppService.getFileEntry(Mockito.anyLong())
		).thenThrow(
			new PrincipalException()
		);

		try {
			_htmlImageAttachmentElementHandler.replaceAttachmentElements(
				_getContent(RandomTestUtil.randomLong()),
				fileEntry -> fileEntry);

			Assert.fail();
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof PrincipalException);
		}
	}

	@Test
	@TestInfo("LPD-107148")
	public void testReplaceAttachmentElementsWhenFileEntryViewable()
		throws Exception {

		FileEntry fileEntry = Mockito.mock(FileEntry.class);

		Mockito.when(
			_dlAppService.getFileEntry(Mockito.anyLong())
		).thenReturn(
			fileEntry
		);

		_htmlImageAttachmentElementHandler.replaceAttachmentElements(
			_getContent(RandomTestUtil.randomLong()),
			resolvedFileEntry -> {
				Assert.assertEquals(fileEntry, resolvedFileEntry);

				return resolvedFileEntry;
			});
	}

	private FileEntry _createFileEntry(long fileEntryId) {
		return new FileEntryWrapper(null) {

			@Override
			public long getFileEntryId() {
				return fileEntryId;
			}

		};
	}

	private String _getContent(long fileEntryId) {
		return StringBundler.concat(
			"<img ", EditorConstants.ATTRIBUTE_DATA_IMAGE_ID, "=\"",
			fileEntryId, "\" />");
	}

	private void _testReplaceAttachmentElementsWithHyphenatedAttribute()
		throws Exception {

		Assert.assertEquals(
			"<img aria-label=\"a\" src=\"/final/1004\" data-fileentryid=" +
				"\"1004\" />",
			_htmlImageAttachmentElementHandler.replaceAttachmentElements(
				"<img aria-label=\"a\" data-image-id=\"4\" src=\"/temp/4\" />",
				tempFileEntry -> _createFileEntry(
					tempFileEntry.getFileEntryId() + 1000)));
	}

	private void _testReplaceAttachmentElementsWithMultipleImages()
		throws Exception {

		Assert.assertEquals(
			StringBundler.concat(
				"<p>a</p><img alt=\"b\" src=\"/final/1001\" data-fileentryid=",
				"\"1001\" /><p>c &amp; d</p><img src=\"/final/1002\" ",
				"data-fileentryid=\"1002\" /><img src=\"/e.png\" />"),
			_htmlImageAttachmentElementHandler.replaceAttachmentElements(
				StringBundler.concat(
					"<p>a</p><img alt=\"b\" data-image-id=\"1\" ",
					"src=\"/temp/1\" /><p>c &amp; d</p><img ",
					"data-image-id=\"2\" src=\"/temp/2\"/><img src=\"/e.png\" ",
					"/>"),
				tempFileEntry -> _createFileEntry(
					tempFileEntry.getFileEntryId() + 1000)));
	}

	private void _testReplaceAttachmentElementsWithRepeatedImage()
		throws Exception {

		List<Long> tempFileEntryIds = new ArrayList<>();

		Assert.assertEquals(
			"<img src=\"/final/1003\" data-fileentryid=\"1003\" /><p>a</p>" +
				"<img src=\"/final/1003\" data-fileentryid=\"1003\" />",
			_htmlImageAttachmentElementHandler.replaceAttachmentElements(
				"<img data-image-id=\"3\" src=\"/temp/3\" /><p>a</p><img " +
					"data-image-id=\"3\" src=\"/temp/3\" />",
				tempFileEntry -> {
					tempFileEntryIds.add(tempFileEntry.getFileEntryId());

					return _createFileEntry(
						tempFileEntry.getFileEntryId() + 1000);
				}));

		Assert.assertEquals(
			tempFileEntryIds.toString(), 1, tempFileEntryIds.size());
	}

	private final DLAppService _dlAppService = Mockito.mock(DLAppService.class);
	private final HTMLImageAttachmentElementHandler
		_htmlImageAttachmentElementHandler =
			new HTMLImageAttachmentElementHandler();
	private final PortletFileRepository _portletFileRepository = Mockito.mock(
		PortletFileRepository.class);

}