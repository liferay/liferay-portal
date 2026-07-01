/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
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

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class RoleModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_auditMessages = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(RoleModelListenerTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		Dictionary<String, Object> properties = new Hashtable<>();

		properties.put("eventTypes", "*");

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class,
			auditMessage -> _auditMessages.add(auditMessage), properties);
	}

	@After
	public void tearDown() throws Exception {
		if (_serviceRegistration != null) {
			_serviceRegistration.unregister();
		}
	}

	@Test
	public void testOnBeforeAddAssociation() throws Exception {
		_user = UserTestUtil.addUser();

		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_company = CompanyTestUtil.addCompany();

		_auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userLocalService.addRoleUsers(
				_role.getRoleId(), new long[] {_user.getUserId()});
		}

		AuditMessage assignAuditMessage = null;

		for (AuditMessage auditMessage : _auditMessages) {
			if (EventTypes.ASSIGN.equals(auditMessage.getEventType()) &&
				Objects.equals(
					User.class.getName(), auditMessage.getClassName())) {

				assignAuditMessage = auditMessage;

				break;
			}
		}

		Assert.assertEquals(
			_user.getCompanyId(), assignAuditMessage.getCompanyId());
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_company = CompanyTestUtil.addCompany();

		_auditMessages.clear();

		_role.setName(RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_role = _roleLocalService.updateRole(_role);
		}

		AuditMessage updateAuditMessage = null;

		for (AuditMessage auditMessage : _auditMessages) {
			if (EventTypes.UPDATE.equals(auditMessage.getEventType()) &&
				Objects.equals(
					Role.class.getName(), auditMessage.getClassName())) {

				updateAuditMessage = auditMessage;

				break;
			}
		}

		Assert.assertEquals(
			_role.getCompanyId(), updateAuditMessage.getCompanyId());
	}

	private List<AuditMessage> _auditMessages;

	@DeleteAfterTestRun
	private Company _company;

	@DeleteAfterTestRun
	private Role _role;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}