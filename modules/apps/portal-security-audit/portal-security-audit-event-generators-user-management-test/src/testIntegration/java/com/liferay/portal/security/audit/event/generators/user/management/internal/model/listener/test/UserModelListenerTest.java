/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.model.WorkflowInstanceLink;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.WorkflowDefinitionLinkLocalService;
import com.liferay.portal.kernel.service.WorkflowInstanceLinkLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.SystemProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTask;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.search.test.util.IdempotentRetryAssert;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Ivica Cardic
 */
@RunWith(Arquillian.class)
public class UserModelListenerTest extends BaseModelListenerTestCase {

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		_deleteWorkflowDefinitionLink();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnBeforeCreate() throws Exception {
		_user = UserTestUtil.addUser();

		AuditMessage auditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.ADD);

		Assert.assertEquals(
			"instance.user.add", auditMessage.getResourceAction());
		Assert.assertEquals("user", auditMessage.getResourceType());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnBeforeRemove() throws Exception {
		User user = UserTestUtil.addUser();

		auditMessages.clear();

		_userLocalService.deleteUser(user);

		AuditMessage auditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.DELETE);

		Assert.assertEquals(
			"instance.user.delete", auditMessage.getResourceAction());
		Assert.assertEquals("user", auditMessage.getResourceType());
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnBeforeUpdate() throws Exception {
		_testOnBeforeUpdate();
		_testOnBeforeUpdateWithAsynchronousApproval();
		_testOnBeforeUpdateWithInlineApproval();
		_testOnBeforeUpdateWithWorkflowDisabled();
	}

	private WorkflowTask _addUserWithWorkflowTask() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(), 0, 0,
			"Single Approver", 1);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRemoteAddr(RandomTestUtil.randomString());
		serviceContext.setRemoteHost(RandomTestUtil.randomString());

		_user = _userLocalService.addUserWithWorkflow(
			0, TestPropsValues.getCompanyId(), false, "test", "test", true,
			StringPool.BLANK, RandomTestUtil.randomString() + "@liferay.com",
			LocaleUtil.US, RandomTestUtil.randomString(), StringPool.BLANK,
			RandomTestUtil.randomString(), 0, 0, true, 1, 1, 1970,
			StringPool.BLANK, UserConstants.TYPE_REGULAR, null, null, null,
			null, false, serviceContext);

		Assert.assertEquals(
			WorkflowConstants.STATUS_PENDING, _user.getStatus());

		WorkflowInstanceLink workflowInstanceLink =
			_workflowInstanceLinkLocalService.fetchWorkflowInstanceLink(
				TestPropsValues.getCompanyId(),
				WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(),
				_user.getUserId());

		List<WorkflowTask> workflowTasks =
			_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
				TestPropsValues.getCompanyId(), null,
				workflowInstanceLink.getWorkflowInstanceId(), null,
				QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		Assert.assertEquals(workflowTasks.toString(), 1, workflowTasks.size());

		WorkflowTask workflowTask = workflowTasks.get(0);

		auditMessages.clear();

		return _workflowTaskManager.assignWorkflowTaskToUser(
			TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			workflowTask.getWorkflowTaskId(), TestPropsValues.getUserId(),
			StringPool.BLANK, null, null);
	}

	private void _assertAuditRequestContext(
		AuditRequestContext auditRequestContext,
		AuditRequestContext expectedAuditRequestContext) {

		Assert.assertEquals(
			expectedAuditRequestContext.getClientHost(),
			auditRequestContext.getClientHost());
		Assert.assertEquals(
			expectedAuditRequestContext.getClientIP(),
			auditRequestContext.getClientIP());
		Assert.assertEquals(
			expectedAuditRequestContext.getRealUserId(),
			auditRequestContext.getRealUserId());
		Assert.assertEquals(
			expectedAuditRequestContext.getServerName(),
			auditRequestContext.getServerName());
		Assert.assertEquals(
			expectedAuditRequestContext.getServerPort(),
			auditRequestContext.getServerPort());
		Assert.assertEquals(
			expectedAuditRequestContext.getSessionID(),
			auditRequestContext.getSessionID());
	}

	private void _assertUpdateAuditMessage(
		AuditRequestContext expectedAuditRequestContext) {

		List<AuditMessage> updateAuditMessages = new ArrayList<>();

		for (AuditMessage auditMessage : auditMessages) {
			if (!Objects.equals(
					auditMessage.getClassName(), User.class.getName()) ||
				!Objects.equals(
					auditMessage.getClassPK(),
					String.valueOf(_user.getUserId())) ||
				!Objects.equals(
					auditMessage.getEventType(), EventTypes.UPDATE)) {

				continue;
			}

			updateAuditMessages.add(auditMessage);
		}

		Assert.assertEquals(
			updateAuditMessages.toString(), 1, updateAuditMessages.size());

		AuditMessage auditMessage = updateAuditMessages.get(0);

		Assert.assertEquals(
			expectedAuditRequestContext.getClientHost(),
			auditMessage.getClientHost());
		Assert.assertEquals(
			expectedAuditRequestContext.getClientIP(),
			auditMessage.getClientIP());
		Assert.assertEquals(
			expectedAuditRequestContext.getServerName(),
			auditMessage.getServerName());
		Assert.assertEquals(
			expectedAuditRequestContext.getServerPort(),
			auditMessage.getServerPort());
		Assert.assertEquals(
			expectedAuditRequestContext.getSessionID(),
			auditMessage.getSessionID());
		Assert.assertEquals(
			expectedAuditRequestContext.getRealUserId(),
			auditMessage.getUserId());

		JSONObject additionalInfoJSONObject = auditMessage.getAdditionalInfo();

		Assert.assertFalse(additionalInfoJSONObject.has("doAsUserId"));
	}

	private AuditRequestContext _createAuditRequestContext() throws Exception {
		AuditRequestContext auditRequestContext = new AuditRequestContext();

		auditRequestContext.setClientHost(RandomTestUtil.randomString());
		auditRequestContext.setClientIP(RandomTestUtil.randomString());
		auditRequestContext.setRealUserId(TestPropsValues.getUserId());
		auditRequestContext.setServerName(RandomTestUtil.randomString());
		auditRequestContext.setServerPort(RandomTestUtil.randomInt());
		auditRequestContext.setSessionID(RandomTestUtil.randomString());

		return auditRequestContext;
	}

	private void _deleteWorkflowDefinitionLink() throws Exception {
		_workflowDefinitionLinkLocalService.updateWorkflowDefinitionLink(
			TestPropsValues.getUserId(), TestPropsValues.getCompanyId(),
			WorkflowConstants.DEFAULT_GROUP_ID, User.class.getName(), 0, 0,
			null);
	}

	private void _testOnBeforeUpdate() throws Exception {
		_company = CompanyTestUtil.addCompany();

		_user = UserTestUtil.addUser();

		Assert.assertFalse(_user.isAgreedToTermsOfUse());

		auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userLocalService.updateAgreedToTermsOfUse(_user.getUserId(), true);
		}

		AuditMessage agreedToTermsOfUseAuditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.AGREED_TO_TERMS_OF_USE);

		JSONObject additionalInfoJSONObject =
			agreedToTermsOfUseAuditMessage.getAdditionalInfo();

		Assert.assertTrue(
			additionalInfoJSONObject.has("termsOfUseJournalArticleGroupId"));
		Assert.assertTrue(
			additionalInfoJSONObject.has("termsOfUseJournalArticleId"));

		Assert.assertEquals(
			String.valueOf(_user.getUserId()),
			agreedToTermsOfUseAuditMessage.getClassPK());
		Assert.assertEquals(
			_user.getCompanyId(),
			agreedToTermsOfUseAuditMessage.getCompanyId());
		Assert.assertEquals(
			"instance.user.agree_to_terms_of_use",
			agreedToTermsOfUseAuditMessage.getResourceAction());
		Assert.assertEquals(
			"user", agreedToTermsOfUseAuditMessage.getResourceType());

		auditMessages.clear();

		_user = _userLocalService.getUser(_user.getUserId());

		_user.setComments(RandomTestUtil.randomString());

		_user = _userLocalService.updateUser(_user);

		for (AuditMessage auditMessage : auditMessages) {
			Assert.assertNotEquals(
				EventTypes.AGREED_TO_TERMS_OF_USE, auditMessage.getEventType());
		}

		AuditMessage updateAuditMessage = fetchAuditMessage(
			User.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			"instance.user.update", updateAuditMessage.getResourceAction());
		Assert.assertEquals("user", updateAuditMessage.getResourceType());

		_userLocalService.deleteUser(_user);

		_user = null;
	}

	private void _testOnBeforeUpdateWithAsynchronousApproval()
		throws Exception {

		WorkflowTask workflowTask = _addUserWithWorkflowTask();

		AuditRequestContext auditRequestContext = _createAuditRequestContext();

		List<Thread> threads = new CopyOnWriteArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(UserModelListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		ServiceRegistration<AuditMessageProcessor> serviceRegistration =
			bundleContext.registerService(
				AuditMessageProcessor.class,
				auditMessage -> threads.add(Thread.currentThread()),
				HashMapDictionaryBuilder.<String, Object>put(
					"eventTypes", EventTypes.UPDATE
				).build());

		String liferayMode = SystemProperties.get("liferay.mode");

		SystemProperties.set("liferay.mode", StringPool.BLANK);

		try (SafeCloseable safeCloseable =
				AuditRequestContextThreadLocal.
					setAuditRequestContextWithSafeCloseable(
						auditRequestContext.clone())) {

			_workflowTaskManager.completeWorkflowTask(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), "approve", StringPool.BLANK,
				null);

			IdempotentRetryAssert.retryAssert(
				1, TimeUnit.MINUTES, 100, TimeUnit.MILLISECONDS,
				() -> {
					User user = _userLocalService.getUser(_user.getUserId());

					Assert.assertEquals(
						WorkflowConstants.STATUS_APPROVED, user.getStatus());

					return null;
				});

			_assertAuditRequestContext(
				AuditRequestContextThreadLocal.getAuditRequestContext(),
				auditRequestContext);
			_assertUpdateAuditMessage(
				AuditRequestContextThreadLocal.getAuditRequestContext());
		}
		finally {
			SystemProperties.set("liferay.mode", liferayMode);

			serviceRegistration.unregister();
		}

		Assert.assertFalse(threads.isEmpty());

		for (Thread thread : threads) {
			Assert.assertNotSame(Thread.currentThread(), thread);
		}

		_userLocalService.deleteUser(_user);

		_user = null;
	}

	private void _testOnBeforeUpdateWithInlineApproval() throws Exception {
		WorkflowTask workflowTask = _addUserWithWorkflowTask();

		AuditRequestContext auditRequestContext = _createAuditRequestContext();

		AuditRequestContext clonedAuditRequestContext =
			auditRequestContext.clone();

		try (SafeCloseable safeCloseable =
				AuditRequestContextThreadLocal.
					setAuditRequestContextWithSafeCloseable(
						clonedAuditRequestContext)) {

			_workflowTaskManager.completeWorkflowTask(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				workflowTask.getWorkflowTaskId(), "approve", StringPool.BLANK,
				null, true);

			Assert.assertSame(
				clonedAuditRequestContext,
				AuditRequestContextThreadLocal.getAuditRequestContext());

			_assertAuditRequestContext(
				AuditRequestContextThreadLocal.getAuditRequestContext(),
				auditRequestContext);
			_assertUpdateAuditMessage(
				AuditRequestContextThreadLocal.getAuditRequestContext());
		}

		_userLocalService.deleteUser(_user);

		_user = null;
	}

	private void _testOnBeforeUpdateWithWorkflowDisabled() throws Exception {
		_deleteWorkflowDefinitionLink();

		AuditRequestContext auditRequestContext = _createAuditRequestContext();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.getSession(true);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setRequest(mockHttpServletRequest);

		auditMessages.clear();

		try (SafeCloseable safeCloseable =
				AuditRequestContextThreadLocal.
					setAuditRequestContextWithSafeCloseable(
						auditRequestContext.clone())) {

			_user = UserTestUtil.addUser(
				TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
				RandomTestUtil.randomString(), LocaleUtil.US,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				new long[] {TestPropsValues.getGroupId()}, serviceContext);

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, _user.getStatus());

			_assertAuditRequestContext(
				AuditRequestContextThreadLocal.getAuditRequestContext(),
				auditRequestContext);
			_assertUpdateAuditMessage(
				AuditRequestContextThreadLocal.getAuditRequestContext());
		}
	}

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private WorkflowDefinitionLinkLocalService
		_workflowDefinitionLinkLocalService;

	@Inject
	private WorkflowInstanceLinkLocalService _workflowInstanceLinkLocalService;

	@Inject
	private WorkflowTaskManager _workflowTaskManager;

}