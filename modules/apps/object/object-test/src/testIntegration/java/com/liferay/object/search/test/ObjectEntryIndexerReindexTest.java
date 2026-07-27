/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.constants.ObjectFieldSettingConstants;
import com.liferay.object.constants.ObjectFieldValidationConstants;
import com.liferay.object.field.builder.AttachmentObjectFieldBuilder;
import com.liferay.object.field.builder.DecimalObjectFieldBuilder;
import com.liferay.object.field.builder.IntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongIntegerObjectFieldBuilder;
import com.liferay.object.field.builder.LongTextObjectFieldBuilder;
import com.liferay.object.field.builder.PrecisionDecimalObjectFieldBuilder;
import com.liferay.object.field.builder.RichTextObjectFieldBuilder;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.setting.builder.ObjectFieldSettingBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.test.util.ObjectFieldTestUtil;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.ReindexCacheThreadLocal;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Victor Kammerer
 */
@RunWith(Arquillian.class)
public class ObjectEntryIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testReindex() throws Exception {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_OBJECT_ENTRY_MODEL_DOCUMENT_CONTRIBUTOR,
				LoggerTestUtil.WARN)) {

			ObjectDefinition objectDefinition = _addObjectDefinition(
				Arrays.asList(
					new DecimalObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"decimalLocalized"
					).build(),
					new IntegerObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"integerLocalized"
					).build(),
					new LongIntegerObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"longIntegerLocalized"
					).build(),
					new LongTextObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"longTextLocalized"
					).build(),
					new PrecisionDecimalObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"precisionDecimalLocalized"
					).build(),
					new RichTextObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"richTextLocalized"
					).build(),
					new TextObjectFieldBuilder(
					).indexed(
						true
					).indexedAsKeyword(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"textLocalized"
					).objectFieldSettings(
						Collections.singletonList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_UNIQUE_VALUES
							).value(
								Boolean.TRUE.toString()
							).build())
					).build()));

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"decimalLocalized_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomDouble()
					).put(
						"pt_BR", RandomTestUtil.randomDouble()
					).build()
				).put(
					"integerLocalized_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomInt()
					).put(
						"pt_BR", RandomTestUtil.randomInt()
					).build()
				).put(
					"longIntegerLocalized_i18n",
					HashMapBuilder.put(
						"en_US",
						RandomTestUtil.randomLong(
							1,
							ObjectFieldValidationConstants.
								BUSINESS_TYPE_LONG_VALUE_MAX)
					).put(
						"pt_BR",
						RandomTestUtil.randomLong(
							1,
							ObjectFieldValidationConstants.
								BUSINESS_TYPE_LONG_VALUE_MAX)
					).build()
				).put(
					"longTextLocalized_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					"precisionDecimalLocalized_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomDouble()
					).put(
						"pt_BR", RandomTestUtil.randomDouble()
					).build()
				).put(
					"richTextLocalized_i18n",
					HashMapBuilder.put(
						"en_US", RandomTestUtil.randomString()
					).put(
						"pt_BR", RandomTestUtil.randomString()
					).build()
				).put(
					"textLocalized_i18n",
					HashMapBuilder.put(
						"en_US", "en_US " + RandomTestUtil.randomString()
					).put(
						"pt_BR", "pt_BR " + RandomTestUtil.randomString()
					).build()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			String originalName = PrincipalThreadLocal.getName();

			_user = TestPropsValues.getUser();

			try {
				PrincipalThreadLocal.setName(null);

				Indexer<ObjectEntry> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						objectEntry.getModelClassName());

				indexer.reindex(
					objectDefinition.getClassName(),
					objectEntry.getObjectEntryId());

				String searchTerm = String.valueOf(
					objectEntry.getObjectEntryId());

				FieldValuesAssert.assertFieldValue(
					Field.ENTRY_CLASS_NAME, objectEntry.getModelClassName(),
					search(searchTerm));

				List<LogEntry> logEntries = logCapture.getLogEntries();

				if (!logEntries.isEmpty()) {
					LogEntry logEntry = logEntries.get(0);

					Assert.assertNotEquals(
						"Unable to index object entry " +
							objectEntry.getObjectEntryId(),
						logEntry.getMessage());
				}

				Assert.assertEquals(
					logEntries.toString(), 0, logEntries.size());
			}
			finally {
				PrincipalThreadLocal.setName(originalName);

				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	@FeatureFlag("LPD-17564")
	@Test
	public void testReindexIndexesOnlyHeadObjectEntry() throws Exception {
		ObjectDefinition objectDefinition = _addObjectDefinition(
			true, true,
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).indexed(
					true
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()));

		String originalName = PrincipalThreadLocal.getName();

		_user = TestPropsValues.getUser();

		try {
			ServiceContext serviceContext =
				ServiceContextTestUtil.getServiceContext();

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"textObjectFieldName", RandomTestUtil.randomString()
				).build(),
				serviceContext);

			serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

			objectEntry = _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"textObjectFieldName", "approvedValue"
				).build(),
				serviceContext);

			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			objectEntry = _objectEntryLocalService.updateObjectEntry(
				TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
				objectEntry.getObjectEntryFolderId(),
				HashMapBuilder.<String, Serializable>put(
					"textObjectFieldName", "draftValue"
				).build(),
				serviceContext);

			ObjectEntry latestApprovedObjectEntry =
				_objectEntryLocalService.fetchObjectEntryByHeadObjectEntryId(
					objectEntry.getObjectEntryId());

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED,
				latestApprovedObjectEntry.getStatus());

			PrincipalThreadLocal.setName(null);

			Indexer<ObjectEntry> indexer =
				IndexerRegistryUtil.nullSafeGetIndexer(
					objectEntry.getModelClassName());

			try (SafeCloseable safeCloseable =
					ReindexCacheThreadLocal.openReindexMode()) {

				indexer.reindexCompany(_user.getCompanyId());
			}

			SearchResponse searchResponse = search(
				objectDefinition.getClassName(), "draftValue",
				WorkflowConstants.STATUS_ANY);

			Assert.assertEquals(
				searchResponse.getRequestString() + "->" +
					searchResponse.getDocuments(),
				1, searchResponse.getCount());

			searchResponse = search(
				objectDefinition.getClassName(), "approvedValue",
				WorkflowConstants.STATUS_ANY);

			Assert.assertEquals(
				searchResponse.getRequestString() + "->" +
					searchResponse.getDocuments(),
				0, searchResponse.getCount());

			_objectEntryLocalService.moveObjectEntryToTrash(
				TestPropsValues.getUserId(), objectEntry,
				ServiceContextTestUtil.getServiceContext());

			searchResponse = search(
				objectDefinition.getClassName(), "draftValue",
				WorkflowConstants.STATUS_IN_TRASH);

			Assert.assertEquals(
				searchResponse.getRequestString() + "->" +
					searchResponse.getDocuments(),
				1, searchResponse.getCount());

			searchResponse = search(
				objectDefinition.getClassName(), "approvedValue",
				WorkflowConstants.STATUS_IN_TRASH);

			Assert.assertEquals(
				searchResponse.getRequestString() + "->" +
					searchResponse.getDocuments(),
				0, searchResponse.getCount());
		}
		finally {
			PrincipalThreadLocal.setName(originalName);

			_objectDefinitionLocalService.deleteObjectDefinition(
				objectDefinition);
		}
	}

	@Test
	public void testReindexWithLocalizedAttachmentObjectField()
		throws Exception {

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				_CLASS_NAME_OBJECT_ENTRY_MODEL_DOCUMENT_CONTRIBUTOR,
				LoggerTestUtil.WARN)) {

			ObjectDefinition objectDefinition = _addObjectDefinition(
				Collections.singletonList(
					new AttachmentObjectFieldBuilder(
					).indexed(
						true
					).labelMap(
						LocalizedMapUtil.getLocalizedMap(
							RandomTestUtil.randomString())
					).localized(
						true
					).name(
						"attachmentLocalized"
					).objectFieldSettings(
						Arrays.asList(
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.
									NAME_ACCEPTED_FILE_EXTENSIONS
							).value(
								"txt"
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_FILE_SOURCE
							).value(
								ObjectFieldSettingConstants.
									VALUE_USER_COMPUTER_TO_DOCS_AND_MEDIA
							).build(),
							new ObjectFieldSettingBuilder(
							).name(
								ObjectFieldSettingConstants.NAME_MAX_FILE_SIZE
							).value(
								"100"
							).build())
					).build()));

			String fileName = RandomTestUtil.randomString() + ".txt";

			FileEntry fileEntry1 = ObjectFieldTestUtil.addTempFileEntry(
				fileName, objectDefinition);

			FileEntry fileEntry2 = ObjectFieldTestUtil.addTempFileEntry(
				RandomTestUtil.randomString() + ".txt", objectDefinition);

			ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
				0, TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId(),
				ObjectEntryFolderConstants.
					PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
				null,
				HashMapBuilder.<String, Serializable>put(
					"attachmentLocalized_i18n",
					HashMapBuilder.put(
						"en_US", fileEntry1.getFileEntryId()
					).put(
						"pt_BR", fileEntry2.getFileEntryId()
					).build()
				).build(),
				ServiceContextTestUtil.getServiceContext());

			String originalName = PrincipalThreadLocal.getName();

			_user = TestPropsValues.getUser();

			try {
				PrincipalThreadLocal.setName(null);

				Indexer<ObjectEntry> indexer =
					IndexerRegistryUtil.nullSafeGetIndexer(
						objectEntry.getModelClassName());

				try (SafeCloseable safeCloseable =
						ReindexCacheThreadLocal.openReindexMode()) {

					indexer.reindex(
						objectDefinition.getClassName(),
						objectEntry.getObjectEntryId());
				}

				FieldValuesAssert.assertFieldValue(
					Field.ENTRY_CLASS_NAME, objectEntry.getModelClassName(),
					search(
						objectDefinition.getClassName(),
						String.valueOf(objectEntry.getObjectEntryId())));
				FieldValuesAssert.assertFieldValue(
					Field.ENTRY_CLASS_NAME, objectEntry.getModelClassName(),
					search(objectDefinition.getClassName(), fileName));

				List<LogEntry> logEntries = logCapture.getLogEntries();

				Assert.assertEquals(
					logEntries.toString(), 0, logEntries.size());
			}
			finally {
				PrincipalThreadLocal.setName(originalName);

				_objectDefinitionLocalService.deleteObjectDefinition(
					objectDefinition);
			}
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected SearchResponse search(String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_user.getCompanyId()
			).queryString(
				searchTerm
			).build());
	}

	protected SearchResponse search(String entryClassName, String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_user.getCompanyId()
			).entryClassNames(
				entryClassName
			).queryString(
				searchTerm
			).build());
	}

	protected SearchResponse search(
		String entryClassName, String searchTerm, int status) {

		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_user.getCompanyId()
			).entryClassNames(
				entryClassName
			).queryString(
				searchTerm
			).withSearchContext(
				searchContext -> searchContext.setAttribute(
					Field.STATUS, status)
			).build());
	}

	@Inject
	protected Searcher searcher;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	private ObjectDefinition _addObjectDefinition(
			boolean enableObjectEntryDraft, boolean enableObjectEntryVersioning,
			List<ObjectField> objectFields)
		throws Exception {

		return ObjectDefinitionTestUtil.publishObjectDefinition(
			enableObjectEntryDraft, false, enableObjectEntryVersioning,
			objectFields, ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	private ObjectDefinition _addObjectDefinition(
			List<ObjectField> objectFields)
		throws Exception {

		return _addObjectDefinition(false, false, objectFields);
	}

	private static final String
		_CLASS_NAME_OBJECT_ENTRY_MODEL_DOCUMENT_CONTRIBUTOR =
			"com.liferay.object.internal.search.spi.model.index.contributor." +
				"ObjectEntryModelDocumentContributor";

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	private User _user;

}