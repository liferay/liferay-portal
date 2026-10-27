/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.audit.router.internal.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.audit.AuditException;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.security.audit.AuditMessageProcessor;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
public class DefaultAuditRouterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		Bundle bundle = FrameworkUtil.getBundle(getClass());

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			AuditMessageProcessor.class, _auditMessages::add,
			HashMapDictionaryBuilder.<String, Object>put(
				"eventTypes", "*"
			).build());
	}

	@After
	public void tearDown() throws Exception {
		_serviceRegistration.unregister();
	}

	@FeatureFlag("LPD-6417")
	@Test
	public void testRoute() throws Exception {
		_testRoute(
			User.class.getName(), "AI_HUB", "ADD", "ai_hub.user.add", "user");
		_testRoute(
			User.class.getName(), null, "LOGIN", "instance.user.login", "user");
		_testRoute(
			User.class.getName(), null, StringPool.BLANK,
			"instance.user.unknown", "user");
		_testRoute(null, null, "ADD", "instance.unknown.add", "unknown");

		AuditMessage auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, "ASSIGN",
			RandomTestUtil.randomLong());

		auditMessage.setResourceType("organization");

		_auditRouter.route(auditMessage);

		Assert.assertEquals(
			"instance.organization.assign", auditMessage.getResourceAction());
		Assert.assertEquals("organization", auditMessage.getResourceType());

		auditMessage = _getAuditMessage(
			User.class.getName(), "AI_HUB", RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		auditMessage.setAction("submit");

		_auditRouter.route(auditMessage);

		Assert.assertEquals(
			"ai_hub.user.submit", auditMessage.getResourceAction());
		Assert.assertEquals("user", auditMessage.getResourceType());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), "AI_HUB",
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());

		auditMessage.setAction("submit");
		auditMessage.setResourceType("prompt");

		_auditRouter.route(auditMessage);

		Assert.assertEquals(
			"ai_hub.prompt.submit", auditMessage.getResourceAction());
		Assert.assertEquals("prompt", auditMessage.getResourceType());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());

		auditMessage.setResourceAction("ai_hub.prompt.submit");

		_auditRouter.route(auditMessage);

		Assert.assertEquals(
			"ai_hub.prompt.submit", auditMessage.getResourceAction());
		Assert.assertNull(auditMessage.getResourceType());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomLong());

		String resourceType = RandomTestUtil.randomString();

		auditMessage.setResourceAction("ai_hub.prompt.submit");
		auditMessage.setResourceType(resourceType);

		_auditRouter.route(auditMessage);

		Assert.assertEquals(
			"ai_hub.prompt.submit", auditMessage.getResourceAction());
		Assert.assertEquals(resourceType, auditMessage.getResourceType());

		_regularRole = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);
		_userGroup = UserGroupTestUtil.addUserGroup();

		_roleLocalService.addGroupRole(
			_userGroup.getGroupId(), _regularRole.getRoleId());

		_group = GroupTestUtil.addGroup();
		_siteRole = RoleTestUtil.addRole(RoleConstants.TYPE_SITE);
		_user = UserTestUtil.addUser();

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _group.getGroupId(),
			new long[] {_siteRole.getRoleId()});

		_organization = OrganizationTestUtil.addOrganization();
		_organizationRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_ORGANIZATION);

		_userGroupRoleLocalService.addUserGroupRoles(
			_user.getUserId(), _organization.getGroupId(),
			new long[] {_organizationRole.getRoleId()});

		_userLocalService.addRoleUser(
			_regularRole.getRoleId(), _user.getUserId());
		_userLocalService.addUserGroupUser(
			_userGroup.getUserGroupId(), _user.getUserId());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_user.getUserId());

		_auditRouter.route(auditMessage);

		List<String> roleNames = JSONUtil.toStringList(
			JSONFactoryUtil.createJSONArray(auditMessage.getRoles()));

		Assert.assertEquals(
			new ArrayList<>(new TreeSet<>(roleNames)), roleNames);
		Assert.assertTrue(roleNames.contains(_organizationRole.getName()));
		Assert.assertTrue(roleNames.contains(_regularRole.getName()));
		Assert.assertTrue(roleNames.contains(_siteRole.getName()));

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_user.getUserId());

		String roles = RandomTestUtil.randomString();

		auditMessage.setRoles(roles);

		_auditRouter.route(auditMessage);

		Assert.assertEquals(roles, auditMessage.getRoles());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			_userLocalService.getGuestUserId(TestPropsValues.getCompanyId()));

		_auditRouter.route(auditMessage);

		roleNames = JSONUtil.toStringList(
			JSONFactoryUtil.createJSONArray(auditMessage.getRoles()));

		Assert.assertTrue(roleNames.contains(RoleConstants.GUEST));

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			0);

		_auditRouter.route(auditMessage);

		Assert.assertNull(auditMessage.getRoles());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		_auditRouter.route(auditMessage);

		Assert.assertNull(auditMessage.getRoles());

		auditMessage = new AuditMessage(
			CompanyConstants.SYSTEM, _user.getUserId(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString());

		auditMessage.setAction(RandomTestUtil.randomString());
		auditMessage.setResourceAction(RandomTestUtil.randomString());
		auditMessage.setResourceType(RandomTestUtil.randomString());
		auditMessage.setRoles(RandomTestUtil.randomString());

		_auditRouter.route(auditMessage);

		Assert.assertTrue(_auditMessages.contains(auditMessage));
		Assert.assertNull(auditMessage.getAction());
		Assert.assertNull(auditMessage.getResourceAction());
		Assert.assertNull(auditMessage.getResourceType());
		Assert.assertNull(auditMessage.getRoles());

		auditMessage = _getAuditMessage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomLong());

		String resourceAction = RandomTestUtil.randomString();

		auditMessage.setResourceAction(resourceAction);

		try {
			_auditRouter.route(auditMessage);

			Assert.fail();
		}
		catch (AuditException auditException) {
			Assert.assertEquals(
				StringBundler.concat(
					"Resource action ", resourceAction,
					" does not match format ",
					"\"<contextName>.<resourceType>.<action>\""),
				auditException.getMessage());
		}

		Assert.assertFalse(_auditMessages.contains(auditMessage));

		_testRouteWhenFeatureFlagIsDisabled();
	}

	private AuditMessage _getAuditMessage(
			String className, String contextName, String eventType, long userId)
		throws Exception {

		return new AuditMessage(
			RandomTestUtil.randomLong(), TestPropsValues.getCompanyId(), userId,
			RandomTestUtil.randomString(), null, RandomTestUtil.randomLong(),
			null, className, RandomTestUtil.randomString(), contextName,
			eventType, null);
	}

	private void _testRoute(
			String className, String contextName, String eventType,
			String expectedResourceAction, String expectedResourceType)
		throws Exception {

		AuditMessage auditMessage = _getAuditMessage(
			className, contextName, eventType, RandomTestUtil.randomLong());

		_auditRouter.route(auditMessage);

		Assert.assertEquals(
			expectedResourceAction, auditMessage.getResourceAction());
		Assert.assertEquals(
			expectedResourceType, auditMessage.getResourceType());
	}

	private void _testRouteWhenFeatureFlagIsDisabled() throws Exception {
		try (SafeCloseable safeCloseable =
				FeatureFlagTestUtil.setFeatureFlagsWithSafeCloseable(
					false, "LPD-6417")) {

			AuditMessage auditMessage = _getAuditMessage(
				RandomTestUtil.randomString(), null,
				RandomTestUtil.randomString(), _user.getUserId());

			auditMessage.setAction(RandomTestUtil.randomString());
			auditMessage.setResourceAction(RandomTestUtil.randomString());
			auditMessage.setResourceType(RandomTestUtil.randomString());
			auditMessage.setRoles(RandomTestUtil.randomString());

			_auditRouter.route(auditMessage);

			Assert.assertTrue(_auditMessages.contains(auditMessage));
			Assert.assertNull(auditMessage.getAction());
			Assert.assertNull(auditMessage.getResourceAction());
			Assert.assertNull(auditMessage.getResourceType());
			Assert.assertNull(auditMessage.getRoles());
		}
	}

	private final List<AuditMessage> _auditMessages = new ArrayList<>();

	@Inject
	private AuditRouter _auditRouter;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private Organization _organization;

	@DeleteAfterTestRun
	private Role _organizationRole;

	@DeleteAfterTestRun
	private Role _regularRole;

	@Inject
	private RoleLocalService _roleLocalService;

	private ServiceRegistration<AuditMessageProcessor> _serviceRegistration;

	@DeleteAfterTestRun
	private Role _siteRole;

	@DeleteAfterTestRun
	private User _user;

	@DeleteAfterTestRun
	private UserGroup _userGroup;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Inject
	private UserLocalService _userLocalService;

}