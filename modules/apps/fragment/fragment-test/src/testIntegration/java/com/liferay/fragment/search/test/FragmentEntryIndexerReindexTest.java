/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentCollection;
import com.liferay.fragment.model.FragmentEntry;
import com.liferay.fragment.service.FragmentCollectionLocalService;
import com.liferay.fragment.service.FragmentEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Collections;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Rubén Pulido
 */
@RunWith(Arquillian.class)
public class FragmentEntryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		_fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, serviceContext.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				serviceContext);
	}

	@Test
	public void testReindex() throws Exception {
		FragmentEntry fragmentEntry = _addFragmentEntry(
			WorkflowConstants.STATUS_DRAFT);

		_assertFieldValues(
			fragmentEntry.isHead(), true, fragmentEntry.isMarketplace(),
			fragmentEntry.getName());

		fragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			fragmentEntry.getFragmentEntryId(), RandomTestUtil.randomString());

		_assertFieldValues(
			fragmentEntry.isHead(), true, fragmentEntry.isMarketplace(),
			fragmentEntry.getName());

		_deleteDocument(
			fragmentEntry.getCompanyId(), uidFactory.getUID(fragmentEntry));

		_assertNoFieldValues(fragmentEntry.getName());

		_reindex(fragmentEntry);

		_assertFieldValues(
			fragmentEntry.isHead(), true, fragmentEntry.isMarketplace(),
			fragmentEntry.getName());

		_reindex();

		_assertFieldValues(
			fragmentEntry.isHead(), true, fragmentEntry.isMarketplace(),
			fragmentEntry.getName());

		_fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);

		_assertNoFieldValues(fragmentEntry.getName());

		fragmentEntry = _addFragmentEntry(WorkflowConstants.STATUS_APPROVED);

		_assertFieldValues(
			fragmentEntry.isHead(), true, fragmentEntry.isMarketplace(),
			fragmentEntry.getName());

		FragmentEntry draftFragmentEntry = _fragmentEntryLocalService.getDraft(
			fragmentEntry.getFragmentEntryId());

		draftFragmentEntry = _fragmentEntryLocalService.updateFragmentEntry(
			draftFragmentEntry.getFragmentEntryId(),
			RandomTestUtil.randomString());

		_assertFieldValues(
			fragmentEntry.isHead(), true, fragmentEntry.isMarketplace(),
			fragmentEntry.getName());
		_assertFieldValues(
			draftFragmentEntry.isHead(), false,
			draftFragmentEntry.isMarketplace(), draftFragmentEntry.getName());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		FragmentCollection fragmentCollection =
			_fragmentCollectionLocalService.addFragmentCollection(
				null, serviceContext.getUserId(), TestPropsValues.getGroupId(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				serviceContext);

		fragmentEntry = _fragmentEntryLocalService.moveFragmentEntry(
			fragmentEntry.getFragmentEntryId(),
			fragmentCollection.getFragmentCollectionId());

		_assertFieldValue(
			"fragmentCollectionId",
			String.valueOf(fragmentCollection.getFragmentCollectionId()),
			fragmentEntry.getName());

		fragmentEntry = _addFragmentEntry(WorkflowConstants.STATUS_APPROVED);

		FragmentEntry copyFragmentEntry =
			_fragmentEntryLocalService.copyFragmentEntry(
				serviceContext.getUserId(), TestPropsValues.getGroupId(),
				fragmentEntry.getFragmentEntryId(),
				_fragmentCollection.getFragmentCollectionId(), serviceContext);

		_fragmentEntryLocalService.deleteFragmentEntry(fragmentEntry);

		_assertFieldValues(
			copyFragmentEntry.isHead(), true, copyFragmentEntry.isMarketplace(),
			copyFragmentEntry.getName());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(
		filter = "indexer.class.name=com.liferay.fragment.model.FragmentEntry"
	)
	protected Indexer<FragmentEntry> indexer;

	@Inject
	protected UIDFactory uidFactory;

	private FragmentEntry _addFragmentEntry(int status) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId());

		return _fragmentEntryLocalService.addFragmentEntry(
			null, serviceContext.getUserId(), TestPropsValues.getGroupId(),
			_fragmentCollection.getFragmentCollectionId(), null,
			RandomTestUtil.randomString(), StringPool.BLANK, "<div></div>",
			StringPool.BLANK, false, StringPool.BLANK, StringPool.BLANK, 0,
			false, false, FragmentConstants.TYPE_COMPONENT, null, status,
			serviceContext);
	}

	private void _assertFieldValue(
			String fieldName, String fieldValue, String queryString)
		throws Exception {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, _search(queryString));
	}

	private void _assertFieldValues(
			boolean head, boolean headListable, boolean marketplace,
			String name)
		throws Exception {

		_assertFieldValue(Field.NAME, name, name);
		_assertFieldValue(
			"fragmentCollectionId",
			String.valueOf(_fragmentCollection.getFragmentCollectionId()),
			name);
		_assertFieldValue("head", String.valueOf(head), name);
		_assertFieldValue("headListable", String.valueOf(headListable), name);
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

	private void _reindex(FragmentEntry fragmentEntry) throws Exception {
		indexer.reindex(fragmentEntry);
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
				FragmentEntry.class
			).queryString(
				queryString
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					"head", Boolean.FALSE)
			).build());
	}

	private FragmentCollection _fragmentCollection;

	@Inject
	private FragmentCollectionLocalService _fragmentCollectionLocalService;

	@Inject
	private FragmentEntryLocalService _fragmentEntryLocalService;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

}