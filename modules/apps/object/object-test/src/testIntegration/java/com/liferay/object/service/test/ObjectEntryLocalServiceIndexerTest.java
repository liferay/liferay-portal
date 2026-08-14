/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.object.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.test.util.TreeTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.filter.ComplexQueryPartBuilderFactory;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.text.DateFormat;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mario Gomes
 */
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceIndexerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testAddObjectEntryWithHierarchy() throws Exception {
		ObjectField objectField = new TextObjectFieldBuilder(
		).labelMap(
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
		).name(
			"a" + RandomTestUtil.randomString()
		).build();

		ObjectDefinition objectDefinitionA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				Collections.singletonList(objectField));
		ObjectDefinition objectDefinitionAA =
			ObjectDefinitionTestUtil.publishObjectDefinition(
				true, false, false, Collections.singletonList(objectField),
				ObjectDefinitionConstants.SCOPE_COMPANY);

		TreeTestUtil.bind(
			_objectRelationshipLocalService,
			Collections.singletonList(
				ObjectRelationshipTestUtil.addObjectRelationship(
					_objectRelationshipLocalService, objectDefinitionA,
					objectDefinitionAA,
					ObjectRelationshipConstants.DELETION_TYPE_CASCADE,
					"objectRelationship")));

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		ObjectEntry objectEntryA = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinitionA.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).build(),
			serviceContext);

		Assert.assertNotNull(
			_searchObjectEntry(
				objectDefinitionA.getClassName(),
				objectEntryA.getObjectEntryId(),
				WorkflowConstants.STATUS_APPROVED));

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			objectDefinitionAA.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				objectField.getName(), RandomTestUtil.randomString()
			).put(
				"r_objectRelationship_" +
					objectDefinitionA.getPKObjectFieldName(),
				objectEntryA.getObjectEntryId()
			).build(),
			serviceContext);

		Assert.assertNotNull(
			_searchObjectEntry(
				objectDefinitionA.getClassName(),
				objectEntryA.getObjectEntryId(),
				WorkflowConstants.STATUS_DRAFT));

		TreeTestUtil.deleteObjectDefinitionHierarchy(
			_objectDefinitionLocalService,
			new String[] {
				objectDefinitionA.getName(), objectDefinitionAA.getName()
			},
			_objectEntryLocalService, _objectRelationshipLocalService);
	}

	@Test
	public void testGetOrAddEmptyObjectEntry() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			objectEntry = _objectEntryLocalService.getOrAddEmptyObjectEntry(
				RandomTestUtil.randomString(), TestPropsValues.getGroupId(),
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId());
		}

		Assert.assertNotNull(
			_searchObjectEntry(
				objectEntry.getObjectEntryId(),
				WorkflowConstants.STATUS_EMPTY));
	}

	@Test
	public void testUpdateModifiedDate() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition();

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"able", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Date modifiedDate = new Date(System.currentTimeMillis() - Time.HOUR);

		_objectEntryLocalService.updateModifiedDate(
			objectEntry.getObjectEntryId(), modifiedDate);

		Document document = _searchObjectEntry(
			objectEntry.getObjectEntryId(), WorkflowConstants.STATUS_APPROVED);

		Assert.assertNotNull(document);

		DateFormat dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			PropsValues.INDEX_DATE_FORMAT_PATTERN);

		Assert.assertEquals(
			dateFormat.format(modifiedDate),
			document.getString(Field.MODIFIED_DATE));
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private Document _searchObjectEntry(long objectEntryId, int status)
		throws Exception {

		return _searchObjectEntry(
			_objectDefinition.getClassName(), objectEntryId, status);
	}

	private Document _searchObjectEntry(
			String entryClassName, long objectEntryId, int status)
		throws Exception {

		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).entryClassNames(
				entryClassName
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					Field.STATUS, status)
			).addComplexQueryPart(
				_complexQueryPartBuilderFactory.builder(
				).query(
					QueriesUtil.term(
						Field.ENTRY_CLASS_PK, String.valueOf(objectEntryId))
				).build()
			).build());

		SearchHits searchHits = searchResponse.getSearchHits();

		List<SearchHit> searchHitsList = searchHits.getSearchHits();

		if (searchHitsList.isEmpty()) {
			return null;
		}

		SearchHit searchHit = searchHitsList.get(0);

		return searchHit.getDocument();
	}

	@Inject
	private ComplexQueryPartBuilderFactory _complexQueryPartBuilderFactory;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}