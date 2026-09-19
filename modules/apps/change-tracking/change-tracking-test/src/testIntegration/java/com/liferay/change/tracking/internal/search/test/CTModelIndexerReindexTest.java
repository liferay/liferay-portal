/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTModelIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, _group.getCompanyId(), TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());
	}

	@Test
	public void testReindex() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(_group.getGroupId());

		BlogsEntry productionBlogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), serviceContext);

		FieldValuesAssert.assertFieldValue(
			Field.TITLE, productionBlogsEntry.getTitle(),
			_search(productionBlogsEntry.getTitle()));

		BlogsEntry ctCollectionBlogsEntry;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			ctCollectionBlogsEntry = _blogsEntryLocalService.updateEntry(
				TestPropsValues.getUserId(), productionBlogsEntry.getEntryId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				serviceContext);

			FieldValuesAssert.assertFieldValue(
				Field.TITLE, ctCollectionBlogsEntry.getTitle(),
				_search(ctCollectionBlogsEntry.getTitle()));
		}

		_indexWriterHelper.reindex(
			TestPropsValues.getUserId(), "reindex",
			new long[] {_group.getCompanyId()}, BlogsEntry.class.getName(),
			null);

		FieldValuesAssert.assertFieldValue(
			Field.TITLE, productionBlogsEntry.getTitle(),
			_search(productionBlogsEntry.getTitle()));

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			FieldValuesAssert.assertFieldValue(
				Field.TITLE, ctCollectionBlogsEntry.getTitle(),
				_search(ctCollectionBlogsEntry.getTitle()));
		}
	}

	private SearchResponse _search(String searchTerm) {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).modelIndexerClasses(
				BlogsEntry.class
			).queryString(
				searchTerm
			).build());
	}

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

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