/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.service.LayoutPageTemplateEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Juan Pablo Montero
 */
@RunWith(Arquillian.class)
public class LayoutPageTemplateEntryIndexerReindexTest {

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

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.addLayoutPageTemplateEntry(
				RandomTestUtil.randomString(), serviceContext.getUserId(),
				TestPropsValues.getGroupId(), 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomString(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT, 0,
				WorkflowConstants.STATUS_DRAFT, serviceContext);

		String name = layoutPageTemplateEntry.getName();

		_assertFieldValue(
			Field.NAME, name, name,
			Collections.singletonMap(
				"types",
				new String[] {
					String.valueOf(
						LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT)
				}));
		_assertNoFieldValues(
			name,
			Collections.singletonMap(
				"types",
				new String[] {
					String.valueOf(
						LayoutPageTemplateEntryTypeConstants.DISPLAY_PAGE)
				}));

		layoutPageTemplateEntry.setName(RandomTestUtil.randomString());

		layoutPageTemplateEntry =
			_layoutPageTemplateEntryLocalService.updateLayoutPageTemplateEntry(
				layoutPageTemplateEntry.getUserId(),
				layoutPageTemplateEntry.getLayoutPageTemplateEntryId(), name,
				WorkflowConstants.STATUS_APPROVED);

		name = layoutPageTemplateEntry.getName();

		_assertFieldValue(Field.NAME, name, name, null);

		_deleteDocument(
			layoutPageTemplateEntry.getCompanyId(),
			uidFactory.getUID(layoutPageTemplateEntry));

		_assertNoFieldValues(name, null);

		_reindex(layoutPageTemplateEntry);

		_assertFieldValue(Field.NAME, name, name, null);

		_reindex();

		_assertFieldValue(Field.NAME, name, name, null);

		_layoutPageTemplateEntryLocalService.deleteLayoutPageTemplateEntry(
			layoutPageTemplateEntry);

		_assertNoFieldValues(name, null);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject(
		filter = "indexer.class.name=com.liferay.layout.page.template.model.LayoutPageTemplateEntry"
	)
	protected Indexer<LayoutPageTemplateEntry> indexer;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject
	protected UIDFactory uidFactory;

	private void _assertFieldValue(
			String fieldName, String fieldValue, String queryString,
			Map<String, Serializable> parameters)
		throws Exception {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, _search(queryString, parameters));
	}

	private void _assertNoFieldValues(
			String queryString, Map<String, Serializable> parameters)
		throws Exception {

		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), _search(queryString, parameters));
	}

	private void _deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	private void _populateSearchContext(
		String searchTerm, Map<String, Serializable> parameters,
		SearchContext searchContext) {

		if (Validator.isNotNull(searchTerm)) {
			searchContext.setKeywords(searchTerm);
		}

		if (parameters != null) {
			String[] types = (String[])parameters.get("types");

			if (ArrayUtil.isNotEmpty(types)) {
				searchContext.setAttribute("types", types);
			}
		}
	}

	private void _reindex() throws Exception {
		indexer.reindex(
			new String[] {String.valueOf(TestPropsValues.getCompanyId())});
	}

	private void _reindex(LayoutPageTemplateEntry layoutPageTemplateEntry)
		throws Exception {

		indexer.reindex(layoutPageTemplateEntry);
	}

	private SearchResponse _search(
			String searchTerm, Map<String, Serializable> parameters)
		throws Exception {

		return _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).groupIds(
				TestPropsValues.getGroupId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				LayoutPageTemplateEntry.class
			).withSearchContext(
				searchContext -> _populateSearchContext(
					searchTerm, parameters, searchContext)
			).build());
	}

	@Inject
	private LayoutPageTemplateEntryLocalService
		_layoutPageTemplateEntryLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}