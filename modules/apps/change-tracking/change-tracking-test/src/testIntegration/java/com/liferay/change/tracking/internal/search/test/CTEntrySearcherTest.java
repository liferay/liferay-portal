/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTConstants;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.model.CTEntry;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.Sort;
import com.liferay.portal.search.sort.SortOrder;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.io.Serializable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTEntrySearcherTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());
		_group = GroupTestUtil.addGroup();
		_journalFolderClassNameId = _classNameLocalService.getClassNameId(
			JournalFolder.class);
		_journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);
	}

	@Test
	public void testSearchByChangeType() throws Exception {
		JournalFolder deletedJournalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());
		JournalFolder modifiedJournalFolder = _journalFolderFixture.addFolder(
			_group.getGroupId(), RandomTestUtil.randomString());
		JournalFolder newJournalFolder;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			_journalFolderLocalService.deleteFolder(deletedJournalFolder);

			modifiedJournalFolder.setDescription(RandomTestUtil.randomString());

			modifiedJournalFolder =
				_journalFolderLocalService.updateJournalFolder(
					modifiedJournalFolder);

			newJournalFolder = _journalFolderFixture.addFolder(
				_group.getGroupId(), RandomTestUtil.randomString());
		}

		_assertHits(
			_getUIDs(_getCTEntries(deletedJournalFolder)),
			_byAttribute(
				"changeType", new int[] {CTConstants.CT_CHANGE_TYPE_DELETION}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(modifiedJournalFolder)),
			_byAttribute(
				"changeType",
				new int[] {CTConstants.CT_CHANGE_TYPE_MODIFICATION}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(newJournalFolder)),
			_byAttribute(
				"changeType", new int[] {CTConstants.CT_CHANGE_TYPE_ADDITION}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSearchByGroupId() throws Exception {
		Group group1 = GroupTestUtil.addGroup();
		Group group2 = GroupTestUtil.addGroup();

		JournalFolder journalFolder1 = null;
		JournalFolder journalFolder2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder1 = _journalFolderFixture.addFolder(
				group1.getGroupId(), RandomTestUtil.randomString());
			journalFolder2 = _journalFolderFixture.addFolder(
				group2.getGroupId(), RandomTestUtil.randomString());
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1)),
			_byAttribute(Field.GROUP_ID, new long[] {group1.getGroupId()}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2)),
			_byAttribute(Field.GROUP_ID, new long[] {group2.getGroupId()}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSearchByKeywords() throws Exception {
		JournalFolder journalFolder = null;
		String keywords = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder = _journalFolderFixture.addFolder(
				_group.getGroupId(), keywords + RandomTestUtil.randomString());

			_journalFolderFixture.addFolder(
				_group.getGroupId(), RandomTestUtil.randomString());
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder)),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}),
			_byKeywords(keywords));
	}

	@Test
	public void testSearchByStatus() throws Exception {
		JournalFolder journalFolder1 = null;
		JournalFolder journalFolder2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder1 = _journalFolderFixture.addFolder(
				_group.getGroupId(), "Test Folder A");

			journalFolder1 = _journalFolderLocalService.updateStatus(
				TestPropsValues.getUserId(), journalFolder1,
				WorkflowConstants.STATUS_APPROVED);

			journalFolder2 = _journalFolderFixture.addFolder(
				_group.getGroupId(), "Test Folder B");

			journalFolder2 = _journalFolderLocalService.updateStatus(
				TestPropsValues.getUserId(), journalFolder2,
				WorkflowConstants.STATUS_DRAFT);
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1)),
			_byAttribute(
				Field.STATUS, new int[] {WorkflowConstants.STATUS_APPROVED}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2)),
			_byAttribute(
				Field.STATUS, new int[] {WorkflowConstants.STATUS_DRAFT}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSearchByUserId() throws Exception {
		User user = UserTestUtil.addUser();

		JournalFolder journalFolder = null;

		String originalName = PrincipalThreadLocal.getName();

		try {
			PrincipalThreadLocal.setName(user.getUserId());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection.getCtCollectionId())) {

				journalFolder = _journalFolderFixture.addFolder(
					_group.getGroupId(), RandomTestUtil.randomString());
			}
		}
		finally {
			PrincipalThreadLocal.setName(originalName);
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder)),
			_byAttribute(Field.USER_ID, new long[] {user.getUserId()}),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSortByGroup() throws Exception {
		Group group1 = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "Test Group A");
		Group group2 = GroupTestUtil.addGroup(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID, "Test Group B");

		JournalFolder journalFolder1 = null;
		JournalFolder journalFolder2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder1 = _journalFolderFixture.addFolder(
				group1.getGroupId(), RandomTestUtil.randomString());
			journalFolder2 = _journalFolderFixture.addFolder(
				group2.getGroupId(), RandomTestUtil.randomString());
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1, journalFolder2)),
			_getSort(Field.GROUP_ID, SortOrder.ASC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2, journalFolder1)),
			_getSort(Field.GROUP_ID, SortOrder.DESC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1, journalFolder2)),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, "groupName")),
				SortOrder.ASC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2, journalFolder1)),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, "groupName")),
				SortOrder.DESC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSortByStatus() throws Exception {
		JournalFolder journalFolder1 = null;
		JournalFolder journalFolder2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder1 = _journalFolderFixture.addFolder(
				_group.getGroupId(), "Test Folder A");

			journalFolder1 = _journalFolderLocalService.updateStatus(
				TestPropsValues.getUserId(), journalFolder1,
				WorkflowConstants.STATUS_APPROVED);

			journalFolder2 = _journalFolderFixture.addFolder(
				_group.getGroupId(), "Test Folder B");

			journalFolder2 = _journalFolderLocalService.updateStatus(
				TestPropsValues.getUserId(), journalFolder2,
				WorkflowConstants.STATUS_DRAFT);
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1, journalFolder2)),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, "statusLabel")),
				SortOrder.ASC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2, journalFolder1)),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, "statusLabel")),
				SortOrder.DESC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSortByTitle() throws Exception {
		JournalFolder journalFolder1 = null;
		JournalFolder journalFolder2 = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalFolder1 = _journalFolderFixture.addFolder(
				_group.getGroupId(), "Test Folder A");
			journalFolder2 = _journalFolderFixture.addFolder(
				_group.getGroupId(), "Test Folder B");
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1, journalFolder2)),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, Field.TITLE)),
				SortOrder.ASC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2, journalFolder1)),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, Field.TITLE)),
				SortOrder.DESC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	@Test
	public void testSortByTypeName() throws Exception {
		JournalArticle journalArticle = null;
		JournalFolder journalFolder = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					_ctCollection.getCtCollectionId())) {

			journalArticle = JournalTestUtil.addArticle(
				_group.getGroupId(),
				JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

			journalFolder = _journalFolderFixture.addFolder(
				_group.getGroupId(), RandomTestUtil.randomString());
		}

		long journalArticleClassNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class);

		CTEntry journalArticleCTEntry = _ctEntryLocalService.fetchCTEntry(
			_ctCollection.getCtCollectionId(), journalArticleClassNameId,
			journalArticle.getId());

		CTEntry journalFolderCTEntry = _getCTEntries(journalFolder)[0];

		_assertHits(
			_getUIDs(journalArticleCTEntry, journalFolderCTEntry),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, "typeName")),
				SortOrder.ASC),
			_byAttribute(
				"modelClassNameId",
				new long[] {
					journalArticleClassNameId, _journalFolderClassNameId
				}));
		_assertHits(
			_getUIDs(journalFolderCTEntry, journalArticleCTEntry),
			_getSort(
				Field.getSortableFieldName(
					Field.getLocalizedName(LocaleUtil.US, "typeName")),
				SortOrder.DESC),
			_byAttribute(
				"modelClassNameId",
				new long[] {
					journalArticleClassNameId, _journalFolderClassNameId
				}));
	}

	@Test
	public void testSortByUserName() throws Exception {
		JournalFolder journalFolder1 = null;
		JournalFolder journalFolder2 = null;
		String originalName = PrincipalThreadLocal.getName();
		User user1 = UserTestUtil.addUser(
			"AA", LocaleUtil.getDefault(), "AA", "AA", null);
		User user2 = UserTestUtil.addUser(
			"BB", LocaleUtil.getDefault(), "BB", "BB", null);

		try {
			PrincipalThreadLocal.setName(user1.getUserId());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection.getCtCollectionId())) {

				journalFolder1 = _journalFolderFixture.addFolder(
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), user1.getUserId()));
			}

			PrincipalThreadLocal.setName(user2.getUserId());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection.getCtCollectionId())) {

				journalFolder2 = _journalFolderFixture.addFolder(
					JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID,
					RandomTestUtil.randomString(),
					ServiceContextTestUtil.getServiceContext(
						_group.getGroupId(), user2.getUserId()));
			}
		}
		finally {
			PrincipalThreadLocal.setName(originalName);
		}

		_assertHits(
			_getUIDs(_getCTEntries(journalFolder1, journalFolder2)),
			_getSort(Field.USER_NAME, SortOrder.ASC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
		_assertHits(
			_getUIDs(_getCTEntries(journalFolder2, journalFolder1)),
			_getSort(Field.USER_NAME, SortOrder.DESC),
			_byAttribute(
				"modelClassNameId", new long[] {_journalFolderClassNameId}));
	}

	private void _assertHits(
			List<String> expectedValues,
			Consumer<SearchRequestBuilder>... consumers)
		throws Exception {

		_assertHits(expectedValues, null, consumers);
	}

	private void _assertHits(
			List<String> expectedValues, Sort sort,
			Consumer<SearchRequestBuilder>... consumers)
		throws Exception {

		Destination destination = MessageBusUtil.getDestination(
			CTDestinationNames.CT_ENTRY_REINDEX);

		DestinationStatistics destinationStatistics =
			destination.getDestinationStatistics();

		int i = 0;

		while ((destinationStatistics.getActiveThreadCount() > 0) ||
			   (destinationStatistics.getPendingMessageCount() > 0)) {

			if (i++ > 60) {
				break;
			}

			Thread.sleep(500);

			destinationStatistics = destination.getDestinationStatistics();
		}

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				CTEntry.class
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					"ctCollectionId", _ctCollection.getCtCollectionId())
			).withSearchRequestBuilder(
				consumers
			);

		if (sort != null) {
			searchRequestBuilder = searchRequestBuilder.addSort(sort);
		}

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		DocumentsAssert.assertValues(
			searchResponse.getResponseString(), searchResponse.getDocuments(),
			Field.UID,
			StringBundler.concat(
				"[", StringUtil.merge(expectedValues, ", "), "]"));
	}

	private Consumer<SearchRequestBuilder> _byAttribute(
		String field, Serializable value) {

		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setAttribute(field, value));
	}

	private Consumer<SearchRequestBuilder> _byKeywords(String keywords) {
		return searchRequestBuilder -> searchRequestBuilder.withSearchContext(
			searchContext -> searchContext.setKeywords(keywords));
	}

	private CTEntry[] _getCTEntries(JournalFolder... journalFolders) {
		return TransformUtil.transform(
			journalFolders,
			journalFolder -> _ctEntryLocalService.fetchCTEntry(
				_ctCollection.getCtCollectionId(), _journalFolderClassNameId,
				journalFolder.getFolderId()),
			CTEntry.class);
	}

	private Sort _getSort(String field, SortOrder sortOrder) {
		return _sorts.field(field, sortOrder);
	}

	private List<String> _getUIDs(BaseModel<?>... baseModels) {
		String[] uids = new String[baseModels.length];

		for (int i = 0; i < baseModels.length; i++) {
			uids[i] = _uidFactory.getUID(baseModels[i]);
		}

		return Arrays.asList(uids);
	}

	@Inject
	private static ClassNameLocalService _classNameLocalService;

	@Inject
	private CounterLocalService _counterLocalService;

	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	private Group _group;
	private long _journalFolderClassNameId;
	private JournalFolderFixture _journalFolderFixture;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Sorts _sorts;

	@Inject
	private UIDFactory _uidFactory;

}