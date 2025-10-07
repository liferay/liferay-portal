/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTScoreLocalService;
import com.liferay.document.library.kernel.model.DLFolder;
import com.liferay.document.library.kernel.service.DLFileEntryLocalService;
import com.liferay.document.library.test.util.DLTestUtil;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.dynamic.data.mapping.test.util.DDMStructureTestUtil;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author David Truong
 */
@RunWith(Arquillian.class)
public class CTScoreLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_ctCollection1 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);
		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);
		_ctCollection3 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, CTCollectionLocalServiceTest.class.getSimpleName(), null);
		_group = GroupTestUtil.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(_group);
	}

	@Test
	public void testAddCTScore() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			_blogsEntryLocalService.addEntry(
				TestPropsValues.getUserId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), serviceContext);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			DDMStructure ddmStructure =
				_ddmStructureLocalService.fetchStructure(
					_group.getGroupId(),
					_portal.getClassNameId(JournalArticle.class.getName()),
					"BASIC-WEB-CONTENT", true);

			_journalArticleLocalService.addArticle(
				null, TestPropsValues.getUserId(), _group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				DDMStructureTestUtil.getSampleStructuredContent(),
				ddmStructure.getStructureId(), "BASIC-WEB-CONTENT",
				serviceContext);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection3.getCtCollectionId())) {

			DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

			for (int i = 0; i < 5; i++) {
				DLTestUtil.addDLFileEntry(dlFolder.getFolderId());
			}
		}

		_ctCollection1 = _ctCollectionLocalService.fetchCTCollection(
			_ctCollection1.getCtCollectionId());

		Assert.assertTrue(_ctCollection1.getScore() > 0);

		_ctCollection2 = _ctCollectionLocalService.fetchCTCollection(
			_ctCollection2.getCtCollectionId());

		Assert.assertTrue(
			_ctCollection2.getScore() > _ctCollection1.getScore());

		_ctCollection3 = _ctCollectionLocalService.fetchCTCollection(
			_ctCollection3.getCtCollectionId());

		Assert.assertTrue(
			_ctCollection3.getScore() > _ctCollection2.getScore());
	}

	@Test
	public void testUpdateCTScore() throws Throwable {
		_ctCollection1 = _ctCollectionLocalService.fetchCTCollection(
			_ctCollection1.getCtCollectionId());

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
					"com.liferay.portal.internal.increment." +
						"BufferedIncrementRunnable",
					LoggerTestUtil.ERROR)) {

				DLFolder dlFolder = DLTestUtil.addDLFolder(_group.getGroupId());

				TransactionConfig transactionConfig =
					TransactionConfig.Factory.create(
						Propagation.REQUIRED, new Class<?>[] {Exception.class});

				TransactionInvokerUtil.invoke(
					transactionConfig,
					() -> {
						_ctScoreLocalService.incrementScore(
							dlFolder.getCtCollectionId(), 10);

						_ctScoreLocalService.incrementScore(
							dlFolder.getCtCollectionId(), 10);

						return null;
					});

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 0, logEntries.size());
			}
		}
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private static CTScoreLocalService _ctScoreLocalService;

	@Inject
	private static DLFileEntryLocalService _dlFileEntryLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@DeleteAfterTestRun
	private CTCollection _ctCollection3;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@Inject
	private Portal _portal;

}