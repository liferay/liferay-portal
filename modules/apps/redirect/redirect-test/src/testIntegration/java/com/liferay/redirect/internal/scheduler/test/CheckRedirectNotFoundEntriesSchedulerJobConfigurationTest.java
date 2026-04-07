/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.redirect.internal.scheduler.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.scheduler.SchedulerJobConfiguration;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.redirect.model.RedirectNotFoundEntry;
import com.liferay.redirect.service.RedirectNotFoundEntryLocalService;

import java.time.Duration;
import java.time.Instant;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 * @author Roberto Díaz
 */
@RunWith(Arquillian.class)
@Sync
public class CheckRedirectNotFoundEntriesSchedulerJobConfigurationTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testDeletesEntriesOlderThan30Days() throws Exception {
		Instant instant = Instant.now();

		_addOrUpdateRedirectNotFoundEntry(
			"url1", Date.from(instant.minus(Duration.ofDays(31))));

		RedirectNotFoundEntry redirectNotFoundEntry =
			_addOrUpdateRedirectNotFoundEntry(
				"url2", Date.from(instant.minus(Duration.ofDays(29))));

		List<RedirectNotFoundEntry> redirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			redirectNotFoundEntries.toString(), 2,
			redirectNotFoundEntries.size());

		Assert.assertEquals(2, _getRedirectNotFoundEntryCount());

		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();

		redirectNotFoundEntries =
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntries(
				_group.getGroupId(), null, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		Assert.assertEquals(
			redirectNotFoundEntries.toString(), 1,
			redirectNotFoundEntries.size());
		Assert.assertEquals(
			redirectNotFoundEntry, redirectNotFoundEntries.get(0));

		Assert.assertEquals(1, _getRedirectNotFoundEntryCount());
	}

	@Test
	public void testDeletesEntriesOverflowing1000Elements() throws Exception {
		for (int i = 0; i < 1001; i++) {
			_redirectNotFoundEntryLocalService.addOrUpdateRedirectNotFoundEntry(
				_group, "url" + i);
		}

		Assert.assertEquals(
			1001,
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntriesCount(
				_group.getGroupId()));
		Assert.assertEquals(1001, _getRedirectNotFoundEntryCount());

		UnsafeRunnable<Exception> jobExecutorUnsafeRunnable =
			_schedulerJobConfiguration.getJobExecutorUnsafeRunnable();

		jobExecutorUnsafeRunnable.run();

		Assert.assertEquals(
			1000,
			_redirectNotFoundEntryLocalService.getRedirectNotFoundEntriesCount(
				_group.getGroupId()));
		Assert.assertEquals(1000, _getRedirectNotFoundEntryCount());
	}

	private RedirectNotFoundEntry _addOrUpdateRedirectNotFoundEntry(
		String url, Date date) {

		RedirectNotFoundEntry redirectNotFoundEntry =
			_redirectNotFoundEntryLocalService.addOrUpdateRedirectNotFoundEntry(
				_group, url);

		redirectNotFoundEntry.setModifiedDate(date);

		return _redirectNotFoundEntryLocalService.updateRedirectNotFoundEntry(
			redirectNotFoundEntry);
	}

	private int _getRedirectNotFoundEntryCount() throws Exception {
		Indexer<RedirectNotFoundEntry> indexer = IndexerRegistryUtil.getIndexer(
			RedirectNotFoundEntry.class);

		Hits hits = indexer.search(
			SearchContextTestUtil.getSearchContext(_group.getGroupId()));

		return hits.getLength();
	}

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private RedirectNotFoundEntryLocalService
		_redirectNotFoundEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.redirect.internal.scheduler.CheckRedirectNotFoundEntriesSchedulerJobConfiguration"
	)
	private SchedulerJobConfiguration _schedulerJobConfiguration;

}