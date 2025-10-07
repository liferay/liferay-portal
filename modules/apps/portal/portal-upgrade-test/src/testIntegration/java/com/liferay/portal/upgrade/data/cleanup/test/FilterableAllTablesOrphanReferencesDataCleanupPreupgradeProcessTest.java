/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upgrade.data.cleanup.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.petra.function.UnsafeBiConsumer;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess;
import com.liferay.portal.kernel.upgrade.data.cleanup.util.OrphanReferencesDataCleanupUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.rule.Inject;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * @author Luis Ortiz
 */
@RunWith(Arquillian.class)
public class FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcessTest
	extends BaseOrphanReferencesDataCleanupPreupgradeProcessTestCase {

	@Before
	public void setUp() throws Exception {
		_companyId = RandomTestUtil.nextLong();
		_journalId = RandomTestUtil.nextLong();
	}

	@Override
	protected UnsafeRunnable<Exception> getInsertDataUnsafeRunnable() {
		return () -> db.runSQL(
			connection,
			StringBundler.concat(
				"insert into DDMTemplateLink (mvccVersion, ctCollectionId, ",
				"templateLinkId, companyId, classNameId, classPK, templateId) ",
				"values (0, 0, ", RandomTestUtil.nextLong(), ", ", _companyId,
				", ",
				_classNameLocalService.getClassNameId(
					JournalArticle.class.getName()),
				", ", _journalId, ", ", RandomTestUtil.nextLong(), ")"));
	}

	@Override
	protected UnsafeBiConsumer<LogCapture, LogCapture, Exception>
		getLogAssertionUnsafeBiConsumer() {

		return (logCapture1, logCapture2) -> {
			List<String> messages = logCapture1.getMessages();

			Assert.assertTrue(
				messages.contains(
					getExpectedMessage(
						1L, "classPK", "DDMTemplateLink",
						new String[] {"resourcePrimKey", "id_"},
						"JournalArticle", _journalId)));
		};
	}

	@Override
	protected String getLoggerClassName() {
		return OrphanReferencesDataCleanupUtil.class.getName();
	}

	@Override
	protected UpgradeProcess getUpgradeProcess() {
		return new FilterableAllTablesOrphanReferencesDataCleanupPreupgradeProcess(
			"classNameId = (select classNameId from ClassName_ where value = " +
				"'com.liferay.journal.model.JournalArticle')",
			new String[] {"classNameId"}, "classPK",
			new String[] {"resourcePrimKey", "id_"}, "JournalArticle");
	}

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private long _companyId;
	private long _journalId;

}