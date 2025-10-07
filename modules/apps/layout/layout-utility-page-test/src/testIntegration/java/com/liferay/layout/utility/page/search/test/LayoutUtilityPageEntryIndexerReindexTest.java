/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.layout.utility.page.model.LayoutUtilityPageEntry;
import com.liferay.layout.utility.page.service.LayoutUtilityPageEntryLocalService;
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
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Collections;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juan Pablo Montero
 */
@RunWith(Arquillian.class)
public class LayoutUtilityPageEntryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testReindex() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		LayoutUtilityPageEntry layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.addLayoutUtilityPageEntry(
				RandomTestUtil.randomString(), serviceContext.getUserId(),
				TestPropsValues.getGroupId(), 0, 0, false,
				RandomTestUtil.randomString(),
				LayoutUtilityPageEntryConstants.TYPE_COOKIE_POLICY, 0L,
				serviceContext);

		String name = layoutUtilityPageEntry.getName();

		_assertFieldValue(Field.NAME, name, name);

		layoutUtilityPageEntry.setName(RandomTestUtil.randomString());

		layoutUtilityPageEntry =
			_layoutUtilityPageEntryLocalService.updateLayoutUtilityPageEntry(
				layoutUtilityPageEntry.getLayoutUtilityPageEntryId(),
				layoutUtilityPageEntry.getName());

		name = layoutUtilityPageEntry.getName();

		_assertFieldValue(Field.NAME, name, name);

		_deleteDocument(
			layoutUtilityPageEntry.getCompanyId(),
			uidFactory.getUID(layoutUtilityPageEntry));

		_assertNoFieldValues(name);

		_reindex(layoutUtilityPageEntry);

		_assertFieldValue(Field.NAME, name, name);

		_reindex();

		_assertFieldValue(Field.NAME, name, name);

		_layoutUtilityPageEntryLocalService.deleteLayoutUtilityPageEntry(
			layoutUtilityPageEntry);

		_assertNoFieldValues(name);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject(
		filter = "indexer.class.name=com.liferay.layout.utility.page.model.LayoutUtilityPageEntry"
	)
	protected Indexer<LayoutUtilityPageEntry> indexer;

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

	private void _reindex(LayoutUtilityPageEntry layoutUtilityPageEntry)
		throws Exception {

		indexer.reindex(layoutUtilityPageEntry);
	}

	private SearchResponse _search(String searchTerm) throws Exception {
		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).groupIds(
				TestPropsValues.getGroupId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				LayoutUtilityPageEntry.class
			).queryString(
				searchTerm
			).build());
	}

	@Inject
	private LayoutUtilityPageEntryLocalService
		_layoutUtilityPageEntryLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}