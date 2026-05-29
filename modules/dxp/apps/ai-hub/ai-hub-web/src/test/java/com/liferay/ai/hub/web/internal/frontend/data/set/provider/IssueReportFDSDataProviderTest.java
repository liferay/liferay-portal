/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.ai.hub.web.internal.frontend.data.set.provider;

import com.liferay.ai.hub.web.internal.frontend.data.set.model.IssueReportFDSEntry;
import com.liferay.frontend.data.set.provider.search.FDSKeywords;
import com.liferay.frontend.data.set.provider.search.FDSPagination;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Davyson Melo
 */
public class IssueReportFDSDataProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_issueReportFDSDataProvider = new IssueReportFDSDataProvider();
	}

	@Test
	public void testGetItemsCountMatchesGetItemsSizeWithoutFilters()
		throws Exception {

		FDSKeywords noKeywords = _keywords(null);

		int count = _issueReportFDSDataProvider.getItemsCount(noKeywords, null);

		List<IssueReportFDSEntry> entries =
			_issueReportFDSDataProvider.getItems(
				noKeywords, _pagination(0, count), null, null);

		Assert.assertEquals(count, entries.size());
	}

	@Test
	public void testGetItemsFiltersByUserEmail() throws Exception {
		List<IssueReportFDSEntry> entries =
			_issueReportFDSDataProvider.getItems(
				_keywords("amelia.cortez"), _pagination(0, 100), null, null);

		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(
			"amelia.cortez@example.com",
			entries.get(
				0
			).getUserEmail());
	}

	@Test
	public void testGetItemsFiltersByUserMessage() throws Exception {
		List<IssueReportFDSEntry> entries =
			_issueReportFDSDataProvider.getItems(
				_keywords("unsafe medication"), _pagination(0, 100), null,
				null);

		Assert.assertEquals(1, entries.size());
		Assert.assertEquals(
			"anonymous@example.com",
			entries.get(
				0
			).getUserEmail());
	}

	@Test
	public void testGetItemsPaginatesResults() throws Exception {
		FDSKeywords noKeywords = _keywords(null);

		int count = _issueReportFDSDataProvider.getItemsCount(noKeywords, null);

		Assert.assertTrue(
			"sample set must have at least 2 entries to exercise pagination",
			count >= 2);

		List<IssueReportFDSEntry> firstPage =
			_issueReportFDSDataProvider.getItems(
				noKeywords, _pagination(0, 1), null, null);
		List<IssueReportFDSEntry> secondPage =
			_issueReportFDSDataProvider.getItems(
				noKeywords, _pagination(1, 2), null, null);

		Assert.assertEquals(1, firstPage.size());
		Assert.assertEquals(1, secondPage.size());
		Assert.assertNotEquals(
			firstPage.get(
				0
			).getUserEmail(),
			secondPage.get(
				0
			).getUserEmail());
	}

	@Test
	public void testPositiveRowsHaveNullIssueTypeAndUserMessage()
		throws Exception {

		List<IssueReportFDSEntry> entries =
			_issueReportFDSDataProvider.getItems(
				_keywords(null), _pagination(0, 100), null, null);

		long positiveRowCount = entries.stream(
		).filter(
			entry -> "POSITIVE".equals(entry.getFeedbackType())
		).peek(
			entry -> {
				Assert.assertNull(
					"positive row issueType must be null",
					entry.getIssueType());
				Assert.assertNull(
					"positive row userMessage must be null",
					entry.getUserMessage());
			}
		).count();

		Assert.assertTrue(
			"sample set must include at least one positive row",
			positiveRowCount > 0);
	}

	private FDSKeywords _keywords(String keywords) {
		return () -> keywords;
	}

	private FDSPagination _pagination(int startPosition, int endPosition) {
		return new FDSPagination() {

			@Override
			public int getEndPosition() {
				return endPosition;
			}

			@Override
			public int getPage() {
				return 1;
			}

			@Override
			public int getPageSize() {
				return endPosition - startPosition;
			}

			@Override
			public int getStartPosition() {
				return startPosition;
			}

		};
	}

	private IssueReportFDSDataProvider _issueReportFDSDataProvider;

}