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
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.exportimport.kernel.lar.ExportImportThreadLocal;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.io.unsync.UnsyncByteArrayInputStream;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileEntry;
import com.liferay.portal.repository.liferayrepository.model.LiferayFileVersion;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
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

	@Before
	public void setUp() throws Exception {
		_addDLFileEntry();
		_addJournalArticle();
	}

	@Test
	public void testUpgradeProcess() throws Exception {
		_runUpgrade();

		JournalArticle journalArticle =
			_journalArticleLocalService.getJournalArticle(
				_journalArticle.getId());

		_assertContains(
			journalArticle.getContent(), "/documents/d/orphan/document");
		_assertContains(
			journalArticle.getContent(),
			"data-fileentryid=\"" + _dlFileEntry.getFileEntryId() + "\"");
	}

	private void _addDLFileEntry() throws Exception {
		_dlFileEntry = _dlFileEntryLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), TestPropsValues.getGroupId(),
			TestPropsValues.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, StringUtil.randomId(),
			ContentTypes.IMAGE_PNG, StringUtil.randomString(),
			StringUtil.randomString(), StringPool.BLANK, StringPool.BLANK,
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT, null,
			null, new UnsyncByteArrayInputStream(new byte[0]), 0, null, null,
			null,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId()));
	}

	private void _addJournalArticle() throws Exception {
		boolean portletImportInProcess =
			ExportImportThreadLocal.isPortletImportInProcess();

		try {
			ExportImportThreadLocal.setPortletImportInProcess(true);

			_journalArticle = JournalTestUtil.addArticleWithXMLContent(
				DDMStructureTestUtil.getSampleStructuredContent(
					"content",
					Collections.singletonList(
						HashMapBuilder.put(
							LocaleUtil.US,
							StringBundler.concat(
								"<img src=\"",
								DLURLHelperUtil.getPreviewURL(
									new LiferayFileEntry(_dlFileEntry),
									new LiferayFileVersion(
										_dlFileEntry.getFileVersion()),
									null, null),
								"\"/><img src=\"/documents/d/orphan/document\"",
								"/>")
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

	@Inject(
		filter = "(&(component.name=com.liferay.dynamic.data.mapping.internal.upgrade.registry.DDMServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

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

}