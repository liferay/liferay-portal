/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.object.configuration.ObjectEntryVersionConfiguration;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.constants.ObjectEntryFolderConstants;
import com.liferay.object.exception.RequiredObjectEntryVersionException;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ModelListener;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.scheduler.TimeUnit;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.dto.converter.DTOConverterRegistry;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Serializable;

import java.sql.Date;

import java.time.LocalDate;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Feliphe Marinho
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
)
@RunWith(Arquillian.class)
public class ObjectEntryVersionLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				true, true, true, false, false, true, null,
				RandomTestUtil.randomLocaleStringMap(),
				"A" + StringUtil.randomString(), null, null,
				RandomTestUtil.randomLocaleStringMap(), true,
				ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(),
				Collections.singletonList(
					new TextObjectFieldBuilder(
					).labelMap(
						RandomTestUtil.randomLocaleStringMap()
					).name(
						"textObjectFieldName"
					).build()),
				Collections.emptyList());

		_objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				_objectDefinition.getObjectDefinitionId());

		_workflowDefinition =
			_workflowDefinitionManager.liberalGetLatestWorkflowDefinition(
				TestPropsValues.getCompanyId(), "Single Approver");

		_configurationProvider.saveCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"maximumRetentionPeriod", 1
			).put(
				"maximumVersionsPerEntry", 3
			).build());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		_configurationProvider.deleteCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId());
	}

	@Test
	public void testAddObjectEntryVersion() throws Exception {
		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build());

		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue1"),
					WorkflowConstants.STATUS_APPROVED, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(2, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue1"),
					WorkflowConstants.STATUS_APPROVED, 1),
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue2"),
					WorkflowConstants.STATUS_APPROVED, 2)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));
	}

	@Test
	public void testAddObjectEntryVersionWithMaximumVersionsPerEntryLimit()
		throws Exception {

		ObjectEntryVersionConfiguration objectEntryVersionConfiguration =
			_configurationProvider.getCompanyConfiguration(
				ObjectEntryVersionConfiguration.class,
				CompanyThreadLocal.getCompanyId());

		Assert.assertEquals(
			3, objectEntryVersionConfiguration.maximumVersionsPerEntry());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		_updateLatestObjectEntryVersion(_getPastDate(3), objectEntry);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_updateLatestObjectEntryVersion(_getPastDate(2), objectEntry);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			3,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			3,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		_configurationProvider.saveCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"maximumRetentionPeriod", 1
			).put(
				"maximumVersionsPerEntry", 4
			).build());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			4,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));
	}

	@Test
	public void testAddObjectEntryVersionWithObjectEntryDraftEnabled()
		throws Exception {

		// Add draft object entry

		_objectDefinition.setEnableObjectEntryDraft(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isDraft());
		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue1"),
					WorkflowConstants.STATUS_DRAFT, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		// Update as draft

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isDraft());
		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue2"),
					WorkflowConstants.STATUS_DRAFT, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		// Update as published

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue3"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isApproved());
		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue3"),
					WorkflowConstants.STATUS_APPROVED, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		// Update published object entry as draft

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue4"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isDraft());
		Assert.assertEquals(2, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue3"),
					WorkflowConstants.STATUS_APPROVED, 1),
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue4"),
					WorkflowConstants.STATUS_DRAFT, 2)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		_objectDefinition.setEnableObjectEntryDraft(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	@Test
	public void testAddObjectEntryVersionWithObjectEntryScheduleEnabled()
		throws Exception {

		_testAddObjectEntryVersionWithObjectEntryScheduleEnabled(
			false, WorkflowConstants.STATUS_APPROVED,
			WorkflowConstants.ACTION_PUBLISH);
		_testAddObjectEntryVersionWithObjectEntryScheduleEnabled(
			true, WorkflowConstants.STATUS_DRAFT,
			WorkflowConstants.ACTION_SAVE_DRAFT);
	}

	@Test
	public void testAddObjectEntryVersionWithWorkflowEnabled()
		throws Exception {

		// Add pending object entry

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkService.addWorkflowDefinitionLink(
				null, TestPropsValues.getUserId(),
				TestPropsValues.getCompanyId(), 0,
				_objectDefinition.getClassName(), 0, 0,
				_workflowDefinition.getName(),
				_workflowDefinition.getVersion());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build());

		Assert.assertTrue(objectEntry.isPending());
		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue1"),
					WorkflowConstants.STATUS_PENDING, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		// Change pending object entry values

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isPending());
		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue2"),
					WorkflowConstants.STATUS_PENDING, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		// Complete pending object entry's workflow instance

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksByUserRoles(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		WorkflowTask workflowTask = workflowTasks.get(0);

		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		_workflowTaskManager.completeWorkflowTask(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), Constants.APPROVE,
			StringPool.BLANK, null);

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue2"),
					WorkflowConstants.STATUS_APPROVED, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		objectEntry = _objectEntryLocalService.getObjectEntry(
			objectEntry.getObjectEntryId());

		Assert.assertTrue(objectEntry.isApproved());
		Assert.assertEquals(1, objectEntry.getVersion());

		// Update approved object entry starting a new workflow instance

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue3"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isPending());
		Assert.assertEquals(2, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue2"),
					WorkflowConstants.STATUS_APPROVED, 1),
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue3"),
					WorkflowConstants.STATUS_PENDING, 2)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		_objectDefinition.setEnableObjectEntryDraft(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		// Update pending object entry as draft

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue4"
			).build(),
			serviceContext);

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);

		Assert.assertTrue(objectEntry.isPending());
		Assert.assertEquals(2, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue2"),
					WorkflowConstants.STATUS_APPROVED, 1),
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue4"),
					WorkflowConstants.STATUS_PENDING, 2)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		_objectDefinition.setEnableObjectEntryDraft(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	@Test
	public void testAuditRouter() throws Exception {
		Queue<AuditMessage> auditMessages = new LinkedList<>();

		AuditRouter auditRouter =
			(AuditRouter)ReflectionTestUtil.getAndSetFieldValue(
				_objectEntryVersionModelListener, "_auditRouter",
				ProxyUtil.newProxyInstance(
					AuditRouter.class.getClassLoader(),
					new Class<?>[] {AuditRouter.class},
					(proxy, method, arguments) -> {
						auditMessages.add((AuditMessage)arguments[0]);

						return null;
					}));

		_objectDefinition.setEnableObjectEntryHistory(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_objectEntryVersionLocalService.deleteObjectEntryVersion(
			objectEntry.getObjectEntryId(), 1);

		AuditMessage auditMessage = auditMessages.poll();

		JSONAssert.assertEquals(
			JSONUtil.put(
				"textObjectFieldName", "textObjectFieldValue1"
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.STRICT_ORDER);

		auditMessages.clear();

		ReflectionTestUtil.setFieldValue(
			_objectEntryVersionModelListener, "_auditRouter", auditRouter);

		_objectDefinition.setEnableObjectEntryHistory(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	@Test
	public void testCheckObjectEntryVersionsWithMaximumRetentionPeriod()
		throws Exception {

		ObjectEntryVersionConfiguration objectEntryVersionConfiguration =
			_configurationProvider.getCompanyConfiguration(
				ObjectEntryVersionConfiguration.class,
				CompanyThreadLocal.getCompanyId());

		Assert.assertEquals(
			1, objectEntryVersionConfiguration.maximumRetentionPeriod());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		_updateLatestObjectEntryVersion(_getPastDate(3), objectEntry);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_updateLatestObjectEntryVersion(_getPastDate(2), objectEntry);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		_updateLatestObjectEntryVersion(_getPastDate(2), objectEntry);

		Assert.assertEquals(
			3,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		_objectEntryVersionLocalService.checkObjectEntryVersions(
			objectEntry.getCompanyId());

		Assert.assertEquals(
			1,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));
	}

	@Test
	public void testDeleteObjectEntryVersion() throws Exception {
		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		Assert.assertEquals(
			1,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		long objectEntryId = objectEntry.getObjectEntryId();

		AssertUtils.assertFailure(
			RequiredObjectEntryVersionException.MustHaveOneVersion.class,
			"At least one version must remain",
			() -> _objectEntryVersionLocalService.deleteObjectEntryVersion(
				objectEntryId, 1));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			2,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		AssertUtils.assertFailure(
			RequiredObjectEntryVersionException.MustNotDeleteLatestVersion.
				class,
			"The latest version cannot be deleted",
			() -> _objectEntryVersionLocalService.deleteObjectEntryVersion(
				objectEntryId, 2));

		_objectEntryVersionLocalService.deleteObjectEntryVersion(
			objectEntry.getObjectEntryId(), 1);

		Assert.assertEquals(
			1,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);
	}

	@Test
	public void testDeleteObjectEntryVersions() throws Exception {
		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());

		Assert.assertEquals(
			1,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(
			2,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));

		_objectEntryLocalService.deleteObjectEntry(objectEntry);

		Assert.assertEquals(
			0,
			_objectEntryVersionLocalService.getObjectEntryVersionsCount(
				objectEntry.getObjectEntryId()));
	}

	@Test
	public void testFetchObjectEntryVersion() throws Exception {
		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build());

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.fetchObjectEntryVersion(
				objectEntry.getObjectEntryId(), 1);

		Assert.assertNotNull(objectEntryVersion);
		Assert.assertEquals(1, objectEntryVersion.getVersion());

		objectEntryVersion =
			_objectEntryVersionLocalService.fetchObjectEntryVersion(
				objectEntry.getObjectEntryId(), 2);

		Assert.assertNotNull(objectEntryVersion);
		Assert.assertEquals(2, objectEntryVersion.getVersion());

		Assert.assertNull(
			_objectEntryVersionLocalService.fetchObjectEntryVersion(
				objectEntry.getObjectEntryId(), 3));
	}

	@Test
	public void testIsLatestObjectEntryVersion() throws Exception {
		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build());

		Assert.assertEquals(1, objectEntry.getVersion());
		Assert.assertTrue(
			_objectEntryVersionLocalService.isLatestObjectEntryVersion(
				objectEntry.getObjectEntryId(), 1));

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertEquals(2, objectEntry.getVersion());
		Assert.assertFalse(
			_objectEntryVersionLocalService.isLatestObjectEntryVersion(
				objectEntry.getObjectEntryId(), 1));
		Assert.assertTrue(
			_objectEntryVersionLocalService.isLatestObjectEntryVersion(
				objectEntry.getObjectEntryId(), 2));
	}

	private void _assertEquals(
			List<ObjectEntryVersion> expectedObjectEntryVersions,
			List<ObjectEntryVersion> actualObjectEntryVersions)
		throws Exception {

		Assert.assertEquals(
			actualObjectEntryVersions.toString(),
			expectedObjectEntryVersions.size(),
			actualObjectEntryVersions.size());

		for (int i = 0; i < expectedObjectEntryVersions.size(); i++) {
			ObjectEntryVersion expectedObjectEntryVersion =
				expectedObjectEntryVersions.get(i);
			ObjectEntryVersion actualObjectEntryVersion =
				actualObjectEntryVersions.get(i);

			JSONObject expectedJSONObject = JSONFactoryUtil.createJSONObject(
				expectedObjectEntryVersion.getContent());
			JSONObject actualJSONObject = JSONFactoryUtil.createJSONObject(
				actualObjectEntryVersion.getContent());

			for (String key : expectedJSONObject.keySet()) {
				Object expectedValue = expectedJSONObject.get(key);
				Object actualValue = actualJSONObject.get(key);

				if (expectedValue instanceof JSONObject) {
					Assert.assertTrue(
						JSONUtil.equals(
							(JSONObject)expectedValue,
							(JSONObject)actualValue));
				}
				else if (expectedValue instanceof JSONArray) {
					Assert.assertTrue(
						JSONUtil.equals(
							(JSONArray)expectedValue, (JSONArray)actualValue));
				}
				else {
					Assert.assertEquals(expectedValue, actualValue);
				}
			}

			Assert.assertEquals(
				expectedObjectEntryVersion.getVersion(),
				actualObjectEntryVersion.getVersion());
			Assert.assertEquals(
				expectedObjectEntryVersion.getStatus(),
				actualObjectEntryVersion.getStatus());
		}
	}

	private ObjectEntryVersion _createObjectEntryVersion(
		String externalReferenceCode, JSONObject jsonObject, int status,
		int version) {

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.createObjectEntryVersion(
				_counterLocalService.increment());

		objectEntryVersion.setContent(
			JSONUtil.put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"keywords", JSONUtil.putAll()
			).put(
				"objectEntryFolderExternalReferenceCode", StringPool.BLANK
			).put(
				"objectEntryFolderId",
				Long.valueOf(
					ObjectEntryFolderConstants.
						PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT)
			).put(
				"properties", jsonObject
			).put(
				"taxonomyCategoryBriefs", JSONUtil.putAll()
			).toString());
		objectEntryVersion.setVersion(version);
		objectEntryVersion.setStatus(status);

		return objectEntryVersion;
	}

	private Date _getPastDate(int months) {
		return Date.valueOf(
			LocalDate.now(
			).minusMonths(
				months
			));
	}

	private void _testAddObjectEntryVersionWithObjectEntryScheduleEnabled(
			boolean enableObjectEntryDraft, int status, int workflowAction)
		throws Exception {

		// Add object entry as published with display date in the future

		ObjectEntryVersionConfiguration
			originalObjectEntryVersionConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ObjectEntryVersionConfiguration.class,
					CompanyThreadLocal.getCompanyId());

		_configurationProvider.saveCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"maximumRetentionPeriod", 1
			).put(
				"maximumVersionsPerEntry", 4
			).build());

		_objectDefinition.setEnableObjectEntryDraft(enableObjectEntryDraft);
		_objectDefinition.setEnableObjectEntrySchedule(true);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_PUBLISH);

		ObjectEntry objectEntry = _objectEntryLocalService.addObjectEntry(
			0, TestPropsValues.getUserId(),
			_objectDefinition.getObjectDefinitionId(),
			ObjectEntryFolderConstants.PARENT_OBJECT_ENTRY_FOLDER_ID_DEFAULT,
			null,
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new Date(System.currentTimeMillis() + TimeUnit.DAY.toMillis(1))
			).put(
				"textObjectFieldName", "textObjectFieldValue1"
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isScheduled());
		Assert.assertEquals(1, objectEntry.getVersion());

		_assertEquals(
			Arrays.asList(
				_createObjectEntryVersion(
					objectEntry.getExternalReferenceCode(),
					JSONUtil.put(
						"textObjectFieldName", "textObjectFieldValue1"),
					WorkflowConstants.STATUS_SCHEDULED, 1)),
			_objectEntryVersionLocalService.getObjectEntryVersions(
				objectEntry.getObjectEntryId()));

		// Update object entry with display date in the future

		serviceContext.setWorkflowAction(workflowAction);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new Date(System.currentTimeMillis() + TimeUnit.DAY.toMillis(1))
			).put(
				"textObjectFieldName", "textObjectFieldValue2"
			).build(),
			serviceContext);

		Assert.assertEquals(2, objectEntry.getVersion());

		if (workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			Assert.assertEquals(
				WorkflowConstants.STATUS_SCHEDULED, objectEntry.getStatus());

			_assertEquals(
				Arrays.asList(
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue1"),
						WorkflowConstants.STATUS_SCHEDULED, 1),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue2"),
						WorkflowConstants.STATUS_SCHEDULED, 2)),
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId()));
		}
		else {
			Assert.assertEquals(status, objectEntry.getStatus());

			_assertEquals(
				Arrays.asList(
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue1"),
						WorkflowConstants.STATUS_SCHEDULED, 1),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue2"),
						WorkflowConstants.STATUS_DRAFT, 2)),
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId()));
		}

		// Update object entry with display date in the past

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"displayDate",
				new Date(System.currentTimeMillis() - TimeUnit.DAY.toMillis(1))
			).put(
				"textObjectFieldName", "textObjectFieldValue3"
			).build(),
			serviceContext);

		Assert.assertEquals(status, objectEntry.getStatus());

		if (workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			Assert.assertEquals(3, objectEntry.getVersion());

			_assertEquals(
				Arrays.asList(
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue1"),
						WorkflowConstants.STATUS_SCHEDULED, 1),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue2"),
						WorkflowConstants.STATUS_SCHEDULED, 2),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue3"),
						WorkflowConstants.STATUS_APPROVED, 3)),
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId()));
		}
		else {
			Assert.assertEquals(2, objectEntry.getVersion());

			_assertEquals(
				Arrays.asList(
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue1"),
						WorkflowConstants.STATUS_SCHEDULED, 1),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue3"),
						WorkflowConstants.STATUS_DRAFT, 2)),
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId()));
		}

		// Update object entry with null display date

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", "textObjectFieldValue4"
			).build(),
			serviceContext);

		Assert.assertEquals(status, objectEntry.getStatus());

		if (workflowAction == WorkflowConstants.ACTION_PUBLISH) {
			Assert.assertEquals(4, objectEntry.getVersion());

			_assertEquals(
				Arrays.asList(
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue1"),
						WorkflowConstants.STATUS_SCHEDULED, 1),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue2"),
						WorkflowConstants.STATUS_SCHEDULED, 2),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue3"),
						WorkflowConstants.STATUS_APPROVED, 3),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue4"),
						WorkflowConstants.STATUS_APPROVED, 4)),
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId()));
		}
		else {
			Assert.assertEquals(2, objectEntry.getVersion());

			_assertEquals(
				Arrays.asList(
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue1"),
						WorkflowConstants.STATUS_SCHEDULED, 1),
					_createObjectEntryVersion(
						objectEntry.getExternalReferenceCode(),
						JSONUtil.put(
							"textObjectFieldName", "textObjectFieldValue4"),
						WorkflowConstants.STATUS_DRAFT, 2)),
				_objectEntryVersionLocalService.getObjectEntryVersions(
					objectEntry.getObjectEntryId()));
		}

		_configurationProvider.saveCompanyConfiguration(
			ObjectEntryVersionConfiguration.class,
			TestPropsValues.getCompanyId(),
			HashMapDictionaryBuilder.<String, Object>put(
				"maximumRetentionPeriod", 1
			).put(
				"maximumVersionsPerEntry",
				originalObjectEntryVersionConfiguration.
					maximumVersionsPerEntry()
			).build());

		_objectDefinition.setEnableObjectEntryDraft(false);
		_objectDefinition.setEnableObjectEntrySchedule(false);

		_objectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				_objectDefinition);
	}

	private void _updateLatestObjectEntryVersion(
			Date createDate, ObjectEntry objectEntry)
		throws Exception {

		ObjectEntryVersion objectEntryVersion =
			_objectEntryVersionLocalService.getObjectEntryVersion(
				objectEntry.getObjectEntryId(), objectEntry.getVersion());

		objectEntryVersion.setCreateDate(createDate);

		_objectEntryVersionLocalService.updateObjectEntryVersion(
			objectEntryVersion);
	}

	@Inject
	private static ConfigurationProvider _configurationProvider;

	private static ObjectDefinition _objectDefinition;

	@Inject
	private static ObjectDefinitionLocalService _objectDefinitionLocalService;

	private static WorkflowDefinition _workflowDefinition;

	@Inject
	private static WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private CounterLocalService _counterLocalService;

	@Inject
	private DTOConverterRegistry _dtoConverterRegistry;

	@Inject
	private JSONFactory _jsonFactory;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Inject(
		filter = "component.name=com.liferay.object.internal.model.listener.ObjectEntryVersionModelListener"
	)
	private ModelListener<ObjectDefinition> _objectEntryVersionModelListener;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionLinkService _workflowDefinitionLinkService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}