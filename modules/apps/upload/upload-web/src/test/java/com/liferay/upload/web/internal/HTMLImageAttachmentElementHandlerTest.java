/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.upload.web.internal;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.editor.constants.EditorConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.upload.AttachmentElementReplacer;

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
		AttachmentElementReplacer attachmentElementReplacer = Mockito.mock(
			AttachmentElementReplacer.class);

		Mockito.when(
			attachmentElementReplacer.replace(
				Mockito.anyString(), Mockito.any())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(0, String.class)
		);

		ReflectionTestUtil.setFieldValue(
			_htmlImageAttachmentElementHandler, "_attachmentElementReplacer",
			attachmentElementReplacer);

		ReflectionTestUtil.setFieldValue(
			_htmlImageAttachmentElementHandler, "_dlAppService", _dlAppService);
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

	private String _getContent(long fileEntryId) {
		return StringBundler.concat(
			"<img ", EditorConstants.ATTRIBUTE_DATA_IMAGE_ID, "=\"",
			fileEntryId, "\" />");
	}

	private final DLAppService _dlAppService = Mockito.mock(DLAppService.class);
	private final HTMLImageAttachmentElementHandler
		_htmlImageAttachmentElementHandler =
			new HTMLImageAttachmentElementHandler();

}