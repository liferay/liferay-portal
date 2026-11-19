/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.internal.events.test;

import com.liferay.account.constants.AccountConstants;
import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.account.service.AccountEntryUserRelLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Christopher Kian
 */
@RunWith(Arquillian.class)
public class LogoutPostActionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage),
			HashMapDictionaryBuilder.put(
				"eventTypes", "*"
			).build());

		_user = UserTestUtil.addUser();
	}

	@After
	public void tearDown() {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testRun() throws Exception {
		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					true, "LPD-113659")) {

			_run();

			AuditMessage auditMessage = _fetchAuditMessage(EventTypes.LOGOUT);

			Assert.assertArrayEquals(
				new long[0], auditMessage.getUserAccountEntryIds());

			AccountEntry accountEntry1 = _addAccountEntry();
			AccountEntry accountEntry2 = _addAccountEntry();

			_run();

			auditMessage = _fetchAuditMessage(EventTypes.LOGOUT);

			long[] userAccountEntryIds = auditMessage.getUserAccountEntryIds();

			Arrays.sort(userAccountEntryIds);

			Assert.assertArrayEquals(
				new long[] {
					accountEntry1.getAccountEntryId(),
					accountEntry2.getAccountEntryId()
				},
				userAccountEntryIds);
		}

		_run();

		AuditMessage auditMessage = _fetchAuditMessage(EventTypes.LOGOUT);

		Assert.assertArrayEquals(
			new long[0], auditMessage.getUserAccountEntryIds());
	}

	private AccountEntry _addAccountEntry() throws Exception {
		AccountEntry accountEntry = _accountEntryLocalService.addAccountEntry(
			RandomTestUtil.randomString(), TestPropsValues.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			null, null, RandomTestUtil.randomString(),
			AccountConstants.ACCOUNT_ENTRY_TYPE_BUSINESS,
			WorkflowConstants.STATUS_APPROVED,
			ServiceContextTestUtil.getServiceContext());

		_accountEntries.add(accountEntry);

		_accountEntryUserRelLocalService.addAccountEntryUserRel(
			accountEntry.getAccountEntryId(), _user.getUserId());

		return accountEntry;
	}

	private AuditMessage _fetchAuditMessage(String eventType) {
		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(auditMessage.getEventType(), eventType)) {
				return auditMessage;
			}
		}

		return null;
	}

	private void _run() throws Exception {
		_auditMessages.clear();

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.COMPANY_ID, TestPropsValues.getCompanyId());
		mockHttpServletRequest.setAttribute(WebKeys.USER, _user);

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					TestPropsValues.getCompanyId())) {

			_logoutPostAction.run(
				mockHttpServletRequest, new MockHttpServletResponse());
		}
	}

	@DeleteAfterTestRun
	private final List<AccountEntry> _accountEntries = new ArrayList<>();

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private AccountEntryUserRelLocalService _accountEntryUserRelLocalService;

	private final List<AuditMessage> _auditMessages = new ArrayList<>();

	@Inject(
		filter = "component.name=com.liferay.portal.security.audit.event.generators.internal.events.LogoutPostAction",
		type = LifecycleAction.class
	)
	private Action _logoutPostAction;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

}