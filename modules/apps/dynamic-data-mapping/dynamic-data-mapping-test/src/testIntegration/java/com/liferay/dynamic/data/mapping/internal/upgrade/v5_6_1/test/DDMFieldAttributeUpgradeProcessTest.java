/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.internal.upgrade.v5_6_1.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.util.DLURLHelperUtil;
import com.liferay.dynamic.data.mapping.model.DDMFieldAttribute;
import com.liferay.dynamic.data.mapping.service.DDMFieldLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class DDMFieldAttributeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testUpgradeProcess() throws Exception {
		_addDLFileEntry(StringUtil.randomId());
		_addJournalArticle(
			StringBundler.concat(
				"<img src=\"",
				DLURLHelperUtil.getPreviewURL(
					new LiferayFileEntry(_dlFileEntry),
					new LiferayFileVersion(_dlFileEntry.getFileVersion()), null,
					null),
				"\"/><img src=\"/documents/d/orphan/document\"/><p>",
				RandomTestUtil.randomString(100), "</p>"));

		_assertDDMFieldAttribute(
			ddmFieldAttribute -> {
				Assert.assertTrue(
					Validator.isNull(
						ddmFieldAttribute.getLargeAttributeValue()));
				Assert.assertFalse(
					Validator.isNull(
						ddmFieldAttribute.getSmallAttributeValue()));
			});

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME, LoggerTestUtil.WARN)) {

			_runUpgrade();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(LoggerTestUtil.WARN, logEntry.getPriority());

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				NoSuchUserException.class, throwable.getClass());
		}

		_assertDDMFieldAttribute(
			ddmFieldAttribute -> {
				Assert.assertFalse(
					Validator.isNull(
						ddmFieldAttribute.getLargeAttributeValue()));
				Assert.assertTrue(
					Validator.isNull(
						ddmFieldAttribute.getSmallAttributeValue()));
			});

		JournalArticle journalArticle =
			_journalArticleLocalService.getJournalArticle(
				_journalArticle.getId());

		_assertContains(
			journalArticle.getContent(), "/documents/d/orphan/document");
		_assertContains(
			journalArticle.getContent(),
			"data-fileentryid=\"" + _dlFileEntry.getFileEntryId() + "\"");
	}

	@Test
	public void testUpgradeProcessWithExistingFileEntryIdAttribute()
		throws Exception {

		_addDLFileEntry(StringUtil.randomId());

		_addJournalArticle(
			StringBundler.concat(
				"<img data-fileentryid=\"0\" src=\"", _getPreviewURL(),
				"\"/>"));

		_runUpgrade();

		JournalArticle journalArticle = _getJournalArticle();

		_assertContains(journalArticle.getContent(), "data-fileentryid=\"0\"");
		_assertNotContains(
			journalArticle.getContent(),
			"data-fileentryid=\"" + _dlFileEntry.getFileEntryId() + "\"");
	}

	@Test
	public void testUpgradeProcessWithHTMLDocumentValue() throws Exception {
		_addDLFileEntry(StringUtil.randomId());

		_addJournalArticle(
			StringBundler.concat(
				"<html lang=\"en\"><head><title>",
				RandomTestUtil.randomString(), "</title></head><body><img ",
				"src=\"", _getPreviewURL(), "\"/></body></html>"));

		_runUpgrade();

		JournalArticle journalArticle = _getJournalArticle();

		_assertContains(journalArticle.getContent(), "<html lang=\"en\">");
		_assertContains(
			journalArticle.getContent(),
			"data-fileentryid=\"" + _dlFileEntry.getFileEntryId() + "\"");
	}

	@Test
	public void testUpgradeProcessWithImgTagInsidePictureElement()
		throws Exception {

		_addDLFileEntry(StringUtil.randomId());

		String pictureElement = StringBundler.concat(
			"<picture><img src=\"", _getPreviewURL(), "\"/></picture>");

		_addJournalArticle(pictureElement);

		_runUpgrade();

		JournalArticle journalArticle = _getJournalArticle();

		_assertContains(journalArticle.getContent(), pictureElement);
		_assertNotContains(journalArticle.getContent(), "data-fileentryid=");
	}

	@Test
	public void testUpgradeProcessWithLegacyEscapedImageURL() throws Exception {
		_addDLFileEntry("large file name.png");
		_addJournalArticle(
			StringBundler.concat(
				"<img src=\"/documents/", _dlFileEntry.getGroupId(),
				StringPool.SLASH, _dlFileEntry.getFolderId(), StringPool.SLASH,
				URLCodec.encodeURL(
					HtmlUtil.unescape(_dlFileEntry.getFileName())),
				"\"/>"));

		_runUpgrade();

		JournalArticle journalArticle =
			_journalArticleLocalService.getJournalArticle(
				_journalArticle.getId());

		_assertContains(
			journalArticle.getContent(),
			"data-fileentryid=\"" + _dlFileEntry.getFileEntryId() + "\"");
	}

	@Test
	public void testUpgradeProcessWithMarkupOutsideImgTags() throws Exception {
		_addDLFileEntry(StringUtil.randomId());

		String externalImgTag = "<img src=\"http://example.com/image.png\"/>";
		String paragraph = "<p>" + RandomTestUtil.randomString(100) + "</p>";

		_addJournalArticle(
			StringBundler.concat(
				paragraph, externalImgTag, "<img src=\"", _getPreviewURL(),
				"\"/>"));

		_runUpgrade();

		JournalArticle journalArticle = _getJournalArticle();

		_assertContains(journalArticle.getContent(), externalImgTag);
		_assertContains(journalArticle.getContent(), paragraph);
	}

	private void _addDLFileEntry(String sourceFileName) throws Exception {
		_dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, sourceFileName,
			ContentTypes.IMAGE_PNG, StringUtil.randomString(),
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new UnsyncByteArrayInputStream(new byte[0]), 0, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private void _addJournalArticle(String content) throws Exception {
		boolean portletImportInProcess =
			ExportImportThreadLocal.isPortletImportInProcess();

		try {
			ExportImportThreadLocal.setPortletImportInProcess(true);

			_journalArticle = JournalTestUtil.addArticleWithXMLContent(
				DDMStructureTestUtil.getSampleStructuredContent(
					"content",
					Collections.singletonList(
						HashMapBuilder.put(
							LocaleUtil.US, content
						).build()),
					LanguageUtil.getLanguageId(LocaleUtil.US)),
				"BASIC-WEB-CONTENT", "BASIC-WEB-CONTENT");
		}
		finally {
			ExportImportThreadLocal.setPortletImportInProcess(
				portletImportInProcess);
		}
	}

	private void _assertContains(String content, String fragment) {
		Assert.assertTrue(content, content.contains(fragment));
	}

	private void _assertDDMFieldAttribute(
			UnsafeConsumer<DDMFieldAttribute, Exception> unsafeConsumer)
		throws Exception {

		for (DDMFieldAttribute ddmFieldAttribute :
				_ddmFieldLocalService.getDDMFieldAttributes(
					_journalArticle.getId(), null)) {

			unsafeConsumer.accept(ddmFieldAttribute);
		}
	}

	private void _assertNotContains(String content, String fragment) {
		Assert.assertFalse(content, content.contains(fragment));
	}

	private JournalArticle _getJournalArticle() throws Exception {
		return _journalArticleLocalService.getJournalArticle(
			_journalArticle.getId());
	}

	private String _getPreviewURL() throws Exception {
		return DLURLHelperUtil.getPreviewURL(
			new LiferayFileEntry(_dlFileEntry),
			new LiferayFileVersion(_dlFileEntry.getFileVersion()), null, null);
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_multiVMPool.clear();
	}

	private static final String _CLASS_NAME =
		"com.liferay.dynamic.data.mapping.internal.upgrade.v5_6_1." +
			"DDMFieldAttributeUpgradeProcess";

	@Inject
	private DDMFieldLocalService _ddmFieldLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private DLFileEntry _dlFileEntry;

	@Inject
	private DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private EntityCache _entityCache;

	@DeleteAfterTestRun
	private JournalArticle _journalArticle;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}