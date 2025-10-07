/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.content.transformer.internal;

import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.adaptive.media.image.mime.type.AMImageMimeTypeProvider;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Alejandro Tardín
 * @author Sergio González
 */
public class HtmlContentTransformerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpHtmlContentTransformerImpl();
		_setUpPDFFileEntry();
		_setUpPNGFileEntry();
	}

	@Test
	public void testAlsoReplacesSeveralImagesInAMultilineString()
		throws Exception {

		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		Assert.assertEquals(
			_duplicateWithNewLine(
				"<div><div><whatever></whatever></div></div><br/>"),
			_htmlContentTransformerImpl.transform(
				_duplicateWithNewLine(
					"<div><div><img data-fileentryid=\"1989\" " +
						"src=\"adaptable\"/></div></div><br/>")));
	}

	@Test
	public void testReplacesAnAdaptableImgAfterANonadaptableOne()
		throws Exception {

		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		Assert.assertEquals(
			"<img src=\"not-adaptable\"/><whatever></whatever>",
			_htmlContentTransformerImpl.transform(
				"<img src=\"not-adaptable\"/><img data-fileentryid=\"1989\" " +
					"src=\"adaptable\"/>"));
	}

	@Test
	public void testReplacesTheAdaptableImagesWithTheAdaptiveTag()
		throws Exception {

		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		Assert.assertEquals(
			"<whatever></whatever>",
			_htmlContentTransformerImpl.transform(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/>"));
	}

	@Test
	public void testReplacesTwoConsecutiveImageTags() throws Exception {
		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		Assert.assertEquals(
			"<whatever></whatever><whatever></whatever>",
			_htmlContentTransformerImpl.transform(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/><img " +
					"data-fileentryid=\"1989\" src=\"adaptable\"/>"));
	}

	@Test
	public void testReplacesTwoConsecutiveImageTagsWithUnsupportedMimeType()
		throws Exception {

		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		Assert.assertEquals(
			"<whatever></whatever><img data-fileentryid=\"1999\" " +
				"src=\"adaptable\"/>",
			_htmlContentTransformerImpl.transform(
				"<img data-fileentryid=\"1989\" src=\"adaptable\"/><img " +
					"data-fileentryid=\"1999\" src=\"adaptable\"/>"));
	}

	@Test
	public void testReturnsNullForNullContent() throws Exception {
		Assert.assertNull(_htmlContentTransformerImpl.transform(null));
	}

	@Test
	public void testReturnsTheSameHTMLIfNoImagesArePresent() throws Exception {
		Assert.assertEquals(
			"<div><div>some <a>stuff</a></div></div>",
			_htmlContentTransformerImpl.transform(
				"<div><div>some <a>stuff</a></div></div>"));
	}

	@Test
	public void testReturnsTheSameHTMLIfThereAreNoAdaptableImagesPresent()
		throws Exception {

		Assert.assertEquals(
			"<div><div><img src=\"no.adaptable\"/></div></div>",
			_htmlContentTransformerImpl.transform(
				"<div><div><img src=\"no.adaptable\"/></div></div>"));
	}

	@Test
	public void testSupportsImageTagsWithNewLineCharacters() throws Exception {
		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileentryid=\"1989\" \nsrc=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		Assert.assertEquals(
			"<whatever></whatever>",
			_htmlContentTransformerImpl.transform(
				"<img data-fileentryid=\"1989\" \nsrc=\"adaptable\"/>"));
	}

	@Test
	public void testTheAttributeIsCaseSensitive() throws Exception {
		Mockito.when(
			_amImageHTMLTagFactory.create(
				"<img data-fileEntryId=\"1989\" src=\"adaptable\"/>",
				_pngFileEntry)
		).thenReturn(
			"<whatever></whatever>"
		);

		String html =
			"<div><div><img data-fileEntryId=\"1989\" src=\"adaptable\"/>" +
				"</div></div><br/>";

		Assert.assertEquals(html, _htmlContentTransformerImpl.transform(html));
	}

	private String _duplicateWithNewLine(String text) {
		return text + StringPool.NEW_LINE + text;
	}

	private void _setUpHtmlContentTransformerImpl() {
		AMImageMimeTypeProvider amImageMimeTypeProvider = Mockito.mock(
			AMImageMimeTypeProvider.class);

		Mockito.when(
			amImageMimeTypeProvider.isMimeTypeSupported(
				ContentTypes.APPLICATION_PDF)
		).thenReturn(
			false
		);

		Mockito.when(
			amImageMimeTypeProvider.isMimeTypeSupported(ContentTypes.IMAGE_PNG)
		).thenReturn(
			true
		);

		_htmlContentTransformerImpl = new HtmlContentTransformerImpl(
			_amImageHTMLTagFactory, amImageMimeTypeProvider,
			_dlAppLocalService);
	}

	private void _setUpPDFFileEntry() throws Exception {
		Mockito.when(
			_dlAppLocalService.getFileEntry(1999L)
		).thenReturn(
			_pdfFileEntry
		);

		Mockito.when(
			_pdfFileEntry.getMimeType()
		).thenReturn(
			ContentTypes.APPLICATION_PDF
		);
	}

	private void _setUpPNGFileEntry() throws Exception {
		Mockito.when(
			_dlAppLocalService.getFileEntry(1989L)
		).thenReturn(
			_pngFileEntry
		);

		Mockito.when(
			_pngFileEntry.getMimeType()
		).thenReturn(
			ContentTypes.IMAGE_PNG
		);
	}

	private final AMImageHTMLTagFactory _amImageHTMLTagFactory = Mockito.mock(
		AMImageHTMLTagFactory.class);
	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private HtmlContentTransformerImpl _htmlContentTransformerImpl;
	private final FileEntry _pdfFileEntry = Mockito.mock(FileEntry.class);
	private final FileEntry _pngFileEntry = Mockito.mock(FileEntry.class);

}