/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.adaptive.media.image.internal.exportimport.content.processor;

import com.liferay.adaptive.media.image.html.AMImageHTMLTagFactory;
import com.liferay.document.library.kernel.exception.NoSuchFileEntryException;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.exportimport.kernel.lar.ExportImportPathUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.StagedModel;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Adolfo Pérez
 */
public class AMImageHTMLExportImportContentProcessorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		ReflectionTestUtil.setFieldValue(
			_amImageHTMLExportImportContentProcessor,
			"_amEmbeddedReferenceSetFactory", _amEmbeddedReferenceSetFactory);
		ReflectionTestUtil.setFieldValue(
			_amImageHTMLExportImportContentProcessor, "_amImageHTMLTagFactory",
			_amImageHTMLTagFactory);
		ReflectionTestUtil.setFieldValue(
			_amImageHTMLExportImportContentProcessor, "_dlAppLocalService",
			_dlAppLocalService);

		Mockito.doReturn(
			_amEmbeddedReferenceSet
		).when(
			_amEmbeddedReferenceSetFactory
		).create(
			Mockito.any(PortletDataContext.class),
			Mockito.any(StagedModel.class)
		);

		Mockito.doThrow(
			NoSuchFileEntryException.class
		).when(
			_dlAppLocalService
		).getFileEntry(
			Mockito.anyLong()
		);

		_setUpFileEntryToExport(_FILE_ENTRY_ID_1, _fileEntry1);
		_setUpFileEntryToImport(_FILE_ENTRY_ID_1, _fileEntry1);
		_setUpFileEntryToExport(_FILE_ENTRY_ID_2, _fileEntry2);
		_setUpFileEntryToImport(_FILE_ENTRY_ID_2, _fileEntry2);
	}

	@After
	public void tearDown() {
		_exportImportPathUtilMockedStatic.close();
	}

	@Test
	public void testExportContentWithNoReferencesDoesNotEscape()
		throws Exception {

		String content = StringPool.AMPERSAND;

		Assert.assertEquals(
			_amImageHTMLExportImportContentProcessor.
				replaceExportContentReferences(
					_portletDataContext, _stagedModel, content, false, false),
			_amImageHTMLExportImportContentProcessor.
				replaceExportContentReferences(
					_portletDataContext, Mockito.mock(StagedModel.class),
					content, false, true));
	}

	@Test
	public void testExportContentWithReferenceDoesNotEscape() throws Exception {
		StringBundler sb = new StringBundler(5);

		sb.append("&<img data-fileentryid=\"");
		sb.append(_FILE_ENTRY_ID_1);
		sb.append("\" src=\"");
		sb.append(RandomTestUtil.randomString());
		sb.append("\" />&");

		Assert.assertEquals(
			_amImageHTMLExportImportContentProcessor.
				replaceExportContentReferences(
					_portletDataContext, _stagedModel, sb.toString(), false,
					false),
			_amImageHTMLExportImportContentProcessor.
				replaceExportContentReferences(
					_portletDataContext, _stagedModel, sb.toString(), false,
					true));
	}

	@Test
	public void testExportContentWithStaticReferenceDoesNotEscape()
		throws Exception {

		String content =
			"&<picture data-fileentryid=\"" + _FILE_ENTRY_ID_1 +
				"\"></picture>&";

		Assert.assertEquals(
			_amImageHTMLExportImportContentProcessor.
				replaceExportContentReferences(
					_portletDataContext, _stagedModel, content, false, false),
			_amImageHTMLExportImportContentProcessor.
				replaceExportContentReferences(
					_portletDataContext, _stagedModel, content, false, true));
	}

	@Test
	public void testExportImportContentWithMultipleReferences()
		throws Exception {

		String prefix = RandomTestUtil.randomString();
		String infix = RandomTestUtil.randomString();
		String suffix = RandomTestUtil.randomString();

		String urlFileEntry1 = RandomTestUtil.randomString();
		String urlFileEntry2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				prefix, "<img src=\"", urlFileEntry1, "\" data-fileentryid=\"",
				_FILE_ENTRY_ID_1, "\" />", infix, "<img src=\"", urlFileEntry2,
				"\" data-fileentryid=\"", _FILE_ENTRY_ID_2, "\" />", suffix),
			_import(
				_export(
					StringBundler.concat(
						prefix, "<img data-fileentryid=\"", _FILE_ENTRY_ID_1,
						"\" src=\"", urlFileEntry1, "\" />", infix,
						"<img data-fileentryid=\"", _FILE_ENTRY_ID_2,
						"\" src=\"", urlFileEntry2, "\" />", suffix))));
	}

	@Test
	public void testExportImportContentWithMultipleStaticReferences()
		throws Exception {

		String prefix = RandomTestUtil.randomString();
		String infix = RandomTestUtil.randomString();
		String suffix = RandomTestUtil.randomString();

		String urlFileEntry1 = RandomTestUtil.randomString();
		String urlFileEntry2 = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				prefix, "<picture data-fileentryid=\"", _FILE_ENTRY_ID_1,
				"\"><source /><img src=\"", urlFileEntry1,
				"\" data-fileentryid=\"", _FILE_ENTRY_ID_1, "\" /></picture>",
				infix, "<picture data-fileentryid=\"", _FILE_ENTRY_ID_2,
				"\"><source /><img src=\"", urlFileEntry2,
				"\" data-fileentryid=\"", _FILE_ENTRY_ID_2, "\" /></picture>",
				suffix),
			_import(
				_export(
					StringBundler.concat(
						prefix, "<picture data-fileentryid=\"",
						_FILE_ENTRY_ID_1, "\"><img src=\"", urlFileEntry1,
						"\" data-fileentryid=\"", _FILE_ENTRY_ID_1,
						"\" /></picture>", infix,
						"<picture data-fileentryid=\"", _FILE_ENTRY_ID_2,
						"\"><img src=\"", urlFileEntry2,
						"\" data-fileentryid=\"", _FILE_ENTRY_ID_2,
						"\" /></picture>", suffix))));
	}

	@Test
	public void testExportImportContentWithNoReferences() throws Exception {
		String content = RandomTestUtil.randomString();

		String importedContent = _import(_export(content));

		Assert.assertEquals(content, importedContent);
	}

	@Test
	public void testExportImportContentWithReference() throws Exception {
		String prefix = RandomTestUtil.randomString();
		String suffix = RandomTestUtil.randomString();

		StringBundler expectedSB = new StringBundler(7);

		String urlFileEntry1 = RandomTestUtil.randomString();

		expectedSB.append(prefix);
		expectedSB.append("<img src=\"");
		expectedSB.append(urlFileEntry1);
		expectedSB.append("\" data-fileentryid=\"");
		expectedSB.append(_FILE_ENTRY_ID_1);
		expectedSB.append("\" />");
		expectedSB.append(suffix);

		Assert.assertEquals(
			expectedSB.toString(),
			_import(
				_export(
					StringBundler.concat(
						prefix, "<img data-fileentryid=\"", _FILE_ENTRY_ID_1,
						"\" src=\"", urlFileEntry1, "\" />", suffix))));
	}

	@Test
	public void testExportImportContentWithReferenceContainingMoreAttributes()
		throws Exception {

		String prefix = RandomTestUtil.randomString();
		String suffix = RandomTestUtil.randomString();

		String urlFileEntry1 = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				prefix, "<img attr1=\"1\" attr2=\"2\" src=\"", urlFileEntry1,
				"\" attr3=\"3\" data-fileentryid=\"", _FILE_ENTRY_ID_1, "\" />",
				suffix),
			_import(
				_export(
					StringBundler.concat(
						prefix, "<img attr1=\"1\" data-fileentryid=\"",
						_FILE_ENTRY_ID_1, "\" attr2=\"2\" src=\"",
						urlFileEntry1, "\" attr3=\"3\"/>", suffix))));
	}

	@Test
	public void testExportImportContentWithStaticReference() throws Exception {
		String prefix = RandomTestUtil.randomString();
		String suffix = RandomTestUtil.randomString();

		String urlFileEntry1 = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				prefix, "<picture data-fileentryid=\"", _FILE_ENTRY_ID_1,
				"\"><source /><img src=\"", urlFileEntry1,
				"\" data-fileentryid=\"", _FILE_ENTRY_ID_1, "\" /></picture>",
				suffix),
			_import(
				_export(
					StringBundler.concat(
						prefix, "<picture data-fileentryid=\"",
						_FILE_ENTRY_ID_1, "\"><img src=\"", urlFileEntry1,
						"\" data-fileentryid=\"", _FILE_ENTRY_ID_1,
						"\" /></picture>", suffix))));
	}

	@Test
	public void testExportImportContentWithStaticReferenceContainingImageWithAttributes()
		throws Exception {

		String urlFileEntry1 = RandomTestUtil.randomString();

		Assert.assertEquals(
			StringBundler.concat(
				"<picture data-fileentryid=\"", _FILE_ENTRY_ID_1,
				"\"><source /><img src=\"", urlFileEntry1,
				"\" class=\"pretty\" data-fileentryid=\"", _FILE_ENTRY_ID_1,
				"\" /></picture>"),
			_import(
				_export(
					StringBundler.concat(
						"<picture data-fileentryid=\"", _FILE_ENTRY_ID_1,
						"\"><img src=\"", urlFileEntry1,
						"\" class=\"pretty\" data-fileentryid=\"",
						_FILE_ENTRY_ID_1, "\" /></picture>"))));
	}

	@Test
	public void testImportContentIgnoresInvalidReferences() throws Exception {
		Mockito.doReturn(
			null
		).when(
			_dlAppLocalService
		).fetchFileEntry(
			_FILE_ENTRY_ID_1
		);

		String content =
			"<img export-import-path=\"PATH_" + _FILE_ENTRY_ID_1 + "\" />";

		Assert.assertEquals(
			content,
			_amImageHTMLExportImportContentProcessor.
				replaceImportContentReferences(
					_portletDataContext, _stagedModel, content));
	}

	@Test
	public void testImportContentIgnoresInvalidStaticReferences()
		throws Exception {

		String content = "<picture export-import-path=\"#@¢∞\"></picture>";

		Assert.assertEquals(
			content,
			_amImageHTMLExportImportContentProcessor.
				replaceImportContentReferences(
					_portletDataContext, _stagedModel, content));
	}

	@Test
	public void testImportContentIgnoresReferencesWithMissingPaths()
		throws Exception {

		String content = "<img export-import-path=\"#@¢∞\" />";

		Assert.assertEquals(
			content,
			_amImageHTMLExportImportContentProcessor.
				replaceImportContentReferences(
					_portletDataContext, _stagedModel, content));
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testValidateContentFailsWithInvalidReferences()
		throws Exception {

		_amImageHTMLExportImportContentProcessor.validateContentReferences(
			RandomTestUtil.randomLong(),
			StringBundler.concat(
				"<img data-fileentryid=\"", RandomTestUtil.randomLong(),
				"\" src=\"PATH_", _FILE_ENTRY_ID_1, "\" />"));
	}

	@Test(expected = NoSuchFileEntryException.class)
	public void testValidateContentFailsWithInvalidStaticReferences()
		throws Exception {

		String content =
			"<picture data-fileentryid=\"" + RandomTestUtil.randomLong() +
				"\"></picture>";

		_amImageHTMLExportImportContentProcessor.validateContentReferences(
			RandomTestUtil.randomLong(), content);
	}

	@Test
	public void testValidateContentSucceedsWhenAllReferencesAreValid()
		throws Exception {

		_amImageHTMLExportImportContentProcessor.validateContentReferences(
			RandomTestUtil.randomLong(),
			StringBundler.concat(
				"<img data-fileentryid=\"", _FILE_ENTRY_ID_1, "\" src=\"PATH_",
				_FILE_ENTRY_ID_1, "\" />"));
	}

	@Test
	public void testValidateContentSucceedsWhenAllStaticReferencesAreValid()
		throws Exception {

		Mockito.doReturn(
			_fileEntry1
		).when(
			_dlAppLocalService
		).getFileEntry(
			_FILE_ENTRY_ID_1
		);

		String content =
			"<picture data-fileentryid=\"" + _FILE_ENTRY_ID_1 + "\"></picture>";

		_amImageHTMLExportImportContentProcessor.validateContentReferences(
			RandomTestUtil.randomLong(), content);
	}

	private String _export(String content) throws Exception {
		return _amImageHTMLExportImportContentProcessor.
			replaceExportContentReferences(
				_portletDataContext, _stagedModel, content, false, false);
	}

	private String _import(String exportedContent) throws Exception {
		return _amImageHTMLExportImportContentProcessor.
			replaceImportContentReferences(
				_portletDataContext, _stagedModel, exportedContent);
	}

	private void _setUpFileEntryToExport(long fileEntryId, FileEntry fileEntry)
		throws Exception {

		Mockito.doReturn(
			fileEntry
		).when(
			_dlAppLocalService
		).getFileEntry(
			fileEntryId
		);

		Mockito.when(
			ExportImportPathUtil.getModelPath(fileEntry)
		).thenReturn(
			"PATH_" + fileEntryId
		);
	}

	private void _setUpFileEntryToImport(long fileEntryId, FileEntry fileEntry)
		throws Exception {

		Mockito.doReturn(
			true
		).when(
			_amEmbeddedReferenceSet
		).containsReference(
			"PATH_" + fileEntryId
		);

		Mockito.doReturn(
			fileEntryId
		).when(
			_amEmbeddedReferenceSet
		).importReference(
			"PATH_" + fileEntryId
		);

		Mockito.doReturn(
			fileEntry
		).when(
			_dlAppLocalService
		).fetchFileEntry(
			fileEntryId
		);

		Mockito.when(
			_amImageHTMLTagFactory.create(
				Mockito.anyString(), Mockito.eq(fileEntry))
		).thenAnswer(
			invocation -> {
				String imgTag = invocation.getArgument(0, String.class);

				return "<picture><source/>" + imgTag + "</picture>";
			}
		);
	}

	private static final long _FILE_ENTRY_ID_1 = RandomTestUtil.randomLong();

	private static final long _FILE_ENTRY_ID_2 = RandomTestUtil.randomLong();

	private final AMEmbeddedReferenceSet _amEmbeddedReferenceSet = Mockito.mock(
		AMEmbeddedReferenceSet.class);
	private final AMEmbeddedReferenceSetFactory _amEmbeddedReferenceSetFactory =
		Mockito.mock(AMEmbeddedReferenceSetFactory.class);
	private final AMImageHTMLExportImportContentProcessor
		_amImageHTMLExportImportContentProcessor =
			new AMImageHTMLExportImportContentProcessor();
	private final AMImageHTMLTagFactory _amImageHTMLTagFactory = Mockito.mock(
		AMImageHTMLTagFactory.class);
	private final DLAppLocalService _dlAppLocalService = Mockito.mock(
		DLAppLocalService.class);
	private final MockedStatic<ExportImportPathUtil>
		_exportImportPathUtilMockedStatic = Mockito.mockStatic(
			ExportImportPathUtil.class);
	private final FileEntry _fileEntry1 = Mockito.mock(FileEntry.class);
	private final FileEntry _fileEntry2 = Mockito.mock(FileEntry.class);
	private final PortletDataContext _portletDataContext = Mockito.mock(
		PortletDataContext.class);
	private final StagedModel _stagedModel = Mockito.mock(StagedModel.class);

}