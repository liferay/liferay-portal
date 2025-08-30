/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roselaine Marques
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateCollectionIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layoutPageTemplateCollectionFixture =
			new LayoutPageTemplateCollectionFixture(
				_group, _layoutPageTemplateCollectionLocalService);

		_layoutPageTemplateCollections =
			_layoutPageTemplateCollectionFixture.
				getLayoutPageTemplateCollections();
	}

	@Test
	public void testReindex() throws Exception {
		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionFixture.
				createLayoutPageTemplateCollection();

		String searchTerm = layoutPageTemplateCollection.getName();

		_assertFieldValue(Field.NAME, searchTerm, searchTerm);

		_deleteDocument(
			layoutPageTemplateCollection.getCompanyId(),
			uidFactory.getUID(layoutPageTemplateCollection));

		_assertNoHits(searchTerm);

		_reindex(layoutPageTemplateCollection);

		_assertFieldValue(Field.NAME, searchTerm, searchTerm);

		_reindexAllIndexerModels();

		_assertFieldValue(Field.NAME, searchTerm, searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject(
		filter = "indexer.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateCollection"
	)
	protected Indexer<LayoutPageTemplateCollection> indexer;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject
	protected UIDFactory uidFactory;

	private void _assertFieldValue(
		String fieldName, String fieldValue, String searchTerm) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, _search(searchTerm));
	}

	private void _assertNoHits(String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), _search(searchTerm));
	}

	private void _deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	private void _reindex(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		indexer.reindex(layoutPageTemplateCollection);
	}

	private void _reindexAllIndexerModels() throws Exception {
		indexer.reindex(new String[] {String.valueOf(_group.getCompanyId())});
	}

	private SearchResponse _search(String searchTerm) {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				LayoutPageTemplateCollection.class
			).queryString(
				searchTerm
			).build());
	}

	@DeleteAfterTestRun
	private Group _group;

	private LayoutPageTemplateCollectionFixture
		_layoutPageTemplateCollectionFixture;

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@DeleteAfterTestRun
	private List<LayoutPageTemplateCollection> _layoutPageTemplateCollections;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}