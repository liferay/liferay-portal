/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alicia García
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntrySearchByStatusTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testSearchFileApprovedStatus() throws Exception {
		String titlePrefix = "Document";

		_addFileEntry(null, titlePrefix + StringUtil.randomString());

		Date displayDate = new Date(System.currentTimeMillis() + Time.DAY);

		_addFileEntry(
			displayDate, titlePrefix + " " + StringUtil.randomString());

		_assertHits(1, titlePrefix, true, WorkflowConstants.STATUS_APPROVED);
	}

	@Test
	public void testSearchFileNoStatusOnlyApproved() throws Exception {
		String titlePrefix = "Document";

		_addFileEntry(null, titlePrefix + " " + StringUtil.randomString());

		Date displayDate = new Date(System.currentTimeMillis() + Time.DAY);

		_addFileEntry(displayDate, titlePrefix + StringUtil.randomString());

		_assertHits(1, titlePrefix, false, WorkflowConstants.STATUS_ANY);
	}

	private void _addFileEntry(Date displayDate, String title)
		throws Exception {

		_dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), _group.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			StringUtil.randomString(), ContentTypes.APPLICATION_OCTET_STREAM,
			title, StringPool.BLANK, StringUtil.randomString(),
			StringUtil.randomString(), new byte[0], displayDate, null, null,
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));
	}

	private void _assertHits(
			int expectedCount, String keywords, boolean setStatus, int status)
		throws Exception {

		SearchContext searchContext = SearchContextTestUtil.getSearchContext(
			_group.getGroupId());

		if (setStatus) {
			searchContext.setAttribute("status", status);
		}

		searchContext.setKeywords(keywords);

		Indexer<DLFileEntry> indexer = _indexerRegistry.getIndexer(
			DLFileEntry.class);

		Hits hits = indexer.search(searchContext);

		Assert.assertEquals(
			searchContext.getAttribute("queryString") + "->" + hits,
			expectedCount, hits.getLength());
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private IndexerRegistry _indexerRegistry;

}