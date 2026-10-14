/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.runtime.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.WorkflowDefinitionLink;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.RequiredWorkflowDefinitionException;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinition;
import com.liferay.portal.workflow.kaleo.model.KaleoDefinitionVersion;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.model.KaleoNotification;
import com.liferay.portal.workflow.kaleo.model.KaleoNotificationRecipient;
import com.liferay.portal.workflow.kaleo.model.KaleoTask;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskAssignment;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.runtime.WorkflowEngine;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoDefinitionVersionLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoNotificationRecipientLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskAssignmentLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTaskLocalService;
import com.liferay.portal.workflow.kaleo.service.KaleoTimerLocalService;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.InputStream;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class WorkflowEngineTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		ObjectDefinition objectDefinition =
			ObjectDefinitionTestUtil.publishObjectDefinition();
		_workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				FileUtil.getBytes(
					_getResourceInputStream("valid-workflow-definition.xml")),
				TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		_workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
				TestPropsValues.getUserId(), TestPropsValues.getCompanyId(), 0,
				objectDefinition.getClassName(), 0, 0,
				_workflowDefinition.getName(), 1);
	}

	@After
	public void tearDown() throws Exception {
		_deleteWorkflowDefinition(_workflowDefinition);
	}

	@Test
	public void testDeleteWorkflowDefinition() throws Exception {
		AssertUtils.assertFailure(
			RequiredWorkflowDefinitionException.class, null,
			() -> _workflowEngine.deleteWorkflowDefinition(
				_workflowDefinition.getName(), 1,
				ServiceContextTestUtil.getServiceContext()));

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			_workflowDefinitionLink);

		AssertUtils.assertFailure(
			WorkflowException.class,
			"Cannot delete active workflow definition " +
				_workflowDefinition.getWorkflowDefinitionId(),
			() -> _workflowEngine.deleteWorkflowDefinition(
				_workflowDefinition.getName(), 1,
				ServiceContextTestUtil.getServiceContext()));
	}

	@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49856"))
	@Test
	public void testDeployWorkflowDefinition() throws Exception {
		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				FileUtil.getBytes(
					_getResourceInputStream("valid-workflow-definition.xml")),
				TestPropsValues.getCompanyId(),
				_workflowDefinition.getExternalReferenceCode(),
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		Assert.assertEquals(
			_workflowDefinition.getName(), workflowDefinition.getName());
		Assert.assertEquals(
			_workflowDefinition.getVersion() + 1,
			workflowDefinition.getVersion());

		WorkflowDefinitionLink workflowDefinitionLink =
			_workflowDefinitionLinkLocalService.getWorkflowDefinitionLink(
				_workflowDefinitionLink.getWorkflowDefinitionLinkId());

		Assert.assertEquals(
			workflowDefinition.getVersion(),
			workflowDefinitionLink.getWorkflowDefinitionVersion());

		_workflowDefinitionLinkLocalService.deleteWorkflowDefinitionLink(
			workflowDefinitionLink);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				_user1)) {

			WorkflowException workflowException = Assert.assertThrows(
				WorkflowException.class,
				() -> _workflowDefinitionManager.deployWorkflowDefinition(
					RandomTestUtil.randomBytes(),
					TestPropsValues.getCompanyId(), null,
					RandomTestUtil.randomString(),
					RandomTestUtil.randomString(), _user1.getUserId()));

			Throwable throwable = workflowException.getCause();

			Assert.assertTrue(
				String.valueOf(throwable),
				throwable instanceof PrincipalException.MustHavePermission);
		}

		_testDeployWorkflowDefinitionWithRoleExternalReferenceCode();
		_testDeployWorkflowDefinitionWithRoleId();
		_testDeployWorkflowDefinitionWithUserExternalReferenceCode();
		_testDeployWorkflowDefinitionWithUserId();
	}

	private void _deleteWorkflowDefinition(
			WorkflowDefinition workflowDefinition)
		throws Exception {

		KaleoDefinition kaleoDefinition =
			_kaleoDefinitionLocalService.getKaleoDefinition(
				workflowDefinition.getName(),
				ServiceContextTestUtil.getServiceContext());

		kaleoDefinition.setActive(false);

		_kaleoDefinitionLocalService.updateKaleoDefinition(kaleoDefinition);

		_workflowEngine.deleteWorkflowDefinition(
			workflowDefinition.getName(), 1,
			ServiceContextTestUtil.getServiceContext());
	}

	private KaleoNotification _getKaleoNotification(
		String kaleoClassName, KaleoDefinitionVersion kaleoDefinitionVersion) {

		List<KaleoNotification> kaleoNotifications =
			_kaleoNotificationLocalService.
				getKaleoDefinitionVersionKaleoNotifications(
					kaleoClassName,
					kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		Assert.assertEquals(
			kaleoNotifications.toString(), 1, kaleoNotifications.size());

		return kaleoNotifications.get(0);
	}

	private long _getKaleoTimerId(
		KaleoDefinitionVersion kaleoDefinitionVersion) {

		List<KaleoTimer> kaleoTimers =
			_kaleoTimerLocalService.getKaleoDefinitionVersionKaleoTimers(
				KaleoNode.class.getName(),
				kaleoDefinitionVersion.getKaleoDefinitionVersionId());

		Assert.assertEquals(kaleoTimers.toString(), 1, kaleoTimers.size());

		KaleoTimer kaleoTimer = kaleoTimers.get(0);

		return kaleoTimer.getKaleoTimerId();
	}

	private InputStream _getResourceInputStream(String name) {
		Class<?> clazz = getClass();

		ClassLoader classLoader = clazz.getClassLoader();

		return classLoader.getResourceAsStream(
			"com/liferay/portal/workflow/kaleo/dependencies/" + name);
	}

	private void _testDeployWorkflowDefinitionWithRoleExternalReferenceCode()
		throws Exception {

		String content = StringUtil.replace(
			StringUtil.read(
				_getResourceInputStream(
					"role-external-reference-code-workflow-definition.json")),
			new String[] {"[$ROLE_EXTERNAL_REFERENCE_CODE$]", "[$ROLE_ID$]"},
			new String[] {
				_role1.getExternalReferenceCode(),
				String.valueOf(_role2.getRoleId())
			});

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				TestPropsValues.getCompanyId(), workflowDefinition.getName(),
				workflowDefinition.getVersion() + StringPool.PERIOD + 0);

		KaleoNotification kaleoNotification = _getKaleoNotification(
			KaleoNode.class.getName(), kaleoDefinitionVersion);

		Assert.assertEquals(
			List.of(_role1.getClassPK()),
			TransformUtil.transform(
				_kaleoNotificationRecipientLocalService.
					getKaleoNotificationRecipients(
						kaleoNotification.getKaleoNotificationId()),
				KaleoNotificationRecipient::getRecipientClassPK));

		KaleoTask kaleoTask = _kaleoTaskLocalService.getKaleoNodeKaleoTask(
			kaleoNotification.getKaleoClassPK());

		Assert.assertEquals(
			List.of(_role1.getRoleId()),
			TransformUtil.transform(
				_kaleoTaskAssignmentLocalService.getKaleoTaskAssignments(
					kaleoTask.getKaleoTaskId()),
				KaleoTaskAssignment::getAssigneeClassPK));

		kaleoNotification = _getKaleoNotification(
			KaleoTimer.class.getName(), kaleoDefinitionVersion);

		Assert.assertEquals(
			List.of(_role1.getClassPK()),
			TransformUtil.transform(
				_kaleoNotificationRecipientLocalService.
					getKaleoNotificationRecipients(
						kaleoNotification.getKaleoNotificationId()),
				KaleoNotificationRecipient::getRecipientClassPK));

		Assert.assertEquals(
			List.of(_role1.getRoleId()),
			TransformUtil.transform(
				_kaleoTaskAssignmentLocalService.getKaleoTaskAssignments(
					KaleoTimer.class.getName(),
					_getKaleoTimerId(kaleoDefinitionVersion)),
				KaleoTaskAssignment::getAssigneeClassPK));

		_deleteWorkflowDefinition(workflowDefinition);
	}

	private void _testDeployWorkflowDefinitionWithRoleId() throws Exception {
		String content = StringUtil.replace(
			StringUtil.read(
				_getResourceInputStream("role-id-workflow-definition.json")),
			"[$ROLE_ID$]", String.valueOf(_role1.getRoleId()));

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				TestPropsValues.getCompanyId(), workflowDefinition.getName(),
				workflowDefinition.getVersion() + StringPool.PERIOD + 0);

		String kaleoDefinitionVersionContent =
			kaleoDefinitionVersion.getContent();

		Assert.assertTrue(
			kaleoDefinitionVersionContent.contains(
				_role1.getExternalReferenceCode()));

		_deleteWorkflowDefinition(workflowDefinition);
	}

	private void _testDeployWorkflowDefinitionWithUserExternalReferenceCode()
		throws Exception {

		String content = StringUtil.replace(
			StringUtil.read(
				_getResourceInputStream(
					"user-external-reference-code-workflow-definition.json")),
			new String[] {"[$USER_EXTERNAL_REFERENCE_CODE$]", "[$USER_ID$]"},
			new String[] {
				_user1.getExternalReferenceCode(),
				String.valueOf(_user2.getUserId())
			});

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				TestPropsValues.getCompanyId(), workflowDefinition.getName(),
				workflowDefinition.getVersion() + StringPool.PERIOD + 0);

		KaleoNotification kaleoNotification = _getKaleoNotification(
			KaleoNode.class.getName(), kaleoDefinitionVersion);

		Assert.assertEquals(
			List.of(_user1.getUserId()),
			TransformUtil.transform(
				_kaleoNotificationRecipientLocalService.
					getKaleoNotificationRecipients(
						kaleoNotification.getKaleoNotificationId()),
				KaleoNotificationRecipient::getRecipientClassPK));

		KaleoTask kaleoTask = _kaleoTaskLocalService.getKaleoNodeKaleoTask(
			kaleoNotification.getKaleoClassPK());

		Assert.assertEquals(
			List.of(_user1.getUserId()),
			TransformUtil.transform(
				_kaleoTaskAssignmentLocalService.getKaleoTaskAssignments(
					kaleoTask.getKaleoTaskId()),
				KaleoTaskAssignment::getAssigneeClassPK));

		kaleoNotification = _getKaleoNotification(
			KaleoTimer.class.getName(), kaleoDefinitionVersion);

		Assert.assertEquals(
			List.of(_user1.getUserId()),
			TransformUtil.transform(
				_kaleoNotificationRecipientLocalService.
					getKaleoNotificationRecipients(
						kaleoNotification.getKaleoNotificationId()),
				KaleoNotificationRecipient::getRecipientClassPK));

		Assert.assertEquals(
			List.of(_user1.getUserId()),
			TransformUtil.transform(
				_kaleoTaskAssignmentLocalService.getKaleoTaskAssignments(
					KaleoTimer.class.getName(),
					_getKaleoTimerId(kaleoDefinitionVersion)),
				KaleoTaskAssignment::getAssigneeClassPK));

		_deleteWorkflowDefinition(workflowDefinition);
	}

	private void _testDeployWorkflowDefinitionWithUserId() throws Exception {
		String content = StringUtil.replace(
			StringUtil.read(
				_getResourceInputStream("user-id-workflow-definition.json")),
			"[$USER_ID$]", String.valueOf(_user1.getUserId()));

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				content.getBytes(), TestPropsValues.getCompanyId(), null,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				TestPropsValues.getUserId());

		KaleoDefinitionVersion kaleoDefinitionVersion =
			_kaleoDefinitionVersionLocalService.getKaleoDefinitionVersion(
				TestPropsValues.getCompanyId(), workflowDefinition.getName(),
				workflowDefinition.getVersion() + StringPool.PERIOD + 0);

		String kaleoDefinitionVersionContent =
			kaleoDefinitionVersion.getContent();

		Assert.assertTrue(
			kaleoDefinitionVersionContent.contains(
				_user1.getExternalReferenceCode()));

		_deleteWorkflowDefinition(workflowDefinition);
	}

	@Inject
	private KaleoDefinitionLocalService _kaleoDefinitionLocalService;

	@Inject
	private KaleoDefinitionVersionLocalService
		_kaleoDefinitionVersionLocalService;

	@Inject
	private KaleoNotificationLocalService _kaleoNotificationLocalService;

	@Inject
	private KaleoNotificationRecipientLocalService
		_kaleoNotificationRecipientLocalService;

	@Inject
	private KaleoTaskAssignmentLocalService _kaleoTaskAssignmentLocalService;

	@Inject
	private KaleoTaskLocalService _kaleoTaskLocalService;

	@Inject
	private KaleoTimerLocalService _kaleoTimerLocalService;

	@DeleteAfterTestRun
	private Role _role1;

	@DeleteAfterTestRun
	private Role _role2;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	private WorkflowDefinition _workflowDefinition;
	private WorkflowDefinitionLink _workflowDefinitionLink;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowEngine _workflowEngine;

}