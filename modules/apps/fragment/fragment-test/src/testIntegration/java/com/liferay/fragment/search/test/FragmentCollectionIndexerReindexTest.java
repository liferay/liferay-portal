/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.service.FragmentCollectionLocalService;
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
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentCollectionIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testReindex() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				RandomTestUtil.randomString(), serviceContext.getUserId(),
				TestPropsValues.getGroupId(), RandomTestUtil.randomString(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, serviceContext);

		_assertFieldValues(
			fragmentCollection.getName(), fragmentCollection.getDescription(),
			fragmentCollection.isMarketplace());

		fragmentCollection =
			_fragmentCollectionLocalService.updateFragmentCollection(
				fragmentCollection.getFragmentCollectionId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString());

		_assertFieldValues(
			fragmentCollection.getName(), fragmentCollection.getDescription(),
			fragmentCollection.isMarketplace());

		_deleteDocument(
			fragmentCollection.getCompanyId(),
			uidFactory.getUID(fragmentCollection));

		_assertNoFieldValues(fragmentCollection.getName());

		_reindex(fragmentCollection);

		_assertFieldValues(
			fragmentCollection.getName(), fragmentCollection.getDescription(),
			fragmentCollection.isMarketplace());

		_reindex();

		_assertFieldValues(
			fragmentCollection.getName(), fragmentCollection.getDescription(),
			fragmentCollection.isMarketplace());

		_fragmentCollectionLocalService.deleteFragmentCollection(
			fragmentCollection);

		_assertNoFieldValues(fragmentCollection.getName());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(
		filter = "indexer.class.name=com.liferay.fragment.model.FragmentCollection"
	)
	protected Indexer<FragmentCollection> indexer;

	@Inject
	protected UIDFactory uidFactory;

	private void _assertFieldValue(
			String fieldName, String fieldValue, String queryString)
		throws Exception {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, _search(queryString));
	}

	private void _assertFieldValues(
			String name, String description, boolean marketplace)
		throws Exception {

		_assertFieldValue(Field.NAME, name, name);
		_assertFieldValue(Field.DESCRIPTION, description, name);
		_assertFieldValue("marketplace", String.valueOf(marketplace), name);
	}

	private void _assertNoFieldValues(String queryString) throws Exception {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), _search(queryString));
	}

	private void _deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	private void _reindex() throws Exception {
		indexer.reindexCompany(TestPropsValues.getCompanyId());
	}

	private void _reindex(FragmentCollection fragmentCollection)
		throws Exception {

		indexer.reindex(fragmentCollection);
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
				FragmentCollection.class
			).queryString(
				queryString
			).build());
	}

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}