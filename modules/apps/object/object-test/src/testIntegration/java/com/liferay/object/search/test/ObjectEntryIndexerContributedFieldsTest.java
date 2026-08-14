/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Marcela Cunha
 */
@RunWith(Arquillian.class)
public class ObjectEntryIndexerContributedFieldsTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(
			ObjectEntryIndexerContributedFieldsTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		_serviceRegistration = bundleContext.registerService(
			(Class<ModelDocumentContributor<?>>)
				(Class<?>)ModelDocumentContributor.class,
			(ModelDocumentContributor<ObjectEntry>)
				(document, objectEntry) -> document.addKeyword(
					_FIELD_NAME, _FIELD_VALUE),
			new HashMapDictionary<>(
				Collections.singletonMap(
					"indexer.class.name", _objectDefinition.getClassName())));
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testContributedFields() throws Exception {
		String objectFieldValue = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"able", objectFieldValue
			).build(),
			serviceContext);

		_assertFieldValue(objectFieldValue, WorkflowConstants.STATUS_APPROVED);

		objectEntry = _objectEntryLocalService.moveObjectEntryToTrash(
			TestPropsValues.getUserId(), objectEntry, serviceContext);

		_assertFieldValue(objectFieldValue, WorkflowConstants.STATUS_IN_TRASH);

		objectEntry = _objectEntryLocalService.restoreObjectEntryFromTrash(
			TestPropsValues.getUserId(), objectEntry, serviceContext);

		_assertFieldValue(objectFieldValue, WorkflowConstants.STATUS_APPROVED);

		objectEntry = _objectEntryLocalService.updateObjectEntry(objectEntry);

		_assertFieldValue(objectFieldValue, WorkflowConstants.STATUS_APPROVED);

		_objectEntryLocalService.updateModifiedDate(
			objectEntry.getObjectEntryId(), RandomTestUtil.nextDate());

		_assertFieldValue(objectFieldValue, WorkflowConstants.STATUS_APPROVED);

		Indexer<ObjectEntry> indexer = IndexerRegistryUtil.nullSafeGetIndexer(
			ObjectEntry.class.getName());

		try (SafeCloseable safeCloseable =
				ReindexCacheThreadLocal.openReindexMode(
					true, new ConcurrentHashMap<>())) {

			indexer.reindexCompany(TestPropsValues.getCompanyId());
		}

		_assertFieldValue(objectFieldValue, WorkflowConstants.STATUS_APPROVED);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertFieldValue(String objectFieldValue, int status)
		throws Exception {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).entryClassNames(
				_objectDefinition.getClassName()
			).queryString(
				objectFieldValue
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					Field.STATUS, status)
			).build());

		FieldValuesAssert.assertFieldValue(
			_FIELD_NAME, _FIELD_VALUE, searchResponse);
	}

	private static final String _FIELD_NAME = RandomTestUtil.randomString();

	private static final long _FIELD_VALUE = RandomTestUtil.randomLong();

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	private ServiceRegistration<ModelDocumentContributor<?>>
		_serviceRegistration;

}