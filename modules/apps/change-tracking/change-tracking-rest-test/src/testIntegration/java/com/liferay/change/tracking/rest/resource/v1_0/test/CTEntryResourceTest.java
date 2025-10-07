/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTDestinationNames;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.rest.client.dto.v1_0.CTEntry;
import com.liferay.change.tracking.rest.client.pagination.Page;
import com.liferay.change.tracking.rest.client.pagination.Pagination;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.change.tracking.service.CTEntryLocalService;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProvider;
import com.liferay.change.tracking.spi.history.CTCollectionHistoryProviderRegistry;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderLocalService;
import com.liferay.journal.test.util.JournalFolderFixture;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationStatistics;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.AddressLocalService;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.test.rule.Inject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTEntryResourceTest extends BaseCTEntryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_journalArticleClassNameId = _classNameLocalService.getClassNameId(
			JournalArticle.class);
	}

	@Override
	@Test
	public void testGetCTEntriesHistoryPage() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalFolderFixture journalFolderFixture = new JournalFolderFixture(
			_journalFolderLocalService);

		JournalFolder journalFolder = journalFolderFixture.addFolder(
			testGroup.getGroupId(), RandomTestUtil.randomString());

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			testGroup.getGroupId(), journalFolder.getFolderId());

		_waitForReindex();

		Page<CTEntry> page = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());

		long ctCollectionId1 = _getCTCollectionId();

		CTEntry ctEntry = _addJournalArticleCTEntry(
			ctCollectionId1, journalArticle1);

		List<CTEntry> ctEntries = Arrays.asList(
			ctEntry,
			_addJournalArticleCTEntry(ctCollectionId1, journalArticle2),
			_addJournalArticleCTEntry(_getCTCollectionId(), journalArticle2));

		_waitForReindex();

		page = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, journalArticle1.getId(), null,
			testGroup.getGroupId(), null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		assertContains(ctEntry, (List<CTEntry>)page.getItems());

		page = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, Pagination.of(1, 10), null);

		Assert.assertEquals(3, page.getTotalCount());

		assertEqualsIgnoringOrder(ctEntries, (List<CTEntry>)page.getItems());
	}

	@Override
	@Test
	public void testGetCTEntriesHistoryPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		CTEntry ctEntry = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle);

		for (EntityField entityField : entityFields) {
			Page<CTEntry> page = ctEntryResource.getCTEntriesHistoryPage(
				_journalArticleClassNameId, null, null, testGroup.getGroupId(),
				getFilterString(entityField, "between", ctEntry),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(ctEntry),
				(List<CTEntry>)page.getItems());
		}
	}

	@Override
	@Test
	public void testGetCTEntriesHistoryPageWithPagination() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		Page<CTEntry> ctEntryPage = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, null, null);

		int totalCount = GetterUtil.getInteger(ctEntryPage.getTotalCount());

		CTEntry ctEntry1 = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle);

		CTEntry ctEntry2 = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle);

		CTEntry ctEntry3 = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle);

		Page<CTEntry> page1 = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, Pagination.of(1, totalCount + 2), null);

		List<CTEntry> ctEntries1 = (List<CTEntry>)page1.getItems();

		Assert.assertEquals(
			ctEntries1.toString(), totalCount + 2, ctEntries1.size());

		Page<CTEntry> page2 = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, Pagination.of(2, totalCount + 2), null);

		Assert.assertEquals(totalCount + 3, page2.getTotalCount());

		List<CTEntry> ctEntries2 = (List<CTEntry>)page2.getItems();

		Assert.assertEquals(ctEntries2.toString(), 1, ctEntries2.size());

		Page<CTEntry> page3 = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, Pagination.of(1, (int)totalCount + 3), null);

		assertContains(ctEntry1, (List<CTEntry>)page3.getItems());
		assertContains(ctEntry2, (List<CTEntry>)page3.getItems());
		assertContains(ctEntry3, (List<CTEntry>)page3.getItems());
	}

	@Override
	protected void assertValid(CTEntry ctEntry) throws Exception {
		boolean valid = true;

		if ((ctEntry.getDateCreated() == null) ||
			(ctEntry.getDateModified() == null) || (ctEntry.getId() == null)) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(additionalAssertFieldName, "actions")) {
				if (ctEntry.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "changeType")) {
				if (ctEntry.getChangeType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "ctCollectionId")) {
				if (ctEntry.getCtCollectionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "hideable")) {
				if (ctEntry.getHideable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "modelClassNameId")) {
				if (ctEntry.getModelClassNameId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "modelClassPK")) {
				if (ctEntry.getModelClassPK() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "ownerId")) {
				if (ctEntry.getOwnerId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "ownerName")) {
				if (ctEntry.getOwnerName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "siteId")) {
				if (ctEntry.getSiteId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "siteName")) {
				if (ctEntry.getSiteName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "status")) {
				if (ctEntry.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "title")) {
				if (ctEntry.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(additionalAssertFieldName, "typeName")) {
				if (ctEntry.getTypeName() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"ctCollectionId", "modelClassNameId", "modelClassPK"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"changeType", "ctCollectionName", "ctCollectionStatus",
			"ctCollectionStatusDate", "ctCollectionStatusUserName", "ownerName",
			"siteId", "siteName", "status", "typeName"
		};
	}

	@Override
	protected CTEntry testGetCtCollectionCTEntriesPage_addCTEntry(
			Long ctCollectionId, CTEntry ctEntry)
		throws Exception {

		return _addCTEntry(ctCollectionId, ctEntry.getTitle());
	}

	@Override
	protected Long testGetCtCollectionCTEntriesPage_getCtCollectionId()
		throws Exception {

		return _getCTCollectionId();
	}

	@Override
	protected Long
			testGetCtCollectionCTEntriesPage_getIrrelevantCtCollectionId()
		throws Exception {

		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, testCompany.getCompanyId(), testCompany.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		return ctCollection.getCtCollectionId();
	}

	@Override
	protected CTEntry
			testGetCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK_addCTEntry()
		throws Exception {

		return _addCTEntry(_getCTCollectionId(), RandomTestUtil.randomString());
	}

	@Override
	protected Long
			testGetCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK_getCtCollectionId(
				CTEntry ctEntry)
		throws Exception {

		return ctEntry.getCtCollectionId();
	}

	@Override
	protected void testGetCTEntriesHistoryPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		if (type == EntityField.Type.STRING) {
			return;
		}

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		CTEntry ctEntry = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle);

		_addJournalArticleCTEntry(_getCTCollectionId(), journalArticle);

		for (EntityField entityField : entityFields) {
			Page<CTEntry> page = ctEntryResource.getCTEntriesHistoryPage(
				_journalArticleClassNameId, null, null, null,
				getFilterString(entityField, operator, ctEntry),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(ctEntry),
				(List<CTEntry>)page.getItems());
		}
	}

	@Override
	protected void testGetCTEntriesHistoryPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, CTEntry, CTEntry, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		CTEntry ctEntry1 = randomCTEntry();
		CTEntry ctEntry2 = randomCTEntry();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, ctEntry1, ctEntry2);
		}

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		ctEntry1 = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle, ctEntry1.getTitle());

		ctEntry2 = _addJournalArticleCTEntry(
			_getCTCollectionId(), journalArticle, ctEntry2.getTitle());

		Page<CTEntry> page = ctEntryResource.getCTEntriesHistoryPage(
			_journalArticleClassNameId, null, null, testGroup.getGroupId(),
			null, null, null);

		for (EntityField entityField : entityFields) {
			Page<CTEntry> ascPage = ctEntryResource.getCTEntriesHistoryPage(
				_journalArticleClassNameId, null, null, testGroup.getGroupId(),
				null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(ctEntry1, (List<CTEntry>)ascPage.getItems());
			assertContains(ctEntry2, (List<CTEntry>)ascPage.getItems());

			Page<CTEntry> descPage = ctEntryResource.getCTEntriesHistoryPage(
				_journalArticleClassNameId, null, null, testGroup.getGroupId(),
				null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(ctEntry2, (List<CTEntry>)descPage.getItems());
			assertContains(ctEntry1, (List<CTEntry>)descPage.getItems());
		}
	}

	@Override
	protected CTEntry testGetCTEntry_addCTEntry() throws Exception {
		return _addCTEntry(_getCTCollectionId(), RandomTestUtil.randomString());
	}

	@Override
	protected CTEntry testGraphQLCTEntry_addCTEntry() throws Exception {
		return _addCTEntry(_getCTCollectionId(), RandomTestUtil.randomString());
	}

	@Override
	protected Long
			testGraphQLGetCtCollectionCTEntryByModelClassNameByModelClassPkModelClassPK_getCtCollectionId(
				CTEntry ctEntry)
		throws Exception {

		return ctEntry.getCtCollectionId();
	}

	private CTEntry _addCTEntry(long ctCollectionId, String name)
		throws Exception {

		Address address = null;

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					Long.valueOf(ctCollectionId))) {

			User user = TestPropsValues.getUser();

			address = _addressLocalService.addAddress(
				null, user.getUserId(), Contact.class.getName(),
				user.getContactId(), 0,
				_listTypeLocalService.getListTypeId(
					testCompany.getCompanyId(), "personal",
					ListTypeConstants.CONTACT_ADDRESS),
				0, RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				false, name, false, RandomTestUtil.randomString(), null, null,
				null, null, null, ServiceContextTestUtil.getServiceContext());
		}

		com.liferay.change.tracking.model.CTEntry serviceBuilderCTEntry =
			_ctEntryLocalService.fetchCTEntry(
				ctCollectionId,
				_classNameLocalService.getClassNameId(Address.class),
				address.getAddressId());

		_waitForReindex();

		return ctEntryResource.getCTEntry(serviceBuilderCTEntry.getCtEntryId());
	}

	private CTEntry _addJournalArticleCTEntry(
			long ctCollectionId, JournalArticle journalArticle)
		throws Exception {

		return _addJournalArticleCTEntry(
			ctCollectionId, journalArticle, RandomTestUtil.randomString());
	}

	private CTEntry _addJournalArticleCTEntry(
			long ctCollectionId, JournalArticle journalArticle, String title)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
					ctCollectionId)) {

			JournalTestUtil.updateArticle(journalArticle, title);
		}

		CTCollectionHistoryProvider<?> ctCollectionHistoryProvider =
			_ctCollectionHistoryProviderRegistry.getCTCollectionHistoryProvider(
				_journalArticleClassNameId);

		com.liferay.change.tracking.model.CTEntry serviceBuilderCTEntry =
			ctCollectionHistoryProvider.getCTEntry(
				ctCollectionId, _journalArticleClassNameId,
				journalArticle.getId());

		_waitForReindex();

		return ctEntryResource.getCTEntry(serviceBuilderCTEntry.getCtEntryId());
	}

	private long _getCTCollectionId() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, testCompany.getCompanyId(), testCompany.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		return ctCollection.getCtCollectionId();
	}

	private void _waitForReindex() throws Exception {
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
	}

	private static long _journalArticleClassNameId;

	@Inject
	private AddressLocalService _addressLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private CTCollectionHistoryProviderRegistry
		_ctCollectionHistoryProviderRegistry;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private CTEntryLocalService _ctEntryLocalService;

	@Inject
	private JournalFolderLocalService _journalFolderLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

}