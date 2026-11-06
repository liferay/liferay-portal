/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.event.generators.user.management.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.configuration.test.util.ConfigurationTemporarySwapper;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;
import com.liferay.portal.security.audit.router.configuration.LoggingAuditMessageProcessorConfiguration;
import com.liferay.portal.security.audit.test.util.AuditMessageTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Lucas Miranda
 */
@RunWith(Arquillian.class)
public class ResourcePermissionModelListenerTest
	extends BaseModelListenerTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_primKey = String.valueOf(TestPropsValues.getCompanyId());
		_role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		auditMessages.clear();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testOnBeforeCreate() throws Exception {
		try (ConfigurationTemporarySwapper configurationTemporarySwapper =
				new ConfigurationTemporarySwapper(
					LoggingAuditMessageProcessorConfiguration.class.getName(),
					HashMapDictionaryBuilder.<String, Object>put(
						"enabled", true
					).put(
						"logMessageFormat", "JSON"
					).build());
			LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				"com.liferay.portal.security.audit.router.internal." +
					"LoggingAuditMessageProcessor",
				LoggerTestUtil.INFO)) {

			_setResourcePermissions(ActionKeys.VIEW);

			AuditMessage auditMessage = fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.ADD);

			_assertAuditMessage(
				new String[] {ActionKeys.VIEW}, auditMessage, new String[0],
				_role);

			Assert.assertEquals(
				"instance.resourcepermission.add",
				auditMessage.getResourceAction());

			AuditMessageTestUtil.assertLogEntries(auditMessage, logCapture);
		}
	}

	@Test
	public void testOnBeforeRemove() throws Exception {
		_setResourcePermissions(ActionKeys.VIEW);

		_resourcePermissionLocalService.deleteResourcePermission(
			_resourcePermissionLocalService.getResourcePermission(
				TestPropsValues.getCompanyId(), _NAME,
				ResourceConstants.SCOPE_COMPANY, _primKey, _role.getRoleId()));

		_assertAuditMessage(
			new String[0],
			fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.DELETE),
			new String[] {ActionKeys.VIEW}, _role);
	}

	@Test
	public void testOnBeforeUpdate() throws Exception {
		_company = CompanyTestUtil.addCompany();

		auditMessages.clear();

		_setResourcePermissions(ActionKeys.VIEW);

		ResourcePermission resourcePermission =
			_resourcePermissionLocalService.getResourcePermission(
				TestPropsValues.getCompanyId(), _NAME,
				ResourceConstants.SCOPE_COMPANY, _primKey, _role.getRoleId());

		resourcePermission.setOwnerId(TestPropsValues.getUserId());

		_resourcePermissionLocalService.updateResourcePermission(
			resourcePermission);

		Assert.assertNull(
			fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.UPDATE));

		try (SafeCloseable safeCloseable =
				CompanyThreadLocal.setCompanyIdWithSafeCloseable(
					_company.getCompanyId())) {

			_setResourcePermissions(ActionKeys.UPDATE);
		}

		_assertAuditMessage(
			new String[] {ActionKeys.UPDATE},
			fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.UPDATE),
			new String[] {ActionKeys.VIEW}, _role);

		_toRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		auditMessages.clear();

		_resourcePermissionLocalService.mergePermissions(
			_role.getRoleId(), _toRole.getRoleId());

		Assert.assertNull(
			fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.UPDATE));

		_assertAuditMessage(
			new String[0],
			fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.DELETE),
			new String[] {ActionKeys.UPDATE}, _role);
		_assertAuditMessage(
			new String[] {ActionKeys.UPDATE},
			fetchAuditMessage(
				ResourcePermission.class.getName(), EventTypes.ADD),
			new String[0], _toRole);
	}

	private void _assertAuditMessage(
			String[] addedActionIds, AuditMessage auditMessage,
			String[] removedActionIds, Role role)
		throws Exception {

		Assert.assertEquals(
			TestPropsValues.getCompanyId(), auditMessage.getCompanyId());

		JSONAssert.assertEquals(
			JSONUtil.put(
				"addedActionIds", JSONUtil.putAll((Object[])addedActionIds)
			).put(
				"name", _NAME
			).put(
				"primKey", _primKey
			).put(
				"removedActionIds", JSONUtil.putAll((Object[])removedActionIds)
			).put(
				"roleId", role.getRoleId()
			).put(
				"roleName", role.getName()
			).put(
				"scope", ResourceConstants.SCOPE_COMPANY
			).toString(),
			String.valueOf(auditMessage.getAdditionalInfo()),
			JSONCompareMode.NON_EXTENSIBLE);
	}

	private void _setResourcePermissions(String actionId) throws Exception {
		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), _NAME,
			ResourceConstants.SCOPE_COMPANY, _primKey, _role.getRoleId(),
			new String[] {actionId});
	}

	private static final String _NAME = Role.class.getName();

	@DeleteAfterTestRun
	private Company _company;

	private String _primKey;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@DeleteAfterTestRun
	private Role _role;

	@DeleteAfterTestRun
	private Role _toRole;

}