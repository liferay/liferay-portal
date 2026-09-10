/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.internal.helper.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.BooleanClause;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.MatchAllQuery;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.TermRangeQuery;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.internal.constants.SegmentsDestinationNames;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rachael Koestartyo
 */
@RunWith(Arquillian.class)
public class UserSegmentsEntryIdsSearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		Criteria criteria = new Criteria();

		_segmentsCriteriaContributor.contribute(
			criteria, String.format("(roleIds eq '%s')", _role.getRoleId()),
			Criteria.Conjunction.AND);

		_segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		ServiceContextThreadLocal.pushServiceContext(
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	public void testSearchSegmentsEntryIdsWithBooleanClauses()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"INDEX_SEARCH_LIMIT", _INDEX_SEARCH_LIMIT)) {

			Set<Long> expectedClassPKs = _addUsers(
				_INDEX_SEARCH_LIMIT * 3);

			_invokeMessageListener();

			Assert.assertEquals(expectedClassPKs, _searchClassPKs());
		}
	}

	@Test
	public void testSearchSegmentsEntryIdsWithoutBooleanClauses()
		throws Exception {

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"INDEX_SEARCH_LIMIT", _INDEX_SEARCH_LIMIT)) {

			Set<Long> expectedClassPKs = _addUsers(_INDEX_SEARCH_LIMIT - 1);

			_invokeMessageListener();

			SearchContext searchContext = _createSearchContext();

			Hits hits = _indexer.search(searchContext);

			Assert.assertEquals(expectedClassPKs, _toClassPKs(hits.getDocs()));
		}
	}

	private Set<Long> _addUsers(int count) throws Exception {
		Set<Long> userIds = new HashSet<>();

		for (int i = 0; i < count; i++) {
			User user = UserTestUtil.addUser();

			_users.add(user);

			_roleLocalService.addUserRole(user.getUserId(), _role);

			userIds.add(user.getUserId());
		}

		return userIds;
	}

	private SearchContext _createSearchContext() {
		SearchContext searchContext = new SearchContext();

		searchContext.setAttribute(
			"segmentsEntryIds",
			new long[] {_segmentsEntry.getSegmentsEntryId()});
		searchContext.setCompanyId(_segmentsEntry.getCompanyId());
		searchContext.setEnd(_INDEX_SEARCH_LIMIT);
		searchContext.setSorts(
			new Sort(Field.ENTRY_CLASS_PK, Sort.LONG_TYPE, false));
		searchContext.setStart(0);

		return searchContext;
	}

	private BooleanQuery _getLastDocumentBooleanQuery(Document lastDocument) {
		BooleanQuery booleanQuery = new BooleanQuery();

		booleanQuery.add(new MatchAllQuery(), BooleanClauseOccur.MUST);
		booleanQuery.add(
			new TermRangeQuery(
				Field.ENTRY_CLASS_PK, lastDocument.get(Field.ENTRY_CLASS_PK),
				null, false, true),
			BooleanClauseOccur.MUST);

		return booleanQuery;
	}

	private void _invokeMessageListener() throws Exception {
		Message message = new Message();

		message.put("companyId", _segmentsEntry.getCompanyId());
		message.put("segmentsEntryId", _segmentsEntry.getSegmentsEntryId());

		_messageListener.receive(message);
	}

	private Set<Long> _searchClassPKs() throws Exception {
		Set<Long> classPKs = new HashSet<>();

		SearchContext searchContext = _createSearchContext();

		Document lastDocument = null;

		while (true) {
			if (lastDocument != null) {
				searchContext.setBooleanClauses(
					new BooleanClause[] {
						new BooleanClause<>(
							_getLastDocumentBooleanQuery(lastDocument),
							BooleanClauseOccur.MUST)
					});
			}

			Hits hits = _indexer.search(searchContext);

			Document[] documents = hits.getDocs();

			if (documents.length == 0) {
				break;
			}

			int previousSize = classPKs.size();

			classPKs.addAll(_toClassPKs(documents));

			if (classPKs.size() == previousSize) {
				break;
			}

			lastDocument = documents[documents.length - 1];
		}

		return classPKs;
	}

	private Set<Long> _toClassPKs(Document[] documents) {
		Set<Long> classPKs = new HashSet<>();

		for (Document document : documents) {
			classPKs.add(
				GetterUtil.getLong(document.get(Field.ENTRY_CLASS_PK)));
		}

		return classPKs;
	}

	private static final int _INDEX_SEARCH_LIMIT = 5;

	@DeleteAfterTestRun
	private Group _group;

	@Inject(filter = "indexer.class.name=com.liferay.portal.kernel.model.User")
	private Indexer<User> _indexer;

	@Inject(
		filter = "destination.name=" + SegmentsDestinationNames.SEGMENTS_ENTRY_REINDEX
	)
	private MessageListener _messageListener;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _segmentsCriteriaContributor;

	@DeleteAfterTestRun
	private SegmentsEntry _segmentsEntry;

	@DeleteAfterTestRun
	private final List<User> _users = new ArrayList<>();

}