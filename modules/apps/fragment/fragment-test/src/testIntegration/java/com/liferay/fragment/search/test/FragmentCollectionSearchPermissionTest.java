/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Thiago Buarque
 */
@RunWith(Arquillian.class)
public class FragmentCollectionSearchPermissionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addGroupUser(_group, RoleConstants.POWER_USER);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				_group, TestPropsValues.getUserId());

		_fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				RandomTestUtil.randomString(), TestPropsValues.getUserId(),
				_group.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, serviceContext);
	}

	@Test
	public void testIndexerSearchLogsNoError() throws Exception {
		_assertNoErrorLogged(() -> _indexer.search(_createSearchContext()));
	}

	@Test
	public void testMultipleIndexerSearchLogsNoError() throws Exception {
		_assertNoErrorLogged(
			() -> {
				SearchResponse searchResponse = _search(
					FragmentCollection.class, FragmentEntry.class);

				Assert.assertEquals(1, searchResponse.getTotalHits());
			});
	}

	@Test
	public void testSingleIndexerSearchLogsNoError() throws Exception {
		_assertNoErrorLogged(() -> _search(FragmentCollection.class));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertNoErrorLogged(UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		UserTestUtil.setUser(_user);

		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		Assert.assertFalse(
			permissionChecker.isCompanyAdmin(TestPropsValues.getCompanyId()));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.search.internal." +
					"SearchPermissionCheckerImpl",
				LoggerTestUtil.ERROR)) {

			unsafeRunnable.run();

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.toString(), logEntries.isEmpty());
		}
	}

	private SearchContext _createSearchContext() throws Exception {
		SearchContext searchContext = new SearchContext();

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setGroupIds(new long[] {_group.getGroupId()});
		searchContext.setKeywords(_fragmentCollection.getName());
		searchContext.setUserId(_user.getUserId());

		return searchContext;
	}

	private SearchResponse _search(Class<?>... clazzes) throws Exception {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).entryClassNames(
				TransformUtil.transformToArray(
					ListUtil.fromArray(clazzes), Class::getName, String.class)
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				clazzes
			).queryString(
				_fragmentCollection.getName()
			).withSearchContext(
				searchContext -> searchContext.setUserId(_user.getUserId())
			).build());
	}

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(
		filter = "indexer.class.name=com.liferay.fragment.model.FragmentCollection"
	)
	private Indexer<FragmentCollection> _indexer;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@DeleteAfterTestRun
	private User _user;

}