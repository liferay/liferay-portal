/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.List;

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
public class CTEntryIndexerReindexTest {

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
			0, RandomTestUtil.randomString(), null);
		_ctCollection2 = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), null);
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testReindex() throws Exception {
		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection1.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection2.getCtCollectionId())) {

			JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);
		}

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.change.tracking.internal.search." +
					"CTEntryModelSearchConfigurator",
				LoggerTestUtil.DEBUG)) {

			_indexWriterHelper.reindex(
				TestPropsValues.getUserId(), "reindex",
				new long[] {_group.getCompanyId()}, CTEntry.class.getName(),
				null);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertFalse(logEntries.isEmpty());

			LogEntry logEntry = logEntries.get(0);

			Assert.assertEquals(
				StringBundler.concat(
					"Restricting indexable results of ",
					CTEntry.class.getName(), " because this can only be ",
					"performed in production mode"),
				logEntry.getMessage());
		}
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection1;

	@DeleteAfterTestRun
	private CTCollection _ctCollection2;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	private Group _group;

	@Inject
	private IndexWriterHelper _indexWriterHelper;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}