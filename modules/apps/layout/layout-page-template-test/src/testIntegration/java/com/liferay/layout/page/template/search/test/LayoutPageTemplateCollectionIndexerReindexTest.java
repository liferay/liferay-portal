/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateCollectionTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateCollection;
import com.liferay.layout.page.template.service.LayoutPageTemplateCollectionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

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

	@Test
	public void testReindex() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		LayoutPageTemplateCollection layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				addLayoutPageTemplateCollection(
					RandomTestUtil.randomString(), serviceContext.getUserId(),
					TestPropsValues.getGroupId(), 0,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(),
					LayoutPageTemplateCollectionTypeConstants.BASIC,
					serviceContext);

		String name = layoutPageTemplateCollection.getName();

		_assertFieldValue(Field.NAME, name, name);

		layoutPageTemplateCollection.setName(RandomTestUtil.randomString());

		layoutPageTemplateCollection =
			_layoutPageTemplateCollectionLocalService.
				updateLayoutPageTemplateCollection(
					layoutPageTemplateCollection.
						getLayoutPageTemplateCollectionId(),
					layoutPageTemplateCollection.getName());

		name = layoutPageTemplateCollection.getName();

		_assertFieldValue(Field.NAME, name, name);

		_deleteDocument(
			layoutPageTemplateCollection.getCompanyId(),
			uidFactory.getUID(layoutPageTemplateCollection));

		_assertNoFieldValues(name);

		_reindex(layoutPageTemplateCollection);

		_assertFieldValue(Field.NAME, name, name);

		_reindex();

		_assertFieldValue(Field.NAME, name, name);

		_layoutPageTemplateCollectionLocalService.
			deleteLayoutPageTemplateCollection(layoutPageTemplateCollection);

		_assertNoFieldValues(name);
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
			String fieldName, String fieldValue, String queryString)
		throws Exception {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, _search(queryString));
	}

	private void _assertNoFieldValues(String queryString) throws Exception {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), _search(queryString));
	}

	private void _deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	private void _reindex() throws Exception {
		indexer.reindex(
			new String[] {String.valueOf(TestPropsValues.getCompanyId())});
	}

	private void _reindex(
			LayoutPageTemplateCollection layoutPageTemplateCollection)
		throws Exception {

		indexer.reindex(layoutPageTemplateCollection);
	}

	private SearchResponse _search(String queryString) throws Exception {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).groupIds(
				TestPropsValues.getGroupId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				LayoutPageTemplateCollection.class
			).queryString(
				queryString
			).build());
	}

	@Inject
	private LayoutPageTemplateCollectionLocalService
		_layoutPageTemplateCollectionLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}