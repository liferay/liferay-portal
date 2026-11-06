/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestContext;
import com.liferay.portal.kernel.audit.AuditRequestContextThreadLocal;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.audit.router.configuration.PersistentAuditMessageProcessorConfiguration;
import com.liferay.portal.security.audit.storage.model.AuditEvent;
import com.liferay.portal.security.audit.storage.service.AuditEventLocalService;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Christian Moura
 */
@RunWith(Arquillian.class)
public class PersistentAuditMessageProcessorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@FeatureFlag("LPD-6417")
	@Test
	public void testProcess() throws Exception {
		_testProcessCorrelationIdPersistsAfterClear();
		_testProcessRolesPersistAfterRoleChanges();
	}

	private Role _addRole() throws Exception {
		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_roles.add(role);

		return role;
	}

	private String _createEventType() {
		return StringUtil.toUpperCase(RandomTestUtil.randomString());
	}

	private AuditEvent _getAuditEvent(long companyId, String eventType) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			List<AuditEvent> auditEvents =
				_auditEventLocalService.getAuditEvents(
					companyId, 0, 0, null, null, null, null, null, null, null,
					null, null, eventType, null, 0, null, false, 0, 1);

			return auditEvents.get(0);
		}
	}

	private int _getAuditEventsCount(long companyId, String eventType) {
		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			return _auditEventLocalService.getAuditEventsCount(
				companyId, 0, 0, null, null, null, null, null, null, null, null,
				null, eventType, null, 0, null, false);
		}
	}

	private void _route(long companyId, long userId, String eventType)
		throws Exception {

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(companyId)) {

			_auditRouter.route(
				new AuditMessage(
					companyId, userId, RandomTestUtil.randomString(),
					eventType));
		}
	}

	private void _route(long companyId, String eventType) throws Exception {
		_route(companyId, RandomTestUtil.randomLong(), eventType);
	}

	private void _testProcessCorrelationIdPersistsAfterClear()
		throws Exception {

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 1
					).build())) {

			_route(TestPropsValues.getCompanyId(), _createEventType());
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 2
					).put(
						"flushInterval", _FLUSH_INTERVAL
					).build())) {

			String correlationId = RandomTestUtil.randomString();

			AuditRequestContext auditRequestContext =
				AuditRequestContextThreadLocal.getAuditRequestContext();

			auditRequestContext.setCorrelationId(correlationId);

			String eventType1 = _createEventType();

			_route(TestPropsValues.getCompanyId(), eventType1);

			auditRequestContext.setCorrelationId(null);

			Assert.assertEquals(
				0,
				_getAuditEventsCount(
					TestPropsValues.getCompanyId(), eventType1));

			String eventType2 = _createEventType();

			_route(TestPropsValues.getCompanyId(), eventType2);

			AuditEvent auditEvent1 = _getAuditEvent(
				TestPropsValues.getCompanyId(), eventType1);
			AuditEvent auditEvent2 = _getAuditEvent(
				TestPropsValues.getCompanyId(), eventType2);

			Assert.assertEquals(correlationId, auditEvent1.getCorrelationId());
			Assert.assertEquals(
				StringPool.BLANK, auditEvent2.getCorrelationId());

			AuditRequestContextThreadLocal.removeAuditRequestContext();
		}
	}

	private void _testProcessRolesPersistAfterRoleChanges() throws Exception {
		_user = UserTestUtil.addUser();

		Role role1 = _addRole();
		Role role2 = _addRole();

		_userLocalService.addRoleUser(role1.getRoleId(), _user.getUserId());
		_userLocalService.addRoleUser(role2.getRoleId(), _user.getUserId());

		String eventType = _createEventType();
		String role1Name = role1.getName();
		Role role3 = _addRole();

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 2000
					).put(
						"flushInterval", _FLUSH_INTERVAL
					).build())) {

			_route(
				TestPropsValues.getCompanyId(), _user.getUserId(), eventType);

			_userLocalService.deleteRoleUser(
				role1.getRoleId(), _user.getUserId());

			role1.setName(RandomTestUtil.randomString());

			_roleLocalService.updateRole(role1);

			_roleLocalService.deleteRole(role2);

			_userLocalService.addRoleUser(role3.getRoleId(), _user.getUserId());

			Assert.assertEquals(
				0,
				_getAuditEventsCount(
					TestPropsValues.getCompanyId(), eventType));
		}

		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					PersistentAuditMessageProcessorConfiguration.class.
						getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"bufferSize", 1
					).put(
						"flushInterval", _FLUSH_INTERVAL
					).build())) {

			_route(TestPropsValues.getCompanyId(), _createEventType());

			AuditEvent auditEvent = _getAuditEvent(
				TestPropsValues.getCompanyId(), eventType);

			List<String> roleNames = JSONUtil.toStringList(
				JSONFactoryUtil.createJSONArray(auditEvent.getRoles()));

			Assert.assertTrue(roleNames.contains(role2.getName()));
			Assert.assertFalse(roleNames.contains(role3.getName()));
			Assert.assertTrue(roleNames.contains(role1Name));
		}
	}

	private static final long _FLUSH_INTERVAL = 86400000;

	@Inject
	private AuditEventLocalService _auditEventLocalService;

	@Inject
	private AuditRouter _auditRouter;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private final List<Role> _roles = new ArrayList<>();

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}