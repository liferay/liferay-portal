/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
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
public class UserGroupModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_auditMessages = new ArrayList<>();

		Bundle bundle = FrameworkUtil.getBundle(
			UserGroupModelListenerTest.class);

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

		_userGroup = UserGroupTestUtil.addUserGroup();

		_company = CompanyTestUtil.addCompany();

		_auditMessages.clear();

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userLocalService.addUserGroupUsers(
				_userGroup.getUserGroupId(), new long[] {_user.getUserId()});
		}

		AuditMessage auditMessage = _fetchAuditMessage(
			User.class.getName(), EventTypes.ASSIGN);

		Assert.assertEquals(_user.getCompanyId(), auditMessage.getCompanyId());
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_userGroup = UserGroupTestUtil.addUserGroup();

		_company = CompanyTestUtil.addCompany();

		_auditMessages.clear();

		_userGroup.setName(RandomTestUtil.randomString());

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_userGroup = _userGroupLocalService.updateUserGroup(_userGroup);
		}

		AuditMessage auditMessage = _fetchAuditMessage(
			UserGroup.class.getName(), EventTypes.UPDATE);

		Assert.assertEquals(
			_userGroup.getCompanyId(), auditMessage.getCompanyId());
	}

	private AuditMessage _fetchAuditMessage(
		String className, String eventType) {

		for (AuditMessage auditMessage : _auditMessages) {
			if (Objects.equals(auditMessage.getClassName(), className) &&
				Objects.equals(auditMessage.getEventType(), eventType)) {

				return auditMessage;
			}
		}

		return null;
	}

	private List<AuditMessage> _auditMessages;

	@DeleteAfterTestRun
	private Company _company;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserLocalService _userLocalService;

}