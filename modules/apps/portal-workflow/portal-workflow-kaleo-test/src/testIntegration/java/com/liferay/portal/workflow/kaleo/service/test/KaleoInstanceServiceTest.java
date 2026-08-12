/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowDefinition;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.workflow.configuration.WorkflowDefinitionConfiguration;
import com.liferay.portal.workflow.constants.WorkflowDefinitionConstants;
import com.liferay.portal.workflow.manager.WorkflowDefinitionManager;

import java.io.Serializable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author João Victor Alves
 */
@RunWith(Arquillian.class)
public class KaleoInstanceServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);
		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testAddKaleoInstance() throws Exception {

		// Group ID as 0

		ConfigurationTestUtil.saveConfiguration(
			_configurationAdmin.getConfiguration(
				WorkflowDefinitionConfiguration.class.getName(),
				StringPool.QUESTION),
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", true
			).build());

		User user = _addUser();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), user.getUserId(),
			AccountConstants.PARENT_ACCOUNT_ENTRY_ID_DEFAULT,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null,
			RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED, serviceContext);

		WorkflowDefinition workflowDefinition =
			_workflowDefinitionManager.deployWorkflowDefinition(
				_getContentBytes("valid-workflow-definition.xml"),
				TestPropsValues.getCompanyId(), null,
				accountEntry.getAccountEntryGroupId(),
				RandomTestUtil.randomString(),
				WorkflowDefinitionConstants.SCOPE_AI, false,
				RandomTestUtil.randomString(), user.getUserId());

		ConfigurationTestUtil.saveConfiguration(
			_configurationAdmin.getConfiguration(
				WorkflowDefinitionConfiguration.class.getName(),
				StringPool.QUESTION),
			HashMapDictionaryBuilder.<String, Object>put(
				"company.administrator.can.publish", false
			).build());

		serviceContext.setScopeGroupId(0);

		AssertUtils.assertFailure(
			WorkflowException.class,
			StringBundler.concat(
				PrincipalException.MustHavePermission.class.getName(),
				": User ", user.getUserId(), " must have ",
				ActionKeys.ADD_INSTANCE, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _workflowInstanceManager.startWorkflowInstance(
				TestPropsValues.getCompanyId(), 0, user.getUserId(),
				workflowDefinition.getName(), workflowDefinition.getVersion(),
				null,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext
				).build()));

		// Group ID as account entry group ID

		serviceContext.setScopeGroupId(accountEntry.getAccountEntryGroupId());

		Assert.assertNotNull(
			_workflowInstanceManager.startWorkflowInstance(
				TestPropsValues.getCompanyId(),
				accountEntry.getAccountEntryGroupId(), user.getUserId(),
				workflowDefinition.getName(), workflowDefinition.getVersion(),
				null,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext
				).build()));

		// Group ID as nonaccount entry group ID

		serviceContext.setScopeGroupId(TestPropsValues.getGroupId());

		AssertUtils.assertFailure(
			WorkflowException.class,
			StringBundler.concat(
				PrincipalException.MustHavePermission.class.getName(),
				": User ", user.getUserId(), " must have ",
				ActionKeys.ADD_INSTANCE, " permission for ",
				WorkflowConstants.RESOURCE_NAME, StringPool.SPACE),
			() -> _workflowInstanceManager.startWorkflowInstance(
				TestPropsValues.getCompanyId(), TestPropsValues.getGroupId(),
				user.getUserId(), workflowDefinition.getName(),
				workflowDefinition.getVersion(), null,
				HashMapBuilder.<String, Serializable>put(
					WorkflowConstants.CONTEXT_SERVICE_CONTEXT, serviceContext
				).build()));
	}

	private User _addUser() throws Exception {
		User user = UserTestUtil.addUser();

		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());

		return user;
	}

	private byte[] _getContentBytes(String fileName) throws Exception {
		Class<?> clazz = getClass();

		String content = StringUtil.read(
			clazz.getClassLoader(),
			"com/liferay/portal/workflow/kaleo/dependencies/" + fileName);

		return content.getBytes();
	}

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ConfigurationAdmin _configurationAdmin;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private WorkflowDefinitionManager _workflowDefinitionManager;

	@Inject
	private WorkflowInstanceManager _workflowInstanceManager;

}