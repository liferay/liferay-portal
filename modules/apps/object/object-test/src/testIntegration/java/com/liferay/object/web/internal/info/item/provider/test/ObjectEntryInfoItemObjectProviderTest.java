/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.info.exception.NoSuchInfoItemException;
import com.liferay.info.item.ClassPKInfoItemIdentifier;
import com.liferay.info.item.InfoItemServiceRegistry;
import com.liferay.info.item.provider.InfoItemObjectProvider;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.related.models.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Roselaine Marques
 */
@RunWith(Arquillian.class)
@Sync
public class ObjectEntryInfoItemObjectProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@Before
	public void setUp() throws Exception {
		_objectDefinition = ObjectDefinitionTestUtil.publishObjectDefinition(
			true, false, true,
			List.of(
				new TextObjectFieldBuilder(
				).userId(
					TestPropsValues.getUserId()
				).labelMap(
					LocalizedMapUtil.getLocalizedMap(
						RandomTestUtil.randomString())
				).name(
					_OBJECT_FIELD_NAME
				).build()),
			ObjectDefinitionConstants.SCOPE_COMPANY);
	}

	@Test
	public void testGetInfoItem() throws Exception {
		_testGetInfoItemApprovedObjectEntry();
		_testGetInfoItemDraftObjectEntryWithLatestApprovedVersion();
		_testGetInfoItemExpiredObjectEntryWithoutLatestApprovedVersion();
		_testGetInfoItemPendingObjectEntryWithLatestApprovedVersion();
	}

	private ObjectEntry _getInfoItem(long classPK)
		throws NoSuchInfoItemException {

		InfoItemObjectProvider<ObjectEntry> infoItemObjectProvider =
			_infoItemServiceRegistry.getFirstInfoItemService(
				InfoItemObjectProvider.class, _objectDefinition.getClassName(),
				ClassPKInfoItemIdentifier.INFO_ITEM_SERVICE_FILTER);

		return infoItemObjectProvider.getInfoItem(
			new ClassPKInfoItemIdentifier(classPK));
	}

	private void _testGetInfoItemApprovedObjectEntry() throws Exception {
		String value = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, value
			).build());

		Assert.assertTrue(objectEntry.isApproved());

		ObjectEntry infoItemObjectEntry = _getInfoItem(
			objectEntry.getObjectEntryId());

		Assert.assertTrue(infoItemObjectEntry.isApproved());

		Map<String, Serializable> values = infoItemObjectEntry.getValues();

		Assert.assertEquals(value, values.get(_OBJECT_FIELD_NAME));
	}

	private void _testGetInfoItemDraftObjectEntryWithLatestApprovedVersion()
		throws Exception {

		String approvedValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, approvedValue
			).build());

		Assert.assertTrue(objectEntry.isApproved());

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(WorkflowConstants.ACTION_SAVE_DRAFT);

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build(),
			serviceContext);

		Assert.assertTrue(objectEntry.isDraft());

		ObjectEntry infoItemObjectEntry = _getInfoItem(
			objectEntry.getObjectEntryId());

		Assert.assertTrue(infoItemObjectEntry.isApproved());

		Map<String, Serializable> values = infoItemObjectEntry.getValues();

		Assert.assertEquals(approvedValue, values.get(_OBJECT_FIELD_NAME));
	}

	private void _testGetInfoItemExpiredObjectEntryWithoutLatestApprovedVersion()
		throws Exception {

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, RandomTestUtil.randomString()
			).build());

		objectEntry = _objectEntryLocalService.expireObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(objectEntry.isExpired());

		long objectEntryId = objectEntry.getObjectEntryId();

		AssertUtils.assertFailure(
			NoSuchInfoItemException.class,
			"Unable to get an approved object entry " + objectEntryId,
			() -> _getInfoItem(objectEntryId));
	}

	private void _testGetInfoItemPendingObjectEntryWithLatestApprovedVersion()
		throws Exception {

		String approvedValue = RandomTestUtil.randomString();

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			0, _objectDefinition.getObjectDefinitionId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, approvedValue
			).build());

		Assert.assertTrue(objectEntry.isApproved());

		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
			_objectDefinition.getClassName(), 0, 0, "Single Approver", 1);

		String pendingValue = RandomTestUtil.randomString();

		objectEntry = _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), objectEntry.getObjectEntryId(),
			objectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>put(
				_OBJECT_FIELD_NAME, pendingValue
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(objectEntry.isPending());

		ObjectEntry infoItemObjectEntry = _getInfoItem(
			objectEntry.getObjectEntryId());

		Assert.assertTrue(infoItemObjectEntry.isApproved());

		Map<String, Serializable> values = infoItemObjectEntry.getValues();

		Assert.assertEquals(approvedValue, values.get(_OBJECT_FIELD_NAME));

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

		infoItemObjectEntry = _getInfoItem(objectEntry.getObjectEntryId());

		Assert.assertTrue(infoItemObjectEntry.isApproved());

		values = infoItemObjectEntry.getValues();

		Assert.assertEquals(pendingValue, values.get(_OBJECT_FIELD_NAME));
	}

	private static final String _OBJECT_FIELD_NAME =
		"a" + RandomTestUtil.randomString();

	@Inject
	private InfoItemServiceRegistry _infoItemServiceRegistry;

	@DeleteAfterTestRun
	private ObjectDefinition _objectDefinition;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}