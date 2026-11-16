/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.marketing.platform.web.internal.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.content.marketing.platform.test.util.CMPTestUtil;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.exception.ObjectEntryValuesException;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.validation.rule.ObjectValidationRuleResult;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.mail.MailServiceTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.test.rule.SynchronousMailTestRule;

import java.io.Serializable;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Guilherme Camacho
 */
@RunWith(Arquillian.class)
public class ObjectEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousMailTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		CMPTestUtil.getOrAddGroup(ObjectEntryLocalServiceTest.class);

		_cmpTaskObjectDefinition =
			_objectDefinitionLocalService.
				getObjectDefinitionByExternalReferenceCode(
					"L_CMP_TASK", TestPropsValues.getCompanyId());
	}

	@FeatureFlag("LPD-107678")
	@Test
	public void testAddCMPTaskObjectEntry() throws Exception {
		ObjectEntry cmpTaskObjectEntry1 = CMPTestUtil.addCMPTaskObjectEntry();

		ObjectEntry cmpTaskObjectEntry2 = _addCMPTaskObjectEntry(
			cmpTaskObjectEntry1);

		ObjectEntry cmpTaskObjectEntry3 = _addCMPTaskObjectEntry(
			cmpTaskObjectEntry2);

		_assertObjectValidationRuleResult(
			"L_CMP_TASK_MAX_DEPTH",
			() -> _addCMPTaskObjectEntry(cmpTaskObjectEntry3));

		ObjectEntry cmpTaskObjectEntry4 = _addCMPTaskObjectEntry(
			_getCMPProjectObjectEntry(cmpTaskObjectEntry3), cmpTaskObjectEntry3,
			WorkflowConstants.ACTION_SAVE_DRAFT);

		_assertObjectValidationRuleResult(
			"L_CMP_TASK_MAX_DEPTH",
			() -> _updateCMPTaskObjectEntry(
				cmpTaskObjectEntry4, cmpTaskObjectEntry3.getObjectEntryId(),
				WorkflowConstants.ACTION_PUBLISH));

		try {
			_addCMPTaskObjectEntry(
				CMPTestUtil.addCMPProjectObjectEntry(), cmpTaskObjectEntry1,
				WorkflowConstants.ACTION_PUBLISH);

			Assert.fail();
		}
		catch (ObjectEntryValuesException.InvalidValue
					objectEntryValuesException) {

			Assert.assertEquals(
				"The value is invalid for object field " +
					"\"r_cmpTaskToCMPTasks_c_cmpTaskId\"",
				objectEntryValuesException.getMessage());
		}
	}

	@FeatureFlag(enable = false, value = "LPD-107678")
	@Test
	public void testAddCMPTaskObjectEntryWhenFeatureFlagIsDisabled()
		throws Exception {

		ObjectEntry cmpTaskObjectEntry = CMPTestUtil.addCMPTaskObjectEntry();

		_assertObjectValidationRuleResult(
			"L_CMP_TASK_MAX_DEPTH",
			() -> _addCMPTaskObjectEntry(cmpTaskObjectEntry));
	}

	@Test
	public void testDeleteCMPProjectObjectEntry() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpProjectLinkObjectEntry =
			CMPTestUtil.addCMPProjectLinkObjectEntry(cmpProjectObjectEntry);

		_objectEntryLocalService.deleteObjectEntry(
			cmpProjectObjectEntry.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpProjectLinkObjectEntry.getObjectEntryId()));
	}

	@FeatureFlag("LPD-107678")
	@Test
	public void testDeleteCMPTaskObjectEntry() throws Exception {
		ObjectEntry cmpTaskObjectEntry1 = CMPTestUtil.addCMPTaskObjectEntry();

		ObjectEntry cmpTaskLinkObjectEntry1 =
			CMPTestUtil.addCMPTaskLinkObjectEntry(cmpTaskObjectEntry1);

		ObjectEntry cmpTaskObjectEntry2 = _addCMPTaskObjectEntry(
			cmpTaskObjectEntry1);

		ObjectEntry cmpTaskLinkObjectEntry2 =
			CMPTestUtil.addCMPTaskLinkObjectEntry(cmpTaskObjectEntry2);

		ObjectEntry cmpTaskObjectEntry3 = _addCMPTaskObjectEntry(
			cmpTaskObjectEntry2);

		_objectEntryLocalService.deleteObjectEntry(
			cmpTaskObjectEntry1.getObjectEntryId());

		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry1.getObjectEntryId()));
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskLinkObjectEntry2.getObjectEntryId()));
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskObjectEntry2.getObjectEntryId()));
		Assert.assertNull(
			_objectEntryLocalService.fetchObjectEntry(
				cmpTaskObjectEntry3.getObjectEntryId()));
	}

	@Test
	public void testUpdateCMPProjectObjectEntry() throws Exception {
		_testUpdateCMPObjectEntry(CMPTestUtil.addCMPProjectObjectEntry());
	}

	@FeatureFlag("LPD-107678")
	@Test
	public void testUpdateCMPTaskObjectEntry() throws Exception {
		ObjectEntry cmpProjectObjectEntry =
			CMPTestUtil.addCMPProjectObjectEntry();

		ObjectEntry cmpTaskObjectEntry1 = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		_testUpdateCMPObjectEntry(cmpTaskObjectEntry1);

		ObjectEntry cmpTaskObjectEntry2 = _addCMPTaskObjectEntry(
			cmpTaskObjectEntry1);

		_testUpdateCMPObjectEntry(cmpTaskObjectEntry2);

		ObjectEntry cmpTaskObjectEntry3 = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		_updateCMPTaskObjectEntry(
			cmpTaskObjectEntry3, cmpTaskObjectEntry3.getObjectEntryId(),
			WorkflowConstants.ACTION_SAVE_DRAFT);

		_assertObjectValidationRuleResult(
			"L_CMP_TASK_MAX_DEPTH",
			() -> _updateCMPTaskObjectEntry(
				cmpTaskObjectEntry3, cmpTaskObjectEntry3.getObjectEntryId(),
				WorkflowConstants.ACTION_PUBLISH));

		ObjectEntry cmpTaskObjectEntry4 = CMPTestUtil.addCMPTaskObjectEntry(
			cmpProjectObjectEntry);

		ObjectEntry cmpTaskObjectEntry5 = _addCMPTaskObjectEntry(
			cmpProjectObjectEntry, cmpTaskObjectEntry4,
			WorkflowConstants.ACTION_SAVE_DRAFT);

		_updateCMPTaskObjectEntry(
			cmpTaskObjectEntry4, cmpTaskObjectEntry5.getObjectEntryId(),
			WorkflowConstants.ACTION_SAVE_DRAFT);

		_assertObjectValidationRuleResult(
			"L_CMP_TASK_MAX_DEPTH",
			() -> _updateCMPTaskObjectEntry(
				cmpTaskObjectEntry4, cmpTaskObjectEntry5.getObjectEntryId(),
				WorkflowConstants.ACTION_PUBLISH));
	}

	private ObjectEntry _addCMPTaskObjectEntry(ObjectEntry cmpTaskObjectEntry)
		throws Exception {

		return _addCMPTaskObjectEntry(
			_getCMPProjectObjectEntry(cmpTaskObjectEntry), cmpTaskObjectEntry,
			WorkflowConstants.ACTION_PUBLISH);
	}

	private ObjectEntry _addCMPTaskObjectEntry(
			ObjectEntry cmpProjectObjectEntry, ObjectEntry cmpTaskObjectEntry,
			int workflowAction)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(workflowAction);

		return _objectEntryLocalService.addObjectEntry(
			cmpProjectObjectEntry.getGroupId(), TestPropsValues.getUserId(),
			_cmpTaskObjectDefinition.getObjectDefinitionId(), 0, null,
			HashMapBuilder.<String, Serializable>put(
				"r_cmpProjectToCMPTasks_c_cmpProjectId",
				cmpProjectObjectEntry.getObjectEntryId()
			).put(
				"r_cmpTaskToCMPTasks_c_cmpTaskId",
				cmpTaskObjectEntry.getObjectEntryId()
			).put(
				"title", RandomTestUtil.randomString()
			).build(),
			serviceContext);
	}

	private void _assertObjectValidationRuleResult(
			String externalReferenceCode,
			UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		try {
			unsafeRunnable.run();

			Assert.fail();
		}
		catch (ModelListenerException modelListenerException) {
			ObjectValidationRuleEngineException
				objectValidationRuleEngineException =
					(ObjectValidationRuleEngineException)
						modelListenerException.getCause();

			List<ObjectValidationRuleResult> objectValidationRuleResults =
				objectValidationRuleEngineException.
					getObjectValidationRuleResults();

			Assert.assertEquals(
				objectValidationRuleResults.toString(), 1,
				objectValidationRuleResults.size());

			ObjectValidationRuleResult objectValidationRuleResult =
				objectValidationRuleResults.get(0);

			Assert.assertEquals(
				externalReferenceCode,
				objectValidationRuleResult.getExternalReferenceCode());
		}
	}

	private ObjectEntry _getCMPProjectObjectEntry(
			ObjectEntry cmpTaskObjectEntry)
		throws Exception {

		return _objectEntryLocalService.getObjectEntry(
			MapUtil.getLong(
				cmpTaskObjectEntry.getValues(),
				"r_cmpProjectToCMPTasks_c_cmpProjectId"));
	}

	private void _testUpdateCMPObjectEntry(ObjectEntry cmpObjectEntry)
		throws Exception {

		_objectEntryLocalService.subscribeObjectEntry(
			TestPropsValues.getUserId(), cmpObjectEntry.getGroupId(),
			cmpObjectEntry.getObjectEntryId());

		_objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), cmpObjectEntry.getObjectEntryId(),
			cmpObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>putAll(
				cmpObjectEntry.getValues()
			).put(
				"description",
				StringBundler.concat(
					"<p><strong>bold</strong></p><p><img src=\"",
					RandomTestUtil.randomString(),
					"\"></p><figure class=\"media\"><oembed url=\"",
					RandomTestUtil.randomString(),
					"\"></oembed></figure><p><iframe data-video-liferay src=\"",
					RandomTestUtil.randomString(), "\"></iframe></p>")
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Assert.assertTrue(
			MailServiceTestUtil.lastMailMessageContains(
				"<p><strong>bold</strong></p><p>[Image]</p><figure " +
					"class=\"media\">[Video]</figure><p>[Video]</p>"));
	}

	private ObjectEntry _updateCMPTaskObjectEntry(
			ObjectEntry cmpTaskObjectEntry, long parentCMPTaskObjectEntryId,
			int workflowAction)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setWorkflowAction(workflowAction);

		return _objectEntryLocalService.updateObjectEntry(
			TestPropsValues.getUserId(), cmpTaskObjectEntry.getObjectEntryId(),
			cmpTaskObjectEntry.getObjectEntryFolderId(),
			HashMapBuilder.<String, Serializable>putAll(
				cmpTaskObjectEntry.getValues()
			).put(
				"r_cmpTaskToCMPTasks_c_cmpTaskId", parentCMPTaskObjectEntryId
			).build(),
			serviceContext);
	}

	private ObjectDefinition _cmpTaskObjectDefinition;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectEntryLocalService _objectEntryLocalService;

}