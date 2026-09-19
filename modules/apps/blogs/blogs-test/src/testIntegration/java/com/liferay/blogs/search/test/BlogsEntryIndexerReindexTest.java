/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.blogs.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class BlogsEntryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Assert.assertEquals(
			MODEL_INDEXER_CLASS.getName(), indexer.getClassName());

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		BlogsEntryFixture blogsEntryFixture = new BlogsEntryFixture(
			blogsEntryLocalService, group);

		_groups = groupSearchFixture.getGroups();
		_group = group;

		_blogsEntries = blogsEntryFixture.getBlogsEntries();
		_blogsEntryFixture = blogsEntryFixture;
	}

	@Test
	public void testReindex() throws Exception {
		BlogsEntry blogsEntry = _blogsEntryFixture.addEntry(
			RandomTestUtil.randomString());

		String searchTerm = blogsEntry.getTitle();

		assertFieldValue(Field.TITLE, searchTerm, searchTerm);

		deleteDocument(
			blogsEntry.getCompanyId(), uidFactory.getUID(blogsEntry));

		assertNoHits(searchTerm);

		reindexAllIndexerModels();

		assertFieldValue(Field.TITLE, searchTerm, searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValue(
		String fieldName, String fieldValue, String searchTerm) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, search(searchTerm));
	}

	protected void assertNoHits(String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), search(searchTerm));
	}

	protected void deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	protected void reindexAllIndexerModels() throws Exception {
		indexer.reindexCompany(_group.getCompanyId());
	}

	protected SearchResponse search(String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				MODEL_INDEXER_CLASS
			).queryString(
				searchTerm
			).build());
	}

	protected static final Class<?> MODEL_INDEXER_CLASS = BlogsEntry.class;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(filter = "indexer.class.name=com.liferay.blogs.model.BlogsEntry")
	protected Indexer<BlogsEntry> indexer;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UIDFactory uidFactory;

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private BlogsEntryFixture _blogsEntryFixture;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

}