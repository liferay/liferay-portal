/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectEntryVersionLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pedro Leite
 */
@FeatureFlags(
	featureFlags = {@FeatureFlag("LPD-17564"), @FeatureFlag("LPD-32050")}
)
@RunWith(Arquillian.class)
public class ObjectEntryVersionModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));

		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			true, false, true,
			Collections.singletonList(
				new TextObjectFieldBuilder(
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					"textObjectFieldName"
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
	}

	@Before
	public void setUp() throws Exception {
		_objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build());
	}

	@Test
	public void testOnAfterCreate() throws Exception {
		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());

		Map<String, Serializable> values = _objectEntry.getValues();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		_objectEntry = _updateObjectEntry(_objectEntry, serviceContext);

		_assertLatestApprovedObjectEntry(
			values, 1, _objectEntry.getObjectEntryId());

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());
	}

	@Test
	public void testOnAfterRemove() throws Exception {
		Map<String, Serializable> values1 = _objectEntry.getValues();

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		Map<String, Serializable> values2 = _objectEntry.getValues();

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		_objectEntry = _objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), _objectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		_assertLatestApprovedObjectEntry(
			values2, 2, _objectEntry.getObjectEntryId());

		_objectEntryVersionLocalService.deleteObjectEntryVersion(
			_objectEntry.getObjectEntryId(), 2);

		_assertLatestApprovedObjectEntry(
			values1, 1, _objectEntry.getObjectEntryId());

		_objectEntryVersionLocalService.deleteObjectEntryVersion(
			_objectEntry.getObjectEntryId(), 1);

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());
	}

	@Test
	public void testOnAfterUpdate() throws Exception {

		// Approved object entry

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());

		Map<String, Serializable> values1 = _objectEntry.getValues();

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());

		Map<String, Serializable> values2 = _objectEntry.getValues();

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());

		// Expired object entry

		_objectEntry = _objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), _objectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		_assertLatestApprovedObjectEntry(
			values2, 2, _objectEntry.getObjectEntryId());

		_objectEntryVersionLocalService.expireObjectEntryVersion(
			TestPropsValues.getUserId(), _objectEntry, 2,
			ServiceContextTestUtil.getServiceContext());

		_assertLatestApprovedObjectEntry(
			values1, 1, _objectEntry.getObjectEntryId());

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());

		Map<String, Serializable> values3 = _objectEntry.getValues();

		// Pending object entry

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		_objectEntry = _updateObjectEntry(
			_objectEntry, ServiceContextTestUtil.getServiceContext());

		_assertLatestApprovedObjectEntry(
			values3, 4, _objectEntry.getObjectEntryId());

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksBySubmittingUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				false, 0, 1, null);

		WorkflowTask workflowTask = workflowTasks.get(0);

		_workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);

		_workflowTaskManager.completeWorkflowTask(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), Constants.APPROVE,
			StringPool.BLANK, null);

		_assertNullLatestApprovedObjectEntry(_objectEntry.getObjectEntryId());
	}

	private void _assertLatestApprovedObjectEntry(
		Map<String, Serializable> expectedValues, int expectedVersion,
		long objectEntryId) {

		ObjectEntry objectEntry =
			_objectEntryLocalService.fetchObjectEntryByHeadObjectEntryId(
				objectEntryId);

		Assert.assertNotNull(objectEntry);

		Assert.assertEquals(
			MapUtil.getString(expectedValues, "textObjectFieldName"),
			MapUtil.getString(objectEntry.getValues(), "textObjectFieldName"));

		Assert.assertEquals(expectedVersion, objectEntry.getVersion());
	}

	private void _assertNullLatestApprovedObjectEntry(long objectEntryId) {
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntryByHeadObjectEntryId(
				objectEntryId));
	}

	private ObjectEntry _updateObjectEntry(
			ObjectEntry objectEntry, ServiceContext serviceContext)
		throws Exception {

		return _objectEntryLocalService.updateObjectEntry(
			objectEntry.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				"textObjectFieldName", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	private static ObjectDefinition _objectDefinition;
	private static PermissionChecker _originalPermissionChecker;

	private ObjectEntry _objectEntry;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private ObjectEntryVersionLocalService _objectEntryVersionLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}